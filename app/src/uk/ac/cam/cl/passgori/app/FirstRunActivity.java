package uk.ac.cam.cl.passgori.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class FirstRunActivity extends Activity {
  private Button mNewUserButton;
  private Button mHaveRemoteButton;
  private Button mHaveBackupButton;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.first_run);

    mNewUserButton = (Button) findViewById(R.id.newUserButton);
    mNewUserButton.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        finish();
        Intent configure = new Intent(FirstRunActivity.this, PassgoriConfigurationEditorActivity.class);
        configure.setAction(PassgoriConfigurationEditorActivity.NEW_USER);
        startActivity(configure);
      }
    });
    mHaveRemoteButton = (Button) findViewById(R.id.haveRemoteButton);
    mHaveRemoteButton.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        finish();
        Intent configure = new Intent(FirstRunActivity.this, PassgoriConfigurationEditorActivity.class);
        configure.setAction(PassgoriConfigurationEditorActivity.EXISTING_REMOTE);
        startActivity(configure);
      }
    });
    mHaveBackupButton = (Button) findViewById(R.id.haveBackupButton);
    mHaveBackupButton.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        finish();
        // TODO(drt24) create restore activity
      }
    });
  }
}
