package ca.ubc.cs.cpsc211.photo.gui;

import java.awt.Container;
import java.awt.Cursor;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

import javax.swing.BoxLayout;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileNameExtensionFilter;

import ca.ubc.cs.cpsc211.photo.Photo;

public class ProgressBar implements PropertyChangeListener	{
	
//	private JProgressBar progressBar;
//	private int lastValue=0;
//	private int maxsize=0;
//	private Task task;
//	private MainWindow main;
//	File[] selectedFiles;
//	
//	class Task extends SwingWorker<Void, Void>{
//		
//		// loading files into album put into background task to free up gui updates.
//		@Override
//		protected Void doInBackground() throws Exception {
//			progressBar.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
//			for (int i=0; i< selectedFiles.length; i++){
//				Photo aPhoto = new Photo(selectedFiles[i].getName());
//				aPhoto.loadPhoto(selectedFiles[i].getCanonicalFile());
//				main.getCurrentAlbumListSelection().addPhoto(aPhoto);
//				int value = progressBar.getValue();
//				System.out.println("progress = " + value);
//			    progressBar.setValue(i+1);
//			    lastValue=i+1;
//			}
//			main.setThumbs(main.getCurrentAlbumListSelection().getPhotos());
//			return null;			
//		}
//		
//	}
//	
//	ProgressBar(int maxSize, MainWindow main	){
//		this.main = main;
//		this.maxsize = maxSize;
//				
//		JFileChooser fc = new JFileChooser();
//		fc.setMultiSelectionEnabled(true);
//		FileNameExtensionFilter filter = new FileNameExtensionFilter(
//				"JPG & GIF Images", "jpg", "gif");
//		fc.setFileFilter(filter);
//
//		int returnVal = fc.showOpenDialog(main.getFrame().getContentPane());
//		if(returnVal == JFileChooser.APPROVE_OPTION) {
//			System.out.println("You chose to add  file: " );
//			this.selectedFiles = fc.getSelectedFiles();
//			for (int i=0; i< selectedFiles.length; i++){
//					System.out.println("\t" + selectedFiles[i].getName());
//				}
//			System.out.println("file directory" + fc.getCurrentDirectory() );
//			
//			task = new Task();
//			task.addPropertyChangeListener(this);
//			task.execute();
//			
//		progressBar = new JProgressBar(0,maxSize);
//		progressBar.setCursor (Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
//		progressBar.setMinimum(0);
//		progressBar.setValue(0);
//		progressBar.setStringPainted(true);
//
//
//		JPanel progressPanel = new JPanel();
//		progressPanel.setLayout(new BoxLayout(progressPanel,BoxLayout.Y_AXIS));
//		progressPanel.add(progressBar);
//		JLabel statusLabel = new JLabel("Adding Photos");
//		progressPanel.add(statusLabel);
//		JFrame progressFrame = new JFrame();
//		Container progressFrameCP = progressFrame.getContentPane();
//		progressFrameCP.add(progressPanel);
//		progressFrame.pack();
//		progressFrame.setVisible(true);
//		System.out.println(progressBar.toString()  + "lastvalue = " + lastValue + "maxsize = " +  maxsize);
//		}
//	}
//	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
//	public void setValue(int i) {
//		lastValue = i+1;
//		progressBar.setValue(i+1);
//		if ((progressBar.getValue() - this.maxsize ) <=1) 
//			progressBar.setCursor(null);
//		
//	}
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		// TODO Auto-generated method stub
		
	}


}
