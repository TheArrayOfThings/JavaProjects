import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
//import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.SWT;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
//import org.eclipse.swt.events.SelectionAdapter;
//import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;
//import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
public class PhoneBookWindow {
	private static Text textContact;
	private static Text txtForename;
	private static Text txtSurname;
	private static Text txtNumber;
	private static Text txtOutput;
	private static Handler mainHandle = new Handler();
	
	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		Display display = Display.getDefault();
		Shell shlPhonebook = new Shell(display, SWT.CLOSE | SWT.TITLE | SWT.MIN );
		shlPhonebook.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		shlPhonebook.setSize(280, 580);
		shlPhonebook.setText("PhoneBook");
		shlPhonebook.setLayout(new GridLayout(4, false));
		
		Label lblContactNumber = new Label(shlPhonebook, SWT.NONE);
		GridData gd_lblContactNumber = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_lblContactNumber.widthHint = 92;
		lblContactNumber.setLayoutData(gd_lblContactNumber);
		lblContactNumber.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblContactNumber.setText("Contact Number");
		
		textContact = new Text(shlPhonebook, SWT.BORDER);
		textContact.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
					mainHandle.retreiveContact(0);
			}
		});
		GridData gd_textContact = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_textContact.widthHint = 24;
		textContact.setLayoutData(gd_textContact);
		
		Button btnPrevious = new Button(shlPhonebook, SWT.NONE);
		btnPrevious.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				mainHandle.retreiveContact(1);
			}
		});
		GridData gd_btnPrevious = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_btnPrevious.widthHint = 53;
		btnPrevious.setLayoutData(gd_btnPrevious);
		btnPrevious.setText("Previous");
		
		Button btnNext = new Button(shlPhonebook, SWT.NONE);
		btnNext.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				mainHandle.retreiveContact(-1);
			}
		});
		GridData gd_btnNext = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_btnNext.widthHint = 62;
		btnNext.setLayoutData(gd_btnNext);
		btnNext.setText("Next");
		
		Label lblForename = new Label(shlPhonebook, SWT.NONE);
		lblForename.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblForename.setText("Forename");
		
		txtForename = new Text(shlPhonebook, SWT.BORDER);
		txtForename.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 3, 1));
		
		Label lblSurname = new Label(shlPhonebook, SWT.NONE);
		lblSurname.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblSurname.setText("Surname");
		
		txtSurname = new Text(shlPhonebook, SWT.BORDER);
		txtSurname.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 3, 1));
		
		Label lblNumber = new Label(shlPhonebook, SWT.NONE);
		lblNumber.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblNumber.setText("Number");
		
		txtNumber = new Text(shlPhonebook, SWT.BORDER);
		txtNumber.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 3, 1));
		
		Button btnSubmit = new Button(shlPhonebook, SWT.NONE);
		GridData gd_btnSubmit = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_btnSubmit.widthHint = 71;
		btnSubmit.setLayoutData(gd_btnSubmit);
		btnSubmit.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				mainHandle.submit();
			}
		});
		btnSubmit.setToolTipText("This will store any changes you've made to the contact.");
		btnSubmit.setText("Submit");
		new Label(shlPhonebook, SWT.NONE);
		
		Button btnRemoveContact = new Button(shlPhonebook, SWT.NONE);
		GridData gd_btnRemoveContact = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_btnRemoveContact.widthHint = 58;
		btnRemoveContact.setLayoutData(gd_btnRemoveContact);
		btnRemoveContact.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
						mainHandle.deleteEntry();
			}
		});
		btnRemoveContact.setText("Remove");
		
		Button btnClear = new Button(shlPhonebook, SWT.NONE);
		btnClear.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				mainHandle.clearAll();
			}
		});
		GridData gd_btnClear = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_btnClear.widthHint = 39;
		btnClear.setLayoutData(gd_btnClear);
		btnClear.setText("Clear");
		
		txtOutput = new Text(shlPhonebook, SWT.READ_ONLY | SWT.BORDER |SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
		txtOutput.setFont(SWTResourceManager.getFont("Calibri", 12, SWT.NORMAL));
		txtOutput.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		GridData gd_txtOutput = new GridData(SWT.FILL, SWT.FILL, false, false, 4, 1);
		gd_txtOutput.widthHint = 184;
		gd_txtOutput.heightHint = 395;
		txtOutput.setLayoutData(gd_txtOutput);
		mainHandle.initialise(txtOutput, txtForename, txtSurname, txtNumber, textContact);
	
		shlPhonebook.open();
		shlPhonebook.layout();
		while (!shlPhonebook.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
}