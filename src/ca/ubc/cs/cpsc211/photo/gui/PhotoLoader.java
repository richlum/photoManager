package ca.ubc.cs.cpsc211.photo.gui;

import java.awt.Container;
import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;

import ca.ubc.cs.cpsc211.photo.Photo;
import ca.ubc.cs.cpsc211.photo.PhotoAlreadyInAlbumException;
import ca.ubc.cs.cpsc211.photo.PhotoDoesNotExistException;

/**
 * query user for what files/pictures to load and load them
 * implement as seperate thread to get it out of the gui thread to
 * prevent blocking of gui updates.
 * @author rlum
 *
 */
public class PhotoLoader extends Thread{
	private MainWindow main;
	private Container cp;
	private JProgressBar progress;  // our progress bar 
	private File[] selectedFiles;  // the set of files that user has selected
	private PhotoProgress photoProgress;
	
	public PhotoLoader(MainWindow mw){
		this.main=mw;
		this.cp = mw.getFrame().getContentPane();
		
		
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
//
//				progress.setMaximum(fc.getSelectedFiles().length);
				
				selectedFiles = fc.getSelectedFiles();
				for (int i=0; i< selectedFiles.length; i++){
					System.out.println("\t" + selectedFiles[i].getName());
				}
				System.out.println("file directory" + fc.getCurrentDirectory() );
		
				this.progress = new JProgressBar(0,selectedFiles.length);
				photoProgress = new PhotoProgress(progress);
			}
		}else{
			String result = "You must select an Album to which photos are to be added";
			JOptionPane.showMessageDialog(null, result);
		}
	}
	
	/*
	 * The main loop for loading multiple selected files into library.
	 * (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	public void run(){
		try {						
			for (int i=0; i< selectedFiles.length; i++){

				System.out.println("canoncacl  "+ i + "  " + selectedFiles[i].getCanonicalFile());
				Photo aPhoto = new Photo(selectedFiles[i].getName());
				aPhoto.loadPhoto(selectedFiles[i].getCanonicalFile());
				main.getCurrentAlbumListSelection().addPhoto(aPhoto);		
				updateProgress(i+1, selectedFiles[i].getName().toString());
				//	above works because it posts a job to swing gui versus directly trying to 
				//	update progress as below -- doesnt work as no cycles allocated for gui update
				//			progress.setValue(i+1);
				//			progress.repaint();
			}
			main.setThumbs(main.getCurrentAlbumListSelection().getPhotos());

		} catch (IOException e) {
			e.printStackTrace();
		}catch (PhotoDoesNotExistException e) {
			e.printStackTrace();
		} catch (PhotoAlreadyInAlbumException e) {
			e.printStackTrace();
		} 

	}
	
	/**
	 * This is the key to updating the progress bar during the time consuming file loading
	 * process.  SwingUtilities invoke later is the way to ask the swing window manager to 
	 * do some work for us since this thread is now not part of the gui event thread,.
	 * 
	 * There seems to be other ways to do this as well but they seem more complex and
	 * require more familiarity with the swing thread model (SwingWorker)
	 */
	private void updateProgress( final int value, final String fn) {
		  SwingUtilities.invokeLater(new Runnable() {
		    public void run() {
		      progress.setValue(value);
		      photoProgress.setLabel(fn);
		    }
		  });
	}
	
	
	
}
