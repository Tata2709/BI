package BI.data_count;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.filechooser.FileNameExtensionFilter;

public class DataCountPanel extends JPanel {
	private DataCountController controller;

	// GUI elements
	private JButton browseButton;
	private JLabel pathLabel;
	private JLabel fileLabel;
	private JLabel countingResultLabel;
	private JButton countButton;

	public DataCountPanel(DataCountController dc) {
		controller = dc;

		// part of the panel for input file
		JPanel inputFilePanel = initializeInputFilePanelGUI();

		// part of the panel for counting results
		JPanel resultPanel = initializeResultPanelGUI();
		
		// part of the panel for Count button
		JPanel countButtonPanel = initializeCountButtonPanelGUI();

		//adding component panels
		FlowLayout experimentLayout = new FlowLayout(FlowLayout.CENTER, 10, 10);
		setLayout(experimentLayout);
		add(inputFilePanel);
		add(resultPanel);
		add(countButtonPanel);

		setSize(700, 650);
		setBackground(new Color(245, 245, 245));
		setBorder(this, "", 5, 5, 5, 5);
		
		

	}
	//set button style
	private void setButtonStyle(JButton button, int width, int height, int fontSize){
		button.setForeground(new Color(93, 93, 95));
		button.setFont(new Font("Times New Roman", Font.BOLD, fontSize));
		button.setPreferredSize(new Dimension(width, height));
	}
	
	//set border for JPanel
	private void setBorder(JPanel panel, String title, int top, int left, int bottom, int right){
		Border innerBorder = BorderFactory.createTitledBorder(title);
		Border outerBorder = BorderFactory.createEmptyBorder(top, left, bottom, right);
		panel.setBorder(BorderFactory.createCompoundBorder(outerBorder, innerBorder));
	}
	// initialize GUI layout for Browse button, input file name and path labels
	private JPanel initializeInputFilePanelGUI() {

		JPanel inputFilePanel = new JPanel();
		inputFilePanel.setPreferredSize(new Dimension(670, 150));
		setBorder(inputFilePanel, "", 5, 5, 5, 5);

		// part of the panel for "Browse" button and input file path
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BorderLayout());
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
		
		//Browse button
		browseButton = new JButton("Browse");
		setButtonStyle(browseButton, 200, 80, 21);
		buttonPanel.add(browseButton, BorderLayout.EAST);
		
		//label for input file path
		pathLabel = new JLabel("");
		buttonPanel.add(pathLabel, BorderLayout.WEST);

		// part of the panel for input file name
		JPanel fileNamePanel = new JPanel();
		fileNamePanel.setLayout(new BorderLayout());
		
		//label for input file name
		fileLabel = new JLabel("");
		fileNamePanel.add(fileLabel, BorderLayout.WEST);
		fileNamePanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 15, 0));

		// add component panels to the input file panel
		inputFilePanel.setLayout(new BorderLayout());
		inputFilePanel.add(buttonPanel, BorderLayout.NORTH);
		inputFilePanel.add(fileNamePanel, BorderLayout.SOUTH);

		return inputFilePanel;
	}

	// initialize GUI layout for presenting counting results
	private JPanel initializeResultPanelGUI() {

		JPanel resultPanel = new JPanel();
		resultPanel.setPreferredSize(new Dimension(670, 280));
		
		setBorder(resultPanel, "Result", 10, 5, 10, 5);

		countingResultLabel = new JLabel("");
		countingResultLabel.setFont(new Font("Times New Roman", Font.BOLD, 17));
		resultPanel.add(countingResultLabel);
		return resultPanel;
	}
	
	// initialize GUI layout for Count button
	private JPanel initializeCountButtonPanelGUI() {
		JPanel countingPanel = new JPanel();
		countingPanel.setPreferredSize(new Dimension(670, 100));
		setBorder(countingPanel, "", 10, 10,0, 5);

		countButton = new JButton("Count");
		setButtonStyle(countButton, 450, 80, 30);
		countButton.setEnabled(true);
		
		countingPanel.add(countButton, BorderLayout.CENTER);
		
		return countingPanel;

	}
	public JButton getBrowseButton() {
		return browseButton;
	}
	public JLabel getPathLabel() {
		return pathLabel;
	}
	public JLabel getFileLabel() {
		return fileLabel;
	}
	public JLabel getCountingResultLabel() {
		return countingResultLabel;
	}
	public JButton getCountButton() {
		return countButton;
	}
}
