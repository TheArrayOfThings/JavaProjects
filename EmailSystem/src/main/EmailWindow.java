package main;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.wb.swt.SWTResourceManager;
import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.enumeration.misc.ExchangeVersion;
import microsoft.exchange.webservices.data.core.enumeration.property.WellKnownFolderName;
import microsoft.exchange.webservices.data.core.enumeration.search.ResolveNameSearchLocation;
import microsoft.exchange.webservices.data.core.exception.service.remote.ServiceRequestException;
import microsoft.exchange.webservices.data.core.exception.service.remote.ServiceResponseException;
import microsoft.exchange.webservices.data.core.service.item.EmailMessage;
import microsoft.exchange.webservices.data.core.service.schema.FolderSchema;
import microsoft.exchange.webservices.data.credential.ExchangeCredentials;
import microsoft.exchange.webservices.data.credential.WebCredentials;
import microsoft.exchange.webservices.data.property.complex.EmailAddress;
import microsoft.exchange.webservices.data.property.complex.FolderId;
import microsoft.exchange.webservices.data.property.complex.Mailbox;
import microsoft.exchange.webservices.data.property.complex.MessageBody;
import microsoft.exchange.webservices.data.search.FindFoldersResults;
import microsoft.exchange.webservices.data.search.FolderView;
import microsoft.exchange.webservices.data.search.filter.SearchFilter;
import microsoft.exchange.webservices.data.misc.NameResolutionCollection;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Combo;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.Properties;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.custom.StyledText;

public class EmailWindow {
	private static Text txtMain;
	private static Text txtSubject;
	private static Text txtName;
	private static Text txtSID;
	private static Text txtEmail;
	private static Button btnPreview;
	private static Button btnSend;
	private static Button btnPrevious;
	private static Button btnNext;
	private static Button btnImport;
	private static Button btnAddAttachment;
	private static Button btnRemoveAttachment;
	private static Button btnImportoft;
	private static Button btnClear;
	private static Label lblDear;
	private static Label lblStudentId;
	private static Label lblEmail;
	private static Label lblInbox;
	private static Label lblAttachment;
	public static StyledText txtSystem;
	private static Button btnAddStudentId;
	private static ExchangeService service = new ExchangeService(ExchangeVersion.Exchange2010_SP2);
	private static Combo comboDropDownIS, comboAttach;
	private static ApplicantImporter importedApplicants = new ApplicantImporter();
	private static boolean sent = false, importFinished = false, autoAdd = false, fatalRunError = false;
	private static ScheduledExecutorService refreshService;
	private static String inbox = "", userName = "", emailBody = "", signature = "", subject = "", tempString = "", loginEmail = "", loginPassword ="", errorString = "";
	private static String dLine = System.getProperty("line.separator") + System.getProperty("line.separator");
	private static String[] attachList = new String[1];
	private static int attachNum = 0;
	private static Shell shell = new Shell();
	private static String[] mergeList = new String[] {""};

