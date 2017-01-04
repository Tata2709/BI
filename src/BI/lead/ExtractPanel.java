package BI.lead;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.filechooser.FileNameExtensionFilter;

public class ExtractPanel extends JPanel {
	private JButton extractButton;
	private JFileChooser fileChooserXML;
	private JLabel extractionResultLabel;
	private JLabel inProgressLabel;
	private JLabel exportFileLabel;

	public ExtractPanel() {

		initializeUIcomponents();

	}

	public void activateExtractButton(boolean show) {
		extractButton.setEnabled(true);
	}

	private void initializeUIcomponents() {
		// separate panel for button "Extract"
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BorderLayout());
		extractButton = new JButton("Export data");
		extractButton.setPreferredSize(new Dimension(200, 60));
		extractButton.setForeground(new Color(93, 93, 95));
		extractButton.setFont(new Font("Times New Roman", Font.BOLD, 18));
		buttonPanel.add(extractButton, BorderLayout.NORTH);
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		// separate panel for status labels
		JPanel labelPanel = new JPanel();

		extractionResultLabel = new JLabel();
		extractionResultLabel.setVisible(false);
		extractionResultLabel.setForeground(new Color(93, 93, 95));

		inProgressLabel = new JLabel("Extraction in progress...");
		inProgressLabel.setVisible(false);
		inProgressLabel.setForeground(new Color(93, 93, 95));

		exportFileLabel = new JLabel();
		exportFileLabel.setVisible(false);
		exportFileLabel.setForeground(new Color(93, 93, 95));

		labelPanel.setLayout(new GridLayout(3, 1));
		labelPanel.add(extractionResultLabel);
		labelPanel.add(inProgressLabel);
		labelPanel.add(exportFileLabel);

		labelPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 15, 0));

		setLayout(new BorderLayout());
		add(buttonPanel, BorderLayout.EAST);
		add(labelPanel, BorderLayout.WEST);

		Border innerBorder = BorderFactory.createTitledBorder("");
		Border outerBorder = BorderFactory.createEmptyBorder(5, 5, 5, 5);
		setBorder(BorderFactory.createCompoundBorder(outerBorder, innerBorder));
		setVisible(true);

		fileChooserXML = new JFileChooser();
		FileNameExtensionFilter filterXML = new FileNameExtensionFilter("XML",
				"xml");
		fileChooserXML.setFileFilter(filterXML);

	}

	public JFileChooser getfileChooserXML() {
		return fileChooserXML;
	}

	public JLabel getExtractionResultLabel() {
		return extractionResultLabel;
	}

	public void refresh() {
		extractionResultLabel.setText("");
		exportFileLabel.setText("");
	}

	public JButton getExtractButton() {
		return extractButton;
	}

	public JLabel getExportFileLabel() {
		return exportFileLabel;
	}

	public void setExtractionResultLabel(JLabel extractionResultLabel) {
		this.extractionResultLabel = extractionResultLabel;
	}

	public JLabel getInProgressLabel() {
		return inProgressLabel;
	}
}
