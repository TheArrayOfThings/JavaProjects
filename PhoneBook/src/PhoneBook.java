public class PhoneBook {
	PhoneEntry[] phoneBook = new PhoneEntry[1000];
	String currentName;
	String searchName;;
	int contactNumber = 1;
	int editContactNumber = 1;
	int arrayNumber = 0;
	int searchContactNumber = 1;
	String returnString = "";
	
	void addContact(PhoneEntry entryPara)	{
		phoneBook[contactNumber] = entryPara;
		contactNumber++;
	}
	void editContact(PhoneEntry editEntryPara, int editContactPara)	{
		editContactNumber = editContactPara;
		phoneBook[editContactNumber] = editEntryPara;
	}
	int displayContactNumber(int arrayContactNumber)	{
		arrayNumber = arrayContactNumber;
		return phoneBook[arrayNumber].displayContactNumber();
	}
	String displayName(int arrayNumName)	{
		arrayNumber = arrayNumName;
		return phoneBook[arrayNumber].displayName();
	}
	String displayNumber(int arrayNum)	{
		arrayNumber = arrayNum;
		return phoneBook[arrayNumber].displayNumber();
	}
	String displayAll()	{
		for (int contactCount = 1; contactCount < contactNumber; contactCount++)	{
			returnString += "\n";
			returnString += "Contact: \n";
			returnString += (phoneBook[contactCount].displayContactNumber());
			returnString += "\n";
			returnString += (phoneBook[contactCount].displayName());
			returnString += "\n";
			returnString += "Mobile: ";
			returnString += "\n";
			returnString += (phoneBook[contactCount].displayNumber());
		}
		return returnString;
	}
	int searchName(String searchNamePara)	{
		searchContactNumber = 1;
		searchName = searchNamePara;
		while ((!(searchName.equals(currentName))) && (searchContactNumber < contactNumber))	{
			currentName = phoneBook[searchContactNumber].displayName().trim().toLowerCase();
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
}