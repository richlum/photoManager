package ca.ubc.cs.cpsc211.photo.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;

import ca.ubc.cs.cpsc211.photo.Album;
import ca.ubc.cs.cpsc211.photo.DuplicateTagException;
import ca.ubc.cs.cpsc211.photo.Photo;
import ca.ubc.cs.cpsc211.photo.PhotoAlreadyInAlbumException;
import ca.ubc.cs.cpsc211.photo.PhotoDoesNotExistException;
import ca.ubc.cs.cpsc211.photo.PhotoManager;
import ca.ubc.cs.cpsc211.photo.Tag;
import ca.ubc.cs.cpsc211.photo.TagManager;
import ca.ubc.cs.cpsc211.utility.ThumbnailDoesNotExistException;
import cs211.labs.cgui.CalculatorGUI;

/**
 * MainWindow is the main frame for most window components and will contain several major
 * panels -
 * 
 * Leftpanel contains albumlist, taglist, information pane. 
 * Centerpane provides space to display large picture. 
 * Thumbnailpane provides a place to see the contents (photos) of currently selected album or tag.
 * 
 * General structure is that all subpanels should reference MainWindow to get access to other subpanel
 * information or send request to update with new information.  Given that there are multiple update
 * relationships (all panels write to information pane to provide info, most panels write to 
 * thumbnail pane in response to user selection and update contents of thumbnail pane. Thumbnail pane 
 * writes to center pane in reponse to user clicks etc..._)  the best way was to have a central 
 * the mainWindow method call the subPanel updates rather than directly from subPanel to subPanel in
 * hopes of providing a central place for future updates/control.
 * 
 * Liberal use of sample code provided in Lectures as well as sample code from Oracle for implementation 
 * of various elements.   Calculator code is GUI extension on top of that provided in CS211 labs.
 * 
 * @author rlum
 *
 */
public class MainWindow {

	private static final int preferedLeftPanelWidth = 320;
	// 
	private JFrame frame; // the main window frame
	private Container cp; // main window content pane
	private JScrollPane thumbnailPane;  // view of photos belonging to current album or tag
	private JScrollPane centerPane;  // place for the big picture display
	private JPanel centerPicture; // within centerpane...
	private CenterLabel centerLabel; // BigPicture display with listeners
	private JScrollPane albumPane; // contains the albumlist
	private JScrollPane tagPane; // contains the taglist
	private JScrollPane infoPane;  // contains the updatefield
	private JTextArea updateField; // displays various information based on mouse position
	private JTextField tagInput; // the textfield in the EditPanel for new tag entry
	private AlbumList albumlist; // the list of albums to be displayed in the album pane
	private TagList taglist; // the list of tags to be displayed in the tagPane
	private JScrollPane editScrollPane;  // the scrollpane that contains the edit panel
	private EditPanel editPanel; // contains multiple components that provide a user means to update photo information
	private JFrame editFrame; // frame that contains the edit panel
	
	private PhotoManager pm ;
	private TagManager tm;
	private JPanel thumbs; // single panel containing thumbList
	private List<JLabel> thumbList; // list of all thumbnails used for display
	
	private JProgressBar progress;
	
	private JFrame tagCloud;
	private MainWindow main;
	private UIManager.LookAndFeelInfo [] looks ;
	/**
	 * the MainWindow that manages all subPanes that are displayed to the user
	 */
	// some utility items
	private Calendar cal;
	private Toolkit tk;
	
