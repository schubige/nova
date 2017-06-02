package org.corebounce.collections;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

@SuppressWarnings("nls")
public final class IdentityHashSet<K> extends AbstractSet<K> implements Cloneable {
	/**
	 * The initial capacity used by the no-args constructor.
	 * MUST be a power of two.  The value 32 corresponds to the
	 * (specified) expected maximum size of 21, given a load factor
	 * of 2/3.
	 */
	private static final int DEFAULT_CAPACITY = 32;

	/**
	 * The minimum capacity, used if a lower value is implicitly specified
	 * by either of the constructors with arguments.  The value 4 corresponds
	 * to an expected maximum size of 2, given a load factor of 2/3.
	 * MUST be a power of two.
	 */
	private static final int MINIMUM_CAPACITY = 4;

	/**
	 * The maximum capacity, used if a higher value is implicitly specified
	 * by either of the constructors with arguments.
	 * MUST be a power of two <= 1<<29.
	 */
	private static final int MAXIMUM_CAPACITY = 1 << 29;

	/**
	 * The table, resized as necessary. Length MUST always be a power of two.
	 */
	transient Object[] table;

	/**
	 * The number of  elements contained in this identity hash set.
	 *
	 * @serial
	 */
	int size;

	/**
	 * The number of modifications, to support fast-fail iterators
	 */
	transient volatile int modCount;

	/**
	 * The next size value at which to resize (capacity * load factor).
	 */
	private transient int threshold;

	/**
	 * Value representing null keys inside tables.
	 */
	private static final Object NULL_KEY = new Object();

	/**
	 * Use NULL_KEY for key if it is null.
	 */

	private static Object maskNull(Object key) {
		return (key == null ? NULL_KEY : key);
	}

	/**
	 * Returns internal representation of null key back to caller as null.
	 */
	static Object unmaskNull(Object key) {
		return (key == NULL_KEY ? null : key);
	}

	/**
	 * Constructs a new, empty identity hash set with a default expected
	 * maximum size (21).
	 */
	public IdentityHashSet() {
		init(DEFAULT_CAPACITY);
	}

	/**
	 * Constructs a new, empty set with the specified expected maximum size.
	 * Putting more than the expected number of  elements into
	 * the set may cause the internal data structure to grow, which may be
	 * somewhat time-consuming.
	 *
	 * @param expectedMaxSize the expected maximum size of the set
	 * @throws IllegalArgumentException if <tt>expectedMaxSize</tt> is negative
	 */
	public IdentityHashSet(int expectedMaxSize) {
		if (expectedMaxSize < 0)
			throw new IllegalArgumentException("expectedMaxSize is negative: "
					+ expectedMaxSize);
		init(capacity(expectedMaxSize));
	}

	/**
	 * Returns the appropriate capacity for the specified expected maximum
	 * size.  Returns the smallest power of two between MINIMUM_CAPACITY
	 * and MAXIMUM_CAPACITY, inclusive, that is greater than
	 * (3 * expectedMaxSize)/2, if such a number exists.  Otherwise
	 * returns MAXIMUM_CAPACITY.  If (3 * expectedMaxSize)/2 is negative, it
	 * is assumed that overflow has occurred, and MAXIMUM_CAPACITY is returned.
	 */
	private static int capacity(int expectedMaxSize) {
		// Compute min capacity for expectedMaxSize given a load factor of 2/3
		int minCapacity = (3 * expectedMaxSize)/2;

		// Compute the appropriate capacity
		int result;
		if (minCapacity > MAXIMUM_CAPACITY || minCapacity < 0) {
			result = MAXIMUM_CAPACITY;
		} else {
			result = MINIMUM_CAPACITY;
			while (result < minCapacity)
				result <<= 1;
		}
		return result;
	}

	/**
	 * Initializes object to be an empty set with the specified initial
	 * capacity, which is assumed to be a power of two between
	 * MINIMUM_CAPACITY and MAXIMUM_CAPACITY inclusive.
	 */
	private void init(int initCapacity) {
		// assert (initCapacity & -initCapacity) == initCapacity; // power of 2
		// assert initCapacity >= MINIMUM_CAPACITY;
		// assert initCapacity <= MAXIMUM_CAPACITY;

		threshold = (initCapacity * 2)/3;
		table = new Object[initCapacity];
	}

