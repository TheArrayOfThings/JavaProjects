package initialSetup;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wb.swt.SWTResourceManager;

import main.InputDialog;
import main.TextMenu;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Combo;

public class SetupWindow extends Dialog {

	protected Shell shlConfirm;
	private Text txtSignature;
	private Combo comboInboxes;
	private Button btnAddInbox;
	private Button btnRemove;
	private Label lblInbox;
	private Label lblSignature;
	private boolean result = false;
	
	public SetupWindow(Shell parent, int style) {
		super(parent, style);
		setText("Setup");
	}

	public boolean open() {
		createContents();
		shlConfirm.open();
		shlConfirm.layout();
		Display display = getParent().getDisplay();
		while (!shlConfirm.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return result;
	}

	private void createContents() {
		shlConfirm = new Shell(getParent(), getStyle());
		shlConfirm.setImage(SWTResourceManager.getImage(SetupWindow.class, "/resources/LogoBasic.png"));
		shlConfirm.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		shlConfirm.setSize(800, 299);
		shlConfirm.setText("Setup");
		shlConfirm.setLayout(new GridLayout(6, false));
		shlConfirm.addListener(SWT.Close, new Listener()	{
			public void handleEvent (Event event)	{
				result = submit();
			}
		});
		
		Label lblAreYouSure = new Label(shlConfirm, SWT.BORDER | SWT.WRAP | SWT.CENTER);
		lblAreYouSure.setFont(SWTResourceManager.getFont("Calibri", 10, SWT.NORMAL));
		lblAreYouSure.setAlignment(SWT.LEFT);
		lblAreYouSure.setBackground(SWTResourceManager.getColor(SWT.COLOR_INFO_BACKGROUND));
		GridData gd_lblAreYouSure = new GridData(SWT.FILL, SWT.FILL, true, true, 4, 6);
		gd_lblAreYouSure.widthHint = 383;
		gd_lblAreYouSure.heightHint = 93;
		lblAreYouSure.setLayoutData(gd_lblAreYouSure);
		lblAreYouSure.setText("\r\nWelcome to the MailMerger setup!\r\n\r\nUse the 'Inboxes' section to set the email inboxes that will be available for selection when using the MailMerger;\r\n\r\n   - Click 'Add' to add an inbox\r\n   - Click 'Remove' to remove an inbox\r\n\r\nUse the 'Signature' section to set the signature that will be automatically added to the bottom of each email;\r\n\r\n   - Right click to add merge fields to the signature (such as 'Job Title')\r\n   - The 'Signature' box is HTML compatible \r\n\r\nClick 'Submit' to continue.");
		
		lblInbox = new Label(shlConfirm, SWT.BORDER);
		lblInbox.setAlignment(SWT.CENTER);
		GridData gd_lblInbox = new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1);
		gd_lblInbox.widthHint = 155;
		lblInbox.setLayoutData(gd_lblInbox);
		lblInbox.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblInbox.setText("Inboxes");
		
		comboInboxes = new Combo(shlConfirm, SWT.READ_ONLY);
		GridData gd_comboInboxes = new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1);
		gd_comboInboxes.widthHint = 201;
		comboInboxes.setLayoutData(gd_comboInboxes);
		
		btnAddInbox = new Button(shlConfirm, SWT.NONE);
		btnAddInbox.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				String input = new InputDialog(shlConfirm, SWT.CLOSE | SWT.APPLICATION_MODAL, "Enter Inbox").open();
				if (!(input.equals(""))) {
					comboInboxes.add(input);
					comboInboxes.select(comboInboxes.getItemCount() - 1);
				}
			}
		});
		GridData gd_btnAddInbox = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_btnAddInbox.widthHint = 56;
		btnAddInbox.setLayoutData(gd_btnAddInbox);
		btnAddInbox.setText("Add");
		
		btnRemove = new Button(shlConfirm, SWT.NONE);
		btnRemove.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				if(comboInboxes.getItemCount() > 0)	{
					comboInboxes.remove(comboInboxes.getText());
					comboInboxes.select(comboInboxes.getItemCount() - 1);
				}
			}
		});
		btnRemove.setText("Remove");
		
		lblSignature = new Label(shlConfirm, SWT.BORDER | SWT.SHADOW_IN);
		lblSignature.setAlignment(SWT.CENTER);
		lblSignature.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
		lblSignature.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblSignature.setText("Signature");
		
		txtSignature = new Text(shlConfirm, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL | SWT.MULTI);
		GridData gd_txtSignature = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
		gd_txtSignature.widthHint = 372;
		gd_txtSignature.heightHint = 123;
		txtSignature.setLayoutData(gd_txtSignature);
		
		Button btnSubmit = new Button(shlConfirm, SWT.NONE);
		btnSubmit.setText("Submit");
		GridData gd_btnSubmit = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_btnSubmit.widthHint = 58;
		btnSubmit.setLayoutData(gd_btnSubmit);
		btnSubmit.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				result = submit();
				if (result)	{
					shlConfirm.close();
				}	else	{
					MessageBox invalid = new MessageBox(shlConfirm, SWT.CLOSE | SWT.APPLICATION_MODAL);
					invalid.setText("Invalid!");
					invalid.setMessage("You need to add at least 1 inbox and your signature cannot be blank!");
					invalid.open();
					}
				}
			});
		new Label(shlConfirm, SWT.NONE);
		new Label(shlConfirm, SWT.NONE);
		new Label(shlConfirm, SWT.NONE);
		new Label(shlConfirm, SWT.NONE);
		new Label(shlConfirm, SWT.NONE);
		new Label(shlConfirm, SWT.NONE);
		new Label(shlConfirm, SWT.NONE);
		
		new TextMenu(txtSignature).addMainMenu(new String[]{"Sending_Name", "Job_Title", "Sending_Inbox"});
		
		if (new File(".//Setup.txt").exists())	{	
			User_Details importDetails = new User_Details();
			for (String eachString: importDetails.getInboxes()) {
				comboInboxes.add(eachString);
			}
			comboInboxes.select(0);
			txtSignature.setText(importDetails.getSignature());
		}

	}
	public void exportSetup(String signaturePara, String[] inboxesPara)	{
		signaturePara = signaturePara.replaceAll(System.getProperty("line.separator"), "<br/>");
		try {
			PrintWriter output = new PrintWriter(new File(".//Setup.txt"));
			output.println("//Signature Start");
			output.println(signaturePara.trim());
			output.println("//Inboxes Start");
			for(String eachString: inboxesPara)	{
				if (eachString.indexOf("@") > 0) {
					output.println(eachString.substring(0, eachString.indexOf("@")).trim());
				}	else	{
					output.println(eachString.trim());
				}
			}
			output.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	private boolean valid()	{
		if (txtSignature.getText().trim().equals("") || comboInboxes.getItemCount() < 1)	{
			return false;
		}
		return true;
	}
	private boolean submit()	{
		if (valid())	{
			String[] inboxes = new String[comboInboxes.getItemCount()];
			for (int i = 0; i < inboxes.length; ++i)	{
				comboInboxes.select(i);
				inboxes[i] = comboInboxes.getText().trim();
				}
			exportSetup(txtSignature.getText().trim(), inboxes);
			return true;
			}
		else	{
			return false;
			}
		}
}
