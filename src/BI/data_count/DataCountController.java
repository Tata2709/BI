package BI.data_count;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.NoSuchElementException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

public class DataCountController {
	private JFrame mainFrame;
	private JTabbedPane tabPane;
	private DataCountPanel panel;
	private String inputFile;

	// GUI elements
	private JButton browseButton;
	private JLabel pathLabel;
	private JLabel fileLabel;
	private JLabel countingResultLabel;
	private JButton countButton;

	// constants
	private final String FACTS = "Facts";
	private final String LEAD = "Lead";
	private final String PERS = "Pers";
	private final String SAUN = "Saun";
	private final String PSCO = "Psco";
	private final String INVALID_FILE = "Input file is not a well-formed XML";
	private final String NO_XML_FILE_SELECTED = "No XML file selected";

	public DataCountController(JFrame mainFrame, JTabbedPane pane) {
		this.mainFrame = mainFrame;
		tabPane = pane;
	}

	public void setDataCountPanel(DataCountPanel dcPanel) {
		panel = dcPanel;

		// retrieve references to GUI elements from the panel
		browseButton = panel.getBrowseButton();
		pathLabel = panel.getPathLabel();
		fileLabel = panel.getFileLabel();
		countingResultLabel = panel.getCountingResultLabel();
		countButton = panel.getCountButton();

		// add logic to GUI elements
		addLogicToBrowseButton();
		addLogicToCountButton();

	}

