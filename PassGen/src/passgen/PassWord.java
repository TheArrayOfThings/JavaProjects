package passgen;

public class PassWord {
	private String password = "";
	private String pName = "";
	PassWord (String namePara, String passwordPara)	{
		pName = namePara;
		password = passwordPara;
	}
	public String returnName()	{
		return pName;
	}
	public String returnPass()	{
		return password;
	}
}
