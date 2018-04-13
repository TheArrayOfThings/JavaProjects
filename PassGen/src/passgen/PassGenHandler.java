package passgen;
import java.io.*;
import org.eclipse.swt.widgets.Text;
import java.util.Scanner;
import java.util.Random;
import java.util.concurrent.*;

public class PassGenHandler {
	private String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890!$%^&*()_-+[{]};:'@#~,<.>/?|=\\£ \"";
	Random random = new Random();
	private String decryptWord = "RyanRules";
	private String firstLine = "";
	private int encrypt1 = 0, encrypt2 = 0, encrypt3 = 0, encrypt4 = 0, totalPasses = 0, disabled = 0;
	private boolean login = false;
	private int tries = 10;
	private Text outputText, keyPWText, passNameText;
	private int selected = -1;
	private PassWord[] passBook = new PassWord[2];
	private File savedPass = new File("SavedPasses.txt");
	private Scanner passScanner;
	public void initialise(Text passNamePara, Text keyPWTextPara, Text outputTextPara)	{
		keyPWText = keyPWTextPara;
		outputText = outputTextPara;
		passNameText = passNamePara;
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
				passBook[totalPasses] = new PassWord(this.decrypt(passScanner.nextLine()), this.decrypt(passScanner.nextLine()));
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
			this.printAll();
			passOutput.close();
			savedPass.setWritable(false);
		} catch (FileNotFoundException unknown2) {
			outputText.setText("Unknown error: " + unknown2);
		}
	}
	private void printAll()	{
		String printAll = "Passwords: \n\r \n\r";
		for (int i = 0; i < totalPasses; i++) {
			printAll += passBook[i].getName() + "\n\r";
			}
		outputText.setText(printAll);
	}
	private void setKey()	{
		encrypt1 = Integer.parseInt(keyPWText.getText().substring(0,1));
		encrypt2 = Integer.parseInt(keyPWText.getText().substring(1,2));
		encrypt3 = Integer.parseInt(keyPWText.getText().substring(2,3));
		encrypt4 = Integer.parseInt(keyPWText.getText().substring(3,4));
	}
	public Boolean submit()	{
		if (login == true) {
			this.addNew();
			return true;
		}	if ((!(savedPass.exists())) && this.valid() && disabled != 1)	{
			this.setKey();
			outputText.setText("Pin set to " + keyPWText.getText());
			this.exportAll();
			this.retreive(0);
			login = true;
			return true;
			}	else	{
			if (this.valid())	{ //Makes sure the pin is valid before checking it
				this.setKey(); //Sets the key to the currently entered pin value
				if (this.decrypt(firstLine).equals(decryptWord)) { //Pin accepted
					outputText.setText("PIN accepted!");
					this.importAll(); //Imports password book. Will only do this if PIN is accepted. 
					this.retreive(0);
					login = true;
					return true;
				}	else if (disabled == 0)	{
					--tries;
					if (tries < 1) {
						passScanner.close();
						savedPass.delete();
						savedPass.deleteOnExit();
						outputText.setText("Tries exceeded: password records deleted. \r\n"
								+ "This program will terminate in 10 seconds.");
						disabled = 1;
						new Thread( new Runnable() {
							public void run()  {
								try {
									TimeUnit.SECONDS.sleep(10);
									} catch (InterruptedException e) {
										e.printStackTrace();
										}
								System.exit(1);
								}
							} ).start();
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
		return false;
	}
	private void setCurrent(int toSet)	{
		if (toSet >= totalPasses)	{
			toSet = totalPasses - 1;
		}	else if (toSet < 0)	{
			toSet = 0;
		}
		passNameText.setText(passBook[toSet].getName());
		keyPWText.setText(passBook[toSet].getPass());
		selected = toSet;
	}
	public void clear()	{
		passNameText.setText("");
		keyPWText.setText("");
		selected = -1;
	}
	public void retreive(int retreivePara)	{
		if (totalPasses <= 0) {
			this.clear();
			return;
		}
		switch(retreivePara)	{
		case 1:
			this.setCurrent(selected + 1);
			break;
		case -1:
			this.setCurrent(selected - 1);
			break;
		case 3:
			this.setCurrent(totalPasses);
			break;
		default:
			this.setCurrent(0);
			break;
		}
		this.printAll();
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
			passBook[totalPasses] = new PassWord(passNameText.getText(), keyPWText.getText());
			totalPasses++;
			outputText.setText("New password added sucessfully!");
			this.exportAll();
			this.retreive(3);
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
	public void remove()	{ //Removes a password object.
		if (selected >= 0 && selected <= totalPasses) {
			PassWord[] tempBook = new PassWord[totalPasses];
			passBook[selected] = null;
			int j = 0;
			for (int i = 0; i<totalPasses; ++i)	{
				if (passBook[i] != null) {
					tempBook[j] = passBook[i];
					++j;
				}
			}
			passBook = tempBook;
			--totalPasses;
			this.retreive(0);
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