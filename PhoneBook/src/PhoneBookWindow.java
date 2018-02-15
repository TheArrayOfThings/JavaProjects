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
import java.util.Scanner; 
import java.io.*;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
public class PhoneBookWindow {
	private static Text textContact;
	private static Text txtForename;
	private static Text txtSurname;
	private static Text txtNumber;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) throws IOException {
		Display display = Display.getDefault();
		Shell shlPhonebook = new Shell();
		shlPhonebook.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_BACKGROUND));
		shlPhonebook.setSize(750, 500);
		shlPhonebook.setText("PhoneBook");
		shlPhonebook.setLayout(null);
		
		Label lblOutputPlace = new Label(shlPhonebook, SWT.WRAP);
		lblOutputPlace.setForeground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblOutputPlace.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
		lblOutputPlace.setBackground(SWTResourceManager.getColor(SWT.COLOR_BLACK));
		lblOutputPlace.setBounds(414, 10, 310, 447);
		//Main stuff starts here
		
		Handler mainHandle = new Handler(lblOutputPlace);
		mainHandle.initialise(); //Initial import/setup
		
		Button btnAddContact = new Button(shlPhonebook, SWT.NONE);
		btnAddContact.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e)	{
				PhoneEntry searchEntry = new PhoneEntry(txtForename.getText(), txtSurname.getText(), txtNumber.getText());
				if (txtForename.getText().equals(""))	{ //checks for blank surname
					lblOutputPlace.setText("Please enter a Forename");
				}
				else if (txtSurname.getText().equals(""))	{ //checks for blank surname
					lblOutputPlace.setText("Please enter a Surname");
				}
				else if (txtNumber.getText().equals(""))	{ //checks for blank number
					lblOutputPlace.setText("Please enter a phone number");
				}
				else if (mainHandle.search(searchEntry))	{
					lblOutputPlace.setText("Contact already found!");
				}
				else	{
					mainHandle.setCurrent(txtForename.getText(), txtSurname.getText(), txtNumber.getText());
					mainHandle.addNew();
					lblOutputPlace.setText("Contact added sucessfully!");
					textContact.setText(Integer.toString((mainHandle.returnLast())));
				}
			}
		});
		btnAddContact.setText("Add Contact");
		btnAddContact.setBounds(10, 354, 95, 25);
		
		Button btnRemoveContact = new Button(shlPhonebook, SWT.NONE);
		btnRemoveContact.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				mainHandle.deleteEntry(Integer.parseInt(textContact.getText()));
				textContact.setText("");
				txtForename.setText("");
				txtSurname.setText("");
				txtNumber.setText("");
			}
		});
		btnRemoveContact.setText("Remove Contact");
		btnRemoveContact.setBounds(154, 354, 95, 25);
		
		Button btnDisplayAll = new Button(shlPhonebook, SWT.NONE);
		btnDisplayAll.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				mainHandle.displayAll();
			}
		});
		btnDisplayAll.setText("Display All");
		btnDisplayAll.setBounds(313, 354, 95, 25);
		
		textContact = new Text(shlPhonebook, SWT.BORDER);
		textContact.setText("");
		textContact.setBounds(111, 12, 26, 21);
		
		txtForename = new Text(shlPhonebook, SWT.BORDER);
		txtForename.setText("");
		txtForename.setBounds(111, 77, 182, 21);
		
		txtSurname = new Text(shlPhonebook, SWT.BORDER);
		txtSurname.setText("");
		txtSurname.setBounds(111, 123, 182, 21);
		
		txtNumber = new Text(shlPhonebook, SWT.BORDER);
		txtNumber.setText("");
		txtNumber.setBounds(111, 169, 182, 21);
		
		Button btnSubmit = new Button(shlPhonebook, SWT.NONE);
		btnSubmit.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				PhoneEntry editEntry = new PhoneEntry(txtForename.getText(), txtSurname.getText(), txtNumber.getText());
				mainHandle.editContact(editEntry, Integer.parseInt(textContact.getText()));
			}
		});
		btnSubmit.setToolTipText("This will store any changes you've made to the contact.");
		btnSubmit.setBounds(10, 212, 95, 25);
		btnSubmit.setText("Submit");
		
		Label lblNotAValid = new Label(shlPhonebook, SWT.NONE);
		lblNotAValid.setBounds(10, 39, 254, 21);
		
		Label lblContactNumber = new Label(shlPhonebook, SWT.NONE);
		lblContactNumber.setBounds(10, 15, 95, 23);
		lblContactNumber.setText("Contact Number");
		
		Label lblForename = new Label(shlPhonebook, SWT.NONE);
		lblForename.setText("Forename");
		lblForename.setBounds(10, 77, 95, 23);
		
		Label lblSurname = new Label(shlPhonebook, SWT.NONE);
		lblSurname.setText("Surname");
		lblSurname.setBounds(10, 123, 95, 23);
		
		Label lblNumber = new Label(shlPhonebook, SWT.NONE);
		lblNumber.setText("Number");
		lblNumber.setBounds(10, 169, 95, 23);
		
		Button btnRetreiveContact = new Button(shlPhonebook, SWT.NONE);
		btnRetreiveContact.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				try	{
					String[] retreiveArray = mainHandle.retreiveContact(Integer.parseInt((textContact.getText())));
					txtForename.setText(retreiveArray[0]);
					txtSurname.setText(retreiveArray[1]);
					txtNumber.setText(retreiveArray[2]);
					lblNotAValid.setText("");
				} catch (Exception outOfBounds)	{
					lblNotAValid.setText("Not a valid contact!");
				}
			}
		});
		btnRetreiveContact.setBounds(154, 10, 106, 25);
		btnRetreiveContact.setText("Retreive Contact");
		
		Button btnClear = new Button(shlPhonebook, SWT.NONE);
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
		btnClear.setBounds(198, 212, 95, 25);
		
		Button btnDebug = new Button(shlPhonebook, SWT.NONE);
		btnDebug.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				lblOutputPlace.setText(Integer.toString((mainHandle.contactNumber)));
			}
		});
		btnDebug.setToolTipText("This will store any changes you've made to the contact.");
		btnDebug.setText("Debug");
		btnDebug.setBounds(111, 276, 95, 25);
	
		shlPhonebook.open();
		shlPhonebook.layout();
		while (!shlPhonebook.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
}