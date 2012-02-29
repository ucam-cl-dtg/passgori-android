/*
 * Copyright 2012 Daniel Thomas (drt24)
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import uk.ac.cam.cl.passgori.IPasswordStore;
import uk.ac.cam.cl.passgori.PasswordStoreException;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.nigori.common.NigoriCryptographyException;
import com.google.nigori.common.UnauthorisedException;

public class PassgoriBackupActivity extends AbstractLoadingActivity {

  private static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
  private static final String LOG_TAG = PassgoriBackupActivity.class.getSimpleName();

  private Button mBackupButton;

  /**
   * The password store.
   */
  private IPasswordStore mPasswordStore;

  private EditText mPasswordField;

  private TextView mBackupDestinationField;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.backup);

    String state = Environment.getExternalStorageState();

    try {
      final boolean canRead;
      final boolean canWrite;
      if (Environment.MEDIA_MOUNTED.equals(state)) {
        canRead = canWrite = true;
      } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
        canRead = true;
        canWrite = false;
      } else {
        canRead = canWrite = false;
      }
      if (canRead) {
        File backupsDir =
            new File(new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/Android/data/uk.ac.cam.cl.passgori.app/files/"), "backups");
        if (canWrite && !backupsDir.exists()) {
          backupsDir.mkdirs();
        }
        if (!backupsDir.exists()) {
          throw new IOException("Backups dir does not exist: " + backupsDir.getAbsolutePath());
        }
        if (canWrite) {
          initaliseBackupView(backupsDir);
        }
        initaliseRestoreView(backupsDir);
      } else {
        runOnUiThread(new FailureNotification(
            "External storage not writable so cannot backup to it"));
      }
    } catch (IOException e1) {
      runOnUiThread(new FailureNotification(e1));
    }
  }

  private void initaliseRestoreView(final File backupsDir) {
    final String[] files = backupsDir.list(new FilenameFilter() {

      @Override
      public boolean accept(File dir, String filename) {
        return filename.endsWith(".bak");
      }
    });
    ListView list = (ListView) findViewById(R.id.restoreList);
    list.setAdapter(new ArrayAdapter<String>(this, R.layout.restore_list_item, files));
    list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        restore(backupsDir, files[position]);
      }
    });
  }

  protected void restore(File backupsDir, String filename) {
    try {
      mPasswordStore.restore(new FileInputStream(new File(backupsDir, filename)), mPasswordField
          .getText().toString());
      Toast.makeText(getApplicationContext(), "Restore complete", Toast.LENGTH_LONG).show();

      finish();// Close the backup activity
    } catch (IOException e) {
      runOnUiThread(new FailureNotification("Restore failed: " + e.toString()));
    } catch (NigoriCryptographyException e) {
      runOnUiThread(new FailureNotification("Restore failed: " + e.toString()));
    } catch (UnauthorisedException e) {
      runOnUiThread(new FailureNotification("Restore failed: " + e.toString()));
    } catch (ClassNotFoundException e) {
      runOnUiThread(new FailureNotification("Restore failed: " + e.toString()));
    }
  }

  private void initaliseBackupView(File backupsDir) throws IOException {
    final File backupDestination =
        new File(backupsDir, "passgori." + format.format(new Date()) + ".bak");
    mBackupDestinationField = (TextView) findViewById(R.id.backupDestination);
    mBackupDestinationField.setText(backupDestination.getAbsolutePath());
    mPasswordField = (EditText) findViewById(R.id.passwordField);

    mBackupButton = (Button) findViewById(R.id.backupButton);
    mBackupButton.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View arg0) {
        try {
          Log.d("PassgoriBackupActivity", "starting backup");
          Log.d(LOG_TAG, "Creating file: " + backupDestination.getAbsolutePath());
          backupDestination.createNewFile();
          String password = mPasswordField.getText().toString();
          mPasswordStore.backup(new FileOutputStream(backupDestination), password);
          Log.d(LOG_TAG, "finished backup");
          Toast.makeText(getApplicationContext(), "Backup complete", Toast.LENGTH_LONG).show();

          finish();// Close the backup activity

        } catch (FileNotFoundException e) {
          runOnUiThread(new FailureNotification(e));
        } catch (IOException e) {
          runOnUiThread(new FailureNotification(e));
        } catch (NigoriCryptographyException e) {
          runOnUiThread(new FailureNotification(e));
        } catch (UnauthorisedException e) {
          runOnUiThread(new FailureNotification(e));
        }
      }
    });
  }

  @Override
  protected void onStart() {
    super.onStart();

    // Bind to PasswordStoreService
    Intent intent = new Intent(this, PasswordStoreService.class);

    if (!connected) {
      mLoadingDialog =
          ProgressDialog.show(PassgoriBackupActivity.this, "", "Connecting. Please wait...", true);
    }
    if (!getApplicationContext().bindService(intent, mConnection, Context.BIND_AUTO_CREATE)) {
      String error = getString(R.string.serviceBindError);
      new FailureNotification(error).run();
    }
  }

  @Override
  protected void displayError(String errorMessage) {
    Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();
  }

  @Override
  protected void onConnected() throws PasswordStoreException {
    mPasswordStore = binder.getStore();
  }

}
