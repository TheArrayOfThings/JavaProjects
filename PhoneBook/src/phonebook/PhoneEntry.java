package phonebook;

public class PhoneEntry {
	String FName;
	String SName;
	String PNumber;	
	PhoneEntry(String FNamePara, String SNamePara, String numPara)	{
		FName = this.textProcess(FNamePara);
		SName = this.textProcess(SNamePara);
		PNumber = this.textProcess(numPara);
	}
	String textProcess (String inputString)	{
		inputString = inputString.toLowerCase().trim();
		return String.valueOf(inputString.charAt(0)).toUpperCase().charAt(0) + inputString.substring(1);
	}
	String displayFullName()	{
		return (FName + " " + SName);
	}
	String displayFName()	{
		return FName;
	}
	String displaySName()	{
		return SName;
	}
	String displayNumber()	{
		return PNumber;
	}
}