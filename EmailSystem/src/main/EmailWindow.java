package main;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.wb.swt.SWTResourceManager;

import encrypter.Encrypter;
import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.enumeration.misc.ExchangeVersion;
import microsoft.exchange.webservices.data.core.enumeration.property.WellKnownFolderName;
import microsoft.exchange.webservices.data.core.service.item.EmailMessage;
import microsoft.exchange.webservices.data.core.service.schema.FolderSchema;
import microsoft.exchange.webservices.data.credential.ExchangeCredentials;
import microsoft.exchange.webservices.data.credential.WebCredentials;
import microsoft.exchange.webservices.data.property.complex.EmailAddress;
import microsoft.exchange.webservices.data.property.complex.MessageBody;
import microsoft.exchange.webservices.data.search.FindFoldersResults;
import microsoft.exchange.webservices.data.search.FolderView;
import microsoft.exchange.webservices.data.search.filter.SearchFilter;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Layout;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;

import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.custom.StyledText;

public class EmailWindow {
	private static Text txtMain;
	private static Text txtSubject;
	private static Button btnPreview;
	private static Button btnSend;
	private static Button btnPrevious;
	private static Button btnNext;
	private static Label lblDear;
	private static Label lblStudentId;
	private static Text txtName;
	private static Text txtSID;
	private static Label lblEmail;
	private static Text txtEmail;
	private static Label lblInbox;
	private static Button btnImport;
	private static ExchangeService service = new ExchangeService(ExchangeVersion.Exchange2010_SP2);
	private static String loginEmail = "";
	private static String loginPassword = "";
	private static Label lblSubject;
	private static Label lblYourName;
	private static Text txtUser;
	public static StyledText txtSystem;
	private static Button btnAddAttachment;
	private static Label lblAttachment;
	private static Text txtAttachment;
	private static Combo comboDropDownIS;
	private static ContactList contacts = new ContactList();
	static boolean importFinished = false, sent = false, attachmentAdded = false, outofMemory = false, credentialsAccepted = false;
	private static ScheduledExecutorService refreshService;
	static String inbox = "", userName = "", emailBody = "", subject = "", logString = "", attachmentLocation = "";
	static String errorString = "";
	static Logger logger = Logger.getLogger(EmailWindow.class);
	static Layout logLayout = new PatternLayout();
	
	

