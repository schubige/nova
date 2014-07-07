package org.corebounce.collections;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Map;

public class CanonicalSynchronizedSet<K> {
	public synchronized K intern(K object) {
		if(object == null) return null;
		final WeakReference<K> resultRef = get(object);
		if(resultRef != null) {
			final K result = resultRef.get();
			if(result != null)
				return result;
		}
		add(object);
		return object;
	}
	/**
	 * The default initial capacity -- MUST be a power of two.
	 */
	private static final int DEFAULT_INITIAL_CAPACITY = 16;

	/**
	 * The maximum capacity, used if a higher value is implicitly specified
	 * by either of the constructors with arguments.
	 * MUST be a power of two <= 1<<30.
	 */
	private static final int MAXIMUM_CAPACITY = 1 << 30;

	/**
	 * The load fast used when none specified in constructor.
	 */
	private static final float DEFAULT_LOAD_FACTOR = 0.75f;

	/**
	 * The table, resized as necessary. Length MUST Always be a power of two.
	 */
	Entry<K>[] table;

	/**
	 * The number of key-value mappings contained in this weak hash map.
	 */
	private int size;

	/**
	 * The next size value at which to resize (capacity * load factor).
	 */
	private int threshold;

	/**
	 * The load factor for the hash table.
	 */
	private final float loadFactor;
	
	/**
	 * Reference queue for cleared WeakEntries
	 */
	private final ReferenceQueue<K> queue = new ReferenceQueue<K>();
	
	@SuppressWarnings("unchecked")
	public CanonicalSynchronizedSet() {
		this.loadFactor = DEFAULT_LOAD_FACTOR;
		threshold = (DEFAULT_INITIAL_CAPACITY);
		table = new Entry[DEFAULT_INITIAL_CAPACITY];
	}

	/**
	 * Checks for equality of non-null reference x and possibly-null y.  By
	 * default uses Object.equals.
	 */
	private static boolean eq(Object x, Object y) {
		return x == y || x.equals(y);
	}

	/**
	 * Returns index for hash code h.
	 */
	private static int indexFor(int h, int length) {
		return h & (length-1);
	}

	/**
	 * Expunges stale entries from the table.
	 */
	@SuppressWarnings("unchecked")
	private void expungeStaleEntries() {
		Entry<K> e;
		while ( (e = (Entry<K>) queue.poll()) != null) {
			int h = e.hash;
			int i = indexFor(h, table.length);

			Entry<K> prev = table[i];
			Entry<K> p = prev;
			while (p != null) {
				Entry<K> next = p.next;
				if (p == e) {
					if (prev == e)
						table[i] = next;
					else
						prev.next = next;
					e.next = null;  // Help GC
					size--;
					break;
				}
				prev = p;
				p = next;
			}
		}
	}

	/**
	 * Returns the table after first expunging stale entries.
	 */
	private Entry<K>[] getTable() {
		expungeStaleEntries();
		return table;
	}

	/**
	 * Returns the number of key-value mappings in this map.
	 * This result is a snapshot, and may not reflect unprocessed
	 * entries that will be removed before next attempted access
	 * because they are no longer referenced.
	 */
	public int size() {
		if (size == 0)
			return 0;
		expungeStaleEntries();
		return size;
	}

	/**
	 * Returns the value to which the specified key is mapped,
	 * or {@code null} if this map contains no mapping for the key.
	 *
	 * <p>More formally, if this map contains a mapping from a key
	 * {@code k} to a value {@code v} such that {@code (key==null ? k==null :
	 * key.equals(k))}, then this method returns {@code v}; otherwise
	 * it returns {@code null}.  (There can be at most one such mapping.)
	 *
	 * <p>A return value of {@code null} does not <i>necessarily</i>
	 * indicate that the map contains no mapping for the key; it's also
	 * possible that the map explicitly maps the key to {@code null}.
	 * The {@link #containsKey containsKey} operation may be used to
	 * distinguish these two cases.
	 *
	 * @see #put(Object, Object)
	 */
	public Entry<K> get(final Object k) {
		final int        h     = hash(k.hashCode());
		final Entry<K>[] tab   = getTable();
		final int        index = indexFor(h, tab.length);
		Entry<K>         e     = tab[index];
		while (e != null) {
			if (e.hash == h && eq(k, e.get()))
				return e;
			e = e.next;
		}
		return null;
	}

