package ComLog;

import java.io.File;

public class DirectoryManager {
	
	private String directoryName;		//hold directory to be traversed
	private String parentDirectory;		//parent directory passed/set in construct
	private String filePath;			//current filepath
	private File[] subDirectories;		// hold all sub directories as file objects
	private String[] subDirNames; 		// hold all sub directory string values 
	private DirectoryManager[] subObjects; // optional array of Dir.Mgr. objects for immediate subdirectories

	/**
	 * builds sub-directory structure of given directory in both File object format 
	 * and string format, both identically build.  File[0] == String[0], etc...
	 * 
	 * Optionally constructs DirectoryManager array of sub-directories 
	 * 
	 * provides methods for retrieving components individually, as well as parent directory,
	 * current file path, and a copy construct.
	 * 
	 * @! IMPORTANT: both parent and current directory must be passed without trailing '\\'
	 * 
	 * @param name : name of directory to be traversed
	 * @param parent :name of direct parent directory 
	 */
	
	public DirectoryManager(String name, String parent){
		// set directory name
		directoryName = name;
		parentDirectory = parent;
		// build current file path
		filePath = parentDirectory + "\\" + directoryName;
		//System.out.println("filePath:" + filePath);
		// get files in directory
		File[] files = Util.getDirectoryContents(filePath);
		
		subDirectories = new File[files.length];
		subDirNames = new String[files.length];
		for(int i = 0; i < files.length; i++){
			// build file array
			subDirectories[i] = files[i];
			// build String array
			subDirNames[i] = files[i].getName();
		}
		 
	}
	
	/*
	 * copy construct
	 */
	public DirectoryManager(DirectoryManager d){
		directoryName = d.directoryName;
		parentDirectory = d.parentDirectory;
		filePath = d.filePath;
		subDirectories = d.subDirectories;
		subDirNames = d.subDirNames;
		subObjects = d.subObjects;
	}
	
	/*optional construction of sub-directory D.M. objects*/
	public void buildDirManagerArrayOfChildren(){
		
		subObjects = new DirectoryManager[subDirectories.length];
		
		for(int i = 0; i < subDirectories.length; i++){
			subObjects[i] = new DirectoryManager(subDirNames[i], directoryName);
		}
	}

	/*get object(directory) name(value)*/
	public String getDirectoryName(){
		return directoryName;
	}
	
	/**
	 *  test if DirectoryManager array has been filled
	 */
	public boolean DMObjectArraySet(){
		if(subObjects != null){
			return true;
		} else {
			return false;
		}
	}
	
	/*get File array of sub-directories*/
	public File[] getDMOFileArray(){
		return subDirectories;
	}

	/*get string array of directory names*/
	public String[] getDMOStringArray(){
		return subDirNames;
	}
	
	/**
	 * get DirectoryManager array. Always check existence with 'DMObjectArraySet()'
	 * prior to calling this function
	 * to avoid possible null pointer error.
	 */
	public DirectoryManager[] getDMO_directory_array(){
		return subObjects;
	}
	public String getFilePath(){
		return filePath;
	}
	
	
}
