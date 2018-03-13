package passgen;

public class PassWord {
	private String password = "";
	private String pName = "";
	PassWord (String passwordPara, String namePara, String encryptionKeyPara)	{
		password = passwordPara;
		pName = namePara;
	}
	public String returnName()	{
		return pName;
	}
	public String returnPass()	{
		return password;
	}
}
