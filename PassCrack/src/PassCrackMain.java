import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.custom.StyledText;
import java.io.*;
import java.util.logging.*;
import java.text.DecimalFormat;
import javax.swing.JOptionPane;

import org.eclipse.jface.text.TextViewer;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;


public class PassCrackMain {

	protected Shell shlPasscrack;
	private Button btnPasscrack;
	public Label outputLabel;
	private Text passInput;
	private Label lblEnterPassword;
	PassCracker passCrackObj = new PassCracker();
	Logger logger = Logger.getAnonymousLogger();
	DecimalFormat triesFormat = new DecimalFormat("#");
	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			PassCrackMain window = new PassCrackMain();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		shlPasscrack.open();
		shlPasscrack.layout();
		while (!shlPasscrack.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
	
	public void displayCrack()	{ //function that handles displaying the crack results
		if ((!passInput.getText().trim().equals("")))	{
			try {
				PassObject passObject = passCrackObj.crack(passInput.getText().trim());
				outputLabel.setText(
						"Final guess: " + passObject.getGuess() + "\n" +
						"Time taken: " + Double.toString(passObject.getTime()) + " seconds" + "\n" + 
						"This took " + String.valueOf(triesFormat.format(passObject.getTries())) + " tries" + "\n"
						);
			} catch (IOException e1) {
				Exception e2 = new Exception(e1);
				logger.log(Level.SEVERE, "Something happened: ", e2);
				e1.printStackTrace();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shlPasscrack = new Shell();
		shlPasscrack.setImage(SWTResourceManager.getImage("H:\\Stuff\\HTML\\Images\\LogoBasic.png"));
		shlPasscrack.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		shlPasscrack.setSize(400, 384);
		shlPasscrack.setText("PassCrack");
		shlPasscrack.setLayout(null);

		
		btnPasscrack = new Button(shlPasscrack, SWT.NONE);
		btnPasscrack.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			}
		});
		btnPasscrack.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				displayCrack();
			}
		});
		btnPasscrack.setBounds(163, 262, 75, 25);
		btnPasscrack.setText("PassCrack");
		
		outputLabel = new Label(shlPasscrack, SWT.WRAP);
		outputLabel.setToolTipText("Output window");
		outputLabel.setText("This program will take your password and brute-force crack it by adding random characters together.\r\n\r\nThis can be useful for testing how good your password is.\r\n\r\nI recommend that you test it with small passwords (2-3 characters long) at first.\r\n \r\nLonger passwords will take a VERY long time to complete. \r\n\r\nThe outcomes of the various 'PassCracks' are stored in 'PassCrack_Output.txt'.");
		outputLabel.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		outputLabel.setBounds(10, 10, 364, 227);
		
		passInput = new Text(shlPasscrack, SWT.BORDER);
		passInput.addKeyListener(new KeyAdapter() { //used to activate with enter press. 
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.character == SWT.CR)	{
					displayCrack();
				}
			}
		});
		passInput.setBounds(10, 264, 147, 21);
		
		lblEnterPassword = new Label(shlPasscrack, SWT.NONE);
		lblEnterPassword.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblEnterPassword.setBounds(10, 243, 87, 15);
		lblEnterPassword.setText("Enter Password:");

	}
}
