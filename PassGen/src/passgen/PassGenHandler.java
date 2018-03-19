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
	private int currentPass = 0;
	private Text outputText;
	private Text keyText;
	private Text passNameText;
	private Text passwordText;
	private PassWord[] passBook = new PassWord[1000];
	private File savedPass = new File("SavedPasses.txt");
	private Scanner passScanner;
	public void initialise(Text keyTextPara, Text outputTextPara, Text passNamePara, Text passwordPara)	{
		keyText = keyTextPara;
		outputText = outputTextPara;
		passNameText = passNamePara;
		passwordText = passwordPara;
		keyText.setVisible(true);
		if (!(savedPass.exists()))	{
			outputText.setText("Please enter a 4-digit encryption key and press 'Submit' \r\n"
					+ "You will have to remember this code!");
		}	else	{
			outputText.setText("Please enter your pin and press 'Submit'");
		}
	}
	private Boolean keyCheck()	{
		if (this.valid())	{
			this.setKey();
			this.importFirst();
			if (this.decrypt(firstLine).equals(decryptWord)) { //Pin accepted
				outputText.setText("PIN accepted!");
				this.importAll();
				return true;
			}	else	{
				outputText.setText("PIN incorrect, please try again.");
				return false;
				//Add limit to number of tries before deletion.
			}//Used to check pin against stored record
		}	else	{
			return false;
		}
	}
	private void importFirst()	{
		try {
			passScanner = new Scanner(savedPass);
			firstLine = passScanner.nextLine();
		} catch (FileNotFoundException unknown1) {
			outputText.setText("Unknown error: " + unknown1);
		}
	}
	private void importAll()	{
			while(passScanner.hasNext())	{
				passBook[totalPasses] = new PassWord(totalPasses, this.decrypt(passScanner.nextLine()), this.decrypt(passScanner.nextLine()));
				totalPasses++;
			}
	}
	public void exportAll()	{
		PrintWriter passOutput;
		try {
			passOutput = new PrintWriter("SavedPasses.txt");
			passOutput.println(this.encrypt(decryptWord));
			for (int i = 0; i < totalPasses; i++) {
				passOutput.println(this.encrypt(passBook[i].returnName()));
				passOutput.println(this.encrypt(passBook[i].returnPass()));
				}
			passOutput.close();
		} catch (FileNotFoundException unknown2) {
			outputText.setText("Unknown error: " + unknown2);
		}
	}
	private void setKey()	{
		encrypt1 = Integer.parseInt(keyText.getText().substring(0,1));
		encrypt2 = Integer.parseInt(keyText.getText().substring(1,2));
		encrypt3 = Integer.parseInt(keyText.getText().substring(2,3));
		encrypt4 = Integer.parseInt(keyText.getText().substring(3,4));
	}
	public Boolean submit()	{
		if ((!(savedPass.exists())) && this.valid())	{
			this.setKey();
			outputText.setText("Pin set to " + keyText.getText());
			this.exportAll();
			return true;
			}
		else	{
			return this.keyCheck();
			}
		}
	private void setCurrent()	{
		passNameText.setText(passBook[currentPass].returnName());
		passwordText.setText(passBook[currentPass].returnPass());
	}
	public PassWord retreive(int toRetreivePara)	{
		if (toRetreivePara < totalPasses && toRetreivePara >= 0) {
			currentPass = toRetreivePara;
			this.setCurrent();
			return passBook[toRetreivePara];
		}	else if (toRetreivePara >= totalPasses)	{
			currentPass = totalPasses - 1;
			return passBook[totalPasses - 1];
		}	else	{
			currentPass = 0;
			return passBook[0];
		}
	}
	public void addNew()	{ //adds new password object to end of array
		if (this.search(passNameText.getText().trim()))	{
			outputText.setText("Name already exists!");
		}	else	{
			passBook[totalPasses] = new PassWord(totalPasses, passNameText.getText(), passwordText.getText());
			totalPasses++;
			this.exportAll();
		}
	}
	public String generateNew()	{
		String returnString = "";
		for (int i=0; i<20; i++) {
			returnString += characters.charAt(random.nextInt(characters.length()));
		}
		return returnString;
	}
	public void remove()	{
		//Removes a password object.
	}
	private boolean search(String nameString)	{
		nameString = nameString.trim();
		Boolean found = false;
		for (int i=0; i<totalPasses; i++) {
			if (passBook[i].returnName().equals(nameString))	{
				found = true;
			}
		}
		return found;
	}
	private boolean valid()	{
		Boolean valid = true;
		String pinCheck = keyText.getText().trim();
		if (pinCheck.equals("") || pinCheck.length() != 4)	{
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
			encryptedString += String.valueOf(Character.toChars(encryptPara.charAt(i) + (encrypt2 / 2)));
			i++;
			if (i < encryptPara.length())	{
				encryptedString += String.valueOf(Character.toChars(encryptPara.charAt(i) + (encrypt3 / 2)));
				i++;
			}
			if (i < encryptPara.length())	{
				encryptedString += String.valueOf(Character.toChars(encryptPara.charAt(i) + (encrypt1 / 2)));
				i++;
			}
			if (i < encryptPara.length())	{
				encryptedString += String.valueOf(Character.toChars(encryptPara.charAt(i) + (encrypt4 / 2)));
				i++;
			}
		}
		return this.scramble(encryptedString);
	}
	private String decrypt(String decryptPara) {
		String unscrambled = this.scramble(decryptPara);
		String decryptedString = "";
		for (int i=0; i<decryptPara.length();)	{
			decryptedString += String.valueOf(Character.toChars(unscrambled.charAt(i) - (encrypt2 / 2)));
			i++;
			if (i < decryptPara.length())	{
				decryptedString += String.valueOf(Character.toChars(unscrambled.charAt(i) - (encrypt3 / 2)));
				i++;
			}
			if (i < decryptPara.length())	{
				decryptedString += String.valueOf(Character.toChars(unscrambled.charAt(i) - (encrypt1 / 2)));
				i++;
			}
			if (i < decryptPara.length())	{
				decryptedString += String.valueOf(Character.toChars(unscrambled.charAt(i) - (encrypt4 / 2)));
				i++;
			}
		}
		return decryptedString;
	}
	private String scramble(String scramblePara)	{
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
