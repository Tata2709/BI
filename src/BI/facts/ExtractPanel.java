package BI.facts;

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
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.filechooser.FileNameExtensionFilter;

import BI.common.components.UpdateProgressTask;

public class ExtractPanel extends JPanel {
	private FactsController controller;
	private JButton extractButton;
	private JFileChooser fileChooserXML;
	private JLabel extractionResultLabel;
	private JLabel inProgressLabel;
	private JLabel exportFileLabel;

	public ExtractPanel(FactsController fc) {
		this.controller = fc;

		initializeUIcomponents();

		extractButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Thread queryThread = new Thread() {
					public void run() {
						
						controller.extractData();
					}
				};
				queryThread.start();
			}
		});
	}

	public void activateExtractButton(boolean show) {
		extractButton.setEnabled(show);
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

	// show that validation is in progress
	public void showExtractionInProgress() {

		Runnable showTaskInProgress = new UpdateProgressTask(inProgressLabel);
		SwingUtilities.invokeLater(showTaskInProgress);

	}

	public JFileChooser getFileChooserXML() {
		return fileChooserXML;
	}

	public JLabel getExtractionResultLabel() {
		return extractionResultLabel;
	}

	public void setResult(String result) {
		inProgressLabel.setVisible(false);
		extractionResultLabel.setVisible(true);
		extractionResultLabel.setText(result);
	}

	public void setInProgress(String inProgress) {
		inProgressLabel.setVisible(true);
		inProgressLabel.setText(inProgress);
	}

	public void refresh() {
		exportFileLabel.setVisible(false);
		inProgressLabel.setVisible(false);
		extractionResultLabel.setVisible(false);
	}

	
	public void showExportFile(String file) {
		int length = file.length();
		
		if (length <= 65) {
			exportFileLabel.setText(file);
		} else {
			String fileNameCutStart = file.substring(0, 60);
			String fileNameCut = fileNameCutStart + " ... .xml";
			exportFileLabel.setText(fileNameCut);
			exportFileLabel.setFont(new Font("Times New Roman", Font.BOLD, 13));
		}
		
		exportFileLabel.setVisible(true);
	}

	public JButton getExtractButton() {
		return extractButton;
	}
}