	/**
	 * Launch the application.
	 * @param args
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException {
		BasicConfigurator.configure();
		Display display = Display.getDefault();
		try {
			service.setUrl(new URI("")); // URI needs to be set for institution.
			} catch (URISyntaxException e2) {
				e2.printStackTrace();
				}
			File credentialFile = new File("Credentials.txt");
			if (credentialFile.exists())	{
				Encrypter decrypt = new Encrypter(1234); //Set a pin
				Scanner credentialScanner = new Scanner(credentialFile);
				loginEmail = decrypt.decrypt(credentialScanner.nextLine());
				loginPassword = decrypt.decrypt(credentialScanner.nextLine());
				credentialScanner.close();
				if (credCheck())	{
					credentialsAccepted = true;
				}
			}
			Shell shell = new Shell();
			shell.setImage(SWTResourceManager.getImage(EmailWindow.class, "/resources/LogoBasic.png"));
			shell.setBackground(SWTResourceManager.getColor(SWT.COLOR_LIST_BACKGROUND));
			shell.setSize(650, 388);
			shell.setText("SWT Application");
			shell.setLayout(new GridLayout(11, false));
			
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
						Contact previous = contacts.getPrevious();
						txtName.setText(previous.getName());
						txtSID.setText(previous.getID());
						txtEmail.setText(previous.getEmail());
					}	else	{
						txtSystem.setText("Error: " + System.getProperty("line.separator") + "Please import applicants first!");
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
						setNext();
					}	else	{
						txtSystem.setText("Error: " + System.getProperty("line.separator") + "Please import applicants first!");
					}
				}
			});
			btnNext.setText(">");
			btnNext.setEnabled(false);
			new Label(shell, SWT.NONE);
			
			lblYourName = new Label(shell, SWT.NONE);
			lblYourName.setFont(SWTResourceManager.getFont("PT Sans", 10, SWT.NORMAL));
			lblYourName.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
			lblYourName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
			lblYourName.setText("Your Name:");
			
			txtUser = new Text(shell, SWT.BORDER);
			txtUser.setFont(SWTResourceManager.getFont("PT Sans", 10, SWT.NORMAL));
			GridData gd_txtUser = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
			gd_txtUser.widthHint = 74;
			txtUser.setLayoutData(gd_txtUser);
			txtUser.setEnabled(false);
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
			new Label(shell, SWT.NONE);
			
			lblInbox = new Label(shell, SWT.NONE);
			lblInbox.setFont(SWTResourceManager.getFont("PT Sans", 10, SWT.NORMAL));
			lblInbox.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
			lblInbox.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
			lblInbox.setText("Inbox");
			
			comboDropDownIS = new Combo(shell, SWT.DROP_DOWN | SWT.BORDER);
			comboDropDownIS.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
			comboDropDownIS.add("ukadmissions");
			comboDropDownIS.add("interviews");
			comboDropDownIS.setText("ukadmissions");
			comboDropDownIS.setEnabled(false);
			new Label(shell, SWT.NONE);
			new Label(shell, SWT.NONE);
			
			lblEmail = new Label(shell, SWT.NONE);
			lblEmail.setFont(SWTResourceManager.getFont("PT Sans", 10, SWT.NORMAL));
			lblEmail.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
			lblEmail.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
			lblEmail.setText("Email: ");
			
			txtEmail = new Text(shell, SWT.BORDER | SWT.READ_ONLY);
			txtEmail.setFont(SWTResourceManager.getFont("PT Sans", 10, SWT.NORMAL));
			GridData gd_txtEmail = new GridData(SWT.FILL, SWT.CENTER, true, false, 5, 1);
			gd_txtEmail.widthHint = 34;
			txtEmail.setLayoutData(gd_txtEmail);
			txtEmail.setEnabled(false);
			
			lblAttachment = new Label(shell, SWT.NONE);
			lblAttachment.setFont(SWTResourceManager.getFont("PT Sans", 10, SWT.NORMAL));
			lblAttachment.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
			lblAttachment.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
			lblAttachment.setText("Attachment");
			
			txtAttachment = new Text(shell, SWT.BORDER);
			txtAttachment.setFont(SWTResourceManager.getFont("PT Sans", 10, SWT.NORMAL));
			txtAttachment.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
			txtAttachment.setEnabled(false);
			new Label(shell, SWT.NONE);
			new Label(shell, SWT.NONE);
			
			lblSubject = new Label(shell, SWT.NONE);
			lblSubject.setFont(SWTResourceManager.getFont("PT Sans", 10, SWT.NORMAL));
			lblSubject.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
			lblSubject.setText("Subject");
			
			txtSubject = new Text(shell, SWT.BORDER);
			txtSubject.setFont(SWTResourceManager.getFont("PT Sans", 10, SWT.NORMAL));
			GridData gd_txtSubject = new GridData(SWT.FILL, SWT.CENTER, true, false, 7, 1);
			gd_txtSubject.widthHint = 40;
			txtSubject.setLayoutData(gd_txtSubject);
			txtSubject.setEnabled(false);
			
			txtSystem = new StyledText(shell, SWT.BORDER | SWT.READ_ONLY | SWT.WRAP | SWT.V_SCROLL);
			txtSystem.setTopMargin(10);
			txtSystem.setAlignment(SWT.CENTER);
			txtSystem.setForeground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
			txtSystem.setSelectionBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
			txtSystem.setSelectionForeground(SWTResourceManager.getColor(SWT.COLOR_BLACK));
			txtSystem.setText("Hello!\r\n\r\nWelcome to Ryan's MailMerger!\r\n\r\nPlease import your data using the 'Import' button, and selecting an excel spreadsheet. \r\n\r\nRemember to complete your name for the signature!");
			txtSystem.setDoubleClickEnabled(false);
			txtSystem.setEditable(false);
			txtSystem.setFont(SWTResourceManager.getFont("PT Sans", 10, SWT.NORMAL));
			txtSystem.setBackground(SWTResourceManager.getColor(SWT.COLOR_BLACK));
			GridData gd_txtSystem = new GridData(SWT.FILL, SWT.FILL, true, true, 3, 2);
			gd_txtSystem.widthHint = 132;
			txtSystem.setLayoutData(gd_txtSystem);
			txtSystem.addListener(SWT.Modify, new Listener(){
			    public void handleEvent(Event e){
			    	txtSystem.setTopIndex(txtSystem.getLineCount() - 1);
			    }
			});
			txtSystem.setEnabled(false);
			
			contacts.initialise(txtSystem);
			
			txtMain = new Text(shell, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
			txtMain.setFont(SWTResourceManager.getFont("PT Sans", 10, SWT.NORMAL));
			txtMain.setText("Body of email.\r\n\r\nKind regards");
			GridData gd_txtMain = new GridData(SWT.FILL, SWT.FILL, true, false, 8, 1);
			gd_txtMain.widthHint = 41;
			gd_txtMain.heightHint = 190;
			txtMain.setLayoutData(gd_txtMain);
			txtMain.setEnabled(false);
			
			btnImport = new Button(shell, SWT.NONE);
			GridData gd_btnImport = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
			gd_btnImport.widthHint = 57;
			btnImport.setLayoutData(gd_btnImport);
			btnImport.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseDown(MouseEvent e) {
					if (importFinished == false)	{
						FileDialog dialog = new FileDialog(shell, SWT.OPEN);
						dialog.setFilterExtensions(new String [] {"*.xlsx"});
						dialog.setFilterPath("H:\\");
						String fileLocation = dialog.open();
						if (fileLocation == null)	{
							txtSystem.setText("Please select a file to import!");
						}	else	{
							disableMain();
							txtSystem.setText("Import started!");
							try	{
								startImport(fileLocation);	
							} catch (OutOfMemoryError ofm)	{
								outofMemory = true;
								errorString = ("Java ran out of memory: " + ofm + System.getProperty("line.separator"));
							}
						}
					}
				}
			});
			btnImport.setText("Import");
			btnImport.setEnabled(false);
			
			btnPreview = new Button(shell, SWT.NONE);
			GridData gd_btnPreview = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
			gd_btnPreview.widthHint = 59;
			btnPreview.setLayoutData(gd_btnPreview);
			btnPreview.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseDown(MouseEvent e) {
					setEmailParas();
					preview();
				}
			});
			btnPreview.setText("Preview");
			btnPreview.setEnabled(false);
			
			btnSend = new Button(shell, SWT.NONE);
			GridData gd_btnSend = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
			gd_btnSend.widthHint = 52;
			btnSend.setLayoutData(gd_btnSend);
			btnSend.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseDown(MouseEvent e) {
					if (importFinished)	{
						if (txtUser.getText().trim().equals(""))	{
							txtSystem.setText("Error: " + System.getProperty("line.separator") + "Please type your name before sending to applicants!" + System.getProperty("line.separator") + System.getProperty("line.separator"));
						}	else if (txtSubject.getText().trim().equals("")) {
							txtSystem.setText("Error: " + System.getProperty("line.separator") + "Please add a subject before sending to applicants!" + System.getProperty("line.separator") + System.getProperty("line.separator"));
						}	else if (txtMain.getText().trim().equals("Body of email.\r\n\r\nKind regards"))	{
							txtSystem.setText("Error: " + System.getProperty("line.separator") + "Please change the body of the email before sending to applicants!" + System.getProperty("line.separator") + System.getProperty("line.separator"));
						}	else	{
							txtSystem.setText("Sending emails...");
							try {
								setEmailParas();
								Shell confirmShell = new Shell(shell, SWT.TITLE|SWT.SYSTEM_MODAL| SWT.CLOSE | SWT.MAX);
								Confirm confirm = new Confirm(confirmShell);
								confirm.open();
							} catch (Exception e1) {
								e1.printStackTrace();
							}
						}
					}	else	{
						txtSystem.setText("Error: " + System.getProperty("line.separator") + "Please import applicants before tyring to send emails!");
					}
				}
			});
			btnSend.setText("Send");
			btnSend.setEnabled(false);
			
			btnAddAttachment = new Button(shell, SWT.NONE);
			btnAddAttachment.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseDown(MouseEvent e) {
					attachmentLocation = "";
					attachmentAdded = false;
					FileDialog dialog = new FileDialog(shell, SWT.OPEN);
					dialog.setFilterExtensions(new String [] {"*.*"});
					dialog.setFilterPath("H:\\");
					attachmentLocation = dialog.open();
					if (!(attachmentLocation.equals("")))	{
						txtSystem.setText("File: " + attachmentLocation.substring(attachmentLocation.lastIndexOf("\\") + 1) + " added successfully!");
						txtAttachment.setText(attachmentLocation.substring(attachmentLocation.lastIndexOf("\\") + 1));
						attachmentAdded = true;
					}
				}
			});
			btnAddAttachment.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 4, 1));
			btnAddAttachment.setText("Add Attachment");
			btnAddAttachment.setEnabled(false);
			new Label(shell, SWT.NONE);
			new Label(shell, SWT.NONE);
			new Label(shell, SWT.NONE);
			new Label(shell, SWT.NONE);
			
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
		txtUser.setEnabled(true);
		txtSID.setEnabled(true);
		comboDropDownIS.setEnabled(true);
		txtEmail.setEnabled(true);
		txtAttachment.setEnabled(true);
		txtSubject.setEnabled(true);
		txtSystem.setEnabled(true);
		txtMain.setEnabled(true);
		btnImport.setEnabled(true);
		btnPreview.setEnabled(true);
		btnSend.setEnabled(true);
		btnAddAttachment.setEnabled(true);
	}
	public static void disableMain()	{
		btnNext.setEnabled(false);
		btnPrevious.setEnabled(false);
		txtUser.setEnabled(false);
		txtSID.setEnabled(false);
		comboDropDownIS.setEnabled(false);
		txtEmail.setEnabled(false);
		txtAttachment.setEnabled(false);
		txtSubject.setEnabled(false);
		txtSystem.setEnabled(false);
		txtMain.setEnabled(false);
		btnImport.setEnabled(false);
		btnPreview.setEnabled(false);
		btnSend.setEnabled(false);
		btnAddAttachment.setEnabled(false);
	}
	public static boolean credCheck()	{
		boolean accepted = false;
		ExchangeCredentials credentials = new WebCredentials(loginEmail, loginPassword);
		service.setCredentials(credentials);
		try {
			FindFoldersResults credentialCheck = service.findFolders(WellKnownFolderName.Root, new SearchFilter.IsGreaterThan(FolderSchema.TotalCount, 0), new FolderView(10));
			if (credentialCheck.getTotalCount() > 0) {
				accepted = true;
			}
		} catch (Exception e1) {
			e1.printStackTrace();
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
						try {
							File xlFile = new File(fileLocation);
							Workbook excelBook = new XSSFWorkbook(xlFile);
							contacts.importAll(excelBook);
						} catch (IOException e1) {
							e1.printStackTrace();
						} catch (OutOfMemoryError e2)	{
							outofMemory = true;
							errorString = ("Java ran out of memory: " + e2 + System.getProperty("line.separator"));
						} catch (InvalidFormatException e) {
							errorString = ("The workbook is not in the correct format: " + e);
						}
					}
	        	});
		txtSystem.setText("Importing..." + System.getProperty("line.separator"));
	    importThread.start();
	    display(importRun);
	}
	static void startSend()	{
		Thread sendThread = new Thread(new Runnable() {
			public void run() {
				try {
					sendAll();
					} catch (Exception e1) {
						e1.printStackTrace();
						}
				}
			});
		sendThread.start();
		display(sendRun);
		}
	private static void display(Runnable toRefresh)	{
	    Runnable runRefresh = new Runnable() {
	        public void run() {
	        	Display.getDefault().asyncExec(toRefresh);
	        }
	    };
	    refreshService = Executors.newSingleThreadScheduledExecutor();
	    refreshService.scheduleAtFixedRate(runRefresh, 0, 33, TimeUnit.MILLISECONDS);
	}
	static Runnable importRun = new Runnable()	{
		@Override
		public void run() {
			txtSystem.setText(txtSystem.getText() + " ...");
			if (importFinished == true)	{
				refreshService.shutdown();
				if (contacts.importSuccess) {
					enableMain();
					txtSystem.setText(contacts.getResults());
					setFirst();
				}	else	{
					importFinished = false;
					String errorString = "A required column was not found: " + System.getProperty("line.separator");
					if (contacts.emailFound == false)	{
						errorString += "No email column detected. Please ensure your data contains an 'Email' column." + System.getProperty("line.separator");
					}
					if (contacts.idFound == false)	{
						errorString += "No Student ID column detected. Please ensure your data contains an 'Student' column." + System.getProperty("line.separator");
					}
					if (contacts.nameFound == false)	{
						errorString += "No Name column detected. Please ensure your data contains an 'Forename' column.";
					}
					txtSystem.setText(errorString);
				}
			}
			if (outofMemory == true) {
				refreshService.shutdown();
				txtSystem.setText(errorString);
				outofMemory = false;
			}
		}
	};
	static Runnable sendRun = new Runnable()	{
		@Override
		public void run() {
			setNext();
			if (sent == true)	{
				enableMain();
				refreshService.shutdown();
				if (errorString.equals(""))	{
					txtSystem.setText("All emails sent without error!");
				}	else	{
					txtSystem.setText("Emails sent with errors: " + System.getProperty("line.separator") + errorString);
					try {
						PrintWriter errorOutput = new PrintWriter("Errors.txt");
						errorOutput.print(errorString);
						errorOutput.close();
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
				}
			}
			if (outofMemory == true) {
				refreshService.shutdown();
				txtSystem.setText(errorString);
				outofMemory = false;
			}
		}
	};
	private static void setFirst()	{
		Contact first = contacts.getFirst();
		txtName.setText(first.getName());
		txtSID.setText(first.getID());
		txtEmail.setText(first.getEmail());
	}
	private static void setNext()	{
		Contact next = contacts.getNext();
		txtName.setText(next.getName());
		txtSID.setText(next.getID());
		txtEmail.setText(next.getEmail());
	}
	private static void setEmailParas()	{
		userName = txtUser.getText();
		inbox = comboDropDownIS.getText() + loginEmail.substring(loginEmail.indexOf('@'));
		emailBody = txtMain.getText();
		subject = txtSubject.getText();
	}
	
	public static EmailMessage createEmail(int current) throws Exception	{
		String signature = ""; // Set your institutions signature using HTML.
		EmailMessage msg = new EmailMessage(service);
		msg.setBody(MessageBody.getMessageBodyFromText("<div style='font-family:PT Sans;font-size:13'>" + 
		"Dear " + contacts.getSpecific(current).getName() + ",<br/><br/>" + 
				emailBody.replaceAll(System.getProperty("line.separator"), "<br/>") + signature));
		if (attachmentAdded == true)	{
			msg.getAttachments().addFileAttachment(attachmentLocation);
		}
		msg.setSubject(subject + " Student ID: " + contacts.getSpecific(current).getID());
		msg.getToRecipients().add(contacts.getSpecific(current).getEmail());
		msg.getBccRecipients().add(inbox);
		msg.setFrom(new EmailAddress(inbox));
		return msg;
	}
	
	public static void sendAll() throws Exception	{
		int total = contacts.getTotal();
		errorString = "";
		for (int i = 0; i < total; ++i)	{
			contacts.setCurrent(i);
			EmailMessage message = createEmail(i);
			if (contacts.getSpecific(i).getEmail().equals("noemail"))	{
				errorString += ("Error: " + contacts.getSpecific(i).getName() + " not emailed. Email address missing!") + System.getProperty("line.separator");
				} else if (contacts.getSpecific(i).getName().equals("Name not found!"))	{
					errorString += ("Error: " + contacts.getSpecific(i).getEmail() + " not emailed. Name is missing!")  + System.getProperty("line.separator");
				} else if (contacts.getSpecific(i).getID().equals("studentIDMissing"))	{
					errorString += ("Error: " + contacts.getSpecific(i).getEmail() + " not emailed. Student ID is missing!" + System.getProperty("line.separator"));
				}	else	{
					try	{
						message.send();
						}	catch (Exception e)	{
							errorString += ("Exception (autoservice started): " + e);
							service.autodiscoverUrl(loginEmail);
							message.send();
							}
					}
			}
		sent = true;
		}
	public static void preview()	{
		try {
			EmailMessage preview = createEmail(contacts.getCurrent());
			File output = new File(".\\preview.eml");
			Session session = null;
			Properties props = new Properties();
			session = Session.getDefaultInstance(props);
			MimeMessage message = new MimeMessage(session);
			message.setContent(preview.getBody().toString(), "text/html");
			message.setSubject(preview.getSubject());
			FileOutputStream os = new FileOutputStream(output);
			message.writeTo(os);
			os.close();
            Runtime.getRuntime().exec(new String[]
            {"rundll32", "url.dll,FileProtocolHandler",
            	output.getAbsolutePath()});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}