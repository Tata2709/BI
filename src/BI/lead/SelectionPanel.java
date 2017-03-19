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

public class SelectionPanel extends JPanel {

	// radiobuttons
	private JRadioButton selectedLeadIDsRadio;
	private JRadioButton leadElementsRadio;
	private ButtonGroup extractRadioButtonGroup;

	// field for entering lead IDs
	JTextArea leadIDField;

	// field for entering element name
	JTextArea elementNameField;

	// field for entering element value
	JTextArea elementValueField;

	private final String EXTRACT_BY_LEADIDS_RADIO = "Extract leads with the specified IDs";
	private final String LEAD_INPUT_HINT = "Enter lead IDs separated by space (max. 20)                       If left empty - ALL leads are extracted ";
	private final String EXTRACT_BY_ELEMENTS_RADIO = "Extract leads by child elements";
	
	
	public SelectionPanel() {

		initializeUIcomponents();
	}

	// create the upper half of the selection panel
	// - the panel with elements for extracting only specified leads
	private JPanel createLeadIDsPanel() {

		JPanel extractLeadsByIDsPanel = new JPanel();

		// radiobutton to select the extraction mode
		selectedLeadIDsRadio = new JRadioButton(EXTRACT_BY_LEADIDS_RADIO);
		selectedLeadIDsRadio
				.setFont(new Font("Times New Roman", Font.BOLD, 16));
		selectedLeadIDsRadio.setForeground(new Color(93, 93, 95));
		selectedLeadIDsRadio.setSelected(true);

		// panel for input field for lead IDs and the label for the field
		JPanel leadIDsPanel = new JPanel();
		leadIDsPanel.setLayout(new BorderLayout());

		// label
		JLabel enterLeadHintLabel = new JLabel(LEAD_INPUT_HINT);
		enterLeadHintLabel.setForeground(new Color(153, 0, 76));
		enterLeadHintLabel.setForeground(new Color(0, 102, 51));
		enterLeadHintLabel.setFont(new Font("Times New Roman", Font.PLAIN, 13));

		// input field
		leadIDField = new JTextArea();
		leadIDField.setEnabled(true);
		leadIDField.setPreferredSize(new Dimension(630, 40));
		leadIDField.setLineWrap(true);
		leadIDField.setWrapStyleWord(true);

		// add input field and label to their panel
		leadIDsPanel.add(enterLeadHintLabel, BorderLayout.NORTH);
		leadIDsPanel.add(leadIDField, BorderLayout.CENTER);

		// pack everything into the panel
		extractLeadsByIDsPanel.setLayout(new BorderLayout(20, 20));
		extractLeadsByIDsPanel.add(selectedLeadIDsRadio, BorderLayout.NORTH);
		extractLeadsByIDsPanel.add(leadIDsPanel, BorderLayout.CENTER);
		extractLeadsByIDsPanel.setBorder(BorderFactory.createEmptyBorder(5, 5,
				5, 5));

		return extractLeadsByIDsPanel;
	}
	
	private JPanel createLeadElementsPanel(){
		JPanel leadElementsPanel = new JPanel();

		// radiobutton to select the extraction mode
		leadElementsRadio = new JRadioButton(EXTRACT_BY_ELEMENTS_RADIO);
		selectedLeadIDsRadio
				.setFont(new Font("Times New Roman", Font.BOLD, 16));
		selectedLeadIDsRadio.setForeground(new Color(93, 93, 95));
		selectedLeadIDsRadio.setSelected(false);
//
//		// panel for input field for lead IDs and the label for the field
//		JPanel leadIDsPanel = new JPanel();
//		leadIDsPanel.setLayout(new BorderLayout());
//
//		// label
//		JLabel enterLeadHintLabel = new JLabel(LEAD_INPUT_HINT);
//		enterLeadHintLabel.setForeground(new Color(153, 0, 76));
//		enterLeadHintLabel.setForeground(new Color(0, 102, 51));
//		enterLeadHintLabel.setFont(new Font("Times New Roman", Font.PLAIN, 13));
//
//		// input field
//		leadIDField = new JTextArea();
//		leadIDField.setEnabled(true);
//		leadIDField.setPreferredSize(new Dimension(630, 40));
//		leadIDField.setLineWrap(true);
//		leadIDField.setWrapStyleWord(true);
//
//		// add input field and label to their panel
//		leadIDsPanel.add(enterLeadHintLabel, BorderLayout.NORTH);
//		leadIDsPanel.add(leadIDField, BorderLayout.CENTER);

		// pack everything into the panel
		leadElementsPanel.setLayout(new BorderLayout(20, 20));
		leadElementsPanel.add(leadElementsRadio, BorderLayout.NORTH);
//		leadElementsPanel.add(leadIDsPanel, BorderLayout.CENTER);
		leadElementsPanel.setBorder(BorderFactory.createEmptyBorder(5, 5,
				5, 5));

		return leadElementsPanel;
	}

	private void initializeUIcomponents() {
		setPreferredSize(new Dimension(700, 400));

		// the upper half of the selection panel - radiobutton and input field
		// for lead IDs
		JPanel extractLeadsByIDsPanel = createLeadIDsPanel();

		// the upper half of the selection panel - radiobutton and input field
		// for lead IDs
		JPanel extractLeadsByOtherElements = createLeadElementsPanel();

		extractRadioButtonGroup = new ButtonGroup();
		extractRadioButtonGroup.add(selectedLeadIDsRadio);

		setLayout(new FlowLayout());
		add(extractLeadsByIDsPanel);
		add(extractLeadsByOtherElements);

		Border innerBorder = BorderFactory.createTitledBorder("");
		Border outerBorder = BorderFactory.createEmptyBorder(5, 5, 5, 5);
		setBorder(BorderFactory.createCompoundBorder(outerBorder, innerBorder));
	}

	public ButtonGroup getRadioButtonGroup() {
		return extractRadioButtonGroup;
	}

	public JRadioButton getselectedLeadIDsRadio() {
		return selectedLeadIDsRadio;
	}

	public ButtonGroup getExtractRadioButtonGroup() {
		return extractRadioButtonGroup;
	}

	public JTextArea getLeadInputField() {
		return leadIDField;
	}

	public JRadioButton getLeadElementsRadio() {
		return leadElementsRadio;
	}
}
