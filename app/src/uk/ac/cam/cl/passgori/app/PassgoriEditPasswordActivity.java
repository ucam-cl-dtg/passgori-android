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

import uk.ac.cam.cl.passgori.IPasswordStore;
import uk.ac.cam.cl.passgori.Password;
import uk.ac.cam.cl.passgori.PasswordStoreException;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * An activity permitting to edit or add new passwords.
 * 
 * @author Miltiadis Allamanis
 * 
 */
public class PassgoriEditPasswordActivity extends AbstractLoadingActivity {

  private final class CancelButtonListener implements View.OnClickListener {
    @Override
    public void onClick(View v) {
      // Finish
      finish();
    }
  }

  private class GetPassword extends Thread {
    @Override
    public void run() {
      try {
        if (getIntent().getExtras() != null) {
          String passId = getIntent().getExtras().getString("passwordId");
          updateUI(passId);
        } else {
          UpdatePasswordDetails updater = new UpdatePasswordDetails(null);
          runOnUiThread(updater);
        }

      } catch (PasswordStoreException e) {
        runOnUiThread(new FailureNotification(e.getMessage()));

      }
    }

    /**
     * @param passId
     * @throws PasswordStoreException
     */
    private void updateUI(String passId) throws PasswordStoreException {
      Password password = mPasswordStore.retrivePassword(passId);

      if (password != null) {
        UpdatePasswordDetails updater = new UpdatePasswordDetails(password);
        runOnUiThread(updater);
      } else {
        // Update GUI about failure!!
        runOnUiThread(new FailureNotification("Password Unaccessible"));
      }
    }
  }

  private class PasswordSaved implements Runnable {
    @Override
    public void run() {
      mLoadingDialog.dismiss();
      setResult(1);
      finish();
    }
  }

  private final class SaveButtonListener implements View.OnClickListener {

    @Override
    public void onClick(View v) {
      // Save changes and go back to initial screen!
      Password toBeSaved =
          new Password(mTitleText.getText().toString(), mUsernameText.getText().toString(),
              mPasswordText.getText().toString(), mNotesText.getText().toString());
      mLoadingDialog =
          ProgressDialog
              .show(PassgoriEditPasswordActivity.this, "", "Saving. Please wait...", true);
      new SavePassword(toBeSaved).start();

    }
  }

  private class SavePassword extends Thread {

    private final Password mPassword;

    public SavePassword(Password password) {
      mPassword = password;
    }

    @Override
    public void run() {
      try {

        mPasswordStore.storePassword(mPassword);
        // But if we changed the title, then we have to delete the old
        // id password too!
        if (getIntent().getExtras() != null) {
          String delteId = getIntent().getExtras().getString("passwordId");
          if (!mPassword.getId().equals(delteId)) {// Only delete it if we didn't just add it.
            mPasswordStore.removePassword(delteId);
          }
        }
        runOnUiThread(new PasswordSaved());
      } catch (PasswordStoreException e) {
        runOnUiThread(new FailureNotification(e.getMessage()));
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
      if (mPassword != null) {
        mTitleText.setText(mPassword.getId());
        mUsernameText.setText(mPassword.getUsername());
        mPasswordText.setText(mPassword.getPassword());
        mNotesText.setText(mPassword.getNotes());
      }

      mLoadingDialog.dismiss();
    }

  }

  private Button mButtonSave;
  private Button mButtonCancel;
  private TextView mTitleText;

  private TextView mUsernameText;

  private TextView mPasswordText;

  private TextView mNotesText;

  protected IPasswordStore mPasswordStore;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.password_edit);

    mButtonSave = (Button) findViewById(R.id.saveButton);
    mButtonCancel = (Button) findViewById(R.id.cancelButton);

    mButtonSave.setOnClickListener(new SaveButtonListener());
    mButtonCancel.setOnClickListener(new CancelButtonListener());

    mTitleText = (TextView) findViewById(R.id.passwordTitleEdit);
    mUsernameText = (TextView) findViewById(R.id.usernameEdit);
    mPasswordText = (TextView) findViewById(R.id.passwordEdit);
    mNotesText = (TextView) findViewById(R.id.notesEdit);

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
    connect();
  }

  @Override
  protected void displayError(String errorMessage) {
    Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();
  }

  @Override
  protected void onConnected() throws PasswordStoreException {
    mPasswordStore = binder.getStore();

    // Spawn thread to get password details, if any!
    new GetPassword().start();
  }

}
