package ca.ubc.cs.cpsc211.photo;
/**
 * exception for when we try to use a  name that is already in use
 * @author rlum
 *
 */
public class NameAlreadyUsedException extends DuplicateTagException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Thrown when a name is already in use
	 * @param msg is the name that had a collision.
	 */
	public NameAlreadyUsedException(String msg){
		super();
	}
}
