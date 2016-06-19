package ComLog;

import ComLog.ComSecurity;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import ComLog.Util;

public class User {

	private byte[] key;
	private String name;
	private String EntryString;
	private String filePath;
	private int sessionLength;
	
	private boolean hasPreviousDayEntry = false;
	private DirectoryManager entryDirectoryM; // hold DM object of all
												// subdirectories

	public User(String userName) {

		setName(userName);
		setKey(getUserKeyFromFile(userName));
		buildUserEntryDirectoryManager();
		
		/*path to user preferences file*/
		String prefPath = name+"\\"+Util.PREFERENCES;
		// set session length by retrieving stored preference
		sessionLength = Integer.parseInt(Util.getUserPreferenceItem(prefPath, "Sess:"));

	}
	
	public int getSessionLengthInSeconds(){
		return sessionLength * 60;
	}
	
	public boolean hasPreviousDaysEntry(){
		return hasPreviousDayEntry; 
	}
	
	public void setPresenceOfYesterdaysEntry(boolean b){
		hasPreviousDayEntry = b;
	}

	/* get text entry field */
	public String getTextString() {
		return EntryString;
	}

	/* set text entry field */
	public void setUserTextString(String txt) {
		EntryString = txt;
	}

	/* set/refresh user Directory Manager Object */
	public void buildUserEntryDirectoryManager() {
		/* if user has no DirectoryMAnager object set */
		if (entryDirectoryM == null) {
			// set new DirectoryManager object for the user
			entryDirectoryM = new DirectoryManager(
					name + "\\" + Util.ENTRY_DIR, Util.SYSTEM_ROOT);

		} else {

			/*
			 * user has an existing object, check to see if DMO array of
			 * subdirectories exists
			 */
			if (entryDirectoryM.DMObjectArraySet()) {

				// set DMO array for subdirectories for main user entry
				// directory
				entryDirectoryM = new DirectoryManager(name + "\\"
						+ Util.ENTRY_DIR, Util.SYSTEM_ROOT);
				entryDirectoryM.buildDirManagerArrayOfChildren();
			} else {
				// no DMO array set, simply refresh the object
				entryDirectoryM = new DirectoryManager(name + "\\"
						+ Util.ENTRY_DIR, Util.SYSTEM_ROOT);
			}
		}

	}

	public DirectoryManager getEntryDirectoryManager() {
		return entryDirectoryM;
	}

	/**
	 * this function searches user directory for file contents based on entry
	 * type. if associated data are found for either the current date or the
	 * selected topic, contents are retrieved, de-crypted, and stored in user
	 * field for corresponding panel to grab
	 */
	public void autoSetCurrentDateText() {

		String contents = ""; // text contents. if no entry found, blank text
		String basePath = name + "\\" + Util.ENTRY_DIR;
		String date = Util.CURRENT_DATE;

		boolean dateExists = searchExistingEntryForThisDate();

		if (dateExists) {
			String fileName = date.replace("/", "-") + ".txt";
			String[] dateParts = date.split("/");
			String yearDir = basePath + "\\" + dateParts[2];
			String monthDir = yearDir + "\\" + dateParts[0];
			String fullFilePath = monthDir + "\\" + fileName;
			byte[] encryptedContents = Util.readByteArrayFromFile(fullFilePath);
			contents = ComSecurity.getDecryptedStringFromEncrypted(
					encryptedContents, key);
			/*set user filePath field to remember this file path*/
			filePath = fullFilePath;
		}

		EntryString = contents;
	}

	/**
	 * 
	 * this function searches the current user directory for a file on a given
	 * date returns true if found and accessable
	 */
	public boolean searchExistingEntryForThisDate() {
		boolean result = false;

		String basePath = name + "\\" + Util.ENTRY_DIR + "\\";
		String date = Util.CURRENT_DATE;

		String[] dateParts = date.split("/");
		String fileName = date.replace("/", "-") + ".txt";
		String yearDir = basePath + "\\" + dateParts[2];
		String monthDir = yearDir + "\\" + dateParts[0];
		String fullFilePath = monthDir + "\\" + fileName;

		Path yearPath = Paths.get(yearDir);
		Path monthPath = Paths.get(monthDir);
		Path filePath = Paths.get(fullFilePath);

		// look for month dir if year dir exists
		if (Files.exists(yearPath)) {
			// look for file if month directory exists
			if (Files.exists(monthPath)) {

				if (Files.exists(filePath)) {
					// return true if the file exists
					result = true;
				}
			}
		}
		return result;
	}

