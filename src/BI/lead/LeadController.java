package BI.lead;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndDocument;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartDocument;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import BI.common.components.DisableWindowTask;
import BI.common.components.FileValidator;
import BI.common.components.UpdateProgressTask;

public class LeadController {
	private JFrame mainFrame;
	private LeadPanel leadPanel;

	// elements of the sub-panel for XML file import
	private ImportFilePanel importFilePanel;
	private JFileChooser importFileChooserXML;
	private String importXMLfile;
	private JButton browseXMLbutton;

	// elements of sub-panel for selecting the extraction mode
	private SelectionPanel selectionPanel;
	private JRadioButton selectedLeadIDsRadio;
	private ButtonGroup extractRadioButtonGroup;
	private JTextArea leadInputField;

	// elements of sub-panel with the button for starting extraction
	private ExtractPanel extractPanel;
	private boolean extractionOK = false;
	private JFileChooser outputFileChooserXML;
	private JButton extractButton;
	private JLabel inProgressLabel;
	private JLabel extractionResultLabel;
	private String outputFile;
	private JLabel exportFileLabel;

	private final String NO_FILE_SELECTED = "You've forgotten to select an XML file";
	private final String INVALID_LEADS_INPUT = "Invalid leads input. Please, enter leads with space inbetween";
	private final String EXTRACTION_SUCCESSFUL = "Extraction successfully completed";
	private final String EXTRACTION_FAILED = "Extraction failed. Import file not valid";
	
	private final String encoding = "UTF-8";
	private LeadExtractionMode leadExtractionMode;
	JTabbedPane tabbedPane;

