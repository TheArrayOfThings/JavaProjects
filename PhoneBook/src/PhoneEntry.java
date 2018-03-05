public class PhoneEntry {
	String FName;
	String SName;
	String PNumber;	
	PhoneEntry(String FNamePara, String SNamePara, String numPara)	{
		FName = FNamePara.trim();
		SName = SNamePara.trim();
		PNumber = numPara.trim();
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