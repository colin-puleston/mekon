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

package uk.ac.manchester.cs.mekon.store.disk;

import java.io.*;
import java.util.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.store.*;
import uk.ac.manchester.cs.mekon.store.disk.zlink.*;

/**
 * Provides mechanisms for building a disk-based instance-store, as
 * represented by an {@link IStore} object.
 *
 * @author Colin Puleston
 */
public class IDiskStoreBuilder {

	static {

		ZIDiskStoreAccessor.set(new ZIDiskStoreAccessorImpl());
	}

	private CModel model;
	private List<IMatcher> matchers = new ArrayList<IMatcher>();

	private File directory = null;

	/**
	 * Sets the directory for instance-store serialisation.
	 * Defaults to the current directory.
	 *
	 * @param directory Relevant serialisation directory
	 */
	public void setStoreDirectory(File directory) {

		this.directory = directory;
	}

	/**
	 * Sets the directory for instance-store serialisation to be
	 * the default-named directory within the specified parent
	 * directory.
	 *
	 * @param parentDir Relevant parent-directory
	 */
	public void setDefaultNamedStoreDirectory(File parentDir) {

		directory = FileStore.getDefaultNamedDirectory(parentDir);
	}

	/**
	 * Adds an instance-matcher to the instance-store.
	 *
	 * @param matcher Instance-matcher to add
	 */
	public void addMatcher(IMatcher matcher) {

		matchers.add(matcher);
	}

	/**
	 * Removes an instance-matcher from the instance-store.
	 *
	 * @param matcher Instance-matcher to remove
	 */
	public void removeMatcher(IMatcher matcher) {

		matchers.remove(matcher);
	}

	/**
	 * Adds an instance-matcher to the instance-store, inserting
	 * it at the specified position in the ordered list.
	 *
	 * @param matcher Instance-matcher to add
	 * @param index Index at which to insert matcher
	 */
	public void insertMatcher(IMatcher matcher, int index) {

		matchers.add(index, matcher);
	}

	/**
	 * Replaces an instance-matcher for the instance-store, placing
	 * the new matcher at the same position as the old in the ordered
	 * list.
	 *
	 * @param oldMatcher Instance-matcher to be replaced
	 * @param newMatcher Replacement instance-matcher
	 */
	public void replaceMatcher(IMatcher oldMatcher, IMatcher newMatcher) {

		matchers.set(matchers.indexOf(oldMatcher), newMatcher);
	}

	/**
	 * Provides all matchers that have been registered.
	 *
	 * @return All registered matchers
	 */
	public List<IMatcher> getMatchers() {

		return new ArrayList<IMatcher>(matchers);
	}

	/**
	 * Creates the {@link IStore} object and loads any matchers that
	 * have been registered with the relevant instance data.
	 *
	 * @return Created store object
	 */
	public IStore build() {

		IDiskStore store = new IDiskStore(model, matchers, directory);

		StoreRegister.add(store);
		store.initialisePostRegistration();

		return store;
	}

	IDiskStoreBuilder(CModel model) {

		this.model = model;
	}
}
