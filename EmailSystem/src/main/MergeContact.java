package main;

public class MergeContact {
	private String name = "";
	private String studentID = "";
	private String email = "";
	MergeContact(String namePara, String studentIDPara, String emailPara)	{
		name = namePara.trim();
		studentID = studentIDPara.trim();
		email = emailPara.trim();
	}
	public String getName()	{
		return name;
	}
	public String getID()	{
		return studentID;
	}
	public String getEmail()	{
		return email;
	}
}