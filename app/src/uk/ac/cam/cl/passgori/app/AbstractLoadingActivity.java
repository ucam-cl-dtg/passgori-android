package uk.ac.cam.cl.passgori.app;

import uk.ac.cam.cl.passgori.PasswordStoreException;
import uk.ac.cam.cl.passgori.app.PasswordStoreService.PasswordStorageBinder;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

public abstract class AbstractLoadingActivity extends Activity {
  public ProgressDialog mLoadingDialog;
  protected class FailureNotification implements Runnable {

    private final String mMessage;

    public FailureNotification(String message) {
      mMessage = message;
    }

    public FailureNotification(Exception e) {
      mMessage = e.toString();
      e.printStackTrace();
    }

    @Override
    public void run() {
      Log.e(this.getClass().getCanonicalName(), mMessage);
      if (mLoadingDialog != null){
        mLoadingDialog.dismiss();
      }
      displayError(mMessage);
    }

  }
  protected abstract void displayError(String errorMessage);
  
  protected boolean connected = false;
  protected PasswordStorageBinder binder;
  /** Defines callbacks for service binding, passed to bindService() */
  protected final ServiceConnection mConnection = new ServiceConnection() {

    @Override
    public void onServiceConnected(ComponentName className, IBinder service) {
      binder = (PasswordStorageBinder) service;
      connected = true;
      if (mLoadingDialog != null) {
        mLoadingDialog.dismiss();
      }
      try {
        onConnected();
      } catch (PasswordStoreException e) {
        runOnUiThread(new FailureNotification(e));
      }

    }

    @Override
    public void onServiceDisconnected(ComponentName arg0) {
      connected = false;
    }
  };
  protected abstract void onConnected() throws PasswordStoreException;
}
