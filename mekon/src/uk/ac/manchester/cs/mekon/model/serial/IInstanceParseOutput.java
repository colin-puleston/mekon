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

package uk.ac.manchester.cs.mekon.model.serial;

import java.util.*;

import uk.ac.manchester.cs.mekon.model.*;

/**
 * Represents the output-data from a specific {@link IFrame}/{@link ISlot}
 * network parsing operation.
 *
 * @author Colin Puleston
 */
public class IInstanceParseOutput {

	private CIdentity rootTypeId;
	private IFrame rootFrame;

	private IInstanceParseStatus status;

	private List<IPath> prunedPaths = new ArrayList<IPath>();
	private List<IPath> prunedSlotPaths = new ArrayList<IPath>();
	private List<IPath> prunedValuePaths = new ArrayList<IPath>();

	/**
	 * Provides root-frame type identity as produced by parsing process,
	 * which may or may not represent a currently valid {@link CFrame}
	 * (see {@link #getStatus}).
	 *
	 * @return Root-frame type identity
	 */
	public CIdentity getRootTypeId() {

		return rootTypeId;
	}

	/**
	 * Provides root-frame of network generated by parsing process
	 *
	 * @return Root-frame of generated network, or null if root-frame
	 * type no longer valid
	 */
	public IFrame getRootFrame() {

		return rootFrame;
	}

	/**
	 * Provides the status of the completed parse process.
	 *
	 * @return Status of parse process
	 */
	public IInstanceParseStatus getStatus() {

		return status;
	}

	/**
	 * Provides list of any slots or slot-values that have been
	 * pruned from the generated network as a result of updates
	 * to the model since the instance was serialised.
	 *
	 * @return List of pruned slots
	 */
	public List<IPath> getAllPrunedPaths() {

		return new ArrayList<IPath>(prunedPaths);
	}

	/**
	 * Provides list of any slots that have been pruned from the
	 * generated network as a result of updates to the model since
	 * the instance was serialised.
	 *
	 * @return List of pruned slots
	 */
	public List<IPath> getPrunedSlotPaths() {

		return new ArrayList<IPath>(prunedSlotPaths);
	}

	/**
	 * Provides list of any slot-values that have been pruned from
	 * the generated network as a result of updates to the model since
	 * the instance was serialised.
	 *
	 * @return List of pruned slot-values
	 */
	public List<IPath> getPrunedValuePaths() {

		return new ArrayList<IPath>(prunedValuePaths);
	}

	IInstanceParseOutput(IFrame rootFrameIfValid, boolean validRoot, PruningData pruningData) {

		rootTypeId = rootFrameIfValid.getType().getIdentity();
		rootFrame = validRoot ? rootFrameIfValid : null;

		prunedPaths = pruningData.getAllPrunedPaths();
		prunedSlotPaths = pruningData.getPrunedSlotPaths();
		prunedValuePaths = pruningData.getPrunedValuePaths();

		status = determineStatus();
	}

	private IInstanceParseStatus determineStatus() {

		if (rootFrame == null) {

			return IInstanceParseStatus.FULLY_INVALID;
		}

		if (prunedPaths.isEmpty()) {

			return IInstanceParseStatus.FULLY_VALID;
		}

		return IInstanceParseStatus.PARTIALLY_VALID;
	}
}