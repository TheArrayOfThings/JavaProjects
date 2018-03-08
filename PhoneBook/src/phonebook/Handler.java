package phonebook;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;
import org.eclipse.swt.widgets.Text;

public class Handler {
	int contactNumber = 1;
	int tempContact = 0;
	String returnString = "";
	Text outputText;
	Text forenameText;
	Text surnameText;
	Text numText;
	Text contactText;
	PhoneEntry[] phoneBook = new PhoneEntry[2];
	File phoneBookFile = new File ("Phonebook.txt");
	boolean exists = phoneBookFile.exists();
	PrintWriter output;
	void initialise(Text outputTextPara, Text forenamePara, Text surnamePara, Text numPara, Text contactPara)	{ //This is the import function for the phoneBook. Should be at the bottom of the code where used. 
		outputText = outputTextPara; //This sets the text boxes for later use by the handler. 
		forenameText = forenamePara;
		surnameText = surnamePara;
		numText = numPara;
		contactText = contactPara;
		if (!(exists))	{
			try	{
				String instructions = "The'submit' button is used to store changes and add new contacts.\r\n" +
						"\r\nYou can use the 'Previous' and 'Next' buttons to browse through your contacts.\r\n" + 
						"\r\nThe 'Remove' button will remove the currently retreived contact.\r\n" + 
						"\r\nThe 'Clear' button clears the currently retreived contact.\r\n";
				PrintWriter instructionsOutput = new PrintWriter ("Phonebook Instructions.txt");
				PrintWriter output = new PrintWriter ("Phonebook.txt"); //create text file if it does not exist
				output.close(); //close it as not used here.
				instructionsOutput.print(instructions);
				instructionsOutput.close();
				outputText.setText("This appears to be your first time running the programme!\r\n\r\n" + instructions + 
						"\r\nThese instructions have been stored in 'Phonebook Instructions.txt");
			}	catch(FileNotFoundException unknownError1)	{
				outputText.setText("Unknown error: " + unknownError1);
			}

		}	else	{
			try	{
			Scanner bookScanner = new Scanner (phoneBookFile);
			outputText.setText("Phone book importing..."); //This is never seen, as it happens too quickly.
			while (bookScanner.hasNext())	{
				forenameText.setText(bookScanner.nextLine());
				surnameText.setText(bookScanner.nextLine());
				numText.setText(bookScanner.nextLine());
				this.addNewEntry(); //Adds each new entry without printing it to 'PhoneBook.txt' (program would never end if it did).
				}
			this.clearAll();
			this.displayAll(); //This triggers if a blank 'PhoneBook.txt' file is found. Bug?
			bookScanner.close(); //Only time we ever need bookScanner. 
			}	catch (FileNotFoundException importFailed) {
				outputText.setText("Import failed! The phonebook appears to be corrupt : " + importFailed);
				try {
					output = new PrintWriter("PhoneBook.txt");
				} catch (FileNotFoundException unknownError2) {
					outputText.setText("Unknown error: " + unknownError2);
				}
				output.println("");
				output.close();
			}
		}
	}
	Boolean hasBlank()	{
		if (forenameText.getText().equals(""))	{ //checks for blank forename
			outputText.setText("Please enter a Forename");
			return true;
		}
		else if (surnameText.getText().equals(""))	{ //checks for blank surname
			outputText.setText("Please enter a Surname");
			return true;
		}
		else if (numText.getText().equals(""))	{ //checks for blank number
			outputText.setText("Please enter a phone number");
			return true;
		}
		else	{
			return false;
		}
	}
	void displayAll()	{ //Displays everything to the output window
		returnString = "";
		for (int contactCount = 1; contactCount < contactNumber; contactCount++)	{
			returnString += "Contact ";
			returnString += contactCount + ":";
			returnString += "\r\n";
			returnString += (phoneBook[contactCount].displayFullName());
			returnString += "\r\n \r\n";
		}
		if (contactNumber <= 1) {
			outputText.setText("Your phonebook appears to be blank. \r\n" + 
					"\r\nPlease refer to 'Phonebook Instructions.txt' for operating instructions.");
		}
		else	{
			outputText.setText(returnString);
		}
	}
	void clearAll()	{
		forenameText.setText("");
		surnameText.setText("");
		numText.setText("");
		contactText.setText("");
	}
	void retreiveFirst()	{
		contactText.setText("1");
		this.retreiveContact(0);
	}
	void doublePB()	{
		PhoneEntry[] tempPhoneBook = new PhoneEntry[contactNumber*2];
		for (int i = 1; i < contactNumber; i++)	{
			tempPhoneBook[i] = phoneBook[i];
		}
		phoneBook = tempPhoneBook;
	}
	void deleteEntry()	{
		try	{
			tempContact = Integer.parseInt(contactText.getText());
			if (!(Integer.parseInt(contactText.getText()) == 0))	{
				phoneBook[tempContact] = null;
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
				this.displayAll();
				this.retreiveFirst();
			}
		}	catch (Exception invalidToDelete)	{
			outputText.setText("Please retreive a valid contact to remove!");
		}
	}
	void retreiveContact(int modifier)	{
		if (contactText.getText().equals(""))	{
			this.displayAll();
			this.retreiveFirst();
		}
		else	{
			try	{
				forenameText.setText(phoneBook[Integer.parseInt(contactText.getText()) - modifier].displayFName());
				surnameText.setText(phoneBook[Integer.parseInt(contactText.getText()) - modifier].displaySName());
				numText.setText(phoneBook[Integer.parseInt(contactText.getText()) - modifier].displayNumber());
				if (modifier != 0) {
					contactText.setText(String.valueOf(Integer.parseInt(contactText.getText()) - modifier));
				}
				this.displayAll();
			}
			catch (Exception outOfBounds)	{
				if (modifier < 0) {
					contactText.setText(String.valueOf(contactNumber - 1));
					this.retreiveContact(0);
				}
				else if (modifier > 0) {
					this.retreiveFirst();
				}
				else	{
					outputText.setText("Not a valid contact!");
					this.clearAll();
				}
			}
		}
	}
	PhoneEntry currentEntry()	{
		return new PhoneEntry(forenameText.getText(), surnameText.getText(), numText.getText());
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
			outputText.setText("Save error: \r\n" + debug + "\r\n" + missing);
		}
	}
	private void addNewEntry()	{ //used by Handler.
		if (contactNumber >= phoneBook.length)	{
			this.doublePB();
		}
		phoneBook[contactNumber] = this.currentEntry();
		contactNumber++;
	}
	public void submit()	{ //used by main programme. 
		if (contactText.getText().equals(""))	{
			try	{
				if (this.hasBlank())	{
				}	else if	(this.search())	{
					outputText.setText("Contact already found!");
				}	else if(!(this.validNumber()))	{
					outputText.setText("Phone number is not valid!");
				}
				else	{
					this.addNewEntry();
					this.printNew();
					this.displayAll();
					this.clearAll();
				}
			}	catch (Exception invalidNum)	{
				outputText.setText("Invalid something something: " + invalidNum);
			}
		}	
		else	{
			if (!(this.search()))	{
				phoneBook[Integer.parseInt(contactText.getText())] = this.currentEntry();
				this.printNew();
				outputText.setText("Contact changes stored!");
			}
		}
	}
	Boolean validNumber()	{
		try	{
			Double.parseDouble(numText.getText());
			return true;
		}	catch (NumberFormatException invalid)	{
			return false;
		}
	}
	Boolean search()	{
		Boolean found = false;
		for (int i = 1; i < contactNumber; i++) {
			if (forenameText.getText().toUpperCase().trim().equals(phoneBook[i].displayFName().toUpperCase())
				&& numText.getText().toUpperCase().trim().equals(phoneBook[i].displayNumber().toUpperCase()))	{
				found = true;
			}
		}
		return found;
	}
}
