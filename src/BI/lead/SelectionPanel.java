package BI.lead;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.Border;

public class SelectionPanel extends JPanel{
	
	// second radiobutton to extract only specified leads
	private JRadioButton selectedLeadIDsRadio;
	
	//radiobutton group
	private ButtonGroup extractRadioButtonGroup;
	
	//field for entering lead IDs
	JTextArea leadInputField;

	
	public SelectionPanel(){
		
		initializeUIcomponents();
	}
	
	private void initializeUIcomponents(){
		setPreferredSize(new Dimension(700, 400));
		
		//panel with elements  for extracting only specified leads
		JPanel extractLeadsByIDsPanel = new JPanel();
		
		selectedLeadIDsRadio = new JRadioButton("Extract Leads by IDs");
		selectedLeadIDsRadio.setFont(new Font("Times New Roman", Font.BOLD, 16));
		selectedLeadIDsRadio.setForeground(new Color(93, 93, 95));
		
		
		
		//panel for input field and label
		JPanel leadIDsPanel = new JPanel();
		leadIDsPanel.setLayout(new BorderLayout());
		
		JLabel enterLeadHintLabel = new JLabel(
				"Enter lead IDs separated by space (max. 20)                       If left empty - ALL leads are extracted ");
		enterLeadHintLabel.setForeground(new Color(153, 0, 76));
		enterLeadHintLabel.setForeground(new Color(0, 102, 51));
		enterLeadHintLabel.setFont(new Font("Times New Roman", Font.PLAIN, 13));
		
		leadInputField = new JTextArea();
		leadInputField.setEnabled(true);
		leadInputField.setPreferredSize(new Dimension(630, 40));
		
		leadIDsPanel.add(enterLeadHintLabel, BorderLayout.NORTH);
		leadIDsPanel.add(leadInputField, BorderLayout.CENTER);
		
		extractLeadsByIDsPanel.setLayout(new BorderLayout(20, 20));
		extractLeadsByIDsPanel.add(selectedLeadIDsRadio, BorderLayout.NORTH);
		extractLeadsByIDsPanel.add(leadIDsPanel, BorderLayout.CENTER);
		extractLeadsByIDsPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		
		
		
		extractRadioButtonGroup = new ButtonGroup();
		extractRadioButtonGroup.add(selectedLeadIDsRadio);
		
		setLayout(new FlowLayout());
		add(extractLeadsByIDsPanel);
		
		Border innerBorder = BorderFactory.createTitledBorder("");
		Border outerBorder = BorderFactory.createEmptyBorder(5, 5, 5, 5);
		setBorder(BorderFactory.createCompoundBorder(outerBorder, innerBorder));
	}
	
		
	
	public ButtonGroup getRadioButtonGroup(){
		return extractRadioButtonGroup;
	}

	public JRadioButton getselectedLeadIDsRadio() {
		return selectedLeadIDsRadio;
	}

	public ButtonGroup getExtractRadioButtonGroup() {
		return extractRadioButtonGroup;
	}

	public JTextArea getLeadInputField() {
		return leadInputField;
	}
}
