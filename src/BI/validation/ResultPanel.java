package BI.validation;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;

import BI.common.components.UpdateProgressTask;

public class ResultPanel extends JPanel {

	// GUI elements
	private JLabel resultLabel;
	private JButton errorLogButton;
	private JLabel inProgressLabel;

	// logic
	private ValidationController controller;

	public ResultPanel(ValidationController vc) {

		super();

		this.controller = vc;

		// initialize GUI elements
		initializeGUIelements();

		addListeners();

		// layout
		layoutComponents();

	}

	// init GUI elements
	private void initializeGUIelements() {
		resultLabel = new JLabel();
		inProgressLabel = new JLabel("Validation in progress...");
		errorLogButton = new JButton("Show Error Log");
		errorLogButton.setForeground(new Color(93, 93, 95));
		errorLogButton.setVisible(false);
		resultLabel.setForeground(new Color(93, 93, 95));
		inProgressLabel.setVisible(false);
		inProgressLabel.setForeground(new Color(93, 93, 95));
	};

	private void layoutComponents() {
		setBackground(new Color(245, 245, 245));
		setPreferredSize(new Dimension(100, 100));

		// graphics

		Border innerBorder = BorderFactory.createTitledBorder("Result");
		Border outerBorder = BorderFactory.createEmptyBorder(5, 5, 5, 5);
		setBorder(BorderFactory.createCompoundBorder(outerBorder, innerBorder));

		setLayout(new GridBagLayout());
		GridBagConstraints gc = new GridBagConstraints();

		// first row first column: result label (gridy 0, gridx 0)
		gc.gridy = 0;
		gc.gridx = 0;
		gc.weightx = 0.1;
		gc.weighty = 0.1;

		gc.fill = GridBagConstraints.NONE;
		gc.anchor = GridBagConstraints.LINE_START;
		gc.insets = new Insets(0, 0, 0, 5);
		add(resultLabel, gc);
		
		// second row first column: in progress label (gridy 1, gridx 0)
		gc.gridy = 1;
		gc.gridx = 0;
		add(inProgressLabel,gc);

		// first row second column: invisible button(gridy 0, gridx 1)
		gc.gridy = 0;
		gc.gridx = 1;

		gc.fill = GridBagConstraints.VERTICAL;
		gc.anchor = GridBagConstraints.LINE_END;
		add(errorLogButton, gc);
	}

	// make result label visible inside the panel
	public void showResult(String text, boolean validationOK) {
		inProgressLabel.setVisible(false);
		resultLabel.setText(text);
		resultLabel.setVisible(true);
		errorLogButton.setVisible(!validationOK);
	}

	// show that validation is in progress
	public void showValidationInProgress() {
	
	Runnable showTaskInProgress = new UpdateProgressTask(inProgressLabel);
		SwingUtilities.invokeLater(showTaskInProgress);
		
	}

	public void setController(ValidationController controller) {
		this.controller = controller;
	}

	public void addListeners() {
		errorLogButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				controller.showErrorLog();
			}
		});
	}

	public void refresh() {
		resultLabel.removeAll();
		resultLabel.setVisible(false);
		errorLogButton.setVisible(false);
		inProgressLabel.setVisible(false);
	}
}