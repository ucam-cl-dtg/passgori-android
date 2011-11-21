/**
 * 
 */
package ac.uk.cam.cl.passgroiApp;

import uk.ac.cam.cl.passgori.IPasswordStore;
import uk.ac.cam.cl.passgori.Password;
import uk.ac.cam.cl.passgori.PasswordStoreException;
import ac.uk.cam.cl.passgroiApp.PasswordStoreService.PasswordStorageBinder;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.TextView;

/**
 * An activity presenting the a password entity.
 * 
 * @author Miltiadis Allamanis
 * 
 */
public class PassgoriPresentPasswordsActivity extends Activity {

	private class GetPassword extends Thread {
		@Override
		public void run() {
			try {
				Password password = mPasswordStore.retrivePassword(getIntent()
						.getExtras().getString("passwordId"));

				if (password != null) {
					UpdatePasswordDetails updater = new UpdatePasswordDetails(
							password);
					runOnUiThread(updater);
				} else {
					// Update GUI about failure!!
				}
			} catch (PasswordStoreException e) {
				// TODO Inform GUI
				e.printStackTrace();
			}
		}
	}

	private class UpdatePasswordDetails implements Runnable {

		private final Password mPassword;

		public UpdatePasswordDetails(Password password) {
			mPassword = password;
		}

		@Override
		public void run() {
			mUsernameField.setText(mPassword.getUsername());
			mPasswordField.setText(mPassword.getPassword());
			mNotesField.setText(mPassword.getNotes());

			mUsernameField.setVisibility(View.VISIBLE);
			mPasswordField.setVisibility(View.VISIBLE);
			mNotesField.setVisibility(View.VISIBLE);

			mLoadingDialog.dismiss();
		}

	}

	/**
	 * The TextView corresponding to the password's title.
	 */
	private TextView mPasswordTitle;

	/**
	 * The TextView corresponding to the paswords's username.
	 */
	private TextView mUsernameField;

	/**
	 * The TextView corresponding to the password's password.
	 */
	private TextView mPasswordField;

	/**
	 * The TextView corresponding to the password's notes.
	 */
	private TextView mNotesField;

	/**
	 * Progress Dialog to indicate progress.
	 */
	private ProgressDialog mLoadingDialog;

	/**
	 * The password store.
	 */
	private IPasswordStore mPasswordStore;

	/** Defines callbacks for service binding, passed to bindService() */
	private final ServiceConnection mConnection = new ServiceConnection() {

		private boolean mBound;

		@Override
		public void onServiceConnected(ComponentName className, IBinder service) {
			PasswordStorageBinder binder = (PasswordStorageBinder) service;
			mPasswordStore = binder.getStore();
			mBound = true;

			// Spawn thread to get password
			new GetPassword().start();

		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			mBound = false;
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.password_present);

		// Assign content
		mPasswordTitle = (TextView) findViewById(R.id.passwordTitle);
		mUsernameField = (TextView) findViewById(R.id.usernameField);
		mPasswordField = (TextView) findViewById(R.id.passwordField);
		mNotesField = (TextView) findViewById(R.id.notesField);

		mLoadingDialog = ProgressDialog.show(this, "",
				"Loading. Please wait...", true);

		mPasswordTitle.setText(getIntent().getExtras().getString("passwordId"));
		mUsernameField.setVisibility(View.INVISIBLE);
		mPasswordField.setVisibility(View.INVISIBLE);
		mNotesField.setVisibility(View.INVISIBLE);
	}

	@Override
	public void onStop() {
		super.onStop();
		// Close activity to avoid storing the password if someone exits us from
		// the home button
		finish();
	}

	@Override
	protected void onStart() {
		super.onStart();

		// Bind to PasswordStoreService
		Intent intent = new Intent(this, PasswordStoreService.class);

		if (!getApplicationContext().bindService(intent, mConnection,
				Context.BIND_AUTO_CREATE)) {
			// Inform GUI for failure!!
		}

		// Once the service is binded, we will load the list
	}

}