	public IdentityHashSet(Collection<? extends K> m) {
		// Allow for a bit of growth
		this((int) ((1 + m.size()) * 1.1));
		addAll(m);
	}

	@SuppressWarnings("unchecked")
	public IdentityHashSet(K ... elements) {
		// Allow for a bit of growth
		this((int) ((1 + elements.length) * 1.1));
		for(K element : elements)
			add(element);
	}

	/**
	 * Returns the number of  elements in this identity hash set.
	 *
	 * @return the number of  elements in this set
	 */
	@Override
	public int size() {
		return size;
	}

	/**
	 * Returns <tt>true</tt> if this identity hash set contains no 
	 * elements.
	 *
	 * @return <tt>true</tt> if this identity hash set contains no 
	 *         elements
	 */
	@Override
	public boolean isEmpty() {
		return size == 0;
	}

	/**
	 * Returns index for Object x.
	 */
	static int hash(Object x, int length) {
		int h = System.identityHashCode(x);
		// Multiply by -127, and left-shift to use least bit as part of hash
		return ((h << 1) - (h << 8)) & (length - 1);
	}

	/**
	 * Circularly traverses table of size len.
	 */
	static int nextKeyIndex(int i, int len) {
		return (i + 1) % len;
	}

	/**
	 * Tests whether the specified object reference is a key in this identity
	 * hash set.
	 *
	 * @param   key   possible key
	 * @return  <code>true</code> if the specified object reference is a key
	 *          in this set
	 * @see     #containsValue(Object)
	 */
	@Override
	public boolean contains(Object key) {
		Object k = maskNull(key);
		Object[] tab = table;
		int len = tab.length;
		int i = hash(k, len);
		while (true) {
			Object item = tab[i];
			if (item == k)
				return true;
			if (item == null)
				return false;
			i = nextKeyIndex(i, len);
		}
	}

	@Override
	public boolean add(K key) {
		Object k = maskNull(key);
		Object[] tab = table;
		int len = tab.length;
		int i = hash(k, len);

		Object item;
		while ( (item = tab[i]) != null) {
			if (item == k)
				return false;
			i = nextKeyIndex(i, len);
		}

		modCount++;
		tab[i] = k;
		if (++size >= threshold)
			resize(len); // len == 2 * current capacity.
		return true;
	}

	/**
	 * Resize the table to hold given capacity.
	 *
	 * @param newCapacity the new capacity, must be a power of two.
	 */
	private void resize(int newCapacity) {
		// assert (newCapacity & -newCapacity) == newCapacity; // power of 2
		int newLength = newCapacity * 2;

		Object[] oldTable = table;
		int oldLength = oldTable.length;
		if (oldLength == 2*MAXIMUM_CAPACITY) { // can't expand any further
			if (threshold == MAXIMUM_CAPACITY-1)
				throw new IllegalStateException("Capacity exhausted.");
			threshold = MAXIMUM_CAPACITY-1;  // Gigantic set!
			return;
		}
		if (oldLength >= newLength)
			return;

		Object[] newTable = new Object[newLength];
		threshold = newLength / 3;

		for (int j = 0; j < oldLength; j++) {
			Object key = oldTable[j];
			if (key != null) {
				oldTable[j] = null;
				int i = hash(key, newLength);
				while (newTable[i] != null)
					i = nextKeyIndex(i, newLength);
				newTable[i] = key;
			}
		}
		table = newTable;
	}

	@Override
	public boolean addAll(Collection<? extends K> m) {
		int n = m.size();
		if (n == 0)
			return false;
		if (n > threshold) // conservatively pre-expand
			resize(capacity(n));

		boolean result = false;
		for (K key : m)
			result |= add(key);
		return result;
	}

	@Override
	public boolean remove(Object key) {
		Object k = maskNull(key);
		Object[] tab = table;
		int len = tab.length;
		int i = hash(k, len);

		while (true) {
			Object item = tab[i];
			if (item == k) {
				modCount++;
				size--;
				tab[i] = null;
				closeDeletion(i);
				return true;
			}
			if (item == null)
				return false;
			i = nextKeyIndex(i, len);
		}

	}

