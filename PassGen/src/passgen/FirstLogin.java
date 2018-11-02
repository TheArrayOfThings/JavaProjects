package passgen;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.wb.swt.SWTResourceManager;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Label;

public class FirstLogin extends Dialog {

	protected Object result;
	protected Shell shlLogin;
	private Text txtPin;
	private static Listener closeAll;
	private Shell mainShell;
	private Text txtConfirm;
	private Label lblPin;
	private Label lblConfirm;
	private StyledText txtInfo;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public FirstLogin(Shell parent, int style) {
		super(parent, style);
		setText("Login");
		mainShell = parent;
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open() {
		createContents();
		shlLogin.open();
		shlLogin.layout();
		closeAll = new Listener() {
			public void handleEvent(Event e) {
				mainShell.close();
			}
		};
		shlLogin.addListener(SWT.Close, closeAll); 
		Display display = getParent().getDisplay();
		while (!shlLogin.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return result;
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shlLogin = new Shell(getParent(), getStyle());
		shlLogin.setImage(SWTResourceManager.getImage(FirstLogin.class, "/resources/LogoBasic.png"));
		shlLogin.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		shlLogin.setSize(413, 115);
		shlLogin.setText("Create PIN");
		shlLogin.setLayout(new GridLayout(3, false));
		
		lblPin = new Label(shlLogin, SWT.NONE);
		lblPin.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblPin.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		lblPin.setText("PIN:");
		
		txtPin = new Text(shlLogin, SWT.BORDER | SWT.PASSWORD);
		txtPin.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		txtInfo = new StyledText(shlLogin, SWT.BORDER | SWT.READ_ONLY);
		txtInfo.setEnabled(false);
		txtInfo.setEditable(false);
		txtInfo.setBackground(SWTResourceManager.getColor(SWT.COLOR_INFO_BACKGROUND));
		txtInfo.setText("Enter a 4 digit PIN.\r\n\r\nThis will be used to access your saved passwords.");
		txtInfo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 3));
		
		lblConfirm = new Label(shlLogin, SWT.NONE);
		lblConfirm.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblConfirm.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		lblConfirm.setText("Confirm:");
		
		txtConfirm = new Text(shlLogin, SWT.BORDER | SWT.PASSWORD);
		txtConfirm.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		txtConfirm.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.character == SWT.CR)	{
					login();
				}
			}
		});
		
		Button btnSubmit = new Button(shlLogin, SWT.NONE);
		GridData gd_btnSubmit = new GridData(SWT.FILL, SWT.FILL, false, false, 2, 1);
		gd_btnSubmit.widthHint = 50;
		btnSubmit.setLayoutData(gd_btnSubmit);
		btnSubmit.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
			}
		});
		btnSubmit.setText("Submit");

	}
	
	private void login()	{
		if (txtPin.getText().trim().equals(txtConfirm.getText().trim()))	{
			if (PassGenHandler.valid(txtPin.getText().trim()))	{
				txtInfo.setText("Success");
				PassGenHandler.setKey(txtPin.getText().trim());
				PassGenHandler.exportAll();
				shlLogin.removeListener(SWT.Close, closeAll);
				shlLogin.close();
			}	else	{
				txtInfo.setText("Invalid PIN;" + System.getProperty("line.separator") + "PIN should contain 4 numbers");
			}
		}	else	{
			txtInfo.setText("Error: Entries do not match!");
		}	
	}

}
