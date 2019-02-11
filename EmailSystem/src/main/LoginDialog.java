package main;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wb.swt.SWTResourceManager;

import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.enumeration.property.WellKnownFolderName;
import microsoft.exchange.webservices.data.core.exception.service.remote.ServiceRequestException;
import microsoft.exchange.webservices.data.core.service.schema.FolderSchema;
import microsoft.exchange.webservices.data.credential.ExchangeCredentials;
import microsoft.exchange.webservices.data.credential.WebCredentials;
import microsoft.exchange.webservices.data.search.FindFoldersResults;
import microsoft.exchange.webservices.data.search.FolderView;
import microsoft.exchange.webservices.data.search.filter.SearchFilter;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.ProgressBar;

public class LoginDialog extends Dialog {
	
	protected Shell shlLogin;
	private Text txtEmail;
	private Text txtPW;
	private String loginEmail = "", results = "";
	private ExchangeService service;
	private Button btnSubmit;
	private Label lblEmail, lblPleaseEnterYour, lblPassword;
	private boolean success = false, running = false, error = false;
	private ScheduledExecutorService refreshService;
	private ProgressBar loginBar;
	private boolean forwards = true;
	
	public LoginDialog(Shell parent, int style, ExchangeService servicePara) {
		super(parent, style);
		setText("Login");
		service = servicePara;
	}

	public String open() {
		createContents();
		shlLogin.open();
		shlLogin.layout();
		Display display = getParent().getDisplay();
		while (!shlLogin.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return loginEmail;
	}

	private void createContents() {
		shlLogin = new Shell(getParent(), getStyle());
		shlLogin.setSize(520, 194);
		shlLogin.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		shlLogin.setImage(SWTResourceManager.getImage(LoginDialog.class, "/resources/LogoBasic.png"));
		shlLogin.setText("Login");
		shlLogin.setLayout(new GridLayout(2, false));
		shlLogin.addListener(SWT.Close, new Listener()	{
			public void handleEvent (Event event)	{
				if (refreshService != null)	{
					if (!(refreshService.isShutdown()))	{
						refreshService.shutdown();
					}
				}
			}
		});
		
		lblPleaseEnterYour = new Label(shlLogin, SWT.BORDER);
		lblPleaseEnterYour.setAlignment(SWT.CENTER);
		GridData gd_lblPleaseEnterYour = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 2);
		gd_lblPleaseEnterYour.heightHint = 52;
		lblPleaseEnterYour.setLayoutData(gd_lblPleaseEnterYour);
		lblPleaseEnterYour.setFont(SWTResourceManager.getFont("Calibri", 10, SWT.NORMAL));
		lblPleaseEnterYour.setBackground(SWTResourceManager.getColor(SWT.COLOR_INFO_BACKGROUND));
		lblPleaseEnterYour.setText("Please enter your login details\r\n**Please be aware this can take a very long time when first run**");
		
		lblEmail = new Label(shlLogin, SWT.NONE);
		lblEmail.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		lblEmail.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblEmail.setFont(SWTResourceManager.getFont("PT Sans", 10, SWT.NORMAL));
		lblEmail.setText("Email:");
		
		txtEmail = new Text(shlLogin, SWT.BORDER);
		txtEmail.setFont(SWTResourceManager.getFont("PT Sans", 10, SWT.NORMAL));
		txtEmail.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		lblPassword = new Label(shlLogin, SWT.NONE);
		lblPassword.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		lblPassword.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblPassword.setFont(SWTResourceManager.getFont("PT Sans", 10, SWT.NORMAL));
		lblPassword.setText("Password:");
		
		txtPW = new Text(shlLogin, SWT.BORDER | SWT.PASSWORD);
		txtPW.setFont(SWTResourceManager.getFont("PT Sans", 10, SWT.NORMAL));
		txtPW.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		txtPW.addKeyListener(new KeyAdapter() { //used to activate with enter press. 
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.character == SWT.CR)	{
					login();
				}
			}
		});
		
		btnSubmit = new Button(shlLogin, SWT.NONE);
		btnSubmit.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		btnSubmit.setText("Submit");
		btnSubmit.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				login();
			}
		});
		
		loginBar = new ProgressBar(shlLogin, SWT.SMOOTH);
		loginBar.setVisible(false);
		loginBar.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
	}
	private void login()	{
		error = false;
		if (!(txtEmail.getText().trim().equals("") || txtPW.getText().trim().equals("")))	{
			String emailToCheck = txtEmail.getText().trim();
			String pwToCheck = txtPW.getText().trim();
			enable(false);
			Thread loginThread = new Thread()	{
				public void run()	{
					running = true;
					error = false;
					boolean credResult = credCheck(emailToCheck, pwToCheck);
					running = false;
					if (credResult)	{
						success = true;
					}
				}
			};
			loginThread.setDaemon(true);
			loginThread.start();
			loginBar.setVisible(true);
			loginBar.setSelection(0);
			refreshService = Executors.newSingleThreadScheduledExecutor();
			refreshService.scheduleAtFixedRate(runLoginRunnable, 0, 16, TimeUnit.MILLISECONDS);
			}
		}
	private boolean credCheck(String emailPara, String passwordPara)	{
		boolean accepted = false;
		String email = emailPara;
		String password = passwordPara;
		ExchangeCredentials credentials = new WebCredentials(email, password);
		try {
			InternetAddress check = new InternetAddress(email);
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
				//Autodiscovery attempted
					try {
						service.autodiscoverUrl(email);
						return credCheck(email, password);
					} catch (Exception e1) {
						error = true;
						results = "Autodiscover error: " + e1.toString();
					}
			}
		} catch (Exception e) {
			error = true;
			results = "Unknown credential error: " + e.toString();
		}
		return accepted;
	}
	public void enable(boolean toSet)	{
		txtEmail.setEnabled(toSet);
		txtPW.setEnabled(toSet);
		btnSubmit.setEnabled(toSet);
		lblEmail.setEnabled(toSet);
		lblPleaseEnterYour.setEnabled(toSet);
		lblPassword.setEnabled(toSet);
	}
	Runnable loginRunnable = new Runnable()	{
			public void run()	{
				if (forwards)	{
					if (loginBar.getSelection() >= 100)	{
						forwards = false;
					}
					loginBar.setSelection(loginBar.getSelection() + 1);
				}	else	{
					if (loginBar.getSelection() <= 0)	{
						forwards = true;
					}
					loginBar.setSelection(loginBar.getSelection() - 1);
				}
				if (running == false)	{
					refreshService.shutdown();
					loginBar.setVisible(false);
					LoginDialog.this.enable(true);
					if (success)	{ //Login success
						loginEmail = txtEmail.getText().trim();
						shlLogin.close();
					}	else	{
						if (error)	{
							writeErrors(results);
						}
						IncorrectDialog incorrect = new IncorrectDialog(shlLogin, SWT.CLOSE | SWT.SYSTEM_MODAL);
						incorrect.open();
						}
					}
				}
			};
	Runnable runLoginRunnable = new Runnable()	{
		public void run()	{
			Display.getDefault().asyncExec(loginRunnable);
		}
	};
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
}