	/**
	 * Rehash all possibly-colliding entries following a
	 * deletion. This preserves the linear-probe
	 * collision properties required by get, put, etc.
	 *
	 * @param d the index of a newly empty deleted slot
	 */
	private void closeDeletion(int d) {
		// Adapted from Knuth Section 6.4 Algorithm R
		Object[] tab = table;
		int len = tab.length;

		// Look for items to swap into newly vacated slot
		// starting at index immediately following deletion,
		// and continuing until a null slot is seen, indicating
		// the end of a run of possibly-colliding keys.
		Object item;
		for (int i = nextKeyIndex(d, len); (item = tab[i]) != null;
		i = nextKeyIndex(i, len) ) {
			// The following test triggers if the item at slot i (which
			// hashes to be at slot r) should take the spot vacated by d.
			// If so, we swap it in, and then continue with d now at the
			// newly vacated i.  This process will terminate when we hit
			// the null slot at the end of this run.
			// The test is messy because we are using a circular table.
			int r = hash(item, len);
			if ((i < r && (r <= d || d <= i)) || (r <= d && d <= i)) {
				tab[d] = item;
				tab[i] = null;
				d = i;
			}
		}
	}

	/**
	 * Removes all of the elements from this set.
	 * The set will be empty after this call returns.
	 */
	@Override
	public void clear() {
		modCount++;
		Object[] tab = table;
		for (int i = 0; i < tab.length; i++)
			tab[i] = null;
		size = 0;
	}

