package main;

public class MergeContact {
	String[] data;
	MergeContact(String[] dataPara)	{
		data = dataPara;
	}
	public String getValue(int toGet)	{
		if (toGet == -1) {
			return "";
		}
		return data[toGet];
	}
}