package main;

import org.apache.poi.openxml4j.exceptions.InvalidOperationException;
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
			tempName = mainSheet.getRow(toRetreive).getCell(nameColumn).getStringCellValue().toString();
			} catch (NullPointerException f) {
				tempName = "";
				}	catch (IllegalStateException f1) {
					tempName = String.valueOf(mainSheet.getRow(toRetreive).getCell(nameColumn).getNumericCellValue());
					}
		try	{
			tempID = String.valueOf(Math.round(mainSheet.getRow(toRetreive).getCell(studentIDColumn).getNumericCellValue()));
			} catch (NullPointerException n) {
				tempID = "";
				}	catch (IllegalStateException n1) {
					tempID = String.valueOf(mainSheet.getRow(toRetreive).getCell(nameColumn).getStringCellValue());
					}
		try	{
			tempEmail = mainSheet.getRow(toRetreive).getCell(emailColumn).getStringCellValue();
			} catch (NullPointerException e) {
				tempEmail = "";
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
	private Boolean isHidden(Row row){
	    return row.getZeroHeight();
	}
	public void importWorkbook(File xlFile)	{
		String importError = "";
		mainSheet = null;
		nameFound = idFound = emailFound = importSuccess = false;
		EmailWindow.setImportFinished(false);
		try	{
			Workbook excelBook = new XSSFWorkbook(xlFile);
			mainSheet = excelBook.getSheetAt(0);
			try	{
				excelBook.close();
			}	catch (FileNotFoundException e)	{
				//Do nothing.. 
			}
			for (Row eachRow: mainSheet)	{
				if (isHidden(eachRow))	{
					importError = "Fatal import error:"
							+ System.getProperty("line.separator") + "Filtered sheet detected! Please unfilter and re-import!"
							+ System.getProperty("line.separator") + System.getProperty("line.separator");
					throw new Exception();
				}
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
						|| StringUtils.containsIgnoreCase(currentHeader, "student-id") 
						|| StringUtils.containsIgnoreCase(currentHeader, "student_id") 
						|| StringUtils.containsIgnoreCase(currentHeader, "studentref") 
						|| StringUtils.containsIgnoreCase(currentHeader, "student ref") 
						|| StringUtils.containsIgnoreCase(currentHeader, "student-ref")
						|| StringUtils.containsIgnoreCase(currentHeader, "student_ref")
						|| StringUtils.containsIgnoreCase(currentHeader, "applicantid") 
						|| StringUtils.containsIgnoreCase(currentHeader, "applicant id")
						|| StringUtils.containsIgnoreCase(currentHeader, "applicant-id")
						|| StringUtils.containsIgnoreCase(currentHeader, "applicant_id")))	{
					idFound = true;
					studentIDColumn = currentColumn;
					resultsString += ("Student ID is column: " + (currentColumn + 1) + System.getProperty("line.separator"));
				}
				if (nameFound == false && (StringUtils.containsIgnoreCase(currentHeader, "fore name") 
						|| StringUtils.containsIgnoreCase(currentHeader, "forename")
						|| StringUtils.containsIgnoreCase(currentHeader, "fore-name") 
						|| StringUtils.containsIgnoreCase(currentHeader, "fore_name")
						|| StringUtils.containsIgnoreCase(currentHeader, "first name")
						|| StringUtils.containsIgnoreCase(currentHeader, "firstname") 
						|| StringUtils.containsIgnoreCase(currentHeader, "first-name") 
						|| StringUtils.containsIgnoreCase(currentHeader, "first_name") 
						|| StringUtils.equalsIgnoreCase(currentHeader, "name")))	{
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
		}	catch (FileNotFoundException  e1) {
			importError = ("Workbook not found: " + e1.toString());
			EmailWindow.setError(importError);
		}	catch (OutOfMemoryError e2)	{
			importError = ("Java ran out of memory: " + e2.toString());
			EmailWindow.setError(importError);
		}	catch (InvalidOperationException e3)	{
			importError = ("The workbook could not be found! " + e3.toString());
			EmailWindow.setError(importError);
		}	catch (Exception e5) {
			if (!(importError.equals("")))	{
				EmailWindow.setError(importError);
			}	else	{
				importError = ("Unexpected import error: " + e5.toString());
				EmailWindow.setError(importError);
			}
		}
	}
}
