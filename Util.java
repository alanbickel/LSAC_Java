package ComLog;

import ComLog.ComSecurity;
import ComLog.ExceptionHandler;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Scanner;

public class Util {

	/* SYSTEM PUBLIC CONSTANTS */
	public final static String USER_LOG = "Usrs.txt";
	public final static String ENTRY_DIR = "ENTRIES";
	public final static String TOPIC_DIR = "TOPICS";
	public final static String CONFIG_FILE = "Config.txt"; // key lives here
	public final static String INIT = "init.txt"; // pass lives here
	public final static String PREFERENCES = "prf.txt"; // preferences live here
	public final static String SYSTEM_ROOT = System.getProperty("user.dir");
	public static String CURRENT_DATE = getCurrentDateString();
	public static String PREVIOUS_DAY = getYesterdayDateString();

	public static boolean isGoodFileName(String inputFileName){
		boolean isGood = false;
		if(inputFileName.matches(".*[^\\w -._].*") ){
			isGood = false;
		} else {
			isGood = true;
		}
		
		return isGood;
	}
	
	public static boolean appendStringToFile(String input, String filePath) {
		// File file = new File(filePath);
		FileWriter fw = null;
		ExceptionHandler eH = null; // handle exceptions gracefully
		boolean result = false;
		try {
			fw = new FileWriter(filePath, true);
			fw.write(input);
			result = true;

		} catch (Exception e) {
			eH = new ExceptionHandler("File I/O Error", e);
		} finally {
			try {
				fw.close();
			} catch (Exception e) {
				eH = new ExceptionHandler("File I/O Error", e);
			}
		}
		return result;
	}

	public static File[] getDirectoryContents(String dirName) {
		File dir = new File(dirName);

		File[] directoryListing = dir.listFiles(); // get list of files in
													// directory

		return directoryListing;
	}

	public static void refreshDate() {

		CURRENT_DATE = getCurrentDateString();
		PREVIOUS_DAY = getYesterdayDateString();
	}

