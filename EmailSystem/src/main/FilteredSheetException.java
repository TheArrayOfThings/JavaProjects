package main;

public class FilteredSheetException extends Exception {
	private static final long serialVersionUID = 1149059763251276686L;

	FilteredSheetException()	{
		super("Error: Filtered sheet detected!");
	}
}
