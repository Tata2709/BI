package BI.facts;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JPanel;

public class FactsButtonActionListener implements ActionListener {
	private JButton button;
	private SelectionPanel selectionPanel;
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

	FactsButtonActionListener(JButton b, SelectionPanel panel) {
		this.button = b;
		this.selectionPanel = panel;

	}

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

	private void addSelectedFact(boolean add, String fact) {
		ArrayList<String> selectedFacts = selectionPanel.getSelectedFacts();
		if (add) {

			selectedFacts.add(fact);
		} else {
			selectedFacts.remove(fact);
		}
	}

	public String[] getFacts() {
		return facts;
	}

	
}
