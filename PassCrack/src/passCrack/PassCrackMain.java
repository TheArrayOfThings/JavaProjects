package passCrack;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Button;
import java.util.Random;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class PassCrackMain {

	protected Shell shlPasscrack;
	private Button btnPasscrack;
	public static Text outputText;
	private static Text passInput;
	private Label lblEnterPassword;
	static double startTime = 0, endTime = 0,totalTime = 0, restartTime = 0, newTime = 0;
	static String password = "", lastGuess = "", returnString = "";
	static final String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890!$%^&*()_-+[{]};:'@#~,<.>/?|=\\£ \"";
	static  final Random random = new Random();
	static int passLength = 0, charsLength = chars.length();
	static long totalTries = 0;
	static boolean disabled = false, endRun = false;
	static Thread guessThread1, guessThread2, guessThread3, guessThread4;
	static ScheduledExecutorService refreshService;
	
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
		shlPasscrack.addListener(SWT.Close, new Listener()	{
			public void handleEvent(Event event) {
				System.out.println("Closing thread...");
				endRun = true;
				if (!(refreshService == null))	{
					refreshService.shutdownNow();
					}
				}
			});
		while (!shlPasscrack.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
	public static void start()	{
		if (passInput.getText().trim().equals(""))	{
			outputText.setText("Please enter a password.");
		}	else if (disabled == false) {
			endRun = false;
			password = passInput.getText().trim();
			passLength = password.length();
			disabled = true;
			lastGuess = "";
			totalTries = 0;
			startTime = System.currentTimeMillis();
			newTime = System.currentTimeMillis();
			outputText.setText("Working...");
			guessThread1 = new Thread()	{
				public void run()	{
					guess();
				}
			};
			guessThread1.start();
			guessThread2 = new Thread()	{
				public void run()	{
					guess();
				}
			};
			guessThread2.start();
			guessThread3 = new Thread()	{
				public void run()	{
					guess();
				}
			};
			guessThread4 = new Thread()	{
				public void run()	{
					guess();
				}
			};
			guessThread4.start();
			display();
		}
	}
	
	private static void guess()	{
		String guess = "";
		while ((!(guess.equals(password))) && endRun == false) {
			++totalTries;
			guess = "";
			while (guess.length() != passLength)	{
				guess += chars.charAt(random.nextInt(charsLength));
				}
			}
    	if (endRun != true) {
    		endRun = true;
    		lastGuess = guess;
    		}
    	}
	private static void display()	{
	    Runnable runRefresh = new Runnable() {
	        public void run() {
	        	Display.getDefault().asyncExec(refresh);
	        }
	    };
	    refreshService = Executors.newSingleThreadScheduledExecutor();
	    refreshService.scheduleAtFixedRate(runRefresh, 0, 33, TimeUnit.MILLISECONDS);
	}
	
	static Runnable refresh = new Runnable() {
			public void run() {
				if (endRun == false)	{
					restartTime = System.currentTimeMillis();
					outputText.setText("Tries: " + totalTries + System.getProperty("line.separator") + 
							"Time: " + Math.round((System.currentTimeMillis() - startTime)/1000) + " seconds.");
					newTime = System.currentTimeMillis();
					return;
				}	else	{
					endTime = System.currentTimeMillis();
					totalTime = endTime - startTime;
					if (totalTime == 0)	{
						totalTime = 1;
					}
					refreshService.shutdownNow();
					returnString = ("The password was: " + lastGuess + System.getProperty("line.separator") + 
						"The program took " + (totalTime / 1000) + " seconds to complete." + System.getProperty("line.separator") +
						"This program took " + totalTries + " tries to guess the password." + System.getProperty("line.separator") + 
						"This is " + Math.round((totalTries/totalTime)) + " tries a millisecond." + System.getProperty("line.separator"));
					try {
						PrintWriter output;
						output = new PrintWriter(new FileWriter("PassCrack_Output.txt", true));
						output.println(returnString);
						output.close();
					} catch (IOException e) {
						outputText.setText(outputText.getText() + System.getProperty("line.separator") + "Error: " + e);
					}
					outputText.setText(returnString + System.getProperty("line.separator") + System.getProperty("line.separator") + "These results have been saved in \"PassCrack_Output.txt\".");
					disabled = false;
				}
			}
		};

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shlPasscrack = new Shell();
		shlPasscrack.setImage(SWTResourceManager.getImage(PassCrackMain.class, "/resources/LogoBasic.png"));
		shlPasscrack.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_BACKGROUND));
		shlPasscrack.setSize(357, 305);
		shlPasscrack.setText("PassCrack");
		shlPasscrack.setLayout(new GridLayout(3, false));
		
		outputText = new Text(shlPasscrack, SWT.BORDER | SWT.READ_ONLY | SWT.WRAP | SWT.V_SCROLL);
		outputText.setFont(SWTResourceManager.getFont("Calibri", 10, SWT.NORMAL));
		GridData gd_outputLabel = new GridData(SWT.FILL, SWT.FILL, true, true, 3, 2);
		gd_outputLabel.widthHint = 293;
		outputText.setLayoutData(gd_outputLabel);
		outputText.setToolTipText("Output window");
		outputText.setText("This program will take your password and brute-force crack it by adding random characters together." + System.getProperty("line.separator") + System.getProperty("line.separator") +
				"This can be useful for testing how good your password is." + System.getProperty("line.separator") + System.getProperty("line.separator") +
				"I recommend that you test it with small passwords (2-3 characters long) at first." + System.getProperty("line.separator") + System.getProperty("line.separator") +
				"Longer passwords will take a VERY long time to complete. " + System.getProperty("line.separator") + System.getProperty("line.separator") +
				"The outcomes of the various 'PassCracks' are stored in 'PassCrack_Output.txt'.");
		outputText.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		
		lblEnterPassword = new Label(shlPasscrack, SWT.BORDER | SWT.SHADOW_IN | SWT.CENTER);
		lblEnterPassword.setFont(SWTResourceManager.getFont("Calibri", 10, SWT.NORMAL));
		GridData gd_lblEnterPassword = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_lblEnterPassword.heightHint = 18;
		gd_lblEnterPassword.widthHint = 91;
		lblEnterPassword.setLayoutData(gd_lblEnterPassword);
		lblEnterPassword.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblEnterPassword.setText("Enter Password:");
		
		passInput = new Text(shlPasscrack, SWT.BORDER);
		GridData gd_passInput = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_passInput.widthHint = 133;
		passInput.setLayoutData(gd_passInput);
		passInput.addKeyListener(new KeyAdapter() { //used to activate with enter press. 
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.character == SWT.CR)	{
					start();
				}
			}
		});

		
		btnPasscrack = new Button(shlPasscrack, SWT.NONE);
		GridData gd_btnPasscrack = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_btnPasscrack.widthHint = 72;
		btnPasscrack.setLayoutData(gd_btnPasscrack);
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
		btnPasscrack.setText("PassCrack");
	}
}