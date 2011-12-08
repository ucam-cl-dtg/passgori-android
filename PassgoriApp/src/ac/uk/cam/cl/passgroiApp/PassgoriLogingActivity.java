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
 
/**
 * 
 */
package ac.uk.cam.cl.passgroiApp;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * A login activity for Passgori.
 * 
 * @author Miltiadis Allamanis
 * 
 */
public class PassgoriLogingActivity extends Activity {
	private class FailureNotification implements Runnable {

		private final String mMessage;

		public FailureNotification(String message) {
			mMessage = message;
		}

		@Override
		public void run() {
			if (mLoadingDialog != null)
				mLoadingDialog.dismiss();
			TextView errorTextView = (TextView) findViewById(R.id.loginerror);
			errorTextView.setText(mMessage);
		}

	}

	/** Defines callbacks for service binding, passed to bindService() */
	private final ServiceConnection mConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName className, IBinder service) {
			PasswordStorageBinder binder = (PasswordStorageBinder) service;
			try {
				EditText passwordField = (EditText) findViewById(R.id.passgoriPassword);
				binder.createStore(passwordField.getText().toString());

				Intent intent = new Intent(PassgoriLogingActivity.this,
						PassgoriListPasswordsActivity.class);
				startActivityForResult(intent, 0);

			} catch (PasswordStoreException e) {
				new FailureNotification(e.getMessage()).run();
			}

		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {

		}
	};

	/**
	 * Progress Dialog to indicate progress.
	 */
	private ProgressDialog mLoadingDialog;

	private Button mLoginButton;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.passgori_login);
	}

	/**
	 * Create the service and connect
	 */
	private void connectAndLogin() {
		// Bind to PasswordStoreService
		Intent intent = new Intent(this, PasswordStoreService.class);

		if (!getApplicationContext().bindService(intent, mConnection,
				Context.BIND_AUTO_CREATE)) {
			new FailureNotification("Failed to create internal service").run();
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		TextView userNameField = (TextView) findViewById(R.id.loginUsernameTextView);
		PassgoriConfigurations pc = new PassgoriConfigurations(this);

		userNameField.setText(pc.getUsername());

		mLoginButton = (Button) findViewById(R.id.unlockButton);

		mLoginButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				connectAndLogin();
			}
		});

	}

	@Override
	protected void onStop() {
		super.onStop();
		finish();
	}
}
