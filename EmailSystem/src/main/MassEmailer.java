package main;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import initialSetup.User_Details;
import initialSetup.SetupWindow;
import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.enumeration.misc.ExchangeVersion;
import microsoft.exchange.webservices.data.core.enumeration.property.WellKnownFolderName;
import microsoft.exchange.webservices.data.core.enumeration.search.ResolveNameSearchLocation;
import microsoft.exchange.webservices.data.core.exception.service.remote.ServiceRequestException;
import microsoft.exchange.webservices.data.core.exception.service.remote.ServiceResponseException;
import microsoft.exchange.webservices.data.core.service.item.EmailMessage;
import microsoft.exchange.webservices.data.misc.NameResolutionCollection;
import microsoft.exchange.webservices.data.property.complex.EmailAddress;
import microsoft.exchange.webservices.data.property.complex.FolderId;
import microsoft.exchange.webservices.data.property.complex.Mailbox;
import microsoft.exchange.webservices.data.property.complex.MessageBody;
import settings.SettingsHandler;

public class MassEmailer {
	private String inbox = "", emailBody = "", subject = "", importSignature = "", loginEmail = "", results = "";
	private String[] mergeList = new String[] {""};
	private String[] inboxes = new String[] {""};
	private boolean sentFinished = false, importSuccess = false, /*autoAdd = false, *//*filterError = false,*/ importFinished = false, loginSuccess;
	private ExchangeService service = new ExchangeService(ExchangeVersion.Exchange2010_SP2);
	private ApplicantImporter importedRecipients = new ApplicantImporter();
	private ScheduledExecutorService refreshService;
	private String[] attachList = new String[1];
	private int attachNum = 0;
	private SheetImporter sheetImporter;
	private String dLine = System.getProperty("line.separator") + System.getProperty("line.separator");
	private User_Details userDetails = new User_Details();
	private SettingsHandler mainSettings;
	private Shell mainShell;
	private SimpleDateFormat format = new SimpleDateFormat("dd-MMMM-yyyy");
	
