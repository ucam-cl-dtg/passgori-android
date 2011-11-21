/**
 * 
 */
package ac.uk.cam.cl.passgroiApp;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

/**
 * An activity presenting the a password entity.
 * 
 * @author Miltiadis Allamanis
 * 
 */
public class PassgoriPresentPasswordsActivity extends Activity {

	/**
	 * TODO: Remove
	 */
	private String currentPasswordTitle;

	/**
	 * The TextView corresponding to the password's title.
	 */
	private TextView mPasswordTitle;

	/**
	 * The TextView corresponding to the paswords's username.
	 */
	private TextView mUsernameField;

	/**
	 * The TextView corresponding to the password's password.
	 */
	private TextView mPasswordField;

	/**
	 * The TextView corresponding to the password's notes.
	 */
	private TextView mNotesField;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.password_present);

		// Assign content
		mPasswordTitle = (TextView) findViewById(R.id.passwordTitle);
		mUsernameField = (TextView) findViewById(R.id.usernameField);
		mPasswordField = (TextView) findViewById(R.id.passwordField);
		mNotesField = (TextView) findViewById(R.id.notesField);

		currentPasswordTitle = getIntent().getExtras().getString("passwordId");
	}

	@Override
	public void onStop() {
		super.onStop();
		// Close activity to avoid storing the password if someone exits us from
		// the home button
		finish();
	}

}
