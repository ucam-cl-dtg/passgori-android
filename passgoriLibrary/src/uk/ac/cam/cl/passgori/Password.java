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
public class Password {

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
		mGeneratedAt = System.currentTimeMillis(); 
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

	public Password(String id, byte[] bytes) throws UnsupportedEncodingException {
    int offset = 0;
    byte[] username = Arrays.copyOfRange(bytes, Util.INT, Util.INT + Util.bin2int(bytes, offset));
    offset += Util.INT + username.length;
    byte[] password = Arrays.copyOfRange(bytes, offset + Util.INT, offset + Util.INT + Util.bin2int(bytes, offset));
    offset += Util.INT + password.length;
    byte[] notes = Arrays.copyOfRange(bytes, offset + Util.INT, offset + Util.INT + Util.bin2int(bytes, offset));
    offset += Util.INT + notes.length;

    mId = id;
    mUsername = new String(username, CHARSET);
    mPassword = new String(password, CHARSET);
    mNotes = new String(notes, CHARSET);
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

  public byte[] toBytes() throws UnsupportedEncodingException {
    byte[] username = mUsername.getBytes(CHARSET);
    byte[] password = mPassword.getBytes(CHARSET);
    byte[] notes = mNotes.getBytes(CHARSET);
    byte[] answer = new byte[3 * Util.INT + username.length + password.length + notes.length + Util.LONG];
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

    System.arraycopy(Util.long2bin(mGeneratedAt),0, answer, insert, Util.LONG);

    return answer;
  }

}
