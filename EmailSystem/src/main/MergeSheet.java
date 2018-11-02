package main;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;

public class MergeSheet {
	private String[] columnHeaders;
	private int totalRows = 0, totalColumns = 0;
	private Sheet mainSheet;
	MergeSheet(Sheet mainSheetPara)	{
		int currentColumn = 0;
		mainSheet = mainSheetPara;
		totalRows = mainSheet.getPhysicalNumberOfRows();
		totalColumns = mainSheet.getRow(0).getPhysicalNumberOfCells();
		columnHeaders = new String[totalColumns];
		for (Cell eachCell: mainSheet.getRow(0)) {
			columnHeaders[currentColumn] = eachCell.getStringCellValue().trim();
			++currentColumn;
		}
	}
	public String getColumnHeader(int toGet)	{
		return columnHeaders[toGet];
	}
	public String[] getColumnHeaders()	{
		return columnHeaders;
	}
	public int findColumn(String[] searchStrings)	{
		int columnIndex = 0;
		for (String headerString: columnHeaders)	{
			for (String eachSearch: searchStrings)	{
				if (headerString.trim().toLowerCase().contains(eachSearch.trim().toLowerCase()))	{
					return columnIndex;
				}
			}
			++columnIndex;
		}
		return -1;
	}
	public int getTotalRows()	{
		return totalRows;
	}
	public int getTotalColumns()	{
		return totalColumns;
	}
	public Sheet getSheet()	{
		return mainSheet;
	}
}
