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
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

/**
 * An activity for showing and editing Passgori configurations.
 * 
 * @author Miltiadis Allamanis
 * 
 */
public class PassgoriConfigurationEditorActivity extends AbstractLoadingActivity {

  public enum MODE {
    NEW_USER("newUser"), EXISTING_REMOTE("haveRemote"), CONFIGURE("configure");
    public final String string;

    private MODE(String str) {
      this.string = str;
    }
  }

  /**
   * The configurations object.
   */
  private PassgoriConfiguration mConfigs;

  private TextView mUsernameField;
  private TextView mServerField;
  private TextView mPortField;
  private TextView mPrefixField;
  private Button mCreateButton;

  private TextView mPasswordField;

  private CheckBox mUseRemoteStoreCheck;
  private MODE mode;

  @Override
  public void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.configuration);

    String action = getIntent().getAction();
    if (MODE.EXISTING_REMOTE.equals(action)) {
      mode = MODE.EXISTING_REMOTE;
    } else if (MODE.NEW_USER.equals(action)) {
      mode = MODE.NEW_USER;
    } else if (MODE.CONFIGURE.equals(action)) {
      mode = MODE.CONFIGURE;
    } else {
      mode = MODE.CONFIGURE;
    }

    mUsernameField = (TextView) findViewById(R.id.passgoriUsername);
    mPasswordField = (TextView) findViewById(R.id.passgoriPassword);
    mUseRemoteStoreCheck = (CheckBox) findViewById(R.id.useRemoteCheckbox);
    mServerField = (TextView) findViewById(R.id.passgoriServer);
    mPortField = (TextView) findViewById(R.id.passgoriPort);
    mPrefixField = (TextView) findViewById(R.id.passgoriServerPrefix);
    mCreateButton = (Button) findViewById(R.id.createButton);
    switch (mode){
      case CONFIGURE:
        mCreateButton.setText(getString(R.string.saveAndUnlockButtonLabel));
        break;
      case EXISTING_REMOTE:
        //mCreateButton.setText(getString(R.string.));
        break;
      case NEW_USER:
        break;// correct text already set
    }

    mConfigs = new PassgoriConfiguration(this);
    mUseRemoteStoreCheck.setChecked(mConfigs.getUseRemoteStore());

    mCreateButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        saveConfig();
        connect();
      }
    });

  }

  /**
   * Save current configurations.
   */
  private void saveConfig() {
    mConfigs.setUsername(mUsernameField.getText().toString());
    mConfigs.setServerPrefix(mPrefixField.getText().toString());
    mConfigs.setServer(mServerField.getText().toString());
    mConfigs.setPort(Integer.parseInt(mPortField.getText().toString()));
    mConfigs.setUseRemoteStore(mUseRemoteStoreCheck.isChecked());
  }

  @Override
  protected void onStart() {
    super.onStart();
    mUsernameField.setText(mConfigs.getUsername());
    mServerField.setText(mConfigs.getServer());
    mPortField.setText(mConfigs.getPort() + "");
    mPrefixField.setText(mConfigs.getServerPrefix());
  }

  @Override
  protected void displayError(String errorMessage) {
    Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();
  }

  @Override
  protected void onConnected() throws PasswordStoreException {
    switch (mode) {
      case EXISTING_REMOTE:
        binder.connectStore(mPasswordField.getText().toString());
        binder.createStore(true);
        break;
      case NEW_USER:
        binder.connectStore(mPasswordField.getText().toString());
        binder.createStore(false);
        break;
      case CONFIGURE:
        finish();
        return;// do nothing
    }
    Intent list = new Intent(this, PassgoriListPasswordsActivity.class);
    startActivity(list);

    finish();
  }
}
