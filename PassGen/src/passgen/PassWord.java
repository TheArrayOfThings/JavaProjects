package passgen;

public class PassWord {
	private String password = "";
	private String pName = "";
	PassWord (String namePara, String passwordPara)	{
		pName = namePara.trim();
		password = passwordPara.trim();
	}
	public String getName()	{
		return pName;
	}
	public String getPass()	{
		return password;
	}
}
