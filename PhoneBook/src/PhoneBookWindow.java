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
public class PhoneBookWindow {
	private static Text text;
	private static Text txtForename;
	private static Text txtSurname;
	private static Text txtNumber;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) throws IOException {

		Handler mainHandle = new Handler();
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
		lblOutputPlace.setBounds(414, 10, 310, 442);
		
		Button btnAddContact = new Button(shlPhonebook, SWT.NONE);
		btnAddContact.setText("Add Contact");
		btnAddContact.setBounds(10, 354, 95, 25);
		
		Button btnRemoveContact = new Button(shlPhonebook, SWT.NONE);
		btnRemoveContact.setText("Remove Contact");
		btnRemoveContact.setBounds(154, 354, 95, 25);
		
		Button btnDisplayAll = new Button(shlPhonebook, SWT.NONE);
		btnDisplayAll.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				//Display All Here
			}
		});
		btnDisplayAll.setText("Display All");
		btnDisplayAll.setBounds(313, 354, 95, 25);
		
		text = new Text(shlPhonebook, SWT.BORDER);
		text.setText("1");
		text.setBounds(111, 12, 26, 21);
		
		txtForename = new Text(shlPhonebook, SWT.BORDER);
		txtForename.setText("Forename");
		txtForename.setBounds(111, 77, 182, 21);
		
		txtSurname = new Text(shlPhonebook, SWT.BORDER);
		txtSurname.setText("Surname");
		txtSurname.setBounds(111, 123, 182, 21);
		
		txtNumber = new Text(shlPhonebook, SWT.BORDER);
		txtNumber.setText("Number");
		txtNumber.setBounds(111, 169, 182, 21);
		
		Button btnSubmit = new Button(shlPhonebook, SWT.NONE);
		btnSubmit.setToolTipText("This will store any changes you've made to the contact.");
		btnSubmit.setBounds(10, 212, 95, 25);
		btnSubmit.setText("Submit");
		
		Label lblNotAValid = new Label(shlPhonebook, SWT.NONE);
		lblNotAValid.setBounds(154, 15, 254, 21);
		lblNotAValid.setText("Not a valid contact!");
		
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

		mainHandle.initialise(lblOutputPlace); //Initial import/setup
	
		shlPhonebook.open();
		shlPhonebook.layout();
		while (!shlPhonebook.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
}