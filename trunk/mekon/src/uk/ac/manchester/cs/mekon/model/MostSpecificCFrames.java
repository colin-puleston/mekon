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
 * Responsible for finding the most-specific members of a specified
 * set of concept-level frames.
 *
 * @author Colin Puleston
 */
public class MostSpecificCFrames {

	private List<CFrame> mostSpecifics = new ArrayList<CFrame>();

	private Set<CFrame> leafs = new HashSet<CFrame>();
	private Set<CFrame> mostSpecificNonLeafs = new HashSet<CFrame>();

	/**
	 * Constructor.
	 */
	public MostSpecificCFrames() {
	}

	/**
	 * Constructor.
	 *
	 * @param frames Frames with which to initialise the set of
	 * most-specific-frames.
	 */
	public MostSpecificCFrames(Collection<CFrame> frames) {

		update(frames);
	}

	/**
	 * Uses the supplied set of frames to update the current set of
	 * most-specific-frames.
	 *
	 * @param newFrames Frames for update
	 */
	public void update(Collection<CFrame> newFrames) {

		for (CFrame newFrame : newFrames) {

			update(newFrame);
		}
	}

	/**
	 * Uses the supplied frame to update the current set of
	 * most-specific-frames.
	 *
	 * @param newFrame Frame for update
	 */
	public void update(CFrame newFrame) {

		if (newFrame.getSubs().isEmpty()) {

			if (leafs.add(newFrame)) {

				mostSpecifics.add(newFrame);
				removeNonLeafSubsumers(newFrame);
			}
		}
		else {

			if (!mostSpecificNonLeafs.contains(newFrame)
				&& !subsumesLeafFrame(newFrame)) {

				updateNonLeafFrames(newFrame);
			}
		}
	}

	/**
	 * Retrieves the current set of most-specific-frames.
	 *
	 * @return Current set of most-specific-frames
	 */
	public List<CFrame> getMostSpecific() {

		return new ArrayList<CFrame>(mostSpecifics);
	}

	private void updateNonLeafFrames(CFrame newFrame) {

		for (CFrame nonLeaf : new HashSet<CFrame>(mostSpecificNonLeafs)) {

			if (newFrame.subsumes(nonLeaf)) {

				return;
			}

			if (nonLeaf.subsumes(newFrame)) {

				removeMostSpecificNonLeaf(nonLeaf);
			}
		}

		addMostSpecificNonLeaf(newFrame);
	}

	private void removeNonLeafSubsumers(CFrame newFrame) {

		for (CFrame nonLeaf : new HashSet<CFrame>(mostSpecificNonLeafs)) {

			if (nonLeaf.subsumes(newFrame)) {

				removeMostSpecificNonLeaf(nonLeaf);
			}
		}
	}

	private boolean subsumesLeafFrame(CFrame newFrame) {

		for (CFrame leaf : leafs) {

			if (newFrame.subsumes(leaf)) {

				return true;
			}
		}

		return false;
	}

	private void addMostSpecificNonLeaf(CFrame nonLeaf) {

		mostSpecifics.add(nonLeaf);
		mostSpecificNonLeafs.add(nonLeaf);
	}

	private void removeMostSpecificNonLeaf(CFrame nonLeaf) {

		mostSpecifics.remove(nonLeaf);
		mostSpecificNonLeafs.remove(nonLeaf);
	}
}
