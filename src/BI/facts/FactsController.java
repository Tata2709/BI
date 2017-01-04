package BI.facts;

import java.awt.Component;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartDocument;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import BI.common.components.DisableWindowTask;

import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndDocument;
import javax.xml.stream.events.EndElement;

import com.sun.xml.internal.fastinfoset.stax.events.XMLConstants;

public class FactsController {
	private FactsPanel factsPanel;
	private ImportFilePanel importFilePanel;
	private SelectionPanel selectionPanel;
	private ExtractPanel extractPanel;
	boolean extractionOK;
	private JFrame mainFrame;
	private JTabbedPane tabPane;
	private JPanel[] panels = new JPanel[3];
	private final String NO_FILE_SELECTED = "You've forgotten to select an XML file";
	private final String NO_FACTS_SELECTED = "Please, select at least one fact";
	private final String INVALID_LEADS_INPUT = "Invalid leads input. Please, enter leads with space inbetween";
	private final String EXTRACTION_SUCCESSFUL = "Extraction successfully completed";
	private final String EXTRACTION_FAILED = "Extraction failed. Import file not valid";
	private final String encoding = "UTF-8";

	public FactsController(JFrame frame, JTabbedPane pane) {
		mainFrame = frame;
		tabPane = pane;

	};

	public void setFactsPanel(FactsPanel fp) {
		this.factsPanel = fp;

		this.importFilePanel = factsPanel.getImportFilePanel();
		this.extractPanel = factsPanel.getExtractPanel();
		this.selectionPanel = factsPanel.getSelectionPanel();

		panels[0] = importFilePanel;
		panels[1] = extractPanel;
		panels[2] = selectionPanel;
	}

	public void showSelectedFileXML() {
		extractPanel.refresh();
		JFileChooser fileChooserXML = importFilePanel.getFileChooserXML();
		if (fileChooserXML.showOpenDialog(importFilePanel) == JFileChooser.APPROVE_OPTION) {
			importFilePanel.showFileLabelXML();
			String fileName = fileChooserXML.getSelectedFile()
					.getAbsolutePath();
			importFilePanel.setXMLfile(fileName);
		}
	}

	// extract all facts for all leads
	private void extractAllFacts(String inputFile, String outputFile) {

		try {

			// initialize the stream reader
			InputStream inputStream = new FileInputStream(inputFile);
			XMLEventReader reader = XMLInputFactory.newInstance()
					.createXMLEventReader(inputStream, encoding);

			// initialize the stream writer
			OutputStream outputStream = new FileOutputStream(outputFile);
			XMLEventWriter writer = XMLOutputFactory.newInstance()
					.createXMLEventWriter(outputStream, encoding);

			// determine whether the XML contains facts at all
			boolean factsFound = false;
			while (reader.hasNext() && !factsFound) {
				XMLEvent event = (XMLEvent) reader.next();
				if (event.getEventType() == event.START_ELEMENT) {
					String name = event.asStartElement().getName()
							.getLocalPart();
					if (name.equals("Facts")) {
						factsFound = true;
					}
				}
			}

			// no facts found in the XML
			if (!factsFound) {
				writer.flush();
				writer.close();
				reader.close();
				return;
			}

			// facts are found in the XML

			// initialize event factory for creating instances of XML events
			XMLEventFactory eventFactory = XMLEventFactory.newInstance();

			// write XML header into the output document
			StartDocument startDocument = eventFactory.createStartDocument(
					encoding, "1.0");
			writer.add(startDocument);

			// 1. create starting tag <Facts>
			StartElement startFacts = eventFactory.createStartElement("", "",
					"Facts");
			writer.add(startFacts);

			// inspect each element starting with the one right after
			// <Facts> and ending with the last one before </Facts>

			while (reader.hasNext()) {
				XMLEvent event = (XMLEvent) reader.next();

				switch (event.getEventType()) {
				// starting tags - just write them to the output
				case (XMLStreamConstants.START_ELEMENT): {
					String name = event.asStartElement().getName()
							.getLocalPart().trim();
					StartElement startElement = eventFactory
							.createStartElement("", "", name);
					writer.add(startElement);

					break;
				}// content elements- just write them to the output
				case (XMLStreamConstants.CHARACTERS): {
					String content = event.asCharacters().getData();
					Characters contentElement = eventFactory
							.createCharacters(content);
					writer.add(contentElement);

					break;
				}// closing tags - just write them to the output
					// if </Facts> has been reached - finish writing
				case (XMLStreamConstants.END_ELEMENT): {
					String name = event.asEndElement().getName().getLocalPart()
							.trim();
					EndElement endElement = eventFactory.createEndElement("",
							"", name);
					writer.add(endElement);

					if (name.equals("Facts")) {
						extractionOK = true;
						EndDocument ed = eventFactory.createEndDocument();
						writer.add(ed);
						writer.flush();
						writer.close();
						reader.close();
						return;
					}

					break;

				}// end end tag
				}// end switch
			}

		} catch (XMLStreamException e) {
			extractionOK = false;
		} catch (java.util.NoSuchElementException e) {
			extractionOK = false;

		} catch (FileNotFoundException e) {
			extractionOK = false;
		}
	}

