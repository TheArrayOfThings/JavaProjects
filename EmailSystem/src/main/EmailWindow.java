package main;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.wb.swt.SWTResourceManager;
import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.enumeration.misc.ExchangeVersion;
import microsoft.exchange.webservices.data.core.service.item.EmailMessage;
import microsoft.exchange.webservices.data.credential.ExchangeCredentials;
import microsoft.exchange.webservices.data.credential.WebCredentials;
import microsoft.exchange.webservices.data.property.complex.EmailAddress;
import microsoft.exchange.webservices.data.property.complex.MessageBody;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Layout;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.WriterAppender;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;
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
	private static Text txtInbox;
	private static Button btnImport;
	private static ExchangeService service = new ExchangeService(ExchangeVersion.Exchange2010_SP2);
	private static String loginEmail = "";
	private static String loginPassword = "";
	private static Label lblSubject;
	private static Label lblYourName;
	private static Text txtUser;
	private static StyledText txtSystem;
	private static ContactList contacts = new ContactList();
	static boolean importFinished = false, sent = false, attachmentAdded = false, outofMemory = false;
	private static ScheduledExecutorService refreshService;
	static String inbox = "", userName = "", emailBody = "", subject = "", logString = "", attachmentLocation = "";
	static String errorString = "";
	
	

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		Display display = Display.getDefault();
		ExchangeCredentials credentials = new WebCredentials(loginEmail, loginPassword);
		service.setCredentials(credentials);
		try {
			service.setUrl(new URI(""));
			} catch (URISyntaxException e2) {
				e2.printStackTrace();
				}
		
		Shell shell = new Shell();
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
		
		btnPrevious = new Button(shell, SWT.NONE);
		btnPrevious.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				if (importFinished) {
					if (contacts.emailFound == false)	{
						txtSystem.setText("Import completed, no Email column found!" + System.getProperty("line.separator") + 
								"Please ensure your data contains a column called 'Email'.");
						importFinished = false;
					}
					if (contacts.idFound == false)	{
						txtSystem.setText("Import completed, no Student ID column found!" + System.getProperty("line.separator") + 
								"Please ensure your data contains a column called 'Student'.");
						importFinished = false;
					}
					if (contacts.nameFound == false)	{
						txtSystem.setText("Import completed, no Name column found!" + System.getProperty("line.separator") + 
								"Please ensure your data contains a column called 'Name'.");
						importFinished = false;
					}
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
		new Label(shell, SWT.NONE);
		
		lblYourName = new Label(shell, SWT.NONE);
		lblYourName.setFont(SWTResourceManager.getFont("PT Sans", 10, SWT.NORMAL));
		lblYourName.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblYourName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblYourName.setText("Your Name:");
		
		txtUser = new Text(shell, SWT.BORDER);
		txtUser.setFont(SWTResourceManager.getFont("PT Sans", 10, SWT.NORMAL));
		txtUser.setText("Ryan Flanagan");
		GridData gd_txtUser = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
		gd_txtUser.widthHint = 74;
		txtUser.setLayoutData(gd_txtUser);
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
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);
		
		lblInbox = new Label(shell, SWT.NONE);
		lblInbox.setFont(SWTResourceManager.getFont("PT Sans", 10, SWT.NORMAL));
		lblInbox.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblInbox.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		lblInbox.setText("Inbox");
		
		txtInbox = new Text(shell, SWT.BORDER);
		txtInbox.setFont(SWTResourceManager.getFont("PT Sans", 10, SWT.NORMAL));
		txtInbox.setText("");
		GridData gd_txtInbox = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
		gd_txtInbox.widthHint = 76;
		txtInbox.setLayoutData(gd_txtInbox);
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
		
		lblAttachment = new Label(shell, SWT.NONE);
		lblAttachment.setFont(SWTResourceManager.getFont("PT Sans", 10, SWT.NORMAL));
		lblAttachment.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblAttachment.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblAttachment.setText("Attachment");
		
		txtAttachment = new Text(shell, SWT.BORDER);
		txtAttachment.setFont(SWTResourceManager.getFont("PT Sans", 10, SWT.NORMAL));
		txtAttachment.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);
		
		lblSubject = new Label(shell, SWT.NONE);
		lblSubject.setFont(SWTResourceManager.getFont("PT Sans", 10, SWT.NORMAL));
		lblSubject.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblSubject.setText("Subject");
		
		txtSubject = new Text(shell, SWT.BORDER);
		txtSubject.setFont(SWTResourceManager.getFont("PT Sans", 10, SWT.NORMAL));
		txtSubject.setText("BU: Something Something");
		GridData gd_txtSubject = new GridData(SWT.FILL, SWT.CENTER, true, false, 7, 1);
		gd_txtSubject.widthHint = 40;
		txtSubject.setLayoutData(gd_txtSubject);
		
		txtSystem = new StyledText(shell, SWT.BORDER | SWT.READ_ONLY | SWT.WRAP | SWT.V_SCROLL);
		txtSystem.setTopMargin(10);
		txtSystem.setAlignment(SWT.CENTER);
		txtSystem.setForeground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		txtSystem.setSelectionBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		txtSystem.setSelectionForeground(SWTResourceManager.getColor(SWT.COLOR_BLACK));
		txtSystem.setText("Hello!\r\n\r\nWelcome to Ryan's MailMerger!\r\n\r\nInsert instructions here!");
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
		
		contacts.initialise(txtSystem);
		
		txtMain = new Text(shell, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		txtMain.setFont(SWTResourceManager.getFont("PT Sans", 10, SWT.NORMAL));
		txtMain.setText("Body of email.\r\n\r\nMultiple lines for testing.\r\n\r\nKind regards");
		GridData gd_txtMain = new GridData(SWT.FILL, SWT.FILL, true, false, 8, 1);
		gd_txtMain.widthHint = 41;
		gd_txtMain.heightHint = 190;
		txtMain.setLayoutData(gd_txtMain);
		
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
		
		btnSend = new Button(shell, SWT.NONE);
		GridData gd_btnSend = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_btnSend.widthHint = 52;
		btnSend.setLayoutData(gd_btnSend);
		btnSend.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				if (importFinished)	{
					try {
						setEmailParas();
						startSend();
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}	else	{
					txtSystem.setText("Error: " + System.getProperty("line.separator") + "Please import applicants before tyring to send emails!");
				}
			}
		});
		btnSend.setText("Send");
		
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
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);

		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
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
	private static void startSend()	{
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
			txtSystem.setText(logString);
			if (sent == true)	{
				refreshService.shutdown();
			}
			if (outofMemory == true) {
				refreshService.shutdown();
				txtSystem.setText(errorString);
				outofMemory = false;
			}
		}
	};
	private static Button btnAddAttachment;
	private static Label lblAttachment;
	private static Text txtAttachment;
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
		inbox = txtInbox.getText() + "@";
		emailBody = txtMain.getText();
		subject = txtSubject.getText();
	}
	
	public static EmailMessage createEmail(int current) throws Exception	{
		String signature = "";
		EmailMessage msg = new EmailMessage(service);
		msg.setBody(MessageBody.getMessageBodyFromText("<div style='font-family:PT Sans;font-size:13'>" + 
		"Dear " + contacts.getSpecific(current).getName() + ",<br/><br/>" + 
				emailBody.replaceAll(System.getProperty("line.separator"), "<br/>") + signature));
		if (attachmentAdded == true)	{
			msg.getAttachments().addFileAttachment(attachmentLocation);
		}
		msg.setSubject(subject + " Student ID: " + contacts.getSpecific(current).getID());
		msg.getToRecipients().add(contacts.getSpecific(current).getEmail());
		//msg.getBccRecipients().add(inbox);
		msg.setFrom(new EmailAddress(inbox));
		return msg;
	}
	
	public static void sendAll() throws Exception	{
		int total = contacts.getTotal();
		for (int i = 0; i < total; ++i)	{
			contacts.setCurrent(i);
			EmailMessage message = createEmail(i);
			Logger logger = Logger.getLogger(EmailWindow.class);
			StringWriter logWriter = new StringWriter();
			Layout logLayout = new PatternLayout();
			WriterAppender logAppender = new WriterAppender(logLayout, logWriter);
			logger.addAppender(logAppender);
			BasicConfigurator.configure();
			if (contacts.getSpecific(i).getEmail().equals("noemail"))	{
				logger.info("Error: " + contacts.getSpecific(i).getName() + " not emailed. Email address missing!");
				} else if (contacts.getSpecific(i).getName().equals("Name not found!"))	{
					logger.info("Error: " + contacts.getSpecific(i).getEmail() + " not emailed. Name is missing!");
				} else if (contacts.getSpecific(i).getID().equals("studentIDMissing"))	{
					logger.info("Error: " + contacts.getSpecific(i).getEmail() + " not emailed. Student ID is missing!");
				}	else	{
					try	{
						message.send();
						}	catch (Exception e)	{
							logger.info("Exception (autoservice started): " + e);
							service.autodiscoverUrl(loginEmail);
							message.send();
							}
					}
			logString += logWriter.toString();
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

