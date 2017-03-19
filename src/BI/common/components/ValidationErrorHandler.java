package BI.common.components;

import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

public class ValidationErrorHandler extends DefaultHandler {
	private String message = "";
	private boolean valid = true;

	// return validation warning
	public void warning(SAXParseException e) {
		message += "\nWarning: ";
		printInfo(e);
		valid = false;
	}

	// return validation error
	public void error(SAXParseException e) {
		message += "\nError: ";
		printInfo(e);
		valid = false;
	}

	// return validation fatal error
	public void fatalError(SAXParseException e) {
		message += "\nFattal error: ";
		printInfo(e);
		valid = false;
	}

	// assembles message to be printed in case of validation failure
	private void printInfo(SAXParseException e) {

		message += "\n    Message:       " + e.getMessage();
		message += "\n    System ID:     " + e.getSystemId();
		message += "\n    Line number:   " + e.getLineNumber();
		message += "\n    Column number: " + e.getColumnNumber();
		message += "\n\n\n";

	}

	// returns the validation message
	public String getValidationMessage() {
		return message;
	}

	// returns the validation result
	public boolean getValidationResult() {
		return valid;
	}
}
