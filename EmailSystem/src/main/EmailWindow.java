package main;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import javax.mail.Message;
import javax.mail.Address;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;

public class EmailWindow {
	private static Text mainText;
	private static Text txtSubject;
	private static Button btnSave;
	private static Email newMail;
	private static Button btnOpen;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		Display display = Display.getDefault();
		Shell shell = new Shell();
		shell.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_BACKGROUND));
		shell.setSize(450, 300);
		shell.setText("SWT Application");
		shell.setLayout(new GridLayout(4, false));
		
		txtSubject = new Text(shell, SWT.BORDER);
		txtSubject.setText("Subject");
		txtSubject.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 4, 1));
		
		mainText = new Text(shell, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		mainText.setText("Main Text");
		GridData gd_mainText = new GridData(SWT.FILL, SWT.FILL, true, false, 4, 1);
		gd_mainText.widthHint = 187;
		gd_mainText.heightHint = 190;
		mainText.setLayoutData(gd_mainText);
		
		Button btnSubmitButton = new Button(shell, SWT.NONE);
		btnSubmitButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				getEmail(); 
				mainText.setText(newMail.getBody());
				txtSubject.setText(newMail.getSubject());
			}
		});
		btnSubmitButton.setText("Submit");
		
		btnOpen = new Button(shell, SWT.NONE);
		btnOpen.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				setEmail();
				newMail.openInOutlook();
			}
		});
		btnOpen.setText("Open");
		
		btnSave = new Button(shell, SWT.NONE);
		btnSave.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				setEmail();
			}
		});
		btnSave.setText("Save");
		new Label(shell, SWT.NONE);

		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
	
	public static void getEmail()	{
		newMail = new Email("H:\\Stuff\\Eclipse Workspace\\EmailSystem\\src\\main\\Test Message.msg");
	}
	public static void setEmail()	{
		newMail.setBody(mainText.getText());
		newMail.setSubject(txtSubject.getText());
	}
}
