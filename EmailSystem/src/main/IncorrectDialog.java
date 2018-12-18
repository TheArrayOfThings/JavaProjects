package main;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;

public class IncorrectDialog extends Dialog {

	protected Shell shlIncorrect;

	public IncorrectDialog(Shell parent, int style) {
		super(parent, style);
		setText("SWT Dialog");
	}

	public void open() {
		createContents();
		shlIncorrect.open();
		shlIncorrect.layout();
		Display display = getParent().getDisplay();
		while (!shlIncorrect.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	private void createContents() {
		shlIncorrect = new Shell(getParent(), getStyle());
		shlIncorrect.setImage(SWTResourceManager.getImage(IncorrectDialog.class, "/resources/LogoBasic.png"));
		shlIncorrect.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		shlIncorrect.setSize(462, 139);
		shlIncorrect.setText("Incorrect!");
		shlIncorrect.setLayout(new GridLayout(1, false));
		
		Label lblLoginCredentialsIncorrect = new Label(shlIncorrect, SWT.BORDER);
		lblLoginCredentialsIncorrect.setAlignment(SWT.CENTER);
		GridData gd_lblLoginCredentialsIncorrect = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_lblLoginCredentialsIncorrect.heightHint = 63;
		lblLoginCredentialsIncorrect.setLayoutData(gd_lblLoginCredentialsIncorrect);
		lblLoginCredentialsIncorrect.setBackground(SWTResourceManager.getColor(SWT.COLOR_INFO_BACKGROUND));
		lblLoginCredentialsIncorrect.setFont(SWTResourceManager.getFont("Calibri", 10, SWT.NORMAL));
		lblLoginCredentialsIncorrect.setText("Login credentials incorrect!\r\n\r\nPlease login to the program using your full outlook email address and password.");
		
		Button btnOk = new Button(shlIncorrect, SWT.NONE);
		btnOk.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				shlIncorrect.close();
			}
		});
		GridData gd_btnOk = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_btnOk.widthHint = 62;
		btnOk.setLayoutData(gd_btnOk);
		btnOk.setText("OK");

	}

}
