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
import java.util.List;

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
	  List<byte[]> bytess = Util.splitBytes(bytes);
    mId = id;
    try {
      mUsername = new String(bytess.get(0), CHARSET);
      mPassword = new String(bytess.get(1), CHARSET);
      mNotes = new String(bytess.get(2), CHARSET);
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }
    mGeneratedAt = Util.bin2long(bytess.get(3));
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
    return Util.joinBytes(MessageLibrary.toBytes(mUsername), MessageLibrary.toBytes(mPassword),
        MessageLibrary.toBytes(mNotes), Util.long2bin(mGeneratedAt));
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

  public long getGeneratedAt() {
    return mGeneratedAt;
  }

}
