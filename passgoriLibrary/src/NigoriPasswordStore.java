import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.AbstractList;
import java.util.ArrayList;

import com.google.nigori.client.NigoriCryptographyException;
import com.google.nigori.client.NigoriDatastore;

/**
 * 
 */

/**
 * A Nigori Password Store
 * 
 * @author Miltiadis Allamanis
 * 
 */
public class NigoriPasswordStore implements IPasswordStore {

	/**
	 * The Nigori client instance.
	 */
	private final NigoriDatastore mNigoriStore;

	private final String mUserName;

	private static final String USERNAME_PREFIX = "_username";
	private static final String PASSWORD_PREFIX = "_password";
	private static final String NOTES_PREFIX = "_notes";
	private static final String NEXT_PREFIX = "_next";
	private static final String PREV_PREFIX = "_prev";
	private static final String PASS_HEAD_PREFIX = "_passHead";

	/**
	 * Default Nigori Password Store example.
	 * 
	 * @param username
	 *            the username of the Nigori server
	 * @param password
	 *            the password of the Nigori user
	 * @param serverURI
	 *            the URI of the server
	 * @param portNumber
	 *            the port number on the Nigori server
	 * @param serverPrefix
	 *            a server prefix
	 * @throws IOException
	 * @throws NigoriCryptographyException
	 */
	public NigoriPasswordStore(final String username, final String password,
			final String serverURI, final int portNumber,
			final String serverPrefix) throws IOException,
			NigoriCryptographyException {
		mNigoriStore = new NigoriDatastore(serverURI, portNumber, serverPrefix,
				username, password);
		mUserName = username;
		register();
	}

	@Override
	public AbstractList<String> getAllStoredPasswordIds() {
		AbstractList<String> passwordIds = new ArrayList<String>();

		byte[] response = null;
		try {
			response = mNigoriStore.get(getPassHeadKey().getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NigoriCryptographyException e) {
			e.printStackTrace();
		}

		if (response != null) {
			String passHeadId = new String(response);

			while (passHeadId != null) {
				passwordIds.add(passHeadId);
				passHeadId = getNextHead(passHeadId);
			}
		}
		return passwordIds;
	}

	@Override
	public boolean removePassword(String aId) {
		try {
			final byte[] previousId = mNigoriStore.get(getPrevKey(aId)
					.getBytes());
			final byte[] nextId = mNigoriStore.get(getNextKey(aId).getBytes());

			if ((previousId != null) && (nextId != null)) {
				mNigoriStore.put(getNextKey(new String(previousId)).getBytes(),
						nextId);
				mNigoriStore.put(getPrevKey(new String(nextId)).getBytes(),
						previousId);
			} else if ((nextId == null) && (previousId == null)) {
				mNigoriStore.delete(getPassHeadKey().getBytes());
			} else if (nextId == null) {
				mNigoriStore.delete(getNextKey(new String(previousId))
						.getBytes());
			} else if (previousId == null) {
				mNigoriStore.delete(getPrevKey(new String(nextId)).getBytes());
				mNigoriStore.put(getPassHeadKey().getBytes(), nextId);
			}

			return (mNigoriStore.delete(getUsernameKey(aId).getBytes())
					&& mNigoriStore.delete(getPasswordKey(aId).getBytes()) && mNigoriStore
						.delete(getNotesKey(aId).getBytes()));
		} catch (UnsupportedEncodingException e) {
			return false;
		} catch (NigoriCryptographyException e) {
			return false;
		} catch (IOException e) {
			return false;
		}

	}

	@Override
	public Password retrivePassword(String aId) {

		byte[] username = null;
		byte[] password = null;
		byte[] notes = null;
		try {
			username = mNigoriStore.get(getUsernameKey(aId).getBytes());
			password = mNigoriStore.get(getPasswordKey(aId).getBytes());
			notes = mNigoriStore.get(getNotesKey(aId).getBytes());
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} catch (NigoriCryptographyException e) {
			e.printStackTrace();
			return null;
		}

		if ((username != null) && (password != null) && (notes != null)) {
			return new Password(aId, new String(username),
					new String(password), new String(notes));
		}
		return null;
	}

	@Override
	public boolean storePassword(Password aPassword) {
		try {
			final byte[] oldHeadBytes = mNigoriStore.get(getPassHeadKey()
					.getBytes());
			if (oldHeadBytes != null) {
				// Store next on linked list
				mNigoriStore.put(getNextKey(aPassword.getId()).getBytes(),
						oldHeadBytes);

				String oldHead = new String(oldHeadBytes);
				// Store prev key
				mNigoriStore.put(getPrevKey(oldHead).getBytes(), aPassword
						.getId().getBytes());
			}

			// Store username
			mNigoriStore.put(getUsernameKey(aPassword.getId()).getBytes(),
					aPassword.getUsername().getBytes());

			// Store password
			mNigoriStore.put(getPasswordKey(aPassword.getId()).getBytes(),
					aPassword.getPassword().getBytes());

			// Store password
			mNigoriStore.put(getNotesKey(aPassword.getId()).getBytes(),
					aPassword.getNotes().getBytes());

			// Store next key
			mNigoriStore.put(getPassHeadKey().getBytes(), aPassword.getId()
					.getBytes());

		} catch (IOException e) {
			return false;
		} catch (NigoriCryptographyException e) {
			return false;
		}
		return true;
	}

	private String getNextHead(final String currentHead) {
		final String key = getNextKey(currentHead);
		byte[] response = null;
		try {
			response = mNigoriStore.get(key.getBytes());
		} catch (IOException e) {
			return null;
		} catch (NigoriCryptographyException e) {
			return null;
		}
		if (response == null)
			return null;
		return new String(response);
	}

	private final String getNextKey(final String passwordId) {
		return mUserName + "_" + passwordId + NEXT_PREFIX;
	}

	private final String getNotesKey(final String passwordId) {
		return mUserName + "_" + passwordId + NOTES_PREFIX;
	}

	private final String getPassHeadKey() {
		return mUserName + PASS_HEAD_PREFIX;
	}

	private final String getPasswordKey(final String passwordId) {
		return mUserName + "_" + passwordId + PASSWORD_PREFIX;
	}

	private final String getPrevKey(final String passwordId) {
		return mUserName + "_" + passwordId + PREV_PREFIX;
	}

	private final String getUsernameKey(final String passwordId) {
		return mUserName + "_" + passwordId + USERNAME_PREFIX;
	}

	private boolean register() throws IOException, NigoriCryptographyException {
		return mNigoriStore.register();
	}

}
