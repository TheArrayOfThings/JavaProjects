package main;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

//Series of functions relating to adding menus to SWT text objects

public class TextMenu {
	private Text textToAdd;
	public TextMenu (Text textToAddPara)	{
		textToAdd = textToAddPara;
	}
	private void insertString(String toAdd)	{
		int caretPosition = textToAdd.getSelection().x;
		int selectedChars = textToAdd.getSelectionCount();
		if (selectedChars > 0)	{
			textToAdd.setText(textToAdd.getText(0, (caretPosition - 1)) + toAdd + textToAdd.getText(caretPosition + selectedChars, textToAdd.getText().length()));
			textToAdd.setSelection(caretPosition + toAdd.length());
		}	else	{
			textToAdd.setText(textToAdd.getText(0, (caretPosition - 1)) + toAdd + textToAdd.getText(caretPosition, textToAdd.getText().length()));
			textToAdd.setSelection(caretPosition + toAdd.length());
		}
	}
	private Menu createBasicMenu(String[] mergeList)	{
		 Menu popupMenu = new Menu(textToAdd);	 
		 MenuItem item = new MenuItem (popupMenu, SWT.PUSH);
		 MenuItem addField = new MenuItem(popupMenu, SWT.CASCADE);
		 addField.setText("Insert Merge Field");
		 item.setText("Cut");
		 item.addListener(SWT.Selection, new Listener()	{
				@Override
				public void handleEvent(Event event) {
					textToAdd.cut();
				}
	 	});
		 item = new MenuItem (popupMenu, SWT.PUSH);
		 item.setText("Copy");
		 item.addListener(SWT.Selection, new Listener()	{
				@Override
				public void handleEvent(Event event) {
					textToAdd.copy();
				}
	 	});
		 item = new MenuItem(popupMenu, SWT.PUSH);
		 item.setText("Paste");
		 item.addListener(SWT.Selection, new Listener()	{
				@Override
				public void handleEvent(Event event) {
					textToAdd.paste();
				}
	 	});
		 item = new MenuItem(popupMenu, SWT.PUSH);
		 item.setText("Select All");
		 item.addListener(SWT.Selection, new Listener()	{
				@Override
				public void handleEvent(Event event) {
					textToAdd.selectAll();
				}
	 	});
		 Menu mergeMenu = new Menu(popupMenu);
		 for (String eachString: mergeList)	{
			 MenuItem field = new MenuItem(mergeMenu, SWT.CASCADE);
			 field.setText(eachString);
			 field.addListener(SWT.Selection, new Listener()	{
				 @Override
				 public void handleEvent(Event event) {
					 insertString("<<" + eachString + ">>");
					 }
				 });
			 }
		 addField.setMenu(mergeMenu);
		 return popupMenu;
	}
	private void insertTag(String tagToAdd)	{
		String toInsert = "", toEnd = "";
		int caretPosition = textToAdd.getSelection().x;
		int selectedChars = textToAdd.getSelectionCount();
		switch (tagToAdd)	{
		case "Bold": toInsert = "<b>"; toEnd = "</b>";
		break;
		case "Italics": toInsert = "<i>"; toEnd = "</i>";
		break;
		case "Strikethrough": toInsert = "<s>"; toEnd = "</s>";
		break;
		case "Underline": toInsert = "<u>"; toEnd ="</u>";
		break;
		case "Red": toInsert = "<span style='color:red'>"; toEnd = "</span>";
		break;
		case "Blue": toInsert = "<span style='color:blue'>"; toEnd = "</span>";
		break;
		case "Green": toInsert = "<span style='color:green'>"; toEnd = "</span>";
		break;
		case "Custom Colour":
			ColorDialog colourDialog = new ColorDialog(new Shell(), SWT.OPEN);
			RGB customColourRGB = colourDialog.open();
			if (customColourRGB == null || customColourRGB.toString().equals(""))	{
				return;
			}
			String customColour = String.format("#%02x%02x%02x", customColourRGB.red, customColourRGB.green, customColourRGB.blue); 
			toInsert = "<span style='color:" + customColour + "'>"; toEnd = "</span>";
		break;
		}
		if (selectedChars > 0)	{
			textToAdd.setText(textToAdd.getText(0, (caretPosition - 1)) + toInsert + textToAdd.getText(caretPosition, (caretPosition + selectedChars) - 1) + toEnd + textToAdd.getText(caretPosition + selectedChars, textToAdd.getText().length()));
			textToAdd.setSelection(caretPosition + toInsert.length() + toEnd.length() + selectedChars);
		}
	}
	private void insertHyperlink()	{
		String toInsert = "", toEnd = "";
		int caretPosition = textToAdd.getSelection().x;
		int selectedChars = textToAdd.getSelectionCount();
		String linkToAdd = new HyperlinkDialog(new Shell(), SWT.CLOSE | SWT.SYSTEM_MODAL).open();
		toInsert = "<a href='" + linkToAdd + "'>"; toEnd = "</a>";
		if (selectedChars > 0 && linkToAdd != null && (!(linkToAdd.equals(""))))	{
			textToAdd.setText(textToAdd.getText(0, (caretPosition - 1)) + toInsert + textToAdd.getText(caretPosition, (caretPosition + selectedChars) - 1) + toEnd + textToAdd.getText(caretPosition + selectedChars, textToAdd.getText().length()));
			textToAdd.setSelection(caretPosition + toInsert.length() + toEnd.length() + selectedChars);
		}
	}
	public void addMainMenu(String [] mergeList)	{
		Menu mainPopupMenu = createBasicMenu(mergeList);
		MenuItem addTag = new MenuItem(mainPopupMenu, SWT.CASCADE);
		addTag.setText("Add Style to Selection");
		Menu tagMenu = new Menu(mainPopupMenu);
		String[] supportedTags = new String[]	{
				"Bold", "Italics", "Strikethrough", "Underline", "Red", "Blue", "Green", "Custom Colour"
				};
		for (String eachString: supportedTags)	{
			MenuItem tag = new MenuItem(tagMenu, SWT.CASCADE);
			tag.setText(eachString);
			tag.addListener(SWT.Selection, new Listener()	{
				@Override
				public void handleEvent(Event event) {
					insertTag(eachString);
					}
				});
			}
		MenuItem hyperlink = new MenuItem(mainPopupMenu, SWT.CASCADE);
		hyperlink.setText("Hyperlink Selection");
		hyperlink.addListener(SWT.Selection, new Listener()	{
			@Override
			public void handleEvent(Event event) {
				insertHyperlink();
				}
			});
		addTag.setMenu(tagMenu);
		textToAdd.setMenu(mainPopupMenu);
		}
	public void addSubjectMenu(String[] mergeList)	{
		Menu subjectPopupMenu = createBasicMenu(mergeList);
		textToAdd.setMenu(subjectPopupMenu);
		}
	}
