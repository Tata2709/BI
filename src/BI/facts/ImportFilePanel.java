package BI.facts;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.filechooser.FileNameExtensionFilter;

public class ImportFilePanel extends JPanel{
	
	
	//GUI elements
	
	//buttons
	private JButton browseXMLbutton;
	
	//XML file name labels
	private JLabel xmlFileLabel;
	private JLabel xmlPathLabel;
		
	//XML and XSD file panels
	private JPanel xmlPanel;
	
	//file choosers
	private JFileChooser fileChooserXML;
	
	//file names
	private String xmlFile;
	
	//flags to check whether valid XML has been selected
	private boolean xmlFileSelected;
	
	//logic, controller
	private FactsController controller;
	
	
	public ImportFilePanel(FactsController fc){
		super();
		
		this.controller = fc;
		
		// initialize GUI elements
		initializeGUIelements();
				
		addListeners();
		
		xmlFileSelected = true;
				
		//layout
		layoutComponents();
		
	}
	
	public void initializeGUIelements(){
		setBackground(new Color(245, 245, 245));
		
		//buttons
		browseXMLbutton = new JButton("Select XML file");
		browseXMLbutton.setForeground(new Color(93, 93, 95));
		browseXMLbutton.setFont(new Font("Times New Roman", Font.BOLD, 18));
		
		//XML labels
		xmlFileLabel = new JLabel();
		xmlPathLabel = new JLabel();
		
		//XML and XSD file panels with buttons and labels inside
		xmlPanel = new JPanel();
		addElementsToPanel(xmlPanel, xmlFileLabel, xmlPathLabel, browseXMLbutton);
		
		//fileChooser
		fileChooserXML = new JFileChooser();
		FileNameExtensionFilter filterXML = new FileNameExtensionFilter("XML", "xml");
		fileChooserXML.setFileFilter(filterXML);
				
	}
	
	private void layoutComponents(){
		setPreferredSize(new Dimension(600, 140));
		setLayout(new BorderLayout());
		add(xmlPanel, BorderLayout.CENTER);
	}
	
	public void addListeners(){
		browseXMLbutton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent event) {
				controller.showSelectedFileXML();
				
				
				
			}
		});
	}
	
	private void addElementsToPanel(JPanel panel, JLabel fileLabel, JLabel pathLabel,
			JButton browseButton){
		
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BorderLayout());
		browseButton.setPreferredSize(new Dimension(200, 60));
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
		Border outerBorder = BorderFactory.createEmptyBorder(5, 5, 5, 5);
		panel.setBorder(BorderFactory.createCompoundBorder(outerBorder,
				innerBorder));
		panel.setVisible(true);
		
	}
	
	public JFileChooser getFileChooserXML() {
		return fileChooserXML;
	}
	
	public void showFileLabelXML(){
		Path completePath = Paths.get(fileChooserXML.getSelectedFile().getAbsolutePath());
		String fileName = completePath.getFileName().toString(); 
		String path = completePath.getParent().toString();
		xmlPathLabel.setText(path);
		xmlPathLabel.setFont(new Font("Times New Roman", Font.PLAIN, 12));
		xmlPathLabel.setVisible(true);
		
		xmlFileLabel.setText(fileName);
		
		int length = fileName.length();
		if (length <= 70) {
			xmlFileLabel.setFont(new Font("Times New Roman", Font.BOLD, 25));
		} else  {
			String fileNameCutStart = fileName.substring(0, 58);
			String fileNameCut = fileNameCutStart + " ... .xml";
			xmlFileLabel.setText(fileNameCut);
			xmlFileLabel.setFont(new Font("Times New Roman", Font.BOLD, 20));
		}
		
		xmlFileLabel.setForeground(new Color(93, 93, 95));
		xmlFileLabel.setVisible(true);
	}
	
	public void setXMLfile(String xmlFile) {
		this.xmlFile = xmlFile;
	}
	

	public String getXMLfile() {
		return xmlFile;
	}
	
	public void setXMLfileSelected(boolean xmlFileSelected) {
		this.xmlFileSelected = xmlFileSelected;
	}
	
	public boolean isXMLfileSelected() {
		return xmlFileSelected;
	}
	
	public JButton getBrowseButton(){
		return browseXMLbutton;
	}

}