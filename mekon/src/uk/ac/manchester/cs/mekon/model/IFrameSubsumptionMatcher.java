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

package uk.ac.manchester.cs.mekon.model;

import java.util.*;

import uk.ac.manchester.cs.mekon.util.*;

/**
 * @author Colin Puleston
 */
class IFrameSubsumptionMatcher extends IFrameMatcher {

	boolean typesMatch(CValue<?> type1, CValue<?> type2) {

		return type1.subsumes(type2);
	}

	boolean listSizesMatch(KList<?> list1, KList<?> list2) {

		return list1.size() <= list2.size();
	}

	boolean slotsMatch(ISlots slots1, ISlots slots2) {

		for (ISlot slot1 : slots1.asList()) {

			ISlot slot2 = slots2.getOrNull(slot1.getType().getIdentity());

			if (slot2 == null || !slotValuesMatch(slot1, slot2)) {

				return false;
			}
		}

		return true;
	}

	boolean valuesMatch(List<IValue> values1, List<IValue> values2) {

		for (IValue value1 : values1) {

			if (!valueMatched(value1, values2)) {

				return false;
			}
		}

		return true;
	}

	private boolean valueMatched(IValue value1, List<IValue> values2) {

		for (IValue value2 : values2) {

			if (valuesMatch(value1, value2)) {

				return true;
			}
		}

		return false;
	}
}
