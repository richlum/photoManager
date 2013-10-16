package ca.ubc.cs.cpsc211.photo.gui;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

public class PhotoProgress  {

	JProgressBar progressBar;
	int progress =0;
	JLabel status;
	/**
	 * create the display for the progress bar.  
	 * @param progressBar
	 */
	public PhotoProgress(JProgressBar progressBar){
		this.progressBar = progressBar;
		
		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension screenSz = tk.getScreenSize();
		
		
		progressBar.setValue(0);
		progressBar.setStringPainted(true);
		
		JFrame pFrame = new JFrame("Adding photos");
		status = new JLabel ("Attempting to Add files: ");
		
		JPanel pPanel = new JPanel();
		pPanel.add(progressBar);
		pPanel.add(status);
		pPanel.setLayout(new GridLayout(0,1));
		pPanel.setBorder(
				BorderFactory.createCompoundBorder(
						BorderFactory.createTitledBorder("Adding new photos"),
						BorderFactory.createEmptyBorder(5,5,5,5)));
		
		Container cp =  pFrame.getContentPane();
		cp.add(pPanel);
		pFrame.pack();
		pFrame.setLocation(screenSz.width/2 -10 ,screenSz.height/3 );
		pFrame.setVisible(true);
	}
	
	/**
	 * allow executing progress update to also send us strings to show user during
	 * updates.
	 * @param str
	 */
	public void setLabel(String str){
		this.status.setText(str);
	}


}