	/**
	 * Create the main window, all subpanels
	 * instantiate the library along with photomanager and tagmanager
	 */
	public MainWindow(){
		try {
			looks = UIManager.getInstalledLookAndFeels();
//			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}catch(Exception e){
			System.out.println("Failed to get native look and feel...proceeding anyway...");
			e.printStackTrace();
		}
		
		cal = Calendar.getInstance();
		
		// get the pointers to the source data for display
		pm = new PhotoManager();
		tm = new TagManager();
		try {
			loadFiles();
		} catch (DuplicateTagException e) {
			e.printStackTrace();
		}
		thumbList = new ArrayList<JLabel>();
		drawMainWindow();
		
		System.out.println("done MainWindow");
//		makeLookFeel();
	}
	
	
	/**
	 * Demonstration files are loaded with this method to provide
	 * some content to play with.
	 * @throws DuplicateTagException 
	 * 
	 * 
	 */
	public void loadFiles() throws DuplicateTagException{
		
		
		
		// temp harness to get initial files for display to be replaced
		// when we implement serialization and saving of meta data.
		// create a number of albums and add a number of photos and random tags
		Album album = new Album("FirstAlbum");
		tm.createTag("oneTag");
		tm.createTag("evenPHotos");
		tm.createTag("notEven");
		tm.createTag("blue");
		tm.createTag("green");
		tm.createTag("red");
		tm.createTag("yellow");
		tm.createTag("purple");
		for (int i = 0; i<10 ; i++){
			Photo aPhoto = new Photo(new Integer(i).toString());
			try {
				Date today = new Date();
				aPhoto.loadPhoto();
				aPhoto.setDateAdded(today);
				album.addPhoto(aPhoto);
				aPhoto.addTag(tm.findTag("oneTag"));
				if (i%2 == 0 ){
					aPhoto.addTag(tm.findTag("evenPHotos"));
				}else{
					aPhoto.addTag(tm.findTag("notEven"));
				}
			} catch (PhotoDoesNotExistException e) {
				e.printStackTrace();
			} catch (PhotoAlreadyInAlbumException e) {
				e.printStackTrace();
			}
			
		}
		pm.addAlbum(album);
		album = new Album("SecondAlbum");
		for (int i = 10; i<16 ; i++){
			Photo aPhoto = new Photo(new Integer(i+1).toString());
			try {
				aPhoto.loadPhoto();
				aPhoto.addTag(tm.findTag("oneTag"));
				if (i%2 == 0 ){
					aPhoto.addTag(tm.findTag("evenPHotos"));
				}
				album.addPhoto(aPhoto);
			} catch (PhotoDoesNotExistException e) {
				e.printStackTrace();
			} catch (PhotoAlreadyInAlbumException e) {
				e.printStackTrace();
			}
			
		}
		pm.addAlbum(album);
		album = new Album("thirdAlbum");
		for (int i = 16; i<20 ; i++){
			Photo aPhoto = new Photo(new Integer(i+1).toString());
			try {
				Date today = new Date();
				aPhoto.loadPhoto();
				aPhoto.setDateAdded(today);
				aPhoto.addTag(tm.findTag("oneTag"));
				if (i%2 == 0 ){
					aPhoto.addTag(tm.findTag("evenPHotos"));
				}
				aPhoto.setDescription("This is a Photograph");
				album.addPhoto(aPhoto);
			} catch (PhotoDoesNotExistException e) {
				e.printStackTrace();
			} catch (PhotoAlreadyInAlbumException e) {
				e.printStackTrace();
			}
			
		}
		pm.addAlbum(album);
		album = new Album("fourthAlbum");
		for (int i = 20; i<23 ; i++){
			Photo aPhoto = new Photo(new Integer(i+1).toString());
			try {
				aPhoto.loadPhoto();
				aPhoto.addTag(tm.findTag("oneTag"));
				album.addPhoto(aPhoto);
				if (i%2 == 0 ){
					aPhoto.addTag(tm.findTag("evenPHotos"));
				}
			} catch (PhotoDoesNotExistException e) {
				e.printStackTrace();
			} catch (PhotoAlreadyInAlbumException e) {
				e.printStackTrace();
			}
			
		}
		pm.addAlbum(album);
		tm.createTag("noDescription");
		tm.createTag("twoTaggers");
		tm.createTag("wDescriptions");
		Set<Photo> photos = pm.getPhotos();
		for (Photo photo : photos){
			if (photo.getDescription().isEmpty()){
				photo.addTag(tm.findTag("noDescription"));
			}else{
				photo.addTag(tm.findTag("wDescriptions"));
			}
			if (photo.getTags().size()>2){
				photo.addTag(tm.findTag("twoTaggers"));
			}
		}
		
	}

	/**
	 * Based on installed look and fields, populate a drop down menu for user to 
	 * select alternate look and feel
	 */
	private void makeLookFeel() {

		JFrame lookFrame = new JFrame();
		Container lookCp =	lookFrame.getContentPane();
		LookAndFeelChooser lookChooser = new LookAndFeelChooser	(looks, main);
		lookCp.add(lookChooser);
		
		lookFrame.pack();
		lookFrame.setVisible(true);
		
	}


