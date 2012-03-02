package uk.ac.cam.cl.passgori.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class StartActivity extends Activity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    finish();
    // TODO(drt24) deal with the other cases
    PassgoriConfiguration pc = new PassgoriConfiguration(this);

    if (!pc.isConfigured()) {// If we are not already configured
      Intent firstRun = new Intent(this, FirstRunActivity.class);
      startActivity(firstRun);
    } else {
      Intent unlock = new Intent(this, PassgoriUnlockActivity.class);
      startActivity(unlock);
    }
  }
}
