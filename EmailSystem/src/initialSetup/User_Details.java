package initialSetup;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class User_Details {
	private File setupFile = new File(".//Setup.txt");
	public String getSignature()	{
		Scanner sigScanner;
		String toReturn = "";
		try {
			sigScanner = new Scanner(setupFile);
			String currentLine = "";
			while (sigScanner.hasNextLine())	{
				currentLine = sigScanner.nextLine().trim();
				if (currentLine.equals("//Inboxes Start"))	{
					break;
				}	else if (currentLine.equals("//Signature Start"))	{
					continue;
				}	else	{
					toReturn += currentLine;
				}
			}
			sigScanner.close();
			toReturn = toReturn.trim();
		} catch (FileNotFoundException e) {
			toReturn = "Signature not found...";
		}
		toReturn = toReturn.replaceAll(System.getProperty("line.separator"), "<br/>");
		return toReturn.trim();
	}
	public String[] getInboxes()	{
		Scanner sigScanner;
		String[] toReturn = new String[] {""};
		String currentLine = "";
		boolean inboxStart = false;
		int current = 0;
		if (setupFile.exists())	{
			try {
				sigScanner = new Scanner(setupFile);
				int inboxNumber = 0, lineNumber = 0;
				inboxStart = false;
				while (sigScanner.hasNextLine())	{
					if (sigScanner.nextLine().equals("//Inboxes Start"))	{
						inboxStart = true;
					}	else if (inboxStart)	{
						++inboxNumber;
					}
					++lineNumber;
				}
				toReturn = new String[inboxNumber];
				sigScanner.close();
				inboxStart = false;
				sigScanner = new Scanner(setupFile);
				for (int i = 0; i < lineNumber; ++i) {
					currentLine = sigScanner.nextLine();
					if (currentLine.equals("//Inboxes Start"))	{
						inboxStart = true;
					}	else if (inboxStart)	{
						toReturn[current] = currentLine;
						++current;
					}
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		return toReturn;
	}
}
