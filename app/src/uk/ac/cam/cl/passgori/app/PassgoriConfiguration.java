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

import android.content.Context;
import android.content.SharedPreferences;

/**
 * A class for managing the configurations
 * 
 * @author Miltiadis Allamanis
 * 
 */
public class PassgoriConfiguration {
  /**
   * Preferences name.
   */
  public static final String PREFS_NAME = "PassgoriPrefs";

  /**
   * The shared preferences object.
   */
  private final SharedPreferences mSettings;

  /**
   * Constructor for the configurations object.
   * 
   * @param context the context to get preferences from
   */
  public PassgoriConfiguration(Context context) {
    mSettings = context.getSharedPreferences(PREFS_NAME, 0);
  }

  /**
   * 
   * @return the stored port (default 443)
   */
  public final int getPort() {
    return mSettings.getInt("passgoriPort", 443);
  }

  /**
   * 
   * @return the stored server, "" if none
   */
  public final String getServer() {
    return mSettings.getString("passgoriServer", "nigori-dev.appspot.com");
  }

  /**
   * 
   * @return the stored server prefix (default nigori)
   */
  public final String getServerPrefix() {
    return mSettings.getString("passgoriServerPrefix", "nigori");
  }

  /**
   * 
   * @return the stored username, "" if none
   */
  public final String getUsername() {
    return mSettings.getString("passgoriUsername", null);
  }

  public final boolean getUseRemoteStore() {
    return mSettings.getBoolean("passgoriUseRemoteStore", true);
  }

  /**
   * Stores the port configuration.
   * 
   * @param port the port
   */
  public void setPort(final int port) {
    SharedPreferences.Editor editor = mSettings.edit();

    editor.putInt("passgoriPort", port);
    editor.commit();
  }

  /**
   * Stores the server configuration.
   * 
   * @param server the server
   */
  public void setServer(final String server) {
    SharedPreferences.Editor editor = mSettings.edit();

    editor.putString("passgoriServer", server);
    editor.commit();
  }

  /**
   * Stores the sever prefix configuration.
   * 
   * @param perfix the prefix
   */
  public void setServerPrefix(final String perfix) {
    SharedPreferences.Editor editor = mSettings.edit();

    editor.putString("passgoriServerPrefix", perfix);
    editor.commit();
  }

  /**
   * Stores the username configuration.
   * 
   * @param username the username
   */
  public void setUsername(final String username) {
    SharedPreferences.Editor editor = mSettings.edit();

    editor.putString("passgoriUsername", username);
    editor.commit();
  }

  public void setUseRemoteStore(final boolean useRemote) {
    SharedPreferences.Editor editor = mSettings.edit();

    editor.putBoolean("passgoriUseRemoteStore", useRemote);
    editor.commit();
  }

  public boolean isConfigured() {
    String username = getUsername();
    return !(username == null || username.length() == 0);
  }
}
