package BI.start;
import javax.swing.SwingUtilities;


public class StartBIReport {
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable(){
			public void run() {
				new MainFrame();
			}	
		});
	}
}
