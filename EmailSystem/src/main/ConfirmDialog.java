package main;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;

public class ConfirmDialog extends Dialog {

	protected boolean result;
	protected Shell shlConfirm;


	public ConfirmDialog(Shell parent, int style) {
		super(parent, style);
		setText("Confirm?");
	}

	public boolean open() {
		createContents();
		shlConfirm.open();
		shlConfirm.layout();
		Display display = getParent().getDisplay();
		while (!shlConfirm.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return result;
	}

	private void createContents() {
		shlConfirm = new Shell(getParent(), getStyle());
		shlConfirm.setSize(450, 160);
		shlConfirm.setImage(SWTResourceManager.getImage(ConfirmDialog.class, "/resources/LogoBasic.png"));
		shlConfirm.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		shlConfirm.setText("Confirm?");
		shlConfirm.setLayout(new GridLayout(2, false));
		
		Label lblAreYouSure = new Label(shlConfirm, SWT.BORDER | SWT.CENTER);
		lblAreYouSure.setFont(SWTResourceManager.getFont("Calibri", 10, SWT.NORMAL));
		lblAreYouSure.setAlignment(SWT.CENTER);
		lblAreYouSure.setBackground(SWTResourceManager.getColor(SWT.COLOR_INFO_BACKGROUND));
		GridData gd_lblAreYouSure = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
		gd_lblAreYouSure.heightHint = 93;
		lblAreYouSure.setLayoutData(gd_lblAreYouSure);
		lblAreYouSure.setText("Are you sure that you want to send?\r\n\r\nIf you click 'Confirm' the program will immediately start sending the emails.\r\n\r\n**Please preview before sending any emails!**");
		
		Button btnYes = new Button(shlConfirm, SWT.NONE);
		btnYes.setText("Confirm");
		GridData gd_btnYes = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_btnYes.widthHint = 58;
		btnYes.setLayoutData(gd_btnYes);
		btnYes.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				result = true;
				shlConfirm.close();
			}
		});
		
		Button btnNo = new Button(shlConfirm, SWT.NONE);
		GridData gd_btnNo = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_btnNo.widthHint = 56;
		btnNo.setLayoutData(gd_btnNo);
		btnNo.setText("Cancel");
		btnNo.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				result = false;
				shlConfirm.close();
			}
		});

	}
}
