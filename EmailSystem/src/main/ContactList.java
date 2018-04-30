package main;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.eclipse.swt.custom.StyledText;
import org.apache.commons.lang3.StringUtils;


public class ContactList {
	Contact[] contactBook; 
	int current = 0;
	int total = 0;
	String[] names;
	String[] studentIDs;
	String[] emails;
	Workbook importBook;
	StyledText systemText;
	public String resultsString = "";
	boolean emailFound = false, idFound = false, nameFound = false, importSuccess = false;
	public void initialise(StyledText systemTextPara) {
		systemText = systemTextPara;
	}
	public void addNew(Contact newContact)	{
		++total;
		contactBook[total] = newContact;
	}
	public Contact getFirst()	{
		current = 0;
		return contactBook[0];
	}
	public Contact getLast()	{
		current = total;
		return contactBook[total];
	}
	public Contact getNext()	{
		if (current < total - 1)	{
			++current;
		}
		return contactBook[current];
	}
	public Contact getPrevious()	{
		if (current > 0)	{
			--current;
		}
		return contactBook[current];
	}
	public Contact getSpecific(int toRetreive) {
		return contactBook[toRetreive];
	}
	public int getCurrent()	{
		return current;
	}
	public void setCurrent(int currentPara) {
		current = currentPara;
	}
	public int getTotal()	{
		return total;
	}
	public String getResults()	{
		return resultsString;
	}
	public void addLine(String toAdd)	{
		systemText.setText(systemText.getText() + System.getProperty("line.separator") + toAdd);
	}
	public void importAll(Workbook excelBook)	{
		importBook = excelBook;
		Sheet mainSheet = importBook.getSheetAt(0);
		System.out.println("Sheet selected!");
		total = mainSheet.getPhysicalNumberOfRows() - 1;
		contactBook = new Contact[total];
		int columns = mainSheet.getRow(0).getPhysicalNumberOfCells();
		System.out.println("Columns = " + columns);
		names = new String[total];
		studentIDs = new String[total];
		emails = new String[total];
		resultsString = ("Import sucessful!" + System.getProperty("line.separator"));
		resultsString += ("Total applicants: " + total + System.getProperty("line.separator"));
		resultsString += ("Total columns: " + columns + System.getProperty("line.separator"));
		int currentColumn = 0;
		Row firstRow = mainSheet.getRow(0);
		String currentHeader = "";
		for (; currentColumn < columns; ++currentColumn) {
			currentHeader = firstRow.getCell(currentColumn).getStringCellValue();
			System.out.println("Investigating column: " + (currentColumn + 1));
			if (StringUtils.containsIgnoreCase(currentHeader, "email") && emailFound == false)	{
				System.out.println("Email column found!");
				emailFound = true;
				resultsString += ("Email is column: " + (currentColumn + 1) + System.getProperty("line.separator"));
				for (int e = 1; e <= total; ++e) {
					try	{
						emails[e - 1] = mainSheet.getRow(e).getCell(currentColumn).getStringCellValue();
					} catch (NullPointerException n) {
						emails[e - 1] = "noemail";
					}
				}
			}
			if (idFound == false && (StringUtils.containsIgnoreCase(currentHeader, "studentid") 
					|| StringUtils.containsIgnoreCase(currentHeader, "student id") 
					|| StringUtils.containsIgnoreCase(currentHeader, "studentref") 
					|| StringUtils.containsIgnoreCase(currentHeader, "student ref") 
					|| StringUtils.containsIgnoreCase(currentHeader, "applicantid") 
					|| StringUtils.containsIgnoreCase(currentHeader, "applicant id")
					|| StringUtils.containsIgnoreCase(currentHeader, "student_reference") 
					|| StringUtils.containsIgnoreCase(currentHeader, "student reference") 
					|| StringUtils.equalsIgnoreCase(currentHeader, "name")))	{
				System.out.println("Student ID column found!");
				idFound = true;
				resultsString += ("Student ID is column: " + (currentColumn + 1) + System.getProperty("line.separator"));
				try	{
					for (int s = 1; s <= total; ++s) {
						try	{
							studentIDs[s - 1] = String.valueOf(Math.round(mainSheet.getRow(s).getCell(currentColumn).getNumericCellValue()));
						} catch (NullPointerException n) {
							studentIDs[s - 1] = "studentIDMissing";
						}
					}
				}	catch (IllegalStateException stringError)	{
					resultsString = ("Error! StudentID detected as string!" + System.getProperty("line.separator"));
					resultsString += (stringError.getMessage());
					idFound = false;
				}
			}
			if (nameFound == false && (StringUtils.containsIgnoreCase(currentHeader, "forename") 
					|| StringUtils.containsIgnoreCase(currentHeader, "firstname") 
					|| StringUtils.containsIgnoreCase(currentHeader, "first name") 
					|| StringUtils.equalsIgnoreCase(currentHeader, "name"))
					|| StringUtils.equalsIgnoreCase(currentHeader, "fullname")
					|| StringUtils.equalsIgnoreCase(currentHeader, "full name"))	{
				System.out.println("Name column found!");
				nameFound = true;
				resultsString += ("Name is column: " + (currentColumn + 1) + System.getProperty("line.separator"));
				for (int n = 1; n <= total; ++n) {
					try	{
						names[n - 1] = mainSheet.getRow(n).getCell(currentColumn).getRichStringCellValue().toString();
					} catch (NullPointerException f) {
						names[n - 1] = "Name not found!";
					}
				}
			}
		}
		System.out.println("All columns investigated!");
		currentColumn = 0;
		if (emailFound && idFound && nameFound)	{
			for (int c = 0; c < total; ++c)	{
				contactBook[c] = new Contact(names[c].trim(), studentIDs[c].trim(), emails[c].trim());
			}
			importSuccess = true;
		}
		EmailWindow.importFinished = true;
	}
}
