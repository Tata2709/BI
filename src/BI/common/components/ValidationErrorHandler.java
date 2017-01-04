package BI.common.components;

import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

public class ValidationErrorHandler extends DefaultHandler {
	String message = "";
	boolean valid = true;
	
	public void warning(SAXParseException e)  {
		message += "\nWarning: "; 
        printInfo(e);
        valid = false;
     }
     public void error(SAXParseException e)  {
    	message += "\nError: "; 
        printInfo(e);
        valid = false;
     }
     public void fatalError(SAXParseException e)  {
    	message += "\nFattal error: "; 
        printInfo(e);
        valid = false;
     }
     private void printInfo(SAXParseException e) {
    	 
    	 message +="\n    Message:       " + e.getMessage();
    	 message +="\n    System ID:     " + e.getSystemId();
    	 message +="\n    Line number:   " + e.getLineNumber();
    	 message += "\n    Column number: " + e.getColumnNumber();
    	 message += "\n\n\n";
    	 
     }
     
     
     public String getValidationMessage() {
 		return message;
 	}

 	public boolean getValidationResult() {
 		return valid;
 	}
  }
