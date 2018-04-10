package passgen;

public class PassWord {
	private int index = 0;
	private String password = "";
	private String pName = "";
	PassWord (int indexPara, String namePara, String passwordPara)	{
		index = indexPara;
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
