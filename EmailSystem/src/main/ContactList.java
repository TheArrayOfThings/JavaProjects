package main;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.File;
import java.io.FileNotFoundException;
import org.apache.commons.lang3.StringUtils;

public class ContactList {
	private MergeContact currentContact;
	private int current = 0, total = 0, nameColumn = 0, studentIDColumn = 0, emailColumn = 0, totalColumns = 0;
	private Sheet mainSheet;
	private String resultsString = "";
	private boolean emailFound = false, idFound = false, nameFound = false, importSuccess = false;
	public boolean getImportSuccess()	{
		return importSuccess;
	}
	public boolean getNameFound()	{
		return nameFound;
	}
	public boolean getIdFound()	{
		return idFound;
	}
	public boolean getEmailFound()	{
		return emailFound;
	}
	public MergeContact getNext()	{
		if (current < total - 1)	{
			++current;
		}
		return this.getSpecific(current);
	}
	public MergeContact getPrevious()	{
		if (current > 1)	{
			--current;
		}
		return this.getSpecific(current);
	}
	public MergeContact getSpecific(int toRetreive) {
		String tempName = "", tempID = "", tempEmail = "";
		try	{
			tempName = mainSheet.getRow(toRetreive).getCell(nameColumn).getRichStringCellValue().toString();
			if (tempName.trim().equals(""))	{
				tempName = "Name not found!";
				}
			} catch (NullPointerException f) {
				tempName = "Name not found!";
				}	catch (IllegalStateException f1) {
					tempName = String.valueOf(mainSheet.getRow(toRetreive).getCell(nameColumn).getNumericCellValue());
					}
		try	{
			tempID = String.valueOf(Math.round(mainSheet.getRow(toRetreive).getCell(studentIDColumn).getNumericCellValue()));
			if (tempID.trim().equals(""))	{
				tempID = "studentIDMissing";
				}
			} catch (NullPointerException n) {
				tempID = "Name not found!";
				}	catch (IllegalStateException n1) {
					tempID = String.valueOf(mainSheet.getRow(toRetreive).getCell(nameColumn).getNumericCellValue());
					}
		try	{
			tempEmail = mainSheet.getRow(toRetreive).getCell(emailColumn).getStringCellValue();
			if (tempEmail.trim().equals(""))	{
				tempEmail = "noemail";
				}
			} catch (NullPointerException e) {
				tempEmail = "Name not found!";
				}	catch (IllegalStateException e) {
					tempEmail = String.valueOf(mainSheet.getRow(toRetreive).getCell(nameColumn).getNumericCellValue());
					}
		current = toRetreive;
		currentContact = new MergeContact(tempName, tempID, tempEmail);
		return currentContact;
	}
	public int getCurrent()	{
		return current;
	}
	public void setCurrent(int currentPara) {
		currentContact = this.getSpecific(currentPara);
		current = currentPara;
	}
	public int getTotal()	{
		return total;
	}
	public String getResults()	{
		return resultsString;
	}
	public Sheet getMainSheet()	{
		return mainSheet;
	}
	public void importWorkbook(File xlFile) throws Exception	{
		mainSheet = null;
		nameFound = idFound = emailFound = importSuccess = false;
		EmailWindow.setImportFinished(false);
		Workbook excelBook = new XSSFWorkbook(xlFile);
		mainSheet = excelBook.getSheetAt(0);
		try	{
			excelBook.close();
		}	catch (FileNotFoundException c) {
			System.out.println("Could not close: " + c);
		}
		total = mainSheet.getPhysicalNumberOfRows();
		totalColumns = mainSheet.getRow(0).getPhysicalNumberOfCells();
		String[] mergeList = new String[totalColumns];
		resultsString = ("Import successful!" + System.getProperty("line.separator"));
		resultsString += ("Total applicants: " + (total - 1) + System.getProperty("line.separator"));
		resultsString += ("Total columns: " + totalColumns + System.getProperty("line.separator"));
		Row firstRow = mainSheet.getRow(0);
		String currentHeader = "";
		for (int currentColumn = 0; currentColumn < totalColumns; ++currentColumn) {
			currentHeader = firstRow.getCell(currentColumn).getStringCellValue().trim();
			mergeList[currentColumn] = new String(currentHeader);
			if (emailFound == false && StringUtils.containsIgnoreCase(currentHeader, "email") 
					|| StringUtils.containsIgnoreCase(currentHeader, "e-mail"))	{
				emailFound = true;
				emailColumn = currentColumn;
				resultsString += ("Email is column: " + (currentColumn + 1) + System.getProperty("line.separator"));
			}
			if (idFound == false && (StringUtils.containsIgnoreCase(currentHeader, "studentid") 
					|| StringUtils.containsIgnoreCase(currentHeader, "student id") 
					|| StringUtils.containsIgnoreCase(currentHeader, "studentref") 
					|| StringUtils.containsIgnoreCase(currentHeader, "student ref") 
					|| StringUtils.containsIgnoreCase(currentHeader, "applicantid") 
					|| StringUtils.containsIgnoreCase(currentHeader, "applicant id")
					|| StringUtils.containsIgnoreCase(currentHeader, "student_ref")))	{
				idFound = true;
				studentIDColumn = currentColumn;
				resultsString += ("Student ID is column: " + (currentColumn + 1) + System.getProperty("line.separator"));
			}
			if (nameFound == false && (StringUtils.containsIgnoreCase(currentHeader, "forename") 
					|| StringUtils.containsIgnoreCase(currentHeader, "firstname") 
					|| StringUtils.containsIgnoreCase(currentHeader, "first name") 
					|| StringUtils.equalsIgnoreCase(currentHeader, "name"))
					|| StringUtils.equalsIgnoreCase(currentHeader, "fullname")
					|| StringUtils.equalsIgnoreCase(currentHeader, "full name"))	{
				nameFound = true;
				nameColumn = currentColumn;
				resultsString += ("Name is column: " + (currentColumn + 1) + System.getProperty("line.separator"));
			}
		}
		EmailWindow.setMergeList(mergeList);
		if (nameFound && idFound && emailFound)	{
			importSuccess = true;
		}
		EmailWindow.setImportFinished(true);
	}
}
