package BI.common.components;

import java.io.File;
import java.io.IOException;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.xml.sax.SAXException;

public class FileValidator {

	private String message;
	private final String FILE_NOT_VALID = "Looks like your XML or XSD is not well-formed \n";

	public String getMessage() {
		return message;
	}

	// XML - XSD validator based on javax.xml.validation.Validator
	public boolean validateXMLAgainstXSD(File inputXMLfile, File inputXSDfile) {
		boolean valid = true;

		try {

			// create a SchemaFactory capable of understanding WXS schemas
			SchemaFactory schemaFactory = SchemaFactory
					.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

			// load a WXS schema, represented by a Schema instance
			Schema schema = schemaFactory.newSchema(inputXSDfile);

			// create a Validator instance, which can be used to validate an
			// instance document
			Validator validator = schema.newValidator();

			// set error handler to log all validation problems
			ValidationErrorHandler vhandler = new ValidationErrorHandler();
			validator.setErrorHandler(vhandler);

			validator.validate(new StreamSource(inputXMLfile));

			// collect validation results from the handler
			message = vhandler.getValidationMessage();
			valid = vhandler.getValidationResult();
		}

		catch (SAXException e) {
			message = FILE_NOT_VALID;
			message += e.getMessage();
			valid = false;
		} catch (IOException e) {
			message = e.getMessage();
			valid = false;
		}

		return valid;
	}

}
