package main;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;

public class CellValue {
	private static String initialParse(Cell toGet)	{
		try	{
			CellType toTest = toGet.getCellType();
			if (toTest == CellType.NUMERIC)	{
				return String.valueOf(Math.round(toGet.getNumericCellValue()));
			}	else if (toTest == CellType.STRING)	{
				return toGet.getStringCellValue().trim();
			}	else if (toTest == CellType.BOOLEAN) {
				return String.valueOf(toGet.getBooleanCellValue());
			}	else if (toTest == CellType.ERROR) {
				return "INVALID";
			}	else if (toTest == CellType.FORMULA) {
				if (toGet.getCachedFormulaResultType() == CellType.NUMERIC)	{
					return String.valueOf(Math.round(toGet.getNumericCellValue()));
				}	else if (toGet.getCachedFormulaResultType() == CellType.ERROR)	{
					return "INVALID";
				}	else if (toGet.getCachedFormulaResultType() == CellType.STRING)	{
					return toGet.getStringCellValue().trim();
				}	else	{
					return "";
				}
			}	else	{
				return "";
			}
		}	catch (NullPointerException e)	{
			return "";
		}
	}
	public static String getCellValue(Cell toGet)	{
		String toReturn = initialParse(toGet);
		if (toReturn == null)	{
			return "";
		}
		return toReturn;
	}
}