	/**
	 * This function updates user object Entry string contents, and returns a
	 * string of decrypted text for JTextField population
	 */
	public String getCurrentTextContents() {
		// autoSetCurrentDateText();
		return EntryString;
	}

	public String setTextContentsForThisDate() {
		autoSetCurrentDateText();
		return EntryString;
	}

	public byte[] getUserKeyFromFile(String username) {
		return ComSecurity.retrieveUserKey(username);
	}

	public void setKey(byte[] b) {
		key = b;
	}

	public byte[] getKey() {
		return key;
	}

	public void setName(String n) {
		name = n;
	}

	public String getName() {
		return name;
	}

	public void setFilePath(String path) {
		filePath = path;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setYesterdayFilePathAndText() {
		String fullFilePath = null; // hold value of file path

		// set base to 'User Name/ENTRIES/'
		String basePath = getName() + "\\" + Util.ENTRY_DIR + "\\";
		// split up the date, file tree = year / month / date / mm\dd\yyyy.txt
		String[] dateParts = Util.PREVIOUS_DAY.split("/");
		// build file path into string
		String datePath = dateParts[2] + "\\" + dateParts[0] + "\\";

		// build filename
		String fileName = Util.getYesterdayDateString().replace("/", "-")
				+ ".txt";

		fullFilePath = basePath + datePath + fileName;
		try {
			File previousDayEntry = new File(fullFilePath); // check if file
															// exists
			if(previousDayEntry.exists()){
			byte[] fileByteArray = Util.readByteArrayFromFile(fullFilePath); // get
																				// data

			/* decrypt data */
			String fileContents = ComSecurity.getDecryptedStringFromEncrypted(
					fileByteArray, getKey());

			/* set user fields */
			setFilePath(fullFilePath);
			setUserTextString(fileContents);
			setPresenceOfYesterdaysEntry(true);
			} else {
				JOptionPane.showMessageDialog(null,
						"No entry found for yesterday.", "No Entry Found",
						JOptionPane.INFORMATION_MESSAGE);
			}
		} catch (NullPointerException npe) {
			JOptionPane.showMessageDialog(null,
					"No entry found for yesterday.", "No Entry Found",
					JOptionPane.INFORMATION_MESSAGE);
		}
	}

	public String buildReturnNewEntryFilePath() {

		String fullFilePath = null; // hold value of file path

		// set base to 'User Name/ENTRIES/'
		String basePath = getName() + "\\" + Util.ENTRY_DIR + "\\";

		// split up the date, file tree = year / month / date / mm\dd\yyyy.txt
		String[] dateParts = Util.CURRENT_DATE.split("/");
		// build file path into string
		String datePath = dateParts[2] + "\\" + dateParts[0] + "\\";

		// build filename
		String fileName = Util.getCurrentDateString().replace("/", "-")
				+ ".txt";

		/* build directories if needed */
		Util.createDirectory(basePath + "\\" + dateParts[2]); // directory of
																// year
		Util.createDirectory(basePath + "\\" + dateParts[2] + "\\"
				+ dateParts[0]); // directory of month
		// put it all together and return;
		fullFilePath = basePath + datePath + fileName;

		return fullFilePath;
	}
	
	/*set user entry contents field with contents of given file (pass full file path)*/
	public void setUserEntryStringForSelectedFile(String fullFilePath){
		
		/*get encrypted file contents as byte array*/
		byte[] fileContents = Util.readByteArrayFromFile(fullFilePath);
		
		/*decrypt file contents*/
		String contents = ComSecurity.getDecryptedStringFromEncrypted(fileContents, getKey());
		
		EntryString = contents;
	}

	public void saveUserFile(JPanel messageCenter) {

		/*optional flag to show/not show message display*/
		// File out_file = new File(file);
		String errMsg = "Could not save data.";
		byte[] encryptedData = ComSecurity.encryptString(EntryString, key);
		boolean isSaved = Util.writeByteArrayToFile(encryptedData, filePath,
				errMsg);

		if (isSaved) {
			JOptionPane.showMessageDialog(messageCenter, "Entry saved.");
			
			
		}

	}

}
