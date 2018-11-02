package passgen;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.wb.swt.SWTResourceManager;

import passgen.Login;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;

public class PassGenWindow {
	private static Text textPassword;
	private static Text txtPassname;
	private static Text txtOutput;
	private static Button btnSubmit;
	private static Label lblPWName;
	private static Label lblLeftLabel;
	private static Button buttonPrevious;
	private static Button buttonNext;
	private static Button btnGenerate;
	private static Button btnClear;
	private static Button btnRemove;
	private static Text txtChars;
	private static Label lblCharacterNumber;
	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		Display display = Display.getDefault();
		Shell shlPassgen = new Shell();
		shlPassgen.setImage(SWTResourceManager.getImage(PassGenWindow.class, "/resources/LogoBasic.png"));
		shlPassgen.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		shlPassgen.setSize(386, 372);
		shlPassgen.setText("Passgen");
		shlPassgen.setLayout(new GridLayout(7, false));
		
		lblPWName = new Label(shlPassgen, SWT.NONE);
		lblPWName.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblPWName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblPWName.setText("Pass Name:");
		
		txtPassname = new Text(shlPassgen, SWT.BORDER);
		GridData gd_txtPassname = new GridData(SWT.FILL, SWT.CENTER, true, false, 4, 1);
		gd_txtPassname.widthHint = 124;
		txtPassname.setLayoutData(gd_txtPassname);
		
		buttonPrevious = new Button(shlPassgen, SWT.NONE);
		buttonPrevious.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				PassGenHandler.retreive(-1);
			}
		});
		buttonPrevious.setText("<");
		
		buttonNext = new Button(shlPassgen, SWT.NONE);
		buttonNext.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				PassGenHandler.retreive(1);
			}
		});
		buttonNext.setText(">");
		
		lblLeftLabel = new Label(shlPassgen, SWT.NONE);
		lblLeftLabel.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblLeftLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		lblLeftLabel.setText("Password:");
		
		textPassword = new Text(shlPassgen, SWT.BORDER);
		GridData gd_textPassword = new GridData(SWT.FILL, SWT.CENTER, true, false, 4, 1);
		gd_textPassword.widthHint = 131;
		textPassword.setLayoutData(gd_textPassword);
		
		btnSubmit = new Button(shlPassgen, SWT.NONE);
		GridData gd_btnSubmit = new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1);
		gd_btnSubmit.widthHint = 49;
		btnSubmit.setLayoutData(gd_btnSubmit);
		btnSubmit.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				PassWord toAdd = new PassWord(txtPassname.getText(), textPassword.getText());
				int searchInt = PassGenHandler.search(toAdd);
				if (searchInt != -1)	{
					if	(!(PassGenHandler.retreiveSpecific(searchInt).getPass().equals(toAdd.getPass()))) {
						PassGenHandler.overwrite(toAdd, searchInt);
					}
				}	else if (toAdd.getName().trim().equals("") || toAdd.getPass().trim().equals(""))	{
					txtOutput.setText("Please add a name and generate a password!");
				}	else	{
					PassGenHandler.addNew(toAdd);
					txtOutput.setText(PassGenHandler.printAll());
				}
			}
		});
		btnSubmit.setText("Submit");
		
		btnGenerate = new Button(shlPassgen, SWT.NONE);
		btnGenerate.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		btnGenerate.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				try	{
					int charNumber = Integer.valueOf(txtChars.getText());
					textPassword.setText(PassGenHandler.generateNew(charNumber));
				}	catch (NumberFormatException e1)	{
					textPassword.setText(PassGenHandler.generateNew(10));
				}
			}
		});
		btnGenerate.setText("Generate");
		
		btnRemove = new Button(shlPassgen, SWT.NONE);
		btnRemove.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				PassGenHandler.remove();
				txtOutput.setText(PassGenHandler.printAll());
			}
		});
		GridData gd_btnRemove = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_btnRemove.widthHint = 61;
		btnRemove.setLayoutData(gd_btnRemove);
		btnRemove.setText("Remove");
		
		btnClear = new Button(shlPassgen, SWT.NONE);
		btnClear.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				clear();
			}
		});
		GridData gd_btnClear = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_btnClear.widthHint = 61;
		btnClear.setLayoutData(gd_btnClear);
		btnClear.setText("Clear");
		
		lblCharacterNumber = new Label(shlPassgen, SWT.NONE);
		lblCharacterNumber.setAlignment(SWT.RIGHT);
		lblCharacterNumber.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 3, 1));
		lblCharacterNumber.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblCharacterNumber.setText("Character Number");
		
		txtChars = new Text(shlPassgen, SWT.BORDER | SWT.CENTER);
		txtChars.setTextLimit(2);
		txtChars.setText("10");
		GridData gd_txtChars = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_txtChars.widthHint = 12;
		txtChars.setLayoutData(gd_txtChars);
		
		txtOutput = new Text(shlPassgen, SWT.READ_ONLY | SWT.BORDER |SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
		txtOutput.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		GridData gd_txtOutput = new GridData(SWT.FILL, SWT.FILL, true, true, 7, 1);
		gd_txtOutput.widthHint = 257;
		gd_txtOutput.heightHint = 228;
		txtOutput.setLayoutData(gd_txtOutput);
		
		shlPassgen.open();
		shlPassgen.layout();
		disableMain();

		if(PassGenHandler.initialise())	{ //Saved passes found
			new Login(shlPassgen, SWT.CLOSE).open(); //Login successful
		}	else	{
			new FirstLogin(shlPassgen, SWT.CLOSE).open();
		}
		
		if (!shlPassgen.isDisposed()) {
			enableMain();
			txtOutput.setText(PassGenHandler.printAll());
		}

		while (!shlPassgen.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
	private static void clear()	{
		txtPassname.setText("");
		textPassword.setText("");
		PassGenHandler.setSelected(-1);
	}
	public static void displayRetreive(PassWord toDisplay)	{
		txtPassname.setText(toDisplay.getName());
		textPassword.setText(toDisplay.getPass());
	}
	private static void enableMain()	{
		textPassword.setEnabled(true);
		txtPassname.setEnabled(true);
		txtOutput.setEnabled(true);
		btnSubmit.setEnabled(true);
		lblPWName.setEnabled(true);
		lblLeftLabel.setEnabled(true);
		buttonPrevious.setEnabled(true);
		buttonNext.setEnabled(true);
		btnGenerate.setEnabled(true);
		btnClear.setEnabled(true);
		btnRemove.setEnabled(true);
		txtChars.setEnabled(true);
		lblCharacterNumber.setEnabled(true);
	}
	private static void disableMain()	{
		textPassword.setEnabled(false);
		txtPassname.setEnabled(false);
		txtOutput.setEnabled(false);
		btnSubmit.setEnabled(false);
		lblPWName.setEnabled(false);
		lblLeftLabel.setEnabled(false);
		buttonPrevious.setEnabled(false);
		buttonNext.setEnabled(false);
		btnGenerate.setEnabled(false);
		btnClear.setEnabled(false);
		btnRemove.setEnabled(false);
		txtChars.setEnabled(false);
		lblCharacterNumber.setEnabled(false);
	}
}