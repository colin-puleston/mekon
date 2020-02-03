/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2019 University of Manchester
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files the "Software", to deal
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

package uk.ac.manchester.cs.mekon.app;

import uk.ac.manchester.cs.mekon.model.*;

/**
 * @author Colin Puleston
 */
class DefaultQueryNameGenerator {

	static private final String NAME_BODY = "QUERY-";

	static boolean generatedNameFormat(String queryName) {

		return lookForIndex(queryName) != null;
	}

	static private Integer lookForIndex(String queryName) {

		if (queryName.startsWith(NAME_BODY)) {

			return toIntegerOrNull(queryName.substring(NAME_BODY.length()));
		}

		return null;
	}

	static private Integer toIntegerOrNull(String value) {

		try {

			return Integer.parseInt(value);
		}
		catch (NumberFormatException e) {

			return null;
		}
	}

	private InstanceType instanceType;

	DefaultQueryNameGenerator(InstanceType instanceType) {

		this.instanceType = instanceType;
	}

	String getNext() {

		return NAME_BODY + getNextIndex();
	}

	private int getNextIndex() {

		int nextIndex = 1;

		for (CIdentity queryId : instanceType.getQueryIdsList().getAllIds()) {

			Integer index = lookForIndex(queryId.getLabel());

			if (index != null && index >= nextIndex) {

				nextIndex = index + 1;
			}
		}

		return nextIndex;
	}
}