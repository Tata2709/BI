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
	private final String LEAD = "Lead";
	private ArrayList<Component> componentsToDisable;
	private LeadExtractionMode leadExtractionMode;
	private ArrayList <String> leadIDs;

	LeadExtractionTask(String input, String output, JLabel extractionResult,
			JLabel exportFile, JButton browseB,
			ArrayList<Component> components, ArrayList <String> leads,
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
			return FAILURE;
		}

	}

	private int extractAllLeads(XMLEventReader reader, XMLEventWriter writer,
			XMLEventFactory eventFactory) {
		String previousEndTagName = "";
		leadCounter = 0;
		try {
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
						previousEndTagName = "";
						leadCounter++;
						publish(Integer.toString(leadCounter));
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
			return FAILURE;

		} catch (java.util.NoSuchElementException e) {
			return FAILURE;

		}

		return SUCCESS;

	}

	private int extractLeadsByIDs(XMLEventReader reader, XMLEventWriter writer,
			XMLEventFactory eventFactory) {
		String previousEndTagName = "";
		leadCounter = 0;
		ArrayList<XMLEvent> leadElements = new ArrayList<XMLEvent>();
		boolean leadMatches = false;

		try {
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
						previousEndTagName = "";
						// leadCounter++;
						// publish(Integer.toString(leadCounter));
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

					if (leadMatches) {
						writer.add(startTag);
					}else{
						leadElements.add(startTag);
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
					}else{
						leadElements.add(endTag);
					}

					previousEndTagName = endTagName;
					
					if (endTagName.equals(LEAD)) {
						leadMatches = false;
					}

					break;
				}

				// Content elements: add to the output
				case (XMLStreamConstants.CHARACTERS): {

					String content = event.asCharacters().getData().trim();
					
					for(String leadID : leadIDs){
						if(leadID.equals(content))
							leadMatches = true;
					}
					
					Characters contentElement = eventFactory
							.createCharacters(content);

					
					if (leadMatches) {
						for(XMLEvent element : leadElements)
							writer.add(element);
						writer.add(contentElement);
					}else{
						leadElements.add(contentElement);
					}

					break;
				}
				}

			}
		} catch (XMLStreamException e) {
			return FAILURE;

		} catch (java.util.NoSuchElementException e) {
			return FAILURE;

		}

		return SUCCESS;

	}

	@Override
	protected void process(List<String> chunks) {
		for (Component component : componentsToDisable) {
			component.setEnabled(false);
		}
		extractionResultLabel.setVisible(true);
		for (String number : chunks) {
			extractionResultLabel.setText("Extracting lead number: " + number);
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
			}
			if (result == SUCCESS) {

				extractionResultLabel.setText(leadCounter
						+ EXTRACTION_SUCCESSFULLY_COMPLETED_MESSAGE);
				extractionResultLabel.setVisible(true);
				exportFileLabel.setText(outputFile);
				exportFileLabel.setVisible(true);

			}
		} catch (InterruptedException | ExecutionException e) {
			extractionResultLabel.setText(EXTRACTION_INTERRUPTED_MESSAGE);
			extractionResultLabel.setVisible(true);
			exportFileLabel.setVisible(false);
		}

	}

}
