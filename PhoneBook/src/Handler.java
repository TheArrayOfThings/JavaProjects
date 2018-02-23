import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;
import org.eclipse.swt.widgets.Text;

public class Handler {
	int contactNumber = 1;
	int retreiveContact = 0;
	String FName = "";
	String SName = "";
	String PNum = "";
	String returnString = "";
	String[] retreiveArray = new String[3];
	Text outputText;
	PhoneEntry[] phoneBook = new PhoneEntry[2];
	File phoneBookFile = new File ("Phonebook.txt");
	boolean exists = phoneBookFile.exists();
	PrintWriter output;
	void setOutput(Text outputTextPara) {
		outputText = outputTextPara;
	}
	void initialise() throws IOException	{ //This is the import function for the phoneBook.
		if (!(exists))	{
			PrintWriter output = new PrintWriter ("Phonebook.txt"); //create text file
			output.close(); //close it as not used here.
			outputText.setText("This appears to be your first time running the programme..");
		}	else	{
			Scanner bookScanner = new Scanner (phoneBookFile);
			outputText.setText("Phone book importing..."); //This is never seen, as it happens too quickly.
			while (bookScanner.hasNext())	{
				if (bookScanner.hasNextLine())	{ //first line should always be the first person's forename. 
					FName = bookScanner.nextLine();
				}
				if (bookScanner.hasNextLine())	{ //Ect Ect. 
					SName = bookScanner.nextLine();
				}
				if (bookScanner.hasNextLine())	{
					PNum = bookScanner.nextLine();
				}
				this.addNewEntry(); //Adds each new entry without printing it to 'PhoneBook.txt' (program would never end if it did).
			}
			bookScanner.close(); //Only time we ever need bookScanner. 
			outputText.setText("Phone book imported successfully!"); //This triggers if a blank 'PhoneBook.txt' file is found. Bug?
		}
	}
	Boolean hasBlank(PhoneEntry checkEntry)	{
		if (checkEntry.displayFName().equals(""))	{ //checks for blank forename
			outputText.setText("Please enter a Forename");
			return true;
		}
		else if (checkEntry.displaySName().equals(""))	{ //checks for blank surname
			outputText.setText("Please enter a Surname");
			return true;
		}
		else if (checkEntry.displayNumber().equals(""))	{ //checks for blank number
			outputText.setText("Please enter a phone number");
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
	String displayAll()	{ //Displays everything to the output window
		returnString = "";
		for (int contactCount = 1; contactCount < contactNumber; contactCount++)	{
			returnString += "Contact ";
			returnString += contactCount + ":";
			returnString += "\n";
			returnString += (phoneBook[contactCount].displayFullName());
			returnString += "\n \n";
		}
		return returnString;
	}
	void doublePB()	{
		PhoneEntry[] tempPhoneBook = new PhoneEntry[contactNumber*2];
		for (int i = 1; i < contactNumber; i++)	{
			tempPhoneBook[i] = phoneBook[i];
		}
		phoneBook = tempPhoneBook;
	}
	void deleteEntry(int entryDelPara)	{
		if (!(entryDelPara == 0))	{
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
	}
	void setCurrent(String FNamePara, String SNamePara, String NumPara)	{
		FName = FNamePara;
		SName = SNamePara;
		PNum = NumPara;
	}
	PhoneEntry retreiveContact(Integer contactPara)	{
		return phoneBook[contactPara];
	}
	void printNew()	{
		String debug = "'PhoneBook.txt' PrintWriter failed to open! Why? We don't know!";
		try	{
			output = new PrintWriter("PhoneBook.txt");
			for (int contactNum = 1; contactNum < contactNumber; contactNum++)	{
				debug = "Displaying first name failed...";
				output.println(phoneBook[contactNum].displayFName());
				debug = "Displaying surname failed...";
				output.println(phoneBook[contactNum].displaySName());
				debug = "Displaying phone number failed...";
				output.println(phoneBook[contactNum].displayNumber());
				debug = "For loop completed sucessfully.. But closing the output seems to have failed :(";
			}
			output.close();
		}	catch (Exception missing) {
			outputText.setText("Save error: \n" + debug + "\n" + missing);
		}
	}
	public int returnLast()	{
		return contactNumber - 1; //Returns the contact number, not the array number. 
	}
	private void addNewEntry()	{ //used by Handler.
		PhoneEntry entry = new PhoneEntry(FName, SName, PNum);
		if (contactNumber >= phoneBook.length)	{
			this.doublePB();
		}
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
