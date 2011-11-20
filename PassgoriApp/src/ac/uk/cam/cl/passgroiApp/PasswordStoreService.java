/**
 * 
 */
package ac.uk.cam.cl.passgroiApp;

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
		PasswordStoreService getService() {
			// Return this instance of LocalService so clients can call public
			// methods
			return PasswordStoreService.this;
		}
	}

	// Binder given to clients
	private final IBinder mBinder = new PasswordStorageBinder();

	/** method for clients */
	public int getRandomNumber() {
		return 0;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}
}
