package BI.facts;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JPanel;


public class FactsPanel extends JPanel{
	private ImportFilePanel importFilePanel;
	private SelectionPanel selectionPanel;
	private ExtractPanel extractPanel;
	private FactsController controller;
	
	public FactsPanel(FactsController controller){
		super();
		
		this.controller = controller;
		
		setBackground(new Color(255, 255, 255));
		setLayout(new BorderLayout());
		
		setVisible(true);
		
		importFilePanel = new ImportFilePanel(controller);
		selectionPanel = new SelectionPanel(controller);
		extractPanel = new ExtractPanel(controller);
		
		add(importFilePanel, BorderLayout.NORTH);
		add(selectionPanel, BorderLayout.CENTER);
		add(extractPanel, BorderLayout.PAGE_END);
	}
	
	public ImportFilePanel getImportFilePanel(){
		return importFilePanel;
	}
	
	public SelectionPanel getSelectionPanel(){
		return selectionPanel;
	}
	
	public ExtractPanel getExtractPanel(){
		return extractPanel;
	}
}
