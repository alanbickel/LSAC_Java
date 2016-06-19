package ComLog;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Display interface for search results
 */
public class SearchResultPanel {
	private ComLog mainWin; // reference current user, toggle program state
	private String state; // toggle visible panels

	/*
	 * passed by action event from menu option, this object holds list of files
	 * containing search term, file paths
	 */
	private SearchModule module;

	private JPanel browsePanel = new JPanel(); // hold interface for perusing
												// existing topics

	/* display search term */
	private JLabel searchText;
	private JPanel resultTable;
	private JScrollPane scroller;

	private boolean queryHasResults = false;// set on each query/ decided wether
											// or not to show search result
											// panel

	public SearchResultPanel(ComLog parent) {
		mainWin = parent;
		buildResultPanel();
	}

	public JPanel getResultPanel() {
		return browsePanel;
	}

	public void setSearchTermText(String s) {
		searchText.setText(s);
	}

	/* object instance of search module */
	public void setSearchModule(SearchModule m) {
		module = m;
	}

	public void buildResultPanel() {
		searchText = new JLabel();
		searchText.setAlignmentX(SwingConstants.CENTER);
		JLabel searchLabel = new JLabel("Search results for: ");
		JPanel titlePanel = new JPanel();
		titlePanel.setLayout(new GridLayout(2, 1));
		titlePanel.add(searchLabel);
		titlePanel.add(searchText);

		browsePanel.add(titlePanel); // add search term display to main panel
	}

	/* set state of display */
	public void updateComponentState(String str) {
		state = str;
	}

	public void buildResultTable(ArrayList<String> list) {

		if (scroller != null) { // if this panel contains results from previous
								// search, remove them.
			browsePanel.remove(scroller);
		}
		resultTable = new JPanel(); // display contents of search results
		resultTable.setLayout(new GridLayout(0, 1));

		list.trimToSize(); // clear any potential trailing null elements
		if (!(list.size() > 0)) { // if no results from search

			/*
			 * set boolean value to false, prevent showing of this JPanel in
			 * setComponentState() method.
			 */

			queryHasResults = false;
			JOptionPane.showMessageDialog(null, // alert user of no result
					"No Match found for '" + module.getSearchString() + "'");
			mainWin.changeUIState(States.progState.NEW_ENTRY.tellState()); // return
																			// to
																			// 'new
																			// entry'
																			// state
			return;

		} else {
			queryHasResults = true; // flag to determine display state in
									// setComponentState() method
		}
		/* loop through matching files, get decoded text for each */
		for (int i = 0; i < list.size(); i++) {

			File file = new File(list.get(i)); // get file
			String fileName = file.getName().replace(".txt", ""); // get name
																	// minus
																	// file
																	// extensions

			byte[] encryptedContents = Util.readByteArrayFromFile(list.get(i)); // get
																				// encrypted
																				// contents
			String contents = ComSecurity.getDecryptedStringFromEncrypted( // get
																			// readable
																			// string
					encryptedContents, mainWin.getCurrentUser().getKey());

			/* lower-case comparison */
			Locale defaultLocale = Locale.getDefault(); // get locale (character
														// set)

			String caseSearch = module.getSearchString().toLowerCase(
					defaultLocale);

			String caseContents = contents.toLowerCase(defaultLocale);

			if ((caseContents.indexOf(caseSearch) != -1)) { // double
															// check
															// there
															// is a
															// match

				/*
				 * get a substring 45 char before search phrase, and 45 char
				 * after.
				 */

				int beginningIndex = contents.indexOf(module.getSearchString()) - 45;
				if (beginningIndex < 0) { // eliminate potential 'out of bounds'
											// exception.
					beginningIndex = 0;
				}
				int endingIndex = beginningIndex
						+ module.getSearchString().length() + 45;
				if (endingIndex > contents.length()) {
					endingIndex = contents.length(); // eliminate potential 'out
														// of bounds' exception.
				}

				/*display snippet of entry, to provide context of search term*/
				String sample = contents.substring(beginningIndex, endingIndex); 

				JButton button = new JButton(fileName);
				button.setActionCommand(list.get(i)); // pass full file path
														// as action command
														// to be retrieved
														// by listener.
				button.addActionListener(new SearchListener());
				JTextArea sampleText = new JTextArea(sample);
				sampleText.setEditable(false);
				sampleText.setPreferredSize(new Dimension(200, 75));
				sampleText.setLineWrap(true);
				sampleText.setWrapStyleWord(true);

				JPanel row = new JPanel();
				row.setBorder(BorderFactory.createEtchedBorder());
				row.add(button);
				row.add(sampleText);

				resultTable.add(row); // add each found entry sample/button to display panel
			}

		}
		scroller = new JScrollPane(resultTable); // container for results
		scroller.setPreferredSize(new Dimension(370, 300));
		scroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		browsePanel.add(scroller);
		browsePanel.add(new JLabel(""));
	}

	public void setComponentState(String str) {
		updateComponentState(str);

		switch (state) {
		case "SEARCH_RESULT": {
			/*
			 * populate interface with data from searchModule object. this state
			 * will only be ever active after assignment of SearchModule
			 */

			searchText.setText(module.getSearchString());
			buildResultTable(module.getMatchedFiles());

			/* check for existence of search results before showing this JPanel */
			if (!queryHasResults) { // flag set in 'buildResultTable()' method
				
				break; // exit without showing this display panel
			}
			browsePanel.setVisible(true);
			break;
		}
		default: {
			browsePanel.setVisible(false);
			break;
		}
		}
	}

	/*action listener for buttons generated by search result*/
	private class SearchListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {

			String filePath = e.getActionCommand(); // file path set as action
													// command when button
													// created, this is the file path of clicked entry

			String entryName = null; // hold file name stripped of extension
			// get file name for display
			String[] fileNameArray = filePath.split("\\\\");
			for (String f : fileNameArray) {
				if (f.contains(".txt")) {
					entryName = f.replace(".txt", ""); // file name without path or extension (human readable)
				}
			}

			// set user file path
			mainWin.getCurrentUser().setFilePath(filePath); // set file path for user object (allow savinf of this file)
			// get encoded byte array for file path
			byte[] encFile = Util.readByteArrayFromFile(filePath);
			// decrypt
			String entry = ComSecurity.getDecryptedStringFromEncrypted(encFile,
					mainWin.getCurrentUser().getKey());
			// set user text
			mainWin.getCurrentUser().setUserTextString(entry);
			// set entry panel text
			// set entry panel file name
			mainWin.getCenterPanel().getEntryObject()
					.setCurrentFileName(entryName);
			// update state to edit entry
			mainWin.changeUIState(States.progState.EDIT_ENTRY.tellState());
		}
	}
}
