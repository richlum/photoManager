package ca.ubc.cs.cpsc211.photo.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Set;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import ca.ubc.cs.cpsc211.photo.Album;
import ca.ubc.cs.cpsc211.photo.Photo;
import ca.ubc.cs.cpsc211.photo.PhotoManager;
/**
 * AlbumList is the data representation for the Album information panel.
 * Tightly linked to MainWindow to cross update to Tag list and thumb nail
 * pane.
 * @author rlum
 *
 */
public class AlbumList extends JList{
	
	private static final long serialVersionUID = -1828931219166917345L;
	private MainWindow mainWindow;
	private PhotoManager photomanager; 
	private DefaultListModel listModel;
	JPopupMenu popup;  // to implement popup menu (typically right mouse click
	JMenuItem menuItem; 
	Object currentSelection;  // holds what the current list selection is.
	AlbumList self; // pointer to albumlist to hand to other objects
	
	/**
	 * make the album list 
	 * Heavily based on Example JLists provided in CS211
	 * @param mw mainwindow pointer to get access to other panels
	 * @param pm photomanager to get phot information
	 * @param m for Jlist element management
	 */
	AlbumList(MainWindow mw,PhotoManager pm, DefaultListModel m){
		super(m);   
		this.mainWindow = mw; // for access to parent window and
		this.photomanager = pm;  // to get other albums
		this.listModel = m; // to manage the list of albums
		this.self = this; // to allow event listeners access to private member of this class
		
		// get the list of albums in PhotoManager and put them into an AlbumList for display
		Set<Album> albums = pm.getAlbums();
		for (Album album : albums){
			m.addElement(album);
		}
		// add one default entry to contain all photos in the library
//		Set<Photo> allPhotos = pm.getPhotos();
		m.addElement("allPhotos");
		
		// implement list selection methods
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		// selecting album updates thumbnails to show all photos from selected album
		addListSelectionListener(new ListSelectionListener(){
			@Override
			public void valueChanged(ListSelectionEvent e) {
				mainWindow.clearTagSelection();  
				// only allow either album panel or tag panel to have a selection active
				// makes it clearer that thumbnail is showing either photos in an album or photos in a tag
				
				//what was selected
				JList source = (JList) e.getSource();
				if (source.isSelectionEmpty())
					return;
				int row = source.getSelectedIndex();
				DefaultListModel m = (DefaultListModel) source.getModel();
				System.out.println(
						"source name= " + source.getName() + 
						"element at row " + row +
						" is " + m.getElementAt(row) +
						" which is an object of type " + m.getElementAt(row).getClass()
						);
				
				// if an album selected get the photos from that album
				Set <Photo> selectedPhotos;
				if (m.getElementAt(row).getClass().equals(Album.class)){
					Album selectedAlbum = (Album) m.getElementAt(row);
					selectedPhotos = selectedAlbum.getPhotos();
					currentSelection = selectedAlbum;
					String result ="";
					result += "AlbumName = " + selectedAlbum.getName() +"\n";
					result +=  "Contains " + selectedAlbum.getPhotos().size() + "Photos";
					mainWindow.getUpdateField().setText(result);
				}else{
					selectedPhotos = photomanager.getPhotos();
					System.out.println ("allphotos size = " + selectedPhotos.size());
					currentSelection = selectedPhotos;
				}
				// update thumbPane to show the albums within the selected album
				mainWindow.setThumbs(selectedPhotos);
				System.out.println("selected item : " + row);
			}
		});
		
		/**
		 * implement mouse listeners for the album pane to allow popup menu actions
		 * Heavily based on Popup men samples provided in cS211
		 */
		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e)
			{
				if (e.isPopupTrigger())
				{
					popup.show(e.getComponent(), e.getX(), e.getY());
				}
			}

