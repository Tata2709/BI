package BI.lead;

import java.awt.Component;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.SwingWorker;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartDocument;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

public class LeadExtractionTask extends SwingWorker<Integer, String> {
	private String outputFile;
	private String inputFile;
	private final int FAILURE = -1;
	private final int SUCCESS = 1;
	private final String encoding = "UTF-8";
	private JLabel extractionResultLabel;
	private int leadCounter;
	private JLabel exportFileLabel;
	private JButton browseXMLbutton;
	private final String EXTRACTION_FAILURE_MESSAGE = "Extraction not successful. \nCheck if the imported XML file is valid";
	private final String EXTRACTION_SUCCESSFULLY_COMPLETED_MESSAGE = " leads successfully extracted: ";
	private final String EXTRACTION_INTERRUPTED_MESSAGE = "Extraction interrupted. \nCheck if the imported XML file is valid";
	private final String NO_MATCHING_LEADS = "No matching leads were found in the input file";
	private final String NO_LEADS_FOUND = "No leads were found in the input file";
	private final String LEAD = "Lead";
	private ArrayList<Component> componentsToDisable;
	private LeadExtractionMode leadExtractionMode;
	private ArrayList<String> leadIDs;
	boolean leadsFound = false;
	private final String LEADID = "LEAD_Lead_ID";

	LeadExtractionTask(String input, String output, JLabel extractionResult,
			JLabel exportFile, JButton browseB,
			ArrayList<Component> components, ArrayList<String> leads,
			LeadExtractionMode exMode) {
		outputFile = output;
		inputFile = input;
		extractionResultLabel = extractionResult;
		exportFileLabel = exportFile;
		browseXMLbutton = browseB;
		componentsToDisable = components;
		leadExtractionMode = exMode;
		leadIDs = leads;
	}

	@Override
	protected Integer doInBackground() throws Exception {

		setProgress(0);

		try {
			// initialize the stream reader
			InputStream inputStream = new FileInputStream(inputFile);
			XMLEventReader reader = XMLInputFactory.newInstance()
					.createXMLEventReader(inputStream, encoding);

			// initialize the stream writer
			OutputStream outputStream = new FileOutputStream(outputFile);
			XMLEventWriter writer = XMLOutputFactory.newInstance()
					.createXMLEventWriter(outputStream, encoding);

			// initialize event factory for creating instances of XML events
			XMLEventFactory eventFactory = XMLEventFactory.newInstance();

			// write XML header into the output document
			StartDocument startDocument = eventFactory.createStartDocument(
					encoding, "1.0");
			writer.add(startDocument);

			// extraction result
			int result = FAILURE;
			switch (leadExtractionMode) {
			case EXTRACTALL: {
				result = extractAllLeads(reader, writer, eventFactory);
				break;
			}
			case EXTRACTBYIDS: {
				result = extractLeadsByIDs(reader, writer, eventFactory);
				break;
			}
			}

			return result;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return FAILURE;
		}

	}

