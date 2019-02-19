package main;

import java.text.SimpleDateFormat;

import org.apache.poi.ss.usermodel.Sheet;

public class MergeSheet {
	private String[] columnHeaders;
	private int totalRows = 0, totalColumns = 0;
	private MergeContact[] importedRecipients;
	private SimpleDateFormat format = new SimpleDateFormat("dd/MMMM/yyyy");
	MergeSheet(Sheet mainSheetPara)	{
		int currentColumn = 0;
		totalRows = mainSheetPara.getPhysicalNumberOfRows();
		importedRecipients = new MergeContact[totalRows];
		totalColumns = mainSheetPara.getRow(0).getPhysicalNumberOfCells();
		columnHeaders = new String[totalColumns];
		for (int i = 0; i < totalColumns; ++i) {
			columnHeaders[currentColumn] = CellValue.getCellValue(mainSheetPara.getRow(0).getCell(i));
			++currentColumn;
		}
		for (int i = 1; i < totalRows; ++i)	{
			String[] rowData = new String[totalColumns];
			int current = 0;
			for (int c = 0; c < totalColumns; ++c)	{
				if (!(columnHeaders[c].toLowerCase().contains("date")))	{
					rowData[current] = CellValue.getCellValue(mainSheetPara.getRow(i).getCell(c));
					}	else	{
						try	{
							rowData[current] = format.format(mainSheetPara.getRow(i).getCell(c).getDateCellValue()).toString();
							}	catch (Exception e) {
								e.printStackTrace();
								rowData[current] = CellValue.getCellValue(mainSheetPara.getRow(i).getCell(c));
								}
					}
				++current;
			}
			importedRecipients[i] = new MergeContact(rowData);
		}
	}
	public String getColumnHeader(int toGet)	{
		return columnHeaders[toGet];
	}
	public String[] getColumnHeaders()	{
		return columnHeaders;
	}
	public int findFirstColumn(String[] searchStrings)	{
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
	public int findExactColumn(String[] searchStrings)	{
		int columnIndex = 0;
		for (String headerString: columnHeaders)	{
			for (String eachSearch: searchStrings)	{
				if (headerString.trim().toLowerCase().equals(eachSearch.trim().toLowerCase()))	{
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
	public MergeContact getSpecific(int toRetreive) {
		return importedRecipients[toRetreive];
	}
}
