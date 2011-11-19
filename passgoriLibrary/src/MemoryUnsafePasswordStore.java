import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * 
 */

/**
 * A memory unsafe password store.
 * 
 * @author Miltiadis Allamanis
 * 
 */
public class MemoryUnsafePasswordStore implements IPasswordStore {

	/**
	 * A hash map storing the passwords.
	 */
	private final HashMap<String, Password> mPasswordStore;

	public MemoryUnsafePasswordStore() {
		mPasswordStore = new HashMap<String, Password>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see IPasswordStore#getAllStoredPasswordIds()
	 */
	@Override
	public List<String> getAllStoredPasswordIds() throws PasswordStoreException {
		final Set<String> idSet = mPasswordStore.keySet();
		return new ArrayList<String>(idSet);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see IPasswordStore#removePassword(java.lang.String)
	 */
	@Override
	public boolean removePassword(String aId) throws PasswordStoreException {
		return mPasswordStore.remove(aId) != null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see IPasswordStore#retrivePassword(java.lang.String)
	 */
	@Override
	public Password retrivePassword(String aId) throws PasswordStoreException {
		return mPasswordStore.get(aId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see IPasswordStore#storePassword(Password)
	 */
	@Override
	public boolean storePassword(Password aPassword)
			throws PasswordStoreException {

		return mPasswordStore.put(aPassword.getId(), aPassword) != null;
	}

}
