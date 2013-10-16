package ca.ubc.cs.cpsc211.photo;

/**
 * Occurs if a photo does not exist in the expected directory
 * 
 * @author CPSC 211 Instructor
 *
 */

public class PhotoDoesNotExistException extends Exception {

	public PhotoDoesNotExistException(String string) {
		super(string);
	}

	/**
	 * You do NOT have to worry about this serialVersionUID
	 * 211 students please ignore :) 
	 */
	private static final long serialVersionUID = 3633905260757030560L;

}
