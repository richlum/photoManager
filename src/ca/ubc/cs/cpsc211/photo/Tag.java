
package ca.ubc.cs.cpsc211.photo;

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * A tag, consisting of a name and an added date.
 */
public class Tag {
	
	private String name;
	private Date dateAdded;
	
	private Set<Photo> photos;
	
	
	/**
	 * Create a new tag with the provided name and set its added date to the current time.
	 * Should only be called by a TagManager.
	 * Clients should use tagManager.createTag(name) to create tags.
	 */
	Tag(String name) {
		setName(name);
		dateAdded = new Date(); 
		photos = new HashSet<Photo>();
	}
	
	
	/**
	 * @return The tag name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return The date this tag was created
	 */
	public Date getDateAdded() {
		return dateAdded;
	}

	
	/**
	 * Set the tag name. Should only be called by a TagManager.
	 * Clients should use tagManager.renameTag(oldName, newName) to rename tags.
	 */
	void setName(String name) {
		this.name = name;
	}
	
	
	/**
	 * Add the tag to a photo.
	 */
	 void addToPhoto(Photo photo){
		if(!photos.contains(photo)){
			photos.add(photo);
			photo.addTag(this);
		}
	}
	
	/**
	 * Remove the tag from a photo.
	 */
	 void removeFromPhoto(Photo photo) {
		if(photos.contains(photo)){
			photos.remove(photo);
			photo.removeTag(this);
		}
	}
	
	/**
	 * @return The photos associated with this tag.
	 */
	public Set<Photo> getPhotos(){
		return Collections.unmodifiableSet(photos);
	}
	
	@Override
	public String toString(){
		return "Tag(" + name + ")";
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
		Tag otherTag = (Tag) o;
		return name.equals( otherTag.getName() );
	}
	
	@Override
	public int hashCode() {
		return name.hashCode();
	}


	
}

