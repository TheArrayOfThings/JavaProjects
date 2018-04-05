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
	private static Text txtPassword;
	private static Label lblPWName;
	private static Label lblPinLabel;
	private static Label lblActualPw;
	private static Button btnAddNew;
	private static Button buttonPrevious;
	private static Button buttonNext;
	private static Button btnGenerate;
	private static PassWord currentPass;
	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		final PassGenHandler mainHandle = new PassGenHandler();
		Display display = Display.getDefault();
		Shell shell = new Shell();
		shell.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		shell.setSize(450, 370);
		shell.setText("SWT Application");
		shell.setLayout(new GridLayout(4, false));
		
		lblPWName = new Label(shell, SWT.NONE);
		lblPWName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblPWName.setText("Password Name:");
		lblPWName.setVisible(false);
		
		txtPassname = new Text(shell, SWT.BORDER);
		txtPassname.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		txtPassname.setVisible(false);
		
		buttonPrevious = new Button(shell, SWT.NONE);
		buttonPrevious.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				if (currentPass == null) {
					currentPass = mainHandle.retreive(0);
				}	else	{
					currentPass = mainHandle.retreive(currentPass.returnIndex() - 1);
				}
			}
		});
		buttonPrevious.setText("<");
		buttonPrevious.setVisible(false);
		
		buttonNext = new Button(shell, SWT.NONE);
		buttonNext.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				if (currentPass == null) {
					currentPass = mainHandle.retreive(0);
				}	else	{
					currentPass = mainHandle.retreive(currentPass.returnIndex() + 1);
				}

			}
		});
		buttonNext.setText(">");
		buttonNext.setVisible(false);
		
		lblPinLabel = new Label(shell, SWT.NONE);
		lblPinLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		lblPinLabel.setText("Pin:");
		lblPinLabel.setVisible(true);
		
		textKey = new Text(shell, SWT.BORDER);
		GridData gd_txtKey = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_txtKey.widthHint = 45;
		textKey.setLayoutData(gd_txtKey);
		
		btnSetKey = new Button(shell, SWT.NONE);
		btnSetKey.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
		btnSetKey.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				if (mainHandle.pinEntry())	{
					lblPinLabel.setVisible(false);
					textKey.setVisible(false);
					btnSetKey.setVisible(false);
					txtPassname.setVisible(true);
					lblPWName.setVisible(true);
					lblActualPw.setVisible(true);
					txtPassword.setVisible(true);
					btnAddNew.setVisible(true);
					buttonPrevious.setVisible(true);
					buttonNext.setVisible(true);
					btnGenerate.setVisible(true);;
				}	else	{
					
				}

			}
		});
		btnSetKey.setText("Submit");
		
		lblActualPw = new Label(shell, SWT.NONE);
		lblActualPw.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		lblActualPw.setText("Password:");
		lblActualPw.setVisible(false);
		
		txtPassword = new Text(shell, SWT.BORDER);
		txtPassword.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		txtPassword.setVisible(false);
		
		btnAddNew = new Button(shell, SWT.NONE);
		GridData gd_btnAddNew = new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1);
		gd_btnAddNew.widthHint = 47;
		btnAddNew.setLayoutData(gd_btnAddNew);
		btnAddNew.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				mainHandle.addNew();
			}
		});
		btnAddNew.setText("Submit");
		btnAddNew.setVisible(false);
		
		btnGenerate = new Button(shell, SWT.NONE);
		btnGenerate.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				txtPassword.setText(mainHandle.generateNew());
			}
		});
		btnGenerate.setText("Generate");
		btnGenerate.setVisible(false);
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);
		
		txtOutput = new Text(shell, SWT.BORDER | SWT.WRAP);
		GridData gd_txtOutput = new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1);
		gd_txtOutput.widthHint = 350;
		gd_txtOutput.heightHint = 157;
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