	/**
	 * Compares the specified object with this set for equality.  Returns
	 * <tt>true</tt> if the given object is also a set and the two sets
	 * represent identical object-reference elements.  More formally, this
	 * set is equal to another set <tt>m</tt> if and only if
	 * <tt>this.entrySet().equals(m.entrySet())</tt>.
	 *
	 * <p><b>Owing to the reference-equality-based semantics of this set it is
	 * possible that the symmetry and transitivity requirements of the
	 * <tt>Object.equals</tt> contract may be violated if this set is compared
	 * to a normal set.  However, the <tt>Object.equals</tt> contract is
	 * guaranteed to hold among <tt>IdentityHashMap</tt> instances.</b>
	 *
	 * @param  o object to be compared for equality with this set
	 * @return <tt>true</tt> if the specified object is equal to this set
	 * @see Object#equals(Object)
	 */
	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		} else if (o instanceof IdentityHashSet<?>) {
			IdentityHashSet<?> m = (IdentityHashSet<?>) o;
			if (m.size() != size)
				return false;

			Object[] tab = m.table;
			for (int i = 0; i < tab.length; i++) {
				Object k = tab[i];
				if (k != null && !contains(k))
					return false;
			}
			return true;
		} else if (o instanceof Collection<?>) {
			return containsAll((Collection<?>)o);
		} else {
			return false;  // o is not a Set
		}
	}

	/**
	 * Returns the hash code value for this set.  The hash code of a set is
	 * defined to be the sum of the hash codes of each entry in the set's
	 * <tt>entrySet()</tt> view.  This ensures that <tt>m1.equals(m2)</tt>
	 * implies that <tt>m1.hashCode()==m2.hashCode()</tt> for any two
	 * <tt>IdentityHashMap</tt> instances <tt>m1</tt> and <tt>m2</tt>, as
	 * required by the general contract of {@link Object#hashCode}.
	 *
	 * <p><b>Owing to the reference-equality-based semantics of the
	 * <tt>Map.Entry</tt> instances in the set returned by this set's
	 * <tt>entrySet</tt> method, it is possible that the contractual
	 * requirement of <tt>Object.hashCode</tt> mentioned in the previous
	 * paragraph will be violated if one of the two objects being compared is
	 * an <tt>IdentityHashMap</tt> instance and the other is a normal set.</b>
	 *
	 * @return the hash code value for this set
	 * @see Object#equals(Object)
	 * @see #equals(Object)
	 */
	@Override
	public int hashCode() {
		int result = 0;
		Object[] tab = table;
		for (int i = 0; i < tab.length; i++) {
			Object key = tab[i];
			if (key != null) {
				Object k = unmaskNull(key);
				result += System.identityHashCode(k);
			}
		}
		return result;
	}

	/**
	 * Returns a shallow copy of this identity hash set: the keys and values
	 * themselves are not cloned.
	 *
	 * @return a shallow copy of this set
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Object clone() {
		try {
			IdentityHashSet<K> m = (IdentityHashSet<K>) super.clone();
			m.table = table.clone();
			return m;
		} catch (CloneNotSupportedException e) {
			throw new InternalError();
		}
	}

	final class IdentityHashSetIterator<T> implements Iterator<T> {
		int index = (size != 0 ? 0 : table.length); // current slot.
		int expectedModCount = modCount; // to support fast-fail
		int lastReturnedIndex = -1;      // to allow remove()
		boolean indexValid; // To avoid unnecessary next computation
		Object[] traversalTable = table; // reference to main table or copy

		@Override
		public boolean hasNext() {
			Object[] tab = traversalTable;
			for (int i = index; i < tab.length; i++) {
				Object key = tab[i];
				if (key != null) {
					index = i;
					return indexValid = true;
				}
			}
			index = tab.length;
			return false;
		}

		protected int nextIndex() {
			if (modCount != expectedModCount)
				throw new ConcurrentModificationException();
			if (!indexValid && !hasNext())
				throw new NoSuchElementException();

			indexValid = false;
			lastReturnedIndex = index;
			index ++;
			return lastReturnedIndex;
		}

		@Override
		@SuppressWarnings("unchecked")
		public void remove() {
			if (lastReturnedIndex == -1)
				throw new IllegalStateException();
			if (modCount != expectedModCount)
				throw new ConcurrentModificationException();

			expectedModCount = ++modCount;
			int deletedSlot = lastReturnedIndex;
			lastReturnedIndex = -1;
			size--;
			// back up index to revisit new contents after deletion
			index = deletedSlot;
			indexValid = false;

			// Removal code proceeds as in closeDeletion except that
			// it must catch the rare case where an element already
			// seen is swapped into a vacant slot that will be later
			// traversed by this iterator. We cannot allow future
			// next() calls to return it again.  The likelihood of
			// this occurring under 2/3 load factor is very slim, but
			// when it does happen, we must make a copy of the rest of
			// the table to use for the rest of the traversal. Since
			// this can only happen when we are near the end of the table,
			// even in these rare cases, this is not very expensive in
			// time or space.

			Object[] tab = traversalTable;
			int len = tab.length;

			int d = deletedSlot;
			K key = (K) tab[d];
			tab[d] = null;        // vacate the slot

			// If traversing a copy, remove in real table.
			// We can skip gap-closure on copy.
			if (tab != IdentityHashSet.this.table) {
				IdentityHashSet.this.remove(key);
				expectedModCount = modCount;
				return;
			}

			Object item;
			for (int i = nextKeyIndex(d, len); (item = tab[i]) != null;
			i = nextKeyIndex(i, len)) {
				int r = hash(item, len);
				// See closeDeletion for explanation of this conditional
				if ((i < r && (r <= d || d <= i)) ||
						(r <= d && d <= i)) {

					// If we are about to swap an already-seen element
					// into a slot that may later be returned by next(),
					// then clone the rest of table for use in future
					// next() calls. It is OK that our copy will have
					// a gap in the "wrong" place, since it will never
					// be used for searching anyway.

					if (i < deletedSlot && d >= deletedSlot &&
							traversalTable == IdentityHashSet.this.table) {
						int remaining = len - deletedSlot;
						Object[] newTable = new Object[remaining];
						System.arraycopy(tab, deletedSlot, newTable, 0, remaining);
						traversalTable = newTable;
						index = 0;
					}

					tab[d] = item;
					tab[i] = null;
					d = i;
				}
			}
		}

		@Override
		@SuppressWarnings("unchecked")
		public T next() {
			return (T) unmaskNull(traversalTable[nextIndex()]);
		}
	}

	@Override
	public Iterator<K> iterator() {
		return new IdentityHashSetIterator<K>();
	}
}
