package ca.ubc.cs.cpsc211.photo.gui;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.UIManager.LookAndFeelInfo;


/** 
 * build a jpanel containing a drop down menu with all the installed
 * look and feels on this machine.  selecting drop down menu will 
 * close current main window (not others) and rebuild a new one 
 * under the new look and feel.
 * @author rlum
 *
 */
public class LookAndFeelChooser extends JPanel implements ActionListener {

	private MainWindow mainWindow;

	/**
	 * builds a new panel with the combo box chooser populated with the list of 
	 * looks installed inthis machine.  
	 * @param looks - array of UIManager.LookAndFeels obtained by UIManager.getInstalledLookAndFeels
	 * @param mainWindow - pointer to mainwindow so we can ask it to close and build a new one
	 */
	public  LookAndFeelChooser (LookAndFeelInfo  [] looks , MainWindow mainWindow){
		super();

		this.mainWindow = mainWindow;
		this.setLayout(new FlowLayout());
		String [] lookStrings = new String [looks.length];
		for (int i = 0 ; i< looks.length ; i++){
			lookStrings[i] = looks[i].getClassName();
			System.out.println("look " + i + " = " + lookStrings[i]);
		}
		JComboBox lookPick = new JComboBox(lookStrings);

		lookPick.addActionListener(this);
		this.add(lookPick);
		this.setBorder(
				BorderFactory.createCompoundBorder(
						BorderFactory.createTitledBorder("Will Destroy Current MainWindow and rebuild"),
						BorderFactory.createEmptyBorder(5,5,5,5)));
	}
	
	/**
	 * responder to user menu selections
	 */
	@Override
	public void actionPerformed(ActionEvent evt) {

		JComboBox cb = (JComboBox)evt.getSource();
		String look = (String) cb.getSelectedItem();
        System.out.println("picked =  " + look);
        try {
			UIManager.setLookAndFeel(look);
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		mainWindow.closeWindow();
        mainWindow.drawMainWindow();
	}

}
