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
