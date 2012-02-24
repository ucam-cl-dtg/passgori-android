/*
 * Copyright 2011 Miltiadis Allamanis
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 
package uk.ac.cam.cl.passgori.app;

import java.util.List;

import uk.ac.cam.cl.passgori.IPasswordStore;
import uk.ac.cam.cl.passgori.PasswordStoreException;
import uk.ac.cam.cl.passgori.app.PasswordStoreService.PasswordStorageBinder;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * The main password listing activity.
 * 
 * @author Miltiadis Allamanis
 * 
 */
public class PassgoriListPasswordsActivity extends Activity {

	/**
	 * A Runnable for updating the GUI on failure.
	 * 
	 */
	private class InformFailureRunnable implements Runnable {

		private final String mFailureMessage;

		/**
		 * The constructor.
		 * 
		 * @param message
		 *            the message to be presented to the user
		 */
		public InformFailureRunnable(String message) {
			mFailureMessage = message;
		}

		@Override
		public void run() {
			mWaitingLinearLayout.removeAllViews();
			mWaitingText.setText(mFailureMessage);
			mWaitingLinearLayout.addView(mWaitingText);
			// TODO: Ideally add a failure sign!
		}

	}

	/**
	 * A Thread responsible for updating the list
	 * 
	 */
	private class UpdateList extends Thread {
		@Override
		public void run() {

			List<String> passwordList;
			try {
				passwordList = getPasswordList();
				if (passwordList != null) {
					// Update GUI
					final ArrayAdapter<String> adapter = new ArrayAdapter<String>(
							PassgoriListPasswordsActivity.this,
							R.layout.password_list_item, passwordList);

					runOnUiThread(new UpdateListRunnable(adapter));
				} else {
					runOnUiThread(new InformFailureRunnable(
							getString(R.string.noPasswordsError)));
				}

			} catch (final PasswordStoreException e) {
				runOnUiThread(new InformFailureRunnable(e.getMessage()));
			}

		}

		/**
		 * Retrieve Password List
		 * 
		 * @return
		 * @throws PasswordStoreException
		 */
		private List<String> getPasswordList() throws PasswordStoreException {
			if (mPasswordStore == null)
				throw new PasswordStoreException(
						"Password Store instance not instantiated");
			return mPasswordStore.getAllStoredPasswordIds();

		}

	}

	/**
	 * Runnable to update the list on the GUI
	 * 
	 */
	private class UpdateListRunnable implements Runnable {
		private final class PasswordListClickListener implements
				AdapterView.OnItemClickListener {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String passwordId = ((TextView) view).getText().toString();
				Intent intent = new Intent(view.getContext(),
						PassgoriPresentPasswordsActivity.class);
				intent.putExtra("passwordId", passwordId);
				startActivityForResult(intent, 0);
			}
		}

		/**
		 * The password List
		 */
		final ArrayAdapter<String> mPasswordAdapter;
		final ListView mPasswordListView;

		public UpdateListRunnable(final ArrayAdapter<String> passwordAdapter) {
			mPasswordAdapter = passwordAdapter;
			mPasswordListView = new ListView(PassgoriListPasswordsActivity.this);
			mPasswordListView
					.setOnItemClickListener(new PasswordListClickListener());
			mPasswordListView.setAdapter(mPasswordAdapter);
		}

		@Override
		public void run() {

			// Remove waiting
			mWaitingLinearLayout.removeAllViews();
			mWaitingLinearLayout.addView(mPasswordListView);
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
			try {
				mPasswordStore = binder.getStore();
				mBound = true;

				// Load List!
				new UpdateList().start();
			} catch (PasswordStoreException e) {
				new InformFailureRunnable(e.getMessage()).run();
			}

		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			mBound = false;
		}
	};

	private LinearLayout mWaitingLinearLayout;

	private TextView mWaitingText;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.password_list);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.password_list_menu, menu);
		return true;
	}

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle item selection
    switch (item.getItemId()) {
      case R.id.passwordListRefresh:
        refreshPasswordList();
        return true;

      case R.id.passgoriConfigure:
        Intent configIntent = new Intent(this, PassgoriConfigurationsEditor.class);
        startActivityForResult(configIntent, 0);
        return true;

      case R.id.passwordListAdd:
        Intent intent = new Intent(this, PassgoriEditPasswordActivity.class);
        startActivityForResult(intent, 0);
        return true;

      case R.id.passgoriBackup:
        Intent backupIntent = new Intent(this, PassgoriBackupActivity.class);
        startActivityForResult(backupIntent, 0);
        return true;

      default:
        return super.onOptionsItemSelected(item);
    }
  }

	/**
	 * Refresh the password list
	 */
	private void refreshPasswordList() {
		mWaitingLinearLayout.removeAllViews();
		TextView waitingText = new TextView(this);
		waitingText.setText(R.string.refreshing);
		waitingText.setGravity(Gravity.CENTER);
		waitingText.setTextAppearance(this,
				android.R.style.TextAppearance_Large);

		final LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);

		final ProgressBar pb = new ProgressBar(this);
		pb.setLayoutParams(params);
		mWaitingLinearLayout.addView(pb);
		mWaitingLinearLayout.addView(waitingText);
		new UpdateList().start();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode > 0)
			refreshPasswordList();
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onStart() {
		super.onStart();

		mWaitingLinearLayout = (LinearLayout) this
				.findViewById(R.id.loadingView);
		mWaitingText = (TextView) this.findViewById(R.id.listLoadingText);

		// Bind to PasswordStoreService
		Intent intent = new Intent(this, PasswordStoreService.class);

		if (!getApplicationContext().bindService(intent, mConnection,
				Context.BIND_AUTO_CREATE)) {
			mWaitingLinearLayout.removeAllViews();
			mWaitingText.setText(R.string.serviceBindError);
			mWaitingLinearLayout.addView(mWaitingText);
		}

		// Once the service is binded, we will load the list
	}

}