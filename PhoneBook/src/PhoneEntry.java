public class PhoneEntry {
	String FName;
	String SName;
	String PNumber;
	int ContactNumber;
	
	PhoneEntry(String FNamePara, String SNamePara, String numPara)	{
		FName = FNamePara.trim();
		SName = SNamePara.trim();
		PNumber = numPara.trim();
	}
	String displayFullName()	{
		return (FName + " " + SName).trim();
	}
	String displayFName()	{
		return FName.trim();
	}
	String displaySName()	{
		return SName.trim();
	}
	String displayNumber()	{
		return PNumber.trim();
	}
}