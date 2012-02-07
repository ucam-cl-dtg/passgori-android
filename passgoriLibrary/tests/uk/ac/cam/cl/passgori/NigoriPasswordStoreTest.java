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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.everyItem;
import static org.hamcrest.core.IsEqual.equalTo;

import java.io.IOException;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.nigori.client.HashMigoriDatastore;
import com.google.nigori.client.MigoriDatastore;
import com.google.nigori.client.NigoriCryptographyException;
import com.google.nigori.client.HTTPNigoriDatastore;
import com.google.nigori.common.Index;
import com.google.nigori.common.RevValue;

/**
 * 
 */

/**
 * Unit test for NigoriPasswordStore
 * 
 * @author Miltiadis Allamanis
 * 
 */
public class NigoriPasswordStoreTest {
	IPasswordStore ps;

	private final String TEST_USERNAME = "testaccount";
	private final String TEST_PASSWORD = "test";
	private final String TEST_SERVER = "localhost";
	private final int TEST_SERVER_PORT = 8888;
	private final String TEST_SERVER_PREFIX = "nigori";

	@BeforeClass
	public static void ensureClean() throws PasswordStoreException {
	  NigoriPasswordStoreTest instance = new NigoriPasswordStoreTest();
	  instance.createStore();
	  instance.destroyStore();
	}

	@Before
	public void createStore() throws PasswordStoreException {
	  ps = new NigoriPasswordStore(TEST_USERNAME,
        TEST_PASSWORD, TEST_SERVER, TEST_SERVER_PORT,
        TEST_SERVER_PREFIX);
	}

	@After
	public void destroyStore() throws PasswordStoreException {
	  ps.destroyStore();
	}

	@Test
	public void testAddKey() throws IOException, NigoriCryptographyException,
			PasswordStoreException {

		Password testPass = new Password("a", "bb", "ccc", "dddd");

		assertTrue(ps.storePassword(testPass));
		assertEquals(ps.getAllStoredPasswordIds().size(), 1);
		assertTrue(ps.getAllStoredPasswordIds().get(0).equals("a"));
		assertTrue(ps.retrivePassword("a").getPassword().equals("ccc"));
		assertTrue(ps.retrivePassword("a").getNotes().equals("dddd"));

		assertFalse(ps.removePassword("bb"));
		List<String> passwordIds = ps.getAllStoredPasswordIds();
		assertEquals(1,passwordIds.size());
		assertNull(ps.retrivePassword("bb"));

		assertTrue(ps.removePassword("a"));
		assertEquals(0,ps.getAllStoredPasswordIds().size());
		assertNull(ps.retrivePassword("a"));
	}

