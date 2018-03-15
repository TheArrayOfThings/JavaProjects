package passgen;
import java.io.*;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import java.util.Scanner;

public class PassGenHandler {
	private String decryptWord = "RyanRules";
	private String firstLine = "";
	private int encrypt1 = 0;
	private int encrypt2 = 0;
	private int encrypt3 = 0;
	private int encrypt4 = 0;
	private int totalPasses = 0;
	private Text outputText;
	private Text keyText;
	private Text passNameText;
	private Text passwordText;
	private PassWord[] passBook = new PassWord[1000];
	private File savedPass = new File("SavedPasses.txt");
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
			this.importAll();
			outputText.setText("Please enter your pin and press 'Submit'");
		}
	}
	private Boolean keyCheck()	{ //Used to check pin against stored record
		this.setKey();
		if (this.decrypt(firstLine).equals(decryptWord)) { //Pin accepted
			outputText.setText("PIN accepted!");
			return true;
		}	else	{
			outputText.setText("PIN incorrect, please try again.");
			return false;
			//Add limit to number of tries before deletion.
		}
	}
	private void importAll()	{
		Scanner passScanner;
		try {
			passScanner = new Scanner(savedPass);
			firstLine = passScanner.nextLine();
		} catch (FileNotFoundException unknown1) {
			outputText.setText("Unknown error: " + unknown1);
		}
	}
	public void exportAll()	{
		PrintWriter passOutput;
		try {
			passOutput = new PrintWriter("SavedPasses.txt");
			this.setKey(); //Remove This!
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
		if (!(savedPass.exists()))	{
			this.setKey();
			outputText.setText("Pin set to " + keyText.getText());
			this.exportAll();
			return true;
			}
		else	{
			return this.keyCheck();
			}
		}
	public void test()	{
		this.importAll();
		this.setKey();
		outputText.setText("Encrypted: " + firstLine + "\r\nDecrypted: " + this.decrypt(firstLine));
		
	}
	private void retreive()	{
		//Retreives 
	}
	public void addNew()	{
		//Adds new password object.
	}
	public void remove()	{
		//Removes a password object.
	}
	public boolean search(PassWord searchObject)	{
		return true;
		//Searches through objects for one in particular. 
		//Want to flag if name or password matches?
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
