package BI.validation;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.Border;
import javax.swing.filechooser.FileNameExtensionFilter;

public class ErrorLogDialog extends JDialog {

	private JButton closeButton;
	private JButton saveLogButton;
	private JPanel errorTextPanel;
	private JTextArea errorLogTextArea;
	private JScrollPane errorLogScrollPane;
	private JPanel buttonsPanel;
	private ValidationController controller;

	private JFileChooser fileChooserTXT;


	public ErrorLogDialog(JFrame parent, ValidationController controller){
		super(parent, "Validation error log", true);
		
		this.controller = controller;
		setResizable(false);
		fileChooserTXT = new JFileChooser();
		FileNameExtensionFilter filterTXT = new FileNameExtensionFilter("TXT", "txt");
		fileChooserTXT.setFileFilter(filterTXT);
		
		closeButton = new JButton("Close");
		saveLogButton = new JButton("Save Log");
		errorTextPanel = new JPanel();
		buttonsPanel = new JPanel();
		errorLogTextArea = new JTextArea();
		errorLogTextArea.setLineWrap(true);
		errorLogTextArea.setMargin(new Insets(10,10,10,10));
		
		errorTextPanel.setPreferredSize(new Dimension(350, 200));
		errorTextPanel.setBackground(new Color(245, 245, 245));
		Border innerBorder = BorderFactory.createTitledBorder("Validation failure log");
		Border outerBorder = BorderFactory.createEmptyBorder(1,1,1,1);
		errorTextPanel.setBorder(BorderFactory.createCompoundBorder(outerBorder, innerBorder));
		errorTextPanel.setLayout(new BorderLayout());
		errorLogScrollPane = new JScrollPane(errorLogTextArea);
		errorLogScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		errorLogScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		errorTextPanel.add(errorLogScrollPane, BorderLayout.CENTER);
		
		
		
		buttonsPanel.setLayout(new BorderLayout(50, 0));
		buttonsPanel.add(saveLogButton, BorderLayout.LINE_START);
		buttonsPanel.add(closeButton, BorderLayout.LINE_END);
		
		
		
		
		add(errorTextPanel);
		add(buttonsPanel);
	
		setLayout(new FlowLayout());
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setSize(400, 300);
		setLocationRelativeTo(parent);
		
		closeButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				ErrorLogDialog.this.setVisible(false);
				ErrorLogDialog.this.dispatchEvent(new WindowEvent(
						ErrorLogDialog.this, WindowEvent.WINDOW_CLOSING));
	            }
			});
		
		SaveErrorLogActionListener selListener = new SaveErrorLogActionListener(controller);
		saveLogButton.addActionListener(selListener);
		
	}
	public JFileChooser getFileChooserTXT(){
		return fileChooserTXT;
	}
	public void showErrorLog(String errorMessage) {
		errorLogTextArea.setText(errorMessage);
		errorLogTextArea.setVisible(true);
	}
	public String getErrorText(){
		return errorLogTextArea.getText();
	}
}
