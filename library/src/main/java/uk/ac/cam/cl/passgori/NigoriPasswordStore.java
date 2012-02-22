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
 
package uk.ac.cam.cl.passgori;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import com.google.nigori.client.CryptoNigoriDatastore;
import com.google.nigori.client.DAG;
import com.google.nigori.client.HashMigoriDatastore;
import com.google.nigori.client.LocalFirstSyncingNigoriDatastore;
import com.google.nigori.client.MigoriDatastore;
import com.google.nigori.client.NigoriDatastore;
import com.google.nigori.client.SyncingNigoriDatastore;
import com.google.nigori.common.Index;
import com.google.nigori.common.NigoriCryptographyException;
import com.google.nigori.common.RevValue;
import com.google.nigori.common.Revision;
import com.google.nigori.common.UnauthorisedException;
import com.google.nigori.server.DatabaseNigoriProtocol;
import com.google.nigori.server.HashMapDatabase;
import com.google.nigori.server.JEDatabase;

/**
 * 
 */

/**
 * A Nigori Password Store.
 * 
 * The passwords are kept in a double linked list on the nigori password store.
 * Each user has associated a head of each list.
 * 
 * @author Miltiadis Allamanis
 * 
 */
public class NigoriPasswordStore implements IPasswordStore {

	/**
	 * The Nigori client instance.
	 */
	private MigoriDatastore mMigoriStore;
	private NigoriDatastore mNigoriStore;

	/**
	 * The username of the nigori password store
	 */
	private final String mUserName;

	private final String mServerPrefix;

	private final String mServerURI;

	private final int mPortNumber;

  private File mDir;

	/**
	 * Constructor that creates the store but performs no authorization.
	 * 
	 * @param serverURI
	 *            the URI of the server
	 * @param portNumber
	 *            the port number on the Nigori server
	 * @param serverPrefix
	 *            a server prefix
	 */
	public NigoriPasswordStore(final String username, final String serverURI,
			final int portNumber, final String serverPrefix) {
		mPortNumber = portNumber;
		mServerPrefix = serverPrefix;
		mServerURI = serverURI;
		mUserName = username;
	}

	/**
	 * Nigori Password Store Constructor, that automatically performs
	 * authorization.
	 * @param dir directory which we can store the local database in 
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
	 * @throws PasswordStoreException
	 */
	public NigoriPasswordStore(File dir, final String username, final String password,
			final String serverURI, final int portNumber,
			final String serverPrefix) throws PasswordStoreException {
	  mDir = dir;
		mPortNumber = portNumber;
		mServerPrefix = serverPrefix;
		mServerURI = serverURI;
		mUserName = username;

		authorize(mUserName, password);

	}

	@Override
	public boolean authorize(String username, String password)
	    throws PasswordStoreException {
	  boolean authenticated = false;
	  try {
	    File jeDir = new File(mDir,"je-database/");
	    if (!jeDir.exists()){
	      if (!jeDir.mkdir()) {
	        throw new PasswordStoreException("Could not create dabase folder: " + jeDir);
	      }
      }
      mNigoriStore =
          new LocalFirstSyncingNigoriDatastore(new CryptoNigoriDatastore(
              new DatabaseNigoriProtocol(new JEDatabase(jeDir)), username, password, "je"),
              new CryptoNigoriDatastore(mServerURI, mPortNumber, mServerPrefix, username, password));
      mMigoriStore = new HashMigoriDatastore(mNigoriStore);

	    authenticated = mMigoriStore.authenticate();
	    if (!authenticated)
	      authenticated = register();
	  } catch (Exception e) {
	    throw new PasswordStoreException(e);
	  }

		return authenticated;
	}

	@Override
	public List<String> getAllStoredPasswordIds() throws PasswordStoreException {
		try {
		  List<String> ids = new ArrayList<String>();
		  for (Index idx : mMigoriStore.getIndices()){
		    ids.add(idx.toString());
		  }
		  return ids;
		} catch (IOException e) {
			throw new PasswordStoreException(e);
		} catch (NigoriCryptographyException e) {
			throw new PasswordStoreException(e);
		} catch (UnauthorisedException e) {
		  throw new PasswordStoreException(e);
    }
	}

