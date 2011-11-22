/**
 * 
 */
package ac.uk.cam.cl.passgroiApp;

import android.app.Activity;
import android.os.Bundle;

/**
 * An activity permitting to edit or add new passwords.
 * 
 * @author Miltiadis Allamanis
 * 
 */
public class PassgoriEditPasswordActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.password_edit);

	}

}
