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
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
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
import java.io.IOException;
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
import org.eclipse.swt.custom.StyledText;

public class EmailWindow {
	private static Text txtMain, txtSubject, txtName, txtSID, txtEmail, txtAttachment;
	private static Button btnPreview, btnSend, btnPrevious, btnNext, btnImport, btnAddAttachment, btnRemoveAttachment;;
	private static Label lblDear, lblStudentId, lblEmail, lblInbox, lblAttachment;
	private static StyledText txtSystem;
	private static ExchangeService service = new ExchangeService(ExchangeVersion.Exchange2010_SP2);
	private static String loginEmail = "";
	private static String loginPassword = "";
	private static Combo comboDropDownIS;
	private static ContactList contacts = new ContactList();
	private static boolean sent = false, attachmentAdded = false, runError = false, credentialsAccepted = false;
	private static boolean importFinished = false;
	private static ScheduledExecutorService refreshService;
	private static String inbox = "", userName = "", emailBody = "", subject = "", attachmentLocation = "", errorString = "", tempString = "";
	private static String dLine = System.getProperty("line.separator") + System.getProperty("line.separator");
	private static String[] mergeList = new String[]{"Empty"};

	public static void main(String[] args) throws FileNotFoundException {
		Display display = Display.getDefault();
		try {
			File url = new File(".\\URL.txt");
			if (url.exists())	{
				Scanner urlScanner = new Scanner(url);
				service.setUrl(new URI(urlScanner.nextLine()));
				urlScanner.close();
			}
			} catch (URISyntaxException e2) {
				e2.printStackTrace();
				}
			Shell shell = new Shell();
			shell.setImage(SWTResourceManager.getImage(EmailWindow.class, "/resources/LogoBasic.png"));
			shell.setBackground(SWTResourceManager.getColor(SWT.COLOR_LIST_BACKGROUND));
			shell.setSize(770, 603);
			shell.setText("Ryan's MailMerger");
			shell.setLayout(new GridLayout(12, false));
			
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
			comboDropDownIS.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
			comboDropDownIS.add("ukadmissions");
			comboDropDownIS.add("interviews");
			comboDropDownIS.setText("ukadmissions");
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
			lblAttachment.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
			lblAttachment.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
			lblAttachment.setText("Attachment:");
			
			txtAttachment = new Text(shell, SWT.BORDER | SWT.READ_ONLY);
			txtAttachment.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
			txtAttachment.setFont(SWTResourceManager.getFont("PT Sans", 10, SWT.NORMAL));
			txtAttachment.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 6, 1));
			txtAttachment.setEnabled(false);
			
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
			GridData gd_btnAddAttachment = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
			gd_btnAddAttachment.widthHint = 53;
			btnAddAttachment.setLayoutData(gd_btnAddAttachment);
			btnAddAttachment.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseDown(MouseEvent e) {
					attachmentLocation = "";
					attachmentAdded = false;
					FileDialog dialog = new FileDialog(shell, SWT.OPEN);
					dialog.setFilterExtensions(new String [] {"*.*"});
					dialog.setFilterPath("H:\\");
					attachmentLocation = dialog.open();
					if (attachmentLocation != null)	{
						txtSystem.setText(txtSystem.getText() + "File: " + attachmentLocation.substring(attachmentLocation.lastIndexOf("\\") + 1) + " added successfully!" + dLine);
						txtAttachment.setText(attachmentLocation.substring(attachmentLocation.lastIndexOf("\\") + 1));
						attachmentAdded = true;
					}
				}
			});
			btnAddAttachment.setText("Add");
			btnAddAttachment.setEnabled(false);
			
			btnRemoveAttachment = new Button(shell, SWT.NONE);
			btnRemoveAttachment.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseDown(MouseEvent e) {
					if (attachmentAdded)	{
						txtSystem.setText(txtSystem.getText() + "File: " + attachmentLocation.substring(attachmentLocation.lastIndexOf("\\") + 1) + " removed successfully!" + dLine);
					}	else	{
						txtSystem.setText(txtSystem.getText() + "No attachment to remove!" + dLine);
					}
					attachmentLocation = null;
					attachmentAdded = false;
					txtAttachment.setText("");
				}
			});
			btnRemoveAttachment.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
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
			txtSystem.setText("Welcome to Ryan's MailMerger!\r\n\r\n1. Import your data using the 'Import' button, and selecting an excel spreadsheet. \r\n\r\n2. Add a subject and complete the body. \r\n\r\n3. Preview before sending!\r\n\r\n**'Dear [name]' is automatically added to the email!**\r\n\r\n**Your signature is automatically added to the email!**\r\n\r\n");
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
					dialog.setFilterExtensions(new String [] {"*.xlsx"});
					dialog.setFilterPath("H:\\");
					String fileLocation = dialog.open();
					if (fileLocation == null)	{
						txtSystem.setText(txtSystem.getText() + "Please select a file to import!" + dLine);
					}	else	{
						disableMain();
						try	{
							startImport(fileLocation);	
						} catch (OutOfMemoryError ofm)	{
							runError = true;
							errorString = ("Java ran out of memory: " + ofm + dLine);
						}
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
						if (txtSubject.getText().trim().contains("[Replace with subject]")) {
							txtSystem.setText(txtSystem.getText() + "Error: " + System.getProperty("line.separator") + "Please add a subject before sending to applicants!" + dLine);
						}	else if (txtMain.getText().trim().contains("[Replace with body of email]"))	{
							txtSystem.setText(txtSystem.getText() + "Error: " + System.getProperty("line.separator") + "Please change the body of the email before sending to applicants!" + dLine);
						}	else	{
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
		txtAttachment.setEnabled(true);
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
		txtAttachment.setEnabled(false);
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
			System.out.println("Invalid address error!");
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
				System.out.println("Suspected URI error: " + e.toString());
				try {
					service.autodiscoverUrl(loginEmail);
					return credCheck();
				} catch (Exception e1) {
					System.out.println("URI Autodiscover error: " + e1.toString());
				}
			}	else	{
				System.out.println("Unauthorised: " + e.toString());
			}
		} catch (Exception e) {
			System.out.println("General exception error: " + e.toString());
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
							contacts.importWorkbook(xlFile);
						} catch (IOException e1) {
							runError = true;
							errorString = ("Workbook not found: " + e1.toString() + System.getProperty("line.separator"));
						} catch (OutOfMemoryError e2)	{
							runError = true;
							errorString = ("Java ran out of memory: " + e2.toString() + System.getProperty("line.separator"));
						} catch (InvalidFormatException e) {
							runError = true;
							errorString = ("The workbook is not in the correct format: " + e.toString() + System.getProperty("line.separator"));
						} catch (Exception e) {
							runError = true;
							errorString = ("Import error: " + e.toString() + System.getProperty("line.separator"));
						}
					}
	        	});
	    tempString = txtSystem.getText();
		txtSystem.setText("Importing..." + System.getProperty("line.separator"));
	    importThread.start();
	    refreshDisplay(importRun);
	}
	public static void startSend()	{
		sent = false;
		Thread sendThread = new Thread(new Runnable() {
			public void run() {
				try {
					sendAll();
					}	catch (ServiceResponseException e)	{
						errorString = ("Fatal send error: You do not have access to the inbox you are trying to send from!" + dLine + e.toString());
						sent = true;
					}	catch (Exception e1) {
						errorString = ("Fatal send error: " + e1.toString());
						sent = true;
						}
				}
			});
		tempString = txtSystem.getText();
		txtSystem.setText("Sending emails..." + System.getProperty("line.separator"));
		sendThread.start();
		refreshDisplay(sendRun);
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
	static Runnable importRun = new Runnable()	{
		@Override
		public void run() {
			txtSystem.setText(txtSystem.getText() + " ...");
			if (importFinished == true)	{
				refreshService.shutdown();
				if (contacts.getImportSuccess()) {
					addPopMenu(txtMain);
					addPopMenu(txtSubject);
					enableMain();
					txtSystem.setText(tempString + contacts.getResults() + System.getProperty("line.separator"));
					MergeContact first = contacts.getSpecific(1);
					txtName.setText(first.getName());
					txtSID.setText(first.getID());
					txtEmail.setText(first.getEmail());
				}	else	{
					importFinished = false;
					enableMain();
					String errorString = "A required column was not found: " + System.getProperty("line.separator");
					if (contacts.getEmailFound() == false)	{
						errorString += "No email column detected. Please ensure your data contains an 'Email' column." + System.getProperty("line.separator");
					}
					if (contacts.getIdFound() == false)	{
						errorString += "No Student ID column detected. Please ensure your data contains an 'Student' column." + System.getProperty("line.separator");
					}
					if (contacts.getNameFound() == false)	{
						errorString += "No Name column detected. Please ensure your data contains an 'Forename' column." + System.getProperty("line.separator");
					}
					txtSystem.setText(txtSystem.getText() + errorString + dLine);
				}
			}
			if (runError == true) {
				refreshService.shutdown();
				txtSystem.setText(txtSystem.getText() + errorString + dLine);
				runError = false;
			}
		}
	};
	static Runnable sendRun = new Runnable()	{
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
					txtSystem.setText(tempString + "Emails sent with errors: " + dLine + errorString + dLine +
							"All errors have been saved to 'Errors.txt'." + dLine +
							"Please be aware that invalid email addresses will bounce back to the inbox, and will not be included in the above errors." + dLine);
					try {
						PrintWriter errorOutput = new PrintWriter("Errors.txt");
						errorOutput.print(errorString);
						errorOutput.close();
					} catch (FileNotFoundException e) {
						txtSystem.setText(txtSystem.getText() + "Error: " + e + dLine);
					}
				}
			}
			if (runError == true) {
				refreshService.shutdown();
				txtSystem.setText(txtSystem.getText() + System.getProperty("line.separator") + errorString + dLine);
				runError = false;
			}
		}
	};
	public static void setMergeList(String[] listToSet) {
		mergeList = listToSet;
	}
	public static void setImportFinished(boolean importToSet)	{
		importFinished = importToSet;
	}
	private static void setEmailParas()	{
		userName = findName();
		inbox = comboDropDownIS.getText().trim() + loginEmail.substring(loginEmail.indexOf('@')).trim();
		emailBody = txtMain.getText().trim();
		subject = txtSubject.getText().trim();
	}
	private static EmailMessage createEmail(int current) throws Exception	{
		String thisBody = emailBody, thisSubject = subject;
		String signature = "";
		if (!(StringUtils.endsWithIgnoreCase(emailBody, "regards") 
				|| StringUtils.endsWithIgnoreCase(emailBody, "wishes")
				|| StringUtils.endsWithIgnoreCase(emailBody, "thanks")))	{
			signature = "<br/><br/>Kind regards";
		}
		signature += "<br/><br/>" + userName + "<br/>" + findJob();
		EmailMessage msg = new EmailMessage(service);
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
		msg.setBody(MessageBody.getMessageBodyFromText("<div style='font-family:PT Sans;font-size:13'>" + 
		"Dear " + contacts.getSpecific(current).getName() + ",<br/><br/>" + 
				thisBody.replaceAll(System.getProperty("line.separator"), "<br/>") + signature));
		if (attachmentAdded == true)	{
			msg.getAttachments().addFileAttachment(attachmentLocation);
		}
		if (thisSubject.endsWith("."))	{
			msg.setSubject(thisSubject + " Student ID: " + contacts.getSpecific(current).getID());
		}	else	{
			msg.setSubject(thisSubject + ". Student ID: " + contacts.getSpecific(current).getID());
		}
		msg.getToRecipients().add(contacts.getSpecific(current).getEmail());
		msg.setFrom(new EmailAddress(inbox));
		return msg;
	}
	
	private static void sendAll() throws Exception	{
		int total = contacts.getTotal();
		errorString = "";
		for (int i = 1; i < total; ++i)	{
			contacts.setCurrent(i);
			EmailMessage message = createEmail(i);
			if (contacts.getSpecific(i).getEmail().equals("noemail"))	{
				errorString += ("Error: " + contacts.getSpecific(i).getName() + " not emailed. Email address missing!") + dLine;
				} else if (contacts.getSpecific(i).getName().equals("Name not found!"))	{
					errorString += ("Error: " + contacts.getSpecific(i).getEmail() + " not emailed. Name is missing!")  + dLine;
				} else if (contacts.getSpecific(i).getID().equals("studentIDMissing"))	{
					errorString += ("Error: " + contacts.getSpecific(i).getName() + " not emailed. Student ID is missing!" + dLine);
				}	else	{
					Mailbox sentBox = new Mailbox();
					sentBox.setAddress(inbox);
					FolderId sentBoxFolder = new FolderId(WellKnownFolderName.SentItems, sentBox);
					if (attachmentAdded)	{
						message.getBccRecipients().add(inbox);
						message.send();
					}	else	{
						message.sendAndSaveCopy(sentBoxFolder);
					}
				}
			}
		sent = true;
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
			message.setSubject(preview.getSubject());
			message.setRecipients(Message.RecipientType.TO,
		            InternetAddress.parse(txtEmail.getText()));
			if (attachmentAdded == true)	{
				Multipart multiPart = new MimeMultipart();
				BodyPart mainBody = new MimeBodyPart();
				mainBody.setContent(preview.getBody().toString().replaceAll("’", "'"), "text/html");
				multiPart.addBodyPart(mainBody);
				mainBody = new MimeBodyPart();
				DataSource source = new FileDataSource(attachmentLocation);
				mainBody.setDataHandler(new DataHandler(source));
				mainBody.setFileName(txtAttachment.getText());
				multiPart.addBodyPart(mainBody);
				message.setContent(multiPart);
			}	else	{
				message.setContent(preview.getBody().toString().replaceAll("’", "'"), "text/html");
			}
			message.writeTo(bos);
			os.close();
			bos.close();
            Runtime.getRuntime().exec(new String[]
            {"rundll32", "url.dll,FileProtocolHandler",
            	output.getAbsolutePath()});
            enableMain();
		} catch (Exception e) {
			enableMain();
			txtSystem.setText("Fatal Preview error: " + e.toString());
			e.printStackTrace();
		}
	}
	private static String findJob()	{
		String returnString = "";
		try {
			NameResolutionCollection nameResolutions = service.resolveName(loginEmail.substring(0, loginEmail.indexOf('@')), ResolveNameSearchLocation.DirectoryOnly, true);
			if (nameResolutions.getCount() == 1) {
				returnString = nameResolutions.nameResolutionCollection(0).getContact().getJobTitle();
			}	else	{
				returnString = "Admissions Member";
			}
		} catch (Exception e) {
			return "Admissions Member";
		}
		return returnString;
	}
	private static String findName()	{
		String returnString = "";
		try {
			NameResolutionCollection nameResolutions = service.resolveName(loginEmail.substring(0, loginEmail.indexOf('@')), ResolveNameSearchLocation.DirectoryOnly, true);
			if (nameResolutions.getCount() == 1) {
				returnString = nameResolutions.nameResolutionCollection(0).getContact().getDisplayName();
			}	else	{
				returnString = "Admissions Member";
			}
		} catch (Exception e) {
			txtSystem.setText("Fatal Name error: " + e.toString());
		}
		return returnString;
	}
	private static void insertMerge(String mergeToAdd, Text text)	{
		int caretPosition = text.getCaretPosition();
		String formatted = "<<" + mergeToAdd + ">>";
		text.setText(text.getText(0, (caretPosition - 1)) + formatted + text.getText(caretPosition, text.getText().length()));
		text.setSelection(caretPosition + formatted.length());
	}
	private static void addPopMenu(Text textToAdd)	{
	    Menu popupMenu = new Menu(textToAdd);
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