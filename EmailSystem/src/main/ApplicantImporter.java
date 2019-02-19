package main;

//Should contain methods specific to the MailMerger and the import function.

public class ApplicantImporter {
	private int nameColumn = -1, studentIDColumn = -1, emailColumn = -1;
	private String resultsString = "";
	private String[] mergeList = new String[] {""};
	private int current = 0;
	MergeSheet main;
	public String importApplicants(MergeSheet toCheck)	{
		main = toCheck;
		nameColumn = studentIDColumn = emailColumn = -1;
		resultsString = "";
		current = 0;
		emailColumn = main.findFirstColumn(new String[] {"email", "e-mail"});
		studentIDColumn = main.findFirstColumn(new String[] {" id", "-id", "_id", "ref", " ref", "-ref", "_ref", "reference"});
		nameColumn = main.findFirstColumn(new String[] {"fore name", "forename", "fore-name", "fore_name", "first name", "firstname", "first-name", "first_name"});
		if (emailColumn == -1)	{
			return ("Fatal Error: No email column found!");
		}	else	{
			resultsString += ("Email is column: " + (emailColumn + 1) + System.getProperty("line.separator"));
		}
		if (studentIDColumn != -1)	{
			resultsString += ("Reference number is column: " + (studentIDColumn + 1) + System.getProperty("line.separator"));
		}
		if (nameColumn == -1)	{
			nameColumn = main.findExactColumn(new String[] {"name"});
			if (nameColumn == -1)	{
				return "Fatal Error: No forename column found!";
			}	else	{
				resultsString += ("Forename is column: " + (nameColumn + 1) + System.getProperty("line.separator"));
			}
		}	else	{
			resultsString += ("Forename is column: " + (nameColumn + 1) + System.getProperty("line.separator"));
			}
		resultsString += ("Import successful!" + System.getProperty("line.separator"));
		resultsString += ("Total recipients: " + (main.getTotalRows() - 1) + System.getProperty("line.separator"));
		resultsString += ("Total columns: " + main.getTotalColumns());
		mergeList = main.getColumnHeaders();
		return resultsString;
	}
	public String[] getMergeList()	{
		return mergeList;
	}
	public MergeContact getNext()	{
		if (current < main.getTotalRows() - 1)	{
			++current;
		}
		return main.getSpecific(current);
	}
	public MergeContact getPrevious()	{
		if (current > 1)	{
			--current;
		}
		return main.getSpecific(current);
	}
	public int getCurrent()	{
		return current;
	}
	public MergeContact getSpecific(int toRetreive) {
		current = toRetreive;
		return main.getSpecific(toRetreive);
	}
	public int getTotal()	{
		return main.getTotalRows();
	}
	public String getResults()	{
		return resultsString;
	}
	public boolean getIdFound()	{
		if (studentIDColumn == -1)	{
			return false;
		}	else	{
			return true;
		}
	}
	public int getName()	{
		return nameColumn;
	}
	public int getID()	{
		return studentIDColumn;
	}
	public int getEmail()	{
		return emailColumn;
	}
}
