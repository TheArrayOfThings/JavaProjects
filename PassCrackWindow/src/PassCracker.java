import java.util.Random;
import java.io.*;
class PassObject	{
	String guess = "";
	double time = 0;
	double tries = 0;
	PassObject(String guessPara, double timePara, double countPara)	{
		guess = guessPara;
		time = timePara;
		tries = countPara;
	}
	public String getGuess()	{
		return guess;
	}
	public double getTime()	{
		return (time/1000);
	}
	public double getTries()	{
		return tries;
	}
}
class PassCracker	{
	String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890!$%^&*()_-+[{]};:'@#~,<.>/?|=\\£ ";
	long triesCount = 0;
	String password = "";
	String guess = "";
	double startTime = 0;
	double endTime = 0;
	double totalTime = 0;
	Random random = new Random();
	public PassObject crack(String passwordPara) throws IOException	{
		guess = "";
		triesCount = 0;
		password = passwordPara;
		//output.println("Password to guess: " + password);
		startTime = System.currentTimeMillis();
		while (!(guess.equals(password)))	{
			guess = "";
			triesCount++;
			while (guess.length() != password.length())	{
				guess += chars.charAt(random.nextInt(chars.length()));
			}
		}
		endTime = System.currentTimeMillis();
		totalTime = (endTime - startTime);
		PrintWriter output= new PrintWriter(new FileWriter("PassCrack_Output.txt", true));
		output.println("The password was: " + guess);
		output.println("The program took " + (totalTime / 1000) + " seconds to complete.");
		output.println("This program took " + triesCount + " tries to guess the password.");
		output.println("");
		output.close();
		PassObject passResults = new PassObject(guess, totalTime, triesCount);
		return passResults;
	}
}