package main;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;

public class Confirm {
	private Text txtAreYouSure;
	private Shell confirmShell;
	Confirm(Shell confirmShellPara)	{
		confirmShell = confirmShellPara;
	}

	public void open() {
		Display display = Display.getDefault();
		createContents();
		confirmShell.open();
		confirmShell.layout();
		while (!confirmShell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		confirmShell.setImage(SWTResourceManager.getImage(Confirm.class, "/resources/LogoBasic.png"));
		confirmShell.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		confirmShell.setSize(450, 237);
		confirmShell.setText("Confirm?");
		confirmShell.setLayout(new GridLayout(2, false));
		
		txtAreYouSure = new Text(confirmShell, SWT.BORDER | SWT.READ_ONLY | SWT.WRAP);
		txtAreYouSure.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		txtAreYouSure.setText("Are you sure that you want to send?\r\n\r\nIf you click 'yes' the program will send out the emails immediately.\r\n\r\n**Please preview before sending any emails!**");
		txtAreYouSure.setBackground(SWTResourceManager.getColor(SWT.COLOR_INFO_BACKGROUND));
		GridData gd_txtAreYouSure = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
		gd_txtAreYouSure.heightHint = 146;
		txtAreYouSure.setLayoutData(gd_txtAreYouSure);
		txtAreYouSure.setFont(SWTResourceManager.getFont("PT Sans", 10, SWT.NORMAL));
		
		Button btnYes = new Button(confirmShell, SWT.NONE);
		btnYes.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				EmailWindow.disableMain();
				EmailWindow.startSend();
				confirmShell.close();
			}
		});
		GridData gd_btnYes = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_btnYes.widthHint = 98;
		btnYes.setLayoutData(gd_btnYes);
		btnYes.setText("Yes!");
		
		Button btnNo = new Button(confirmShell, SWT.NONE);
		btnNo.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				confirmShell.close();
			}
		});
		GridData gd_btnNo = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_btnNo.widthHint = 97;
		btnNo.setLayoutData(gd_btnNo);
		btnNo.setText("No!");

	}

}
