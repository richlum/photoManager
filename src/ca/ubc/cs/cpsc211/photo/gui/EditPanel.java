package ca.ubc.cs.cpsc211.photo.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import ca.ubc.cs.cpsc211.photo.Album;
import ca.ubc.cs.cpsc211.photo.DuplicateTagException;
import ca.ubc.cs.cpsc211.photo.Photo;
import ca.ubc.cs.cpsc211.photo.PhotoAlreadyInAlbumException;
import ca.ubc.cs.cpsc211.photo.PhotoDoesNotExistException;
import ca.ubc.cs.cpsc211.photo.PhotoManager;
import ca.ubc.cs.cpsc211.photo.Tag;
import ca.ubc.cs.cpsc211.photo.TagManager;
import ca.ubc.cs.cpsc211.utility.ThumbnailDoesNotExistException;

/**
 * EditPanel contains a number of components, each geared towards aiding user in 
 * adding,changing,deleting some aspect of the photo
 * @author rlum
 *
 */
public class EditPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4989458565932548617L;
	private MainWindow mainWindow;
	private Photo currPhoto;
	private PhotoManager photomanager;
	private TagManager tagmanager;
	private EditPanel editPanel;
	private Set<Photo> thumbNailSet;
	private List<Photo> thumbNailList;
	private JTextArea description;
	private JPanel currentTagsPanel;
	private JScrollPane currentTagsScrollPane;
	private Album trash;
	private CustomTextArea customTagEntryField;
	private JButton prevB ;
	private JButton nextB ;
	private JButton delB ;
	/**
	 * EditPanel contains a number of components to allow user to modify photo information
	 * @param mw main window pointer to refresh other panels as required
	 * @param photo  is the item being edited
	 * @param pm photomanager for access to photo library information
	 * @param tm tagmanager for access to tag information.
	 */
	public EditPanel(MainWindow mw, Photo photo, PhotoManager pm, TagManager tm){
		//		super(mw.getEditPanel());

		this.editPanel = mw.getEditPanel();
		this.mainWindow = mw;
		this.currPhoto=photo;
		this.photomanager=pm;
		this.tagmanager=tm;
		this.trash = new Album("Trash");
		
		// give mainWindow a pointer to this editpanel	
		mainWindow.setEditPanel(this);
		this.editPanel = this;
		mainWindow.getEditPanel().setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		makeDisplay(photo);  // make various subcompnents and place it in editpanel

		mainWindow.getEditScrollPane().setViewportView(editPanel);
		mainWindow.getEditScrollPane().setVisible(true);
		if(mainWindow.getEditFrame() == null){
			mainWindow.setEditFrame(new JFrame("Editing "+photo.getName() + " info"));
			mainWindow.getEditFrame().setLocation(mainWindow.getFrame().getWidth() - 120, mainWindow.getFrame().getY()+50);
		}
		mainWindow.getEditFrame().add(editPanel);
		mainWindow.getEditFrame().pack();
		mainWindow.getEditFrame().setVisible(true);
	}




	/**
	 * add all the subpanels for the editpanel based on a specific photo
	 * @param photo
	 */
	public void makeDisplay(Photo photo){
		// put list of photos in thumbnail panel into local 
		thumbNailList = new ArrayList<Photo>();
		this.thumbNailSet = mainWindow.getThumbPhotoSet();
		for (Photo thumbPhoto: thumbNailSet){
			thumbNailList.add(thumbPhoto);
		}

		JPanel albumchangerPanel = makeAlbumSelector();
		JPanel photoFrame = makePhotoFrame();
		JPanel descriptionPanel = makePhotoDescriptionPanel ();
		JPanel tagEntryPanel = makeTagEntryPanel();
		currentTagsPanel = makeCurrentTagPanel();
		currentTagsScrollPane = new JScrollPane(currentTagsPanel);
		
		editPanel.add(photoFrame);
		editPanel.add(albumchangerPanel);
		editPanel.add(new JScrollPane(descriptionPanel));
		editPanel.add(tagEntryPanel);		
		editPanel.add(currentTagsScrollPane);
		editPanel.setVisible(true);
	}// main method to build all components for the edit pane
	
	
