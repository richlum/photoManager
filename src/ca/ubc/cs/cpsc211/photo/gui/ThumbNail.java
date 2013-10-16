package ca.ubc.cs.cpsc211.photo.gui;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.border.Border;

import ca.ubc.cs.cpsc211.photo.Photo;
import ca.ubc.cs.cpsc211.photo.Tag;
import ca.ubc.cs.cpsc211.utility.ThumbnailDoesNotExistException;
/**
 * A utility class to hold a set of thumbnail images in a Jlable for display.  
 * Tightly linked to MainWindow and requires a pointer to MainWindow in order to 
 * respond to mouseclick on a specific thumbnail and call MainWindow to insert
 * a large version of the image into the mainwindow center pane.  Also require
 * access to MainWindow to update Information Pane to update with the thumbnail
 * specific metadata when user mouse hovers over thumbnail.
 * @author rlum
 *
 */
public class ThumbNail extends JLabel implements MouseListener  {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Photo photo;  // for sending to center pane
	private JTextArea updateField; // for updating information pane data with this thumbnail data
	private MainWindow main;
	private Border loweredbevel;
	
	ThumbNail(Photo photo, MainWindow main) throws ThumbnailDoesNotExistException{
		super(new ImageIcon(photo.getThumbnailImage()));
		this.photo = photo; 
		this.main = main; // for access to MainWindow (to get access to other panes)
 		this.updateField = main.getUpdateField();  // the information pane
		this.addMouseListener(this); // to receive clicks and mouseEnters
		this.loweredbevel = BorderFactory.createLoweredBevelBorder(); // for user feedback on hover
	}
	
	/**
	 * get the photo associated with this thumbnail
	 * @return photo that is associated with this thumbnail
	 */
	public Photo getPhoto(){
		return photo;
	}

	/**
	 * mouse listener methods for click response and hovering to show photo information
	 * in information panel
	 */
	@Override
	public void mouseClicked(MouseEvent arg0) {
		
	}

	/**
	 * method to provide meta data associated with this thumbnail/photo
	 * in the information pane when hovering over thumbnail
	 */
	@Override
	public void mouseEntered(MouseEvent arg0) {
		this.setBorder(loweredbevel);
		Photo photo = this.getPhoto();
		String result = "Photo = " + photo +"\n";
		result += "Album = " + photo.getAlbum() + "\n";
		result += "Date Added = " + photo.getDateAdded() + "\n";
		result += "Description = " + photo.getDescription() + "\n";
		Set<Tag> tags = photo.getTags();
		result += "Tags for this Photo: \n " ;
		for (Tag tag : tags){
			result += "\t"  + tag  + "\n";
		}
		
		updateField.setText(result);
		updateField.setVisible(true);

	}

	/**
	 * erase information pane when mouse leaves the thumbnail to prevent confusion to the user.
	 * Allows other album, photo or center pane to use the center pane.
	 * Philosophy is to follow the mouse location and provide feedback to user base on where the 
	 * mouse currently is.
	 */
	@Override
	public void mouseExited(MouseEvent arg0) {

		updateField.setText(null);
		this.setBorder(null);
	}


	@Override
	public void mousePressed(MouseEvent arg0) {
		
	}


	/**
	 * on mouseUp put large version of photo in the center pane
	 */
	@Override
	public void mouseReleased(MouseEvent me) {
		System.out.println(me.getSource().getClass().toString());
		if (me.getSource().getClass().equals(this.getClass())){
			ThumbNail source = (ThumbNail) me.getSource();
			main.getCenterLabel().setImage(source.getPhoto());
			main.getCenterLabel().setVisible(true);
		}
	}

}