  @Override
  public boolean removePassword(String aId) throws PasswordStoreException {
    try {
      Index index = new Index(aId);
      RevValue current = mMigoriStore.getMerging(index, new PasswordMerger());
      if (current != null) {
        return mMigoriStore.removeIndex(index, current.getRevision());
      } else {
        return mMigoriStore.removeIndex(index, Revision.EMPTY);
      }

    } catch (NigoriCryptographyException e) {
      throw new PasswordStoreException(e);
    } catch (IOException e) {
      throw new PasswordStoreException(e);
    } catch (UnauthorisedException e) {
      throw new PasswordStoreException(e);
    }
  }

  @Override
  public Password retrivePassword(String aId) throws PasswordStoreException {
    try {
      RevValue current = mMigoriStore.getMerging(new Index(aId), new PasswordMerger());
      if (current == null) {
        return null;
      }
      return new Password(aId, current.getValue());
    } catch (IOException e) {
      throw new PasswordStoreException(e);
    } catch (NigoriCryptographyException e) {
      throw new PasswordStoreException(e);
    } catch (UnauthorisedException e) {
      throw new PasswordStoreException(e);
    }
  }

	@Override
  public Password retrivePassword(String index, Revision revision) throws PasswordStoreException {
    try {
      return new Password(index, mMigoriStore.getRevision(new Index(index), revision));
    } catch (IOException e) {
      throw new PasswordStoreException(e);
    } catch (NigoriCryptographyException e) {
      throw new PasswordStoreException(e);
    } catch (UnauthorisedException e) {
      throw new PasswordStoreException(e);
    }
  }

  @Override
	public boolean storePassword(Password aPassword)
			throws PasswordStoreException {
		try {
		  Index index = new Index(aPassword.getId());
      RevValue current = mMigoriStore.getMerging(index, new PasswordMerger());
      if (current != null) {
        mMigoriStore.put(index, aPassword.toBytes(), current);
      } else {
        mMigoriStore.put(index, aPassword.toBytes());
      }
		} catch (IOException e) {
		  throw new PasswordStoreException(e);
		} catch (NigoriCryptographyException e) {
		  throw new PasswordStoreException(e);
		} catch (UnauthorisedException e) {
		  throw new PasswordStoreException(e);
    }
		return true;
	}

	/**
	 * Register the user.
	 * 
	 * @return
	 * @throws IOException
	 * @throws NigoriCryptographyException
	 */
	private boolean register() throws IOException, NigoriCryptographyException {
		return mMigoriStore.register();
	}
	private boolean unregister() throws IOException, NigoriCryptographyException, UnauthorisedException {
	  return mMigoriStore.unregister();
	}

  @Override
  public boolean destroyStore() throws PasswordStoreException {
    try {
      return unregister();
    } catch (NigoriCryptographyException e) {
      throw new PasswordStoreException(e);
    } catch (IOException e) {
      throw new PasswordStoreException(e);
    } catch (UnauthorisedException e) {
      throw new PasswordStoreException(e);
    }
  }

  @Override
  public DAG<Revision> getHistory(String passwordId) throws PasswordStoreException {
    try {
      return mMigoriStore.getHistory(new Index(passwordId));
    } catch (NigoriCryptographyException e) {
      throw new PasswordStoreException(e);
    } catch (IOException e) {
      throw new PasswordStoreException(e);
    } catch (UnauthorisedException e) {
      throw new PasswordStoreException(e);
    }
  }

  @Override
  public void backup(OutputStream output, String password) throws IOException,
      NigoriCryptographyException, UnauthorisedException {
    HashMapDatabase database = new HashMapDatabase();
    NigoriDatastore backupStore =
        new CryptoNigoriDatastore(new DatabaseNigoriProtocol(database), mUserName, password,
            "backup");
    if (!backupStore.register()) {
      throw new UnauthorisedException("Could not register with backup server");
    }
    SyncingNigoriDatastore syncing = new SyncingNigoriDatastore(mNigoriStore, backupStore);
    syncing.syncAll();
    ObjectOutputStream oos = new ObjectOutputStream(output);
    oos.writeObject(database);
    oos.flush();
    oos.close();
  }

  @Override
  public void restore(InputStream input, String password) throws IOException,
      NigoriCryptographyException, ClassNotFoundException, UnauthorisedException {
    ObjectInputStream ois = new ObjectInputStream(input);

    HashMapDatabase database = (HashMapDatabase) ois.readObject();
    ois.close();

    SyncingNigoriDatastore syncing =
        new SyncingNigoriDatastore(mNigoriStore, new CryptoNigoriDatastore(
            new DatabaseNigoriProtocol(database), mUserName, password, "backup"));
    syncing.syncAll();
  }

}
