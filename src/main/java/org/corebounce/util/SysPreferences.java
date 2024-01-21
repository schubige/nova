package org.corebounce.util;

import java.util.Vector;

/**
 * System preferences.
 * 
 * (c) 1999, 2000, 2001, IIUF, DIUF
 * <p>
 * 
 * @author shoobee
 */
public class SysPreferences implements PreferencesStore {

	@Override
	public Object get(String key) {
		try {
			return System.getProperty(key);
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public void remove(String key) {
		System.getProperties().remove(key);
	}

	@Override
	public void set(String key, Object value) {
	}

	@Override
	public void load() {
	}

	@Override
	public void store() {
	}

	@Override
	public void clear() {
	}

	@Override
	public void getMulti(String prefix, Vector<Object> result) {
		String[] keys = System.getProperties().keySet().toArray(new String[System.getProperties().size()]);
		for (int i = 0; i < keys.length; i++)
			if (keys[i].startsWith(prefix))
				result.add(System.getProperty(keys[i]));
	}
}

