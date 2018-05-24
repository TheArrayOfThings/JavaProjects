package main;

import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.wb.swt.SWTResourceManager;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;

public class Login {
	private static Shell mainShell, loginShell;	
	private static Text txtPleaseEnterExchange, txtEmail, txtPW;
	private static Listener closeAll;
	
	protected static void openLogin(Shell mainShellPara)	{
		mainShell = mainShellPara;
		loginShell = new Shell(mainShell, SWT.TITLE|SWT.SYSTEM_MODAL| SWT.CLOSE | SWT.MAX);
		loginShell.setImage(SWTResourceManager.getImage(Login.class, "/resources/LogoBasic.png"));
		loginShell.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		loginShell.setSize(382, 183);
		loginShell.setText("Login");
		loginShell.setLayout(new GridLayout(2, false));
		closeAll = new Listener() {
			public void handleEvent(Event e) {
				mainShell.close();
			}
		};
		loginShell.addListener(SWT.Close, closeAll); 
		
		txtPleaseEnterExchange = new Text(loginShell, SWT.BORDER | SWT.READ_ONLY | SWT.CENTER | SWT.WRAP);
		txtPleaseEnterExchange.setFont(SWTResourceManager.getFont("PT Sans", 10, SWT.NORMAL));
		txtPleaseEnterExchange.setBackground(SWTResourceManager.getColor(SWT.COLOR_INFO_BACKGROUND));
		txtPleaseEnterExchange.setText("Please enter your login details" + System.getProperty("line.separator") + 
				"**Please be aware this can take a very long time when first run**"); 
		GridData gd_txtPleaseEnterExchange = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
		gd_txtPleaseEnterExchange.widthHint = 343;
		txtPleaseEnterExchange.setLayoutData(gd_txtPleaseEnterExchange);
		txtPleaseEnterExchange.setEnabled(false);
		new Label(loginShell, SWT.NONE);
		new Label(loginShell, SWT.NONE);
		
		Label lblEmail = new Label(loginShell, SWT.NONE);
		lblEmail.setFont(SWTResourceManager.getFont("PT Sans", 10, SWT.NORMAL));
		lblEmail.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		GridData gd_lblEmail = new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1);
		gd_lblEmail.widthHint = 40;
		lblEmail.setLayoutData(gd_lblEmail);
		lblEmail.setText("Email:");
		
		txtEmail = new Text(loginShell, SWT.BORDER);
		GridData gd_txtEmail = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_txtEmail.widthHint = 276;
		txtEmail.setLayoutData(gd_txtEmail);
		
		Label lblPW = new Label(loginShell, SWT.NONE);
		lblPW.setFont(SWTResourceManager.getFont("PT Sans", 10, SWT.NORMAL));
		lblPW.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		GridData gd_lblPW = new GridData(SWT.RIGHT, SWT.FILL, false, false, 1, 1);
		gd_lblPW.widthHint = 60;
		lblPW.setLayoutData(gd_lblPW);
		lblPW.setText("Password:");
		
		txtPW = new Text(loginShell, SWT.BORDER | SWT.PASSWORD);
		GridData gd_txtPW = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_txtPW.widthHint = 277;
		txtPW.setLayoutData(gd_txtPW);
		txtPW.addKeyListener(new KeyAdapter() { //used to activate with enter press. 
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.character == SWT.CR)	{
					login();
				}
			}
		});
		
		Button btnSubmit = new Button(loginShell, SWT.NONE);
		btnSubmit.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				login();
			}
		});
		btnSubmit.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		btnSubmit.setText("Submit");
		new Label(loginShell, SWT.NONE);
		loginShell.open();
	}
	private static void login()	{
		if (!(txtEmail.getText().trim().equals("") || txtPW.getText().trim().equals("")))	{
			EmailWindow.setEmail(txtEmail.getText());
			EmailWindow.setPW(txtPW.getText());
			try {
				if (EmailWindow.credCheck())	{
					loginShell.removeListener(SWT.Close, closeAll);
					loginShell.close();
					mainShell.open();
					mainShell.layout();
					mainShell.setMinimized(false);
					mainShell.setActive();
					EmailWindow.enableMain();
				}	else	{
					Shell incorrectShell = new Shell(mainShell, SWT.TITLE|SWT.SYSTEM_MODAL| SWT.CLOSE | SWT.MAX);
					Incorrect incorrect = new Incorrect(incorrectShell);
					incorrect.open();
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}
}
