package BI.common.components;

import java.awt.Component;
import java.util.ArrayList;

import javax.swing.JTabbedPane;

public class DisableWindowTask implements Runnable {
	JTabbedPane tabPane;
	boolean enableOrDisable;
	ArrayList <Component> components;

	public DisableWindowTask(JTabbedPane pane, boolean enableDisable,
			ArrayList <Component> c) {
		tabPane = pane;
		enableOrDisable = enableDisable;
		components = c;
	}

	@Override
	public void run() {

		tabPane.setEnabledAt(0, enableOrDisable);
		tabPane.setEnabledAt(1, enableOrDisable);
		tabPane.setEnabledAt(2, enableOrDisable);

		for (Component c : components) {

			c.setEnabled(enableOrDisable);
		}
	}

}