	public static String getYesterdayDateString() {
		DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -1);
		return dateFormat.format(cal.getTime());
	}

	public static String getCurrentDateString() {
		DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
		Calendar cal = Calendar.getInstance();
		// cal.add(Calendar.DATE);
		return dateFormat.format(cal.getTime());
	}

	/* Make Sure We've got a user log to write to */
	public static void buildUserLogIfNotExists() {

		File f = new File(USER_LOG);
		PrintWriter writer = null; // build reference to obj. address

		if ((!f.exists()) && !f.isDirectory()) { // if not exists, write it
			try {
				writer = new PrintWriter(USER_LOG, "UTF-8");
				writer.print("Select User");
				writer.println();
				writer.print("New User");
				writer.println();

			} catch (Exception e) {

				// pass exceptions to handler to deal with
				ExceptionHandler eH = new ExceptionHandler(
						"Unable to write user log", e);

				/* Debug runtime exception monitoring */
				// System.out.println(eH);
			} finally {
				writer.close(); // close the file!
			}
		}
	}

	/*
	 * Builds array to populate user select list at login time. Also provides
	 * the initial text option 'Select User' for the combobox. option 'new'
	 * user' will be flagged by event handler as queue to fire new user state.
	 */
	public static String[] readFileToArray(String fileName) {

		BufferedReader fileIn = null; // read buffer
		String[] contents = new String[20]; // hold file contents
		String line;
		int i = 0;

		try {

			fileIn = new BufferedReader(new FileReader(fileName)); // file
																	// stream
																	// obj.

			while ((line = fileIn.readLine()) != null) {
				contents[i] = line;
				i++;
			}

		} catch (IOException e) {

			ExceptionHandler eH = new ExceptionHandler("Unable to read file: "
					+ fileName, e);

		} finally {

			try {
				fileIn.close();
			} catch (IOException e) {

				ExceptionHandler eH = new ExceptionHandler(
						"Unable to close file: " + fileName, e);

			}

		}
		return contents;
	}

	public static void createDirectory(String directory) {
		File newUserDir = new File(directory);
		// if the directory does not exist, create it
		if (!newUserDir.exists()) {

			try {
				newUserDir.mkdir();

			} catch (SecurityException se) {

				ExceptionHandler eH = new ExceptionHandler(
						"Unable to create user directory", se);
			}
		}
	}

	public static void createNewUserDirectory(String userName, int iterations,
			JPanel targetDisplay) {

		File newUserDir = new File(userName);

		// if the directory does not exist, create it
		if (!newUserDir.exists()) {
			boolean result = false;

			try {
				newUserDir.mkdir();
				result = true;
			} catch (SecurityException se) {

				ExceptionHandler eH = new ExceptionHandler(
						"Unable to create user directory", se);
			}

			// if directory created, build sub-folders
			if (result) {
				iterations++;
				if (iterations == 1) {
					createNewUserDirectory(userName + "\\" + ENTRY_DIR,
							iterations, targetDisplay);
					createNewUserDirectory(userName + "\\" + TOPIC_DIR,
							iterations, targetDisplay);
					return;

				}

			}
		} else {
			JOptionPane.showMessageDialog(targetDisplay, "User Already Exists",
					null, JOptionPane.ERROR_MESSAGE);
		}
	}

	public static void updateUserLog(String newUserName) {

		try {

			PrintWriter out = new PrintWriter(new BufferedWriter(
					new FileWriter(USER_LOG, true)));
			out.println(newUserName);
			out.close();

		} catch (Exception e) {
			ExceptionHandler eH = new ExceptionHandler(
					"Unable to append user log", e);
			// out.close();
		} finally {

		}

	}

	/* attempts to write to file. will return true is success */
	public static boolean writeByteArrayToFile(byte[] input, String file,
			String errorMsg) {

		boolean result = false;
		BufferedOutputStream bos = null;
		try {
			// create an object of FileOutputStream
			FileOutputStream fos = new FileOutputStream(new File(file));

			// create an object of BufferedOutputStream
			bos = new BufferedOutputStream(fos);

			bos.write(input);
			bos.close();
			result = true;
			return result;
		} catch (Exception e) {

			ExceptionHandler eH = new ExceptionHandler(errorMsg, e);
			// System.out.println(eH);
		}
		return result;
	}

	/* write new user data to files in their directory */
	public static boolean writeNewUserConfigData(byte[] key, String pass,
			String userName) {
		// path to store key
		String keyPath = userName + "\\" + CONFIG_FILE;
		String passPath = userName + "\\" + INIT;
		String prefPath = userName + "\\" + PREFERENCES;

		String errorMessage = "Attempt to create config file failed. Ensure write permission to Journal root directory.";
		byte[] encodedPass = ComSecurity.encryptString(pass, key);

		boolean fileWritten = writeByteArrayToFile(key, keyPath, errorMessage);
		if (!fileWritten) {
			return false;
		}

		boolean passWritten = writeByteArrayToFile(encodedPass, passPath,
				errorMessage);
		if (!passWritten) {
			return false;
		}
		/* initialize preferences file for user */
		String sessionPreference = "Sess:5";
		boolean preferencesWritten = appendStringToFile(sessionPreference,
				prefPath);
		if (!preferencesWritten) {
			return false;
		}

		return true;
	}

	public static String getUserInputPasswordFromPopup(
			JComboBox showOnThisElement) {
		JPanel panel = new JPanel();
		JLabel label = new JLabel("Enter password:");
		JPasswordField pass = new JPasswordField(20);
		panel.add(label);
		panel.add(pass);
		String[] options = new String[] { "OK", "Cancel" };
		int option = JOptionPane.showOptionDialog(showOnThisElement, panel,
				"Confirm Identity", JOptionPane.NO_OPTION,
				JOptionPane.PLAIN_MESSAGE, null, options, pass);

		if (option == 0) // pressing OK button
		{
			String Pass = new String(pass.getPassword());
			return Pass;
		} else {
			return new String("cancel");
		}
	}

	public static byte[] readByteArrayFromFile(String fileName) {
		InputStream fis = null;
		BufferedReader br = null;
		byte[] contents = null;

		try {
			File file = new File(fileName); // get the pass file

			// convert file into array of bytes
			fis = new FileInputStream(file);

			contents = IOUtils.toByteArray(fis);

			fis.close();

		} catch (Exception e) {

			ExceptionHandler eH = new ExceptionHandler("File Read Failue. ", e);

		}
		return contents;
	}

	/*
	 * search user preference file for desired value. returns as string, convert
	 * as necessary
	 */
	public static String getUserPreferenceItem(String fileName, String flag) {

		String paramater; // hold return value
		Scanner scanner = null;

		// return options for various preference flags
		switch (flag) {

		case "Sess:": {
			paramater = "5"; // return default shortest session time if no other
								// found
		}
		default: {
			paramater = "";
		}
		}
		File file = new File(fileName);
		try {
			scanner = new Scanner(file);
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				// System.out.println("line: "+line);
				if (line.contains(flag)) { // if the line contains the desired
											// flag

					String[] itemParts = line.split(":"); // break the string to
															// get variable
					paramater = itemParts[1];
				}
			}
		} catch (Exception e) {
			ExceptionHandler eH = new ExceptionHandler(
					"Error reading preferences", e);
		}

		scanner.close();
		// System.out.println("Session value: "+paramater);
		return paramater;
	}

	public static void updatePreferenceFile(String fileName, String flag,
			String replacement) {
		//File file = new File(fileName);
		String contents = "";
		PrintWriter pWriter = null;
		int location = 0;
		String[] prefArray = readFileToArray(fileName); // get array of settings
		
		switch(flag){
		case "Sess:":{
			 location = 0;
		}
		
		}
		
			if (prefArray[location].contains(flag)) { // search for flag,
				prefArray[location] = replacement;
			
		}

		try {
			Path configPath = Paths.get(fileName); // delete the file
			Files.deleteIfExists(configPath);
          pWriter = new PrintWriter(new FileWriter(fileName), true); // open to append the file
			for (int i = 0; i < prefArray.length; i++) {
				if(prefArray[i] != null){
					if(i == location){
						prefArray[i] = replacement;
					}
					//System.out.println("contnets: '"+prefArray[i]+"'~I="+i);
					pWriter.println(prefArray[i]); // append newline char to string line
					
				}
			}
			pWriter.close();
		} catch (Exception e) {
			ExceptionHandler eH = new ExceptionHandler(
					"Error updating preferences", e);
		}

	}

	public static String getMonthNameFromStringNumber(String mon) {
		String textMonth = "";

		switch (mon) {

		case "01":
			textMonth = "January";
			break;
		case "02":
			textMonth = "February";
			break;
		case "03":
			textMonth = "March";
			break;
		case "04":
			textMonth = "April";
			break;
		case "05":
			textMonth = "May";
			break;
		case "06":
			textMonth = "June";
			break;
		case "07":
			textMonth = "July";
			break;
		case "08":
			textMonth = "August";
			break;
		case "09":
			textMonth = "September";
			break;
		case "10":
			textMonth = "October";
			break;
		case "11":
			textMonth = "November";
			break;
		case "12":
			textMonth = "December";
			break;
		}

		return textMonth;
	}

	public static String getStringMonthNumberFromName(String mon) {
		String monthNum = "";

		switch (mon) {

		case "January":
			monthNum = "01";
			break;
		case "February":
			monthNum = "02";
			break;
		case "March":
			monthNum = "03";
			break;
		case "April":
			monthNum = "04";
			break;
		case "May":
			monthNum = "05";
			break;
		case "June":
			monthNum = "06";
			break;
		case "July":
			monthNum = "07";
			break;
		case "August":
			monthNum = "08";
			break;
		case "September":
			monthNum = "09";
			break;
		case "October":
			monthNum = "10";
			break;
		case "November":
			monthNum = "11";
			break;
		case "December":
			monthNum = "12";
			break;
		}

		return monthNum;
	}
}