	/**
	 * the main window method that lays out subpanels
	 */
	public void drawMainWindow(){
		main = this;
		frame = new JFrame(checkDate());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		cp = frame.getContentPane();
		initializeMenus();
		
		cp.setLayout(new BorderLayout());
		frame.setPreferredSize(getUserScreenSize());
	
		infoPane = initializeInfoPane();
		tagPane = initTagPanel();
//		tagInput = initTagInput();
		albumPane = initAlbumPanel();
		// Initialise with thumbnailPane showing all photos in library
		thumbnailPane = initThumbsPanel(pm.getPhotos());
		centerPane = initCenterPanel();
		
		// left pane will contain albumpane, tagpane and infopane
		JPanel leftpane = new JPanel();
		leftpane.setLayout(new GridLayout(3,1));
		leftpane.setPreferredSize(new Dimension( preferedLeftPanelWidth, frame.getSize().height));
		leftpane.add(albumPane);
		leftpane.add(tagPane);
		leftpane.add(infoPane);
		
		// editscroll pane will not be initially visible but is intialized here
		editScrollPane = initEditScrollPane(main,null);

		// allows user selectable width to tradeoff centerpane vs left pane allocation
		JSplitPane mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                leftpane,centerPane	);
		mainSplitPane.setOneTouchExpandable(true);
        mainSplitPane.setDividerLocation(250);
        
        frame.getContentPane().add(mainSplitPane,BorderLayout.CENTER);
		frame.getContentPane().add(thumbnailPane, BorderLayout.SOUTH);
		
		frame.pack();
		frame.setVisible(true);
	}


