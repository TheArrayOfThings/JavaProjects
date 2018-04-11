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
	private static Text textKeyPW;
	private static Text txtPassname;
	private static Text txtOutput;
	private static Button btnSubmit;
	private static Label lblPWName;
	private static Label lblLeftLabel;
	private static Button buttonPrevious;
	private static Button buttonNext;
	private static Button btnGenerate;
	private static Button btnClear;
	private static boolean login = false;
	private static Button btnRemove;
	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		final PassGenHandler mainHandle = new PassGenHandler();
		Display display = Display.getDefault();
		Shell shell = new Shell();
		shell.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		shell.setSize(374, 415);
		shell.setText("SWT Application");
		shell.setLayout(new GridLayout(6, false));
		
		lblPWName = new Label(shell, SWT.NONE);
		lblPWName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblPWName.setText("Pass Name:");
		lblPWName.setVisible(false);
		
		txtPassname = new Text(shell, SWT.BORDER);
		GridData gd_txtPassname = new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1);
		gd_txtPassname.widthHint = 124;
		txtPassname.setLayoutData(gd_txtPassname);
		txtPassname.setVisible(false);
		
		buttonPrevious = new Button(shell, SWT.NONE);
		buttonPrevious.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				mainHandle.retreive(-1);
			}
		});
		buttonPrevious.setText("<");
		buttonPrevious.setVisible(false);
		
		buttonNext = new Button(shell, SWT.NONE);
		buttonNext.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				mainHandle.retreive(1);
			}
		});
		buttonNext.setText(">");
		buttonNext.setVisible(false);
		
		lblLeftLabel = new Label(shell, SWT.NONE);
		lblLeftLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		lblLeftLabel.setText("Pin:");
		
		textKeyPW = new Text(shell, SWT.BORDER);
		GridData gd_textKeyPW = new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1);
		gd_textKeyPW.widthHint = 131;
		textKeyPW.setLayoutData(gd_textKeyPW);
		
		btnSubmit = new Button(shell, SWT.NONE);
		GridData gd_btnSubmit = new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1);
		gd_btnSubmit.widthHint = 49;
		btnSubmit.setLayoutData(gd_btnSubmit);
		btnSubmit.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				if (login == false) {
					if (mainHandle.pinEntry())	{
						lblLeftLabel.setText("Password");
						txtPassname.setVisible(true);
						lblPWName.setVisible(true);
						textKeyPW.setText("");
						buttonPrevious.setVisible(true);
						buttonNext.setVisible(true);
						btnGenerate.setVisible(true);
						btnRemove.setVisible(true);
						btnClear.setVisible(true);
						mainHandle.retreive(0);
						login = true;
					}
				}	else	{
					mainHandle.addNew();
				}
			}
		});
		btnSubmit.setText("Submit");
		
		btnGenerate = new Button(shell, SWT.NONE);
		btnGenerate.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		btnGenerate.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				mainHandle.generateNew();
			}
		});
		btnGenerate.setText("Generate");
		btnGenerate.setVisible(false);
		
		btnRemove = new Button(shell, SWT.NONE);
		btnRemove.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				mainHandle.remove();
			}
		});
		GridData gd_btnRemove = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_btnRemove.widthHint = 61;
		btnRemove.setLayoutData(gd_btnRemove);
		btnRemove.setText("Remove");
		btnRemove.setVisible(false);
		
		btnClear = new Button(shell, SWT.NONE);
		btnClear.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				mainHandle.clear();
			}
		});
		GridData gd_btnClear = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_btnClear.widthHint = 61;
		btnClear.setLayoutData(gd_btnClear);
		btnClear.setText("Clear");
		btnClear.setVisible(false);
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);
		
		txtOutput = new Text(shell, SWT.READ_ONLY | SWT.BORDER |SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
		txtOutput.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		GridData gd_txtOutput = new GridData(SWT.FILL, SWT.FILL, true, true, 6, 1);
		gd_txtOutput.widthHint = 257;
		gd_txtOutput.heightHint = 228;
		txtOutput.setLayoutData(gd_txtOutput);
		
		mainHandle.initialise(txtPassname, textKeyPW, txtOutput);
		
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
}