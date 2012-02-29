package uk.ac.cam.cl.passgori;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.nigori.client.MigoriDatastore;
import com.google.nigori.client.MigoriDatastore.MigoriMerger;
import com.google.nigori.common.Index;
import com.google.nigori.common.NigoriCryptographyException;
import com.google.nigori.common.RevValue;
import com.google.nigori.common.UnauthorisedException;

public class PasswordMerger implements MigoriMerger {

  // TODO(drt24) abstract this into a generic Comparable<T> merger

  @Override
  public RevValue merge(MigoriDatastore store, Index index, Collection<RevValue> heads)
      throws IOException, NigoriCryptographyException, UnauthorisedException {
    assert heads.size() > 0;// caller must ensure that this is true
    List<Password> passwords = new ArrayList<Password>();
    Map<Password, RevValue> mapBack = new HashMap<Password, RevValue>();
    for (RevValue value : heads) {
      Password password;
      try {
        password = new Password(index.toString(), value.getValue());
      } catch (PasswordStoreException e) {
        throw new IOException(e);
      }
      passwords.add(password);
      mapBack.put(password, value);
    }
    findEquivalences(store, index, passwords, mapBack);
    Password latest = passwords.get(passwords.size() - 1);
    Password newPassword =
        new Password(latest.getId(), latest.getUsername(), latest.getPassword(), latest.getNotes());
    return store
        .put(index, newPassword.toBytes(), map(passwords, mapBack).toArray(new RevValue[0]));
  }

  private Collection<RevValue> map(List<Password> passwords, Map<Password, RevValue> mapBack) {
    Collection<RevValue> answer = new ArrayList<RevValue>();
    for (Password password : passwords) {
      answer.add(mapBack.get(password));
    }
    return answer;
  }

  private void findEquivalences(MigoriDatastore store, Index index, List<Password> passwords,
      Map<Password, RevValue> mapBack) throws IOException, NigoriCryptographyException, UnauthorisedException {
    Collections.sort(passwords);
    // Daniel's magic find things which are the same and merge them together by means of delicate loops algorithm
    // NEEDS thorough testing, many edge cases.
    Collection<Password> equivalence = new ArrayList<Password>();
    Collection<Password> toRemove = new ArrayList<Password>();
    Password last = null;
    boolean changed = false;
    for (Password password : passwords) {
      if (null == last) {
        last = password;
        continue;
      }
      if (password.equals(last)) {
        equivalence.add(last);
      } else {
        if (!equivalence.isEmpty()) {
          equivalence.add(last);
          PasswordValue newValue = putEquivalence(store, index, equivalence, mapBack);
          mapBack.put(newValue.password, newValue.value);
          passwords.add(newValue.password);
          toRemove.addAll(equivalence);
          equivalence.clear();
          changed = true;
        }
      }
      last = password;
    }
    if (!equivalence.isEmpty()) {
      equivalence.add(last);
      PasswordValue newValue = putEquivalence(store, index, equivalence, mapBack);
      mapBack.put(newValue.password, newValue.value);
      passwords.add(newValue.password);
      toRemove.addAll(equivalence);
      equivalence.clear();
      changed = true;
    }
    passwords.removeAll(toRemove);
    Collections.sort(passwords);
    if (changed) {
      findEquivalences(store, index, passwords, mapBack);
    }
  }

  private PasswordValue putEquivalence(MigoriDatastore store, Index index,
      Collection<Password> equivalence, Map<Password, RevValue> mapBack) throws IOException,
      NigoriCryptographyException, UnauthorisedException {
    List<RevValue> values = new ArrayList<RevValue>();
    Password value = null;
    for (Password password : equivalence) {
      value = password;
      values.add(mapBack.get(password));
    }
    Password password =
        new Password(value.getId(), value.getUsername(), value.getPassword(), value.getNotes());
    return new PasswordValue(password, store.put(index, password.toBytes(), values.toArray(new RevValue[0])));
  }

  private static class PasswordValue {
    public final Password password;
    public final RevValue value;

    public PasswordValue(Password password, RevValue value) {
      this.password = password;
      this.value = value;
    }
  }
}
