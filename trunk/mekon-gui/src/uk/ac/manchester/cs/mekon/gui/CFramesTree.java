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

package uk.ac.manchester.cs.mekon.gui;

import java.util.*;
import javax.swing.tree.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.util.*;

import uk.ac.manchester.cs.mekon.gui.util.*;

/**
 * @author Colin Puleston
 */
class CFramesTree extends CTree {

	static private final long serialVersionUID = -1;

	private CFrameVisibility visibility;
	private KListMap<CFrame, CFrameNode> frameNodeLists
						= new KListMap<CFrame, CFrameNode>();

	private SelectionRelay selectionRelay = new SelectionRelay();

	private class DummyRootNode extends GNode {

		private List<CFrame> rootFrames;

		protected void addInitialChildren() {

			for (CFrame rootFrame : rootFrames) {

				addSubRootNode(new CFrameNode(CFramesTree.this, rootFrame));
			}
		}

		protected GCellDisplay getDisplay() {

			return GCellDisplay.NO_DISPLAY;
		}

		DummyRootNode(List<CFrame> rootFrames) {

			super(CFramesTree.this);

			this.rootFrames = rootFrames;
		}

		private void addSubRootNode(CFrameNode subRootNode) {

			addChild(subRootNode);
			addFrameNode(subRootNode);
		}
	}

	private class SelectionRelay extends CFrameSelectionRelay {

		void addUpdateListener(CFrameSelectionListener listener) {

			addSelectionListener(listener);
		}

		void update(CFrame selection) {

			select(selection);
		}
	}

	CFramesTree(CFrame rootFrame, CFrameVisibility visibility, boolean showRoot) {

		this(visibility, showRoot);

		addFrameNode(initialise(rootFrame));
	}

	CFramesTree(List<CFrame> rootFrames, CFrameVisibility visibility) {

		this(visibility, false);

		initialise(new DummyRootNode(rootFrames));
	}

	Boolean leafCFrameNodeFastCheck(CFrameNode node) {

		return node.getCFrame().getSubs(visibility).isEmpty();
	}

	void addCFrameChildren(CFrameNode parent) {

		for (CFrameNode node : parent.addSubFrameNodes(visibility)) {

			addFrameNode(node);
		}
	}

	void select(CFrame frame) {

		List<CFrameNode> frameNodes = ensureFrameNodesIfAny(frame);

		if (frameNodes != null && !selectFirstVisible(frameNodes)) {

			selectFirst(frameNodes);
		}
	}

	CFrameSelectionRelay getSelectionRelay() {

		return selectionRelay;
	}

	private CFramesTree(CFrameVisibility visibility, boolean showRoot) {

		this.visibility = visibility;

		if (!showRoot) {

			setRootVisible(false);
			setShowsRootHandles(true);
		}
	}

	private void addFrameNode(CFrameNode node) {

		frameNodeLists.add(node.getCFrame(), node);
	}

	private List<CFrameNode> ensureFrameNodesIfAny(CFrame frame) {

		if (!frameNodeLists.containsKey(frame)) {

			expandFirstPathThrough(getFirstSuper(frame));
		}

		return frameNodeLists.getList(frame);
	}

	private void expandFirstPathThrough(CFrame frame) {

		for (CFrameNode node : ensureFrameNodesIfAny(frame)) {

			node.checkExpanded();

			break;
		}
	}

	private CFrame getFirstSuper(CFrame frame) {

		return frame.getSupers().get(0);
	}

	private boolean selectFirstVisible(List<CFrameNode> frameNodes) {

		for (CFrameNode node : frameNodes) {

			TreePath path = node.getTreePath();

			if (isVisible(path)) {

				selectAndEnsureRowVisible(path);

				return true;
			}
		}

		return false;
	}

	private void selectFirst(List<CFrameNode> frameNodes) {

		for (CFrameNode node : frameNodes) {

			selectAndEnsureRowVisible(node.getTreePath());

			break;
		}
	}

	private void selectAndEnsureRowVisible(TreePath path) {

		setSelectionPath(path);
		scrollRowToVisible(getSelectionRows()[0]);
	}
}
