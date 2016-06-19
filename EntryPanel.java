package ComLog;

import ComLog.User;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class EntryPanel extends ComLog {

	private String state; // toggle visible panels

	private ComLog parent; // reference current user, toggle program state

	private JTextArea entryText = new JTextArea(); // holds user text

	private JPanel entryPanel = new JPanel(); // hold contents of all entry
												// interface
	private JPanel browsePanel = new JPanel(); // UI for select date to view

	private JTextField currentFile; // field that holds name (date or topic name) of file being
										// viewed
	
	private JButton saveButton; // reference save button, accessed by menu to trigger file save

	/*
	 * month listener holds boolean field to toggle listening, set to false when
	 * manipulating contents of Month combo box, set to true when finished
	 * re-populating
	 */
	private MonthListener monthListener = new MonthListener();

	private JComboBox<String> yearBox; // list years available to user
	private JComboBox<String> monthBox; // list months for selected year
	private JComboBox<String> fileBox; // list files for selected month

	public EntryPanel(ComLog mainWin) {
		// set parent of this panel
		parent = mainWin;

		// interface for editing entries
		buildEntryPanel();

		// to browse existing entries
		buildBrowseEntryPanel();
	}
	
	public JButton getSaveButton(){
		return saveButton;
	}

	public void buildEntryPanel() {

		/* Title Components */
		JLabel title = new JLabel("Currently Viewing: ");
		currentFile = new JTextField();
		currentFile.setEditable(false);
		currentFile.setColumns(15);
		JPanel titlePanel = new JPanel();
		titlePanel.add(title);
		titlePanel.add(currentFile);

		/* Text Area Components */
		entryText = new JTextArea();
		entryText.setPreferredSize(new Dimension(400, 15000));
		entryText.setLineWrap(true);
		entryText.setWrapStyleWord(true);
		/* no border on text area */
		entryText.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		/* Make Scroll-able */
		JScrollPane scrollPane = new JScrollPane(entryText);
		scrollPane.setPreferredSize(new Dimension(400, 400));
		scrollPane
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		/* Add user action buttons */
		JPanel buttonPanel = new JPanel();
		saveButton = new JButton("Save");
		saveButton.addActionListener(new SaveListener());

		JButton cancelButton = new JButton("Revert");
		cancelButton.addActionListener(new SaveListener());

		/* placement of buttons */
		buttonPanel.add(saveButton);
		buttonPanel.add(cancelButton);

		/* build main entry panel */
		entryPanel.add(titlePanel);
		entryPanel.add(scrollPane);
		entryPanel.add(buttonPanel);
		entryPanel.setAlignmentY(JComponent.CENTER_ALIGNMENT);

	}

	public void buildBrowseEntryPanel() {

		/* title for browsing entry dates */
		JPanel title = new JPanel();
		JLabel tLabel = new JLabel("Please Select a date:");
		title.add(tLabel);

		/* build container for select boxes */
		JPanel selectField = new JPanel();
		selectField.setLayout(new GridLayout(2, 3));

		/* year box */
		JLabel yrTag = new JLabel("Year:");
		yearBox = new JComboBox<String>();
		yearBox.setActionCommand("year");
		yearBox.addActionListener(new DateSelectListener());

		/* month box */
		JLabel monTag = new JLabel("Month:");
		monthBox = new JComboBox<String>();
		monthBox.addActionListener(monthListener);
		monthBox.setActionCommand("month");

		/* date box */
		JLabel filTag = new JLabel("Entry:");
		fileBox = new JComboBox<String>();

		/* select fields populated upon state changes */
		selectField.add(yrTag);
		selectField.add(monTag);
		selectField.add(filTag);
		selectField.add(yearBox);
		selectField.add(monthBox);
		selectField.add(fileBox);

		/* build action button */
		JPanel buttonPanel = new JPanel();
		JButton goButton = new JButton("Load");
		goButton.setActionCommand("load");
		goButton.addActionListener(new loadButtonListener());
		buttonPanel.add(goButton);

		browsePanel.setLayout(new GridLayout(0, 1));
		browsePanel.add(new JLabel(""));
		browsePanel.add(new JLabel(""));
		browsePanel.add(title);
		browsePanel.add(selectField);
		browsePanel.add(new JLabel(""));
		browsePanel.add(buttonPanel);
	}

	public JPanel getEntryPanel() {
		return entryPanel;
	}

	public JPanel getBrowseEntryPanel() {
		return browsePanel;
	}

	public void setCurrentFileName(String date) {
		currentFile.setText(date);
	}

	public void setParent(ComLog c) {
		parent = c;
	}

	public User getUser() {
		return parent.getCurrentUser();
	}

	public JTextArea getTextArea() {
		return entryText;
	}

	public String getShortFileName(){
		return currentFile.getText();
	}
	/* POPULATE YEAR/DATE COMBO BOX WITH INITIAL USER DATA */
	public void loadDateSelectBoxes() {

		/* get user's file directory manager */
		DirectoryManager userDM = getUser().getEntryDirectoryManager();

		/* get list of years from directory manager */
		String[] userYears = userDM.getDMOStringArray();
		int yrLen = userYears.length + 1;

		// build initialization list of available years
		String[] yearOptions = new String[yrLen];
		yearOptions[0] = "Select Year";
		for (int i = 1; i < yrLen; i++) { // add available years to combo box
			yearOptions[i] = userYears[i - 1];
		}

		/* initialize the year select box */
		yearBox.setModel(new StringBoxModel(yearOptions));

		/* initialize month box/ refresh on each listen of year box select */
		String[] monthOptions = { "Select Month" };

		monthListener.toggleActionListener(false); // shut off listener while
													// re-building month select
													// box
		monthBox.setModel(new StringBoxModel(monthOptions));
		monthListener.toggleActionListener(true); // turn action listener back
													// on
		// refresh file box options
		fileBox.removeAllItems();
		fileBox.addItem("Select File");

	}

	public void updateComponentState(String str) {
		state = str;
	}

	public void setComponentState(String str) {

		updateComponentState(str); // set state to member field

		switch (state) {

		case "NEW_ENTRY": {

			// make browse entries panel invisible
			browsePanel.setVisible(false);
			// make new entry panel visible
			entryPanel.setVisible(true);
			/* Display current date */
			currentFile.setText(Util.getCurrentDateString());
			//System.out.println("date: "+Util.getCurrentDateString());
			// set text contents for this date based on
			// directory name (new entry) and to search for previous posts to
			// this date
			User user = parent.getCurrentUser();
			String currentText = user.setTextContentsForThisDate();
			entryText.setText(currentText);
			break;
		}
		case "BROWSE_ENTRY": {

			// make browse entries panel visible
			browsePanel.setVisible(true);
			// make edit entry panel invisible
			entryPanel.setVisible(false);

			/* refresh user file tree */
			parent.getCurrentUser().buildUserEntryDirectoryManager();

			/* initialize and populate user file options */
			loadDateSelectBoxes();

			break;
		}

		case "EDIT_ENTRY": {

			// make browse entries panel invisible
			browsePanel.setVisible(false);
			// make new entry panel visible
			entryPanel.setVisible(true);
			/* Display cuttent date */
			//EditTitleDate.setText(Util.getYesterdayDateString());
			entryText.setText(getUser().getTextString());

			break;
		}

		case "YESTERDAY": {

			/* get file path and contents for yesterday's entry if exists */
			getUser().setYesterdayFilePathAndText();

			/* only show edit panel if pervious date exists */
			boolean hasYesterdayEntry = getUser().hasPreviousDaysEntry();

			if (hasYesterdayEntry) {
				// make browse entries panel invisible
				browsePanel.setVisible(false);
				// make new entry panel visible
				entryPanel.setVisible(true);
				/* Display cuttent date */
				currentFile.setText(Util.getYesterdayDateString());
				/*reset value of yesterday's entry, force check for existence on each load,
				 * this way if user runs program during date rollover, false 'true will not occur'*/
				entryText.setText(getUser().getTextString());
				getUser().setPresenceOfYesterdaysEntry(false);
			/*revert to 'new entry' state*/
			} else {
				parent.changeUIState(States.progState.NEW_ENTRY
						.tellState());
			}
			break;
		}
		case "EDIT_TOPIC":{
			/*show entry panel*/
			browsePanel.setVisible(false);
			entryPanel.setVisible(true);
		}

		default: {
			/* HIDE ALL PANELS */
			browsePanel.setVisible(false);
			entryPanel.setVisible(false);
		}

		}
	}

	/* combo box model for building with string array */
	private class StringBoxModel extends DefaultComboBoxModel {
		public StringBoxModel(String[] items) {
			super(items);
		}
	}

	/* ACTION LISTENERS */

	// save/ cancel new entry button actions
	private class SaveListener implements ActionListener {

		public void actionPerformed(ActionEvent e) {

			String command = e.getActionCommand(); // find out what button the
													// user clicked

			if (command.equals("Save")) {

				// if new entry, build file path. else this path is set prior to
				// state change by actionListeners
				if (state.equals("NEW_ENTRY")) {
					// get file path for new entry
					String newFilePath = getUser()
							.buildReturnNewEntryFilePath();
					// set user file path
					getUser().setFilePath(newFilePath);
				}

				/* save entry to user field, saving handled by user object */
				getUser().setUserTextString(entryText.getText());

				getUser().saveUserFile(entryPanel);

			} else { // user has clicked 'cancel'

				// revert to most recently retreived file contents (auto set to
				// "" for new entry)
				entryText.setText(getUser().getCurrentTextContents());
			}
		}
	}

	/* action listener for month select box */
	private class MonthListener implements ActionListener {

		/* toggled T/F while manipulating contents of select box */
		private boolean listenerIsActive = false;

		public void toggleActionListener(boolean b) {
			listenerIsActive = b;
		}

		public void actionPerformed(ActionEvent e) {
			if (listenerIsActive) {
				/* get user file tree */
				DirectoryManager userDM = getUser().getEntryDirectoryManager();

				String command = e.getActionCommand(); // get command of firing

				String item = (String) ((JComboBox) e.getSource())
						.getSelectedItem(); // get selected item
				if (command.equals("month")) {

					if (((JComboBox) e.getSource()) == monthBox) { //

						if (((JComboBox) e.getSource()).getSelectedIndex() != 0) {

							// get selected year
							String yr = (String) yearBox.getSelectedItem();
							// get directory where months subfolders live
							String parentDir = userDM.getDirectoryName() + "\\"
									+ yr;

							// convert month name to number string
							String monthNum = Util
									.getStringMonthNumberFromName(item);

							// get directory manager for file directory
							DirectoryManager monDM = new DirectoryManager(
									monthNum, parentDir);
							// Get string array of file names
							String[] files = monDM.getDMOStringArray(); // get
																		// list
																		// of
																		// months
																		// in
																		// selected
																		// year
							fileBox.removeAllItems();
							fileBox.addItem("Select File");
							for (String file : files) {
								// remove file extension from string
								String fileName = file.replace(".txt", "");
								fileBox.addItem(fileName);
							}

						}

					}
				}
			}
		}
	}

	/* action listener for year select box */
	private class DateSelectListener implements ActionListener {

		public void actionPerformed(ActionEvent e) {

			String command = e.getActionCommand();

			JComboBox cb = (JComboBox) e.getSource(); // reference to the
														// combobox object

			String item = (String) cb.getSelectedItem(); // get selected item

			// access to user file tree
			DirectoryManager userDM = parent.getCurrentUser()
					.getEntryDirectoryManager();

			if (cb == yearBox && (!(item.equals(null)))) { // if year changed

				if (!(item.equals("Select Year"))) { // if value selected

					// create subdirectory manager to get months for select box
					DirectoryManager yrDM = new DirectoryManager(item,
							userDM.getDirectoryName());
					String[] mons = yrDM.getDMOStringArray(); // get list of
																// months in
																// selected year
					// shut down month box action listener
					monthListener.toggleActionListener(false);

					monthBox.removeAllItems();
					monthBox.addItem("Select Month");
					for (String month : mons) {
						String monthName = Util
								.getMonthNameFromStringNumber(month);
						monthBox.addItem(monthName);
					}

					// enable month box action listener again
					monthListener.toggleActionListener(true);

				}

			}

		}

	}

	/* action listener for load button */
	private class loadButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {

			String year = (String) yearBox.getSelectedItem(); // get year
			String month = (String) monthBox.getSelectedItem(); // get month
			String monthNum = Util.getStringMonthNumberFromName(month); // get
																		// numeric
																		// representation
																		// of
																		// month
																		// name
			String file = (String) fileBox.getSelectedItem(); // get file

			/* test for default 'Select item' presence */
			if (!(year.equals("Select Year"))) {
				if (!(month.equals("Select Month"))) {
					if (!(file.equals("Select File"))) {

						// access to user file tree
						DirectoryManager userDM = getUser()
								.getEntryDirectoryManager();

						/* build file string */
						String root = userDM.getDirectoryName();
						String selectedFile = root + "\\" + year + "\\"
								+ monthNum + "\\" + file + ".txt";

						/* get encrypted byte array from file */
						byte[] encFile = Util
								.readByteArrayFromFile(selectedFile);

						/* decrypt to readable string */
						String entry = ComSecurity
								.getDecryptedStringFromEncrypted(encFile,
										getUser().getKey());
//System.out.println(entry);
						/* set user entry field */
						getUser().setUserTextString(entry);

						/* set file path to save edits */
						getUser().setFilePath(selectedFile);

						/* Change Entry Date for display */
						setCurrentFileName(file);
						// editDate = file;

						/* change program state to 'Edit Entry' */
						parent.changeUIState(States.progState.EDIT_ENTRY
								.tellState());

					}
				}
			}
		}
	}

}
