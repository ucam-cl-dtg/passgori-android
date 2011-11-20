package uk.ac.cam.cl.passgori;
import java.util.List;

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
	 * Authorize access inputing username and password.
	 * 
	 * @param username
	 *            the username
	 * @param password
	 *            the password
	 * @return true if authorization process has succeeded
	 * @throws PasswordStoreException
	 */
	public boolean authorize(final String username, final String password)
			throws PasswordStoreException;

	/**
	 * Return a list of all the stored passwords.
	 * 
	 * @return a vector containing the ids of stored passwords
	 * @throws PasswordStoreException
	 *             when is unable to perform operation
	 */
	public List<String> getAllStoredPasswordIds() throws PasswordStoreException;

	/**
	 * Remove password from store.
	 * 
	 * @param aId
	 *            the id of the password
	 * @return true if the password has been successful removed, false if the
	 *         password does not exist or deletion has failed
	 * @throws PasswordStoreException
	 *             when store is unable to perform operation
	 */
	public boolean removePassword(final String aId)
			throws PasswordStoreException;

	/**
	 * Retrieve a unique password with the specified id.
	 * 
	 * @param aId
	 *            the unique password id to look for
	 * @return a Password object or null if no password with an id is found
	 * @throws PasswordStoreException
	 *             when store is unable to perform operation
	 */
	public Password retrivePassword(final String aId)
			throws PasswordStoreException;

	/**
	 * Store a password object to the store.
	 * 
	 * @param aPassword
	 *            the password to store
	 * @return true if the password has been stored successfully
	 * @throws PasswordStoreException
	 *             when is unable to perform operation
	 */
	public boolean storePassword(final Password aPassword)
			throws PasswordStoreException;
}
