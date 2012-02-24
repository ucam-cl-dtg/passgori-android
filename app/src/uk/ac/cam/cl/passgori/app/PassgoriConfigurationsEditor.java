/*
 * Copyright 2011 Miltiadis Allamanis
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 
/**
 * 
 */
package uk.ac.cam.cl.passgori.app;

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