	public boolean extractData() {

		// retrieve import XML file name
		String importXMLfile = importFilePanel.getXMLfile();

		// no XML file selected
		if (importXMLfile == null) {
			JOptionPane.showMessageDialog(mainFrame, NO_FILE_SELECTED);
			return false;

		}

		// if not facts selected
		if (selectionPanel.getSelectedFacts().size() == 0) {
			JOptionPane.showMessageDialog(mainFrame, NO_FACTS_SELECTED);
			return false;

		}

		// empty the result panel
		extractPanel.refresh();

		// show file chooser dialogue to select output file
		JFileChooser fileChooserXML = extractPanel.getFileChooserXML();
		if (fileChooserXML.showSaveDialog(extractPanel) == JFileChooser.APPROVE_OPTION) {

			// show that extraction is in progress
			extractPanel.showExtractionInProgress();

			// disable all buttons on the dialogue
			ArrayList<Component> components = new ArrayList<Component>();
			components.add(importFilePanel.getBrowseButton());
			components.add(extractPanel.getExtractButton());
			components.addAll(selectionPanel.getComponentsToDisable());

			Runnable disableWindow = new DisableWindowTask(tabPane, false,
					components);
			SwingUtilities.invokeLater(disableWindow);

			// determine the output file
			File outputFile = fileChooserXML.getSelectedFile();

			// determine whether all facts have to be extracted or only selected
			// and
			// whether for all leads or for the specified only
			ArrayList<String> selectedFacts = selectionPanel.getSelectedFacts();
			String leads = selectionPanel.getSelectedLeadIDs();

			// check whether the leads entered in the input field are valid
			boolean validInput = validateInputLeads(leads.trim());
			if (!validInput) {
				JOptionPane.showMessageDialog(mainFrame, INVALID_LEADS_INPUT);
				return false;
			}

			// convert leads String into an array of lead IDs
			ArrayList<String> leadsToFind = new ArrayList<String>();
			if (leads.length() > 0) {
				leadsToFind = leadsToFind(leads);
			}

			// check whether all facts have been selected
			boolean selectAllFacts = selectionPanel.getFacts().length == selectedFacts
					.size();

			// check whether any leads have been specified
			boolean leadsSpecified = leadsToFind.size() > 0;

			// if all facts selected
			if (selectAllFacts) {

				// extract all facts but only for the specified leads
				if (leadsSpecified) {
					extractAllFactsForSpecifiedLeads(leadsToFind,
							importXMLfile, outputFile.getAbsolutePath()
									+ ".xml");
				}
				// extract all facts from the XML
				else {

					extractAllFacts(importXMLfile, outputFile.getAbsolutePath()
							+ ".xml");
				}

			} else {
				// extract selected facts for specified leads
				if (leadsSpecified) {
					extractSelectedFactsWithSpecifiedLeads(selectedFacts,
							leadsToFind, importXMLfile,
							outputFile.getAbsolutePath() + ".xml");
				}
				// extract selected facts without lead restriction
				else {
					extractSelectedFacts(selectedFacts, importXMLfile,
							outputFile.getAbsolutePath() + ".xml");
				}
			}

			if (extractionOK) {
				extractPanel.setResult(EXTRACTION_SUCCESSFUL);
				extractPanel.showExportFile(outputFile.getAbsolutePath()
						+ ".xml");
			} else {
				extractPanel.setResult(EXTRACTION_FAILED);
			}

			Runnable enableWindow = new DisableWindowTask(tabPane, true,
					components);
			SwingUtilities.invokeLater(enableWindow);
		}
		return true;

	}

