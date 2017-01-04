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
	private ArrayList <Component> componentsToDisable;

	LeadExtractionTask(String input, String output, JLabel extractionResult,
			JLabel exportFile, JButton browseB, ArrayList <Component> components) {
		outputFile = output;
		inputFile = input;
		extractionResultLabel = extractionResult;
		exportFileLabel = exportFile;
		browseXMLbutton = browseB;
		componentsToDisable = components;
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

			String previousEndTagName = "";
			leadCounter = 0;
			while (reader.hasNext()) {
				// inspect each <Lead> element
				XMLEvent event = (XMLEvent) reader.next();

				switch (event.getEventType()) {
				case (XMLStreamConstants.START_ELEMENT): {

					String startTagName = event.asStartElement().getName()
							.getLocalPart().trim();

					if (startTagName.equals("Lead")) {
						previousEndTagName = "";
						leadCounter++;
						// System.out.println("Extracting lead number " +
						// leadCounter);
						publish(Integer.toString(leadCounter));
					}

					if (previousEndTagName.equals("Lead")
							&& !startTagName.equals("Lead")) {
						writer.flush();
						writer.close();
						reader.close();
						return SUCCESS;
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
			return FAILURE;

		} catch (java.util.NoSuchElementException e) {
			return FAILURE;

		} catch (FileNotFoundException e) {
			return FAILURE;
		}

		return null;
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
