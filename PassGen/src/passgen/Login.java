package passgen;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;

public class Login extends Dialog {

	protected Object result;
	protected Shell shlLogin;
	private Text txtPin;
	private int tries = 10;
	private static Listener closeAll;
	private Shell mainShell;
	private StyledText txtInfo;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public Login(Shell parent, int style) {
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
		shlLogin.setImage(SWTResourceManager.getImage(Login.class, "/resources/LogoBasic.png"));
		shlLogin.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		shlLogin.setSize(352, 145);
		shlLogin.setText("Login");
		shlLogin.setLayout(new GridLayout(2, false));
		
		txtInfo = new StyledText(shlLogin, SWT.BORDER | SWT.READ_ONLY);
		txtInfo.setEnabled(false);
		txtInfo.setEditable(false);
		txtInfo.setBackground(SWTResourceManager.getColor(SWT.COLOR_INFO_BACKGROUND));
		txtInfo.setText("Enter your 4 digit PIN.\r\n\r\nAfter 10 unsucessful tries your saved passwords will be deleted.");
		txtInfo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		
		txtPin = new Text(shlLogin, SWT.BORDER | SWT.PASSWORD);
		txtPin.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		txtPin.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.character == SWT.CR)	{
					login();
				}
			}
		});
		
		Button btnSubmit = new Button(shlLogin, SWT.NONE);
		btnSubmit.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				login();
			}
		});
		btnSubmit.setText("Submit");

	}
	
	private void login()	{
		if(PassGenHandler.check(txtPin.getText()))	{
			PassGenHandler.importAll();
			shlLogin.removeListener(SWT.Close, closeAll);
			shlLogin.close();
		}	else if (tries > 0)	{
			--tries;
			txtInfo.setText("PIN incorrect!" + System.getProperty("line.separator") + 
					"You have " + tries + " tries remaining!");
		}	else	{
			PassGenHandler.deleteAll();
			MessageBox deletion = new MessageBox(shlLogin, SWT.ICON_ERROR | SWT.OK);
			deletion.setMessage("Passwords deleted...");
			deletion.open();
			shlLogin.close();
		}
	}

}