	// extract only selected facts with only specified lead IDs
	private void extractSelectedFactsWithSpecifiedLeads(
			ArrayList<String> facts, ArrayList<String> leads,
			String importFile, String outputFile) {

		try {

			// initialize the stream reader
			InputStream inputStream = new FileInputStream(importFile);
			XMLEventReader reader = XMLInputFactory.newInstance()
					.createXMLEventReader(inputStream, encoding);

			// initialize the stream writer
			OutputStream outputStream = new FileOutputStream(outputFile);
			XMLEventWriter writer = XMLOutputFactory.newInstance()
					.createXMLEventWriter(outputStream, encoding);

			// determine whether the XML contains facts at all
			boolean factsFound = false;
			while (reader.hasNext() && !factsFound) {
				XMLEvent event = (XMLEvent) reader.next();
				if (event.getEventType() == event.START_ELEMENT) {
					String name = event.asStartElement().getName()
							.getLocalPart();
					if (name.equals("Facts")) {
						factsFound = true;
					}
				}
			}

			// no facts found in the XML
			if (!factsFound) {
				writer.flush();
				writer.close();
				reader.close();
				return;
			}

			// facts are found in the XML

			// initialize event factory for creating instances of XML events
			XMLEventFactory eventFactory = XMLEventFactory.newInstance();

			// write XML header into the output document
			StartDocument startDocument = eventFactory.createStartDocument(
					encoding, "1.0");
			writer.add(startDocument);

			// 1. create starting tag <Facts>
			StartElement startFacts = eventFactory.createStartElement("", "",
					"Facts");
			writer.add(startFacts);

			// into this array we will write each sub-element of each fact
			// if the searched lead id is encountered - this whole fact will
			// then be written into the output file.
			// When starting with the new fact - the array is cleared
			ArrayList<XMLEvent> elements = new ArrayList<XMLEvent>();

			// start element of each fact, for ex.: <F_Offer_Created>
			StartElement factStartTag = null;
			String factStartTagName = "";

			// flag to determine whether the fact contains one of the searched
			// lead ids
			boolean leadMatchFound = false;

			// flag to determine whether the fact is one of the searched facts
			boolean factMatch = false;

			while (reader.hasNext()) {
				// inspect each element starting with the one right after
				// <Facts> and ending with the last one before </Facts>
				XMLEvent event = (XMLEvent) reader.next();

				switch (event.getEventType()) {
				case (XMLStreamConstants.START_ELEMENT): {

					String startTagName = event.asStartElement().getName()
							.getLocalPart().trim();
					StartElement startTag = eventFactory.createStartElement("",
							"", startTagName);

					// this condition helps decide whether this element is
					// 1. the starting element of the fact, like
					// <F_Offer_Created> or
					// 2. the starting element of one of the sub-elements of
					// the fact, like <FOC_Date>
					//
					// The starting element of the fact (option 1), like
					// <F_Offer_Created>, is either followed by another
					// starting element or a comment element or white
					// space symbols

					// option 1 - f.ex., <F_Offer_Created>
					if ((reader.peek().isStartElement())
							|| (reader.peek().getEventType() == XMLStreamConstants.COMMENT)
							|| (reader.peek().isCharacters() && reader.peek()
									.asCharacters().getData().trim().equals(""))) {

						for (String fact : facts) {
							if (fact.equals(startTagName)) {
								factMatch = true;
								factStartTag = startTag;
								factStartTagName = startTagName;
							}
						}
					}
					// option 2 - f.ex., <FOC_Date>
					else {
						if (factMatch) {
							elements.add(startTag);
						}
					}
					break;
				}

				case (XMLStreamConstants.END_ELEMENT): {
					String endTagName = event.asEndElement().getName()
							.getLocalPart().trim();
					EndElement endTag = eventFactory.createEndElement("", "",
							endTagName);
					// here we decide if we reached:
					// 1. the end element of the fact, like </F_Offer_Created>
					// OR
					// 2. we've reached the end of facts </Facts>
					// OR
					// 3. it is an end element of one of the sub-elements of the
					// fact, like </FOC_Date>.
					// In the first case, the end element's name is the same
					// as the name of the fact's starting element, which was
					// previously stored in factStartTagName

					// option 2 - we've reached end of facts: so we write
					// </Facts> and close the writer
					if (endTagName.equals("Facts")) {
						EndElement endFacts = eventFactory.createEndElement("",
								"", "Facts");
						writer.add(endFacts);

						extractionOK = true;

						writer.flush();
						writer.close();
						reader.close();
						return;
					}

					// consider only the end tags of the searched facts
					if (factMatch) {

						// option 1 - for ex., </F_Offer_Created>
						if (endTagName.equals(factStartTagName)) {
							// if we've reached the end of the fact we need to
							// decide whether we write this fact to the output
							// file or not. This depends on the flag
							// leadMatchFound:
							if (leadMatchFound) {
								writer.add(factStartTag);
								for (XMLEvent element : elements) {
									writer.add(element);
								}

								writer.add(endTag);
							}

							// remove all elements of the fact to start with the
							// next one
							elements.clear();
							leadMatchFound = false;
							factMatch = false;
						}

						// option 3: if this is an end element of a fact's
						// sub-element, like </FOC_Date>, - simply add it to the
						// arrayList of other sub-elements
						else {
							elements.add(endTag);
						}
					}
					break;
				}
				// Content elements are simply added to the arrayList of the
				// other sub-elements. If one of the searched lead IDs is
				// encountered - the flag leadMatchFound is set to true.
				case (XMLStreamConstants.CHARACTERS): {
					if (factMatch) {
						String content = event.asCharacters().getData().trim();
						Characters contentElement = eventFactory
								.createCharacters(content);

						elements.add(contentElement);
						for (String lead : leads) {
							if (lead.equals(content)) {
								leadMatchFound = true;
							}
						}
					}

					break;
				}
				}
			}

		} catch (XMLStreamException e) {
			// System.out.println("1");
			extractionOK = false;

		} catch (java.util.NoSuchElementException e) {
			// System.out.println("2");
			extractionOK = false;

		} catch (FileNotFoundException e) {
			// System.out.println("3");
			extractionOK = false;
		}
	}

