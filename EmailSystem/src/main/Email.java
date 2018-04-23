package main;

//import javax.mail.Message;
//import javax.mail.Session;
//import javax.mail.internet.MimeMessage;

import ch.astorm.jotlmsg.OutlookMessage;

import java.awt.Desktop;
import java.io.*;
import java.net.URI;
//import java.util.Properties;
import java.net.URISyntaxException;

public class Email {
	String emailFileName = "";
	OutlookMessage message;
	File emailFile;
	Desktop desktop;
	Email (String fileName){
		emailFileName = fileName;
		emailFile = new File(emailFileName);
		try {
			message = new OutlookMessage(emailFile);
			} catch (IOException e) {
				System.out.println("Error: " + e);
				}
		}
	public String getBody()	{
		return message.getPlainTextBody();
		}
	public String getSubject()	{
		return message.getSubject();
		}
	public void write()	{
		try {
			message.writeTo(emailFile);
		} catch (IOException e) {
			System.out.println("Error: " + e);
		}
	}
	public void setBody(String bodyString) {
		this.write();
		message.setPlainTextBody(bodyString);
		this.write();
	}
	public void setSubject(String subjectString)	{
		message.setSubject(subjectString);
		this.write();
	}
	public void openInOutlook()	{
		try {
			URI mailTo = new URI("mailto:john@smith.com?subject=Hello%20World");
			desktop.mail(mailTo);
		} catch (Exception e) {
			System.out.println("Error: " + e);
		}
	}
	}