	public LeadController(JFrame mainFrame, LeadPanel lp, JTabbedPane pane) {
		this.mainFrame = mainFrame;
		tabbedPane = pane;
		leadPanel = lp;

		// initialize GUI elements of the XML file import panel
		this.importFilePanel = leadPanel.getImportFilePanel();

		browseXMLbutton = importFilePanel.getBrowseXMLbutton();
		browseXMLbutton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				showSelectedFileXML();
			}
		});

		importFileChooserXML = importFilePanel.getFileChooserXML();

		// initialize GUI elements of panel for selecting the extraction mode
		this.selectionPanel = leadPanel.getSelectionPanel();

		extractRadioButtonGroup = selectionPanel.getExtractRadioButtonGroup();

		selectedLeadIDsRadio = selectionPanel.getselectedLeadIDsRadio();
		selectedLeadIDsRadio.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				activateSelection(false);
			}
		});
		selectedLeadIDsRadio.setActionCommand("extractLeadsByIDs");

		leadInputField = selectionPanel.getLeadInputField();

		// initialize GUI elements of panel with the button for starting
		// extraction
		this.extractPanel = leadPanel.getExtractPanel();
		outputFileChooserXML = extractPanel.getfileChooserXML();
		extractButton = extractPanel.getExtractButton();
		extractButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				extractData();
			}
		});
		inProgressLabel = extractPanel.getInProgressLabel();
		extractionResultLabel = extractPanel.getExtractionResultLabel();
		exportFileLabel = extractPanel.getExportFileLabel();

	}

	private void showSelectedFileXML() {
		extractPanel.refresh();

		if (importFileChooserXML.showOpenDialog(importFilePanel) == JFileChooser.APPROVE_OPTION) {
			importFilePanel.showFileLabelXML();
			String fileName = importFileChooserXML.getSelectedFile()
					.getAbsolutePath();
			importXMLfile = fileName;
		}
	}

	private void activateSelection(boolean mode) {
		extractPanel.refresh();

	}

	private void extractAllLeads(String inputFile, String outputFile) {
		try {
			// initialize the stream reader
			InputStream inputStream = new FileInputStream(inputFile);
			XMLEventReader reader = XMLInputFactory.newInstance()
					.createXMLEventReader(inputStream);

			// initialize the stream writer
			OutputStream outputStream = new FileOutputStream(outputFile);
			XMLEventWriter writer = XMLOutputFactory.newInstance()
					.createXMLEventWriter(outputStream);

			// initialize event factory for creating instances of XML events
			XMLEventFactory eventFactory = XMLEventFactory.newInstance();

			// write XML header into the output document
			StartDocument startDocument = eventFactory.createStartDocument(
					encoding, "1.0");
			writer.add(startDocument);

			String previousEndTagName = "";

			while (reader.hasNext()) {
				// inspect each <Lead> element
				XMLEvent event = (XMLEvent) reader.next();

				switch (event.getEventType()) {
				case (XMLStreamConstants.START_ELEMENT): {

					String startTagName = event.asStartElement().getName()
							.getLocalPart().trim();

					if (startTagName.equals("Lead"))
						previousEndTagName = "";

					if (previousEndTagName.equals("Lead")
							&& !startTagName.equals("Lead")) {
						extractionOK = true;
						writer.flush();
						writer.close();
						reader.close();
						return;
					}

					StartElement startTag = eventFactory.createStartElement("",
							"", startTagName);

					writer.add(startTag);
					break;
				}

				case (XMLStreamConstants.END_ELEMENT): {
					String endTagName = event.asEndElement().getName()
							.getLocalPart().trim();
					EndElement endTag = eventFactory.createEndElement("", "",
							endTagName);

					writer.add(endTag);

					previousEndTagName = endTagName;

					break;
				}

				// Content elements are simply added to the arrayList of the
				// other sub-elements. If one of the searched lead IDs is
				// encountered - the flag leadMatchFound is set to true.
				case (XMLStreamConstants.CHARACTERS): {

					String content = event.asCharacters().getData().trim();
					Characters contentElement = eventFactory
							.createCharacters(content);

					writer.add(contentElement);

					break;
				}
				}
			}

		} catch (XMLStreamException e) {
			extractionOK = false;

		} catch (java.util.NoSuchElementException e) {
			extractionOK = false;

		} catch (FileNotFoundException e) {
			extractionOK = false;
		}
	}

	private String extractSpecifiedLeads(String importFilePath, String leadID) {
		String extract = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
		extract += "\n";
		JLabel resultLabel = extractPanel.getExtractionResultLabel();
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			factory.setIgnoringComments(true);
			factory.setIgnoringElementContentWhitespace(true);
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(importFilePath);
			// get root element, which is POS
			Element root = doc.getDocumentElement();

			// get root's children: Leads, Facts, PERS, Orgs
			NodeList mainBIelements = root.getChildNodes();
			for (int index = 0; index < mainBIelements.getLength(); index++) {
				boolean found = false;
				if (mainBIelements.item(index).getNodeName().equals("Lead")) {
					Node lead = mainBIelements.item(index);
					// extract subelements of the Lead with their values
					// and check lead ID
					NodeList leadElements = lead.getChildNodes();
					int counter = 0;
					Node subElement = leadElements.item(counter);
					while (leadElements.item(counter).getNodeName() != "LEAD_Lead_ID") {
						counter++;
						subElement = leadElements.item(counter);
					}
					if (subElement.getTextContent().equals(leadID)) {
						found = true;
						extract += "<Lead>" + "\n";
						for (int idx = 0; idx < leadElements.getLength(); idx++) {
							subElement = leadElements.item(idx);
							if (subElement.getNodeName() != "#text") {
								extract += "\t<" + subElement.getNodeName()
										+ ">" + subElement.getTextContent()
										+ "</" + subElement.getNodeName() + ">"
										+ "\n";
							}
						}
						extract += "</Lead>" + "\n";
					}
				}
				if (found) {
					break;
				}
			}// end For-loop over main BI-XML elements: Lead, Facts...
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		}
		return extract;
	}

	public void extractData() {
		extractPanel.refresh();

		String extractMode = extractRadioButtonGroup.getSelection()
				.getActionCommand();
		ArrayList<Component> componentsToDisable = collectComponents();

		if (outputFileChooserXML.showSaveDialog(extractPanel) == JFileChooser.APPROVE_OPTION) {

			// // show "extraction in progress" label
			// showExtractionInProgress();
			//
			// // disable all buttons on the dialogue
			// ArrayList <Component> components = collectComponents();
			// Runnable disableWindow = new DisableWindowTask(tabbedPane, false,
			// components);
			// SwingUtilities.invokeLater(disableWindow);

			// get the name of the output file
			outputFile = outputFileChooserXML.getSelectedFile()
					.getAbsolutePath();

			// prepare extract from XML to write into file
			if (extractMode == "extractLeadsByIDs") {
				String leads = leadInputField.getText().trim();
				ArrayList <String> leadIDs = leadsToFind(leads);
				if (leads.length() == 0) {
					LeadExtractionTask let = new LeadExtractionTask(
							importXMLfile, outputFile, extractionResultLabel,
							exportFileLabel, browseXMLbutton,
							componentsToDisable, leadIDs,
							leadExtractionMode.EXTRACTALL);
					let.execute();
				} else {
					if (validateInputLeads(leads)) {
						
						LeadExtractionTask let = new LeadExtractionTask(
								importXMLfile, outputFile,
								extractionResultLabel, exportFileLabel,
								browseXMLbutton, componentsToDisable, leadIDs,
								leadExtractionMode.EXTRACTBYIDS);
						let.execute();
					} else {

						JOptionPane.showMessageDialog(mainFrame,
								INVALID_LEADS_INPUT);
						return;
					}
				}
			}
		}
	

	}

	// make sure the input consists of numeric lead IDs separated by space
	private boolean validateInputLeads(String input) {
		String pattern = "^(\\s*\\d\\s*)*$";
		Pattern r = Pattern.compile(pattern);
		Matcher m = r.matcher(input);
		if (!m.matches())
			return false;

		return true;
	}

	// prevents too long field input: if input is longer than 300 symbols, it is
	// truncated to 300
	private String truncateFieldInput(String input) {
		int inputLength = input.length();
		if (inputLength > 300) {
			return input.substring(0, 300);
		}
		return input;
	}

	// show that validation is in progress
	private void showExtractionInProgress() {

		Runnable showTaskInProgress = new UpdateProgressTask(inProgressLabel);
		SwingUtilities.invokeLater(showTaskInProgress);

	}

	private ArrayList<Component> collectComponents() {

		ArrayList<Component> components = new ArrayList<Component>();

		components.add(browseXMLbutton);
		components.add(selectedLeadIDsRadio);
		components.add(leadInputField);
		components.add(extractButton);

		return components;
	}
	
	private ArrayList<String> leadsToFind(String input) {
		ArrayList<String> leads = new ArrayList<String>();
		int length = input.length();
		int counter = 0;
		
		if(input.length() == 0)
			return leads;
		
		while (counter < length) {
			String leadId = "";
			boolean leadIdStartFound = false;

			while ((!leadIdStartFound) && (counter < length)) {
				Character digit = input.charAt(counter);
				if (Character.isDigit(digit)) {
					leadIdStartFound = true;
					leadId += digit;
				}
				counter++;
			}

			boolean leadIdEndFound = false;
			while ((!leadIdEndFound) && (counter < length)) {
				Character digit = input.charAt(counter);
				if (Character.isWhitespace(digit)) {
					leadIdEndFound = true;
				} else {
					leadId += digit;
					counter++;
				}
			}

			leads.add(leadId);

		}

		return leads;
	}
}
