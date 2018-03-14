package passgen;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.wb.swt.SWTResourceManager;

import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class PassGenWindow {
	private static Text textKey;
	private static Text txtPassname;
	private static Text txtOutput;
	private static Button btnSetKey;
	private static Button btnTest;
	private static Button btnPrintall;
	private static Text txtPassword;
	private static Label lblPWName;
	private static Label lblPinLabel;
	private static Label lblActualPw;
	private static Button btnAddNew;
	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		final PassGenHandler mainHandle = new PassGenHandler();
		Display display = Display.getDefault();
		Shell shell = new Shell();
		shell.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		shell.setSize(450, 300);
		shell.setText("SWT Application");
		shell.setLayout(new GridLayout(4, false));
		
		lblPWName = new Label(shell, SWT.NONE);
		lblPWName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblPWName.setText("PW Name Label");
		lblPWName.setVisible(false);
		
		txtPassname = new Text(shell, SWT.BORDER);
		txtPassname.setText("PassName");
		txtPassname.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		txtPassname.setVisible(false);
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);
		
		lblPinLabel = new Label(shell, SWT.NONE);
		lblPinLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		lblPinLabel.setText("Pin Label");
		lblPinLabel.setVisible(true);
		
		textKey = new Text(shell, SWT.BORDER);
		textKey.setText("1234");
		GridData gd_txtKey = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_txtKey.widthHint = 45;
		textKey.setLayoutData(gd_txtKey);
		
		btnSetKey = new Button(shell, SWT.NONE);
		btnSetKey.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				if (mainHandle.keyCheck())	{
					lblPinLabel.setVisible(false);
					textKey.setVisible(false);
					btnSetKey.setVisible(false);
					txtPassname.setVisible(true);
					lblPWName.setVisible(true);
					lblActualPw.setVisible(true);
					txtPassword.setVisible(true);
					btnAddNew.setVisible(true);
				}
			}
		});
		btnSetKey.setText("Submit");
		
		btnTest = new Button(shell, SWT.NONE);
		btnTest.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				//Test stuff goes here
			}
		});
		btnTest.setText("Test");
		
		lblActualPw = new Label(shell, SWT.NONE);
		lblActualPw.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		lblActualPw.setText("Actual PW");
		lblActualPw.setVisible(false);
		
		txtPassword = new Text(shell, SWT.BORDER);
		txtPassword.setText("Password");
		txtPassword.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		txtPassword.setVisible(false);
		
		btnAddNew = new Button(shell, SWT.NONE);
		btnAddNew.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				mainHandle.submit();
			}
		});
		btnAddNew.setText("Submit");
		btnAddNew.setVisible(false);
		
		btnPrintall = new Button(shell, SWT.NONE);
		btnPrintall.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				try {
					mainHandle.exportAll();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		btnPrintall.setText("PrintAll");
		
		txtOutput = new Text(shell, SWT.BORDER | SWT.WRAP);
		txtOutput.setText("Output");
		GridData gd_txtOutput = new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1);
		gd_txtOutput.heightHint = 160;
		txtOutput.setLayoutData(gd_txtOutput);
		
		new Label(shell, SWT.NONE);
		
		mainHandle.initialise(textKey, txtOutput, txtPassname, txtPassword);
		
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

}
