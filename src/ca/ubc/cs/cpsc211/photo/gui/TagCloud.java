package ca.ubc.cs.cpsc211.photo.gui;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import ca.ubc.cs.cpsc211.photo.Photo;
import ca.ubc.cs.cpsc211.photo.Tag;
import ca.ubc.cs.cpsc211.photo.TagManager;
import ca.ubc.cs.cpsc211.utility.ThumbnailDoesNotExistException;

/**
 * TagCloud builds a frame to display the tags as a tagCloud
 * Animation effects rely on Timer class
 * WindowListener is implemented to detect when window is closed or deactivated
 * @author rlum
 *
 */
public class TagCloud implements ActionListener, WindowListener{

	private static final int ANIMATION_DELAY = 10000;
	private MainWindow mainWindow;
	private JFrame tagCloud;
	private List<MovingTagButton> mybuttons ;
	private JPanel tagJpane;
	private Container tagCloudContentPane;
	
	private Random rangen;
	private Timer animationTimer;

	/**
	 * make a tag cloud based on fequency counts of tags.  Poll frequcency and 
	 * divide in to groups  based on the max frequncy
	 * @param mw
	 */
	public TagCloud(MainWindow mw) {
		mybuttons = new ArrayList<MovingTagButton>();
		rangen = new Random();
		animationTimer = new Timer(ANIMATION_DELAY,this);
		
		
		this.mainWindow = mw;
		final int  numFontLevel = 5;  // number of font size levels to use
		System.out.println("photoupdate menu item");

		tagCloud = new JFrame("TagCloud");
		
		tagCloud.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		tagCloud.addWindowListener(this); // notify us so we can shut down timers if user closes windows
		tagCloudContentPane = tagCloud.getContentPane();

		tagJpane = new JPanel();

		// find the highest frequency of occurence for any given tag
		TagManager tm = mainWindow.getTagManager();
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
		//tagJpane.setLayout(new GridLayout(0,3));
		tagJpane.setLayout(new FlowLayout());
		
		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension screenSize = tk.getScreenSize();
		tagJpane.setPreferredSize(screenSize);
		// create a button for each tag.  Note the use of TagButton class
		// makes the TagCloud clickable to update thumbnail pane.
		for (Tag tag : allTags){
			MovingTagButton theTagButton = new MovingTagButton( mainWindow, tag);
			tagJpane.add(theTagButton);
			int whichLevel = tag.getPhotos().size() / 5;
			int newSize = levelarray[whichLevel]*incrSize + defaultSize;
			theTagButton.setFont(new Font("Dialog", Font.PLAIN, newSize));
			mybuttons.add(theTagButton);
		}
//		JScrollPane tagSP = new JScrollPane(tagJpane);
//		tagSP.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
//		tagSP.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
//		tagCloudContentPane.add(tagSP);
		tagCloudContentPane.add(tagJpane);
		tagCloud.pack();
		tagCloud.setVisible(true);
		
		// dont want size to change
		for (MovingTagButton button : mybuttons){
			button.setMaximumSize(button.getSize());
		}
		
//		System.out.println("tagsp = " + tagSP.getSize());
		System.out.println("tagCloudcp =" +tagCloudContentPane.getSize());
		System.out.println("tagPane = " + tagJpane.getSize());
		System.out.println("tagCloud = " + tagCloud.getSize());
		
//		System.out.println("tagsp = " + tagSP.getBounds());
		System.out.println("tagCloudcp =" +tagCloudContentPane.getBounds());
		System.out.println("tagPane = " + tagJpane.getBounds());
		System.out.println("tagCloud = " + tagCloud.getBounds());
		
		//remove layoutmanager now that he set up our initial positions and sizes
		// avoid fighting with it while we manually move thing around
		tagJpane.setLayout(null);
		// start the long cycle proding of buttons to move. 
		// calls to TagCloud.actionPerformed
		animationTimer.start();
		System.out.println("animationtimer running = " + animationTimer.isRunning());
	}

	/**
	 * build on top of tagButton to retain clickable behavoir and add movement
	 *  and  photo scrolling behavoir
	 * @author rlum
	 *
	 */
	class MovingTagButton extends TagButton implements  ActionListener{

