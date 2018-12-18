package main;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.wb.swt.SWTResourceManager;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;

import java.io.File;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.custom.StyledText;

public class EmailWindow {
	private static Text txtMain;
	private static Text txtSubject;
	private static Text txtName;
	private static Text txtSID;
	private static Text txtEmail;
	private static Button btnPreview;
	private static Button btnSend;
	private static Button btnPrevious;
	private static Button btnNext;
	private static Button btnImport;
	private static Button btnAddAttachment;
	private static Button btnRemoveAttachment;
	private static Button btnImportoft;
	private static Button btnClear;
	private static Label lblDear;
	private static Label lblStudentId;
	private static Label lblEmail;
	private static Label lblInbox;
	private static Label lblAttachment;
	private static StyledText txtSystem;
	private static Button btnAddStudentId;
	private static Combo comboDropDownIS;
	private static Combo comboAttach;
	private static Shell shell = new Shell();
	private static String dLine = System.getProperty("line.separator") + System.getProperty("line.separator");
	private static Button btnSetup;
	private static Button btnFilter;

	public static void main(String[] args) {
		Display display = Display.getDefault();
		MassEmailer emailer = new MassEmailer(shell);
			shell.setImage(SWTResourceManager.getImage(EmailWindow.class, "/resources/LogoBasic.png"));
			shell.setBackground(SWTResourceManager.getColor(SWT.COLOR_LIST_BACKGROUND));
			shell.setSize(790, 600);
			shell.setText("Ryan's MailMerger");
			shell.addListener(SWT.Close, new Listener()	{
				public void handleEvent(Event event) {
					emailer.shutDown();
					}
				});
			shell.setLayout(new GridLayout(13, false));
			lblDear = new Label(shell, SWT.NONE);
			lblDear.setFont(SWTResourceManager.getFont("PT Sans", 10, SWT.NORMAL));
			lblDear.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
			lblDear.setText("Forename");
			
			txtName = new Text(shell, SWT.BORDER | SWT.READ_ONLY);
			GridData gd_txtName = new GridData(SWT.FILL, SWT.CENTER, true, false, 4, 1);
			gd_txtName.widthHint = 250;
			txtName.setLayoutData(gd_txtName);
			txtName.setFont(SWTResourceManager.getFont("PT Sans", 10, SWT.NORMAL));
			
			btnPrevious = new Button(shell, SWT.NONE);
			btnPrevious.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseDown(MouseEvent e) {
					MergeContact previous = emailer.getPrevious();
					if (previous != null)	{
						setContact(previous);
					}	else	{
						writeConsole("Error: " + System.getProperty("line.separator") + "Please import applicants first!");
					}
				}
			});
			btnPrevious.setText("<");
			
			btnNext = new Button(shell, SWT.NONE);
			btnNext.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseDown(MouseEvent e) {
					MergeContact next = emailer.getNext();
					if (next != null)	{
						setContact(next);
					}	else	{
						writeConsole("Error: " + System.getProperty("line.separator") + "Please import applicants first!");
					}
				}
			});
			btnNext.setText(">");
			
			lblInbox = new Label(shell, SWT.NONE);
			lblInbox.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
			lblInbox.setFont(SWTResourceManager.getFont("PT Sans", 10, SWT.NORMAL));
			lblInbox.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
			lblInbox.setText("Inbox");
			
			comboDropDownIS = new Combo(shell, SWT.READ_ONLY);
			comboDropDownIS.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 5, 1));
			
			lblStudentId = new Label(shell, SWT.NONE);
			lblStudentId.setFont(SWTResourceManager.getFont("PT Sans", 10, SWT.NORMAL));
			lblStudentId.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
			lblStudentId.setText("Student ID ");
			
			txtSID = new Text(shell, SWT.BORDER | SWT.READ_ONLY);
			txtSID.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 4, 1));
			txtSID.setFont(SWTResourceManager.getFont("PT Sans", 10, SWT.NORMAL));
			new Label(shell, SWT.NONE);
			new Label(shell, SWT.NONE);
			
			lblAttachment = new Label(shell, SWT.NONE);
			lblAttachment.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
			lblAttachment.setFont(SWTResourceManager.getFont("PT Sans", 10, SWT.NORMAL));
			lblAttachment.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
			lblAttachment.setText("Attachments");
			
			comboAttach = new Combo(shell, SWT.READ_ONLY);
			GridData gd_comboAttach = new GridData(SWT.FILL, SWT.FILL, true, false, 5, 1);
			gd_comboAttach.widthHint = 109;
			comboAttach.setLayoutData(gd_comboAttach);
			
			lblEmail = new Label(shell, SWT.NONE);
			lblEmail.setFont(SWTResourceManager.getFont("PT Sans", 10, SWT.NORMAL));
			lblEmail.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
			lblEmail.setText("Email");
			
			txtEmail = new Text(shell, SWT.BORDER | SWT.READ_ONLY);
			txtEmail.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 4, 1));
			txtEmail.setFont(SWTResourceManager.getFont("PT Sans", 10, SWT.NORMAL));
			new Label(shell, SWT.NONE);
			new Label(shell, SWT.NONE);
			new Label(shell, SWT.NONE);
			
			btnAddAttachment = new Button(shell, SWT.NONE);
			GridData gd_btnAddAttachment = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
			gd_btnAddAttachment.widthHint = 64;
			btnAddAttachment.setLayoutData(gd_btnAddAttachment);
			btnAddAttachment.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseDown(MouseEvent e) {
					FileDialog dialog = new FileDialog(shell, SWT.OPEN);
					dialog.setFilterExtensions(new String [] {"*.*"});
					dialog.setFilterPath("H:\\");
					String location = dialog.open();
					if (location != null && new File(location).exists())	{
						emailer.addAttachment(location);
						comboAttach.add(location.substring(location.lastIndexOf("\\") + 1));
						comboAttach.select(comboAttach.getItemCount() - 1);
						writeConsole("File: '" + location.substring(location.lastIndexOf("\\") + 1) + "' added successfully!");
					}	else	{
						writeConsole("Attachment not found!");
					}
				}
			});
			btnAddAttachment.setText("Add");
			
			btnRemoveAttachment = new Button(shell, SWT.NONE);
			GridData gd_btnRemoveAttachment = new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1);
			gd_btnRemoveAttachment.widthHint = 69;
			btnRemoveAttachment.setLayoutData(gd_btnRemoveAttachment);
			btnRemoveAttachment.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseDown(MouseEvent e) {
					if(comboAttach.getItemCount() > 0)	{
						emailer.removeAttachment(comboAttach.getText(), comboAttach.indexOf(comboAttach.getText()));
						writeConsole("File: '" + comboAttach.getText() + "' removed successfully!");
						comboAttach.remove(comboAttach.getText());
						comboAttach.select(comboAttach.getItemCount() - 1);
					}	else	{
						writeConsole("No attachment to remove!");
					}
				}
			});
			btnRemoveAttachment.setText("Remove");
			new Label(shell, SWT.NONE);
			
			txtSubject = new Text(shell, SWT.BORDER);
			txtSubject.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 7, 1));
			txtSubject.setText("[Replace with subject]");
			txtSubject.setFont(SWTResourceManager.getFont("PT Sans", 10, SWT.NORMAL));
			
			txtSystem = new StyledText(shell, SWT.BORDER | SWT.READ_ONLY | SWT.WRAP);
			txtSystem.setAlignment(SWT.CENTER);
			txtSystem.setTopMargin(10);
			txtSystem.setText("Welcome to Ryan's MailMerger!\r\n\r\n1):  Import your data using the 'Import' button.\r\n\r\n2): Add a subject and complete the body.\r\n\r\n3): Preview before sending!\r\n\r\n**'Dear [name]' is automatically added!**\r\n\r\n**'Student ID: [Student ID]' is automatically added!**\r\n\r\n**Your signature is automatically added!**\r\n\r\n");
			txtSystem.setForeground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
			txtSystem.setFont(SWTResourceManager.getFont("Calibri", 11, SWT.NORMAL));
			txtSystem.setBackground(SWTResourceManager.getColor(SWT.COLOR_BLACK));
			GridData gd_txtSystem = new GridData(SWT.FILL, SWT.FILL, true, true, 6, 2);
			gd_txtSystem.widthHint = 389;
			txtSystem.setLayoutData(gd_txtSystem);
			txtSystem.addListener(SWT.Modify, new Listener(){
			    public void handleEvent(Event e){
			    	txtSystem.setTopIndex(txtSystem.getLineCount() - 1);
			    }
			});
			
			txtMain = new Text(shell, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
			GridData gd_txtMain = new GridData(SWT.FILL, SWT.FILL, true, true, 7, 1);
			gd_txtMain.widthHint = 363;
			txtMain.setLayoutData(gd_txtMain);
			txtMain.setFont(SWTResourceManager.getFont("PT Sans", 10, SWT.NORMAL));
			txtMain.setText("[Replace with body of email]");
			
			btnImport = new Button(shell, SWT.NONE);
			btnImport.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
			btnImport.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseDown(MouseEvent e) {
					try	{
						FileDialog dialog = new FileDialog(shell, SWT.OPEN);
						dialog.setFilterExtensions(new String [] {"*.xls*"});
						dialog.setFilterPath("H:\\");
						String fileLocation = dialog.open();
						if (fileLocation == null)	{
							writeConsole("Please select a file to import!");
							}	else	{ //Start import
								int sheetNumber = 0;
								Workbook tempBook = new XSSFWorkbook(new File(fileLocation));
								if (tempBook.getNumberOfSheets() > 1)	{
				    			SelectSheet selectSheet = new SelectSheet(shell, SWT.CLOSE | SWT.SYSTEM_MODAL, tempBook, emailer);
				    			sheetNumber = selectSheet.open();
				    			tempBook.close();
				    			}
								String refreshLog = txtSystem.getText();
								txtSystem.setText("Importing..." + System.getProperty("line.separator"));
								enabled(false);
								emailer.refreshDisplay(new Runnable()	{
									@Override
									public void run() {
										if (emailer.getImportFinished())	{
											txtSystem.setText(refreshLog);
											writeConsole(emailer.killRefresh());
											if(emailer.getImportSuccess())	{ //Import successful!
												setContact(emailer.getNext());
												emailer.addMenus(txtSubject, txtMain);
												if (emailer.getIdFound())	{
													btnAddStudentId.setEnabled(true);
													btnAddStudentId.setSelection(true);
												}	else	{
													btnAddStudentId.setEnabled(false);
													btnAddStudentId.setSelection(false);
												}
											}	else	{
												txtName.setText("");
												txtEmail.setText("");
												txtSID.setText("");
											}
											enabled(true);
										}	else	{
											txtSystem.setText(EmailWindow.txtSystem.getText() + " ...");
										}
									}
								});
								emailer.startImport(fileLocation, sheetNumber);	
								}
						}	catch (Exception e1)	{
							writeConsole("Catastropic import error: " + e1.toString());
							emailer.importFailed("Catastropic import error: " + e1.toString());
							}
					}
				});
			btnImport.setText("Import");
			
			btnPreview = new Button(shell, SWT.NONE);
			GridData gd_btnPreview = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
			gd_btnPreview.widthHint = 57;
			btnPreview.setLayoutData(gd_btnPreview);
			btnPreview.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseDown(MouseEvent e) {
					emailer.setEmailParas(comboDropDownIS.getText().trim(), txtMain.getText(), txtSubject.getText());
					String previewResult = emailer.preview(txtEmail.getText());
					if (!(previewResult.equals("")))	{
						writeConsole(previewResult);
					}
				}
			});
			btnPreview.setText("Preview");
			
			btnSend = new Button(shell, SWT.NONE);
			GridData gd_btnSend = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
			gd_btnSend.widthHint = 58;
			btnSend.setLayoutData(gd_btnSend);
			btnSend.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseDown(MouseEvent e) {
					if (emailer.getImportSuccess())	{
						if (EmailWindow.txtSubject.getText().trim().contains("[Replace with subject]") || EmailWindow.txtSubject.getText().trim().equals("")) {
							writeConsole("Error: " + System.getProperty("line.separator") + "Please add a subject before sending to applicants!");
						}	else if (EmailWindow.txtMain.getText().trim().contains("[Replace with body of email]") || EmailWindow.txtMain.getText().trim().equals(""))	{
							writeConsole("Error: " + System.getProperty("line.separator") + "Please change the body of the email before sending to applicants!");
						}	else if (EmailWindow.txtMain.getText().trim().startsWith("dear"))	{
							writeConsole("Error: you inserted your own 'Dear'. Please remove this before sending!");
						}	else	{
							try {
								String refreshLog = txtSystem.getText();
								emailer.setEmailParas(comboDropDownIS.getText(), txtMain.getText(), txtSubject.getText());
								ConfirmDialog confirm = new ConfirmDialog(shell, SWT.SYSTEM_MODAL| SWT.CLOSE);
								if (confirm.open())	{
									enabled(false);
									emailer.refreshDisplay(new Runnable()	{
										@Override
										public void run() {
											if (emailer.getSentFinished())	{
												enabled(true);
												txtSystem.setText(refreshLog);
												String results = emailer.killRefresh();
												if (results.equals(""))	{
													writeConsole("All emails sent without error!");
												}	else if (results.startsWith("Fatal"))	{
													writeConsole("Emails not sent: " + System.getProperty("line.separator") + results.trim());
													}	else	{
														writeConsole("Emails sent with errors: " + System.getProperty("line.separator") + results.trim());
													}
											}	else	{
												txtSystem.setText("Sending to:" + dLine + "Name: " + txtName.getText() + System.getProperty("line.separator") + "Email: " + txtEmail.getText());
												setContact(emailer.getCurrent());
										}
										}
									});
									emailer.sendAll();
								}
							} catch (Exception e1) {
								emailer.writeErrors("Send error: " + e1.toString());
							}
						}
					}	else	{
						writeConsole("Error: " + System.getProperty("line.separator") + "Please import applicants before tyring to send emails!");
					}		
				}
			});
			btnSend.setText("Send");
			
			btnClear = new Button(shell, SWT.NONE);
			GridData gd_btnClear = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
			gd_btnClear.widthHint = 59;
			btnClear.setLayoutData(gd_btnClear);
			btnClear.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseDown(MouseEvent e) {
					txtMain.setText("");
					txtSubject.setText("");
				}
			});
			btnClear.setText("Clear");
			new Label(shell, SWT.NONE);
			new Label(shell, SWT.NONE);
			new Label(shell, SWT.NONE);
			
			btnImportoft = new Button(shell, SWT.NONE);
			GridData gd_btnImportoft = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
			gd_btnImportoft.widthHint = 65;
			btnImportoft.setLayoutData(gd_btnImportoft);
			btnImportoft.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseDown(MouseEvent e) {
					TemplateImport tImporter = new TemplateImport();
					try	{
						String importString = tImporter.importTemplate(shell).trim();
						if (!(importString.equals("")))	{
							txtMain.setText(importString);
						}
					}	catch (Exception e1)	{
						writeConsole("Template error: Template appears to be corrupted!");
						emailer.writeErrors("Template error: " + e1.toString());
					}
				}
			});
			btnImportoft.setText("Template");
			
			btnSetup = new Button(shell, SWT.NONE);
			btnSetup.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseDown(MouseEvent e) {
					if (comboDropDownIS.getItemCount() > 0) {
						comboDropDownIS.removeAll();
					}
					emailer.setup();
					setInboxes(comboDropDownIS, emailer.getInboxes());
					comboDropDownIS.add(emailer.getPersonal());
				}
			});
			GridData gd_btnSetup = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
			gd_btnSetup.widthHint = 72;
			btnSetup.setLayoutData(gd_btnSetup);
			btnSetup.setText("Setup");
			new Label(shell, SWT.NONE);
			
			btnFilter = new Button(shell, SWT.CHECK);
			btnFilter.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 2, 1));
			btnFilter.setToolTipText("If this is selected, the program will not block the user from importing a filtered sheet.");
			btnFilter.setText("Ignore Filters?");
			btnFilter.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
			btnFilter.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseUp(MouseEvent e) {
					if (emailer.getFilterError() == true) {
						btnFilter.setSelection(false);
						emailer.setFilterError(false);
					}	else	{
						btnFilter.setSelection(true);
						emailer.setFilterError(true);
					}
				}
			});
			
			btnAddStudentId = new Button(shell, SWT.CHECK);
			btnAddStudentId.setToolTipText("If this is selected, the program will automatically add ' - Student ID ([ID Number])' to the end of the subject.");
			btnAddStudentId.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
			btnAddStudentId.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseUp(MouseEvent e) {
					if (emailer.getAutoAdd() == true) {
						btnAddStudentId.setSelection(false);
						emailer.setAutoAdd(false);
					}	else	{
						btnAddStudentId.setSelection(true);
						emailer.setAutoAdd(true);
					}
				}
			});
			btnAddStudentId.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
			btnAddStudentId.setText("Add Student IDs?");
			btnAddStudentId.setEnabled(false);
			
			if (!(shell.isDisposed()))	{
				shell.open();
				shell.layout();
				emailer.initialise(txtSubject, txtMain);
				setInboxes(comboDropDownIS, emailer.getInboxes());
				comboDropDownIS.add(emailer.getPersonal());
			}
			
			if (!(emailer.getLoginSuccess()))	{
				return;
			}
			
			while (!shell.isDisposed()) {
				if (!display.readAndDispatch()) {
					display.sleep();
				}
			}
	}
	public static void setContact(MergeContact toSet)	{
		txtName.setText(toSet.getName());
		txtEmail.setText(toSet.getEmail());
		txtSID.setText(toSet.getID());
	}
	public static void writeConsole(String toWrite)	{
		txtSystem.setText(txtSystem.getText() + toWrite + dLine);
	}
	public static void enabled(boolean toSet)	{
		btnNext.setEnabled(toSet);
		btnPrevious.setEnabled(toSet);
		txtSID.setEnabled(toSet);
		comboDropDownIS.setEnabled(toSet);
		txtEmail.setEnabled(toSet);
		txtName.setEnabled(toSet);
		comboAttach.setEnabled(toSet);
		txtSubject.setEnabled(toSet);
		txtSystem.setEnabled(toSet);
		txtMain.setEnabled(toSet);
		btnImport.setEnabled(toSet);
		btnPreview.setEnabled(toSet);
		btnSend.setEnabled(toSet);
		btnAddAttachment.setEnabled(toSet);
		btnRemoveAttachment.setEnabled(toSet);
		btnClear.setEnabled(toSet);
		btnImportoft.setEnabled(toSet);
		btnSetup.setEnabled(toSet);
	}
	public static void setInboxes(Combo inboxDropdown, String [] toAdd)	{
		for (String eachString: toAdd)	{
			inboxDropdown.add(eachString);
		}
		inboxDropdown.select(0);
	}
}