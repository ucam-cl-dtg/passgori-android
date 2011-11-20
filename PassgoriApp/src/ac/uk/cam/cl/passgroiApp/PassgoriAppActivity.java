package ac.uk.cam.cl.passgroiApp;

import java.util.List;

import uk.ac.cam.cl.passgori.IPasswordStore;
import uk.ac.cam.cl.passgori.PasswordStoreException;
import ac.uk.cam.cl.passgroiApp.PasswordStoreService.PasswordStorageBinder;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * 
 * @author Miltiadis Allamanis
 * 
 */
public class PassgoriAppActivity extends Activity {

	/**
	 * A Thread responsible for updating the list
	 * 
	 */
	private class UpdateList extends Thread {
		@Override
		public void run() {

			final List<String> passwordList = getPasswordList();
			if (passwordList != null) {
				// Update GUI
				runOnUiThread(new UpdateListRunnable(passwordList));
			} else {
				// Inform GUI about our tragic failure
			}
		}

		/**
		 * Retrieve Password List
		 * 
		 * @return
		 */
		private List<String> getPasswordList() {
			try {
				mPasswordStore.authorize("l", "b"); // TODO: Change...
				return mPasswordStore.getAllStoredPasswordIds();
			} catch (PasswordStoreException e) {
				return null;
			}

		}

	}

	/**
	 * Runnable to update the list on the GUI
	 * 
	 */
	private class UpdateListRunnable implements Runnable {
		/**
		 * The password List
		 */
		final List<String> mPasswordList;

		public UpdateListRunnable(final List<String> passwordList) {
			mPasswordList = passwordList;

			ListView passwordListView = new ListView(PassgoriAppActivity.this);
			passwordListView.setAdapter(new ArrayAdapter<String>(
					PassgoriAppActivity.this, R.layout.password_list_item,
					mPasswordList));
			// Remove waiting
			mWaitingLinearLayout.removeAllViews();
			mWaitingLinearLayout.addView(passwordListView);

		}

		@Override
		public void run() {

		}

	}

	/**
	 * The password store.
	 */
	private IPasswordStore mPasswordStore;

	/**
	 * Boolean indicating if activity is bound on service.
	 */
	boolean mBound = false;

	/** Defines callbacks for service binding, passed to bindService() */
	private final ServiceConnection mConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName className, IBinder service) {
			PasswordStorageBinder binder = (PasswordStorageBinder) service;
			mPasswordStore = binder.getStore();
			mBound = true;

			// Load List!
			new UpdateList().run();
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			mBound = false;
		}
	};

	private LinearLayout mWaitingLinearLayout;

	private ProgressBar mWaitingProgress;

	private TextView mWaitingText;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.password_list);

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// Unbind from the service
		if (mBound) {
			unbindService(mConnection);
			mBound = false;
		}
	}

	@Override
	protected void onStart() {
		super.onStart();

		mWaitingLinearLayout = (LinearLayout) this
				.findViewById(R.id.loadingView);
		mWaitingProgress = (ProgressBar) this
				.findViewById(R.id.listLoadingProgress);
		mWaitingText = (TextView) this.findViewById(R.id.listLoadingText);

		// Bind to PasswordStoreService
		Intent intent = new Intent(this, PasswordStoreService.class);

		if (!getApplicationContext().bindService(intent, mConnection,
				Context.BIND_AUTO_CREATE)) {
			mWaitingText.setText("Failed to Bind to Internal Service");
			mWaitingLinearLayout.removeView(mWaitingProgress);
		}

		// Once the service is binded, we will load the list
	}

	@Override
	protected void onStop() {
		super.onStop();
		// Unbind from the service
		if (mBound) {
			unbindService(mConnection);
			mBound = false;
		}
	}

}