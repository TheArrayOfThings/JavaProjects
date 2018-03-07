public class PhoneEntry {
	String FName;
	String SName;
	String PNumber;	
	PhoneEntry(String FNamePara, String SNamePara, String numPara)	{
		FName = FNamePara;
		SName = SNamePara;
		PNumber = numPara;
	}
	String textProcess (String inputString)	{
		inputString = inputString.toLowerCase().trim();
		return String.valueOf(inputString.charAt(0)).toUpperCase().charAt(0) + inputString.substring(1);
	}
	String displayFullName()	{
		return (this.textProcess(FName) + " " + this.textProcess(SName));
	}
	String displayFName()	{
		return this.textProcess(FName);
	}
	String displaySName()	{
		return this.textProcess(SName);
	}
	String displayNumber()	{
		return this.textProcess(PNumber);
	}
}