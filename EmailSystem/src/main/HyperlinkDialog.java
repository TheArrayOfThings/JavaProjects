package main;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;

public class HyperlinkDialog extends Dialog {

	protected String result;
	protected Shell shlEnterHyperlink;
	private Text txtHyperlink;

	public HyperlinkDialog(Shell parent, int style) {
		super(parent, style);
		setText("Insert Hyperlink");
	}

	public String open() {
		createContents();
		shlEnterHyperlink.open();
		shlEnterHyperlink.layout();
		Display display = getParent().getDisplay();
		while (!shlEnterHyperlink.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return result;
	}

	private void createContents() {
		shlEnterHyperlink = new Shell(getParent(), getStyle());
		shlEnterHyperlink.setImage(SWTResourceManager.getImage(HyperlinkDialog.class, "/resources/LogoBasic.png"));
		shlEnterHyperlink.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		shlEnterHyperlink.setSize(450, 180);
		shlEnterHyperlink.setText("Enter Hyperlink");
		shlEnterHyperlink.setLayout(new GridLayout(2, false));
		
		Label lblInstructions = new Label(shlEnterHyperlink, SWT.BORDER);
		lblInstructions.setBackground(SWTResourceManager.getColor(SWT.COLOR_INFO_BACKGROUND));
		lblInstructions.setAlignment(SWT.CENTER);
		GridData gd_lblInstructions = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
		gd_lblInstructions.heightHint = 107;
		gd_lblInstructions.widthHint = 435;
		lblInstructions.setLayoutData(gd_lblInstructions);
		lblInstructions.setText("\r\nEnter link to hyperlink and press 'Submit'");
		
		txtHyperlink = new Text(shlEnterHyperlink, SWT.BORDER);
		txtHyperlink.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		txtHyperlink.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.character == SWT.CR)	{
					result = txtHyperlink.getText();
					shlEnterHyperlink.close();
				}
			}
		});
		
		Button btnSubmit = new Button(shlEnterHyperlink, SWT.NONE);
		btnSubmit.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				result = txtHyperlink.getText();
				shlEnterHyperlink.close();
			}
		});
		btnSubmit.setText("Submit");

	}

}
