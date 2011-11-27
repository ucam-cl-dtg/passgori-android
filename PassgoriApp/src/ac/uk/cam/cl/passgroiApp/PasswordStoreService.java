/**
 * 
 */
package ac.uk.cam.cl.passgroiApp;

import uk.ac.cam.cl.passgori.IPasswordStore;
import uk.ac.cam.cl.passgori.NigoriPasswordStore;
import uk.ac.cam.cl.passgori.PasswordStoreException;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

/**
 * A Password store service used to access the password store from various
 * activities.
 * 
 * @author Miltiadis Allamanis
 * 
 */
public class PasswordStoreService extends Service {

	/**
	 * Class used for the client Binder.
	 */
	public class PasswordStorageBinder extends Binder {
		/**
		 * Return the unique store provided by the service.
		 * 
		 * @return the password store
		 * @throws PasswordStoreException
		 */
		IPasswordStore getStore() throws PasswordStoreException {
			// TODO: Add timeout for security purposes
			if (mPasswordStore == null) {
				try {
					mPasswordStore = new NigoriPasswordStore("test", "test",
							"192.168.0.8", 8888, "nigori");
					// TODO: dynamically create type based on stored parameters
					// (?)
				} catch (Exception e) {
					throw new PasswordStoreException(e.getMessage());
				}
			}
			return mPasswordStore;
		}
	}

	/**
	 * The password store being used.
	 */
	IPasswordStore mPasswordStore = null;

	// Binder given to clients
	private final IBinder mBinder = new PasswordStorageBinder();

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}
}
