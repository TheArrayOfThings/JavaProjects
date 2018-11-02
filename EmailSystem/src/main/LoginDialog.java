package main;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wb.swt.SWTResourceManager;
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

public class LoginDialog extends Dialog {

	protected Object result;
	protected Shell shlLogin;
	private Text txtEmail;
	private Text txtPW;
	Shell mainShell;
	Listener closeAll;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public LoginDialog(Shell parent, int style) {
		super(parent, style);
		setText("SWT Dialog");
		mainShell = parent;
	}

	public Object open() {
		createContents();
		shlLogin.open();
		shlLogin.layout();
		Display display = getParent().getDisplay();
		while (!shlLogin.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return result;
	}

	private void createContents() {
		shlLogin = new Shell(getParent(), getStyle());
		shlLogin.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		shlLogin.setImage(SWTResourceManager.getImage(LoginDialog.class, "/resources/LogoBasic.png"));
		shlLogin.setSize(450, 184);
		shlLogin.setText("Login");
		shlLogin.setLayout(new GridLayout(2, false));
		closeAll = new Listener() {
			public void handleEvent(Event e) {
				mainShell.close();
			}
		};
		shlLogin.addListener(SWT.Close, closeAll); 
		
		Label lblPleaseEnterYour = new Label(shlLogin, SWT.BORDER);
		lblPleaseEnterYour.setAlignment(SWT.CENTER);
		GridData gd_lblPleaseEnterYour = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 2);
		gd_lblPleaseEnterYour.heightHint = 52;
		lblPleaseEnterYour.setLayoutData(gd_lblPleaseEnterYour);
		lblPleaseEnterYour.setFont(SWTResourceManager.getFont("PT Sans", 10, SWT.NORMAL));
		lblPleaseEnterYour.setBackground(SWTResourceManager.getColor(SWT.COLOR_INFO_BACKGROUND));
		lblPleaseEnterYour.setText("Please enter your login details\r\n**Please be aware this can take a very long time when first run**");
		
		Label lblEmail = new Label(shlLogin, SWT.NONE);
		lblEmail.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		lblEmail.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblEmail.setFont(SWTResourceManager.getFont("PT Sans", 10, SWT.NORMAL));
		lblEmail.setText("Email:");
		
		txtEmail = new Text(shlLogin, SWT.BORDER);
		txtEmail.setFont(SWTResourceManager.getFont("PT Sans", 10, SWT.NORMAL));
		txtEmail.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblPassword = new Label(shlLogin, SWT.NONE);
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
		
		Button btnSubmit = new Button(shlLogin, SWT.NONE);
		btnSubmit.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		btnSubmit.setText("Submit");
		btnSubmit.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				login();
			}
		});
		new Label(shlLogin, SWT.NONE);

	}
	private void login()	{
		if (!(txtEmail.getText().trim().equals("") || txtPW.getText().trim().equals("")))	{
			EmailWindow.setEmail(txtEmail.getText());
			EmailWindow.setPW(txtPW.getText());
			try {
				if (EmailWindow.credCheck())	{ //Login success
					EmailWindow.enableMain();
					shlLogin.removeListener(SWT.Close, closeAll);
					shlLogin.close();
					EmailWindow.addPersonal();
					EmailWindow.enableMain();
				}	else	{
					IncorrectDialog incorrect = new IncorrectDialog(shlLogin, SWT.CLOSE | SWT.SYSTEM_MODAL);
					incorrect.open();
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}
}