//	//place holder to implement tag inputs
//	private JTextField initTagInput() {
//		tagInput = new JTextField();
//		// place holder to implement actions on tag info
//		tagInput.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent e)
//			{
//				//listModel.addElement(tf.getText());
//			}
//		});
//		return tagInput;
//	}

	/**
	 * information pane shows user the details of currently selected item, 
	 * wether its a album from the album list, a tag from the taglist or
	 * photo from the thumbnails
	 * @return JScrollPane containing the infoPane
	 */
	private JScrollPane initializeInfoPane(){
		updateField = new JTextArea();
		updateField.setText("Information Pane");
		updateField.setVisible(true);
		updateField.setLayout(new FlowLayout());

		JScrollPane result = new JScrollPane(updateField);
		result.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		result.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		result.setVisible(true);
		return result;
	}
	
	/**
	 * center pane is for display of the large view of the photo
	 * @return JScrollPane containing the centerPane
	 */
	private JScrollPane initCenterPanel() {
		centerLabel = new CenterLabel(main, null);
		centerLabel.setVisible(true);
		centerPicture = new JPanel();
		centerPicture.add(centerLabel);
		JScrollPane centerPane = new JScrollPane(centerPicture);
		centerPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		centerPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		centerPane.setVisible(true);
		centerPicture.setVisible(true);
		
		return centerPane;
		
	}

	/**
	 * the scrolling view of a collection of photos - either from
	 * an album, from a tag collection of photos or photomanagers.getPHotos
	 * @param photos
	 * @return JScrollPane containing the set of thumbnails
	 */
	public JScrollPane initThumbsPanel(Set<Photo> photos) {
		thumbs = new JPanel();
		thumbs.setLayout(new BoxLayout(thumbs, BoxLayout.X_AXIS));
		int maxHt =0;
		for (Photo photo : photos){
			try {
				System.out.println("th comp count = " + thumbs.getComponentCount());
				ThumbNail thumbnail = new ThumbNail(photo,this);
				thumbs.add(thumbnail);
				thumbList.add(thumbnail);
				if (thumbnail.getPreferredSize().width > maxHt){
					maxHt = thumbnail.getPreferredSize().width;
				}
			} catch (ThumbnailDoesNotExistException e) {
				e.printStackTrace();
			}
		}
		thumbs.getComponent(0).requestFocus();
		
		JScrollPane result = new JScrollPane(thumbs);
		result.setPreferredSize(new Dimension(getUserScreenSize().width ,maxHt));
		result.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		result.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		return result;
	}

	/**
	 * given a set of photos, get the thumbnails and put them into the  
	 * the thumbnail pane.  This allows other panels to display their 
	 * selection in the thumbnail pane.
	 * @param photos - set of photos that are to be displayed in the thumbnail pane.
	 */
	public void setThumbs(Set<Photo> photos){
		
		int newPanelWidth = 0;
		System.out.println("th comp count = " + thumbs.getComponentCount());
		thumbs.removeAll();

		System.out.println("th comp count = " + thumbs.getComponentCount());
		for (Photo photo : photos){
			try {
				ThumbNail thumbnail = new ThumbNail(photo,this);
				newPanelWidth += thumbnail.getPreferredSize().width;
				thumbs.add(thumbnail);
				thumbList.add(thumbnail);
				System.out.println("th comp count = " + thumbs.getComponentCount());
						
			} catch (ThumbnailDoesNotExistException e) {
				e.printStackTrace();
			}
		}
		if (thumbs.getPreferredSize().width<newPanelWidth){
			thumbs.setPreferredSize(new Dimension(newPanelWidth,thumbs.getPreferredSize().height));
		}
		thumbs.doLayout();
		thumbnailPane.doLayout();
		frame.repaint();
		

	}
	
	/**
	 * initialize the album pane with all the albums in the library
	 * @return JScrollPane containing the Album pane
	 */
	private JScrollPane initAlbumPanel() {
		albumlist = new AlbumList(this, pm, new DefaultListModel());

		albumlist.setVisible(true);
		JScrollPane jsp = new JScrollPane(albumlist);
		jsp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		jsp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		jsp.setVisible(true);
		return jsp;
		
	}

	/**
	 * Initialize the tagpane with current set of tags in library
	 * @return the JScrollPane containing the tag pane
	 */
	private JScrollPane initTagPanel() {
		taglist = new TagList(this, tm, new DefaultListModel());

		JScrollPane jsp = new JScrollPane(taglist);
		jsp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		jsp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		return jsp;		
	}

	/**
	 * initialize the editPane, not initially visible.
	 * @param main - hand the edit pane a pointer to the main window so that edit pane
	 * can send update requests to other panels.  Since edit activity can update any
	 * photo information, editPane needs access to all other panels for updating.
	 * @param photo
	 * @return scrollpane containing the editPane
	 */
	private JScrollPane initEditScrollPane(MainWindow main, Photo photo){
		editScrollPane = new JScrollPane();
		return editScrollPane;
	}
	
	/**
	 * find the user environment and set prefered sizing for application window
	 * @return
	 */
	Dimension getUserScreenSize(){
		tk = Toolkit.getDefaultToolkit();
		tk.beep();
		Dimension screenSize = tk.getScreenSize();
		System.out.println("User Screen Size = " + screenSize )	;
		screenSize.height=screenSize.height*7/8;
		screenSize.width = screenSize.width*7/8;
		return screenSize;
	}
	
	/**
	 * multiple menus to add.  Event listeners all created as anonymous local methods
	 * some logic moved to helper methods for readability.
	 */
	private void initializeMenus() {
		JMenuBar menuBar;
		
		JMenu fileMenu;
		JMenuItem addPhotosToAlbum;
		JMenuItem quitMenuItem;
		
		JMenu editMenu;
		JMenuItem editMenuItem;
		JMenuItem unDeleteMenuItem;
		JMenuItem emptyTrashMenuItem;
		JMenuItem tagCloudMenuItem;
		JMenuItem changeLookMenuItem;
		
		JMenu helpMenu;
		JMenuItem aboutMenu;
		JMenuItem goofyMenuItem;
				
		// add the menu components
		fileMenu = new JMenu("File");
		fileMenu.setMnemonic(KeyEvent.VK_F);
		
		addPhotosToAlbum = new JMenuItem("Add Photos to a SelectedAlbum");
		addPhotosToAlbum.setMnemonic(KeyEvent.VK_A);
		addPhotosToAlbum.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				PhotoLoader pl = new PhotoLoader(main);
				pl.start();
			}

