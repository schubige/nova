package org.corebounce.util;

import java.util.Vector;

/**
 * Preferences store interface.
 * 
 * (c) 1999, 2000, 2001, IIUF, DIUF
 * <p>
 * 
 * @author shoobee
 */
public interface PreferencesStore {
	Object get(String key);

	void getMulti(String prefix, Vector<Object> result);

	void set(String key, Object value);

	void remove(String key);

	void load();

	void store();

	void clear();
}
