/**
 * 
 */
package uk.ac.cam.cl.passgori;

import java.util.List;

/**
 * A password store offering random delay to emulate the network
 * 
 * @author Miltiadis Allamanis
 * 
 */
public class DelayedDummyPasswordStore extends DummyPasswordStore {

	/**
	 * A constant indicating the maximum delay in milliseconds.
	 */
	public static final int MAX_DELAY = 10000;

	@Override
	public boolean authorize(String username, String password) {
		try {
			Thread.sleep((long) (MAX_DELAY * Math.random()));
		} catch (InterruptedException e) {
		}
		return super.authorize(username, password);
	}

	@Override
	public List<String> getAllStoredPasswordIds() throws PasswordStoreException {
		try {
			Thread.sleep((long) (MAX_DELAY * Math.random()));
		} catch (InterruptedException e) {
		}
		return super.getAllStoredPasswordIds();
	}

	@Override
	public boolean removePassword(String aId) throws PasswordStoreException {
		try {
			Thread.sleep((long) (MAX_DELAY * Math.random()));
		} catch (InterruptedException e) {
		}
		return super.removePassword(aId);
	}

	@Override
	public Password retrivePassword(String aId) throws PasswordStoreException {
		try {
			Thread.sleep((long) (MAX_DELAY * Math.random()));
		} catch (InterruptedException e) {
		}
		return super.retrivePassword(aId);
	}

	@Override
	public boolean storePassword(Password aPassword)
			throws PasswordStoreException {
		try {
			Thread.sleep((long) (MAX_DELAY * Math.random()));
		} catch (InterruptedException e) {
		}
		return super.storePassword(aPassword);
	}
}
