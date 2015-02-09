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
class CModelFrames {

	static final CModelFrames INERT_INSTANCE = new CModelFrames() {

		void add(CModelFrame frame) {

			onAttemptedUpdate();
		}

		void remove(CModelFrame frame) {

			onAttemptedUpdate();
		}

		private void onAttemptedUpdate() {

			throw new Error("Illegal updating of inert object!");
		}
	};

	private List<CModelFrame> frames = new ArrayList<CModelFrame>();

	private abstract class Getter<F extends CFrame> {

		List<F> getAll(CVisibility visibility) {

			return visibility == CVisibility.ALL ? getAll() : select(visibility);
		}

		abstract List<F> getAll();

		abstract void addSelection(List<F> selected, CModelFrame frame);

		private List<F> select(CVisibility visibility) {

			List<F> selected = new ArrayList<F>();

			for (CModelFrame frame : frames) {

				if (visibility.coversHiddenStatus(frame.hidden())) {

					addSelection(selected, frame);
				}
			}

			return selected;
		}
	}

	private class FrameGetter extends Getter<CFrame> {

		List<CFrame> getAll() {

			return new ArrayList<CFrame>(frames);
		}

		void addSelection(List<CFrame> selected, CModelFrame frame) {

			selected.add(frame);
		}
	}

	private class ModelFrameGetter extends Getter<CModelFrame> {

		List<CModelFrame> getAll() {

			return new ArrayList<CModelFrame>(frames);
		}

		void addSelection(List<CModelFrame> selected, CModelFrame frame) {

			selected.add(frame);
		}
	}

	void add(CModelFrame frame) {

		frames.add(frame);
	}

	void remove(CModelFrame frame) {

		frames.remove(frame);
	}

	boolean isEmpty() {

		return frames.isEmpty();
	}

	boolean contains(CModelFrame frame) {

		return frames.contains(frame);
	}

	List<CModelFrame> getAll() {

		return new ModelFrameGetter().getAll();
	}

	List<CModelFrame> getAll(CVisibility visibility) {

		return new ModelFrameGetter().getAll(visibility);
	}

	List<CFrame> asFrames(CVisibility visibility) {

		return new FrameGetter().getAll(visibility);
	}
}
