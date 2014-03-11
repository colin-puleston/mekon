/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 University of Manchester
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package uk.ac.manchester.cs.mekon.util;

import java.util.*;

import uk.ac.manchester.cs.mekon.*;

/**
 * Base-class for classes that represent ordered lists, whose
 * update-operations can be listened to. Provides standard and
 * some non-standard list-operations, plus methods for attaching
 * listeners to listen for the results of those operations.
 *
 * @author Colin Puleston
 */
public abstract class KList<V> {

	private List<V> values = new ArrayList<V>();

	private List<KUpdateListener> updateListeners = new ArrayList<KUpdateListener>();
	private List<KValuesListener<V>> valuesListeners = new ArrayList<KValuesListener<V>>();

	/**
	 * Adds a general-update listener to the list.
	 *
	 * @param listener Listener to add
	 */
	public void addUpdateListener(KUpdateListener listener) {

		updateListeners.add(listener);
	}

	/**
	 * Removes a general-update listener to the list. If specified
	 * listener is not a currenly registered listener then does
	 * nothing.
	 *
	 * @param listener Listener to remove
	 */
	public void removeUpdateListener(KUpdateListener listener) {

		updateListeners.remove(listener);
	}

	/**
	 * Adds a listener for specific types of list-value updates.
	 *
	 * @param listener Listener to add
	 */
	public void addValuesListener(KValuesListener<V> listener) {

		valuesListeners.add(listener);
	}

	/**
	 * Removes a listener for specific types of list-value updates.
	 * If specified listener is not a currenly registered listener,
	 * does nothing.
	 *
	 * @param listener Listener to remove
	 */
	public void removeValuesListener(KValuesListener<V> listener) {

		valuesListeners.remove(listener);
	}

	/**
	 * Tests for equality between this and other specified object.
	 *
	 * @param other Object to test for equality with this one
	 * @return true if other object is another <code>KList</code>
	 * containing the same ordered set of values
	 */
	public boolean equals(Object other) {

		if (other instanceof KList) {

			return ((KList)other).values.equals(values);
		}

		return false;
	}

	/**
	 * Provides hash-code based on ordered set of values.
	 *
	 * @return hash-code for this object
	 */
	public int hashCode() {

		return values.hashCode();
	}

	/**
	 * Specifies the current size of the list.
	 *
	 * @return Size of list
	 */
	public int size() {

		return values.size();
	}

	/**
	 * Tests whether the list is currently empty.
	 *
	 * @return True if empty
	 */
	public boolean isEmpty() {

		return values.isEmpty();
	}

	/**
	 * Tests whether the list contains the specified value.
	 *
	 * @param value Value to look for
	 * @return True list contains required value
	 */
	public boolean contains(V value) {

		return values.contains(value);
	}

	/**
	 * Provides contents of list as a <code>List</code> object.
	 *
	 * @return Ordered list contents
	 */
	public List<V> asList() {

		return new ArrayList<V>(values);
	}

	/**
	 * Provides contents of list as a <code>Set</code> object.
	 *
	 * @return List contents
	 */
	public Set<V> asSet() {

		return new HashSet<V>(values);
	}

	/**
	 * Constructor.
	 */
	protected KList() {
	}

	/**
	 * Constructor.
	 *
	 * @param values Values to be added to list
	 */
	protected KList(Collection<V> values) {

		addAllValues(values);
	}

	/**
	 * Adds specified value to the list (if not already present).
	 *
	 * @param value Value to add
	 * @return True if value was added (i.e. not already present)
	 */
	protected boolean addValue(V value) {

		if (addNewValue(value)) {

			pollListenersForUpdate();

			return true;
		}

		return false;
	}

	/**
	 * Adds all specified values to the list (if not already present).
	 *
	 * @param values Values to add
	 * @return All values that were added (i.e. those not already
	 * present)
	 */
	protected List<V> addAllValues(Collection<V> values) {

		List<V> additions = new ArrayList<V>();

		for (V value : values) {

			if (addNewValue(value)) {

				additions.add(value);
			}
		}

		if (!additions.isEmpty()) {

			pollListenersForUpdate();
		}

		return additions;
	}

	/**
	 * Removes specified value from the list (if present).
	 *
	 * @param value Value to remove
	 * @return True if value was removed (i.e. if present)
	 */
	protected boolean removeValue(V value) {

		if (removeOldValue(value)) {

			pollListenersForUpdate();

			return true;
		}

		return false;
	}

	/**
	 * Removes value at specified index from the list.
	 *
	 * @param index Index of value to remove
	 * @throws KAccessException if illegal index
	 */
	protected void removeValue(int index) {

		if (index > values.size()) {

			throw new KAccessException("Illegal value-index: " + index);
		}

		removeValue(values.get(index));
	}

	/**
	 * Removes all values from the list.
	 */
	protected void clearValues() {

		if (!values.isEmpty()) {

			List<V> cleared = new ArrayList<V>(values);

			values.clear();

			pollListenersForCleared(cleared);
			pollListenersForUpdate();
		}
	}

	/**
	 * Updates the list so that it contains each of the specified
	 * values, and only those values, making any required additions
	 * and deletions. Where relevant, will maintain the current list
	 * ordering in preference to the supplied list.
	 *
	 * @param latestValues Values that list is to contain
	 */
	protected void updateValues(List<V> latestValues) {

		boolean removals = removeOldValues(latestValues);
		boolean additions = addNewValues(latestValues);

		if (additions || removals) {

			pollListenersForUpdate();
		}
	}

	private boolean addNewValues(List<V> latestValues) {

		boolean additions = false;
		List<V> previousValues = new ArrayList<V>(values);

		for (V value : latestValues) {

			if (!previousValues.contains(value)) {

				addNewValue(value);

				additions = true;
			}
		}

		return additions;
	}

	private boolean removeOldValues(List<V> latestValues) {

		boolean removals = false;

		for (V value : new ArrayList<V>(values)) {

			if (!latestValues.contains(value)) {

				removeOldValue(value);

				removals = true;
			}
		}

		return removals;
	}

	private boolean addNewValue(V value) {

		if (!values.contains(value)) {

			values.add(value);
			pollListenersForAdded(value);

			return true;
		}

		return false;
	}

	private boolean removeOldValue(V value) {

		if (values.remove(value)) {

			pollListenersForRemoved(value);

			return true;
		}

		return false;
	}

	private void pollListenersForUpdate() {

		for (KUpdateListener listener : copyUpdateListeners()) {

			listener.onUpdated();
		}
	}

	private void pollListenersForAdded(V value) {

		for (KValuesListener<V> listener : copyValuesListeners()) {

			listener.onAdded(value);
		}
	}

	private void pollListenersForRemoved(V value) {

		for (KValuesListener<V> listener : copyValuesListeners()) {

			listener.onRemoved(value);
		}
	}

	private void pollListenersForCleared(List<V> values) {

		for (KValuesListener<V> listener : copyValuesListeners()) {

			listener.onCleared(values);
		}
	}

	private List<KUpdateListener> copyUpdateListeners() {

		return new ArrayList<KUpdateListener>(updateListeners);
	}

	private List<KValuesListener<V>> copyValuesListeners() {

		return new ArrayList<KValuesListener<V>>(valuesListeners);
	}
}
