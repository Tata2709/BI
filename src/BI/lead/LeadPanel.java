package BI.lead;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JPanel;

import BI.lead.ExtractPanel;
import BI.lead.ImportFilePanel;
import BI.lead.SelectionPanel;

public class LeadPanel extends JPanel{
	private ImportFilePanel importFilePanel;
	private SelectionPanel selectionPanel;
	private ExtractPanel extractPanel;
	private LeadController controller;
	
	public LeadPanel(){
		super();
		
		setBackground(new Color(255, 255, 255));
		setLayout(new BorderLayout());
		
		setVisible(true);
		
		
		importFilePanel = new ImportFilePanel();
		selectionPanel = new SelectionPanel();
		extractPanel = new ExtractPanel();
		
		add(importFilePanel, BorderLayout.NORTH);
		add(selectionPanel, BorderLayout.CENTER);
		add(extractPanel, BorderLayout.SOUTH);
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