	// extract all facts (all facts selected) for the leads entered in the input
	// field
	private void extractAllFactsForSpecifiedLeads(ArrayList<String> leads,
			String importFile, String outputFile) {
		try {

			// initialize the stream reader
			InputStream inputStream = new FileInputStream(importFile);
			XMLEventReader reader = XMLInputFactory.newInstance()
					.createXMLEventReader(inputStream, encoding);

			// initialize the stream writer
			OutputStream outputStream = new FileOutputStream(outputFile);
			XMLEventWriter writer = XMLOutputFactory.newInstance()
					.createXMLEventWriter(outputStream, encoding);

			// determine whether the XML contains facts at all
			boolean factsFound = false;
			while (reader.hasNext() && !factsFound) {
				XMLEvent event = (XMLEvent) reader.next();
				if (event.getEventType() == event.START_ELEMENT) {
					String name = event.asStartElement().getName()
							.getLocalPart();
					if (name.equals("Facts")) {
						factsFound = true;
					}
				}
			}

			// no facts found in the XML
			if (!factsFound) {
				writer.flush();
				writer.close();
				reader.close();
				return;
			}

			// facts are found in the XML

			// initialize event factory for creating instances of XML events
			XMLEventFactory eventFactory = XMLEventFactory.newInstance();

			// write XML header into the output document
			StartDocument startDocument = eventFactory.createStartDocument(
					encoding, "1.0");
			writer.add(startDocument);

			// 1. create starting tag <Facts>
			StartElement startFacts = eventFactory.createStartElement("", "",
					"Facts");
			writer.add(startFacts);

			// into this array we will write each sub-element of each fact
			// if the searched lead id is encountered - this whole fact will
			// then be written into the output file.
			// When starting with the new fact - the array is cleared
			ArrayList<XMLEvent> elements = new ArrayList<XMLEvent>();

			// start element of each fact, for ex.: <F_Offer_Created>
			StartElement factStartTag = null;
			String factStartTagName = "";

			// flag to determine whether the fact has to be written into the
			// output file because it contains one of the searched lead ids
			boolean leadMatchFound = false;

			while (reader.hasNext()) {
				// inspect each element starting with the one right after
				// <Facts> and ending with the last one before </Facts>
				XMLEvent event = (XMLEvent) reader.next();

				switch (event.getEventType()) {
				case (XMLStreamConstants.START_ELEMENT): {

					String startTagName = event.asStartElement().getName()
							.getLocalPart().trim();
					StartElement startTag = eventFactory.createStartElement("",
							"", startTagName);

					// this condition helps decide whether this element is
					// 1. the starting element of the fact, like
					// <F_Offer_Created> or
					// 2. the starting element of one of the sub-elements of
					// the fact, like <FOC_Date>
					//
					// The starting element of the fact (option 1), like
					// <F_Offer_Created>, is either followed by another
					// starting element or a comment element or white
					// space symbols

					// option 1 - f.ex., <F_Offer_Created>
					if ((reader.peek().isStartElement())
							|| (reader.peek().getEventType() == XMLStreamConstants.COMMENT)
							|| (reader.peek().isCharacters() && reader.peek()
									.asCharacters().getData().trim().equals(""))) {
						factStartTag = startTag;
						factStartTagName = startTagName;
					}
					// option 2 - f.ex., <FOC_Date>
					else {
						elements.add(startTag);
					}
					break;
				}

				case (XMLStreamConstants.END_ELEMENT): {
					String endTagName = event.asEndElement().getName()
							.getLocalPart().trim();
					EndElement endTag = eventFactory.createEndElement("", "",
							endTagName);
					// here we decide if we reached:
					// 1. the end element of the fact, like </F_Offer_Created>
					// OR
					// 2. we've reached the end of facts </Facts>
					// OR
					// 3. it is an end element of one of the sub-elements of the
					// fact, like </FOC_Date>.
					// In the first case, the end element's name is the same
					// as the name of the fact's starting element, which was
					// previously stored in factStartTagName

					// option 1 - for ex., </F_Offer_Created>
					if (endTagName.equals(factStartTagName)) {
						// if we've reached the end of the fact we need to
						// decide whether we write this fact to the output
						// file or not. This depends on the flag leadMatchFound:
						if (leadMatchFound) {
							writer.add(factStartTag);
							for (XMLEvent element : elements) {
								writer.add(element);
							}

							writer.add(endTag);
						}

						// remove all elements of the fact to start with the
						// next one
						elements.clear();
						leadMatchFound = false;
					}
					// option 2 - we've reached end of facts: so we write
					// </Facts> and
					// close the writer
					else if (endTagName.equals("Facts")) {
						EndElement endFacts = eventFactory.createEndElement("",
								"", "Facts");
						writer.add(endFacts);

						extractionOK = true;

						writer.flush();
						writer.close();
						reader.close();
						return;
					}
					// if this is an end element of a fact's subelement,
					// like </FOC_Date>, - simply add it to the arrayList of
					// other sub-elements
					else {
						elements.add(endTag);
					}
					break;
				}
				// Content elements are simply added to the arrayList of the
				// other sub-elements. If one of the searched lead IDs is
				// encountered - the flag leadMatchFound is set to true.
				case (XMLStreamConstants.CHARACTERS): {
					String content = event.asCharacters().getData().trim();
					Characters contentElement = eventFactory
							.createCharacters(content);
					elements.add(contentElement);

					for (String lead : leads) {
						if (lead.equals(content)) {
							leadMatchFound = true;
						}
					}
					break;
				}
				}
			}

		} catch (XMLStreamException e) {
			// System.out.println("1");
			extractionOK = false;

		} catch (java.util.NoSuchElementException e) {
			// System.out.println("2");
			extractionOK = false;

		} catch (FileNotFoundException e) {
			// System.out.println("3");
			extractionOK = false;
		}

	}

