package ca.ubc.cs.cpsc211.photo;

import java.util.*;

/**
 * An album contains a set of photos.
 */
public class Album {

	// The photos that the album contains
	private Set<Photo> photos;

	// The name of the album
	private String name;

	/**
	 * @pre name != null
	 * @param name
	 *            The name for the album
	 */
	public Album(String name) {
		assert(name != null);
		this.name = name;
		photos = new HashSet<Photo>();
	}

	/**
	 * @return The album name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set the tag name. Should only be called by a PhotoManager.
	 * Clients should use PhotoManager.renameAlbum(oldName, newName) to rename tags.
	 */
	void setName(String name) {
		assert(name != null);		
		this.name = name;
	}

	/**
	 * Add a photo to this album.
	 * 
	 * @pre photos.contains(photo) == false AND photo.getAlbum() == null
	 * @post @post.photos.contains(photo) == true
	 * @throws PhotoAlreadyInAlbumException
	 *             if the precondition is violated
	 */
	public void addPhoto(Photo photo) throws PhotoAlreadyInAlbumException {
		if (photo.getAlbum() != null)
			throw new PhotoAlreadyInAlbumException();
		if (photos.contains(photo))
			throw new PhotoAlreadyInAlbumException();
		photos.add(photo);
		photo.setAlbum(this);
	}

	/**
	 * Remove a photo from this album.
	 * 
	 * @pre getPhotos.contains(photo) == true
	 * @post @post.getPhotos.contains(photo) == false
	 * @throws PhotoDoesNotExistException
	 *             if the precondition is violated
	 */
	public void removePhoto(Photo photo) throws PhotoDoesNotExistException {
		if (!photos.contains(photo))
			throw new PhotoDoesNotExistException(photo.getName());
		photos.remove(photo);
		// Set the photo's link to its album to null just in case we haven't
		// severed all links to this photo. The photo is logically removed
		// from the system. Java will garbage collect the space taken
		// by this photo when there exists no more references to the object.
		photo.setAlbum(null);
	}
	
	/**
	 * Return access to all photos
	 * @return An unmodifiable version of the photos set
	 */
	public Set<Photo> getPhotos() {
		return Collections.unmodifiableSet(photos);
	}
	
	@Override
	public boolean equals(Object o) {
		if ( o == null )
			return false;
		if ( getClass() != o.getClass() )
			return false;
		if (name == null)
			return false;
		// Two albums are equal if their names are the same
		Album otherAlbum = (Album) o;
		return name.equals( otherAlbum.getName() );
	}
	
	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public String toString() {
		return "Album(" + name + ")";
	}


}
