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
 
/**
 * 
 */
package uk.ac.cam.cl.passgori;

import java.util.List;

/**
 * A password store offering random delay to emulate the network
 * 
 * @author Miltiadis Allamanis
 * 
 */
public class DelayedDummyPasswordStore extends DummyPasswordStore {

	/**
	 * A constant indicating the maximum delay in milliseconds.
	 */
	public static final int MAX_DELAY = 10000;

	@Override
	public boolean authenticate(String username, String password) {
		try {
			Thread.sleep((long) (MAX_DELAY * Math.random()));
		} catch (InterruptedException e) {
		}
		return super.authenticate(username, password);
	}

	@Override
	public List<String> getAllStoredPasswordIds() throws PasswordStoreException {
		try {
			Thread.sleep((long) (MAX_DELAY * Math.random()));
		} catch (InterruptedException e) {
		}
		return super.getAllStoredPasswordIds();
	}

	@Override
	public boolean removePassword(String aId) throws PasswordStoreException {
		try {
			Thread.sleep((long) (MAX_DELAY * Math.random()));
		} catch (InterruptedException e) {
		}
		return super.removePassword(aId);
	}

	@Override
	public Password retrivePassword(String aId) throws PasswordStoreException {
		try {
			Thread.sleep((long) (MAX_DELAY * Math.random()));
		} catch (InterruptedException e) {
		}
		return super.retrivePassword(aId);
	}

	@Override
	public boolean storePassword(Password aPassword)
			throws PasswordStoreException {
		try {
			Thread.sleep((long) (MAX_DELAY * Math.random()));
		} catch (InterruptedException e) {
		}
		return super.storePassword(aPassword);
	}
}