			public void mouseReleased(MouseEvent e)
			{
				if (e.isPopupTrigger())
				{
					popup.show(e.getComponent(), e.getX(), e.getY());
				}
			}
		});
		
		//set the popup menus
		popup = new JPopupMenu();
		popup.setBorderPainted(true);
		
		// allow creation of new albums
		menuItem = new JMenuItem("New Album");
		// implement the actionlistner for new album
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				String inputValue = JOptionPane.showInputDialog("Please input a new album name");
				if (photomanager.findAlbum(inputValue)!=null){
					JOptionPane.showMessageDialog(mainWindow.getFrame(), "Album Already Exists", "alert", JOptionPane.ERROR_MESSAGE);;
				}else{
					Album album = new Album(inputValue);
					photomanager.addAlbum(album);
					listModel.addElement(album);
				}				
			}
		});
		popup.add(menuItem);
		
		// add a remove Album menu item
		menuItem = new JMenuItem("Remove");
		// action listener for removing album
		// #TODO - Im pretty sure that the underlying code does not handle the implication of
		// updating tag information if we remove an album with photos in it.  Check
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{	
				String question = "Are you Sure you want to irreversibly delete this album and all the photos it contains?";

				if ((currentSelection!= null)&&(currentSelection.getClass().equals(Album.class))){
					int selected = JOptionPane.showConfirmDialog(mainWindow.getFrame(), question, "Confirm " ,JOptionPane.YES_NO_OPTION);
					if (selected ==0)	{
						photomanager.removeAlbum((Album) currentSelection);
						listModel.remove(self.getSelectedIndex());
						System.out.println("removed selected");
					}
				}else{
					JOptionPane.showMessageDialog(mainWindow.getFrame(), "Select an Album First", "alert", JOptionPane.ERROR_MESSAGE);;
				}
			}
		});
		popup.add(menuItem);
		
		// rename an existing album
		menuItem = new JMenuItem("Rename Album");
		// actionlistener for removing album
		// #TODO - Im pretty sure that the underlying code does not handle the implication of
		// updating tag information if we remove an album with photos in it.  Check
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)			{	
				if ((currentSelection!= null)&&(currentSelection.getClass().equals(Album.class))){
					String inputValue = JOptionPane.showInputDialog("Please input a new album name");
					if (photomanager.findAlbum(inputValue)!=null){
						JOptionPane.showMessageDialog(mainWindow.getFrame(), "Album Already Exists", "alert", JOptionPane.ERROR_MESSAGE);;
					}else{
						Album oldAlbum = (Album)currentSelection;
						photomanager.renameAlbum(oldAlbum.getName(),inputValue);
						refreshAlbumList();
					}				
				}else{
					JOptionPane.showMessageDialog(mainWindow.getFrame(), "Select an Album First", "alert", JOptionPane.ERROR_MESSAGE);;
				}
			}
		});
		popup.add(menuItem);


		
		menuItem = new JMenuItem("Add Photos to Album");
		// action listener for adding more photos from files
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{	
				
				if ((currentSelection!= null)&&(currentSelection.getClass().equals(Album.class))){
//					mainWindow.loadNewPhotos();
					PhotoLoader pl = new PhotoLoader(mainWindow);
					pl.start();
				}else{
					JOptionPane.showMessageDialog(mainWindow.getFrame(), "Select an Album First", "alert", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		popup.add(menuItem);
		
		// redundant access to album info that is shown in information pane when a specific album selected
		menuItem = new JMenuItem("Show");
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{		
				String result = "";
				if ((currentSelection!= null)&&(currentSelection.getClass().equals(Album.class))){
					Album currSelect = (Album)currentSelection;
					result += currSelect.getName() + "\n";
					result += currSelect.getPhotos().size() + " Photos\n";
					
				}else{
					result += photomanager.getPhotos().size() + "Total Photos in Library";
				}				JOptionPane.showMessageDialog(null, result);
			}
		});
		popup.add(menuItem);
	}
	
	/**
	 * get the current album that user selected
	 * @return album that user selected
	 */
	public Album getCurrentSelection(){
		if ((currentSelection != null)&&(currentSelection.getClass().equals(Album.class))){
			return (Album) currentSelection;
		}else{
			return null;
		}
		
	}
	
	/**
	 * rebuild the list from photomanager and update displayed list
	 */
	public void refreshAlbumList(){
		listModel.clear();
		Set<Album> albums = photomanager.getAlbums();
		for (Album album : albums){
			listModel.addElement(album);
		}
		// add one default entry to contain all photos in the library

		listModel.addElement("allPhotos");
	}
	
}