	// logic for Count Button
	private void addLogicToCountButton() {
		countButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent event) {
				if (inputFile == null) {
					JOptionPane.showMessageDialog(mainFrame,
							NO_XML_FILE_SELECTED);
				} else {
					String result = countData(inputFile);
					countingResultLabel.setText(result);
				}
			}
		});
	}

	// logic for Browse button
	private void addLogicToBrowseButton() {
		browseButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent event) {

				showSelectedFileXML();
			}
		});

	}

	// concatenate result string from separate functions calculating the number
	// of each element in the XML.
	// If XML is not valid - return error string
	private String countData(String inputFile) {
		int counter = 0;
		String result = "";

		int leadsCounter = countLeads(inputFile);
		if (leadsCounter < 0)
			return INVALID_FILE;
		result += "<html>Leads: &emsp;&emsp;&emsp;&emsp;" + leadsCounter
				+ "<br><br>";
		counter += leadsCounter;

		int factsCounter = countFacts(inputFile);
		if (factsCounter < 0)
			return INVALID_FILE;
		result += "        Facts: &emsp;&emsp;&emsp;&emsp;" + factsCounter
				+ "<br><br>";
		counter += factsCounter;

		int persCounter = countPersons(inputFile);
		if (persCounter < 0)
			return INVALID_FILE;
		result += "Persons: &emsp;&emsp;&emsp;" + persCounter + "<br><br>";
		counter += persCounter;

		int orgCounter = countSauns(inputFile);
		if (orgCounter < 0)
			return INVALID_FILE;
		result += "Organisations: &emsp;" + orgCounter + "<br><br>";
		counter += orgCounter;

		int companyCounter = countCompanies(inputFile);
		if (companyCounter < 0)
			return INVALID_FILE;
		result += "Companies: &emsp;&emsp;&emsp;" + companyCounter + "<br><br>";
		counter += companyCounter;

		result += "<font color=\"red\">" + "Total: &emsp;&emsp;&emsp;"
				+ counter + "</font></html>";

		return result;
	}

	public void showSelectedFileXML() {
		// make the result panel empty for the next counting
		countingResultLabel.setText("");

		// display input file name and path
		JFileChooser fileChooserXML = new JFileChooser();
		FileNameExtensionFilter filterXML = new FileNameExtensionFilter("XML",
				"xml");
		fileChooserXML.setFileFilter(filterXML);

		if (fileChooserXML.showOpenDialog(panel) == JFileChooser.APPROVE_OPTION) {
			Path completePath = Paths.get(fileChooserXML.getSelectedFile()
					.getAbsolutePath());

			String fileName = completePath.getFileName().toString();
			String path = completePath.getParent().toString();

			showPath(path, pathLabel);
			showFileName(fileName, fileLabel);

			// set input file name for future use
			inputFile = completePath.toString();
		}
	}

	private void showFileName(String fileName, JLabel label) {
		label.setText(fileName);

		int length = fileName.length();
		if (length <= 70) {
			label.setFont(new Font("Times New Roman", Font.BOLD, 25));
		} else {
			String fileNameCutStart = fileName.substring(0, 58);
			String fileNameCut = fileNameCutStart + " ... .xml";
			label.setText(fileNameCut);
			label.setFont(new Font("Times New Roman", Font.BOLD, 20));
		}

		label.setForeground(new Color(93, 93, 95));
		label.setVisible(true);
	}

	private void showPath(String path, JLabel label) {
		label.setText(path);

		int length = path.length();
		if (length > 100) {
			String pathSubstring = path.substring(0, 80);
			String pathCut = pathSubstring + "...";
			label.setText(pathCut);
		}

		label.setFont(new Font("Times New Roman", Font.PLAIN, 12));
		label.setVisible(true);
	}

	private int countCompanies(String inputFile) {
		int counter = 0;
		// initialize the stream reader
		InputStream inputStream;
		try {
			inputStream = new FileInputStream(inputFile);
			XMLEventReader reader = XMLInputFactory.newInstance()
					.createXMLEventReader(inputStream);

			while (reader.hasNext()) {
				XMLEvent event = (XMLEvent) reader.next();
				if (event.getEventType() == event.START_ELEMENT) {
					String name = event.asStartElement().getName()
							.getLocalPart();
					if (name.equals(PSCO)) {
						counter++;
					}
				}
			}
		} catch (NoSuchElementException e) {
			e.printStackTrace();
			return -1;

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return -1;
		} catch (XMLStreamException e) {
			e.printStackTrace();
			return -1;
		} catch (FactoryConfigurationError e) {
			e.printStackTrace();
			return -1;
		}catch (Exception e) {
			e.printStackTrace();
			return -1;
		}

		return counter;
	}

	private int countLeads(String inputFile) {
		int counter = 0;
		// initialize the stream reader
		InputStream inputStream;
		try {
			inputStream = new FileInputStream(inputFile);
			XMLEventReader reader = XMLInputFactory.newInstance()
					.createXMLEventReader(inputStream);

			while (reader.hasNext()) {
				XMLEvent event = (XMLEvent) reader.next();
				if (event.getEventType() == event.START_ELEMENT) {
					String name = event.asStartElement().getName()
							.getLocalPart();
					if (name.equals(LEAD)) {
						counter++;
					}
				}
			}
		} catch (NoSuchElementException e) {
			e.printStackTrace();
			return -1;

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return -1;
		} catch (XMLStreamException e) {
			e.printStackTrace();
			return -1;
		} catch (FactoryConfigurationError e) {
			e.printStackTrace();
			return -1;
		}catch (Exception e) {
			e.printStackTrace();
			return -1;
		}

		return counter;
	}

	private int countPersons(String inputFile) {
		int counter = 0;
		// initialize the stream reader
		InputStream inputStream;
		try {
			inputStream = new FileInputStream(inputFile);
			XMLEventReader reader = XMLInputFactory.newInstance()
					.createXMLEventReader(inputStream);

			while (reader.hasNext()) {
				XMLEvent event = (XMLEvent) reader.next();
				if (event.getEventType() == event.START_ELEMENT) {
					String name = event.asStartElement().getName()
							.getLocalPart();
					if (name.equals(PERS)) {
						counter++;
					}
				}
			}
		} catch (NoSuchElementException e) {
			e.printStackTrace();
			return -1;

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return -1;
		} catch (XMLStreamException e) {
			e.printStackTrace();
			return -1;
		} catch (FactoryConfigurationError e) {
			e.printStackTrace();
			return -1;
		}catch (Exception e) {
			e.printStackTrace();
			return -1;
		}

		return counter;
	}

	private int countSauns(String inputFile) {
		int counter = 0;
		// initialize the stream reader
		InputStream inputStream;
		try {
			inputStream = new FileInputStream(inputFile);
			XMLEventReader reader = XMLInputFactory.newInstance()
					.createXMLEventReader(inputStream);

			while (reader.hasNext()) {
				XMLEvent event = (XMLEvent) reader.next();
				if (event.getEventType() == event.START_ELEMENT) {
					String name = event.asStartElement().getName()
							.getLocalPart();
					if (name.equals(SAUN)) {
						counter++;
					}
				}
			}
		} catch (NoSuchElementException e) {
			e.printStackTrace();
			return -1;

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return -1;
		} catch (XMLStreamException e) {
			e.printStackTrace();
			return -1;
		} catch (FactoryConfigurationError e) {
			e.printStackTrace();
			return -1;
		}catch (Exception e) {
			e.printStackTrace();
			return -1;
		}

		return counter;
	}

	private int countFacts(String inputFile) {
		int counter = 0;

		// initialize the stream reader
		InputStream inputStream;
		String factName = "";
		try {
			inputStream = new FileInputStream(inputFile);
			XMLEventReader reader = XMLInputFactory.newInstance()
					.createXMLEventReader(inputStream);

			// determine whether the XML contains facts at all
			boolean factsFound = false;
			while (reader.hasNext() && !factsFound) {
				XMLEvent event = (XMLEvent) reader.next();
				if (event.getEventType() == event.START_ELEMENT) {
					String name = event.asStartElement().getName()
							.getLocalPart();
					if (name.equals(FACTS)) {
						factsFound = true;
						boolean endOfFacts = false;
						boolean firstFact = false;
						while (!firstFact && !endOfFacts) {
							XMLEvent factEvent = (XMLEvent) reader.next();
							if (factEvent.isStartElement()) {
								factName = factEvent.asStartElement().getName()
										.getLocalPart().trim();
								counter++;
								firstFact = true;
							} else if (factEvent.isEndElement()) {
								String endTagName = factEvent.asEndElement()
										.getName().getLocalPart().trim();
								if (endTagName.equals(FACTS))
									endOfFacts = true;
							}

						}

					}
				}
			}

			if (!factsFound)
				return 0;

			boolean endOfFacts = false;
			boolean endOfFact = false;

			while (reader.hasNext() && !endOfFacts) {
				XMLEvent factEvent = (XMLEvent) reader.next();

				switch (factEvent.getEventType()) {

				// starting tags - check whether it is a new fact (the previous
				// one ended)
				case (XMLStreamConstants.START_ELEMENT): {

					if (endOfFact) {
						counter++;
						endOfFact = false;
						factName = factEvent.asStartElement().getName()
								.getLocalPart().trim();
					}

					break;
				} // check whether it is the end of the current fact or the end
					// of all facts
				case (XMLStreamConstants.END_ELEMENT): {
					String name = factEvent.asEndElement().getName()
							.getLocalPart().trim();
					if (name.equals(factName)) {
						endOfFact = true;
					} else if (name.equals(FACTS))
						endOfFacts = true;

					break;
				}
				}
			}

		} catch (NoSuchElementException e) {
			e.printStackTrace();
			return -1;

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return -1;
		} catch (XMLStreamException e) {
			e.printStackTrace();
			return -1;
		} catch (FactoryConfigurationError e) {
			e.printStackTrace();
			return -1;
		}catch (Exception e) {
			e.printStackTrace();
			return -1;
		}

		return counter;
	}

}