	MassEmailer(Shell mainShellPara)	{
		mainShell = mainShellPara;
	}
	public void initialise(Text subjectText, Text bodyText)	{
		try {
			File url = new File(".//URL.txt");
			if (url.exists())	{
				Scanner urlScanner = new Scanner(url);
				service.setUrl(new URI(urlScanner.nextLine()));
				urlScanner.close();
				}
			} catch (Exception e) {
				writeErrors("Initial URI failed: " + e.toString());
				}
		addMenus(subjectText, bodyText);
		Boolean setupComplete = false;
		if (new File(".//Setup.txt").exists())	{
			setupComplete = true;
			importSignature = userDetails.getSignature();
			inboxes = userDetails.getInboxes();
		}	else 	{
			setupComplete = setup();
		}
		if (setupComplete)	{
			LoginDialog login = new LoginDialog(mainShell, SWT.CLOSE | SWT.SYSTEM_MODAL, service);
			loginEmail = login.open();
			if (loginEmail.equals("") || setupComplete == false) {
				loginSuccess = false;
			}	else	{
				loginSuccess = true;
			}
		}
	}
	public void setSettings()	{
		mainSettings = new SettingsHandler();
	}
	public String[] getInboxes()	{
		return inboxes;
	}
	public boolean setup()	{
		boolean toReturn = false;
		SetupWindow setupWindow = new SetupWindow(mainShell, SWT.CLOSE | SWT.SYSTEM_MODAL);
		toReturn = setupWindow.open();
		importSignature = userDetails.getSignature();
		inboxes = userDetails.getInboxes();
		return toReturn;
	}
	public void addMenus(Text subjectText, Text bodyText)	{
		new TextMenu(bodyText).addMainMenu(mergeList);
		new TextMenu(subjectText).addSubjectMenu(mergeList);
	}
	public MergeContact getPrevious()	{
		if (importSuccess) {
			return importedRecipients.getPrevious();
		}	else	{
			return null;
		}
	}
	public MergeContact getNext()	{
		if (importSuccess) {
			return importedRecipients.getNext();
		}	else	{
			return null;
		}
	}
	public MergeContact getCurrent()	{
		if (importSuccess) {
			return importedRecipients.getSpecific(importedRecipients.getCurrent());
		}	else	{
			return null;
		}
	}
	public void removeAttachment(String toRemove, int index)	{
		attachList[index] = null;
		String[] tempAttach = new String[(attachNum) - 1];
		int tempCount = 0;
		for (int i = 0; i < attachNum; ++i)	{
			if (attachList[i] != null)	{
				tempAttach[tempCount] = attachList[i];
				++tempCount;
			}
		}
		attachList = tempAttach;
		--attachNum;
	}
	public void setMergeList(String[] toSet)	{
		mergeList = toSet;
	}
	public void startImport(String fileLocation, int sheetNumber)	{
		Thread importThread = new Thread(new Runnable() {
			public void run() {
				File xlFile = new File(fileLocation);
				try {
					importSuccess = false;
					importFinished = false;
					sheetImporter = new SheetImporter(mainSettings.getSetting("Ignore Filters?"));
					String results = importedRecipients.importApplicants(sheetImporter.importWorkbook(xlFile, sheetNumber));
					if (results.startsWith("Fatal Error"))	{
						importFailed(results);
					}	else	{
						setMergeList(importedRecipients.getMergeList());
						importSuccess(results);
					}
					} catch (OutOfMemoryError nme)	{
						importFailed("Java ran out of memory :( Spreadsheet too large : " + nme.toString());
					}	catch (NullPointerException n)	{
						importFailed("Error: Selected sheet appears to be blank.");
					}	catch(FilteredSheetException y)	{
						importFailed("Filtered sheet error: " + y.toString() + System.getProperty("line.separator") + "Tick 'Ignore Filters?' to suppress this error.");
					}	catch (Exception e)	{
						e.printStackTrace();
						importFailed("Unknown import error: " + e.toString());
					}
				}
			});
		importThread.setDaemon(true);
		importThread.start();
	}
	public void importFailed(String errorString)	{
		results = errorString;
		writeErrors(results);
		importSuccess = false;
		importFinished = true;
	}
	public void importSuccess(String resultsPara)	{
		results = resultsPara;
		importSuccess = true;
		importFinished = true;
	}
	public void refreshDisplay(Runnable toRefresh)	{
	 Runnable runRefresh = new Runnable() {
	  public void run() {
	  	Display.getDefault().asyncExec(toRefresh);
	  }
	 };
	 refreshService = Executors.newSingleThreadScheduledExecutor();
	 refreshService.scheduleAtFixedRate(runRefresh, 0, 16, TimeUnit.MILLISECONDS);
	}
	public void setEmailParas(String inboxPara, String emailBodyPara, String subjectString)	{
		inbox = inboxPara + loginEmail.substring(loginEmail.indexOf('@')).trim();
		emailBody = emailBodyPara.trim();
		subject = subjectString.trim();
	}
	private EmailMessage createEmail(int current) throws Exception	{
		String thisBody = emailBody, thisSubject = subject;
		Cell tempCell;
		if (importSuccess)	{
			for (int i = 0; i < importedRecipients.getMergeSheet().getTotalColumns(); ++i) {
				if (thisSubject.contains("<<" + mergeList[i] + ">>"))	{
					tempCell = importedRecipients.getMainSheet().getRow(current).getCell(i);
					if (mergeList[i].toLowerCase().contains("date"))	{
						try	{
							thisSubject = thisSubject.replaceAll(Pattern.quote("<<" + mergeList[i] + ">>"), format.format(tempCell.getDateCellValue()).toString());
							}	catch (IllegalStateException e) {
								thisSubject = thisSubject.replaceAll(Pattern.quote("<<" + mergeList[i] + ">>"), CellValue.getCellValue(tempCell));
								}
						}	else	{
							thisSubject = thisSubject.replaceAll(Pattern.quote("<<" + mergeList[i] + ">>"), CellValue.getCellValue(tempCell));
							}	
					}
				if (thisBody.contains("<<" + mergeList[i] + ">>"))	{
					tempCell = importedRecipients.getMainSheet().getRow(current).getCell(i);
					if (mergeList[i].toLowerCase().contains("date"))	{
						try	{
							thisBody = thisBody.replaceAll(Pattern.quote("<<" + mergeList[i] + ">>"), format.format(tempCell.getDateCellValue()).toString());
							}	catch (IllegalStateException e) {
								thisBody = thisBody.replaceAll(Pattern.quote("<<" + mergeList[i] + ">>"), CellValue.getCellValue(tempCell));
								}
						}	else	{
							thisBody = thisBody.replaceAll(Pattern.quote("<<" + mergeList[i] + ">>"), CellValue.getCellValue(tempCell));
							}	
					}
			}
		}
		EmailMessage msg = new EmailMessage(service);
		MergeContact currentContact = importedRecipients.getSpecific(current);
		if (!(StringUtils.endsWithIgnoreCase(thisBody, "regards") 
				|| StringUtils.endsWithIgnoreCase(thisBody, "regards,")
				|| StringUtils.endsWithIgnoreCase(thisBody, "wishes")
				|| StringUtils.endsWithIgnoreCase(thisBody, "wishes,")
				|| StringUtils.endsWithIgnoreCase(thisBody, "thanks")
				|| StringUtils.endsWithIgnoreCase(thisBody, "thanks,")
				|| StringUtils.endsWithIgnoreCase(thisBody, "sincerely")
				|| StringUtils.endsWithIgnoreCase(thisBody, "sincerely,")))	{
			thisBody += "<br/><br/>Kind regards";
		}
		msg.setBody(MessageBody.getMessageBodyFromText("<div style='font-family:PT Sans;font-size:13'>" + 
				"Dear " + currentContact.getName() + ",<br/><br/>" + 
				thisBody.replaceAll(System.getProperty("line.separator"), "<br/>") + "<br/><br/>" + mergeSignature(importSignature)));
		for (String eachString: attachList)	{
			if (eachString != null)	{
				msg.getAttachments().addFileAttachment(eachString);
			}
		}
		if (mainSettings.getSetting("Add Student IDs?")) {
			if (importedRecipients.getIdFound()) {
				if (thisSubject.endsWith("."))	{
					msg.setSubject(thisSubject.trim() + " Student ID: " + currentContact.getID());
				}	else	{
					msg.setSubject(thisSubject.trim() + " - Student ID: " + currentContact.getID());
				}
			}	else	{
				msg.setSubject(thisSubject.trim());
			}
		}	else	{
			msg.setSubject(thisSubject.trim());
		}
		msg.getToRecipients().add(currentContact.getEmail());
		msg.setFrom(new EmailAddress(inbox));
		return msg;
	}
	public void addAttachment(String location)	{
			String[] tempAttach = new String[(attachNum) + 1];
			int tempCount = 0;
			for (int i = 0; i < attachNum; ++i)	{
				if (attachList[i] != null)	{
					tempAttach[tempCount] = attachList[i];
					++tempCount;
				}
			}
			attachList = tempAttach;
			attachList[attachNum] = location;
			++attachNum;
	}
	public void writeErrors(String error)	{
		try {
			error = error.trim();
			PrintWriter errorOutput = new PrintWriter(new FileWriter(".//Errors.txt", true));
			Date errorDate = new Date();
			errorOutput.println("Error occured at: " + errorDate.toString());
			errorOutput.println(error);
			errorOutput.println("");
			errorOutput.close();
		}	catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void sendAll()	{
		sentFinished = false;
			Thread sendThread = new Thread()	{
				public void run()	{
					int total = importedRecipients.getTotal();
					results = "";
					Mailbox sentBox = new Mailbox(inbox);
					FolderId sentBoxSentItems = new FolderId(WellKnownFolderName.SentItems, sentBox);
					FolderId sentBoxDrafts = new FolderId(WellKnownFolderName.Drafts, sentBox);
					for (int i = 1; i < total; ++i)	{
						MergeContact currentContact = importedRecipients.getSpecific(i);
						if (!(currentContact.getEmail().equals("") || currentContact.getName().equals("") || currentContact.getEmail().trim().equals("INVALID")))	{
							try	{
							EmailMessage message = createEmail(i);
							message.save(sentBoxDrafts); //Adds many ms with attachments, seemingly unfixable :(
							message.sendAndSaveCopy(sentBoxSentItems);
							}	catch(ServiceResponseException | ServiceRequestException | FileNotFoundException e)	{
								if (StringUtils.containsIgnoreCase(e.toString(), "The system cannot find the file specified"))	{
									results = "Fatal send error: program cannot find attachment!";
									writeErrors(results);
									sentFinished = true;
									return;
								}	else if (StringUtils.containsIgnoreCase(e.toString(), "The SMTP address has no mailbox associated with it"))	{
									results = ("Fatal send error: sending inbox does not exist!");
									writeErrors(results);
									sentFinished = true;
									return;
								}	else	{
									String error = "Unknown send error for applicant " + currentContact.getEmail() + ": " + e.toString() + dLine;
										results += (error);
										writeErrors(error);
										continue;
								}
							}	catch (Exception e1)	{
								String error = "Unknown send error for applicant " + currentContact.getEmail() + ": " + e1.toString() + dLine;
								results += (error);
								writeErrors(error);
								continue;
							}
						}	else if (currentContact.getEmail().equals("") && currentContact.getName().equals(""))	{
							continue;
						}	else if(currentContact.getEmail().equals(""))	{
							String error = ("Error: " + currentContact.getName() + ": " + currentContact.getID() + " not emailed. Email address missing!") + dLine;
							results += (error);
							writeErrors(error);
							continue;
							} else if (currentContact.getName().equals(""))	{
								String error = ("Error: " + currentContact.getEmail() + ": " + currentContact.getID() + " not emailed. Name is missing!") + dLine;
								results += (error);
								writeErrors(error);
								continue;
							}	else if (currentContact.getEmail().equals("INVALID"))	{
								String error = ("Error: " + currentContact.getName() + ": " + currentContact.getID() + " not emailed. Email address is invalid!") + dLine;
								results += (error);
								writeErrors(error);
								continue;
							}
						}
					sentFinished = true;
				}
			};
			sendThread.setDaemon(true);
			sendThread.start();
	}
	public String preview(String recipientEmail)	{
		String errorString = "";
		try {
			EmailMessage preview = createEmail(importedRecipients.getCurrent());
			File output = new File(".\\preview.eml");
			FileOutputStream os = new FileOutputStream(output);
			BufferedOutputStream bos = new BufferedOutputStream(os);
			Properties props = new Properties();
			Session session = Session.getDefaultInstance(props);
			MimeMessage message = new MimeMessage(session);
			message.setSubject(preview.getSubject().replaceAll("’", "'"), "text/html; charset=UTF-8"); //Occasional Encoding Error
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail.trim()));
			if (attachNum > 0)	{
				BodyPart mainBody = new MimeBodyPart();
				mainBody.setContent(preview.getBody().toString().replaceAll("’", "'"), "text/html; charset=UTF-8");
				Multipart multiPart = new MimeMultipart();
				multiPart.addBodyPart(mainBody);
				for (String eachString: attachList)	{
					mainBody = new MimeBodyPart();
					DataSource source = new FileDataSource(eachString);
					mainBody.setDataHandler(new DataHandler(source));
					mainBody.setFileName(eachString.substring(eachString.lastIndexOf("\\") + 1));
					multiPart.addBodyPart(mainBody);
					}
				message.setContent(multiPart);
				}	else	{
				message.setContent(preview.getBody().toString().replaceAll("’", "'"), "text/html; charset=UTF-8");
			}
			message.writeTo(bos);
			os.close();
			bos.close();
			Runtime.getRuntime().exec(new String[] {"rundll32", "url.dll,FileProtocolHandler", output.getAbsolutePath()});
		}	catch (Exception e) {
			errorString = "Fatal Preview error: " + e.toString();
			writeErrors(errorString);
		}
		return errorString;
	}
	public String findJob()	{
		try {
			NameResolutionCollection nameResolutions = service.resolveName(loginEmail.substring(0, loginEmail.indexOf('@')), ResolveNameSearchLocation.DirectoryOnly, true);
			if (nameResolutions.getCount() == 1) {
				return nameResolutions.nameResolutionCollection(0).getContact().getJobTitle();
			}	else	{
				return "BU Staff";
			}
		} catch (Exception e) {
			return "BU Staff";
		}
	}
	public String findName()	{
		try {
			NameResolutionCollection nameResolutions = service.resolveName(loginEmail.substring(0, loginEmail.indexOf('@')), ResolveNameSearchLocation.DirectoryOnly, true);
			if (nameResolutions.getCount() == 1) {
				return nameResolutions.nameResolutionCollection(0).getContact().getDisplayName();
			}	else	{
				return loginEmail.substring(0, loginEmail.indexOf('@'));
			}
		} catch (Exception e) {
			return loginEmail.substring(0, loginEmail.indexOf('@'));
		}
	}
	public String getPersonal()	{
		if (loginEmail.indexOf('@') > 0)	{
			return loginEmail.substring(0, loginEmail.indexOf('@')).trim();
		}	else	{
			return "";
		}
	}
	public void shutDown()	{
		importFinished = true;
		sentFinished = true;
		if (!(refreshService == null))	{
			refreshService.shutdownNow();
			}
		File preview = new File(".\\preview.eml");
		if (preview.exists())	{
			preview.delete();
		}
	}
	public boolean getLoginSuccess()	{
		return loginSuccess;
	}
	public boolean getImportFinished()	{
		return importFinished;
	}
	public boolean getImportSuccess()	{
		return importSuccess;
	}
	public boolean getSentFinished()	{
		return sentFinished;
	}
	/*public void setAutoAdd(boolean toSet)	{
		autoAdd = toSet;
	}
	public void setFilterError(boolean toSet)	{
		filterError = toSet;
	}
	public boolean getAutoAdd()	{
		return autoAdd;
	}
	public boolean getFilterError()	{
		return filterError;
	}*/
	public String killRefresh()	{
		if(!(refreshService.isShutdown()))	{
			refreshService.shutdown();
		}
		return results;
	}
	public boolean getIdFound()	{
		return importedRecipients.getIdFound();
	}
	private String mergeSignature(String toMerge)	{
		String toReturn = toMerge;
		if (toReturn.indexOf("<<Job_Title>>") != -1)	{
			toReturn = toReturn.replace("<<Job_Title>>", findJob());
		}
		if (toReturn.indexOf("<<Sending_Inbox>>") != -1)	{
			toReturn = toReturn.replace("<<Sending_Inbox>>", inbox);
		}
		if (toReturn.indexOf("<<Sending_Name>>") != -1)	{
			toReturn = toReturn.replace("<<Sending_Name>>", findName());
		}
		return toReturn;
	}
}
