package main;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Text;

public class Incorrect {
	Shell incorrectShell;
	Incorrect(Shell incorrectShellPara)	{
		incorrectShell = incorrectShellPara;
	}

	private Text txtOutput;

	public void open() {
		Display display = Display.getDefault();
		createContents();
		incorrectShell.open();
		incorrectShell.layout();
		while (!incorrectShell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		incorrectShell.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		incorrectShell.setImage(SWTResourceManager.getImage(Incorrect.class, "/resources/LogoBasic.png"));
		incorrectShell.setSize(450, 168);
		incorrectShell.setText("Incorrect login details!");
		incorrectShell.setLayout(new GridLayout(7, false));
		
		txtOutput = new Text(incorrectShell, SWT.BORDER | SWT.READ_ONLY | SWT.WRAP);
		txtOutput.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		txtOutput.setText("Login credentials incorrect! \r\n\r\nPlease login to this program using your staff login details for outlook.\r\n");
		GridData gd_txtOutput = new GridData(SWT.FILL, SWT.FILL, true, false, 7, 3);
		gd_txtOutput.heightHint = 85;
		txtOutput.setLayoutData(gd_txtOutput);
		
		Button btnOk = new Button(incorrectShell, SWT.NONE);
		GridData gd_btnOk = new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1);
		gd_btnOk.widthHint = 83;
		btnOk.setLayoutData(gd_btnOk);
		btnOk.setText("OK");
		btnOk.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				incorrectShell.close();
			}
		});
		new Label(incorrectShell, SWT.NONE);
		new Label(incorrectShell, SWT.NONE);
		new Label(incorrectShell, SWT.NONE);
		new Label(incorrectShell, SWT.NONE);
		new Label(incorrectShell, SWT.NONE);
		new Label(incorrectShell, SWT.NONE);

	}

}
