public class PhoneEntry {
	String FName;
	String SName;
	String PNumber;
	int ContactNumber;
	
	PhoneEntry(String FNamePara, String SNamePara, String numPara, int contactNumPara)	{
		FName = FNamePara;
		SName = SNamePara;
		PNumber = numPara;
		ContactNumber = contactNumPara;
	}
	int displayContactNumber()	{
		return ContactNumber;
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
