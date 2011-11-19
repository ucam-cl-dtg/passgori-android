import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.AbstractList;

import org.junit.Before;
import org.junit.Test;

import com.google.nigori.client.NigoriCryptographyException;
import com.google.nigori.client.NigoriDatastore;

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
	NigoriPasswordStore ps;

	private final String TEST_USERNAME = "test";
	private final String TEST_PASSWORD = "test";
	private final String TEST_SERVER = "localhost";
	private final int TEST_SERVER_PORT = 8888;
	private final String TEST_SERVER_PREFIX = "nigori";

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {

	}

	@Test
	public void testAddKey() throws IOException, NigoriCryptographyException,
			PasswordStoreException {
		NigoriPasswordStore nps = new NigoriPasswordStore(TEST_USERNAME,
				TEST_PASSWORD, TEST_SERVER, TEST_SERVER_PORT,
				TEST_SERVER_PREFIX);

		Password testPass = new Password("a", "bb", "ccc", "dddd");

		assertTrue(nps.storePassword(testPass));
		assertEquals(nps.getAllStoredPasswordIds().size(), 1);
		assertTrue(nps.getAllStoredPasswordIds().get(0).equals("a"));
		assertTrue(nps.retrivePassword("a").getPassword().equals("ccc"));
		assertTrue(nps.retrivePassword("a").getNotes().equals("dddd"));

		// assertFalse(nps.removePassword("bb")); TODO: Server API does not
		// respond correctly
		assertEquals(nps.getAllStoredPasswordIds().size(), 1);
		assertNull(nps.retrivePassword("bb"));

		assertTrue(nps.removePassword("a"));
		assertEquals(nps.getAllStoredPasswordIds().size(), 0);
		assertNull(nps.retrivePassword("a"));
	}

	@Test
	public void testKeyLinkedList() throws IOException,
			NigoriCryptographyException, PasswordStoreException {
		NigoriPasswordStore nps = new NigoriPasswordStore(TEST_USERNAME,
				TEST_PASSWORD, TEST_SERVER, TEST_SERVER_PORT,
				TEST_SERVER_PREFIX);

		Password testPass1 = new Password("a", "bb", "ccc", "dddd");
		assertTrue(nps.storePassword(testPass1));
		assertEquals(nps.getAllStoredPasswordIds().size(), 1);
		assertTrue(nps.getAllStoredPasswordIds().get(0).equals("a"));
		assertTrue(nps.retrivePassword("a").getPassword().equals("ccc"));
		assertTrue(nps.retrivePassword("a").getNotes().equals("dddd"));

		Password testPass2 = new Password("b", "cc", "ddd", "eeee");
		assertTrue(nps.storePassword(testPass2));
		assertEquals(nps.getAllStoredPasswordIds().size(), 2);
		assertTrue(nps.getAllStoredPasswordIds().contains("b"));
		assertTrue(nps.getAllStoredPasswordIds().contains("a"));
		assertTrue(nps.retrivePassword("a").getPassword().equals("ccc"));
		assertTrue(nps.retrivePassword("a").getNotes().equals("dddd"));
		assertTrue(nps.retrivePassword("b").getPassword().equals("ddd"));
		assertTrue(nps.retrivePassword("b").getNotes().equals("eeee"));

		Password testPass3 = new Password("c", "dd", "eee", "ffff");
		assertTrue(nps.storePassword(testPass3));
		assertEquals(nps.getAllStoredPasswordIds().size(), 3);
		assertTrue(nps.getAllStoredPasswordIds().contains("b"));
		assertTrue(nps.getAllStoredPasswordIds().contains("a"));
		assertTrue(nps.getAllStoredPasswordIds().contains("c"));
		assertTrue(nps.retrivePassword("a").getPassword().equals("ccc"));
		assertTrue(nps.retrivePassword("a").getNotes().equals("dddd"));
		assertTrue(nps.retrivePassword("b").getPassword().equals("ddd"));
		assertTrue(nps.retrivePassword("b").getNotes().equals("eeee"));
		assertTrue(nps.retrivePassword("c").getPassword().equals("eee"));
		assertTrue(nps.retrivePassword("c").getNotes().equals("ffff"));

		assertTrue(nps.removePassword("b"));
		assertEquals(nps.getAllStoredPasswordIds().size(), 2);
		assertFalse(nps.getAllStoredPasswordIds().contains("b"));
		assertTrue(nps.getAllStoredPasswordIds().contains("a"));
		assertTrue(nps.getAllStoredPasswordIds().contains("c"));
		assertTrue(nps.retrivePassword("a").getPassword().equals("ccc"));
		assertTrue(nps.retrivePassword("a").getNotes().equals("dddd"));
		assertTrue(nps.retrivePassword("c").getPassword().equals("eee"));
		assertTrue(nps.retrivePassword("c").getNotes().equals("ffff"));
		assertNull(nps.retrivePassword("b"));

		assertTrue(nps.removePassword("c"));
		assertEquals(nps.getAllStoredPasswordIds().size(), 1);
		assertFalse(nps.getAllStoredPasswordIds().contains("b"));
		assertTrue(nps.getAllStoredPasswordIds().contains("a"));
		assertFalse(nps.getAllStoredPasswordIds().contains("c"));
		assertTrue(nps.retrivePassword("a").getPassword().equals("ccc"));
		assertTrue(nps.retrivePassword("a").getNotes().equals("dddd"));
		assertNull(nps.retrivePassword("b"));
		assertNull(nps.retrivePassword("c"));

		assertTrue(nps.removePassword("a"));
		assertEquals(nps.getAllStoredPasswordIds().size(), 0);
		assertNull(nps.retrivePassword("c"));
		assertNull(nps.retrivePassword("b"));
		assertNull(nps.retrivePassword("a"));

	}

	@Test
	public void testNigoriConnection() throws IOException,
			NigoriCryptographyException {
		NigoriDatastore mNigoriStore = null;

		try {
			mNigoriStore = new NigoriDatastore(TEST_SERVER, TEST_SERVER_PORT,
					TEST_SERVER_PREFIX, TEST_USERNAME, TEST_PASSWORD);

		} catch (UnsupportedEncodingException e) {
			fail(e.getMessage());
		} catch (NigoriCryptographyException e) {
			fail(e.getMessage());
		}

		byte[] key = new byte[1];
		byte[] value = new byte[1];
		key[0] = 1;
		value[0] = 10;

		mNigoriStore.register();

		mNigoriStore.put(key, value);

		assertEquals(mNigoriStore.get(key)[0], 10);
	}

	@Test
	public void testPasswordList2() throws IOException,
			NigoriCryptographyException, PasswordStoreException {
		NigoriPasswordStore nps = new NigoriPasswordStore("test", "test",
				"localhost", 8888, "nigori");

		for (int i = 0; i < 100; i++) {
			Password pass = new Password("a" + i, "bb" + (2 * i), "ccc", "dddd");
			assertTrue(nps.storePassword(pass));
			assertEquals(nps.getAllStoredPasswordIds().size(), i + 1);
		}

		AbstractList<String> list = nps.getAllStoredPasswordIds();
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
			assertTrue(nps.removePassword("a" + indexes[i]));
			AbstractList<String> storedpwds = nps.getAllStoredPasswordIds();
			assertEquals(storedpwds.size(), (100 - i) - 1);
			assertFalse(storedpwds.contains("a" + indexes[i]));
		}

	}

}
