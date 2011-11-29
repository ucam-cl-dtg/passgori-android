/**
 * 
 */
package ac.uk.cam.cl.passgroiApp;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * A class for managing the configurations
 * 
 * @author Miltiadis Allamanis
 * 
 */
public class PassgoriConfigurations {
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
	 * @param context
	 *            the context to get preferences from
	 */
	public PassgoriConfigurations(Context context) {
		mSettings = context.getSharedPreferences(PREFS_NAME, 0);
	}

	/**
	 * 
	 * @return the stored port (default 80)
	 */
	public final int getPort() {
		return mSettings.getInt("passgoriPort", 80);
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
		return mSettings.getString("passgoriUsername", "nobody");
	}

	/**
	 * Stores the port configuration.
	 * 
	 * @param port
	 *            the port
	 */
	public void setPort(final int port) {
		SharedPreferences.Editor editor = mSettings.edit();

		editor.putInt("passgoriPort", port);
		editor.commit();
	}

	/**
	 * Stores the server configuration.
	 * 
	 * @param server
	 *            the server
	 */
	public void setServer(final String server) {
		SharedPreferences.Editor editor = mSettings.edit();

		editor.putString("passgoriServer", server);
		editor.commit();
	}

	/**
	 * Stores the sever prefix configuration.
	 * 
	 * @param perfix
	 *            the prefix
	 */
	public void setServerPrefix(final String perfix) {
		SharedPreferences.Editor editor = mSettings.edit();

		editor.putString("passgoriServerPrefix", perfix);
		editor.commit();
	}

	/**
	 * Stores the username configuration.
	 * 
	 * @param username
	 *            the username
	 */
	public void setUsername(final String username) {
		SharedPreferences.Editor editor = mSettings.edit();

		editor.putString("passgoriUsername", username);
		editor.commit();
	}

}
