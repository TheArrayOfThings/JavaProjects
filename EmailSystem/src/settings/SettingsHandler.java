package settings;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SettingsHandler {
	private File settingsFile = new File("Settings.txt");
	private String[] settings;
	private Scanner settingScanner;
	public SettingsHandler()	{
		if (settingsFile.exists())	{
			importSettings();
		}	else	{
			setDefaults();
			exportSettings();
		}
	}
	private void importSettings()	{
		List<String> tempSettings = new ArrayList<String>();
		try {
			settingScanner = new Scanner(settingsFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			exportSettings();
		}
		while (settingScanner.hasNextLine())	{
			tempSettings.add(settingScanner.nextLine());
		}
		settings = new String[tempSettings.size()];
		int tempCount = 0;
		for (String eachString: tempSettings)	{
			settings[tempCount] = eachString;
			++tempCount;
		}
		
	}
	public void exportSettings()	{
		//Exports current settings.
		try {
			PrintWriter output = new PrintWriter(settingsFile);
			for (String eachString: settings)	{
				output.println(eachString);
			}
			output.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	private void setDefaults()	{
		//Sets default settings.
		settings = new String[2];
		settings[0] = "Ignore Filters?:true";
		settings[1] = "Add Student IDs?:true";
	}
	public boolean getSetting(String settingName)	{
		for (String eachString: settings)	{
			if (eachString.substring(0, eachString.indexOf(":")).equals(settingName))	{
				return Boolean.parseBoolean(eachString.substring(eachString.indexOf(":") + 1, eachString.length()));
			}
		}
		return false;
	}
	public boolean setSetting(String settingName, boolean toSet)	{
		int forCount = 0;
		for (String eachString: settings)	{
			if (eachString.substring(0, eachString.indexOf(":")).equals(settingName))	{
				settings[forCount] = settingName + ":" + toSet;
				exportSettings();
				return true;
			}
			++forCount;
		}
		return false;
	}
	public void printSettings()	{
		for (String eachString: settings)	{
			System.out.println("Name:" + eachString.substring(0, eachString.indexOf(":")));
			System.out.println("Value:" + Boolean.parseBoolean(eachString.substring(eachString.indexOf(":"), eachString.length())));
		}
	}
}
