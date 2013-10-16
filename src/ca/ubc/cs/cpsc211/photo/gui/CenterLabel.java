package ca.ubc.cs.cpsc211.photo.gui;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import ca.ubc.cs.cpsc211.photo.Photo;
import ca.ubc.cs.cpsc211.photo.PhotoDoesNotExistException;

/**
 * larger panel to show full size images.  Also contains mouselistener to activate edit panel
 * @author rlum
 *
 */
public class CenterLabel extends JLabel implements MouseListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Photo photo;
	private MainWindow mainWindow;
	public CenterLabel(MainWindow mw, Photo photo){
		super();
		this.photo = photo;
		this.mainWindow=mw;
		addMouseListener(this);
		this.setToolTipText("Click on This to bring up Edit Menu");

	}

	/**
	 * set the image for the center panel
	 * @param photo
	 */
	public void setImage(Photo photo){
		try {
			super.setIcon(new ImageIcon(photo.getImage()));
			this.photo = photo;
		} catch (PhotoDoesNotExistException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Get the photo being displayed
	 * @return photo that is being displayed
	 */
	public Photo getPhoto(){
		return this.photo;
	}

	/**
	 * mouse listener methods to create edit menu when user clicks on photo
	 */
	@Override
	public void mouseClicked(MouseEvent me) {
		System.out.println("ceterLabel received mouse click event");
		EditPanel theEditPanel = mainWindow.getEditPanel();
		if (this.getPhoto()!=null)
			if (theEditPanel==null){
				theEditPanel = new EditPanel(mainWindow, getPhoto(), mainWindow.getPhotoManager(), mainWindow.getTagManager());
			}else{
				theEditPanel.setPhoto(this.getPhoto());
			}
		
	}

	
	@Override
	public void mouseEntered(MouseEvent arg0) {
//		System.out.println("centerLabel mouse Entered");
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
//		System.out.println("centerlabel mouse exited");
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		
	}
	

	
	
}
