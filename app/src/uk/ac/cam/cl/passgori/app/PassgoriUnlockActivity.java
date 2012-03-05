/*
 * Copyright 2011 Miltiadis Allamanis
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

/**
 * 
 */
package uk.ac.cam.cl.passgori.app;

import uk.ac.cam.cl.passgori.PasswordStoreException;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
public class PassgoriUnlockActivity extends AbstractLoadingActivity {

  private Button mLoginButton;
  
  private void displayListPasswordActivity() {
    try {
      EditText passwordField = (EditText) findViewById(R.id.passgoriPassword);
      binder.connectStore(passwordField.getText().toString());

      Intent intent = new Intent(PassgoriUnlockActivity.this, PassgoriListPasswordsActivity.class);
      startActivityForResult(intent, 0);
    } catch (PasswordStoreException e) {
      new FailureNotification(e.getMessage()).run();
    }
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.unlock);
  }

  /**
   * Create the service and connect
   */
  private void connectAndLogin() {
    connect();
  }

  @Override
  protected void onStart() {
    super.onStart();

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

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.unlock_menu, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle item selection
    switch (item.getItemId()) {
      case R.id.configure:
        Intent configIntent =
            new Intent(PassgoriUnlockActivity.this, PassgoriConfigurationEditorActivity.class);
        configIntent.setAction(PassgoriConfigurationEditorActivity.CONFIGURE);
        startActivityForResult(configIntent, 0);
        return true;

      default:
        return super.onOptionsItemSelected(item);
    }
  }
}
