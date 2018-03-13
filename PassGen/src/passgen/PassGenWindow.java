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

public class PassGenWindow {
	private static Text textKey;
	private static Text txtPassname;
	private static Text txtOutput;
	private static Button btnSubmit;
	private static Button btnTest;
	private static Button btnPrintall;
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
		shell.setLayout(new GridLayout(3, false));
		
		textKey = new Text(shell, SWT.BORDER);
		textKey.setText("1234");
		GridData gd_txtKey = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_txtKey.widthHint = 45;
		textKey.setLayoutData(gd_txtKey);
		
		btnTest = new Button(shell, SWT.NONE);
		btnTest.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				try {
					mainHandle.encryptTest();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		btnTest.setText("EncryptWord");
		
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
		new Label(shell, SWT.NONE);
		
		btnSubmit = new Button(shell, SWT.NONE);
		btnSubmit.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				mainHandle.submit();
			}
		});
		btnSubmit.setText("Submit");
		
		txtPassname = new Text(shell, SWT.BORDER);
		txtPassname.setText("PassName");
		txtPassname.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		txtOutput = new Text(shell, SWT.BORDER | SWT.WRAP);
		txtOutput.setText("Output");
		GridData gd_txtOutput = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
		gd_txtOutput.heightHint = 188;
		txtOutput.setLayoutData(gd_txtOutput);
		new Label(shell, SWT.NONE);
		
		mainHandle.initialise(textKey, txtOutput, txtPassname, btnSubmit);

		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

}
