package passCrack;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Button;
import java.util.Random;
import java.util.Scanner;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
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
	public static Label outputLabel;
	private static Text passInput;
	private Label lblEnterPassword;
	static double startTime = 0, endTime = 0,totalTime = 0, restartTime = 0, newTime = 0;
	static String password = "", guess = "", returnString = "";
	static final String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890!$%^&*()_-+[{]};:'@#~,<.>/?|=\\£ \"";
	static  final Random random = new Random();
	static int length = 0;
	static long tries = 0;
	Scanner conInput = new Scanner (System.in);
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
	public static void start()	{
		tries = 0;
		startTime = System.currentTimeMillis();
		newTime = System.currentTimeMillis();
		outputLabel.setText("Working...");
		guess();
		display();
	}
	
	public static void guess()	{
		password = passInput.getText().trim();
		length = password.length();
		Thread guessThread = new Thread()	{
			public void run()	{
		    	while (!(guess.equals(password))) {
		    		guess = "";
		    		++tries;
					while (guess.length() != password.length())	{
						guess += chars.charAt(random.nextInt(chars.length()));
					}
		    	}
			}
		};
		guessThread.start();
	}
	private static void display()	{
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				if (!(password.equals(guess)))	{
					if (newTime - restartTime > 1000) {
						restartTime = System.currentTimeMillis();
						outputLabel.setText(outputLabel.getText() + " ...");
					}
					newTime = System.currentTimeMillis();
					display();
				}	else	{
					endTime = System.currentTimeMillis();
					totalTime = endTime - startTime;
					returnString = ("The password was: " + guess + System.getProperty("line.separator"));
					returnString += ("The program took " + (totalTime / 1000) + " seconds to complete." + System.getProperty("line.separator"));
					returnString += ("This program took " + tries + " tries to guess the password." + System.getProperty("line.separator"));
					try {
						PrintWriter output;
						output = new PrintWriter(new FileWriter("PassCrack_Output.txt", true));
						output.println(returnString);
						output.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					outputLabel.setText(returnString);
				}
			}
		});
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
				start();
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
					start();
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
