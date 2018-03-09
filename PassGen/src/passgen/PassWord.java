package passgen;

public class PassWord {
	private String password = "";
	private String pName = "";
	private String encryptionKey = ""; 
	PassWord (String passwordPara, String namePara, String encryptionKeyPara)	{
		password = passwordPara;
		pName = namePara;
		encryptionKey = encryptionKeyPara;
	}
	public String returnName()	{
		return pName;
	}
	public String returnPass()	{
		return password;
	}
	public String returnEncrypt()	{
		return encryptionKey;
	}
}
