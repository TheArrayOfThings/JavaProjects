package passgen;
import java.io.*;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import java.util.Scanner;
import java.util.Random;

public class PassGenHandler {
	private String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890!$%^&*()_-+[{]};:'@#~,<.>/?|=\\£ \"";
	Random random = new Random();
	private String decryptWord = "RyanRules";
	private String firstLine = "";
	private int encrypt1 = 0;
	private int encrypt2 = 0;
	private int encrypt3 = 0;
	private int encrypt4 = 0;
	private int totalPasses = 0;
	private int tries = 10;
	private Text outputText;
	private Text keyPWText;
	private Text passNameText;
	private Text currentText;
	private PassWord[] passBook = new PassWord[2];
	private File savedPass = new File("SavedPasses.txt");
	private Scanner passScanner;
	public void initialise(Text passNamePara, Text keyPWTextPara, Text outputTextPara, Text currentTextPara)	{
		keyPWText = keyPWTextPara;
		outputText = outputTextPara;
		passNameText = passNamePara;
		currentText = currentTextPara;
		if (!(savedPass.exists()))	{
			outputText.setText("Please enter a 4-digit encryption key and press 'Submit' \r\n"
					+ "You will have to remember this code! \r\n"
					+ "Your PIN cannot contain a zero(0).");
		}	else	{
			try {	
				passScanner = new Scanner(savedPass);
				firstLine = passScanner.nextLine();
			} catch (FileNotFoundException unknown1) {
				outputText.setText("Unknown error: " + unknown1);
			}
			outputText.setText("Please enter your PIN and press 'Submit'");
		}
	}
	public int getTotal()	{
		return totalPasses;
	}
	private void importAll()	{
			while(passScanner.hasNext())	{
				if (passBook.length >= totalPasses) {
					this.doubleArray();
				}
				passBook[totalPasses] = new PassWord(totalPasses, this.decrypt(passScanner.nextLine()), this.decrypt(passScanner.nextLine()));
				totalPasses++;
			}
			passScanner.close();
	}
	public void exportAll()	{
		PrintWriter passOutput;
		try {
			savedPass.setWritable(true);
			passOutput = new PrintWriter("SavedPasses.txt");
			passOutput.println(this.encrypt(decryptWord));
			for (int i = 0; i < totalPasses; i++) {
				passOutput.println(this.encrypt(passBook[i].getName()));
				passOutput.println(this.encrypt(passBook[i].getPass()));
				}
			passOutput.close();
			savedPass.setWritable(false);
		} catch (FileNotFoundException unknown2) {
			outputText.setText("Unknown error: " + unknown2);
		}
	}
	private void setKey()	{
		encrypt1 = Integer.parseInt(keyPWText.getText().substring(0,1));
		encrypt2 = Integer.parseInt(keyPWText.getText().substring(1,2));
		encrypt3 = Integer.parseInt(keyPWText.getText().substring(2,3));
		encrypt4 = Integer.parseInt(keyPWText.getText().substring(3,4));
	}
	public Boolean pinEntry()	{
		if ((!(savedPass.exists())) && this.valid())	{
			this.setKey();
			outputText.setText("Pin set to " + keyPWText.getText());
			this.exportAll();
			return true;
			}
		else	{
			if (this.valid())	{ //Makes sure the pin is valid before checking it
				this.setKey(); //Sets the key to the currently entered pin value
				if (this.decrypt(firstLine).equals(decryptWord)) { //Pin accepted
					outputText.setText("PIN accepted!");
					this.importAll(); //Imports password book. Will only do this if PIN is accepted. 
					return true;
				}	else	{
					--tries;
					if (tries < 1) {
						passScanner.close();
						if (savedPass.delete()) {
							outputText.setText("Tries exceeded: password records deleted. \r/n"
									+ "Please enter a new PIN to create a new password file.");
						}	else	{
							outputText.setText("Tries exceeded: password records not deleted. \r/n"
									+ "Please standby!");
						}
						return false;
					}	else	{
						outputText.setText("PIN incorrect, please try again. \r\n"
								+ "You have " + tries + " tries remaining. \r\n"
										+ "Your saved passwords will be deleted if this number is exceeded.");
						return false;
					}
				}
			}	else	{
				return false;
			}
			}
		}
	private void setCurrent(int toSet)	{
		passNameText.setText(passBook[toSet].getName());
		keyPWText.setText(passBook[toSet].getPass());
		currentText.setText(String.valueOf(toSet));
	}
	public void clear()	{
		passNameText.setText("");
		keyPWText.setText("");
	}
	public void retreive(int toRetreivePara)	{
		if (toRetreivePara < totalPasses && toRetreivePara >= 0 && totalPasses > 0) {
			this.setCurrent(toRetreivePara);
		}	else if (toRetreivePara >= totalPasses && toRetreivePara != 0 && totalPasses > 0)	{
			this.setCurrent(totalPasses - 1);
		}	else if (totalPasses > 0)	{
			this.setCurrent(0);
		}
	}
	private void doubleArray()	{
		PassWord[] tempBook = new PassWord[passBook.length*2];
		for (int i=0; i<totalPasses; ++i) {
			tempBook[i] = passBook[i];
		}
		passBook = tempBook;
	}
	public void addNew()	{ //adds new password object to end of array
		if (passBook.length >= totalPasses) {
			this.doubleArray();
		}
		if (this.search() == 1)	{
			outputText.setText("Password name already in use!");
		}	else if (this.search() == 2)	{
			outputText.setText("Password already in use!");
		}
			else if (passNameText.getText().trim().equals("") || keyPWText.getText().trim().equals(""))	{
			outputText.setText("Please add a name and generate a password!");
		}	else	{
			passBook[totalPasses] = new PassWord(totalPasses, passNameText.getText(), keyPWText.getText());
			totalPasses++;
			outputText.setText("New password added sucessfully!");
			this.exportAll();
		}
	}
	public void generateNew()	{
		String returnString = "";
		if (keyPWText.getText().equals(""))	{
			for (int i=0; i<20; i++) {
				returnString += characters.charAt(random.nextInt(characters.length()));
			}
			keyPWText.setText(returnString);
		}	else	{
			outputText.setText("Please press 'clear' before generating a new password!");
		}
	}
	public void remove(int toRemove)	{ //Removes a password object.
		if (toRemove >= 0 && toRemove <= totalPasses) {
			PassWord[] tempBook = new PassWord[totalPasses];
			passBook[toRemove] = null;
			int j = 0;
			for (int i = 0; i<totalPasses; ++i)	{
				if (passBook[i] != null) {
					tempBook[j] = passBook[i];
					++j;
				}
			}
			passBook = tempBook;
			--totalPasses;
			this.exportAll();
		}	else	{
			outputText.setText("Cannot remove!");
		}
	}
	private int search()	{
		int found = 0;
		for (int i=0; i<totalPasses; i++) {
			if (passBook[i].getName().toUpperCase().equals(passNameText.getText().trim().toUpperCase()))	{
				found = 1;
			}	else if (passBook[i].getPass().toUpperCase().equals(keyPWText.getText().trim().toUpperCase())) {
				found = 2;
			}
		}
		return found;
	}
	private boolean valid()	{
		Boolean valid = true;
		String pinCheck = keyPWText.getText().trim();
		if (pinCheck.equals("") || pinCheck.length() != 4 || pinCheck.contains("0"))	{
			outputText.setText("Please enter a valid pin.");
			valid = false;
		}
		try	{
			Integer.parseInt(pinCheck);
		}	catch (NumberFormatException inval) {
			valid = false;
		}
		return valid;
	}
	private String encrypt(String encryptPara)	{
		String encryptedString = "";
		for (int i=0; i<encryptPara.length();)	{
			encryptedString += String.valueOf(Character.toChars(encryptPara.charAt(i) + (encrypt2)));
			i++;
			if (i < encryptPara.length())	{
				encryptedString += String.valueOf(Character.toChars(encryptPara.charAt(i) + (encrypt3)));
				i++;
			}
			if (i < encryptPara.length())	{
				encryptedString += String.valueOf(Character.toChars(encryptPara.charAt(i) + (encrypt1)));
				i++;
			}
			if (i < encryptPara.length())	{
				encryptedString += String.valueOf(Character.toChars(encryptPara.charAt(i) + (encrypt4)));
				i++;
			}
		}
		return this.scramble(encryptedString);
	}
	private String decrypt(String decryptPara) {
		String unscrambled = this.scramble(decryptPara);
		String decryptedString = "";
		for (int i=0; i<decryptPara.length();)	{
			decryptedString += String.valueOf(Character.toChars(unscrambled.charAt(i) - (encrypt2)));
			i++;
			if (i < decryptPara.length())	{
				decryptedString += String.valueOf(Character.toChars(unscrambled.charAt(i) - (encrypt3)));
				i++;
			}
			if (i < decryptPara.length())	{
				decryptedString += String.valueOf(Character.toChars(unscrambled.charAt(i) - (encrypt1)));
				i++;
			}
			if (i < decryptPara.length())	{
				decryptedString += String.valueOf(Character.toChars(unscrambled.charAt(i) - (encrypt4)));
				i++;
			}
		}
		return decryptedString;
	}
	private String scramble(String scramblePara)	{ //Also unscrambles :D
		String scrambledString = "";
		String [] stringChars = scramblePara.split("");
		int highIndex = (stringChars.length - 1);
		int currentIndex = 0;
		while (currentIndex <= highIndex) {
			if (currentIndex % 2 == 0)	{
				scrambledString += stringChars[currentIndex];
				currentIndex++;
			}	else	{
				scrambledString += stringChars[highIndex];
				highIndex--;
				}
			}
		return scrambledString;
	}
}