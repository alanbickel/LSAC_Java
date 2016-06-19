package ComLog;

import javax.swing.JOptionPane;

import java.util.ArrayList;
import java.util.Locale;


/**
 *gets search query from user, directory to search is set from menu selection.
 *builds list of all files in the directory, then searches each file for the 
 *string given by user.  both lists stored as arrayList<String>. each search implements
 *new object instance, this object is not recycled.
 *the searchResultPanel object maintains a boolean field based on the size of
 * 'fileMatches' arrayList (passed as an arg to the buildResultPanel method)
 *to determine whether to display the results (i.e. empty list = don't show results)
 */
 
public class SearchModule {
	
	private ComLog mainWin; //parent 
	private String searchString; // string to be searched
	private User user; // current user
	private DirectoryManager directory; // hold main directory to search
	private ArrayList<String> fileNameArray; // hold list of all file names in directory to be searched
	private ArrayList<String> fileMatches; // hold list of all files containing search term
	
	public SearchModule(ComLog parent, String searchDirectory){
		mainWin = parent; // set main window reference
		user = mainWin.getCurrentUser(); // set user
		searchForContent(searchDirectory); // wrapper function
		
	}
	
	public void getAndSetSearchString(){
		
		/*get user search term*/
		 String search = (String) JOptionPane.showInputDialog(null,
				"Query:", "Search Content",
				JOptionPane.QUESTION_MESSAGE);
		
		if(!(search == null)){  // if user doesn't cancel
			
			if((!(search.equals(""))&&(!(search.equals(" "))))){ // if not empty string
				
				searchString = search; // set search string for later use
				
			} else { // alert user, can't search empty string
				JOptionPane.showMessageDialog(null,
						"Cannot search empty string:", "Search Error",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	public String getSearchString(){
		return searchString;
	}
	
	/*get the list of files that contain search term*/
	public ArrayList<String> getMatchedFiles(){
		return fileMatches;
	}

	/*supply directory to search, build user directory manager to traverse directories*/
	public void setSearchDirectory(String d){
		directory = new DirectoryManager(d, user.getName());
		
	}
	
	/*build string array of all file names (full paths) in the chosen directory to search*/
	public void buildfileNameArray(){
		
		String entryType = directory.getDirectoryName(); // base search location on this value
		
		String baseDir = directory.getFilePath(); // get base directory to be searched 

		if(entryType.equals(Util.ENTRY_DIR)){ // if user wants to search dated entries
			
			fileNameArray = new ArrayList<String>(); //  object to hold list of topic files to search
			
			/*get year directories as string array*/
			String[] years = directory.getDMOStringArray(); // build DMO array of year directories
			
			
			for(int i =0; i <years.length; i++ ){ // each year directory has a  array of month directories
				
				DirectoryManager year =  new DirectoryManager(years[i],baseDir ); // build DMO of years
				
				String[] months = year.getDMOStringArray(); // build DMO array of year directories
				
				for(int j=0; j < months.length; j++ ){ // each month DMO array holds file names

					DirectoryManager month =  new DirectoryManager(months[j], year.getFilePath() ); // build DMO of years
					
					String[] files = month.getDMOStringArray(); // get list of files in directory array
					
					
					for(String f : files){ // iterate each file
						fileNameArray.add(baseDir+"\\"+years[i]+"\\"+months[j]+"\\"+f); // add files to list
					}
					
				}
			}
			
			
		} else { // user wants to search topic directory
			
			String[] fileNames = directory.getDMOStringArray(); // get file names
			
			int arrLen = fileNames.length;
			
			fileNameArray = new ArrayList<String>(); // hold list of topic files to search
			
			for(int i=0; i <arrLen; i++){
				//System.out.println(directory.getFilePath() +"\\" + fileNames[i]);
				fileNameArray.add(directory.getFilePath() +"\\" + fileNames[i]);
			}
		}
	}

	/*search populated array list of files for user defined search term. 
	 * add files that match to arrayList of hits*/
	public void searchFilesSetMatchArray(){ 
		
		byte[] userKey = user.getKey(); //decrypt key
		
		fileMatches = new ArrayList<String>(); // make new list 
		
		for(String file: fileNameArray){ // iterate through all files, look for a match
			
			byte[] encryptedContents = Util.readByteArrayFromFile(file); // get encrypted contents
			
			String contents = ComSecurity.getDecryptedStringFromEncrypted( // get readable string
					encryptedContents, userKey);
		    Locale defaultLocale = Locale.getDefault();

			/*convert both strings to lower case to perform case-insensitive search*/
			String caseSearch = searchString.toLowerCase(defaultLocale);
			String caseContents = contents.toLowerCase(defaultLocale);
			
			if(caseContents.contains(caseSearch)){ // if match, add file to list of matches
				fileMatches.add(file);
			}

		}
	}
	
	public void searchForContent(String directoryToSearch){
		getAndSetSearchString(); // get search criteria
		setSearchDirectory(directoryToSearch); // set directory to search
		buildfileNameArray(); // get list of all files in directory
		searchFilesSetMatchArray(); // place all files with match in list

	}
}
