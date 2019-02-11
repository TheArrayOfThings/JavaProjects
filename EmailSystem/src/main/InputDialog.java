package main;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;

public class InputDialog extends Dialog {

	protected String result;
	protected Shell shlEnter;
	private Text txtSubmit;
	private String name = "Enter";

	public InputDialog(Shell parent, int style, String namePara) {
		super(parent, style);
		setText(namePara);
		name = namePara;
	}

	public String open() {
		createContents();
		shlEnter.open();
		shlEnter.layout();
		Display display = getParent().getDisplay();
		while (!shlEnter.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return result;
	}

	private void createContents() {
		shlEnter = new Shell(getParent(), getStyle());
		shlEnter.setSize(540, 120);
		shlEnter.setText(name);
		shlEnter.setImage(SWTResourceManager.getImage(InputDialog.class, "/resources/LogoBasic.png"));
		shlEnter.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		shlEnter.setLayout(new GridLayout(1, false));
		Listener closeAll = new Listener() {
			public void handleEvent(Event e) {
				result = txtSubmit.getText().trim();
			}
		};
		shlEnter.addListener(SWT.Close, closeAll); 
		
		txtSubmit = new Text(shlEnter, SWT.BORDER);
		txtSubmit.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.character == SWT.CR) {
					shlEnter.close();
				}
			}
		});
		txtSubmit.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		Button btnSubmit = new Button(shlEnter, SWT.NONE);
		btnSubmit.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				shlEnter.close();
			}
		});
		btnSubmit.setText("Submit");

	}

}
