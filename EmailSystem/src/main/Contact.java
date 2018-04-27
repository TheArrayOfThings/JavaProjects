package main;

public class Contact {
	private String name = "";
	private String studentID = "";
	private String email = "";
	Contact(String namePara, String studentIDPara, String emailPara)	{
		name = namePara;
		studentID = studentIDPara;
		email = emailPara;
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
