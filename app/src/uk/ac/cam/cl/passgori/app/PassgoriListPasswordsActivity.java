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

package uk.ac.cam.cl.passgori.app;

import java.util.List;

import uk.ac.cam.cl.passgori.IPasswordStore;
import uk.ac.cam.cl.passgori.PasswordStoreException;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * The main password listing activity.
 * 
 * @author Miltiadis Allamanis
 * 
 */
public class PassgoriListPasswordsActivity extends AbstractLoadingActivity {

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
          final ArrayAdapter<String> adapter =
              new ArrayAdapter<String>(PassgoriListPasswordsActivity.this,
                  R.layout.password_list_item, passwordList);

          runOnUiThread(new UpdateListRunnable(adapter));
        } else {
          runOnUiThread(new FailureNotification(getString(R.string.noPasswordsError)));
        }

      } catch (final PasswordStoreException e) {
        runOnUiThread(new FailureNotification(e));
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
        throw new PasswordStoreException("Password Store instance not instantiated");
      return mPasswordStore.getAllStoredPasswordIds();

    }

  }

  /**
   * Runnable to update the list on the GUI
   * 
   */
  private class UpdateListRunnable implements Runnable {
    private final class PasswordListClickListener implements AdapterView.OnItemClickListener {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String passwordId = ((TextView) view).getText().toString();
        Intent intent = new Intent(view.getContext(), PassgoriPresentPasswordsActivity.class);
        intent.putExtra("passwordId", passwordId);
        startActivityForResult(intent, 0);
      }
    }

    /**
     * The password List
     */
    private final ArrayAdapter<String> mPasswordAdapter;
    private ListView mPasswordListView;

    public UpdateListRunnable(final ArrayAdapter<String> passwordAdapter) {
      mPasswordAdapter = passwordAdapter;

    }

    @Override
    public void run() {
      mPasswordListView = (ListView) findViewById(R.id.passwordList);
      mPasswordListView.setOnItemClickListener(new PasswordListClickListener());
      mPasswordListView.setAdapter(mPasswordAdapter);
      mLoadingDialog.dismiss();
    }

  }

  private static final String LOG_TAG = PassgoriListPasswordsActivity.class.getCanonicalName();

  /**
   * The password store.
   */
  private IPasswordStore mPasswordStore;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.password_list);

  }

  @Override
  protected void onStart() {
    super.onStart();
    connectAndGo();
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
        Intent configIntent = new Intent(this, PassgoriConfigurationEditorActivity.class);
        configIntent.setAction(PassgoriConfigurationEditorActivity.CONFIGURE);
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
    mLoadingDialog = ProgressDialog.show(this, "", "Refreshing. Please wait...", true);
    new UpdateList().start();
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (resultCode > 0)
      refreshPasswordList();
    super.onActivityResult(requestCode, resultCode, data);
  }

  /**
   * When we come back from doing a backup/restore then we need to reflect any changes that there
   * were.
   */
  @Override
  public void finishFromChild(Activity child) {
    if (child instanceof PassgoriBackupActivity) {
      refreshPasswordList();
    }
    super.finishFromChild(child);
  }

  @Override
  protected void displayError(String errorMessage) {
    Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();
  }

  @Override
  protected void onConnected() throws PasswordStoreException {
    Log.d(LOG_TAG, "connected, setting store and updating list");
    mPasswordStore = binder.getStore();
    mLoadingDialog = ProgressDialog.show(this, "", "Loading password list...", true);
    new UpdateList().start();

  }

}