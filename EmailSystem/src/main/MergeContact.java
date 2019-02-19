package main;

public class MergeContact {
	String[] data;
	MergeContact(String[] dataPara)	{
		data = dataPara;
	}
	public String getValue(int toGet)	{
		if (data[toGet] == null) {
			return "";
		}
		return data[toGet];
	}
}