		private static final int BUTTON_ANIMATION_DELAY = 20;
		private Point currentLoc;
		private Rectangle whereTo = new Rectangle(0,0) ;
		private Timer myTimer;
		private String name;
		private List<Photo> photoList;
		private int photoTimeMultiple = 50;
		private int photoTimerIncrement = 0;
		private int currentPhoto=0;
		private int qtyPhotos=0;
		
		MovingTagButton(MainWindow mw, Tag myTag) {
			super(mw, myTag);
//			setOpaque(false);  // for transparent button plus paint code below
			name = myTag.getName();
			
			currentLoc = this.getLocation();
			myTimer = new Timer(BUTTON_ANIMATION_DELAY, this);	
			
//			System.out.println(this.getName());
			// get list of photos with this tag
			photoList = new ArrayList<Photo>();
			Set<Photo>tagPhotos = mainWindow.getTagManager().findTag(this.getName()).getPhotos();
			Iterator<Photo> it = tagPhotos.iterator();
			while(it.hasNext()){
				Photo photo = it.next();
				photoList.add(photo	);
				qtyPhotos++;
			}
			
			// set the initial button icon to the first photo associated with this tag 
			it = tagPhotos.iterator();
			if (it.hasNext()){
				Photo firstPhoto = it.next();
				currentPhoto = photoList.indexOf(firstPhoto);
				try {
					this.setIcon(new ImageIcon(firstPhoto.getThumbnailImage()));
				} catch (ThumbnailDoesNotExistException e) {
					e.printStackTrace();
				}
			}
		}
		
		/**
		 * return the name of this tag
		 */
		public String getName(){
			return name;
		}
		
		/**
		 * This works but is really slow.  Transparent buttons.
		 * @return
		 */
//		public void paint(Graphics g){
//			Graphics2D g2 = (Graphics2D) g.create();
//			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.5f));
//			super.paint(g2);
//			g2.dispose();
//			//ref source http://www.dreamincode.net/forums/topic/57078-make-a-transparent-swing-ui-components/
//		}
		// transparency has really high impact on cpu 
		
		/**
		 * return true if this buttons timer is running (eg its moving)
		 */
		public boolean isMoving(){
			return myTimer.isRunning();
		}

		/**
		 * set the destination of this button 
		 */
		private void setTarget(){
			int maxY = tagJpane.getHeight()-100;
			int maxX = tagJpane.getWidth()-300;
			
			System.out.println ("bounds" + tagJpane.getSize());
			int targX = rangen.nextInt(maxX);
			int targY = rangen.nextInt(maxY);
			int count = 0;
			boolean found = false;
			
			while ((count <5000)&&(!found)){
					if (tagJpane.getComponentAt(new Point (targX,targY)) ==null){
						found = true; // found a point with no other components
						
					}else{
						targX = rangen.nextInt(maxX);
						targY = rangen.nextInt(maxY);
					}
					count++;
			}// try to look for an unoccupied random space
			
			whereTo = new Rectangle();
			whereTo.setLocation(new Point(targX,targY));
			System.out.println("target location " + whereTo.getLocation());
			
		}

		/**
		 * start this buttons timer - which starts this buttons move action
		 */
		public void startMeMoving(){
			myTimer.start();
		}
		
		/** 
		 * the move action called once per timer signal
		 */
		private void moveButton(){
			
			
			int targX = whereTo.x;
			int targY = whereTo.y;
			int curX = this.getX();
			int curY = this.getY();

			if ((targX == 0)||(targY == 0)){
				setTarget();
			}
			int ht = this.getHeight();
			int wt = this.getWidth();
			Rectangle newBound = new Rectangle (curX,curY,wt,ht	);
			if (this.getBounds().getLocation().equals(whereTo.getLocation())){
				myTimer.stop();
//				updatePhoto();
			}else{
				if (curX !=targX){	
					//    			System.out.println("deltaX = " +i);
					if (targX>curX) {
						newBound.setLocation(++curX , curY);
					}else{
						newBound.setLocation(--curX , curY);
					}
				}
				if (curY != targY){
					if (targY>curY){
						newBound.setLocation(curX,++curY);
					}else{
						newBound.setLocation(curX,--curY);
					}

				}
				this.setLocation(newBound.getLocation());
//				this.setBounds(newBound);  //avoid setting size of button, causses issues. bounds set size and location
				this.setVisible(true);  
			}
			
			// use moveButtom to also update photo in button
			// layout manager fighting with me for size of button???
			updatePhoto();
		}


