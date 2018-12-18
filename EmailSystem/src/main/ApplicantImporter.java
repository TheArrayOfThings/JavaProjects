package main;


import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.apache.poi.ss.usermodel.Sheet;

//Should contain methods specific to the MailMerger and the import function.

public class ApplicantImporter {
	private int nameColumn = -1, studentIDColumn = -1, emailColumn = -1;
	private MergeSheet mergeSheet;
	private String resultsString = "";
	private String[] mergeList = new String[] {""};
	private int current = 0;
	public String importApplicants(MergeSheet toCheck)	{
		nameColumn = studentIDColumn = emailColumn = -1;
		mergeSheet = toCheck;
		resultsString = "";
		current = 0;
		emailColumn = mergeSheet.findColumn(new String[] {"email", "e-mail"});
		studentIDColumn = mergeSheet.findColumn(new String[] {"studentid", "student id", "student-id", "student_id", "studentref", "student ref", "student-ref", "student_ref", "applicantid", "applicant id", "applicant-id", "applicant_id"});
		nameColumn = mergeSheet.findColumn(new String[] {"fore name", "forename", "fore-name", "fore_name", "first name", "firstname", "first-name", "first_name"});
		if (emailColumn == -1)	{
			return ("Fatal Error: No email column found!");
		}	else	{
			resultsString += ("Email is column: " + (emailColumn + 1) + System.getProperty("line.separator"));
		}
		if (studentIDColumn != -1)	{
			resultsString += ("Student ID is column: " + (studentIDColumn + 1) + System.getProperty("line.separator"));
		}
		if (nameColumn == -1)	{
			return "Fatal Error: No forename column found!";
		}	else	{
			resultsString += ("Forename is column: " + (nameColumn + 1) + System.getProperty("line.separator"));
			}
		resultsString += ("Import successful!" + System.getProperty("line.separator"));
		resultsString += ("Total applicants: " + (mergeSheet.getTotalRows() - 1) + System.getProperty("line.separator"));
		resultsString += ("Total columns: " + mergeSheet.getTotalColumns());
		mergeList = toCheck.getColumnHeaders();
		return resultsString;
	}
	public String[] getMergeList()	{
		return mergeList;
	}
	public MergeContact getNext()	{
		if (current < mergeSheet.getTotalRows() - 1)	{
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
	public int getCurrent()	{
		return current;
	}
	public MergeContact getSpecific(int toRetreive) {
		String tempName = "", tempID = "", tempEmail = "";
		try	{
			tempName = mergeSheet.getSheet().getRow(toRetreive).getCell(nameColumn).getStringCellValue().toString();
			} catch (NullPointerException f) {
				tempName = "";
				}	catch (IllegalStateException f1) {
					tempName = String.valueOf(mergeSheet.getSheet().getRow(toRetreive).getCell(nameColumn).getNumericCellValue());
				}	catch (Exception f2)	{
					tempName = "";
				}
		if (studentIDColumn != -1)	{
			try	{
				tempID = String.valueOf(Math.round(mergeSheet.getSheet().getRow(toRetreive).getCell(studentIDColumn).getNumericCellValue()));
				} catch (NullPointerException n) {
					tempID = "";
					}	catch (IllegalStateException n1) {
						tempID = String.valueOf(mergeSheet.getSheet().getRow(toRetreive).getCell(studentIDColumn).getStringCellValue());
					}	catch (Exception n2)	{
						tempID = "";
					}
			tempID = tempID.replaceAll("\u00A0", ""); //This removes some spacing that is not UTF-8 compliant
			}	else	{
				tempID = "";
			}
		try	{
			tempEmail = mergeSheet.getSheet().getRow(toRetreive).getCell(emailColumn).getStringCellValue();
			} catch (NullPointerException e) {
				tempEmail = "";
				}	catch (IllegalStateException e1) {
					tempEmail = String.valueOf(mergeSheet.getSheet().getRow(toRetreive).getCell(emailColumn).getNumericCellValue());
				}	catch (Exception e2)	{
					tempEmail = "";
				}
		if (!(tempEmail.trim().equals("")))	{
			try {
				InternetAddress check = new InternetAddress(tempEmail);
				check.validate();
			} catch (AddressException e1) {
				tempEmail = "INVALID";
			}
		}
		current = toRetreive;
		return new MergeContact(tempName, tempID, tempEmail);
	}
	public int getTotal()	{
		return mergeSheet.getTotalRows();
	}
	public String getResults()	{
		return resultsString;
	}
	public Sheet getMainSheet()	{
		return mergeSheet.getSheet();
	}
	public MergeSheet getMergeSheet()	{
		return mergeSheet;
	}
	public boolean getIdFound()	{
		if (studentIDColumn == -1)	{
			return false;
		}	else	{
			return true;
		}
	}

}
