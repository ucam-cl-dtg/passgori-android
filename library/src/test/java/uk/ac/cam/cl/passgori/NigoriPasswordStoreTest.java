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

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.matchers.JUnitMatchers.everyItem;
import static org.junit.matchers.JUnitMatchers.hasItem;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.nigori.client.CryptoNigoriDatastore;
import com.google.nigori.client.HashMigoriDatastore;
import com.google.nigori.client.MigoriDatastore;
import com.google.nigori.client.NigoriCryptographyException;
import com.google.nigori.common.Index;
import com.google.nigori.common.RevValue;
import com.google.nigori.common.UnauthorisedException;

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

	private static final boolean LOCAL_TESTING = false;
	private final String TEST_USERNAME = "testaccount";
	private final String TEST_PASSWORD = "test";
	private final String TEST_SERVER = LOCAL_TESTING ? "localhost" : "nigori-dev.appspot.com";
	private final int TEST_SERVER_PORT = LOCAL_TESTING ? 8888 : 80;
	private final String TEST_SERVER_PREFIX = "nigori";
	private final File je = new File("je-test-dir/");
	private static final int REPEAT = 50;

	@BeforeClass
	public static void ensureClean() throws PasswordStoreException {
	  NigoriPasswordStoreTest instance = new NigoriPasswordStoreTest();
	  instance.createStore();
	  instance.destroyStore();
	}

	@Before
	public void createStore() throws PasswordStoreException {
	  je.mkdirs();
	  je.deleteOnExit();
	  ps = new NigoriPasswordStore(je, TEST_USERNAME,
        TEST_PASSWORD, TEST_SERVER, TEST_SERVER_PORT,
        TEST_SERVER_PREFIX);
	}

	@After
	public void destroyStore() throws PasswordStoreException {
	  ps.destroyStore();
	  je.delete();
	  //TODO(drt24) do the delete properly
	}

  @After
  public void deleteDatabase() {
    File dataDir = new File("je-test-dir/");
    if (dataDir.exists()){
      deleteDir(dataDir);
      dataDir.delete();
    }
  }

  /**
   * Deletes all files and subdirectories under dir. Returns true if all deletions were successful.
   * If a deletion fails, the method stops attempting to delete and returns false.
   * 
   * From http://www.exampledepot.com/egs/java.io/DeleteDir.html
   * 
   * @param dir
   * @return
   */
  public static boolean deleteDir(File dir) {
    if (dir.isDirectory()) {
      String[] children = dir.list();
      for (int i = 0; i < children.length; i++) {
        boolean success = deleteDir(new File(dir, children[i]));
        if (!success) {
          return false;
        }
      }
    }

    // The directory is now empty so delete it
    return dir.delete();
  }

  @Test
  public void testNigoriConnection() throws IOException, NigoriCryptographyException,
      UnauthorisedException {
    MigoriDatastore mMigoriStore = new HashMigoriDatastore(new CryptoNigoriDatastore(TEST_SERVER, TEST_SERVER_PORT,
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
  public void testPasswordList2() throws IOException, NigoriCryptographyException,
      PasswordStoreException {

    for (int i = 0; i < REPEAT; i++) {
      Password pass = new Password("a" + i, "bb" + (2 * i), "ccc", "dddd");
      assertTrue(ps.storePassword(pass));
      assertEquals(i + 1, ps.getAllStoredPasswordIds().size());
    }

    List<String> list = ps.getAllStoredPasswordIds();
    for (int i = 0; i < REPEAT; i++) {
      assertThat(list, hasItem("a" + i));
    }

    Collections.shuffle(list);

    int size = REPEAT;
    for (String id : list) {
      assertTrue(ps.removePassword(id));
      List<String> storedpwds = ps.getAllStoredPasswordIds();
      assertEquals(--size, storedpwds.size());
      assertThat(storedpwds, not(hasItem(id)));
    }
  }

}
