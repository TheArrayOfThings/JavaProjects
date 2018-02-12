import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class Handler {
	String searchName = "";
	int contactNumber = 0;
	int contactEdit = 0;
	int exceptionTrip = 0;
	int arrayReached = 0;
	int wholeTrip = 0;
	int retreiveContact = 0;
	String FName = "";
	String SName = "";
	String pNum = "";
	Label outputLabel;
	String currentName;
	int editContactNumber = 1;
	int arrayNumber = 0;
	int searchContactNumber = 1;
	String returnString = "";
	String[] retreiveArray = new String[3];
	PhoneEntry[] phoneBook = new PhoneEntry[1000];
	File phoneBookFile = new File ("Phonebook.txt");
	Handler (Label outputLabelPara)	{
		outputLabel = outputLabelPara;
	}
	void editContact(PhoneEntry editEntryPara, int editContactPara)	{
		editContactNumber = editContactPara;
		phoneBook[editContactNumber] = editEntryPara;
	}
	String displayAll()	{
		returnString = "";
		for (int contactCount = 0; contactCount < contactNumber; contactCount++)	{
			returnString += "Contact: \n";
			returnString += (phoneBook[contactCount].displayContactNumber());
			returnString += "\n";
			returnString += (phoneBook[contactCount].displayFullName());
			returnString += "\n \n";
		}
		return returnString;
	}
	int searchName(String searchNamePara)	{
		searchContactNumber = 1;
		searchName = searchNamePara;
		while ((!(searchName.equals(currentName))) && (searchContactNumber < contactNumber))	{
			currentName = phoneBook[searchContactNumber].displayFullName().trim().toLowerCase();
			searchContactNumber++;
		}
		searchContactNumber--;
		if (currentName.equals(searchName))	{
			currentName = null; 
			return searchContactNumber;
		}
		else	{
			currentName = null; 
			return searchContactNumber + 1;
		}
	}
	void setCurrent(String FNamePara, String SNamePara, String NumPara)	{
		FName = FNamePara;
		SName = SNamePara;
		pNum = NumPara;
	}
	String[] retreiveContact(Integer contactPara)	{
		retreiveContact = contactPara;
		retreiveArray[0] = phoneBook[retreiveContact].displayFName();
		retreiveArray[1] = phoneBook[retreiveContact].displaySName();
		retreiveArray[2] = phoneBook[retreiveContact].displayNumber();
		return retreiveArray;
	}
	void addNew()	{
		PhoneEntry entry = new PhoneEntry(FName, SName, pNum, contactNumber);
		phoneBook[contactNumber] = entry;
		contactNumber++;
	}
	void initialise() throws FileNotFoundException	{
		boolean exists = phoneBookFile.exists();
		if (!(exists))	{
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
				if (bookScanner.hasNextLine())	{
					FName = bookScanner.nextLine();
				}
				if (bookScanner.hasNextLine())	{
					SName = bookScanner.nextLine();
				}
				if (bookScanner.hasNextLine())	{
					pNum = bookScanner.nextLine();
				}
				this.addNew();
			}
			bookScanner.close();
			outputLabel.setText("Phone book imported successfully!");
		}
	}
}
