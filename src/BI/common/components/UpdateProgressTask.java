package BI.common.components;

import javax.swing.JLabel;

public class UpdateProgressTask  implements Runnable {
	JLabel progress;
	public UpdateProgressTask(JLabel progress){
		this.progress = progress;
	}
	@Override
	public void run() {
		System.out.println("new update task " + progress);
		progress.setVisible(true);
	}
	
}
