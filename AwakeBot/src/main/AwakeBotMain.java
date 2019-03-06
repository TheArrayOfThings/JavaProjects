package main;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.layout.GridData;

import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Label;

public class AwakeBotMain {

	protected Shell shlClicker;
	boolean activated = false;
	public static boolean exited = false;
	public static Label lblProgramIsWorking;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			AwakeBotMain window = new AwakeBotMain();
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
		shlClicker.open();
		shlClicker.layout();
		shlClicker.addListener(SWT.Close, new Listener()	{
			public void handleEvent(Event event) {
				exited = true;
				}
			});
		while (!shlClicker.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shlClicker = new Shell(SWT.SHELL_TRIM & (~SWT.RESIZE) & (~SWT.MAX));
		shlClicker.setImage(SWTResourceManager.getImage(AwakeBotMain.class, "/resources/LogoBasic.png"));
		shlClicker.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.character == SWT.CR)	{
					exited = false;
					AwakeBot.start();
				}
			}
		});
		shlClicker.setBackground(SWTResourceManager.getColor(SWT.COLOR_TRANSPARENT));
		shlClicker.setSize(210, 170);
		shlClicker.setText("AwakeBot");
		shlClicker.setLayout(new GridLayout(1, false));
		
		Button btnStart = new Button(shlClicker, SWT.NONE);
		btnStart.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				exited = false;
				AwakeBot.start();
			}
		});
		btnStart.setFont(SWTResourceManager.getFont("Segoe UI", 26, SWT.NORMAL));
		GridData gd_btnStart = new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1);
		gd_btnStart.heightHint = 103;
		gd_btnStart.widthHint = 192;
		btnStart.setLayoutData(gd_btnStart);
		btnStart.setText("Start");
		
		lblProgramIsWorking = new Label(shlClicker, SWT.CENTER);
		lblProgramIsWorking.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblProgramIsWorking.setFont(SWTResourceManager.getFont("Calibri", 14, SWT.NORMAL));
		lblProgramIsWorking.setText("Press start");
		GridData gd_lblProgramIsWorking = new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1);
		gd_lblProgramIsWorking.widthHint = 69;
		lblProgramIsWorking.setLayoutData(gd_lblProgramIsWorking);

	}
}