		/**
		 * method to select next photo to paste into button icon
		 */
		private void updatePhoto() {
			if (this.getIcon()== null){
				return;
			}
			photoTimerIncrement++;
			if (photoTimerIncrement>= photoTimeMultiple){
				photoTimerIncrement = 0; // restart counter
//				// pick the next photo
				if (currentPhoto >= qtyPhotos-1){
					currentPhoto = 0;
				}else{
					currentPhoto++;
				}
				Photo next = photoList.get(currentPhoto);
				// update the thumbnail
				try {
					this.setIcon(new ImageIcon(next.getThumbnailImage()));
				} catch (ThumbnailDoesNotExistException e) {
					e.printStackTrace();
				}
			}
			
		}

		/**
		 * movingTagButtons listener for Time events. Each timer event executes this
		 * for a single 'frame' in the animation
		 */
		@Override
		public void actionPerformed(ActionEvent e) {

			moveButton();
		}

	}
	
	
	/** 
	 * this is the timer associated with the tagCloud. It prods all the buttons
	 * to move if they aren't moving already
	 * 
	 * This is also a good place to shuffle the zorder of buttons so that
	 * buttons on the bottom can be rotated to the top.
	 *
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		System.out.println("animationTimer fired" +  animationTimer.isRunning());
		for (MovingTagButton mtb : mybuttons ){
			if (!mtb.isMoving())
				mtb.setTarget(); // pick a random location within Frame
				mtb.moveButton(); // prod the buttons to move
				mtb.startMeMoving();
				
				// while getting buttons to move again, find the bottom zorder button and bring it to the top.
				// ask swing to do this - doing it directly seems to cause race condition issues during animation.
				
				SwingUtilities.invokeLater(new Runnable() {
			    public void run() {
			    	int count = tagJpane.getComponentCount();
//			    	System.out.println("component count = " + count);

			    	//shift all components down one
			    	for (int i = count-2; i>0 ; i--){
			    		tagJpane.setComponentZOrder(
			    				tagJpane.getComponent(i) ,i+1) ;
//			    		System.out.println(tagJpane.getComponent(i).getName() + " z = " +
//			    			tagJpane.getComponentZOrder(tagJpane.getComponent(i)));
			    	}
			    	// take the top and push it to the bottom
			    	tagJpane.setComponentZOrder(
			    			tagJpane.getComponent(count-1), 0);	

			    }
			});
		}
	}


	/**
	 * Following method are for the windowlistener implementation so that
	 * we shut down timers when user closes window or deactivates windows.
	 * restart timers if windows become active again.
	 * Since button timers shut themselves of upon reaching destination, we
	 * dont need to shut them of, only the tagcloud timer that launches the button
	 * timers.
	 */
	@Override
	public void windowActivated(WindowEvent arg0) {
		
		if (!animationTimer.isRunning()){
			animationTimer.start();
		}
		
	}


	@Override
	public void windowClosed(WindowEvent arg0) {

		if (animationTimer.isRunning()){
			animationTimer.stop();
		}
		
	}


	@Override
	public void windowClosing(WindowEvent arg0) {

		if (animationTimer.isRunning()){
			animationTimer.stop();
		}
	}


	@Override
	public void windowDeactivated(WindowEvent arg0) {

		if (animationTimer.isRunning()){
			animationTimer.stop();
		}
		
	}


	@Override
	public void windowDeiconified(WindowEvent arg0) {
		if (!animationTimer.isRunning()){
			animationTimer.start();
		}
	}


	@Override
	public void windowIconified(WindowEvent arg0) {

		if (animationTimer.isRunning()){
			animationTimer.stop();
		}
	}


	@Override
	public void windowOpened(WindowEvent arg0) {

		
	}

}
