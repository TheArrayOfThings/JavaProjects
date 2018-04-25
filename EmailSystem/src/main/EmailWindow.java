package main;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.ole.win32.OleAutomation;
import org.eclipse.swt.ole.win32.OleClientSite;
import org.eclipse.swt.ole.win32.OleFrame;
import org.eclipse.wb.swt.SWTResourceManager;

import ch.astorm.jotlmsg.OutlookMessage;
import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.PropertySet;
import microsoft.exchange.webservices.data.core.enumeration.misc.ExchangeVersion;
import microsoft.exchange.webservices.data.core.enumeration.property.WellKnownFolderName;
import microsoft.exchange.webservices.data.core.service.item.EmailMessage;
import microsoft.exchange.webservices.data.core.service.item.Item;
import microsoft.exchange.webservices.data.credential.ExchangeCredentials;
import microsoft.exchange.webservices.data.credential.WebCredentials;
import microsoft.exchange.webservices.data.property.complex.EmailAddress;
import microsoft.exchange.webservices.data.property.complex.FolderId;
import microsoft.exchange.webservices.data.property.complex.MessageBody;
import microsoft.exchange.webservices.data.property.complex.MimeContent;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import java.awt.Desktop;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.mail.Address;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;

public class EmailWindow {
	private static Text txtMain;
	private static Text txtSubject;
	private static EmailBook newMail;
	private static Button btnPreview;
	private static Button btnSend;
	private static Button button;
	private static Button button_1;
	private static Label lblDear;
	private static Label lblStudentId;
	private static Text txtName;
	private static Text txtSID;
	private static Label lblEmail;
	private static Text txtEmail;
	private static Label lblInbox;
	private static Text txtInbox;
	private static Button btnImport;
	private static Desktop desktop = Desktop.getDesktop();
	private static ExchangeService service = new ExchangeService(ExchangeVersion.Exchange2010_SP2);
	private static String loginEmail = "useremail"; //add user email here
	private static String loginPassword = "password"; //add user password here
	private static Label lblSubject;
	private static Label lblYourName;
	private static Text txtUser;
	
	

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
		shell.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_BACKGROUND));
		shell.setSize(505, 385);
		shell.setText("SWT Application");
		shell.setLayout(new GridLayout(15, false));
		
		lblDear = new Label(shell, SWT.NONE);
		lblDear.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		lblDear.setText("Dear");
		
		txtName = new Text(shell, SWT.BORDER);
		txtName.setText("Ryan");
		txtName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);
		
		lblInbox = new Label(shell, SWT.NONE);
		lblInbox.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		lblInbox.setText("Inbox");
		
		txtInbox = new Text(shell, SWT.BORDER);
		txtInbox.setText("ukadmissions");
		txtInbox.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 5, 1));
		new Label(shell, SWT.NONE);
		
		button = new Button(shell, SWT.NONE);
		button.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		button.setText("<");
		
		button_1 = new Button(shell, SWT.NONE);
		button_1.setText(">");
		
		lblStudentId = new Label(shell, SWT.NONE);
		lblStudentId.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblStudentId.setText("Student ID: ");
		
		txtSID = new Text(shell, SWT.BORDER);
		txtSID.setText("4530542");
		GridData gd_txtSID = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
		gd_txtSID.widthHint = 16;
		txtSID.setLayoutData(gd_txtSID);
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);
		
		lblYourName = new Label(shell, SWT.NONE);
		lblYourName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblYourName.setText("Your Name:");
		
		txtUser = new Text(shell, SWT.BORDER);
		txtUser.setText("Ryan Flanagan");
		txtUser.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 5, 1));
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);
		
		lblEmail = new Label(shell, SWT.NONE);
		lblEmail.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		lblEmail.setText("Email: ");
		
		txtEmail = new Text(shell, SWT.BORDER);
		txtEmail.setText("");
		txtEmail.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 6, 1));
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);
		
		lblSubject = new Label(shell, SWT.NONE);
		lblSubject.setText("Subject");
		
		txtSubject = new Text(shell, SWT.BORDER);
		txtSubject.setText("BU: Something Something");
		txtSubject.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 14, 1));
		
		txtMain = new Text(shell, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		txtMain.setText("Body of email.\r\n\r\nMultiple lines for testing.\r\n\r\nKind regards");
		GridData gd_txtMain = new GridData(SWT.FILL, SWT.FILL, true, false, 15, 1);
		gd_txtMain.widthHint = 284;
		gd_txtMain.heightHint = 190;
		txtMain.setLayoutData(gd_txtMain);
		
		btnImport = new Button(shell, SWT.NONE);
		btnImport.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		btnImport.setText("Import");
		
		btnPreview = new Button(shell, SWT.NONE);
		GridData gd_btnPreview = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_btnPreview.widthHint = 55;
		btnPreview.setLayoutData(gd_btnPreview);
		btnPreview.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				preview();
			}
		});
		btnPreview.setText("Preview");
		
		btnSend = new Button(shell, SWT.NONE);
		GridData gd_btnSend = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_btnSend.widthHint = 35;
		btnSend.setLayoutData(gd_btnSend);
		btnSend.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				try {
					sendEmail();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		btnSend.setText("Send");
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);
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
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
	
	public static EmailMessage createEmail() throws Exception	{
		String inbox = txtInbox.getText() + "@bournemouth.ac.uk";
		String userName = txtUser.getText();
		String signature = "<br/><br/>" + userName + "<br/>UK Admissions | Academic Services<br/><br/>" + 
				"<span style='color:#7F7F7F'>+44 (0)1202 9<b>65356</b></span> | <a>" + inbox + "</a><br/>" + 
				"<span style='color:#7F7F7F'>Bournemouth University | Melbury House | 1-3 Oxford Road | Bournemouth | BH8 8ES</span><br/>" + 
				"<a href='https://www1.bournemouth.ac.uk/'>www.bournemouth.ac.uk</a><br/>" + 
				"<a href='https://www.facebook.com/JoinBournemouthUni'>Facebook</a> |<a href='https://twitter.com/bournemouthuni'>Twitter</a> |<a href='https://www.youtube.com/user/bournemouthuni'>YouTube</a> |<a href='https://www.linkedin.com/groups/3886322/profile'>LinkedIn</a><br/>" + 
				"If you’ve lost your login details, or not received them, please contact the IT Helpdesk by calling 01202 965515.</div>";
		EmailMessage msg = new EmailMessage(service);
		msg.setBody(MessageBody.getMessageBodyFromText("<div style='font-family:PT Sans;font-size:13'>" + 
		"Dear " + txtName.getText() + ",<br/><br/>" + 
		"Student ID: " + txtSID.getText() + "<br/><br/>" + txtMain.getText().replaceAll(System.getProperty("line.separator"), "<br/>") + signature));
		msg.setSubject(txtSubject.getText());
		msg.getToRecipients().add(txtEmail.getText());
		//msg.getBccRecipients().add(inbox);
		msg.setFrom(new EmailAddress(inbox));
		return msg;
	}
	
	public static void sendEmail() throws Exception	{
		EmailMessage message = createEmail();
		Logger logger = Logger.getLogger(EmailBook.class);
		BasicConfigurator.configure();
		logger.info("Trying to send email...");
		try	{
			message.sendAndSaveCopy(WellKnownFolderName.SentItems);
			}	catch (Exception e)	{
				logger.info("Exception (autoservice started): " + e);
				service.autodiscoverUrl(loginEmail);
				message.send();
				}
		}
	public static void preview()	{
		try {
			EmailMessage preview = createEmail();
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