/**
 * Create panel containing all the tags associated with the current photo.
 * Each tag will have a button to allow deletion of existing tags.
 * @return jpanel containing delete buttons for tags currently associated with photo
 */
	private JPanel makeCurrentTagPanel() {	
		// create a panel containing buttons for all currently assigned tags
		// button action is to remove tag from this photo
		currentTagsPanel = new JPanel();
		currentTagsPanel.setBorder(
				BorderFactory.createCompoundBorder(
						BorderFactory.createTitledBorder("Tag Deleter"),
						BorderFactory.createEmptyBorder(5,5,5,5)));
		currentTagsPanel.setLayout(new GridLayout(0,1));
		currentTagsPanel.setToolTipText("Buttons represent currently assigned Tags that will be deleted on Button Press");
		Set<Tag> tags = currPhoto.getTags();
		for (Tag tag : tags){
			JButton tagButton = new JButton(tag.getName());
			tagButton.addActionListener(new ActionListener(){

				@Override
				public void actionPerformed(ActionEvent e) {
					JButton theButton = (JButton)e.getSource();
					Tag tag = tagmanager.findTag(theButton.getText());
					if (tag != null){
						currPhoto.removeTag(tag);
						currentTagsPanel.remove(theButton);
						currentTagsPanel.doLayout();
						currentTagsScrollPane.validate();
						currentTagsScrollPane.repaint();
						editPanel.doLayout();

					}

				}

			});
			currentTagsPanel.add(tagButton);		
		}	
		return currentTagsPanel;
	}// end of building delete current tags panel of edit pane



