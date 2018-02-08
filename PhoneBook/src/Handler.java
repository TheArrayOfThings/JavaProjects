import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;
import org.eclipse.swt.widgets.Label;

public class Handler {
	PhoneBook phoneBook = new PhoneBook();
	String searchName = "";
	int contactNumber = 1;
	int contactEdit = 0;
	int exceptionTrip = 0;
	int arrayReached = 0;
	int wholeTrip = 0;
	String FName = "";
	String SName = "";
	String pNum = "";
	Label outputLabel;
	public static File phoneBookFile = new File ("Phonebook.txt");
	Handler (Label labelPara)	{
		outputLabel = labelPara;
	}
	void addNew(String FNamePara, String SNamePara, String numPara)	{
		FName = FNamePara;
		SName = SNamePara;
		pNum = numPara;
		PhoneEntry entry = new PhoneEntry(FName, SName, pNum, contactNumber);
		phoneBook.addContact(entry);
		contactNumber++;
	}
	void initialise() throws FileNotFoundException	{
		boolean exists = phoneBookFile.exists();
		if (!(exists))
		{
			PrintWriter output = new PrintWriter ("Phonebook.txt");
			/*System.out.println("This program can create, edit, and store a phonebook.");
			System.out.println();
			System.out.println("Enter \"add\" to add a new contact.");
			System.out.println("Enter \"display\" to display a contact.");
			System.out.println("Enter \"change\" edit a contact.");
			System.out.println("Enter \"all\" to view all contacts.");
			System.out.println();*/
			outputLabel.setText("This program can create, edit, and store a phone book");
		}
		else	{
			Scanner bookScanner = new Scanner (phoneBookFile);
			outputLabel.setText("Phone book importing...");
			while (bookScanner.hasNext())	{
				if (bookScanner.hasNextInt())	{
					contactNumber = bookScanner.nextInt();
				}
				if (bookScanner.hasNextLine())	{
					bookScanner.nextLine();
					FName = bookScanner.nextLine();
				}
				if (bookScanner.hasNextLine())	{
					bookScanner.nextLine();
					SName = bookScanner.nextLine();
				}
				if (bookScanner.hasNextLine())	{
					pNum = bookScanner.nextLine();
				}
				if (bookScanner.hasNext())	{
					bookScanner.next();
				}
				PhoneEntry entry = new PhoneEntry(FName, SName, pNum, contactNumber);
				phoneBook.addContact(entry);
				contactNumber++;
			}
			bookScanner.close();
			outputLabel.setText("Phone book imported successfully!");
		}
	}
}
