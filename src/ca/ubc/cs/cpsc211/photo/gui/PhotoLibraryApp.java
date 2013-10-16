package ca.ubc.cs.cpsc211.photo.gui;



public class PhotoLibraryApp {

	/**
	 * Run the application.
	 */
	public static void main(String[] args) {
		// following the sun style of gui launching in hopes of minimizing
		// what appears to be race condition issues between window manager
		// and my updates (component additions/removals on the fly)
		
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
            	MainWindow theMainWindow = new MainWindow();
            }
        });
		
		
	}

}
