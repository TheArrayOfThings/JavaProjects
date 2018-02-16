import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;
import org.eclipse.swt.widgets.Label;

public class Handler {
	int contactNumber = 1;
	int retreiveContact = 0;
	String FName = "";
	String SName = "";
	String PNum = "";
	Label outputLabel;
	String returnString = "";
	String[] retreiveArray = new String[3];
	PhoneEntry[] phoneBook = new PhoneEntry[1000];
	File phoneBookFile = new File ("Phonebook.txt");
	boolean exists = phoneBookFile.exists();
	PrintWriter output;
	Handler (Label outputLabelPara)	{
		outputLabel = outputLabelPara;
	}
	void initialise() throws IOException	{ //This is the import function for the phoneBook.
		if (!(exists))	{
			PrintWriter output = new PrintWriter ("Phonebook.txt"); //create text file
			output.close(); //close it as not used here.
			outputLabel.setText("This appears to be your first time running the programme..");
		}	else	{
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
					PNum = bookScanner.nextLine();
				}
				this.addNewEntry();
			}
			bookScanner.close();
			outputLabel.setText("Phone book imported successfully!");
		}
	}
	Boolean hasBlank(PhoneEntry checkEntry)	{
		if (checkEntry.displayFName().equals(""))	{ //checks for blank surname
			outputLabel.setText("Please enter a Forename");
			return true;
		}
		else if (checkEntry.displaySName().equals(""))	{ //checks for blank surname
			outputLabel.setText("Please enter a Surname");
			return true;
		}
		else if (checkEntry.displayNumber().equals(""))	{ //checks for blank number
			outputLabel.setText("Please enter a phone number");
			return true;
		}
		else	{
			return false;
		}
	}
	void editContact(PhoneEntry editEntryPara, int contactNumPara)	{
		phoneBook[contactNumPara] = editEntryPara;
		this.printNew();
	}
	void displayAll()	{ //Displays everything to my output window
		returnString = "";
		for (int contactCount = 1; contactCount < contactNumber; contactCount++)	{
			returnString += "Contact: \n";
			returnString += contactCount;
			returnString += "\n";
			returnString += (phoneBook[contactCount].displayFullName());
			returnString += "\n \n";
		}
		outputLabel.setText(returnString);
	}
	void deleteEntry(int entryDelPara)	{
		phoneBook[entryDelPara] = null;
		PhoneEntry[] tempPhoneBook = new PhoneEntry[1000];
		int tempBookCount = 1;
		for (int i = 1; i < contactNumber; i++)	{
			if (phoneBook[i] != null) {
				tempPhoneBook[tempBookCount] = phoneBook[i];
				tempBookCount++;
			}
		}
		contactNumber = contactNumber - 1;
		phoneBook = tempPhoneBook;
		this.printNew();
	}
	void setCurrent(String FNamePara, String SNamePara, String NumPara)	{
		FName = FNamePara;
		SName = SNamePara;
		PNum = NumPara;
	}
	String[] retreiveContact(Integer contactPara)	{
		retreiveContact = contactPara;
		retreiveArray[0] = phoneBook[retreiveContact].displayFName();
		retreiveArray[1] = phoneBook[retreiveContact].displaySName();
		retreiveArray[2] = phoneBook[retreiveContact].displayNumber();
		return retreiveArray;
	}
	void printNew()	{
		int debug = 0;
		try	{
			output = new PrintWriter("PhoneBook.txt");
			debug = 0;
			for (int contactNum = 1; contactNum < contactNumber; contactNum++)	{
				debug++;
				output.println(phoneBook[contactNum].displayFName());
				debug++;
				output.println(phoneBook[contactNum].displaySName());
				debug++;
				output.println(phoneBook[contactNum].displayNumber());
				debug++;
			}
			output.close();
		}	catch (Exception missing) {
			outputLabel.setText("Error in printNew(): " + debug);
		}
	}
	public int returnLast()	{
		return contactNumber - 1; //Returns the contact number, not the array number. 
	}
	private void addNewEntry()	{ //used by Handler.
		PhoneEntry entry = new PhoneEntry(FName, SName, PNum);
		phoneBook[contactNumber] = entry;
		contactNumber++;
	}
	public void addNew()	{ //used by main programme. 
		this.addNewEntry();
		this.printNew();
	}
	Boolean search(PhoneEntry searchEntryPara)	{
		PhoneEntry searchEntry = searchEntryPara;
		Boolean found = false;
		for (int i = 1; i < contactNumber; i++) {
			if (searchEntry.displayFName().toUpperCase().equals(phoneBook[i].displayFName().toUpperCase())
				&& searchEntry.displaySName().toUpperCase().equals(phoneBook[i].displaySName().toUpperCase())
				&& searchEntry.displayNumber().toUpperCase().equals(phoneBook[i].displayNumber().toUpperCase()))	{
				found = true;
			}
		}
		return found;
	}
}
