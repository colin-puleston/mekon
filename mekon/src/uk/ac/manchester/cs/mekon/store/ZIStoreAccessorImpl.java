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

package uk.ac.manchester.cs.mekon.store;

import java.util.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.mechanism.*;
import uk.ac.manchester.cs.mekon.mechanism.core.*;
import uk.ac.manchester.cs.mekon.config.*;

/**
 * @author Colin Puleston
 */
class ZIStoreAccessorImpl extends ZIStoreAccessor {

	private Map<CModel, IStoreInitialiser> storeInitialisers
						= new HashMap<CModel, IStoreInitialiser>();

	public void createStore(CModel model) {

		IStoreManager.create(model);

		storeInitialisers.put(model, createStoreInitialiser(model));
	}

	public IStoreInitialiser getStoreInitialiser(CModel model) {

		IStoreInitialiser initialiser = storeInitialisers.get(model);

		if (initialiser == null) {

			throw new KSystemConfigException("Store has not been created for model");
		}

		return initialiser;
	}

	public void checkStopStore(CModel model) {

		IStoreManager.checkStop(model);
	}

	private IStoreInitialiser createStoreInitialiser(CModel model) {

		return new IStoreInitialiserImpl(IStoreManager.get(model));
	}
}
