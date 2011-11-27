/**
 * 
 */
package ac.uk.cam.cl.passgroiApp;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * An activity for showing and editing Passgori configurations.
 * 
 * @author Miltiadis Allamanis
 * 
 */
public class PassgoriConfigurationsEditor extends Activity {

	/**
	 * The configurations object.
	 */
	private PassgoriConfigurations mConfigs;

	private TextView mUsernameField;
	private TextView mServerField;
	private TextView mPortField;
	private TextView mPrefixField;
	private Button mSaveButton;
	private Button mCancelButton;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.configurations);

		mUsernameField = (TextView) findViewById(R.id.passgoriUsername);
		mServerField = (TextView) findViewById(R.id.passgoriServer);
		mPortField = (TextView) findViewById(R.id.passgoriPort);
		mPrefixField = (TextView) findViewById(R.id.passgoriServerPrefix);
		mSaveButton = (Button) findViewById(R.id.saveButton);
		mCancelButton = (Button) findViewById(R.id.cancelButton);

		mConfigs = new PassgoriConfigurations(this);

		mSaveButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				saveConfig();
				finish();
			}
		});

		mCancelButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
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
	}

	@Override
	protected void onStart() {
		super.onStart();
		mUsernameField.setText(mConfigs.getUsername());
		mServerField.setText(mConfigs.getServer());
		mPortField.setText(mConfigs.getPort() + "");
		mPrefixField.setText(mConfigs.getServerPrefix());
	}
}
