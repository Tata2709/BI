package BI.validation;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SaveErrorLogActionListener implements ActionListener {
	
	ValidationController controller;
	
	public SaveErrorLogActionListener(ValidationController vController) {
		controller = vController;
	}
	
	public void actionPerformed(ActionEvent e){
		controller.saveErrorLog();
	}

}
