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
package uk.ac.cam.cl.passgori.app;

import uk.ac.cam.cl.passgori.PasswordStoreException;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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
public class PassgoriLoginActivity extends AbstractLoadingActivity {

  private void displayListPasswordActivity() {
    try {
      EditText usernameField = (EditText) findViewById(R.id.loginUsernameEditView);
      EditText passwordField = (EditText) findViewById(R.id.passgoriPassword);
      binder.createStore(usernameField.getText().toString(), passwordField.getText().toString());

      Intent intent = new Intent(PassgoriLoginActivity.this, PassgoriListPasswordsActivity.class);
      startActivityForResult(intent, 0);
    } catch (PasswordStoreException e) {
      new FailureNotification(e.getMessage()).run();
    }
  }

	private Button mLoginButton;
	private Button mConfigureButton;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.passgori_login);
	}

	/**
	 * Create the service and connect
	 */
	private void connectAndLogin() {

    if (!connected) {
      mLoadingDialog = ProgressDialog.show(this, "", "Connecting. Please wait...", true);
      new AsyncTask<Object, Object, Object>() {// don't want to do this on the UI thread

        @Override
        protected Object doInBackground(Object... arg0) {
          // Bind to PasswordStoreService
          Intent intent = new Intent(PassgoriLoginActivity.this, PasswordStoreService.class);

          if (!getApplicationContext().bindService(intent, mConnection, Context.BIND_AUTO_CREATE)) {
            runOnUiThread(new FailureNotification("Failed to create internal service"));
          }
          return null;
        }
      }.execute();

    } else {
      displayListPasswordActivity();
    }
	}

	@Override
	protected void onStart() {
		super.onStart();
		EditText userNameField = (EditText) findViewById(R.id.loginUsernameEditView);
		PassgoriConfigurations pc = new PassgoriConfigurations(this);

		userNameField.setText(pc.getUsername());

		mLoginButton = (Button) findViewById(R.id.unlockButton);

		mLoginButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				connectAndLogin();
			}
		});
    mConfigureButton = (Button) findViewById(R.id.configureButton);

    mConfigureButton.setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View v) {
        Intent configIntent =
            new Intent(PassgoriLoginActivity.this, PassgoriConfigurationsEditor.class);
        startActivityForResult(configIntent, 0);

      }
    });

	}

	@Override
	protected void onStop() {
		super.onStop();
		EditText passwordField = (EditText) findViewById(R.id.passgoriPassword);
		passwordField.setText("");
	}

  @Override
  protected void displayError(String errorMessage) {
    TextView errorTextView = (TextView) findViewById(R.id.loginerror);
    errorTextView.setText(errorMessage);
  }

  @Override
  protected void onConnected() {
    displayListPasswordActivity();    
  }
}
