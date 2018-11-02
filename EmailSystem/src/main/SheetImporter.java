package main;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.IOException;

//SheetImporter to take a file and spit out a 'MergeSheet'. SheetImporter should be reusable.

public class SheetImporter {
	private static Boolean isHidden(Row row){
	    return row.getZeroHeight();
	}
	private static Boolean filterCheck(Sheet toCheck)	{
		for (Row eachRow: toCheck)	{
			if (isHidden(eachRow))	{
				return true;
			}
		}
		return false;
	}
	public static MergeSheet importWorkbook(File xlFile, int sheetNumber) throws FilteredSheetException, InvalidFormatException, IOException	{
		EmailWindow.setImportFinished(false);
			Workbook excelBook = new XSSFWorkbook(xlFile);
			MergeSheet returnSheet = new MergeSheet(excelBook.getSheetAt(sheetNumber));
			try	{
				excelBook.close();
			}	catch (Exception e)	{
				//Do nothing..
			}
			if (filterCheck(excelBook.getSheetAt(sheetNumber))) {
				throw new FilteredSheetException();
			}
			return returnSheet;
		}
	}
