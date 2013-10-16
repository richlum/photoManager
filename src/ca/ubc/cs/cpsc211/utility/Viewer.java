package ca.ubc.cs.cpsc211.utility;

import java.awt.*;
import javax.swing.*;

/**
 * Simple viewer to help test code.
 * 
 * <p>To use this class, create an instance, add images and then call
 * display. A window will pop up with the images. If you exit this window,
 * the window will be disposed but the application will not exit. It is
 * a good idea to also call "close" on the viewer.
 * 
 * @author CPSC 211 Instructor
 */

public class Viewer extends JFrame {

	// We use a panel to hold all of the thumbnails we want to view.
	private JPanel thumbnailPanel;

	/** 
	 * Constructor
	 * @pre name != null
	 * @post New instance of Viewer created
	 * @param name The name of the window 
	 */
	public Viewer( String name ) {
		// Since Viewer is a subclass of JFrame, the Viewer is a window
		// and the name will be given to the window
		super( name );

		// Close the window but don't exit the application if the window
		// close operation is called
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		// The pane is inside the Window
		Container content = getContentPane();
		content.setBackground(Color.white);
		content.setLayout(new GridLayout());

		// Create a panel for the thumbnails that we can make scrollable below 
		thumbnailPanel = new JPanel();
		thumbnailPanel.setLayout(new GridLayout(0, 5));

	}

	/**
	 * Add an image into the viewer
	 * 
	 * @pre image != null
	 * @post Image is ready to be displayed but not yet visible 
	 * @param image Image to display
	 */
	public void addImage(Image image) {
		thumbnailPanel.add(new JLabel(new ImageIcon(image)));
	}

	/**
	 * Display all added images
	 * 
	 * @pre true
	 * @post Window is displayed
	 */
	public void display() {
		JScrollPane scrollPane = new JScrollPane(thumbnailPanel,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		getContentPane().add(scrollPane);
		pack();
		setVisible(true);
	}

	/**
	 * Close the window.
	 * 
	 * @pre Assumes that display has been called
	 * @post Window is no longer visible
	 */
	public void close() {
		dispose();
		setVisible(false);
	}

	/**
	 * You do NOT have to worry about this serialVersionUID
	 * 211 students please ignore :) 
	 */
	private static final long serialVersionUID = -1308515978449923986L;	

}
