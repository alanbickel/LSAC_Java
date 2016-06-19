package ComLog;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.JOptionPane;

public class FileRemover {

	private String currentFilePath;
	private boolean fileExists;

	public FileRemover(String file) {
		currentFilePath = file;
		checkForFile();
	}

	public void checkForFile() {
		File f = new File(currentFilePath);
		if (f.exists()) {
			fileExists = true;
		} else {
			fileExists = false;
		}
	}

	public void delete() {
		if (fileExists) {
			try {
				Path filePath = Paths.get(currentFilePath);
				Files.delete(filePath);
				JOptionPane.showMessageDialog(null, "File removed successfully.");
			} catch (Exception e) {
				ExceptionHandler eH = new ExceptionHandler("Unable to delete file.", e);
			}
		} else {
			JOptionPane.showMessageDialog(null, "Unable to locate file");
		}
	}

	public void removFileInterface(String shortFileName){
		int result = JOptionPane.showConfirmDialog(null, "Permanently delete '"+shortFileName+"' ?");
		if(result == JOptionPane.OK_OPTION){
			delete();
		}
	}
}
