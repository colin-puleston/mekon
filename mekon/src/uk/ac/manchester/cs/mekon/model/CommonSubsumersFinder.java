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

/**
 * @author Colin Puleston
 */
class CommonSubsumersFinder {

	private CFrameVisibility visibility;

	CommonSubsumersFinder(CFrameVisibility visibility) {

		this.visibility = visibility;
	}

	CFrame getClosestSingle(Collection<? extends CFrame> frames) {

		Set<CFrame> closests = getAllClosest(frames);

		return closests.size() == 1
					? closests.iterator().next()
					: getClosestSingle(closests);
	}

	Set<CFrame> getAllClosest(Collection<? extends CFrame> frames) {

		Set<CFrame> commons = getAllCommon(frames);

		for (CFrame common : new HashSet<CFrame>(commons)) {

			commons.removeAll(common.getSupers(visibility));
		}

		return commons;
	}

	private Set<CFrame> getAllCommon(Collection<? extends CFrame> frames) {

		Set<CFrame> commons = new HashSet<CFrame>();

		for (CFrame frame : frames) {

			Set<CFrame> subs = getSubsumers(frame);

			if (commons.isEmpty()) {

				commons.addAll(subs);
			}
			else {

				commons.retainAll(subs);
			}
		}

		return commons;
	}

	private Set<CFrame> getSubsumers(CFrame frame) {

		return frame.getSubsumers(visibility);
	}
}
