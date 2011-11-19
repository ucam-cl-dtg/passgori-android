/**
 * 
 */

/**
 * A class representing password information
 * 
 * @author Miltiadis Allamanis
 * 
 */
public class Password {

	private final String mId;

	private final String mUsername;
	private final String mPassword;

	private final String mNotes;

	/**
	 * A copy constructor.
	 * 
	 * @param object
	 *            the object to copy.
	 */
	public Password(Password object) {
		mId = object.mId;
		mUsername = object.mUsername;
		mPassword = object.mPassword;
		mNotes = object.mNotes;
	}

	/**
	 * Password object constructor.
	 * 
	 * @param aId
	 *            the password unique identifier
	 * @param aUsername
	 *            the username
	 * @param aPassword
	 *            the password
	 * @param aNotes
	 *            notes
	 */
	public Password(String aId, String aUsername, String aPassword,
			String aNotes) {
		mId = aId;
		mUsername = aUsername;
		mPassword = aPassword;
		mNotes = aNotes;
	}

	public final String getId() {
		return mId;
	}

	public final String getNotes() {
		return mNotes;
	}

	public final String getPassword() {
		return mPassword;
	}

	public final String getUsername() {
		return mUsername;
	}

}
