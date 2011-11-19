import java.util.AbstractList;

/**
 * 
 */

/**
 * An interface for password store objects.
 * 
 * @author Miltiadis Allamanis
 * 
 */
public interface IPasswordStore {
	/**
	 * Return a list of all the stored passwords.
	 * 
	 * @return a vector containing the ids of stored passwords
	 */
	public AbstractList<String> getAllStoredPasswordIds();

	/**
	 * Remove password from store.
	 * 
	 * @param aId
	 *            the id of the password
	 * @return true if the password has been sucessfuly removed
	 */
	public boolean removePassword(final String aId);

	/**
	 * Retrieve a unique password with the specified id.
	 * 
	 * @param aId
	 *            the unique password id to look for
	 * @return a Password object or null if no password with an id is found
	 */
	public Password retrivePassword(final String aId);

	/**
	 * Store a password object to the store.
	 * 
	 * @param aPassword
	 *            the password to store
	 * @return true if the password has been stored sucessfully
	 */
	public boolean storePassword(final Password aPassword);
}
