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

import ca.ubc.cs.cpsc211.photo.DuplicateTagException;
import ca.ubc.cs.cpsc211.photo.Photo;
import ca.ubc.cs.cpsc211.photo.Tag;
import ca.ubc.cs.cpsc211.photo.TagManager;

public class TagList extends JList {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private MainWindow mainWindow;
	private TagManager tagmanager;
	private DefaultListModel listModel;
	JPopupMenu popup;
	JMenuItem menuItem;
	Object currentSelection;
	TagList self;
	/**
	 * the taglist that contains the list of all tags currently in the library for 
	 * display in the taglist panel.  parallel structure to AlbumList
	 * Heavily based on cs211 sample code for jlist implementation
	 * @param mw pointer to mainwindow for access to updating other components when this component is master
	 * @param tm tagManager to get and update tags based on user input
	 * @param m the defaultlistModel to manage user input interaction with list
	 */
	TagList(MainWindow mw,TagManager tm, DefaultListModel m){
		super(m);
		this.mainWindow = mw;
		this.listModel = m;
		this.self = this;
		this.tagmanager = tm;
		
		Set<Tag> tags = tm.getTags();
		for (Tag tag : tags){
			m.addElement(tag);
		}

		
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		addListSelectionListener(new ListSelectionListener(){

			/**
			 * update all panels based on  the selection of tag list entry
			 */
			@Override
			public void valueChanged(ListSelectionEvent e) {
				mainWindow.clearAlbumSelection();
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
						//"source component " + source.getComponent(source.getSelectedIndex())
						);
				Set <Photo> selectedPhotos;
				if (m.getElementAt(row).getClass().equals(Tag.class)){
					Tag selectedTag = (Tag) m.getElementAt(row);
					selectedPhotos = selectedTag.getPhotos();
					currentSelection = selectedTag;
					String result = "";
					result += "TagName = " + selectedTag.getName() +"\n";
					result +=  "Contains " + selectedTag.getPhotos().size() + "Photos";
					mainWindow.getUpdateField().setText(result);
				}else{
					selectedPhotos = null;
					currentSelection = selectedPhotos;
				}
				mainWindow.setThumbs(selectedPhotos);
				System.out.println("selected item : " + row);				
			}
			
		});
		
		/**
		 *  mouse listener to provide pop up menu in taglist
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
		//	lsp = new JScrollPane(list);
		//	getContentPane().add(lsp, BorderLayout.CENTER);

		popup = new JPopupMenu();
		popup.setBorderPainted(true);
		
		// pop up menu item to add new tags
		menuItem = new JMenuItem("New Tag");
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				String inputValue = JOptionPane.showInputDialog("Please input a new tag name");
				if (tagmanager.findTag(inputValue)!=null){
					JOptionPane.showMessageDialog(mainWindow.getFrame(), "TagName Already Exists", "alert", JOptionPane.ERROR_MESSAGE);;
				}else{
					try {
						tagmanager.createTag(inputValue);
					} catch (DuplicateTagException e1) {
						e1.printStackTrace();
					}
					listModel.addElement(tagmanager.findTag(inputValue));
				}
			
			}
		});
		popup.add(menuItem);
		
		// Popup menu remove tag method
		// #TODO the underlying code does not handle tags with multiple associate photos. It loops
		// between photo remove tag and tag remove photo throwing a concurrent modification exception.
		menuItem = new JMenuItem("Remove");
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{	
				String question = "Are you Sure you want to irreversibly delete this tag?";

				if ((currentSelection!= null)&&(currentSelection.getClass().equals(Tag.class))){
					int selected = JOptionPane.showConfirmDialog(mainWindow.getFrame(), question, "Confirm " ,JOptionPane.YES_NO_OPTION);
					if (selected ==0)	{
						tagmanager.removeTag(((Tag)currentSelection).getName());
						listModel.remove(self.getSelectedIndex());
						System.out.println("removed selected");
					}
				}
			}
		});
		popup.add(menuItem);
		
		
		menuItem = new JMenuItem("Rename Tag");
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				if ((currentSelection!= null)&&(currentSelection.getClass().equals(Tag.class))){

					String inputValue = JOptionPane.showInputDialog("Please input a new tag name");
					if (tagmanager.findTag(inputValue)!=null){
						JOptionPane.showMessageDialog(mainWindow.getFrame(), "TagName Already Exists", "alert", JOptionPane.ERROR_MESSAGE);;
					}else{
						try {
							Tag oldTag = (Tag) currentSelection;
							tagmanager.renameTag(oldTag.getName(), inputValue);
							refreshTagList();
//							tagmanager.createTag(inputValue);
						} catch (DuplicateTagException e1) {
							e1.printStackTrace();
						}
//						listModel.addElement(tagmanager.findTag(inputValue));
					}
				}else{
					JOptionPane.showMessageDialog(mainWindow.getFrame(), "Select a Tag First", "alert", JOptionPane.ERROR_MESSAGE);;

				}

			}
		});
		popup.add(menuItem);

		
		
		menuItem = new JMenuItem("Show");
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{		
				String result = "";
				if ((currentSelection!= null)&&(currentSelection.getClass().equals(Tag.class))){
					Tag currSelect = (Tag)currentSelection;
					result += currSelect.getName() + "\n";
					result += currSelect.getPhotos().size() + " Photos\n";
				}
				JOptionPane.showMessageDialog(null, result);
			}
		});
		popup.add(menuItem);
	}
	
	public void refreshTagList(){
		Set<Tag> tags = tagmanager.getTags();
		listModel.clear();
		for (Tag tag : tags){
			listModel.addElement(tag);
		}
	}
	
}



