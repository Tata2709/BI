package BI.validation;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.filechooser.FileNameExtensionFilter;

public class FilePanel extends JPanel {

	// GUI elements

	// buttons
	private JButton browseXMLbutton;
	private JButton browseXSDbutton;
	private JButton validateButton;

	// XML file name labels
	private JLabel xmlFileLabel;
	private JLabel xmlPathLabel;
	private JLabel xmlValidationLabel;

	// XSD file name labels
	private JLabel xsdFileLabel;
	private JLabel xsdPathLabel;
	private JLabel xsdValidationLabel;

	// XML and XSD file panels
	private JPanel xmlPanel;
	private JPanel xsdPanel;
	private JPanel validatePanel;

	// file choosers
	private JFileChooser fileChooserXML;
	private JFileChooser fileChooserXSD;

	// file names
	private String xsdFile;
	private String xmlFile;
	private String validationError;

	// flags to check whether valid XML and XSD have been selected
	private boolean xsdFileSelected;
	private boolean xmlFileSelected;

	// controller
	ValidationController controller;

	public FilePanel(ValidationController vc) {
		super();

		this.controller = vc;

		// initialize GUI elements
		initializeGUIelements();

		addListeners();
		xsdFileSelected = false;
		xmlFileSelected = false;

		// layout
		layoutComponents();
	}

	private void initializeGUIelements() {

		setBackground(new Color(245, 245, 245));

		// buttons
		browseXMLbutton = new JButton("Select XML file");
		browseXSDbutton = new JButton(" Select XSD file");
		validateButton = new JButton("Validate");
		browseXSDbutton.setForeground(new Color(93, 93, 95));
		browseXMLbutton.setForeground(new Color(93, 93, 95));
		validateButton.setForeground(new Color(93, 93, 95));

		browseXMLbutton.setFont(new Font("Times New Roman", Font.BOLD, 18));
		browseXSDbutton.setFont(new Font("Times New Roman", Font.BOLD, 18));
		validateButton.setFont(new Font("Times New Roman", Font.BOLD, 21));

		validateButton.setEnabled(true);

		// XML labels
		xmlFileLabel = new JLabel();
		xmlPathLabel = new JLabel();
		xmlValidationLabel = new JLabel();

		// XSD labels
		xsdFileLabel = new JLabel();
		xsdValidationLabel = new JLabel();
		xsdPathLabel = new JLabel();

		// XML and XSD file panels with buttons and labels inside
		xmlPanel = new JPanel();
		xsdPanel = new JPanel();
		addElementsToPanel(xmlPanel, xmlFileLabel, xmlPathLabel,
				xmlValidationLabel, browseXMLbutton);
		addElementsToPanel(xsdPanel, xsdFileLabel, xsdPathLabel,
				xsdValidationLabel, browseXSDbutton);

		// panel with Validate button
		validatePanel = new JPanel();
		validatePanel.setLayout(new GridBagLayout());
		validatePanel.setPreferredSize(new Dimension(550, 100));
		validatePanel.setVisible(true);
		validateButton.setPreferredSize(new Dimension(450, 80));
		validatePanel.add(validateButton);

		// fileChooser
		fileChooserXML = new JFileChooser();
		FileNameExtensionFilter filterXML = new FileNameExtensionFilter("XML",
				"xml");
		fileChooserXML.setFileFilter(filterXML);

		fileChooserXSD = new JFileChooser();
		FileNameExtensionFilter filterXSD = new FileNameExtensionFilter("XSD",
				"xsd");
		fileChooserXSD.setFileFilter(filterXSD);
	};

	private void layoutComponents() {
		Border innerBorder = BorderFactory.createTitledBorder("");
		Border outerBorder = BorderFactory.createEmptyBorder(1, 1, 1, 1);
		setBorder(BorderFactory.createCompoundBorder(outerBorder, innerBorder));
		setLayout(new GridLayout(3, 1, 10, 10));
		add(xmlPanel);
		add(xsdPanel);
		add(validatePanel);
	}

	private void addElementsToPanel(JPanel panel, JLabel fileLabel,
			JLabel pathLabel, JLabel validationLabel, JButton browseButton) {

		panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BorderLayout());
		browseButton.setPreferredSize(new Dimension(200, 80));
		buttonPanel.add(browseButton, BorderLayout.EAST);
		buttonPanel.add(pathLabel, BorderLayout.WEST);
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));

		JPanel fileNamePanel = new JPanel();
		fileNamePanel.setLayout(new BorderLayout());
		fileNamePanel.add(fileLabel, BorderLayout.WEST);
		fileNamePanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 15, 0));

		panel.setLayout(new BorderLayout());
		panel.add(buttonPanel, BorderLayout.NORTH);
		panel.add(fileNamePanel, BorderLayout.SOUTH);

		Border innerBorder = BorderFactory.createTitledBorder("");
		innerBorder.getBorderInsets(panel);

		Border outerBorder = BorderFactory.createEmptyBorder(5, 5, 5, 5);
		panel.setPreferredSize(new Dimension(550, 100));
		panel.setBorder(BorderFactory.createCompoundBorder(outerBorder,
				innerBorder));
		panel.setVisible(true);
	}

	public void addListeners() {
		browseXMLbutton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				controller.showSelectedFileXML();
			}
		});

		browseXSDbutton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent event) {

				controller.showSelectedFileXSD();
			}
		});

		validateButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent ev) {
				Thread queryThread = new Thread() {
					public void run() {
						controller.validateXMLagainstXSD(xmlFile, xsdFile);
					}
				};
				queryThread.start();

			}
		});
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

	public void showFileLabelXML() {
		Path completePath = Paths.get(fileChooserXML.getSelectedFile()
				.getAbsolutePath());

		String fileName = completePath.getFileName().toString();
		String path = completePath.getParent().toString();

		showPath(path, xmlPathLabel);
		showFileName(fileName, xmlFileLabel);

	}

	public void showFileLabelXSD() {
		Path completePath = Paths.get(fileChooserXSD.getSelectedFile()
				.getAbsolutePath());

		String fileName = completePath.getFileName().toString();
		String path = completePath.getParent().toString();

		showPath(path, xsdPathLabel);
		showFileName(fileName, xsdFileLabel);
	}

	public String getXSDfile() {
		return xsdFile;
	}

	public void setXSDfile(String xsdFile) {
		this.xsdFile = xsdFile;
	}

	public String getXMLfile() {
		return xmlFile;
	}

	public void setXMLfile(String xmlFile) {
		this.xmlFile = xmlFile;
	}

	public boolean isXSDfileSelected() {
		return xsdFileSelected;
	}

	public void setXSDfileSelected(boolean xsdFileSelected) {
		this.xsdFileSelected = xsdFileSelected;
	}

	public boolean isXMLfileSelected() {
		return xmlFileSelected;
	}

	public void setXMLfileSelected(boolean xmlFileSelected) {
		this.xmlFileSelected = xmlFileSelected;
	}

	public JFileChooser getFileChooserXML() {
		return fileChooserXML;
	}

	public JFileChooser getFileChooserXSD() {
		return fileChooserXSD;
	}

	public void setValidationError(String error) {
		this.validationError = error;
	}

	public String getValidationError() {
		return this.validationError;
	}
	
	
	public ArrayList <Component> getComponentsToDisable(){
		ArrayList <Component> c = new ArrayList <Component>();
		c.add(browseXMLbutton);	
		c.add(browseXSDbutton);
		c.add(validateButton);
		return c;
	}
}
