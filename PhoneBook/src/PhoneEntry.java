public class PhoneEntry {
	String Name;
	String PNumber;
	int ContactNumber;
	
	PhoneEntry(String namePara, String numPara, int contactNumPara)
	{
		Name = namePara;
		PNumber = numPara;
		ContactNumber = contactNumPara;
	}
	int displayContactNumber()
	{
		return ContactNumber;
	}
	String displayName()
	{
		return Name.trim();
	}
	String displayNumber()
	{
		return PNumber.trim();
	}
}
