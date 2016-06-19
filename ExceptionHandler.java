package ComLog;

import javax.swing.JOptionPane;

/**
 Class handles exception of all types, allows for quick alert to user if error on their part, 
 or ease of dealing wiht exceptions without catching all unique instances. 
 can look @ specific exception with 'getHiddenException()'
 or just send a simple message in 'ExceptionHandler()' String arg.*/
public class ExceptionHandler extends Exception {

	private Exception hiddenException;
	private String exceptionString;
	
	   public ExceptionHandler(String error, Exception excp)
	   {
	      super(excp);
	      
	      hiddenException= excp;
	      exceptionString = error;
	      showExceptionErrorMessage();
	      //consoleLogHiddenException();
	   }
	   
	public Exception getHiddenException()
	{
	   return(hiddenException);
	}
	
	public void consoleLogHiddenException(){
		Exception x = getHiddenException();
		System.out.println(x.getMessage());
	}
	public String toString(){
		String str = "Error: "+ hiddenException.getMessage();
		return str;
	}
	private void showExceptionErrorMessage(){

		JOptionPane.showMessageDialog(null, exceptionString);
	}
}
