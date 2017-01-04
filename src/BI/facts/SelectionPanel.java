package BI.facts;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

public class SelectionPanel extends JPanel {
	private FactsController controller;

//	// first radio button to extract all facts
//	private JRadioButton allFactsRadio;
//
//	// second radiobutton to extract only specified facts belonging to the
//	// specified leads
//	private JRadioButton selectedFactsRadio;

	private JTextArea leadInputField;
	private JButton selectAllButton;
	private JButton deselectAllButton;

//	// radiobutton group
//	private ButtonGroup extractRadioButtonGroup;

	// currently available Facts
	String[] facts = { "ActivityAborted", "ActivityCancelled",
			"ActivityCompleted", "ActivityEscalated", "ActivityOverdue",
			"ActivityPlanned", "ActivityRescheduled", "FirstOfferPublished",
			"LeadCandidateQualified", "LeadCandidateReceived", "LeadClosed",
			"LeadCreatedManually", "LeadReferred", "OfferPublished",
			"OrderAmendmentConfirmed", "OrderCancelled", "OrderCreated",
			"OrderPublished", "OrderTriggered", "SalesAlternativeApproval",
			"SalesAlternativeCreated", "TestDrivePerformed",
			"VehicleHandedOver" };

	ArrayList<JButton> factsButtons;
	ArrayList<String> selectedFacts;

	public SelectionPanel(FactsController fc) {
		this.controller = fc;
		factsButtons = new ArrayList<JButton>();
		selectedFacts = new ArrayList<String>();

		initializeUIcomponents();

		addListeners();

	}

	private void initializeUIcomponents() {
		setPreferredSize(new Dimension(700, 400));

		JPanel selectButtonsPanel = new JPanel();
		selectAllButton = new JButton("Select all");
		selectAllButton.setForeground(new Color(10, 102, 51));
		selectAllButton.setFont(new Font("Times New Roman", Font.PLAIN, 16));
		
		
		deselectAllButton = new JButton("Deselect all");
		deselectAllButton.setForeground(new Color(10, 102, 51));
		deselectAllButton.setFont(new Font("Times New Roman", Font.PLAIN, 16));
		selectButtonsPanel.setLayout(new BorderLayout());
		selectButtonsPanel.add(selectAllButton, BorderLayout.WEST);
		selectButtonsPanel.add(deselectAllButton, BorderLayout.EAST);
		
		// panel for selecting facts and entering leads
		JPanel leadsAndFactsPanel = new JPanel();

		// panel for Facts buttons on the right (CENTER)
		JPanel factsButtonsPanel = new JPanel();
		factsButtonsPanel.setLayout(new GridLayout(6, 4, 5, 5));
		for (String fact : facts) {
			JButton factButton = new JButton(fact);
			factButton.setFont(new Font("Times New Roman", Font.PLAIN, 12));
			factButton.setForeground(new Color(93, 93, 95));
			factButton.setPreferredSize(new Dimension(160, 30));
			factsButtonsPanel.add(factButton);
			factButton.setEnabled(true);
			factsButtons.add(factButton);
		}
		factsButtonsPanel
				.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		// panel for Lead IDs (BOTTOM)
		JPanel leadInputPanel = new JPanel();
		JLabel leadIDsLabel = new JLabel(
				"Enter lead IDs separated by space (max. 10)          Leave empty if extracting Facts for all Leads ");
		leadIDsLabel.setForeground(new Color(10, 102, 51));
		leadIDsLabel.setFont(new Font("Times New Roman", Font.PLAIN, 13));
		
		leadInputField = new JTextArea();
		leadInputField.setEnabled(true);
		leadInputField.setPreferredSize(new Dimension(630, 40));
		leadInputPanel.setLayout(new BorderLayout());
		leadInputPanel.add(leadIDsLabel, BorderLayout.NORTH);
		leadInputPanel.add(leadInputField, BorderLayout.CENTER);
		leadInputPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		leadsAndFactsPanel.add(factsButtonsPanel, BorderLayout.NORTH);
		leadsAndFactsPanel.add(leadInputPanel, BorderLayout.SOUTH);

		setLayout(new BorderLayout());
		add(selectButtonsPanel, BorderLayout.NORTH);
		add(leadsAndFactsPanel, BorderLayout.CENTER);

		Border innerBorder = BorderFactory.createTitledBorder("");
		Border outerBorder = BorderFactory.createEmptyBorder(5, 5, 5, 5);
		setBorder(BorderFactory.createCompoundBorder(outerBorder, innerBorder));
	}

