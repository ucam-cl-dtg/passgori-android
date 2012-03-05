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
import uk.ac.cam.cl.passgori.NigoriPasswordStore;
import uk.ac.cam.cl.passgori.PasswordStoreException;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;

/**
 * A Password store service used to access the password store from various activities.
 * 
 * @author Miltiadis Allamanis
 * 
 */
public class PasswordStoreService extends Service {

  /**
   * Class used for the client Binder.
   */
  public class PasswordStorageBinder extends Binder {
    public void connectStore(String password) throws PasswordStoreException {
      Context context = getBaseContext();
      PassgoriConfiguration pc = new PassgoriConfiguration(context);
      mPasswordStore =
          new NigoriPasswordStore(context.getFilesDir(), pc.getUsername(), password, pc
              .getServer(), pc.getPort(), pc.getServerPrefix());
      // TODO: dynamically create type based on stored parameters
      // (?)
      IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);

      BroadcastReceiver mReceiver = new ScreenReceiver();
      registerReceiver(mReceiver, filter);

    }

    public void createStore(boolean localOnly) throws PasswordStoreException {
      if (!mPasswordStore.createStore(localOnly)) {
        throw new PasswordStoreException("Store not successfully created");
      }
    }

    /**
     * Return the unique store provided by the service.
     * 
     * @return the password store
     * @throws PasswordStoreException
     */
    IPasswordStore getStore() throws PasswordStoreException {
      // TODO: Add timeout for security purposes
      return mPasswordStore;
    }
  }

  /**
   * The password store being used.
   */
  IPasswordStore mPasswordStore = null;

  // Binder given to clients
  private final IBinder mBinder = new PasswordStorageBinder();

  @Override
  public IBinder onBind(Intent intent) {
    return mBinder;
  }

  private class ScreenReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
      if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
        mPasswordStore = null;
        stopSelf();
      }
    }

  }
}