/**
 * Create a Panel with a textfield entry for tag entry
 * Actionlistner will create new tags as necessary or 
 * assign existing tags 
 * @return jpanel containg tag entry 
 */
	private JPanel makeTagEntryPanel() {
		JPanel tagEntryPanel = new JPanel();
		tagEntryPanel.setBorder(
				BorderFactory.createCompoundBorder(
						BorderFactory.createTitledBorder("Add New Tags"),
						BorderFactory.createEmptyBorder(5,5,5,5)));

		JTextField newTagEntryField = new JTextField();
		
		newTagEntryField.setToolTipText("Rapid Tag entry will add existing tag to photo or  automatically create new tags as required.");
		newTagEntryField.addActionListener(new ActionListener(){
			/**
			 * rapid tagging mode.  accept whatever text input is given.
			 * If it matches a tag, assign it.  If it doesn't exist, create the
			 * tag and assign it.
			 */
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println(e.getActionCommand());
				boolean addedTag = false;
				JTextField source = (JTextField)e.getSource();
				String input = source.getText();
				if (!(input == null)&&!(input.equals(""))){
					System.out.println(input);
					Tag tagLookup = tagmanager.findTag(input);
					if (tagLookup == null){
						try {
							tagmanager.createTag(input);
							tagLookup = tagmanager.findTag(input);
							currPhoto.addTag(tagLookup);
							mainWindow.refreshTagPane();
							addedTag=true;
						} catch (DuplicateTagException e1) {
							e1.printStackTrace();
						}

					}else {
						if(!currPhoto.getTags().contains(tagLookup)){
							currPhoto.addTag(tagLookup);
							addedTag=true;
						}
						// only add tag if it doesnt already exist in this photo.
					}
					if (addedTag){
						JButton tagButton = new JButton(tagLookup.getName());
						tagButton.addActionListener(new ActionListener(){
							@Override
							public void actionPerformed(ActionEvent e) {
								JButton theButton = (JButton)e.getSource();
								Tag tag = tagmanager.findTag(theButton.getText());
								if (tag != null){
									currPhoto.removeTag(tag);
									currentTagsPanel.remove(theButton);
								}

							}

						});

						// resize calculation on EditFrame required to take into account added button
						// in currentTagsPanel jpanel, otherwise buttons squish
						//					mainWindow.getEditFrame().getContentPane().validate();  
						currentTagsPanel.add(tagButton);  
						currentTagsScrollPane.validate();
						currentTagsScrollPane.doLayout();
					}
					source.setText(null);
				}
			}

		});

		newTagEntryField.setPreferredSize(new Dimension(60,20));
		
		// custom textarea with autocomplete capability. 
		// requires use of commit button for batch processing 
		List<String> words = new ArrayList<String>();
		Set<Tag> tags = tagmanager.getTags();
		for (Tag tag :tags){
			words.add(tag.getName());
		}
		Collections.sort(words);
		customTagEntryField = new CustomTextArea(words);
		JScrollPane cTagEntrySP = new JScrollPane(customTagEntryField);
		
		// the commit button do to batch processing on user input
		JButton commitBulkTags = new JButton("Commit Tags");
		commitBulkTags.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				JTextArea bulkTags = customTagEntryField.getTextArea();
				String input = bulkTags.getText();
				System.out.println("input = " + input);
				customTagEntryField.getTextArea().setText("");
				try {
					processTags(input);
					currentTagsPanel=makeCurrentTagPanel();
					currentTagsScrollPane.setViewportView(currentTagsPanel);
					currentTagsScrollPane.validate();
					currentTagsScrollPane.doLayout();
				} catch (DuplicateTagException e) {
					e.printStackTrace();
				}
			}
			
			/**
			 * the loop to actually scan the user input and build and associate
			 * tags tot he current photo
			 * @param input
			 * @throws DuplicateTagException
			 */
			private void processTags(String input) throws DuplicateTagException {
				Scanner sc = new Scanner(input);
				while (sc.hasNext()){
					String taglet = sc.next();
					Tag tag = tagmanager.findTag(taglet);
					if (tag == null){
						Tag newTag = tagmanager.createTag(taglet);
						currPhoto.addTag(newTag);
					}else{
						currPhoto.addTag(tag);
					}
				}
				
				mainWindow.refreshTagPane();
			}
			
		});
		commitBulkTags.setToolTipText("Commit new tags to this photo");
		commitBulkTags.setVisible(true);
		
		// panel for bulk input
		JPanel bulkTagPanel = new JPanel();
		bulkTagPanel.add(cTagEntrySP);
		bulkTagPanel.add(commitBulkTags);
		bulkTagPanel.setLayout(new BoxLayout(bulkTagPanel, BoxLayout.Y_AXIS));

		
		// put one tag input method in each tab.
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.addTab("RapidTagger",newTagEntryField);
		tabbedPane.addTab("TagAssist",bulkTagPanel);
		tagEntryPanel.add(tabbedPane);
		tabbedPane.setToolTipTextAt(0, "Accepts all input and autogenerates new tags as necessary");
		tabbedPane.setToolTipTextAt(1, "Assists with autocomplete for building bulk Tag entries");
		
		return tagEntryPanel;
	}// end of building tag Entry Panel portion of edit panel



	/**
	 * create a panel that contains a JTextArea populated with the current
	 * description of the photo.  attach buttons to allow updating of 
	 * description or retrieval of current description in case user changes mind.
	 * @return
	 */
	private JPanel makePhotoDescriptionPanel() {
		JPanel descriptionPanel = new JPanel();
		descriptionPanel.setBorder(
				BorderFactory.createCompoundBorder(
						BorderFactory.createTitledBorder("Edit Description"),
						BorderFactory.createEmptyBorder(5,5,5,5)));
		// the description text field
		description = new JTextArea( currPhoto.getDescription(),5,1);
		description.setWrapStyleWord(true);
		description.setLineWrap(true);
//		description.setPreferredSize(new Dimension(etPreferredSize()));
		descriptionPanel.setLayout(new BoxLayout(descriptionPanel, BoxLayout.Y_AXIS));
		description.setVisible(true);
		
		// button to commit user entry description
		JButton updateDesc = new JButton ("Update");
		updateDesc.setToolTipText("Will replace Description with your Text");
		updateDesc.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				currPhoto.setDescription(description.getText());
				description.setText("");
			}
		});
		
		// button to refresh text field with current description
		JButton getDesc = new JButton ("Current");
		getDesc.setToolTipText("Places copy of current Description in Text Window");
		getDesc.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				description.setText(currPhoto.getDescription());
			}
		});
		
		// add the buttons
		JPanel descButtonPanel = new JPanel();
		descButtonPanel.setLayout(new GridLayout(1,0));
		descButtonPanel.add(updateDesc);
		descButtonPanel.add(getDesc);
		descriptionPanel.add(descButtonPanel);
		descriptionPanel.add(description);
		
		return descriptionPanel;

	}



	/**
	 * make the thumbnail with name and date info
	 * @return panel containing name, thumbnail and date for the photo being edited
	 */
	private JPanel makePhotoFrame() {
		JLabel photoname = new JLabel(currPhoto.getName());
		JPanel photoFrame = new JPanel();
		photoFrame.setBorder(
				BorderFactory.createCompoundBorder(
						BorderFactory.createTitledBorder("Editing Photo"),
						BorderFactory.createEmptyBorder(5,5,5,5)));

		photoFrame.setLayout(new BorderLayout());
		photoname.setHorizontalAlignment(SwingConstants.CENTER);
		photoname.setToolTipText("The name of the photo taken from filename");
		photoFrame.add(photoname,BorderLayout.NORTH);

		// add photoFrame to editPanel
		try {
			ThumbNail thumbNail = new ThumbNail(currPhoto,mainWindow);
			photoFrame.add(thumbNail,BorderLayout.CENTER);

		} catch (ThumbnailDoesNotExistException e) {
			e.printStackTrace();
		}
		JLabel dateLabel = new JLabel("No Date Set");

		dateLabel.setHorizontalAlignment(SwingConstants.CENTER);
		if ((currPhoto!=null)&&(currPhoto.getDateAdded()!=null)){
			dateLabel.setText(( currPhoto.getDateAdded().toString()));
			dateLabel.setHorizontalAlignment(SwingConstants.CENTER);
			dateLabel.setToolTipText("Date that the Photo was loaded into Library");
		}
		photoFrame.add(dateLabel,BorderLayout.SOUTH	); 
		
		// add prev next buttons
		prevB = new JButton ("Prev");
		nextB = new JButton ("Next");
		delB = new JButton ("Del");
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(prevB);
		buttonPanel.add(nextB);
		buttonPanel.add(delB);
		buttonPanel.setLayout(new BoxLayout(buttonPanel,BoxLayout.Y_AXIS));
		
		
		
		// button implementations
		ActionListener pfButtons = new ActionListener(){
			int currIdx = 0;	
			@Override
			public void actionPerformed(ActionEvent event) {
				
				if (thumbNailList.contains(currPhoto)){
					currIdx = thumbNailList.indexOf(currPhoto);
				}else{ // no valid set to work with, this photo may have been deleted.
					prevB.setEnabled(false);
					nextB.setEnabled(false);
					delB.setEnabled(false);
				}
				if (currIdx == 0){
					prevB.setEnabled(false);
				}
				if (currIdx == thumbNailList.size()-1){
					nextB.setEnabled(false);
				}
				
				if (event.getActionCommand().equals("Prev")){
					editPanel.setPhoto(thumbNailList.get(currIdx-1));
				}else if (event.getActionCommand().equals("Next")){
					editPanel.setPhoto(thumbNailList.get(currIdx+1));
				}else if (event.getActionCommand().equals("Del")){
					if (currIdx<thumbNailList.size()){
						Album currAlbum = currPhoto.getAlbum();
						try {
							currAlbum.removePhoto(currPhoto);
							trash.addPhoto(currPhoto);
							mainWindow.setThumbs(currAlbum.getPhotos());
						} catch (PhotoDoesNotExistException e) {
							e.printStackTrace();
						} catch (PhotoAlreadyInAlbumException e) {
							e.printStackTrace();
						}
						editPanel.setPhoto(thumbNailList.get(currIdx+1));
					}
					
				}else{
					System.out.println("unknown event " + event.getActionCommand());
				}
				
				// updated photo, get new index and set buttons accordingly
				currIdx = thumbNailList.indexOf(currPhoto);
				if ((thumbNailList.size()==1)){
					prevB.setEnabled(false);
					nextB.setEnabled(false);
				}else if (currIdx == 0 ){
					prevB.setEnabled(false);
					nextB.setEnabled(true);
				}else if (currIdx == thumbNailList.size()-1){
					prevB.setEnabled(true);
					nextB.setEnabled(false);
				}else {
					prevB.setEnabled(true);
					nextB.setEnabled(true);
				}
				if (thumbNailList.contains(currPhoto)){
					delB.setEnabled(true);
				}else{
					delB.setEnabled(false);
				}			
			}
			
		};
		
		prevB.addActionListener(pfButtons);
		nextB.addActionListener(pfButtons);
		delB.addActionListener(pfButtons);
		
		photoFrame.add(buttonPanel,BorderLayout.EAST);
		return photoFrame;
	}


	/**
	 * Make a panel containing a selector for an album.
	 * Show current album by default and update as user
	 * selects.  Trigger update views to thumbnail panel as well.
	 * @return
	 */
	private JPanel makeAlbumSelector() {
		// create drop down list of albums
		Set<Album> albums = photomanager.getAlbums();
		JComboBox albumComboBoxList = new JComboBox(albums.toArray());
		albumComboBoxList.addItem(trash); // add a trash album not in photomanager
		
		if (currPhoto!= null){
//			JLabel photoname = new JLabel(currPhoto.getName());
			
			// find the index of the album for our photo
			int i = 0 ;
			boolean found = false;
			while ((i< albums.size())&&(!found)){
				if (albumComboBoxList.getItemAt(i).equals(currPhoto.getAlbum())){
					albumComboBoxList.setSelectedIndex(i);
					found = true;
				}
				i++;
			}
			if (!found){
				albumComboBoxList.setSelectedIndex(albumComboBoxList.getItemCount());
			}

			// action listener in drop down box to move to new album
			albumComboBoxList.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					JComboBox cb = (JComboBox)e.getSource();
					Album selectedAlbum = (Album)cb.getSelectedItem();
					Album oldAlbum = currPhoto.getAlbum();
					if (!selectedAlbum.equals(oldAlbum)){
						try {
							oldAlbum.removePhoto(currPhoto);
							selectedAlbum.addPhoto(currPhoto);
							mainWindow.setThumbs(selectedAlbum.getPhotos());
						} catch (PhotoDoesNotExistException e1) {
							e1.printStackTrace();
						} catch (PhotoAlreadyInAlbumException e1) {
							e1.printStackTrace();
						}

					}
				}
			});
			
			JPanel albumchangerPanel = new JPanel();
			albumchangerPanel.setBorder(
					BorderFactory.createCompoundBorder(
							BorderFactory.createTitledBorder("Move Photo to Album"),
							BorderFactory.createEmptyBorder(5,5,5,5)));
			albumchangerPanel.add(albumComboBoxList);
			albumchangerPanel.setToolTipText("Shows current Album of this Photo, Select to Change");
			
			return albumchangerPanel;
		}// else photo is null, major problem.... return a blank panel
		return new JPanel();
	}




	/**
	 * Set all the Edit pane information to reflect the current information associated with 
	 * the newly selected photo. This can be either a new edit window or updating an existing
	 * edit window.
	 * @param photo
	 */
	public void setPhoto(Photo aPhoto){
		this.currPhoto = aPhoto;
			System.out.println("EditPanel.setPHoto");
			JFrame theEditFrame = mainWindow.getFrame();
			if (theEditFrame == null){
				mainWindow.setEditFrame(new JFrame("Photo Editor"));
				theEditFrame = mainWindow.getFrame();
			}
			mainWindow.getEditFrame().setTitle("PhotoEditor: " + aPhoto.getName());

			mainWindow.setEditPanel(this);
			this.editPanel = this;
			this.editPanel.removeAll();
			makeDisplay(aPhoto);

			mainWindow.getEditScrollPane().setViewportView(editPanel);
			mainWindow.getEditScrollPane().setVisible(true);
			if(mainWindow.getEditFrame() == null){
				mainWindow.setEditFrame(new JFrame("Editing "+aPhoto.getName() + " info"));
			}
			mainWindow.getEditFrame().add(editPanel);
			mainWindow.getEditFrame().pack();
			mainWindow.getEditFrame().setVisible(true);

		}
	

	/**
	 * get the photo currently being edited
	 * @return photo that is currently the focus of the editpanel
	 */
	Photo getCurrentPhoto(){
		return currPhoto;
	}


	/**
	 * get the album labeled trash. This album is not registered with the photomanager so 
	 * that any photos moved to it are no longer visible to the photomanager.
	 * @return
	 */
	public Album getTrash() {
		return trash;
	}


	/**
	 * completely remove access to any photos in the trash album
	 */
	public void emptyTrash() {
		trash = new Album("trash");

	}
}

