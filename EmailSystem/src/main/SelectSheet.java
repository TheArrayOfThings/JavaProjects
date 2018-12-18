package main;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wb.swt.SWTResourceManager;

import java.io.IOException;

import org.apache.poi.ss.usermodel.Workbook;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Label;

public class SelectSheet extends Dialog {

	String[] sheetNames;
	protected int selectedSheet = 0;
	protected Shell shlSelectSheet;
	private Combo combo;
	Listener closeAll;
	private Label lblMultipleSheetsDetected;

	public SelectSheet(Shell parent, int style, Workbook excelBook, MassEmailer windowHandlerPara) throws IOException {
		super(parent, style);
		sheetNames = new String[excelBook.getNumberOfSheets()];
		for (int i = 0; i < excelBook.getNumberOfSheets(); ++i)	{
			sheetNames[i] = excelBook.getSheetName(i);
		}
		setText("SWT Dialog");
	}

	public int open() {
		createContents();
		shlSelectSheet.open();
		shlSelectSheet.layout();
		Display display = getParent().getDisplay();
		while (!shlSelectSheet.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return selectedSheet;
	}

	private void createContents() {
		shlSelectSheet = new Shell(getParent(), getStyle());
		shlSelectSheet.setImage(SWTResourceManager.getImage(SelectSheet.class, "/resources/LogoBasic.png"));
		shlSelectSheet.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		shlSelectSheet.setSize(241, 138);
		shlSelectSheet.setText("Select Sheet");
		shlSelectSheet.setLayout(new GridLayout(1, false));
		closeAll = new Listener() {
			public void handleEvent(Event e) {
				EmailWindow.writeConsole("Warning: Default sheet selected: " + sheetNames[0]);
			}
		};
		shlSelectSheet.addListener(SWT.Close, closeAll); 
		
		lblMultipleSheetsDetected = new Label(shlSelectSheet, SWT.BORDER | SWT.CENTER);
		lblMultipleSheetsDetected.setFont(SWTResourceManager.getFont("PT Sans", 10, SWT.NORMAL));
		lblMultipleSheetsDetected.setBackground(SWTResourceManager.getColor(SWT.COLOR_INFO_BACKGROUND));
		GridData gd_lblMultipleSheetsDetected = new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1);
		gd_lblMultipleSheetsDetected.widthHint = 223;
		gd_lblMultipleSheetsDetected.heightHint = 40;
		lblMultipleSheetsDetected.setLayoutData(gd_lblMultipleSheetsDetected);
		lblMultipleSheetsDetected.setText("Multiple sheets detected in spreadsheet!\r\nPlease select correct sheet!");
		
		combo = new Combo(shlSelectSheet, SWT.READ_ONLY);
		combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		for (String eachString: sheetNames)	{
			combo.add(eachString);
		}
		combo.setText(combo.getItem(0));
		
		Button btnSubmit = new Button(shlSelectSheet, SWT.NONE);
		btnSubmit.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				shlSelectSheet.removeListener(SWT.Close, closeAll);
				selectedSheet = combo.getSelectionIndex();
				shlSelectSheet.close();
			}
		});
		btnSubmit.setText("Submit");

	}
}