	// extract selected facts with no lead restriction (when no leads are
	// specified)
	private void extractSelectedFacts(ArrayList<String> facts,
			String importFile, String outputFile) {
		try {

			// initialize the stream reader
			InputStream inputStream = new FileInputStream(importFile);
			XMLEventReader reader = XMLInputFactory.newInstance()
					.createXMLEventReader(inputStream, encoding);

			// initialize the stream writer
			OutputStream outputStream = new FileOutputStream(outputFile);
			XMLEventWriter writer = XMLOutputFactory.newInstance()
					.createXMLEventWriter(outputStream, encoding);

			// determine whether the XML contains facts at all
			boolean factsFound = false;
			while (reader.hasNext() && !factsFound) {
				XMLEvent event = (XMLEvent) reader.next();
				if (event.getEventType() == event.START_ELEMENT) {
					String name = event.asStartElement().getName()
							.getLocalPart();
					if (name.equals("Facts")) {
						factsFound = true;
					}
				}
			}

			// no facts found in the XML
			if (!factsFound) {
				writer.flush();
				writer.close();
				reader.close();
				return;
			}

			// facts are found in the XML
			
			// initialize event factory for creating instances of XML events
			XMLEventFactory eventFactory = XMLEventFactory.newInstance();
			
			// write XML header into the output document
			StartDocument startDocument = eventFactory.createStartDocument(
					encoding, "1.0");
			writer.add(startDocument);

			// 1. create starting tag <Facts>
			StartElement startFacts = eventFactory.createStartElement("", "",
					"Facts");
			writer.add(startFacts);

			// flag that decides whether to write the fact into the output file
			// or not
			boolean factMatch = false;

			// store the start tag name of each fact, for ex.,
			// <F_Lead_Created_Manually> to compare later with the end
			// tag and stop when the fact end reached
			String startFactName = null;

			// inspect each element starting with the one right after
			// <Facts> and ending with the last one before </Facts>
			while (reader.hasNext()) {

				XMLEvent event = (XMLEvent) reader.next();

				switch (event.getEventType()) {
				case (XMLStreamConstants.START_ELEMENT): {

					String startTagName = event.asStartElement().getName()
							.getLocalPart().trim();

					// this condition helps decide whether this element is
					// 1. the starting element of the fact, like
					// <F_Offer_Created> or
					// 2. the starting element of one of the sub-elements of
					// the fact, like <FOC_Date>
					//
					// The starting element of the fact (option 1), like
					// <F_Offer_Created>, is either followed by another
					// starting element or a comment element or white
					// space symbols

					// option 1 - f.ex., <F_Offer_Created>
					if ((reader.peek().isStartElement())
							|| (reader.peek().getEventType() == XMLStreamConstants.COMMENT)
							|| (reader.peek().isCharacters() && reader.peek()
									.asCharacters().getData().trim().equals(""))) {

						for (String fact : facts) {
							if (fact.equals(startTagName)) {
								startFactName = startTagName;
								factMatch = true;
								StartElement startTag = eventFactory
										.createStartElement("", "",
												startTagName);
								writer.add(startTag);
							}
						}

					}
					// option 2 - for ex., <FOC_Date>
					else {
						if (factMatch) {
							StartElement startTag = eventFactory
									.createStartElement("", "", startTagName);
							writer.add(startTag);
						}
					}

					break;
				}

				case (XMLStreamConstants.END_ELEMENT): {
					String endTagName = event.asEndElement().getName()
							.getLocalPart().trim();

					// here we decide if we reached:
					// 1. the end element of the fact, like </F_Offer_Created>
					// OR
					// 2. we've reached the end of facts </Facts>
					// OR
					// 3. it is an end element of one of the sub-elements of the
					// fact, like </FOC_Date>.
					// In the first case, the end element's name is the same
					// as the name of the fact's starting element, which was
					// previously stored in factStartTagName

					// option 1 - for ex., </F_Offer_Created>: write to the
					// output if it belongs to one of the searched facts
					if (endTagName.equals(startFactName)) {

						if (factMatch) {
							EndElement endTag = eventFactory.createEndElement(
									"", "", endTagName);
							writer.add(endTag);
							factMatch = false;
						}

					}
					// option 2 - we've reached end of facts: so we write
					// </Facts> and close the writer and reader
					else if (endTagName.equals("Facts")) {
						EndElement endFacts = eventFactory.createEndElement("",
								"", "Facts");
						writer.add(endFacts);

						extractionOK = true;

						writer.flush();
						writer.close();
						reader.close();
						return;
					}
					// option 3 - for ex., </FOC_Date>: write to the output if
					// it belongs to one of the searched facts
					else {
						if (factMatch) {
							EndElement endTag = eventFactory.createEndElement(
									"", "", endTagName);
							writer.add(endTag);
						}
					}

					break;
				}
				// Content elements are written if they belong to one of the
				// searched facts
				case (XMLStreamConstants.CHARACTERS): {
					if (factMatch) {
						String content = event.asCharacters().getData().trim();
						Characters contentElement = eventFactory
								.createCharacters(content);
						writer.add(contentElement);
					}
					break;
				}
				}
			}

		} catch (XMLStreamException e) {
			// System.out.println("1");
			extractionOK = false;

		} catch (java.util.NoSuchElementException e) {
			// System.out.println("2");
			extractionOK = false;

		} catch (FileNotFoundException e) {
			// System.out.println("3");
			extractionOK = false;
		}
	}

	private ArrayList<String> leadsToFind(String input) {
		ArrayList<String> leads = new ArrayList<String>();
		int length = input.length();
		int counter = 0;

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

	private boolean validateInputLeads(String input) {
		String pattern = "^(\\s*\\d\\s*)*$";
		Pattern r = Pattern.compile(pattern);
		Matcher m = r.matcher(input);
		if (!m.matches())
			return false;

		return true;
	}
}
