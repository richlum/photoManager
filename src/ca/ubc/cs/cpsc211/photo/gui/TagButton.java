package ca.ubc.cs.cpsc211.photo.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import ca.ubc.cs.cpsc211.photo.Tag;

public class TagButton extends JButton {
	/**
	 * TagButton is primarly used as the basis for TagCloud display of Tags as 
	 * clickable buttons that will update the main window thumbnail pane
	 */
	private static final long serialVersionUID = 1L;
	private Tag myTag;
	private MainWindow mainWindow;
	
	TagButton( MainWindow mw, Tag myTag){
		super(myTag.getName());
		this.myTag=myTag;
		this.mainWindow = mw;
		this.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				TagButton tagbutton = (TagButton) e.getSource();
				mainWindow.setThumbs(tagbutton.getTag().getPhotos());
			}
			
		});

	}
	
	Tag getTag(){
		return myTag;
	}
}
