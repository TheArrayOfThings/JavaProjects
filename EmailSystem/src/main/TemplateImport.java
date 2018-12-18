package main;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

import com.auxilii.msgparser.MsgParser;

//Handles importing of outlook .msg & .oft files.

public class TemplateImport {
	private static String selectTemplate(Shell mainShell)	{
		FileDialog dialog = new FileDialog(mainShell, SWT.OPEN);
		dialog.setFilterExtensions(new String [] {"*.oft*", "*.msg*"});
		dialog.setFilterPath("H:\\");
		return dialog.open();
	}
	private String convertTemplate(String toConvert)	{
		String toReturn;
		if (toConvert.indexOf("Dear") != 0)	{
			toReturn = toConvert.substring((toConvert.indexOf("Dear") + 4), toConvert.length());
		}	else if (toConvert.indexOf("Hi") != 0)	{
			toReturn = toConvert.substring((toConvert.indexOf("Hi") + 2), toConvert.length());
		}	else	{
			toReturn = toConvert.substring(toConvert.indexOf("<body"), toConvert.length());
		}
		toReturn = toReturn.replaceAll("&nbsp;", "");
		toReturn = toReturn.replaceAll("<o:p></o:p>", "");
		if (StringUtils.containsIgnoreCase(toReturn, "Kind Regard"))	{
			toReturn = toReturn.substring(0, (StringUtils.indexOfIgnoreCase(toReturn, "Kind Regard")));
			toReturn = nulifyLastTag(toReturn, "<p");
			toReturn = nulifyLastTag(toReturn, "<p");
			toReturn = nulifyLastTag(toReturn, "</p");
			toReturn = nulifyLastTag(toReturn, "</p");
		}	else if (StringUtils.containsIgnoreCase(toReturn, "Many Thank"))	{
			toReturn = toReturn.substring(0, (StringUtils.indexOfIgnoreCase(toReturn, "Many Thank")));
			toReturn = nulifyLastTag(toReturn, "<p");
			toReturn = nulifyLastTag(toReturn, "<p");
			toReturn = nulifyLastTag(toReturn, "</p");
			toReturn = nulifyLastTag(toReturn, "</p");
		}	else if (StringUtils.containsIgnoreCase(toReturn, "Best Wish"))	{
			toReturn = toReturn.substring(0, (StringUtils.indexOfIgnoreCase(toReturn, "Best Wish")));
			toReturn = nulifyLastTag(toReturn, "<p");
			toReturn = nulifyLastTag(toReturn, "<p");
			toReturn = nulifyLastTag(toReturn, "</p");
			toReturn = nulifyLastTag(toReturn, "</p");
		}
		return toReturn.trim();
	}
	//private static String deleteTag(int startIndex, String toParse, String toDelete)	{
	//	return toParse.substring(startIndex, toParse.indexOf("<" + toDelete)) + toParse.substring((toParse.indexOf(">", toParse.indexOf("<" + toDelete)) + 1), toParse.length());
	//}
	private String nulifyLastTag(String toParse, String toDelete)	{
		int position = toParse.lastIndexOf(toDelete);
		if (position >= 0)	{
			return new StringBuilder(toParse).replace(position, position + toDelete.length(),"<a").toString();
		}	else	{
			return toParse;
		}
	}
	public String importTemplate(Shell mainShell)	{
		String toImport = selectTemplate(mainShell);
		if (toImport != null) {
			File msgFile, orgFile = new File(toImport);
			if (toImport.endsWith(".oft"))	{
				msgFile = new File(toImport.substring(0, (toImport.length() - 3)) + "msg");
				orgFile.renameTo(msgFile);
			}	else	{
				msgFile = new File(toImport);
			}
			MsgParser messageParser = new MsgParser();
			try {
				String toReturn = messageParser.parseMsg(msgFile).getBodyHTML();
				if (toImport.endsWith(".oft"))	{
					msgFile.renameTo(new File(toImport));
				}	else	{
					msgFile.renameTo(new File(toImport.substring(0, (toImport.length() - 3)) + "oft"));
				}
				msgFile.renameTo(new File(toImport));
				toReturn = convertTemplate(toReturn);
				//Conversion done, now handle UTF-8 format
				byte temp[] = toReturn.getBytes(java.nio.charset.StandardCharsets.ISO_8859_1); 
				toReturn = new String(temp, java.nio.charset.StandardCharsets.UTF_8);
				return toReturn;
			} catch (UnsupportedOperationException e) {
				return "";
			} catch (IOException e) {
				return "";
			}
		}	else	{
			return "";
		}
	}
}
