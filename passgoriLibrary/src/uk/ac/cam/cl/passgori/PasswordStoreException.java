package uk.ac.cam.cl.passgori;
/**
 * 
 */

/**
 * An exception being thrown by the password store.
 * 
 * @author Miltiadis Allamanis
 * 
 */
public class PasswordStoreException extends Exception {
	public PasswordStoreException(Exception e) {
		super(e);
	}

	public PasswordStoreException(String msg) {
		super(msg);
	}
}