	@Test
	public void testKeyLinkedList() throws IOException,
			NigoriCryptographyException, PasswordStoreException {

		Password testPass1 = new Password("a", "bb", "ccc", "dddd");
		assertTrue(ps.storePassword(testPass1));
		assertEquals(ps.getAllStoredPasswordIds().size(), 1);
		assertTrue(ps.getAllStoredPasswordIds().get(0).equals("a"));
		assertTrue(ps.retrivePassword("a").getPassword().equals("ccc"));
		assertTrue(ps.retrivePassword("a").getNotes().equals("dddd"));

		Password testPass2 = new Password("b", "cc", "ddd", "eeee");
		assertTrue(ps.storePassword(testPass2));
		assertEquals(ps.getAllStoredPasswordIds().size(), 2);
		assertTrue(ps.getAllStoredPasswordIds().contains("b"));
		assertTrue(ps.getAllStoredPasswordIds().contains("a"));
		assertTrue(ps.retrivePassword("a").getPassword().equals("ccc"));
		assertTrue(ps.retrivePassword("a").getNotes().equals("dddd"));
		assertTrue(ps.retrivePassword("b").getPassword().equals("ddd"));
		assertTrue(ps.retrivePassword("b").getNotes().equals("eeee"));

		Password testPass3 = new Password("c", "dd", "eee", "ffff");
		assertTrue(ps.storePassword(testPass3));
		assertEquals(ps.getAllStoredPasswordIds().size(), 3);
		assertTrue(ps.getAllStoredPasswordIds().contains("b"));
		assertTrue(ps.getAllStoredPasswordIds().contains("a"));
		assertTrue(ps.getAllStoredPasswordIds().contains("c"));
		assertTrue(ps.retrivePassword("a").getPassword().equals("ccc"));
		assertTrue(ps.retrivePassword("a").getNotes().equals("dddd"));
		assertTrue(ps.retrivePassword("b").getPassword().equals("ddd"));
		assertTrue(ps.retrivePassword("b").getNotes().equals("eeee"));
		assertTrue(ps.retrivePassword("c").getPassword().equals("eee"));
		assertTrue(ps.retrivePassword("c").getNotes().equals("ffff"));

		assertTrue(ps.removePassword("b"));
		assertEquals(ps.getAllStoredPasswordIds().size(), 2);
		assertFalse(ps.getAllStoredPasswordIds().contains("b"));
		assertTrue(ps.getAllStoredPasswordIds().contains("a"));
		assertTrue(ps.getAllStoredPasswordIds().contains("c"));
		assertTrue(ps.retrivePassword("a").getPassword().equals("ccc"));
		assertTrue(ps.retrivePassword("a").getNotes().equals("dddd"));
		assertTrue(ps.retrivePassword("c").getPassword().equals("eee"));
		assertTrue(ps.retrivePassword("c").getNotes().equals("ffff"));
		assertNull(ps.retrivePassword("b"));

		assertTrue(ps.removePassword("c"));
		assertEquals(ps.getAllStoredPasswordIds().size(), 1);
		assertFalse(ps.getAllStoredPasswordIds().contains("b"));
		assertTrue(ps.getAllStoredPasswordIds().contains("a"));
		assertFalse(ps.getAllStoredPasswordIds().contains("c"));
		assertTrue(ps.retrivePassword("a").getPassword().equals("ccc"));
		assertTrue(ps.retrivePassword("a").getNotes().equals("dddd"));
		assertNull(ps.retrivePassword("b"));
		assertNull(ps.retrivePassword("c"));

		assertTrue(ps.removePassword("a"));
		assertEquals(ps.getAllStoredPasswordIds().size(), 0);
		assertNull(ps.retrivePassword("c"));
		assertNull(ps.retrivePassword("b"));
		assertNull(ps.retrivePassword("a"));

	}

	@Test
	public void testNigoriConnection() throws IOException,
			NigoriCryptographyException {
		MigoriDatastore mMigoriStore = new HashMigoriDatastore(new HTTPNigoriDatastore(TEST_SERVER, TEST_SERVER_PORT,
        TEST_SERVER_PREFIX, TEST_USERNAME, TEST_PASSWORD));

		Index index = new Index("1");
		byte[] value = new byte[1];
		value[0] = 10;

    try {
      mMigoriStore.register();

      RevValue put = mMigoriStore.put(index, value);
      assertArrayEquals(value,put.getValue());
      assertThat(mMigoriStore.get(index),everyItem(equalTo(put)));
    } finally {
      mMigoriStore.unregister();
		}
	}

	@Test
	public void testPasswordList2() throws IOException,
			NigoriCryptographyException, PasswordStoreException {

		for (int i = 0; i < 100; i++) {
			Password pass = new Password("a" + i, "bb" + (2 * i), "ccc", "dddd");
			assertTrue(ps.storePassword(pass));
			assertEquals(ps.getAllStoredPasswordIds().size(), i + 1);
		}

		List<String> list = ps.getAllStoredPasswordIds();
		for (int i = 0; i < 100; i++) {
			assertTrue(list.contains("a" + i));
		}

		int suffler[] = new int[100];
		int indexes[] = new int[100];
		for (int i = 0; i < 100; i++) {
			suffler[i] = (int) (Math.random() * 100);
			indexes[i] = i;
		}

		for (int i = 0; i < 100; i++) {
			int temp = indexes[suffler[i]];
			indexes[suffler[i]] = indexes[i];
			indexes[i] = temp;
		}

		for (int i = 0; i < 100; i++) {
			assertTrue(ps.removePassword("a" + indexes[i]));
			List<String> storedpwds = ps.getAllStoredPasswordIds();
			assertEquals(storedpwds.size(), (100 - i) - 1);
			assertFalse(storedpwds.contains("a" + indexes[i]));
		}

	}

}