	private void addListeners() {

		selectAllButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				selectedFacts.clear();
				for (JButton button : factsButtons){
					button.setForeground(Color.RED);
					String fact = convertButtonToFact(button.getText());
					addSelectedFact(true, fact);
				}
			}
		});
		
		deselectAllButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				selectedFacts.clear();
				for (JButton button : factsButtons){
					button.setForeground(new Color(93, 93, 95));
				}
			}
		});
		
		for (JButton button : factsButtons) {
			button.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					String fact = convertButtonToFact(button.getText());
					if (button.getForeground().equals(new Color(93, 93, 95))) {
						button.setForeground(Color.RED);
						addSelectedFact(true, fact);
					} else {
						button.setForeground(new Color(93, 93, 95));
						addSelectedFact(false, fact);
					}

				}
			});
		}
	}
	private void addSelectedFact(boolean add, String fact) {
		if (add) {
			selectedFacts.add(fact);
		} else {
			selectedFacts.remove(fact);
		}
	}

	private String convertButtonToFact(String buttonLabel) {
		switch (buttonLabel) {
		case "ActivityAborted": {
			return "F_Activity_Aborted";
		}
		case "ActivityCancelled": {
			return "F_Activity_Cancelled";
		}
		case "ActivityCompleted": {
			return "F_Activity_Completed";
		}
		case "ActivityEscalated": {
			return "F_Activity_Escalated";
		}
		case "ActivityOverdue": {
			return "F_Activity_Overdue";
		}
		case "ActivityPlanned": {
			return "F_Activity_Planned";
		}
		case "ActivityRescheduled": {
			return "F_Activity_Rescheduled";
		}
		case "FirstOfferPublished": {
			return "F_First_Offer_Published";
		}
		case "LeadCandidateQualified": {
			return "F_Lead_Candidate_Qualified";
		}
		case "LeadCandidateReceived": {
			return "F_Lead_Candidate_Received";
		}
		case "LeadClosed": {
			return "F_Lead_Closed";
		}
		case "LeadCreatedManually": {
			return "F_Lead_Created_Manually";
		}
		case "LeadReferred": {
			return "F_Lead_Referred";
		}
		case "OfferPublished": {
			return "F_Offer_Published";
		}
		case "OrderAmendmentConfirmed": {
			return "F_Order_Amandement_Confirmed";
		}
		case "OrderCancelled": {
			return "F_Order_Cancelled";
		}
		case "OrderCreated": {
			return "F_Order_Created";
		}
		case "OrderPublished": {
			return "F_Order_Published";
		}
		case "OrderTriggered": {
			return "F_Order_Triggered";
		}
		case "SalesAlternativeApproval": {
			return "F_Sales_Alternative_Approval";
		}
		case "SalesAlternativeCreated": {
			return "F_Sales_Alternative_Created";
		}
		case "TestDrivePerformed": {
			return "F_Test_Drive_Performed";
		}
		case "VehicleHandedOver": {
			return "F_Vehicle_Handed_Over";
		}
		default:
			return "";
		}
	}

	public ArrayList<String> getSelectedFacts() {
		return selectedFacts;
	}
	public String getSelectedLeadIDs(){
		int inputLength = leadInputField.getText().trim().length();
		if(inputLength > 150){
			return leadInputField.getText().trim().substring(0, 150);
		}
		else{
			return leadInputField.getText().trim();
		}
	}

	public String[] getFacts() {
		return facts;
	}
	
	public ArrayList <Component> getComponentsToDisable(){
		ArrayList <Component> c = new ArrayList <Component>();
		c.add(selectAllButton);	
		c.add(deselectAllButton);
		c.addAll(factsButtons);
		c.add(leadInputField);
		return c;
	}
}
