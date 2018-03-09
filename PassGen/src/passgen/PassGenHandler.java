package passgen;
import java.io.*;
import java.util.Random;
import java.io.PrintWriter;

public class PassGenHandler {
	Boolean storeExists = false;
	private String encryptionKey = "1483";
	private PassWord[] passBook = new PassWord[1000];
	private int totalPasses = 0;
	private void storePasswords() throws IOException	{
		PrintWriter passOutput = new PrintWriter("SavedPasses.txt");
		for (int i = 0; i < totalPasses; i++) {
			passOutput.println(passBook[i].returnName());
			passOutput.println(passBook[i].returnPass());
		}
	}
}
