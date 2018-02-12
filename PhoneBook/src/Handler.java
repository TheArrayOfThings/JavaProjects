import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;
import org.eclipse.swt.widgets.Label;

public class Handler {
	String searchName = "";
	int contactNumber = 1;
	int retreiveContact = 0;
	int intComplete = 0;
	String FName = "";
	String SName = "";
	String PNum = "";
	Label outputLabel;
	String currentName;
	int editContactNumber = 1;
	int searchContactNumber = 1;
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
			intComplete = 1; //Used to denote than import is complete (prevents duplication of imported contacts. 
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
				this.addNew();
			}
			intComplete = 1; 
			bookScanner.close();
			outputLabel.setText("Phone book imported successfully!");
		}
	}
	void editContact(PhoneEntry editEntryPara, int editContactPara)	{
		editContactNumber = editContactPara;
		phoneBook[editContactNumber] = editEntryPara;
	}
	void displayAll()	{
		returnString = "";
		for (int contactCount = 1; contactCount < contactNumber; contactCount++)	{
			returnString += "Contact: \n";
			returnString += (phoneBook[contactCount].displayContactNumber());
			returnString += "\n";
			returnString += (phoneBook[contactCount].displayFullName());
			returnString += "\n \n";
		}
		outputLabel.setText(returnString);
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
		if (intComplete == 1)	{
			try	{
				output = new PrintWriter(new FileWriter("PhoneBook.txt", true));
				output.println(FName);
				output.println(SName);
				output.println(PNum);
				output.close();
			}	catch (Exception missing) {
				outputLabel.setText("Error in addNew(): " + missing);
			}
		}
	}
	void addNew()	{
		PhoneEntry entry = new PhoneEntry(FName, SName, PNum, contactNumber);
		phoneBook[contactNumber] = entry;
		this.printNew();
		contactNumber++;
	}
}
