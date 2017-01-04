package BI.validation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import BI.common.components.DisableWindowTask;
import BI.common.components.FileValidator;


public class ValidationController {

	FilePanel filePanel;
	ResultPanel resultPanel;
	ValidationPanel validationPanel;
	ErrorLogDialog errorLogDialog;
	JFrame mainFrame;
	JTabbedPane tabPane;
	private final String NO_XML_FILE_SELECTED = "You've forgotten to select an XML file";
	private final String NO_XSD_FILE_SELECTED = "You've forgotten to select an XSD file";
	private final String XML_FILE_NOT_EXISTS = "Can not access the selected XML file";
	private final String XSD_FILE_NOT_EXISTS = "Can not access the selected XSD file";

	public ValidationController(JFrame mainFrame, JTabbedPane pane) {
		this.mainFrame = mainFrame;
		tabPane = pane;

	}

	public void showSelectedFileXML() {
		JFileChooser fileChooserXML = filePanel.getFileChooserXML();
		if (fileChooserXML.showOpenDialog(filePanel) == JFileChooser.APPROVE_OPTION) {
			filePanel.showFileLabelXML();
			String fileName = fileChooserXML.getSelectedFile()
					.getAbsolutePath();
			filePanel.setXMLfile(fileName);
			resultPanel.refresh();
		}
	}

	public void showSelectedFileXSD() {
		JFileChooser fileChooserXSD = filePanel.getFileChooserXSD();
		if (fileChooserXSD.showOpenDialog(filePanel) == JFileChooser.APPROVE_OPTION) {
			filePanel.showFileLabelXSD();
			String fileName = fileChooserXSD.getSelectedFile()
					.getAbsolutePath();
			filePanel.setXSDfile(fileName);
			resultPanel.refresh();
		}
	}

	public boolean validateXMLagainstXSD(String xmlFile, String xsdFile) {

		if (xmlFile == null) {
			JOptionPane.showMessageDialog(mainFrame, NO_XML_FILE_SELECTED);
			return false;
		}

		if (xsdFile == null) {
			JOptionPane.showMessageDialog(mainFrame, NO_XSD_FILE_SELECTED);
			return false;

		}
		File inputXMLfile = new File(xmlFile);


		File inputXSDfile = new File(xsdFile);

		resultPanel.refresh();

		resultPanel.showValidationInProgress();
		Runnable disableWindow = new DisableWindowTask(tabPane, false,
				filePanel.getComponentsToDisable());
		SwingUtilities.invokeLater(disableWindow);

		FileValidator validator = new FileValidator();

		boolean result = validator.validateXMLAgainstXSD(inputXMLfile,
				inputXSDfile);

		if (result) {
			resultPanel.showResult("Validation OK", result);
		} else {
			filePanel.setValidationError(validator.getMessage());
			resultPanel
					.showResult(
							"Validation failed. Click \"Show Error log\" to see the reason",
							result);
		}
		Runnable enableWindow = new DisableWindowTask(tabPane, true,
				filePanel.getComponentsToDisable());
		SwingUtilities.invokeLater(enableWindow);
		return true;
	}

	public void showErrorLog() {
		String errorMessage = filePanel.getValidationError();
		this.errorLogDialog = new ErrorLogDialog(mainFrame, this);
		errorLogDialog.showErrorLog(errorMessage);
		errorLogDialog.setVisible(true);
	}

	public void saveErrorLog() {
		JFileChooser fileChooserTXT = errorLogDialog.getFileChooserTXT();
		if (fileChooserTXT.showSaveDialog(filePanel) == JFileChooser.APPROVE_OPTION) {
			String errorMessage = errorLogDialog.getErrorText();
			File logFile = fileChooserTXT.getSelectedFile();
			try {
				FileWriter fileWriter = new FileWriter(
						logFile.getAbsolutePath());
				BufferedWriter out = new BufferedWriter(fileWriter);
				out.write(errorMessage);
				out.close();

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException io) {
				io.printStackTrace();
			}
		}
	}

	public void setValidationPanel(ValidationPanel vp) {
		this.validationPanel = vp;
		this.filePanel = validationPanel.getFilePanel();
		this.resultPanel = validationPanel.getResultPanel();
	}
}