	public static void main(String[] args) {
		Display display = Display.getDefault();
		try {
			File url = new File(".\\URL.txt");
			if (url.exists())	{
				Scanner urlScanner = new Scanner(url);
				service.setUrl(new URI(urlScanner.nextLine()));
				urlScanner.close();
				}
			} catch (URISyntaxException e) {
				writeErrors("Initial URI failed: " + e.toString());
				} catch (FileNotFoundException e1) {
					writeErrors("URI file not found: " + e1.toString());
			}
			shell.setImage(SWTResourceManager.getImage(EmailWindow.class, "/resources/LogoBasic.png"));
			shell.setBackground(SWTResourceManager.getColor(SWT.COLOR_LIST_BACKGROUND));
			shell.setSize(790, 600);
			shell.setText("Ryan's MailMerger");
			shell.addListener(SWT.Close, new Listener()	{
				public void handleEvent(Event event) {
					importFinished = true;
					sent = true;
					if (!(refreshService == null))	{
						refreshService.shutdownNow();
						}
					File preview = new File(".\\preview.eml");
					if (preview.exists())	{
						preview.delete();
					}
					}
				});
			shell.setLayout(new GridLayout(12, false));
			lblDear = new Label(shell, SWT.NONE);
			lblDear.setFont(SWTResourceManager.getFont("PT Sans", 10, SWT.NORMAL));
			lblDear.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
			lblDear.setText("Dear");
			
			txtName = new Text(shell, SWT.BORDER | SWT.READ_ONLY);
			GridData gd_txtName = new GridData(SWT.FILL, SWT.CENTER, true, false, 4, 1);
			gd_txtName.widthHint = 250;
			txtName.setLayoutData(gd_txtName);
			txtName.setFont(SWTResourceManager.getFont("PT Sans", 10, SWT.NORMAL));
			
			btnPrevious = new Button(shell, SWT.NONE);
			btnPrevious.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseDown(MouseEvent e) {
					if (importFinished) {
						MergeContact previous = importedApplicants.getPrevious();
						txtName.setText(previous.getName());
						txtSID.setText(previous.getID());
						txtEmail.setText(previous.getEmail());
					}	else	{
						writeConsole("Error: " + System.getProperty("line.separator") + "Please import applicants first!");
					}
				}
			});
			btnPrevious.setText("<");
			
			btnNext = new Button(shell, SWT.NONE);
			btnNext.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseDown(MouseEvent e) {
					if (importFinished) {
						MergeContact next = importedApplicants.getNext();
						txtName.setText(next.getName());
						txtSID.setText(next.getID());
						txtEmail.setText(next.getEmail());
					}	else	{
						writeConsole("Error: " + System.getProperty("line.separator") + "Please import applicants first!");
					}
				}
			});
			btnNext.setText(">");
			
			lblInbox = new Label(shell, SWT.NONE);
			lblInbox.setFont(SWTResourceManager.getFont("PT Sans", 10, SWT.NORMAL));
			lblInbox.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
			lblInbox.setText("Inbox:");
			
			comboDropDownIS = new Combo(shell, SWT.READ_ONLY);
			comboDropDownIS.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 4, 1));
			comboDropDownIS.add("ukadmissions");
			comboDropDownIS.add("interviews");
			comboDropDownIS.setText("ukadmissions");
			
			lblStudentId = new Label(shell, SWT.NONE);
			lblStudentId.setFont(SWTResourceManager.getFont("PT Sans", 10, SWT.NORMAL));
			lblStudentId.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
			lblStudentId.setText("Student ID: ");
			
			txtSID = new Text(shell, SWT.BORDER | SWT.READ_ONLY);
			txtSID.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 4, 1));
			txtSID.setFont(SWTResourceManager.getFont("PT Sans", 10, SWT.NORMAL));
			new Label(shell, SWT.NONE);
			new Label(shell, SWT.NONE);
			
			lblAttachment = new Label(shell, SWT.NONE);
			lblAttachment.setFont(SWTResourceManager.getFont("PT Sans", 10, SWT.NORMAL));
			lblAttachment.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
			lblAttachment.setText("Attachments:");
			
			comboAttach = new Combo(shell, SWT.READ_ONLY);
			GridData gd_comboAttach = new GridData(SWT.FILL, SWT.FILL, true, false, 4, 1);
			gd_comboAttach.widthHint = 109;
			comboAttach.setLayoutData(gd_comboAttach);
			
			lblEmail = new Label(shell, SWT.NONE);
			lblEmail.setFont(SWTResourceManager.getFont("PT Sans", 10, SWT.NORMAL));
			lblEmail.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
			lblEmail.setText("Email: ");
			
			txtEmail = new Text(shell, SWT.BORDER | SWT.READ_ONLY);
			txtEmail.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 4, 1));
			txtEmail.setFont(SWTResourceManager.getFont("PT Sans", 10, SWT.NORMAL));
			new Label(shell, SWT.NONE);
			new Label(shell, SWT.NONE);
			new Label(shell, SWT.NONE);
			
			btnAddAttachment = new Button(shell, SWT.NONE);
			GridData gd_btnAddAttachment = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
			gd_btnAddAttachment.widthHint = 64;
			btnAddAttachment.setLayoutData(gd_btnAddAttachment);
			btnAddAttachment.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseDown(MouseEvent e) {
					FileDialog dialog = new FileDialog(shell, SWT.OPEN);
					dialog.setFilterExtensions(new String [] {"*.*"});
					dialog.setFilterPath("H:\\");
					addAttachment(dialog.open());
				}
			});
			btnAddAttachment.setText("Add");
			
			btnRemoveAttachment = new Button(shell, SWT.NONE);
			GridData gd_btnRemoveAttachment = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
			gd_btnRemoveAttachment.widthHint = 69;
			btnRemoveAttachment.setLayoutData(gd_btnRemoveAttachment);
			btnRemoveAttachment.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseDown(MouseEvent e) {
					if (attachNum > 0)	{
						writeConsole("File: '" + comboAttach.getText() + "' removed successfully!");
						attachList[comboAttach.indexOf(comboAttach.getText())] = null;
						comboAttach.remove(comboAttach.getText());
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
						comboAttach.select((attachNum) - 1);
					}	else	{
						writeConsole("No attachment to remove!");
					}
				}
			});
			btnRemoveAttachment.setText("Remove");
			new Label(shell, SWT.NONE);
			new Label(shell, SWT.NONE);
			
			txtSubject = new Text(shell, SWT.BORDER);
			txtSubject.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 7, 1));
			txtSubject.setText("[Replace with subject]");
			txtSubject.setFont(SWTResourceManager.getFont("PT Sans", 10, SWT.NORMAL));
			
			txtSystem = new StyledText(shell, SWT.BORDER | SWT.READ_ONLY | SWT.WRAP); // | SWT.V_SCROLL);
			txtSystem.setAlignment(SWT.CENTER);
			txtSystem.setTopMargin(10);
			txtSystem.setText("Welcome to Ryan's MailMerger!\r\n\r\n1):  Import your data using the 'Import' button.\r\n\r\n2): Add a subject and complete the body.\r\n\r\n3): Preview before sending!\r\n\r\n**'Dear [name]' is automatically added!**\r\n\r\n**'Student ID: [Student ID]' is automatically added!**\r\n\r\n**Your signature is automatically added!**\r\n\r\n");
			txtSystem.setForeground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
			txtSystem.setFont(SWTResourceManager.getFont("Calibri", 11, SWT.NORMAL));
			txtSystem.setBackground(SWTResourceManager.getColor(SWT.COLOR_BLACK));
			GridData gd_txtSystem = new GridData(SWT.FILL, SWT.FILL, true, true, 5, 2);
			gd_txtSystem.widthHint = 389;
			txtSystem.setLayoutData(gd_txtSystem);
			txtSystem.addListener(SWT.Modify, new Listener(){
			    public void handleEvent(Event e){
			    	txtSystem.setTopIndex(txtSystem.getLineCount() - 1);
			    }
			});
			
			txtMain = new Text(shell, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
			GridData gd_txtMain = new GridData(SWT.FILL, SWT.FILL, true, true, 7, 1);
			gd_txtMain.widthHint = 363;
			txtMain.setLayoutData(gd_txtMain);
			txtMain.setFont(SWTResourceManager.getFont("PT Sans", 10, SWT.NORMAL));
			txtMain.setText("[Replace with body of email]");
			
			btnImport = new Button(shell, SWT.NONE);
			btnImport.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
			btnImport.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseDown(MouseEvent e) {
					try	{
						FileDialog dialog = new FileDialog(shell, SWT.OPEN);
						dialog.setFilterExtensions(new String [] {"*.xls*"});
						dialog.setFilterPath("H:\\");
						String fileLocation = dialog.open();
						if (fileLocation == null)	{
							writeConsole("Please select a file to import!");
						}	else	{
							disableMain();
			        		int sheetNumber = 0;
			        		Workbook tempBook = new XSSFWorkbook(new File(fileLocation));
			        		if (tempBook.getNumberOfSheets() > 1)	{
			        			SelectSheet selectSheet = new SelectSheet(shell, SWT.CLOSE | SWT.SYSTEM_MODAL, tempBook);
			        			sheetNumber = selectSheet.open();
			        			tempBook.close();
			        		}
							startImport(fileLocation, sheetNumber);	
						}
					}	catch (Exception e1)	{
						writeConsole("Catastropic import error: You did something Ryan didn't think of :(");
						setError(e1.toString());
						importFinished = false;
						enableMain();
					}
				}
			});
			btnImport.setText("Import");
			
			btnPreview = new Button(shell, SWT.NONE);
			GridData gd_btnPreview = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
			gd_btnPreview.widthHint = 57;
			btnPreview.setLayoutData(gd_btnPreview);
			btnPreview.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseDown(MouseEvent e) {
					setEmailParas();
					disableMain();
					preview();
				}
			});
			btnPreview.setText("Preview");
			
			btnSend = new Button(shell, SWT.NONE);
			GridData gd_btnSend = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
			gd_btnSend.widthHint = 58;
			btnSend.setLayoutData(gd_btnSend);
			btnSend.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseDown(MouseEvent e) {
					if (importFinished)	{
						if (txtSubject.getText().trim().contains("[Replace with subject]") || txtSubject.getText().trim().equals("")) {
							writeConsole("Error: " + System.getProperty("line.separator") + "Please add a subject before sending to applicants!");
						}	else if (txtMain.getText().trim().contains("[Replace with body of email]") || txtMain.getText().trim().equals(""))	{
							writeConsole("Error: " + System.getProperty("line.separator") + "Please change the body of the email before sending to applicants!");
						}	else if (txtMain.getText().trim().startsWith("dear"))	{
							writeConsole("Error: you inserted your own 'Dear'. Please remove this before sending!");
						}	else	{
							try {
								setEmailParas();
								Shell confirmShell = new Shell(shell, SWT.TITLE|SWT.SYSTEM_MODAL| SWT.CLOSE | SWT.MAX);
								Confirm confirm = new Confirm(confirmShell);
								confirm.open();
							} catch (Exception e1) {
								writeErrors("Send error: " + e1.toString());
							}
						}
					}	else	{
						writeConsole("Error: " + System.getProperty("line.separator") + "Please import applicants before tyring to send emails!");
					}
				}
			});
			btnSend.setText("Send");
			
			addMainMenu(txtMain);
			addSubjectMenu(txtSubject);
			
			btnClear = new Button(shell, SWT.NONE);
			GridData gd_btnClear = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
			gd_btnClear.widthHint = 59;
			btnClear.setLayoutData(gd_btnClear);
			btnClear.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseDown(MouseEvent e) {
					txtMain.setText("");
					txtSubject.setText("");
				}
			});
			btnClear.setText("Clear");
			new Label(shell, SWT.NONE);
			new Label(shell, SWT.NONE);
			new Label(shell, SWT.NONE);
			
			btnImportoft = new Button(shell, SWT.NONE);
			btnImportoft.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseDown(MouseEvent e) {
					try	{
						String importString = TemplateImport.importTemplate(shell).trim();
						if (!(importString.equals("")))	{
							txtMain.setText(importString);
						}
					}	catch (Exception e1)	{
						writeConsole("Template error: Template appears to be corrupted!");
						writeErrors("Template error: " + e1.toString());
					}
				}
			});
			btnImportoft.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
			btnImportoft.setText("Template Import");
			
			btnAddStudentId = new Button(shell, SWT.CHECK);
			btnAddStudentId.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
			btnAddStudentId.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseUp(MouseEvent e) {
					if (autoAdd == true) {
						btnAddStudentId.setSelection(false);
						autoAdd = false;
					}	else	{
						btnAddStudentId.setSelection(true);
						autoAdd = true;
					}
				}
			});
			btnAddStudentId.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 3, 1));
			btnAddStudentId.setText("Automatically add Student IDs");
			btnAddStudentId.setEnabled(false);
			
			shell.open();
			shell.layout();
			LoginDialog login = new LoginDialog(shell, SWT.CLOSE | SWT.SYSTEM_MODAL);
			disableMain();
			login.open();
			while (!shell.isDisposed()) {
				if (!display.readAndDispatch()) {
					display.sleep();
				}
			}
	}
	public static void writeConsole(String toWrite)	{
		txtSystem.setText(txtSystem.getText() + toWrite + dLine);
	}
	public static void enableMain()	{
		btnNext.setEnabled(true);
		btnPrevious.setEnabled(true);
		txtSID.setEnabled(true);
		comboDropDownIS.setEnabled(true);
		txtEmail.setEnabled(true);
		txtName.setEnabled(true);
		comboAttach.setEnabled(true);
		txtSubject.setEnabled(true);
		txtSystem.setEnabled(true);
		txtMain.setEnabled(true);
		btnImport.setEnabled(true);
		btnPreview.setEnabled(true);
		btnSend.setEnabled(true);
		btnAddAttachment.setEnabled(true);
		btnRemoveAttachment.setEnabled(true);
		btnClear.setEnabled(true);
		btnImportoft.setEnabled(true);
		if (importFinished)	{
			if (importedApplicants.getIdFound())	{
				btnAddStudentId.setEnabled(true);
			}
		}
	}
	public static void disableMain()	{
		btnNext.setEnabled(false);
		btnPrevious.setEnabled(false);
		txtSID.setEnabled(false);
		comboDropDownIS.setEnabled(false);
		txtEmail.setEnabled(false);
		txtName.setEnabled(false);
		comboAttach.setEnabled(false);
		txtSubject.setEnabled(false);
		txtSystem.setEnabled(false);
		txtMain.setEnabled(false);
		btnImport.setEnabled(false);
		btnPreview.setEnabled(false);
		btnSend.setEnabled(false);
		btnAddAttachment.setEnabled(false);
		btnRemoveAttachment.setEnabled(false);
		btnClear.setEnabled(false);
		btnImportoft.setEnabled(false);
		btnAddStudentId.setEnabled(false);
	}
	public static void setMergeList(String[] toSet)	{
		mergeList = toSet;
	}
	public static boolean credCheck()	{
		boolean accepted = false;
		ExchangeCredentials credentials = new WebCredentials(loginEmail, loginPassword);
		try {
			InternetAddress check = new InternetAddress(loginEmail);
			check.validate();
		} catch (AddressException e1) {
			return accepted;
		}
		service.setCredentials(credentials);
		FindFoldersResults credentialCheck;
		try {
			credentialCheck = service.findFolders(WellKnownFolderName.Root, new SearchFilter.IsGreaterThan(FolderSchema.TotalCount, 0), new FolderView(10));
			if (credentialCheck.getTotalCount() > 0) {
				accepted = true;
				PrintWriter uriOutput = new PrintWriter(".//URL.txt");
				uriOutput.println(service.getUrl().toString());
				uriOutput.close();
				}
		} catch (ServiceRequestException e) {
			if (e.toString().indexOf("401") == -1)	{
					try {
						service.autodiscoverUrl(loginEmail);
						return credCheck();
					} catch (Exception e1) {
						writeErrors("Autodiscover error: " + e1.toString());
					}
			}	else	{
				writeErrors("Non-401 credential error: " + e.toString());
			}
		} catch (Exception e) {
			writeErrors("Unknown credential error: " + e.toString());
		}
		return accepted;
	}
	public static void setEmail(String emailToTest)	{
		loginEmail = emailToTest;
	}
	public static void setPW(String PWToTest)	{
		loginPassword = PWToTest;
	}
	private static void startImport(String fileLocation, int sheetNumber)	{
	    Thread importThread = new Thread(new Runnable() {
	        public void run() {
	        	fatalRunError = false;
	        	errorString = "";
	        	File xlFile = new File(fileLocation);
	        	try {
					importedApplicants.importApplicants(SheetImporter.importWorkbook(xlFile, sheetNumber));
				} catch (InvalidFormatException e) {
					setError(e.toString());
				} catch (FilteredSheetException e) {
					setError(e.toString());
				} catch (IOException e) {
					setError(e.toString());
				}	catch (OutOfMemoryError nme)	{
					setError("Java ran out of memory :( Spreadsheet too large : " + nme.toString());
				}	catch (NullPointerException n)	{
					setError("Error: Selected sheet appears to be blank.");
				}	catch (Exception e)	{
					e.printStackTrace();
					setError(e.toString());
				}
	        	}
	        });
	    tempString = txtSystem.getText();
		txtSystem.setText("Importing..." + System.getProperty("line.separator"));
		importThread.setDaemon(true);
	    importThread.start();
	    refreshDisplay(refreshImport); 
	}
	public static void startSend()	{
		sent = false;
		fatalRunError = false;
		errorString = "";
		sendAll();
		tempString = txtSystem.getText();
		refreshDisplay(refreshSend);
		}
	private static void refreshDisplay(Runnable toRefresh)	{
	    Runnable runRefresh = new Runnable() {
	        public void run() {
	        	Display.getDefault().asyncExec(toRefresh);
	        }
	    };
	    refreshService = Executors.newSingleThreadScheduledExecutor();
	    refreshService.scheduleAtFixedRate(runRefresh, 0, 33, TimeUnit.MILLISECONDS);
	}
	static Runnable refreshImport = new Runnable()	{
		@Override
		public void run() {
			txtSystem.setText(txtSystem.getText() + " ...");
			if (importFinished == true)	{
				refreshService.shutdown();
				if (importedApplicants.getImportSuccess()) {
					addMainMenu(txtMain);
					addSubjectMenu(txtSubject);
					enableMain();
					txtSystem.setText(tempString + importedApplicants.getResults() + dLine);
					MergeContact first = importedApplicants.getSpecific(1);
					txtName.setText(first.getName());
					txtEmail.setText(first.getEmail());
					if (importedApplicants.getIdFound() == true)	{
						txtSID.setText(first.getID());
						btnAddStudentId.setEnabled(true);
						btnAddStudentId.setSelection(true);
						autoAdd = true;
					}	else	{
						txtSID.setText("");
						btnAddStudentId.setSelection(false);
						btnAddStudentId.setEnabled(false);
						autoAdd = false;						
					}
				}	else	{
					txtEmail.setText("");
					txtSID.setText("");
					txtName.setText("");
					importFinished = false;
					enableMain();
				}
			}
			if (fatalRunError == true) {
				refreshService.shutdown();
				txtSystem.setText(tempString + errorString + dLine);
				fatalRunError = false;
				enableMain();
			}
		}
	};
	static Runnable refreshSend = new Runnable()	{
		@Override
		public void run() {
			MergeContact current = importedApplicants.getSpecific(importedApplicants.getCurrent());
			txtName.setText(current.getName());
			txtSID.setText(current.getID());
			txtEmail.setText(current.getEmail());
			txtSystem.setText("Sending to " + txtName.getText() + dLine + 
					"Student ID: " + txtSID.getText() + dLine + 
					"Email: " + txtEmail.getText() + dLine);
			if (sent)	{
				enableMain();
				if (importedApplicants.getIdFound() == false)	{
					btnAddStudentId.setSelection(false);
					btnAddStudentId.setEnabled(false);				
				}
				refreshService.shutdown();
				if (errorString.equals(""))	{
					txtSystem.setText(tempString + "All emails sent without error!" + dLine);
				}	else	{
					txtSystem.setText(tempString + "Emails sent with errors: " + dLine + errorString +
							"All errors have been saved to 'Errors.txt'." + dLine +
							"Please be aware that invalid email addresses will bounce back to the inbox, and will not be included in the above errors." + dLine);
					writeErrors(errorString);
				}
			}
			if (fatalRunError == true) {
				refreshService.shutdown();
				txtSystem.setText(tempString + errorString + dLine);
				fatalRunError = false;
				enableMain();
			}
		}
	};
	public static void setError(String errorPara)	{
		fatalRunError = true;
		errorString = errorPara;
		writeErrors(errorPara);
	}
	public static void setImportFinished(boolean importToSet)	{
		importFinished = importToSet;
	}
	private static void setEmailParas()	{
		userName = findName();
		inbox = comboDropDownIS.getText().trim() + loginEmail.substring(loginEmail.indexOf('@')).trim();
		emailBody = txtMain.getText();
		emailBody = emailBody.trim();
		subject = txtSubject.getText().trim();
		signature = "";
	}
	private static EmailMessage createEmail(int current) throws Exception	{
		String thisBody = emailBody, thisSubject = subject;
		if (importFinished)	{
			for (int i = 0; i < importedApplicants.getMergeSheet().getTotalColumns(); ++i) {
				try	{
					thisSubject = thisSubject.replaceAll(Pattern.quote("<<" + mergeList[i] + ">>"), importedApplicants.getMainSheet().getRow(current).getCell(i).getStringCellValue());
					thisBody = thisBody.replaceAll(Pattern.quote("<<" + mergeList[i] + ">>"), importedApplicants.getMainSheet().getRow(current).getCell(i).getStringCellValue());
				}	catch (IllegalStateException e)	{
					thisSubject = thisSubject.replaceAll(Pattern.quote("<<" + mergeList[i] + ">>"), String.valueOf(Math.round(importedApplicants.getMainSheet().getRow(current).getCell(i).getNumericCellValue())));
					thisBody = thisBody.replaceAll(Pattern.quote("<<" + mergeList[i] + ">>"), String.valueOf(Math.round(importedApplicants.getMainSheet().getRow(current).getCell(i).getNumericCellValue())));
				}	catch (NullPointerException n)	{
					thisSubject = thisSubject.replaceAll(Pattern.quote("<<" + mergeList[i] + ">>"), "");
					thisBody = thisBody.replaceAll(Pattern.quote("<<" + mergeList[i] + ">>"), "");
				}	catch (Exception lastResort)	{
					throw new Exception();
				}
			}
		}
		EmailMessage msg = new EmailMessage(service);
		MergeContact currentContact = importedApplicants.getSpecific(current);
		msg.setBody(MessageBody.getMessageBodyFromText("<div style='font-family:PT Sans;font-size:13'>" + 
				"Dear " + currentContact.getName() + ",<br/><br/>" + 
				thisBody.replaceAll(System.getProperty("line.separator"), "<br/>") + signature));
		for (String eachString: attachList)	{
			if (eachString != null)	{
				msg.getAttachments().addFileAttachment(eachString);
			}
		}
		if (autoAdd) {
			if (thisSubject.endsWith("."))	{
				msg.setSubject(thisSubject.trim() + " Student ID: " + currentContact.getID());
			}	else	{
				msg.setSubject(thisSubject.trim() + " - Student ID: " + currentContact.getID());
			}
		}	else	{
			msg.setSubject(thisSubject.trim());
		}
		msg.getToRecipients().add(currentContact.getEmail());
		msg.setFrom(new EmailAddress(inbox));
		return msg;
	}
	public static void addAttachment(String attachmentLocation)	{
		if (attachmentLocation != null)	{
			File testFile = new File (attachmentLocation);
			if (testFile.exists()) {
				writeConsole("File: '" + attachmentLocation.substring(attachmentLocation.lastIndexOf("\\") + 1) + "' added successfully!");
				comboAttach.add(attachmentLocation.substring(attachmentLocation.lastIndexOf("\\") + 1));
				String[] tempAttach = new String[(attachNum) + 1];
				int tempCount = 0;
				for (int i = 0; i < attachNum; ++i)	{
					if (attachList[i] != null)	{
						tempAttach[tempCount] = attachList[i];
						++tempCount;
					}
				}
				attachList = tempAttach;
				attachList[attachNum] = attachmentLocation;
				comboAttach.select(attachNum);
				++attachNum;
			}	else	{
				writeConsole("Attachment not found!");
			}
		}
	}
	private static void writeErrors(String error)	{
		try {
			error = error.trim();
			PrintWriter errorOutput = new PrintWriter(new FileWriter("Errors.txt", true));
			Date errorDate = new Date();
			errorOutput.println("Error occured at: " + errorDate.toString());
			errorOutput.println(error);
			errorOutput.println("");
			errorOutput.close();
		}	catch (Exception e) {
			e.printStackTrace();
		}
	}
	private static void sendAll()	{
			Thread sendThread = new Thread()	{
				public void run()	{
					int total = importedApplicants.getTotal();
					errorString = "";
					Mailbox sentBox = new Mailbox(inbox);
					FolderId sentBoxSentItems = new FolderId(WellKnownFolderName.SentItems, sentBox);
					FolderId sentBoxDrafts = new FolderId(WellKnownFolderName.Drafts, sentBox);
					for (int i = 1; i < total; ++i)	{
						MergeContact currentContact = importedApplicants.getSpecific(i);
						if (!(currentContact.getEmail().equals("") || currentContact.getName().equals("") || currentContact.getEmail().trim().equals("INVALID")))	{
							try	{
							EmailMessage message = createEmail(i);
							message.save(sentBoxDrafts); //Adds many ms with attachments, seemingly unfixable :(
							message.sendAndSaveCopy(sentBoxSentItems);
							}	catch(ServiceResponseException | ServiceRequestException | FileNotFoundException e)	{
								if (StringUtils.containsIgnoreCase(e.toString(), "The system cannot find the file specified"))	{
									setError("Fatal send error: program cannot find attachment!");
									return;
								}	else	{
									errorString += ("Unknown send error for applicant " + currentContact.getEmail() + ": " + e.toString() + dLine);
									continue;
								}
							}	catch (Exception e1)	{
								errorString += ("Unknown send error for applicant " + currentContact.getEmail() + ": " + e1.toString() + dLine);
								continue;
							}
						}	else if (currentContact.getEmail().equals("") && currentContact.getName().equals(""))	{
							continue;
						}	else if(currentContact.getEmail().equals(""))	{
							errorString += ("Error: " + currentContact.getName() + ": " + currentContact.getID() + "  not emailed. Email address missing!") + dLine;
							continue;
							} else if (currentContact.getName().equals(""))	{
								errorString += ("Error: " + currentContact.getEmail() + ": " + currentContact.getID() + " not emailed. Name is missing!")  + dLine;
								continue;
							}	else if (currentContact.getEmail().equals("INVALID"))	{
								errorString += ("Error: " + currentContact.getName() + ": " + currentContact.getID() + " not emailed. Email address is invalid!")  + dLine;
								continue;
							}
						}
					sent = true;
				}
			};
			sendThread.start();
	}
	private static void preview()	{
		try {
			EmailMessage preview = createEmail(importedApplicants.getCurrent());
			File output = new File(".\\preview.eml");
			FileOutputStream os = new FileOutputStream(output);
			BufferedOutputStream bos = new BufferedOutputStream(os);
			Properties props = new Properties();
			Session session = Session.getDefaultInstance(props);
			MimeMessage message = new MimeMessage(session);
			message.setSubject(preview.getSubject().replaceAll("’", "'"), "text/html; charset=UTF-8"); //Occasional Encoding Error
			message.setRecipients(Message.RecipientType.TO,
		            InternetAddress.parse(txtEmail.getText()));
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
            Runtime.getRuntime().exec(new String[]
            {"rundll32", "url.dll,FileProtocolHandler",
            	output.getAbsolutePath()});
            enableMain();
		} catch (ServiceRequestException | FileNotFoundException s)	{
			enableMain();
			writeConsole("Unable to preview: Attachment '" + comboAttach.getText() + "' not found.");
		}	catch (Exception e) {
			enableMain();
			writeConsole("Fatal Preview error: " + e.toString());
			e.printStackTrace();
		}
	}
	private static String findJob()	{
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
	private static String findName()	{
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
	private static void insertMerge(String mergeToAdd, Text text)	{
		int caretPosition = text.getSelection().x;
		int selectedChars = text.getSelectionCount();
		if (selectedChars > 0)	{
			String formatted = "<<" + mergeToAdd + ">>";
			text.setText(text.getText(0, (caretPosition - 1)) + formatted + text.getText(caretPosition + selectedChars, text.getText().length()));
			text.setSelection(caretPosition + formatted.length());
		}	else	{
			String formatted = "<<" + mergeToAdd + ">>";
			text.setText(text.getText(0, (caretPosition - 1)) + formatted + text.getText(caretPosition, text.getText().length()));
			text.setSelection(caretPosition + formatted.length());
		}
	}
	private static void insertTag(String tagToAdd, Text text)	{
		String toInsert = "", toEnd = "";
		int caretPosition = text.getSelection().x;
		int selectedChars = text.getSelectionCount();
		switch (tagToAdd)	{
		case "Bold": toInsert = "<b>"; toEnd = "</b>";
		break;
		case "Italics": toInsert = "<i>"; toEnd = "</i>";
		break;
		case "Strikethrough": toInsert = "<s>"; toEnd = "</s>";
		break;
		case "Underline": toInsert = "<u>"; toEnd ="</u>";
		break;
		case "Red": toInsert = "<span style='color:red'>"; toEnd = "</span>";
		break;
		case "Blue": toInsert = "<span style='color:blue'>"; toEnd = "</span>";
		break;
		case "Green": toInsert = "<span style='color:green'>"; toEnd = "</span>";
		break;
		case "Custom Colour":
			ColorDialog colourDialog = new ColorDialog(shell, SWT.OPEN);
			RGB customColourRGB = colourDialog.open();
			if (customColourRGB == null || customColourRGB.toString().equals(""))	{
				return;
			}
			String customColour = String.format("#%02x%02x%02x", customColourRGB.red, customColourRGB.green, customColourRGB.blue);  
			toInsert = "<span style='color:" + customColour + "'>"; toEnd = "</span>";
		break;
		}
		if (selectedChars > 0)	{
			text.setText(text.getText(0, (caretPosition - 1)) + toInsert + text.getText(caretPosition, (caretPosition + selectedChars) - 1) + toEnd + text.getText(caretPosition + selectedChars, text.getText().length()));
			text.setSelection(caretPosition + toInsert.length() + toEnd.length() + selectedChars);
		}
	}
	private static void insertHyperlink(Text text)	{
		String toInsert = "", toEnd = "";
		int caretPosition = text.getSelection().x;
		int selectedChars = text.getSelectionCount();
		String linkToAdd = new HyperlinkDialog(shell, SWT.CLOSE).open();
		toInsert = "<a href='" + linkToAdd + "'>"; toEnd = "</a>";
		if (selectedChars > 0 && linkToAdd != null && (!(linkToAdd.equals(""))))	{
			text.setText(text.getText(0, (caretPosition - 1)) + toInsert + text.getText(caretPosition, (caretPosition + selectedChars) - 1) + toEnd + text.getText(caretPosition + selectedChars, text.getText().length()));
			text.setSelection(caretPosition + toInsert.length() + toEnd.length() + selectedChars);
		}
	}
	private static void addMainMenu(Text textToAdd)	{
	    Menu popupMenu = new Menu(textToAdd);	    
	    MenuItem item = new MenuItem (popupMenu, SWT.PUSH);
	    item.setText("Cut");
	    item.addListener(SWT.Selection, new Listener()	{
			@Override
			public void handleEvent(Event event) {
				textToAdd.cut();
			}
    	});
	    item = new MenuItem (popupMenu, SWT.PUSH);
	    item.setText("Copy");
	    item.addListener(SWT.Selection, new Listener()	{
			@Override
			public void handleEvent(Event event) {
				textToAdd.copy();
			}
    	});
	    item = new MenuItem(popupMenu, SWT.PUSH);
	    item.setText("Paste");
	    item.addListener(SWT.Selection, new Listener()	{
			@Override
			public void handleEvent(Event event) {
				textToAdd.paste();
			}
    	});
	    item = new MenuItem(popupMenu, SWT.PUSH);
	    item.setText("Select All");
	    item.addListener(SWT.Selection, new Listener()	{
			@Override
			public void handleEvent(Event event) {
				textToAdd.selectAll();
			}
    	});
	    MenuItem addField = new MenuItem(popupMenu, SWT.CASCADE);
	    addField.setText("Insert Merge Field");
	    MenuItem addTag = new MenuItem(popupMenu, SWT.CASCADE);
	    addTag.setText("Add Style to Selection");
	    Menu tagMenu = new Menu(popupMenu);
	    String[] supportedTags = new String[]	{
	    		"Bold", "Italics", "Strikethrough", "Underline", "Red", "Blue", "Green", "Custom Colour"
	    };
	    for (String eachString: supportedTags)	{
	    	MenuItem tag = new MenuItem(tagMenu, SWT.CASCADE);
	    	tag.setText(eachString);
	    	tag.addListener(SWT.Selection, new Listener()	{
				@Override
				public void handleEvent(Event event) {
					insertTag(eachString, textToAdd);
				}
	    	});
	    }
	    MenuItem hyperlink = new MenuItem(popupMenu, SWT.CASCADE);
	    hyperlink.setText("Hyperlink Selection");
	    hyperlink.addListener(SWT.Selection, new Listener()	{
			@Override
			public void handleEvent(Event event) {
				insertHyperlink(textToAdd);
			}
    	});
	    Menu newMenu = new Menu(popupMenu);
	    for (String eachString: mergeList)	{
	    	MenuItem field = new MenuItem(newMenu, SWT.CASCADE);
	    	field.setText(eachString);
	    	field.addListener(SWT.Selection, new Listener()	{
				@Override
				public void handleEvent(Event event) {
					insertMerge(eachString, textToAdd);
				}
	    		
	    	});
	    }
	    addTag.setMenu(tagMenu);
	    addField.setMenu(newMenu);
	    textToAdd.setMenu(popupMenu);
	}
	public static void addPersonal()	{
		comboDropDownIS.add(loginEmail.substring(0, loginEmail.indexOf('@')).trim());
	}
	private static void addSubjectMenu(Text textToAdd)	{
	    Menu popupMenu = new Menu(textToAdd);	    
	    MenuItem item = new MenuItem (popupMenu, SWT.PUSH);
	    item.setText("Cut");
	    item.addListener(SWT.Selection, new Listener()	{
			@Override
			public void handleEvent(Event event) {
				textToAdd.cut();
			}
    	});
	    item = new MenuItem (popupMenu, SWT.PUSH);
	    item.setText("Copy");
	    item.addListener(SWT.Selection, new Listener()	{
			@Override
			public void handleEvent(Event event) {
				textToAdd.copy();
			}
    	});
	    item = new MenuItem(popupMenu, SWT.PUSH);
	    item.setText("Paste");
	    item.addListener(SWT.Selection, new Listener()	{
			@Override
			public void handleEvent(Event event) {
				textToAdd.paste();
			}
    	});
	    item = new MenuItem(popupMenu, SWT.PUSH);
	    item.setText("Select All");
	    item.addListener(SWT.Selection, new Listener()	{
			@Override
			public void handleEvent(Event event) {
				textToAdd.selectAll();
			}
    	});
	    MenuItem addField = new MenuItem(popupMenu, SWT.CASCADE);
	    addField.setText("Insert Merge Field");
	    Menu newMenu = new Menu(popupMenu);
	    for (String eachString: mergeList)	{
	    	MenuItem field = new MenuItem(newMenu, SWT.CASCADE);
	    	field.setText(eachString);
	    	field.addListener(SWT.Selection, new Listener()	{
				@Override
				public void handleEvent(Event event) {
					insertMerge(eachString, textToAdd);
				}
	    	});
	    }
	    addField.setMenu(newMenu);
	    textToAdd.setMenu(popupMenu);
	}
}