	/**
	 * Associates the specified value with the specified key in this map.
	 * If the map previously contained a mapping for this key, the old
	 * value is replaced.
	 *
	 * @param key key with which the specified value is to be associated.
	 * @param value value to be associated with the specified key.
	 * @return the previous value associated with <tt>key</tt>, or
	 *         <tt>null</tt> if there was no mapping for <tt>key</tt>.
	 *         (A <tt>null</tt> return can also indicate that the map
	 *         previously associated <tt>null</tt> with <tt>key</tt>.)
	 */
	public Map.Entry<K,?> add(K k) {
		int h = hash(k.hashCode());
		Entry<K>[] tab = getTable();
		int i = indexFor(h, tab.length);

		for (Entry<K> e = tab[i]; e != null; e = e.next) {
			if (h == e.hash && eq(k, e.get())) {
				return e;
			}
		}

		Entry<K> e = tab[i];
		tab[i] = new Entry<K>(k, queue, h, e);
		if (++size >= threshold)
			resize(tab.length * 2);
		return null;
	}

	/**
	 * Rehashes the contents of this map into a new array with a
	 * larger capacity.  This method is called automatically when the
	 * number of keys in this map reaches its threshold.
	 *
	 * If current capacity is MAXIMUM_CAPACITY, this method does not
	 * resize the map, but sets threshold to Integer.MAX_VALUE.
	 * This has the effect of preventing future calls.
	 *
	 * @param newCapacity the new capacity, MUST be a power of two;
	 *        must be greater than current capacity unless current
	 *        capacity is MAXIMUM_CAPACITY (in which case value
	 *        is irrelevant).
	 */
	@SuppressWarnings("unchecked")
	private void resize(int newCapacity) {
		Entry<K>[] oldTable = getTable();
		int oldCapacity = oldTable.length;
		if (oldCapacity == MAXIMUM_CAPACITY) {
			threshold = Integer.MAX_VALUE;
			return;
		}

		Entry<K>[] newTable = new Entry[newCapacity];
		transfer(oldTable, newTable);
		table = newTable;

		/*
		 * If ignoring null elements and processing ref queue caused massive
		 * shrinkage, then restore old table.  This should be rare, but avoids
		 * unbounded expansion of garbage-filled tables.
		 */
		if (size >= threshold / 2) {
			threshold = (int)(newCapacity * loadFactor);
		} else {
			expungeStaleEntries();
			transfer(newTable, oldTable);
			table = oldTable;
		}
	}

	/** Transfers all entries from src to dest tables */
	private void transfer(Entry<K>[] src, Entry<K>[] dest) {
		for (int j = 0; j < src.length; ++j) {
			Entry<K> e = src[j];
			src[j] = null;
			while (e != null) {
				Entry<K> next = e.next;
				Object key = e.get();
				if (key == null) {
					e.next = null;  // Help GC
					size--;
				} else {
					int i = indexFor(e.hash, dest.length);
					e.next = dest[i];
					dest[i] = e;
				}
				e = next;
			}
		}
	}

	/**
	 * The entries in this hash table extend WeakReference, using its main ref
	 * field as the key.
	 */
	static final class Entry<K> extends WeakReference<K> implements Map.Entry<K,Entry<K>> {
		final int hash;
		Entry<K> next;

		/**
		 * Creates new entry.
		 */
		Entry(K key, ReferenceQueue<K> queue, int hash, Entry<K> next) {
			super(key, queue);
			this.hash  = hash;
			this.next  = next;
		}

		@Override
		public K getKey() {
			return get();
		}

		@Override
		public Entry<K> getValue() {
			return this;
		}

		@Override
		public Entry<K> setValue(Entry<K> newValue) {
			return this;
		}

		@SuppressWarnings("rawtypes")
		@Override
		public boolean equals(Object o) {
			if (!(o instanceof Map.Entry))
				return false;
			Map.Entry e = (Map.Entry)o;
			Object k1 = getKey();
			Object k2 = e.getKey();
			return k1 == k2 || k1.equals(k2);
		}

		@Override
		public int hashCode() {
			return getKey().hashCode();
		}

		@Override
		public String toString() {
			return getKey() + "=" + getValue();
		}
	}

	/**
	 * Applies a supplemental hash function to a given hashCode, which
	 * defends against poor quality hash functions.  This is critical
	 * because HashMap uses power-of-two length hash tables, that
	 * otherwise encounter collisions for hashCodes that do not differ
	 * in lower bits. Note: Null keys always map to hash 0, thus index 0.
	 */
	static int hash(int h) {
		// This function ensures that hashCodes that differ only by
		// constant multiples at each bit position have a bounded
		// number of collisions (approximately 8 at default load factor).
		h ^= (h >>> 20) ^ (h >>> 12);
		return h ^ (h >>> 7) ^ (h >>> 4);
	}

	@Override
	public String toString() {
		return toArrayList().toString();
	}

	public Object[] toArray() {
		return toArrayList().toArray();
	}

	private ArrayList<Object> toArrayList() {
		ArrayList<Object> result = new ArrayList<Object>(size());
		Entry<K>[] tab = getTable();
		for(int i = 0; i < tab.length; i++)
			if(tab[i] != null) {
				for(Entry<K> e = tab[i]; e != null;) {
					Object   key  = e.get();
					if (key != null)
						result.add(key);
					e = e.next;
				}
			}
		return result;
	}
}
