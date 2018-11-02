package passgen;
import java.io.*;
import java.util.Scanner;
import java.util.Random;
import java.util.Date;

public class PassGenHandler {
	private static String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890!$%^&*()_-+[{]};:'@#~,<.>/?|=\\£ \"";
	private static Random random = new Random();
	private static String decryptWord = "RyanRules";
	private static String firstLine = "";
	private static int encrypt1 = 0;
	private static int encrypt2 = 0;
	private static int encrypt3 = 0;
	private static int encrypt4 = 0;
	private static int totalPasses = 0;
	private static int selected = -1;
	private static PassWord[] passBook = new PassWord[2];
	private static File savedPass = new File("SavedPasses.txt");
	private static PrintWriter errorWriter;
	private static Scanner passScanner;
	private static Date errorDate;
	public static boolean initialise()	{
		try {
			if (savedPass.exists()) {
				passScanner = new Scanner(savedPass);
				firstLine = passScanner.nextLine();
				return true;
				}	else	{
					return false;
				}
			} catch (FileNotFoundException e) {//Should never happen!
				addError("Something impossible happened...");
				return false;
				}
	}
	public static void setSelected(int toSet)	{
		selected = toSet;
	}
	public static void importAll()	{
			while(passScanner.hasNext())	{
				if (passBook.length <= totalPasses) {
					doubleArray();
				}
				passBook[totalPasses] = new PassWord(decrypt(passScanner.nextLine()), decrypt(passScanner.nextLine()));
				totalPasses++;
			}
	}
	public static String exportAll()	{
		PrintWriter passOutput;
		try {
			savedPass.setWritable(true);
			passOutput = new PrintWriter("SavedPasses.txt");
			passOutput.println(encrypt(decryptWord));
			for (int i = 0; i < totalPasses; i++) {
				passOutput.println(encrypt(passBook[i].getName()));
				passOutput.println(encrypt(passBook[i].getPass()));
				}
			passOutput.close();
			savedPass.setWritable(false);
		} catch (FileNotFoundException unknown2) {
			addError("Unknown error: " + unknown2);
		}
		return printAll();
	}
	public static void deleteAll()	{
		passScanner.close();
		if(!(savedPass.delete()))	{
			savedPass.deleteOnExit();
		}
	}
	public static String printAll()	{
		String printAll = "Passwords: \n\r \n\r";
		for (int i = 0; i < totalPasses; i++) {
			printAll += passBook[i].getName() + "\n\r";
			}
		return printAll;
	}
	public static void setKey(String toSet)	{
		encrypt1 = Integer.parseInt(toSet.substring(0,1));
		encrypt2 = Integer.parseInt(toSet.substring(1,2));
		encrypt3 = Integer.parseInt(toSet.substring(2,3));
		encrypt4 = Integer.parseInt(toSet.substring(3,4));
	}
	public static Boolean check(String toCheck)	{
			if (valid(toCheck))	{ //Makes sure the pin is valid before checking it
				setKey(toCheck); //Sets the key to the currently entered pin value
				if (decrypt(firstLine).equals(decryptWord)) { //Pin accepted
					importAll(); //Imports password book. Will only do this if PIN is accepted. 
					retreive(0);
					return true;
				}
			}
		return false;
	}
	private static void setCurrent(int toSet)	{
		if (toSet >= totalPasses)	{
			toSet = totalPasses - 1;
		}	else if (toSet <= 0)	{
			toSet = 0;
		}
		selected = toSet;
		PassGenWindow.displayRetreive(passBook[toSet]);
	}
	public static PassWord retreiveSpecific(int toRetreive)	{
		return passBook[toRetreive];
	}
	public static void retreive(int retreivePara)	{
		switch(retreivePara)	{
		case 1:
			setCurrent(selected + 1);
			break;
		case -1:
			setCurrent(selected - 1);
			break;
		case 3:
			setCurrent(totalPasses);
			break;
		default:
			setCurrent(0);
			break;
		}
	}
	private static void doubleArray()	{
		PassWord[] tempBook = new PassWord[passBook.length*2];
		for (int i=0; i<totalPasses; ++i) {
			tempBook[i] = passBook[i];
		}
		passBook = tempBook;		
	}
	public static void addNew(PassWord toAdd)	{ //adds new password object to end of array
		if (passBook.length <= totalPasses) {
			doubleArray();
		} 
		passBook[totalPasses] = toAdd;
		totalPasses++;
		exportAll();
		retreive(3);
	}
	public static String generateNew(int passLength)	{
		String returnString = "";
			for (int i=0; i<passLength; i++) {
				returnString += characters.charAt(random.nextInt(characters.length()));
			}
		return returnString;
	}
	public static void remove()	{ //Removes a password object and returns the number of removed password
		if (selected >= 0 && totalPasses > 0) {
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
			retreive(-1);
			exportAll();
		}
		if (totalPasses < 1)	{
			selected = -1;
			PassGenWindow.displayRetreive(new PassWord("",""));
		}
	}
	public static int search(PassWord toSearch)	{
		int found = -1;
		for (int i=0; i<totalPasses; i++) {
			if (passBook[i].getName().toUpperCase().equals(toSearch.getName().toUpperCase()))	{
				found = i;
			}
		}
		return found;
	}
	public static void overwrite(PassWord toOverwrite, int location)	{
		passBook[location] = toOverwrite;
		exportAll();
	}
	public static boolean valid(String toCheck)	{
		Boolean valid = true;
		String pinCheck = toCheck.trim();
		if (pinCheck.equals("") || pinCheck.length() != 4 || pinCheck.contains("0"))	{
			valid = false;
		}
		try	{
			Integer.parseInt(pinCheck);
		}	catch (NumberFormatException inval) {
			valid = false;
		}
		return valid;
	}
	private static String encrypt(String encryptPara)	{
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
		return scramble(encryptedString);
	}
	private static String decrypt(String decryptPara) {
		String unscrambled = scramble(decryptPara);
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
	private static String scramble(String scramblePara)	{ //Also unscrambles :D
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
	private static void addError(String toAdd)	{
		try {
			errorWriter = new PrintWriter(new FileWriter("Errors.txt", true));
			errorDate = new Date();
			errorWriter.print("Error at: " + errorDate.toString() + System.getProperty("line.separator"));
			errorWriter.print(toAdd);
			errorWriter.print(System.getProperty("line.separator"));
			errorWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}