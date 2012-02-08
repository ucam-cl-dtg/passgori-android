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

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import com.google.nigori.common.MessageLibrary;
/**
 * 
 */
import com.google.nigori.common.Util;

/**
 * A class representing password information
 * 
 * @author Miltiadis Allamanis
 * 
 */
public class Password implements Comparable<Password> {

	private final String mId;

	private final String mUsername;
	private final String mPassword;

	private final String mNotes;
	private final long mGeneratedAt;

	private static final String CHARSET = MessageLibrary.CHARSET;
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
		mGeneratedAt = object.mGeneratedAt; 
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
		mGeneratedAt = System.currentTimeMillis();
	}

	public Password(String id, byte[] bytes) {
    int offset = 0;
    byte[] username = Arrays.copyOfRange(bytes, Util.INT, Util.INT + Util.bin2int(bytes, offset));
    offset += Util.INT + username.length;
    byte[] password = Arrays.copyOfRange(bytes, offset + Util.INT, offset + Util.INT + Util.bin2int(bytes, offset));
    offset += Util.INT + password.length;
    byte[] notes = Arrays.copyOfRange(bytes, offset + Util.INT, offset + Util.INT + Util.bin2int(bytes, offset));
    offset += Util.INT + notes.length;

    mId = id;
    try {
      mUsername = new String(username, CHARSET);
      mPassword = new String(password, CHARSET);
      mNotes = new String(notes, CHARSET);
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }
    mGeneratedAt = Util.bin2long(bytes, offset);
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

  public byte[] toBytes() {
    try {
      byte[] username = mUsername.getBytes(CHARSET);
      byte[] password = mPassword.getBytes(CHARSET);
      byte[] notes = mNotes.getBytes(CHARSET);

      byte[] answer =
          new byte[3 * Util.INT + username.length + password.length + notes.length + Util.LONG];
      int insert = 0;

      System.arraycopy(Util.int2bin(username.length), 0, answer, insert, Util.INT);
      insert += Util.INT;
      System.arraycopy(username, 0, answer, insert, username.length);
      insert += username.length;

      System.arraycopy(Util.int2bin(password.length), 0, answer, insert, Util.INT);
      insert += Util.INT;
      System.arraycopy(password, 0, answer, insert, password.length);
      insert += password.length;

      System.arraycopy(Util.int2bin(notes.length), 0, answer, insert, Util.INT);
      insert += Util.INT;
      System.arraycopy(notes, 0, answer, insert, notes.length);
      insert += notes.length;

      System.arraycopy(Util.long2bin(mGeneratedAt), 0, answer, insert, Util.LONG);

      return answer;
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public int compareTo(Password other) {
    if (other == null) {
      throw new NullPointerException("Can't compare with null passwords");
    }
    if (this.equals(other)) {
      return 0;
    } else if (mGeneratedAt < other.mGeneratedAt) {
      return -1;
    } else if (mGeneratedAt > other.mGeneratedAt) {
      return 1;
    } else {
      int id = mId.compareTo(other.mId);
      if (id != 0) {
        return id;
      }
      int username = mUsername.compareTo(other.mUsername);
      if (username != 0) {
        return username;
      }
      int password = mPassword.compareTo(other.mPassword);
      if (password != 0) {
        return password;
      }
      int notes = mNotes.compareTo(other.mNotes);
      if (notes != 0) {
        return notes;
      }
      return 0;// this shouldn't happen due to the .equals above.
    }
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((mId == null) ? 0 : mId.hashCode());
    result = prime * result + ((mNotes == null) ? 0 : mNotes.hashCode());
    result = prime * result + ((mPassword == null) ? 0 : mPassword.hashCode());
    result = prime * result + ((mUsername == null) ? 0 : mUsername.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Password other = (Password) obj;
    if (mId == null) {
      if (other.mId != null)
        return false;
    } else if (!mId.equals(other.mId))
      return false;
    if (mNotes == null) {
      if (other.mNotes != null)
        return false;
    } else if (!mNotes.equals(other.mNotes))
      return false;
    if (mPassword == null) {
      if (other.mPassword != null)
        return false;
    } else if (!mPassword.equals(other.mPassword))
      return false;
    if (mUsername == null) {
      if (other.mUsername != null)
        return false;
    } else if (!mUsername.equals(other.mUsername))
      return false;
    return true;
  }

}
