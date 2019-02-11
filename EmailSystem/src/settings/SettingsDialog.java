package settings;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.layout.GridLayout;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;

public class SettingsDialog extends Dialog {

	protected SettingsHandler mainSettings = new SettingsHandler();
	protected Shell shlSettings;

	public SettingsDialog(Shell parent, int style) {
		super(parent, style);
		setText("Settings");
	}

	public void open() {
		createContents();
		shlSettings.open();
		shlSettings.layout();
		Display display = getParent().getDisplay();
		while (!shlSettings.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	private void createContents() {
		shlSettings = new Shell(getParent(), getStyle());
		shlSettings.setSize(220, 174);
		shlSettings.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		shlSettings.setImage(SWTResourceManager.getImage(SettingsDialog.class, "/resources/LogoBasic.png"));
		shlSettings.setText("Settings");
		shlSettings.setLayout(new GridLayout(2, false));
		
		Label lblHello = new Label(shlSettings, SWT.BORDER);
		lblHello.setBackground(SWTResourceManager.getColor(SWT.COLOR_INFO_BACKGROUND));
		lblHello.setFont(SWTResourceManager.getFont("PT Sans", 10, SWT.NORMAL));
		lblHello.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		lblHello.setText("Below are the MailMerger settings.\r\n\r\nPlease click 'Apply' to apply them.");
		
		Button ignoreFilters = new Button(shlSettings, SWT.CHECK);
		ignoreFilters.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		ignoreFilters.setToolTipText("If this is selected, the program will not block the user from importing a filtered sheet.");
		ignoreFilters.setText("Ignore Filters?");
		ignoreFilters.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		ignoreFilters.setSelection(mainSettings.getSetting(ignoreFilters.getText()));
		
		Button addStudentReference = new Button(shlSettings, SWT.CHECK);
		addStudentReference.setSelection(true);
		addStudentReference.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		addStudentReference.setToolTipText("If this is selected, the program will automatically add ' - Student ID ([ID Number])' to the end of the subject.");
		addStudentReference.setText("Add Student IDs?");
		addStudentReference.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		addStudentReference.setSelection(mainSettings.getSetting(addStudentReference.getText()));
		
		Button btnApply = new Button(shlSettings, SWT.NONE);
		btnApply.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				mainSettings.setSetting(ignoreFilters.getText(), ignoreFilters.getSelection());
				mainSettings.setSetting(addStudentReference.getText(), addStudentReference.getSelection());
				shlSettings.close();
			}
		});
		btnApply.setText("Apply");
		
		Button btnCancel = new Button(shlSettings, SWT.NONE);
		btnCancel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				shlSettings.close();
			}
		});
		btnCancel.setText("Cancel");
		Button[] settingsButtons = new Button[2];
		settingsButtons[0] = ignoreFilters;
		settingsButtons[1] = addStudentReference;

	}

}
