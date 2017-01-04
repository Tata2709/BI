package BI.validation;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JPanel;

public class ValidationPanel extends JPanel{
	private FilePanel filePanel;
	private ResultPanel resultPanel;
	private ValidationController controller;

	
	public ValidationPanel(ValidationController vc){
		super();
		
		this.controller = vc;
		
		filePanel = new FilePanel(vc);
		resultPanel = new ResultPanel(vc);		
		
		setBackground(new Color(255, 255, 255));
		setLayout(new BorderLayout(0, 5));
		
		add(filePanel, BorderLayout.CENTER);
		add(resultPanel, BorderLayout.SOUTH);	
		
		setSize(600, 500);
		setMinimumSize(new Dimension(500, 400));
		setVisible(true);
	}
	
	public ValidationController getController() {
		return controller;
	}
	
	public FilePanel getFilePanel(){
		return filePanel;
	}
	
	public ResultPanel getResultPanel(){
		return resultPanel;
	}
	
}
