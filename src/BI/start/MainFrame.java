package BI.start;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;

import BI.data_count.DataCountController;
import BI.data_count.DataCountPanel;
import BI.facts.FactsController;
import BI.facts.FactsPanel;
import BI.lead.LeadController;
import BI.lead.LeadPanel;
import BI.validation.ValidationController;
import BI.validation.ValidationPanel;

public class MainFrame extends JFrame {

	private ValidationPanel validationPanel;
	private DataCountPanel dataCountPanel;
	private JTabbedPane tabPane;
	private FactsPanel factsPanel;
	private FactsController factsController;
	private LeadPanel leadPanel;
	private LeadController leadController;

	public MainFrame() {
		super("BI Toolbox");

		setBackground(new Color(255, 255, 255));
		setLayout(new BorderLayout(0, 5));
		UIManager.put("JFrame.activeTitleBackground", Color.red);

		setSize(700, 650);
		setMinimumSize(new Dimension(500, 400));
		setLocationRelativeTo(null);
		setResizable(false);
		setVisible(true);

		tabPane = new JTabbedPane();

		add(tabPane, BorderLayout.CENTER);
		// panels

		// validate XML against XSD
		ValidationController validationController = new ValidationController(
				this, tabPane);
		validationPanel = new ValidationPanel(validationController);
		validationController.setValidationPanel(validationPanel);
		tabPane.addTab("XML validation", validationPanel);

		// data count panel
		DataCountController dataCountController = new DataCountController(
				this, tabPane);
		dataCountPanel = new DataCountPanel(dataCountController);
		dataCountController.setDataCountPanel(dataCountPanel);
		tabPane.addTab("Data Count", dataCountPanel);

		// Facts panel
		factsController = new FactsController(this, tabPane);
		factsPanel = new FactsPanel(factsController);
		factsController.setFactsPanel(factsPanel);
		tabPane.addTab("Facts", factsPanel);

		// Lead panel
		leadPanel = new LeadPanel();
		LeadController leadController = new LeadController(this, leadPanel,
				tabPane);
		tabPane.addTab("Leads", leadPanel);

		tabPane.addTab("Persons", new JPanel());
		tabPane.addTab("Organisations", new JPanel());
		// Extract elements panel
		JPanel elementsPanel = new JPanel();
		tabPane.addTab("Other Elements", elementsPanel);
		tabPane.addTab("XSD validation", new JPanel());
		tabPane.addTab("Coverage", new JPanel());

		tabPane.setEnabledAt(1, true);
		tabPane.setEnabledAt(2, false);
		tabPane.setEnabledAt(3, false);
		tabPane.setEnabledAt(4, false);
		tabPane.setEnabledAt(5, false);
		tabPane.setEnabledAt(6, false);
		tabPane.setEnabledAt(7, false);
		tabPane.setEnabledAt(8, false);

		// closing operation
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		ImageIcon icon = new ImageIcon("bi9.png");

		setIconImage(icon.getImage());
	}

	public JTabbedPane getTabPane() {
		return tabPane;
	}
}
