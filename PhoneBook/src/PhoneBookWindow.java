import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.SWT;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import java.io.*;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import java.lang.NumberFormatException;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.graphics.Point;
public class PhoneBookWindow {
	private static Text textContact;
	private static Text txtForename;
	private static Text txtSurname;
	private static Text txtNumber;
	private static Text txtOutput;
	private static PhoneEntry tempEntry;
	private static Handler mainHandle = new Handler();
	
	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) throws IOException {
		Display display = Display.getDefault();
		Shell shlPhonebook = new Shell(display, SWT.CLOSE | SWT.TITLE | SWT.MIN );
		shlPhonebook.setMinimumSize(new Point(380, 580));
		shlPhonebook.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		shlPhonebook.setSize(380, 580);
		shlPhonebook.setText("PhoneBook");
		shlPhonebook.setLayout(new GridLayout(4, false));
		
		Label lblContactNumber = new Label(shlPhonebook, SWT.NONE);
		lblContactNumber.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblContactNumber.setText("Contact Number");
		
		textContact = new Text(shlPhonebook, SWT.BORDER);
		GridData gd_textContact = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_textContact.widthHint = 14;
		textContact.setLayoutData(gd_textContact);
		textContact.setText("");
		
		Button btnRetreiveContact = new Button(shlPhonebook, SWT.NONE);
		btnRetreiveContact.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				try	{
					tempEntry =  mainHandle.retreiveContact(Integer.parseInt((textContact.getText())));
					txtForename.setText(tempEntry.displayFName());
					txtSurname.setText(tempEntry.displaySName());
					txtNumber.setText(tempEntry.displayNumber());
					txtOutput.setText("");
				} catch (Exception outOfBounds)	{
					txtOutput.setText("Not a valid contact!");
				}
			}
		});
		btnRetreiveContact.setText("Retreive");
		
		Button btnClear = new Button(shlPhonebook, SWT.NONE);
		GridData gd_btnClear = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_btnClear.widthHint = 59;
		btnClear.setLayoutData(gd_btnClear);
		btnClear.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				textContact.setText("");
				txtForename.setText("");
				txtSurname.setText("");
				txtNumber.setText("");
			}
		});
		btnClear.setToolTipText("This will store any changes you've made to the contact.");
		btnClear.setText("Clear");
		
		Label lblForename = new Label(shlPhonebook, SWT.NONE);
		lblForename.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblForename.setText("Forename");
		
		txtForename = new Text(shlPhonebook, SWT.BORDER);
		txtForename.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 2, 1));
		new Label(shlPhonebook, SWT.NONE);
		
		Label lblSurname = new Label(shlPhonebook, SWT.NONE);
		lblSurname.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblSurname.setText("Surname");
		
		txtSurname = new Text(shlPhonebook, SWT.BORDER);
		txtSurname.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 2, 1));
		new Label(shlPhonebook, SWT.NONE);
		
		Label lblNumber = new Label(shlPhonebook, SWT.NONE);
		lblNumber.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblNumber.setText("Number");
		
		txtNumber = new Text(shlPhonebook, SWT.BORDER);
		txtNumber.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
		new Label(shlPhonebook, SWT.NONE);
		
		Button btnSubmit = new Button(shlPhonebook, SWT.NONE);
		btnSubmit.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		btnSubmit.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				tempEntry = new PhoneEntry(txtForename.getText(), txtSurname.getText(), txtNumber.getText());
				if (!(mainHandle.hasBlank(tempEntry)))	{
					try	{
						if (mainHandle.search(tempEntry))	{
							txtOutput.setText("No change detected!");
						}
						else	{
							mainHandle.editContact(tempEntry, Integer.parseInt(textContact.getText()));
							txtOutput.setText("Changes submitted successfully!");
						}
					}
					catch (NumberFormatException blankInt)	{
						txtOutput.setText("Please retreive a contact before submitting changes.");
					}
				}
			}
		});
		btnSubmit.setToolTipText("This will store any changes you've made to the contact.");
		btnSubmit.setText("Submit");
		
		Button btnAddContact = new Button(shlPhonebook, SWT.NONE);
		btnAddContact.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		btnAddContact.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e)	{
				tempEntry = new PhoneEntry(txtForename.getText(), txtSurname.getText(), txtNumber.getText());
				if (!(mainHandle.hasBlank(tempEntry)))	{
					if (mainHandle.search(tempEntry))	{
						txtOutput.setText("Contact already found!");
					}
					else	{
						mainHandle.setCurrent(txtForename.getText(), txtSurname.getText(), txtNumber.getText());
						mainHandle.addNew();
						txtOutput.setText(mainHandle.displayAll());
						textContact.setText(Integer.toString((mainHandle.returnLast())));
					}
				}
			}
		});
		btnAddContact.setText("Add Contact");
		
		Button btnRemoveContact = new Button(shlPhonebook, SWT.NONE);
		GridData gd_btnRemoveContact = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_btnRemoveContact.widthHint = 98;
		btnRemoveContact.setLayoutData(gd_btnRemoveContact);
		btnRemoveContact.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				try	{
					if (Integer.parseInt(textContact.getText()) == 0) {
						txtOutput.setText("Not a valid contact!");
					}
					else	{
						mainHandle.deleteEntry(Integer.parseInt(textContact.getText()));
						textContact.setText("");
						txtForename.setText("");
						txtSurname.setText("");
						txtNumber.setText("");
						txtOutput.setText("Contact deleted sucessfully!");
					}
				}
				catch (NumberFormatException blankDelete)	{
					txtOutput.setText("Please retreive a contact to remove!");
					}	
				catch (ArrayIndexOutOfBoundsException outOfBounds)	{
					txtOutput.setText("Not a valid contact!");
					}
			}
		});
		btnRemoveContact.setText("Remove Contact");
		
		Button btnDisplayAll = new Button(shlPhonebook, SWT.NONE);
		btnDisplayAll.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				txtOutput.setText(mainHandle.displayAll());
			}
		});
		btnDisplayAll.setText("Display All");
		
		txtOutput = new Text(shlPhonebook, SWT.READ_ONLY | SWT.BORDER |SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
		txtOutput.setFont(SWTResourceManager.getFont("Calibri", 12, SWT.NORMAL));
		txtOutput.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		GridData gd_txtOutput = new GridData(SWT.LEFT, SWT.FILL, false, false, 4, 1);
		gd_txtOutput.widthHint = 342;
		gd_txtOutput.heightHint = 395;
		txtOutput.setLayoutData(gd_txtOutput);
		mainHandle.setOutput(txtOutput);
		mainHandle.initialise(); //Initial import/setup
	
		shlPhonebook.open();
		shlPhonebook.layout();
		while (!shlPhonebook.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
}