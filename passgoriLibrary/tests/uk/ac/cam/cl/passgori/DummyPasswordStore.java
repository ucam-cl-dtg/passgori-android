/**
 * 
 */
package uk.ac.cam.cl.passgori;

/**
 * A Dummy Password Store to be used for testing
 * 
 * @author Miltiadis Allamanis
 * 
 */
public class DummyPasswordStore extends MemoryUnsafePasswordStore {
	/**
	 * Initialize and add dummy data.
	 */
	public DummyPasswordStore() {
		super();
		for (int i = 0; i < 20; i++) {
			this.mPasswordStore.put("Password" + i, new Password(
					"Password" + i, "a" + i, "bb" + i, "ccc" + 1));
		}

	}
}