//			private void showProgressBar() {
//				progress = new JProgressBar(0,100);
//				PhotoProgress photoProgress = new PhotoProgress(progress);
//				
//			}
		});
		
		quitMenuItem = new JMenuItem("Quit");
		quitMenuItem.setMnemonic(KeyEvent.VK_Q);
			quitMenuItem.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					frame.setVisible(false);
					frame.dispose();
				}
			});
		// first menuitems on menubar	
		fileMenu.add(addPhotosToAlbum);
		fileMenu.add(quitMenuItem);
		
		
		editMenu = new JMenu("Edit");
		editMenu.setMnemonic(KeyEvent.VK_E);
		
		tagCloudMenuItem = new JMenuItem("TagCloud");
		tagCloudMenuItem.setMnemonic(KeyEvent.VK_T);
		tagCloudMenuItem.addActionListener(new ActionListener() {
				
			@Override
			public void actionPerformed(ActionEvent e) {
//				generateAndShowTagCloud();
				TagCloud tagCloud = new TagCloud(main);
				}
		});
		editMenu.add(tagCloudMenuItem);
			
		editMenuItem = new JMenuItem("Modify Photo Info");
		editMenuItem.setMnemonic(KeyEvent.VK_M);
			editMenuItem.addActionListener(new ActionListener(){
				// launches edit scroll panel
				@Override
				public void actionPerformed(ActionEvent e) {
					EditPanel theEditPanel = getEditPanel();
					if (main.getCenterLabel().getPhoto()!=null)
						if (theEditPanel==null){
							theEditPanel = new EditPanel(main, main.getCenterLabel().getPhoto(), getPhotoManager(), getTagManager());
						}else{
							theEditPanel.setPhoto(main.getCenterLabel().getPhoto());
						}
					
				}
			});
		editMenu.add(editMenuItem);
		
		
		
		unDeleteMenuItem = new JMenuItem("Retrieve Trash");
		unDeleteMenuItem.addActionListener(new ActionListener(){
				// launches edit scroll panel
				@Override
				public void actionPerformed(ActionEvent e) {
					EditPanel theEditPanel = getEditPanel();
					if ((pm.findAlbum("trash")==null)){
						pm.addAlbum(theEditPanel.getTrash());
						albumlist.refreshAlbumList();
					}
				}
			});
		editMenu.add(unDeleteMenuItem);
		
		emptyTrashMenuItem = new JMenuItem("Empty Trash");
		emptyTrashMenuItem.addActionListener(new ActionListener(){
				// launches edit scroll panel
				@Override
				public void actionPerformed(ActionEvent e) {
					
						pm.removeAlbum(pm.findAlbum("trash"));
						albumlist.refreshAlbumList();
					
					EditPanel theEditPanel = getEditPanel();
					theEditPanel.emptyTrash();
				}
			});
		editMenu.add(emptyTrashMenuItem);	
		
		
		changeLookMenuItem = new JMenuItem("Change Look and Feel");
		changeLookMenuItem.addActionListener(new ActionListener(){
				// launches edit scroll panel
				@Override
				public void actionPerformed(ActionEvent e) {
					
						makeLookFeel();
				}
			});
		editMenu.add(changeLookMenuItem);
		
		// completes the second list of menu items
		
		
		helpMenu = new JMenu("Help");
		helpMenu.setMnemonic(KeyEvent.VK_H);
		aboutMenu = new JMenuItem("About");
			aboutMenu.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					String result = "Help?,  We're beyond help!";
					JOptionPane.showMessageDialog(null, result); 	
					
				}
			});
		helpMenu.add(aboutMenu);
		
		// some fun stuff
		goofyMenuItem = new JMenuItem("Goofy");
		goofyMenuItem.setMnemonic(KeyEvent.VK_G);
			goofyMenuItem.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					System.out.println("goofy menu item");
					CalculatorGUI calculator = new CalculatorGUI();
			        calculator.showCalculator(pm.getPhotos());
					
				}
			});
		helpMenu.add(goofyMenuItem);
		// completes the last set of menu items
		
		menuBar = new JMenuBar();
		menuBar.add(fileMenu);
		menuBar.add(editMenu);
		menuBar.add(helpMenu);
		
		frame.setJMenuBar(menuBar);
		
	}

	/**
	 * to remind user to do christmas shopping (and maybe put me on the christmas list
	 * @return String containing title for frame window
	 */
	public String checkDate(){
		String result = "";
		if (cal.get(Calendar.MONTH) >= Calendar.NOVEMBER){
			Calendar  christmas = Calendar.getInstance();
			christmas.set(cal.get(Calendar.YEAR),Calendar.DECEMBER,25);
			long christmasDay =christmas.getTimeInMillis();
			long now = cal.getTimeInMillis();
			long delta = christmasDay - now;
			long diffDays = delta / (24 * 60 * 60 * 1000);
			int diffdaysI = (int) diffDays;
			if (diffdaysI>0){
				result = "" + diffdaysI + " days  to Christmas";
			}else{
				result = "Merry Christmas";
			}
		}else{
			result = " THE CS211 PhotoBrowser";
		}
		return result; 
	}

	/**
	 * for  methods to update the information pane that is used as the main user feedback 
	 * based on mouse position.
	 * @return JTextArea that contains the update information displayed to the user
	 */
	public JTextArea getUpdateField(){
		return this.updateField;
	}
	/**
	 * for methods to find out what is currently displayed in the center pane
	 * @return JPanel containg the centerPicture
	 */
	public JPanel getCenterPicture(){
		return this.centerPicture;
	}
	/** 
	 * for sub methods to get content pane of root window
	 * @return Content pane of the main window
	 */
	public Container getMainWindowContentPane(){
		return this.cp;
	}
	
	/**
	 * for sub methods to get the root frame
	 * @return frame of the main window
	 */
	public JFrame getFrame(){
		return this.frame;
	}
	/**
	 * to allow methods to place a photo in the center pane
	 * primarily for thumbnail pane to throw up a large photo
	 * @return The label that contains the large photo in centerpane
	 */
	public CenterLabel getCenterLabel(){
		return this.centerLabel;
	}

	/**
	 * to allow co-ordination between albumlist and taglist so that
	 * only one or the other has an active selection
	 */
	public void clearTagSelection(){
		taglist.clearSelection();
	}
	
	/**
	 * to allow co-ordination between albumlist and taglist so that
	 * only one or the other has an active selection
	 */
	public void clearAlbumSelection(){
		albumlist.clearSelection();
	}
	
	/**
	 * allows other panels to get a hold of editScrollPane for updating
	 * @return editScrollPane - is the scrollpane that contains the editPanel
	 */
	public JScrollPane getEditScrollPane(){
		return editScrollPane;
	}
	/**
	 * allows other panels to get access to EditPanel for updating
	 * @return editPanel is the Panel that contains multiple components for editing photo information
	 */
	public EditPanel getEditPanel(){
		return editPanel;
	}
	/**
	 * rewrite theEditPanel for refresh as necessary
	 * @param editPanel
	 */
	public void setEditPanel (EditPanel editPanel){
		this.editPanel = editPanel;
	}
	/**
	 * get the frame that contains the EditPanel and EditScrollPanel
	 * @return editFrame that contains the edit panel 
	 */
	public JFrame getEditFrame(){
		return this.editFrame;
	}
	/**
	 * rewrite the Frame that contains the editPanel
	 * @param frame that contains the editpanel
	 */
	public void setEditFrame(JFrame frame){
		this.editFrame = frame;
	}
	/**
	 * allows other panels to get access to the photomanager kept by the main window
	 * @return photomanager
	 */
	public PhotoManager getPhotoManager(){
		return pm;
	}
	/**
	 * allows other panels to get access to the tagmanager kept by the main window
	 * @return tagmanager
	 */
	public TagManager getTagManager(){
		return tm;
	}
	
	/**
	 * the current set of photos in the thumbnail display panel. Other panels need to 
	 * get or set this for co-ordinated updating activity between panels.
	 * @return set of photos that are currently displayed in the thumbnail pane
	 */
	public Set<Photo> getThumbPhotoSet(){
		Set<Photo> result = new HashSet<Photo>();
		int qty = thumbs.getComponentCount();
		for (int i = 0; i< qty; i++) {
			ThumbNail thumbNail = (ThumbNail) thumbs.getComponent(i);
			result.add(thumbNail.getPhoto());
		}
		return result;
	}
	
	/**
	 * get the currently selected item in the album list. It will always be an
	 * album or null if nothing is selected or if allPhotos is selected.
	 * @return Album that is currently selected in the album selction list
	 */
	public Album getCurrentAlbumListSelection(){
		return albumlist.getCurrentSelection();
	}
	/**
	 * To allow other panels to request the TagPanel to refresh (such as when new tags 
	 * are added in the editPanel.
	 */
	public void refreshTagPane(){
		taglist.refreshTagList();
	}
	
	/**
	 * utility method to prompt and load new files/photos
	 */
	
	void loadNewPhotos(){
		System.out.println("addPhotosToAlbumitem selected");

		if (main.getCurrentAlbumListSelection()!=null)
		{
			JFileChooser fc = new JFileChooser();
			fc.setMultiSelectionEnabled(true);
			FileNameExtensionFilter filter = new FileNameExtensionFilter(
					"JPG & GIF Images", "jpg", "gif");
			fc.setFileFilter(filter);

			int returnVal = fc.showOpenDialog(cp);
			if(returnVal == JFileChooser.APPROVE_OPTION) {
				System.out.println("You chose to add  file: " );

				File[] selectedFiles = fc.getSelectedFiles();
				for (int i=0; i< selectedFiles.length; i++){
					System.out.println("\t" + selectedFiles[i].getName());
				}
				System.out.println("file directory" + fc.getCurrentDirectory() );
				try {						
					for (int i=0; i< selectedFiles.length; i++){
						System.out.println("canoncacl  " + selectedFiles[i].getCanonicalFile());
						Photo aPhoto = new Photo(selectedFiles[i].getName());
						aPhoto.loadPhoto(selectedFiles[i].getCanonicalFile());
						main.getCurrentAlbumListSelection().addPhoto(aPhoto);							    
					}
					setThumbs(main.getCurrentAlbumListSelection().getPhotos());

				} catch (IOException e) {
					e.printStackTrace();
				}catch (PhotoDoesNotExistException e) {
					e.printStackTrace();
				} catch (PhotoAlreadyInAlbumException e) {
					e.printStackTrace();
				} 

			}
		}else{
			String result = "You must select an Album to which photos are to be added";
			JOptionPane.showMessageDialog(null, result);
		}
	}				

	/**
	 * calculate and display the tagCloud as a series of buttons with the 
	 * font size reflecting the relative quantity of occurences.
	 */
	private void  generateAndShowTagCloud(){
		final int  numFontLevel = 5;  // number of font size levels to use
		System.out.println("photoupdate menu item");
		
		tagCloud = new JFrame("TagCloud");
		tagCloud.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		Container tagCloudContentPane = tagCloud.getContentPane();

		
		// find the highest frequency of occurence for any given tag
		Set<Tag> allTags = tm.getTags();
		int maxFreq = 0;
		for (Tag tag: allTags){
			if (tag.getPhotos().size()>maxFreq)
				maxFreq = tag.getPhotos().size();
		}
		
		// create an array of boundaries for frequency counts to assign to given font size.
		int levels = numFontLevel;
		int [] levelarray = { 
				 maxFreq/levels, maxFreq/levels*2, maxFreq/levels*3, maxFreq/levels*4,maxFreq/levels*5
				};
		int incrSize = 2;  // size step between levels
		int defaultSize = 8; // minimum font size
		JPanel tagJpane = new JPanel();
		tagJpane.setLayout(new GridLayout(0,3));

		// create a button for each tag.  Note the use of TagButton class
		// makes the TagCloud clickable to update thumbnail pane.
		for (Tag tag : allTags){
			TagButton theTagButton = new TagButton( main, tag);
			tagJpane.add(theTagButton);
			int whichLevel = tag.getPhotos().size() / 5;
			int newSize = levelarray[whichLevel]*incrSize + defaultSize;
			theTagButton.setFont(new Font("Dialog", Font.PLAIN, newSize));
		}
		JScrollPane tagSP = new JScrollPane(tagJpane);
		tagSP.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		tagSP.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		tagCloudContentPane.add(tagSP);
		tagCloud.pack();
		tagCloud.setVisible(true);
	}

	/**
	 * get the progress bar in case another panel requires a pointer to it
	 * @return progressBar for loading files
	 */
	public JProgressBar getProgress(){
		return progress;
	}
	
	/**
	 * close the main window.  Main user is the look and feel choose who will geneate
	 * a new main window to replace the current
	 */
	public void closeWindow(){
		frame.removeAll();
		frame.dispose();
	}
}
