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
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
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
	private static Text txtMain, txtSubject, txtName, txtSID, txtEmail;
	private static Button btnPreview, btnSend, btnPrevious, btnNext, btnImport, btnAddAttachment, btnRemoveAttachment;;
	private static Label lblDear, lblStudentId, lblEmail, lblInbox, lblAttachment;
	private static StyledText txtSystem;
	private static ExchangeService service = new ExchangeService(ExchangeVersion.Exchange2010_SP2);
	private static Combo comboDropDownIS, comboAttach;
	private static ContactList contacts = new ContactList();
	private static boolean sent = false, runError = false, credentialsAccepted = false, importFinished = false;
	private static ScheduledExecutorService refreshService;
	private static String inbox = "", userName = "", emailBody = "", signature = "", subject = "", errorString = "", tempString = "", loginEmail = "", loginPassword ="";
	private static String dLine = System.getProperty("line.separator") + System.getProperty("line.separator");
	private static String[] mergeList = new String[]{"Empty"};
	private static String[] attachList = new String[1];
	private static int attachNum = 0;
	private static Shell shell = new Shell();

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
				writeErrors(e.toString());
				} catch (FileNotFoundException e1) {
					writeErrors(e1.toString());
			}
			shell.setImage(SWTResourceManager.getImage(EmailWindow.class, "/resources/LogoBasic.png"));
			shell.setBackground(SWTResourceManager.getColor(SWT.COLOR_LIST_BACKGROUND));
			shell.setSize(800, 554);
			shell.setText("Ryan's MailMerger");
			shell.setLayout(new GridLayout(12, false));
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
			lblDear = new Label(shell, SWT.NONE);
			lblDear.setFont(SWTResourceManager.getFont("PT Sans", 10, SWT.NORMAL));
			lblDear.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
			lblDear.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
			lblDear.setText("Dear");
			
			txtName = new Text(shell, SWT.BORDER | SWT.READ_ONLY);
			txtName.setFont(SWTResourceManager.getFont("PT Sans", 10, SWT.NORMAL));
			GridData gd_txtName = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
			gd_txtName.widthHint = 31;
			txtName.setLayoutData(gd_txtName);
			txtName.setEnabled(false);
			
			btnPrevious = new Button(shell, SWT.NONE);
			btnPrevious.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseDown(MouseEvent e) {
					if (importFinished) {
						MergeContact previous = contacts.getPrevious();
						txtName.setText(previous.getName());
						txtSID.setText(previous.getID());
						txtEmail.setText(previous.getEmail());
					}	else	{
						txtSystem.setText(txtSystem.getText() + "Error: " + System.getProperty("line.separator") + "Please import applicants first!" + dLine);
					}
				}
			});
			btnPrevious.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
			btnPrevious.setText("<");
			btnPrevious.setEnabled(false);
			
			btnNext = new Button(shell, SWT.NONE);
			btnNext.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseDown(MouseEvent e) {
					if (importFinished) {
						MergeContact next = contacts.getNext();
						txtName.setText(next.getName());
						txtSID.setText(next.getID());
						txtEmail.setText(next.getEmail());
					}	else	{
						txtSystem.setText(txtSystem.getText() + "Error: " + System.getProperty("line.separator") + "Please import applicants first!" + dLine);
					}
				}
			});
			btnNext.setText(">");
			btnNext.setEnabled(false);
			
			lblInbox = new Label(shell, SWT.NONE);
			lblInbox.setFont(SWTResourceManager.getFont("PT Sans", 10, SWT.NORMAL));
			lblInbox.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
			lblInbox.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
			lblInbox.setText("Inbox:");
			
			comboDropDownIS = new Combo(shell, SWT.READ_ONLY);
			GridData gd_comboDropDownIS = new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1);
			gd_comboDropDownIS.widthHint = 98;
			comboDropDownIS.setLayoutData(gd_comboDropDownIS);
			comboDropDownIS.add(""); //add inbox name (before @ symbol)
			comboDropDownIS.add("");
			comboDropDownIS.setText("");
			comboDropDownIS.setEnabled(false);
			new Label(shell, SWT.NONE);
			new Label(shell, SWT.NONE);
			new Label(shell, SWT.NONE);
			new Label(shell, SWT.NONE);
			
			lblStudentId = new Label(shell, SWT.NONE);
			lblStudentId.setFont(SWTResourceManager.getFont("PT Sans", 10, SWT.NORMAL));
			lblStudentId.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
			lblStudentId.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
			lblStudentId.setText("Student ID: ");
			
			txtSID = new Text(shell, SWT.BORDER | SWT.READ_ONLY);
			txtSID.setFont(SWTResourceManager.getFont("PT Sans", 10, SWT.NORMAL));
			GridData gd_txtSID = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
			gd_txtSID.widthHint = 71;
			txtSID.setLayoutData(gd_txtSID);
			txtSID.setEnabled(false);
			new Label(shell, SWT.NONE);
			new Label(shell, SWT.NONE);
			
			lblAttachment = new Label(shell, SWT.NONE);
			lblAttachment.setFont(SWTResourceManager.getFont("PT Sans", 10, SWT.NORMAL));
			lblAttachment.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
			lblAttachment.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
			lblAttachment.setText("Attachments:");
			
			comboAttach = new Combo(shell, SWT.READ_ONLY);
			GridData gd_comboAttach = new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1);
			gd_comboAttach.widthHint = 99;
			comboAttach.setLayoutData(gd_comboAttach);
			comboAttach.setEnabled(false);
			new Label(shell, SWT.NONE);
			new Label(shell, SWT.NONE);
			new Label(shell, SWT.NONE);
			
			lblEmail = new Label(shell, SWT.NONE);
			lblEmail.setFont(SWTResourceManager.getFont("PT Sans", 10, SWT.NORMAL));
			lblEmail.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
			lblEmail.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
			lblEmail.setText("Email: ");
			
			txtEmail = new Text(shell, SWT.BORDER | SWT.READ_ONLY);
			txtEmail.setFont(SWTResourceManager.getFont("PT Sans", 10, SWT.NORMAL));
			GridData gd_txtEmail = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
			gd_txtEmail.widthHint = 34;
			txtEmail.setLayoutData(gd_txtEmail);
			txtEmail.setEnabled(false);
			new Label(shell, SWT.NONE);
			new Label(shell, SWT.NONE);
			new Label(shell, SWT.NONE);
			
			btnAddAttachment = new Button(shell, SWT.NONE);
			GridData gd_btnAddAttachment = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
			gd_btnAddAttachment.widthHint = 58;
			btnAddAttachment.setLayoutData(gd_btnAddAttachment);
			btnAddAttachment.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseDown(MouseEvent e) {
					String attachmentLocation = "";
					FileDialog dialog = new FileDialog(shell, SWT.OPEN);
					dialog.setFilterExtensions(new String [] {"*.*"});
					dialog.setFilterPath("H:\\");
					attachmentLocation = dialog.open();
					if (attachmentLocation != null)	{
						File testFile = new File (attachmentLocation);
						if (testFile.exists()) {
							txtSystem.setText(txtSystem.getText() + "File: '" + attachmentLocation.substring(attachmentLocation.lastIndexOf("\\") + 1) + "' added successfully!" + dLine);
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
							txtSystem.setText(txtSystem.getText() + "Attachment not found!" + dLine);
						}
					}
				}
			});
			btnAddAttachment.setText("Add");
			btnAddAttachment.setEnabled(false);
			
			btnRemoveAttachment = new Button(shell, SWT.NONE);
			btnRemoveAttachment.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseDown(MouseEvent e) {
					if (attachNum > 0)	{
						txtSystem.setText(txtSystem.getText() + "File: '" + comboAttach.getText() + "' removed successfully!" + dLine);
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
						txtSystem.setText(txtSystem.getText() + "No attachment to remove!" + dLine);
					}
				}
			});
			GridData gd_btnRemoveAttachment = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
			gd_btnRemoveAttachment.widthHint = 58;
			btnRemoveAttachment.setLayoutData(gd_btnRemoveAttachment);
			btnRemoveAttachment.setText("Remove");
			btnRemoveAttachment.setEnabled(false);
			new Label(shell, SWT.NONE);
			new Label(shell, SWT.NONE);
			new Label(shell, SWT.NONE);
			new Label(shell, SWT.NONE);
			
			txtSubject = new Text(shell, SWT.BORDER);
			txtSubject.setText("[Replace with subject]");
			txtSubject.setFont(SWTResourceManager.getFont("PT Sans", 10, SWT.NORMAL));
			GridData gd_txtSubject = new GridData(SWT.FILL, SWT.CENTER, true, false, 5, 1);
			gd_txtSubject.widthHint = 40;
			txtSubject.setLayoutData(gd_txtSubject);
			txtSubject.setEnabled(false);
			
			txtSystem = new StyledText(shell, SWT.BORDER | SWT.READ_ONLY | SWT.WRAP | SWT.V_SCROLL);
			txtSystem.setRightMargin(2);
			txtSystem.setLeftMargin(2);
			txtSystem.setTopMargin(10);
			txtSystem.setForeground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
			txtSystem.setSelectionBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
			txtSystem.setSelectionForeground(SWTResourceManager.getColor(SWT.COLOR_BLACK));
			txtSystem.setText("Welcome to Ryan's MailMerger!\r\n\r\n1. Import your data using the 'Import' button.\r\n\r\n2. Add a subject and complete the body. \r\n\r\n3. Preview before sending!\r\n\r\n**'Dear [name]' is automatically added!**\r\n\r\n**'Student ID: [Student ID]' is automatically added!**\r\n\r\n**Your signature is automatically added!**\r\n\r\n");
			txtSystem.setDoubleClickEnabled(false);
			txtSystem.setEditable(false);
			txtSystem.setFont(SWTResourceManager.getFont("PT Sans", 10, SWT.NORMAL));
			txtSystem.setBackground(SWTResourceManager.getColor(SWT.COLOR_BLACK));
			GridData gd_txtSystem = new GridData(SWT.FILL, SWT.FILL, true, true, 7, 2);
			gd_txtSystem.widthHint = 132;
			txtSystem.setLayoutData(gd_txtSystem);
			txtSystem.addListener(SWT.Modify, new Listener(){
			    public void handleEvent(Event e){
			    	txtSystem.setTopIndex(txtSystem.getLineCount() - 1);
			    }
			});
			txtSystem.setEnabled(false);
			
			txtMain = new Text(shell, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
			txtMain.setFont(SWTResourceManager.getFont("PT Sans", 10, SWT.NORMAL));
			txtMain.setText("[Replace with body of email]");
			GridData gd_txtMain = new GridData(SWT.FILL, SWT.FILL, true, false, 5, 1);
			gd_txtMain.widthHint = 265;
			gd_txtMain.heightHint = 190;
			txtMain.setLayoutData(gd_txtMain);
			txtMain.setEnabled(false);
			
			btnImport = new Button(shell, SWT.NONE);
			GridData gd_btnImport = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
			gd_btnImport.widthHint = 57;
			btnImport.setLayoutData(gd_btnImport);
			btnImport.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseDown(MouseEvent e) {
					FileDialog dialog = new FileDialog(shell, SWT.OPEN);
					dialog.setFilterExtensions(new String [] {"*.xls*"});
					dialog.setFilterPath("H:\\");
					String fileLocation = dialog.open();
					if (fileLocation == null)	{
						txtSystem.setText(txtSystem.getText() + "Please select a file to import!" + dLine);
					}	else	{
						disableMain();
						startImport(fileLocation);	
					}
				}
			});
			btnImport.setText("Import");
			btnImport.setEnabled(false);
			
			btnPreview = new Button(shell, SWT.NONE);
			GridData gd_btnPreview = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
			gd_btnPreview.widthHint = 62;
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
			btnPreview.setEnabled(false);
			
			btnSend = new Button(shell, SWT.NONE);
			GridData gd_btnSend = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
			gd_btnSend.widthHint = 64;
			btnSend.setLayoutData(gd_btnSend);
			btnSend.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseDown(MouseEvent e) {
					if (importFinished)	{
						if (txtSubject.getText().trim().contains("[Replace with subject]") || txtSubject.getText().trim().equals("")) {
							txtSystem.setText(txtSystem.getText() + "Error: " + System.getProperty("line.separator") + "Please add a subject before sending to applicants!" + dLine);
						}	else if (txtMain.getText().trim().contains("[Replace with body of email]") || txtMain.getText().trim().equals(""))	{
							txtSystem.setText(txtSystem.getText() + "Error: " + System.getProperty("line.separator") + "Please change the body of the email before sending to applicants!" + dLine);
						}	else	{
							try {
								setEmailParas();
								Shell confirmShell = new Shell(shell, SWT.TITLE|SWT.SYSTEM_MODAL| SWT.CLOSE | SWT.MAX);
								Confirm confirm = new Confirm(confirmShell);
								confirm.open();
							} catch (Exception e1) {
								writeErrors(e1.toString());
							}
						}
					}	else	{
						txtSystem.setText(txtSystem.getText() + "Error: " + System.getProperty("line.separator") + "Please import applicants before tyring to send emails!" + dLine);
					}
				}
			});
			btnSend.setText("Send");
			btnSend.setEnabled(false);
			new Label(shell, SWT.NONE);
			new Label(shell, SWT.NONE);
			new Label(shell, SWT.NONE);
			new Label(shell, SWT.NONE);
			new Label(shell, SWT.NONE);
			new Label(shell, SWT.NONE);
			new Label(shell, SWT.NONE);
			new Label(shell, SWT.NONE);
			new Label(shell, SWT.NONE);
			
			addMainMenu(txtMain);
			addSubjectMenu(txtSubject);
			shell.open();
			shell.layout();
			if (credentialsAccepted == false) {
				Login.openLogin(shell);
			}	else	{
				enableMain();
			}
			while (!shell.isDisposed()) {
				if (!display.readAndDispatch()) {
					display.sleep();
				}
			}
	}
	public static void enableMain()	{
		btnNext.setEnabled(true);
		btnPrevious.setEnabled(true);
		txtSID.setEnabled(true);
		comboDropDownIS.setEnabled(true);
		txtEmail.setEnabled(true);
		comboAttach.setEnabled(true);
		txtSubject.setEnabled(true);
		txtSystem.setEnabled(true);
		txtMain.setEnabled(true);
		btnImport.setEnabled(true);
		btnPreview.setEnabled(true);
		btnSend.setEnabled(true);
		btnAddAttachment.setEnabled(true);
		btnRemoveAttachment.setEnabled(true);
	}
	public static void disableMain()	{
		btnNext.setEnabled(false);
		btnPrevious.setEnabled(false);
		txtSID.setEnabled(false);
		comboDropDownIS.setEnabled(false);
		txtEmail.setEnabled(false);
		comboAttach.setEnabled(false);
		txtSubject.setEnabled(false);
		txtSystem.setEnabled(false);
		txtMain.setEnabled(false);
		btnImport.setEnabled(false);
		btnPreview.setEnabled(false);
		btnSend.setEnabled(false);
		btnAddAttachment.setEnabled(false);
		btnRemoveAttachment.setEnabled(false);
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
						writeErrors(e1.toString());
					}
			}	else	{
				writeErrors(e.toString());
			}
		} catch (Exception e) {
			writeErrors(e.toString());
		}
		return accepted;
	}
	public static void setEmail(String emailToTest)	{
		loginEmail = emailToTest;
	}
	public static void setPW(String PWToTest)	{
		loginPassword = PWToTest;
	}
	private static void startImport(String fileLocation)	{
	    Thread importThread = new Thread(new Runnable() {
	        public void run() {
	        	runError = false;
	        	errorString = "";
	        	File xlFile = new File(fileLocation);
	        	contacts.importWorkbook(xlFile);
					}
	        	});
	    tempString = txtSystem.getText();
		txtSystem.setText("Importing..." + System.getProperty("line.separator"));
	    importThread.start();
	    refreshDisplay(refreshImport); 
	}
	public static void startSend()	{
		sent = false;
		Thread sendThread = new Thread(new Runnable() {
			public void run() {
				runError = false;
				errorString = "";
				sendAll();
				}
			});
		tempString = txtSystem.getText();
		txtSystem.setText("Sending emails..." + System.getProperty("line.separator"));
		sendThread.start();
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
				if (contacts.getImportSuccess()) {
					addMainMenu(txtMain);
					addSubjectMenu(txtSubject);
					enableMain();
					txtSystem.setText(tempString + contacts.getResults() + System.getProperty("line.separator"));
					MergeContact first = contacts.getSpecific(1);
					txtName.setText(first.getName());
					txtSID.setText(first.getID());
					txtEmail.setText(first.getEmail());
				}	else	{
					importFinished = false;
					enableMain();
					errorString = "A required column was not found: " + System.getProperty("line.separator");
					if (contacts.getEmailFound() == false)	{
						runError = true;
						errorString += "No email column detected. Please ensure your data contains an 'Email' column." + dLine;
					}
					if (contacts.getIdFound() == false)	{
						runError = true;
						errorString += "No Student ID column detected. Please ensure your data contains an 'Student' column." + dLine;
					}
					if (contacts.getNameFound() == false)	{
						runError = true;
						errorString += "No Name column detected. Please ensure your data contains an 'Forename' column." + dLine;
					}
				}
			}
			if (runError == true) {
				refreshService.shutdown();
				txtSystem.setText(tempString + errorString);
				runError = false;
				enableMain();
			}
		}
	};
	static Runnable refreshSend = new Runnable()	{
		@Override
		public void run() {
			MergeContact current = contacts.getSpecific(contacts.getCurrent());
			txtName.setText(current.getName());
			txtSID.setText(current.getID());
			txtEmail.setText(current.getEmail());
			txtSystem.setText("Sending to " + txtName.getText() + dLine + 
					"Student ID: " + txtSID.getText() + dLine + 
					"Email: " + txtEmail.getText());
			if (sent == true)	{
				enableMain();
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
			if (runError == true) {
				refreshService.shutdown();
				txtSystem.setText(tempString + errorString + dLine);
				runError = false;
				enableMain();
			}
		}
	};
	public static void setMergeList(String[] listToSet) {
		mergeList = listToSet;
	}
	public static void setError(String errorPara)	{
		runError = true;
		errorString = errorPara;
	}
	public static void setImportFinished(boolean importToSet)	{
		importFinished = importToSet;
	}
	private static void setEmailParas()	{
		userName = findName();
		inbox = comboDropDownIS.getText().trim() + loginEmail.substring(loginEmail.indexOf('@')).trim();
		emailBody = txtMain.getText().trim();
		subject = txtSubject.getText().trim();
		signature = "";
		if (!(StringUtils.endsWithIgnoreCase(emailBody, "regards") 
				|| StringUtils.endsWithIgnoreCase(emailBody, "regards,")
				|| StringUtils.endsWithIgnoreCase(emailBody, "wishes")
				|| StringUtils.endsWithIgnoreCase(emailBody, "wishes,")
				|| StringUtils.endsWithIgnoreCase(emailBody, "thanks")
				|| StringUtils.endsWithIgnoreCase(emailBody, "thanks,")))	{
			signature = "<br/><br/>Kind regards";
		}
		signature += ""; //Add html signature here
	}
	private static EmailMessage createEmail(int current) throws Exception	{
		String thisBody = emailBody, thisSubject = subject;
		for (int i = 0; i < mergeList.length; ++i) {
			try	{
				thisSubject = thisSubject.replaceAll(Pattern.quote("<<" + mergeList[i] + ">>"), contacts.getMainSheet().getRow(current).getCell(i).getStringCellValue());
				thisBody = thisBody.replaceAll(Pattern.quote("<<" + mergeList[i] + ">>"), contacts.getMainSheet().getRow(current).getCell(i).getStringCellValue());
			}	catch (IllegalStateException e)	{
				thisSubject = thisSubject.replaceAll(Pattern.quote("<<" + mergeList[i] + ">>"), String.valueOf(Math.round(contacts.getMainSheet().getRow(current).getCell(i).getNumericCellValue())));
				thisBody = thisBody.replaceAll(Pattern.quote("<<" + mergeList[i] + ">>"), String.valueOf(Math.round(contacts.getMainSheet().getRow(current).getCell(i).getNumericCellValue())));
			}	catch (NullPointerException n)	{
				thisSubject = thisSubject.replaceAll(Pattern.quote("<<" + mergeList[i] + ">>"), "");
				thisBody = thisBody.replaceAll(Pattern.quote("<<" + mergeList[i] + ">>"), "");
			}	catch (Exception lastResort)	{
				throw new Exception();
			}
		}
		EmailMessage msg = new EmailMessage(service);
		MergeContact currentContact = contacts.getSpecific(current);
		msg.setBody(MessageBody.getMessageBodyFromText("<div style='font-family:PT Sans;font-size:13'>" + 
				"Dear " + currentContact.getName() + ",<br/><br/>" + 
				thisBody.replaceAll(System.getProperty("line.separator"), "<br/>") + signature));
		for (String eachString: attachList)	{
			if (eachString != null)	{
				msg.getAttachments().addFileAttachment(eachString);
			}
		}
		if (thisSubject.endsWith("."))	{
			msg.setSubject(thisSubject + " Student ID: " + currentContact.getID());
		}	else	{
			msg.setSubject(thisSubject + ". Student ID: " + currentContact.getID());
		}
		msg.getToRecipients().add(currentContact.getEmail());
		msg.setFrom(new EmailAddress(inbox));
		return msg;
	}
	private static void writeErrors(String error)	{
		try {
			PrintWriter errorOutput = new PrintWriter(new FileWriter("Errors.txt", true));
			errorOutput.print(error);
			errorOutput.close();
		}	catch (Exception e) {
			e.printStackTrace();
		}
	}
	private static void sendAll()	{
		try	{
			int total = contacts.getTotal();
			errorString = "";
			for (int i = 1; i < total; ++i)	{
				contacts.setCurrent(i);
				MergeContact currentContact = contacts.getSpecific(i);
				EmailMessage message = createEmail(i);
				if (!(currentContact.getEmail().equals("") && currentContact.getName().equals("") && currentContact.getID().equals(""))) {
					Mailbox sentBox = new Mailbox();
					sentBox.setAddress(inbox);
					FolderId sentBoxSentItems = new FolderId(WellKnownFolderName.SentItems, sentBox);
					FolderId sentBoxDrafts = new FolderId(WellKnownFolderName.Drafts, sentBox);
					message.save(sentBoxDrafts);
					message.sendAndSaveCopy(sentBoxSentItems);
				}	else if(currentContact.getEmail().equals(""))	{
					errorString += ("Error: " + currentContact.getName() + ": " + currentContact.getID() + "  not emailed. Email address missing!") + dLine;
					} else if (currentContact.getName().equals(""))	{
						errorString += ("Error: " + currentContact.getEmail() + ": " + currentContact.getID() + " not emailed. Name is missing!")  + dLine;
					} else if (currentContact.getID().equals(""))	{
						errorString += ("Error: " + currentContact.getName() + ": " + currentContact.getEmail() + " not emailed. Student ID is missing!" + dLine);
					}
				}
			sent = true;
			}	catch(ServiceResponseException | ServiceRequestException | FileNotFoundException e)	{
				runError = true;
				if (StringUtils.containsIgnoreCase(e.toString(), "The system cannot find the file specified"))	{
					errorString = ("Fatal send error: program cannot find attachment!");
				}	else	{
					errorString = ("Fatal send error: You do not have access to the inbox you are trying to send from!");
				}
			}	catch (Exception e1)	{
				runError = true;
				errorString = ("Fatal send error: " + e1.toString());
			}
	}
	private static void preview()	{
		try {
			EmailMessage preview = createEmail(contacts.getCurrent());
			File output = new File(".\\preview.eml");
			FileOutputStream os = new FileOutputStream(output);
			BufferedOutputStream bos = new BufferedOutputStream(os);
			Properties props = new Properties();
			Session session = Session.getDefaultInstance(props);
			MimeMessage message = new MimeMessage(session);
			message.setSubject(preview.getSubject(), "text/html; charset=UTF-8");
			message.setRecipients(Message.RecipientType.TO,
		            InternetAddress.parse(txtEmail.getText()));
			if (attachNum > 0)	{
				BodyPart mainBody = new MimeBodyPart();
				mainBody.setContent(preview.getBody().toString(), "text/html; charset=UTF-8");
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
				message.setContent(preview.getBody().toString(), "text/html; charset=UTF-8");
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
			txtSystem.setText(txtSystem.getText() + "Unable to preview: Attachment '" + comboAttach.getText() + "' not found." + dLine);
		}	catch (Exception e) {
			enableMain();
			txtSystem.setText(txtSystem.getText() + "Fatal Preview error: " + e.toString());
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
		int caretPosition = text.getCaretPosition();
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
		int caretPosition = text.getCaretPosition();
		int selectedChars = text.getSelectionCount();
		switch (tagToAdd)	{
		case "Bold": toInsert = "<b>"; toEnd = "</b>";
		break;
		case "Italics": toInsert = "<i>"; toEnd = "</i>";
		break;
		case "Strikethrough": toInsert = "<s>"; toEnd = "</s>";
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
		int caretPosition = text.getCaretPosition();
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
	    		"Bold", "Italics", "Strikethrough", "Red", "Blue", "Green", "Custom Colour"
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