	private int extractAllLeads(XMLEventReader reader, XMLEventWriter writer,
			XMLEventFactory eventFactory) {
		String previousEndTagName = "";
		leadCounter = 0;

		try {
			leadsFound = false;

			while (reader.hasNext()) {
				// inspect each <Lead> element
				XMLEvent event = (XMLEvent) reader.next();

				// opening tag: three options possible:
				// 1. this is the beginning of another Lead
				// 2. this is the beginning of a sub-element of lead
				// 3. this is the beginning of the first element after the last
				// lead closing tag
				switch (event.getEventType()) {
				case (XMLStreamConstants.START_ELEMENT): {

					String startTagName = event.asStartElement().getName()
							.getLocalPart().trim();

					// option 1 - increase the lead counter
					if (startTagName.equals(LEAD)) {
						leadsFound = true;
						previousEndTagName = "";
						leadCounter++;
						publish("Extracting lead number " + leadCounter);
					}

					// option 3 - finish export
					if (previousEndTagName.equals(LEAD)
							&& !startTagName.equals(LEAD)) {
						writer.flush();
						writer.close();
						reader.close();
						return SUCCESS;
					}
					// option 1 and 2 - write it to the output
					StartElement startTag = eventFactory.createStartElement("",
							"", startTagName);

					writer.add(startTag);
					break;
				}
				// closing tag: 1. write it to the output, then
				// 2. store it in previousEndTagName to recognize where leads
				// end
				case (XMLStreamConstants.END_ELEMENT): {
					String endTagName = event.asEndElement().getName()
							.getLocalPart().trim();
					EndElement endTag = eventFactory.createEndElement("", "",
							endTagName);

					writer.add(endTag);

					previousEndTagName = endTagName;

					break;
				}

				// Content elements: add to the output
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
			e.printStackTrace();
			return FAILURE;

		} catch (java.util.NoSuchElementException e) {
			e.printStackTrace();
			return FAILURE;
		}

		return SUCCESS;

	}

	private int extractLeadsByIDs(XMLEventReader reader, XMLEventWriter writer,
			XMLEventFactory eventFactory) {
		String previousEndTagName = "";
		boolean leadIDElement = false;
		leadCounter = 0;
		leadsFound = false;
		boolean insideLead = false;
		ArrayList<XMLEvent> leadElements = new ArrayList<XMLEvent>();
		boolean leadMatches = false;
		String matchedLead = null;
		try {
			publish("Searching...");
			while (reader.hasNext()) {
				// inspect each <Lead> element
				XMLEvent event = (XMLEvent) reader.next();

				// opening tag: three options possible:
				// 1. this is the beginning of another Lead
				// 2. this is the beginning of a sub-element of lead
				// 3. this is the beginning of the first element after the last
				// lead closing tag
				switch (event.getEventType()) {
				case (XMLStreamConstants.START_ELEMENT): {

					String startTagName = event.asStartElement().getName()
							.getLocalPart().trim();

					// option 3 - finish export
					if (previousEndTagName.equals(LEAD)
							&& !startTagName.equals(LEAD)) {
						writer.flush();
						writer.close();
						reader.close();
						return SUCCESS;
					}

					// option 1 - reset previousEndTag to find the end of this
					// lead
					if (startTagName.equals(LEAD)) {
						leadsFound = true;
						previousEndTagName = "";
						insideLead = true;
					} else if (startTagName.equals(LEADID)) {
						leadIDElement = true;
					}

					// option 1 and 2 - write it to the output
					StartElement startTag = eventFactory.createStartElement("",
							"", startTagName);

					if (leadMatches) {
						writer.add(startTag);
					} else {
						if (insideLead) {
							leadElements.add(startTag);
						}
					}
					break;
				}
				// closing tag: 1. write it to the output, then
				// 2. store it in previousEndTagName to recognize where leads
				// end
				case (XMLStreamConstants.END_ELEMENT): {
					String endTagName = event.asEndElement().getName()
							.getLocalPart().trim();
					EndElement endTag = eventFactory.createEndElement("", "",
							endTagName);

					if (leadMatches) {
						writer.add(endTag);
					} else {
						if (insideLead) {
							leadElements.add(endTag);
						}
					}

					previousEndTagName = endTagName;

					if (endTagName.equals(LEAD)) {
						if (leadMatches)
							leadIDs.remove(matchedLead);
						if (leadIDs.size() == 0) {
							writer.flush();
							writer.close();
							reader.close();
							return SUCCESS;
						}
						leadElements.clear();
						leadMatches = false;
						matchedLead = null;
					}

					break;
				}

				// Content elements: add to the output
				case (XMLStreamConstants.CHARACTERS): {

					String content = event.asCharacters().getData().trim();
					if (leadIDElement) {
						for (String leadID : leadIDs) {
							if (leadID.equals(content)) {
								leadMatches = true;
								matchedLead = leadID;
								for (XMLEvent element : leadElements) {
									writer.add(element);
								}
								leadCounter++;
								publish("Extracted " + leadCounter
										+ " leads. Searching...");
							}
						}
						leadIDElement = false;
					}
					Characters contentElement = eventFactory
							.createCharacters(content);

					if (leadMatches) {

						writer.add(contentElement);
					} else {
						if (insideLead) {
							leadElements.add(contentElement);
						}
					}

					break;
				}
				}

			}
		} catch (XMLStreamException e) {
			return FAILURE;

		} catch (java.util.NoSuchElementException e) {
			System.out.println("failure");
			return FAILURE;

		}
		return SUCCESS;

	}

	@Override
	protected void process(List<String> chunks) {
		for (Component component : componentsToDisable) {
			component.setEnabled(false);
		}

		for (String message : chunks) {
			extractionResultLabel.setText(message);
			// extractionResultLabel.setText("Extracting lead number: " +
			// number);
			extractionResultLabel.setVisible(true);
		}
	}

	@Override
	protected void done() {
		for (Component component : componentsToDisable) {
			component.setEnabled(true);
		}
		int result;
		try {
			result = get();
			if (result == FAILURE) {
				extractionResultLabel.setText(EXTRACTION_FAILURE_MESSAGE);
				extractionResultLabel.setVisible(true);
				exportFileLabel.setVisible(false);
			} else if (result == SUCCESS) {
				if (!leadsFound) {
					extractionResultLabel.setText(NO_LEADS_FOUND);
				} else {
					if (leadCounter == 0) {
						extractionResultLabel.setText(NO_MATCHING_LEADS);
					} else {
						extractionResultLabel.setText(leadCounter
								+ EXTRACTION_SUCCESSFULLY_COMPLETED_MESSAGE);
						exportFileLabel.setText(outputFile);
						exportFileLabel.setVisible(true);
					}
				}
				extractionResultLabel.setVisible(true);

			}
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			extractionResultLabel.setText(EXTRACTION_INTERRUPTED_MESSAGE);
			extractionResultLabel.setVisible(true);
			exportFileLabel.setVisible(false);
		}

	}

}
