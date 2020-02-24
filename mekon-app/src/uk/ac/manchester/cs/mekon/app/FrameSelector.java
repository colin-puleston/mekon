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

import java.util.*;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.BorderLayout;
import javax.swing.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.gui.*;

/**
 * @author Colin Puleston
 */
abstract class FrameSelector extends Selector<CFrame> {

	static private final long serialVersionUID = -1;

	static private final String FILTER_PANEL_TITLE = "Filter";
	static private final String SINGLE_SELECT_TITLE = "Select option";
	static private final String MULTI_SELECT_TITLE = SINGLE_SELECT_TITLE + "(s)";

	static private final Dimension WINDOW_SIZE = new Dimension(500, 500);

	static private final Color LEXICAL_MATCH_BACKGROUND_CLR = new Color(255,237,160);

	static private String getTitle(boolean multiSelect) {

		return multiSelect ? MULTI_SELECT_TITLE : SINGLE_SELECT_TITLE;
	}

	private CFrame rootFrame;
	private boolean forQuery;

	private OptionsTree optionsTree = new OptionsTree();
	private OptionsFilterPanel optionsFilterPanel = new OptionsFilterPanel();

	private class OptionsTree extends GActionTree {

		static private final long serialVersionUID = -1;

		private class FrameNode extends GNode {

			private CFrame frame;

			private class SelectionAction extends GNodeAction {

				protected void perform() {

					onSelection(frame);
				}
			}

			protected void addInitialChildren() {

				for (CFrame subFrame : frame.getSubs()) {

					if (optionsFilterPanel.requiredInTree(subFrame)) {

						addChild(new FrameNode(subFrame));
					}
				}
			}

			protected GNodeAction getPositiveAction1() {

				return new SelectionAction();
			}

			protected GCellDisplay getDisplay() {

				GCellDisplay display = getFrameCellDisplay(frame);

				if (optionsFilterPanel.passesFilter(frame)) {

					display.setBackgroundColour(LEXICAL_MATCH_BACKGROUND_CLR);
				}

				return display;
			}

			FrameNode(CFrame frame) {

				super(OptionsTree.this);

				this.frame = frame;
			}
		}

		OptionsTree() {

			setRootVisible(false);
			setShowsRootHandles(true);
		}

		void initialise() {

			initialise(new FrameNode(rootFrame));
		}

		CFrame getSelection() {

			return ((FrameNode)getSelectedNode()).frame;
		}
	}

	private class OptionsFilterPanel extends GTreeFilterPanel<CFrame> {

		static private final long serialVersionUID = -1;

		protected void reinitialiseTree() {

			optionsTree.reinitialise();
		}

		protected Collection<CFrame> getRootNodes() {

			return getChildNodes(rootFrame);
		}

		protected Collection<CFrame> getChildNodes(CFrame parent) {

			return parent.getSubs(CVisibility.EXPOSED);
		}

		protected String getNodeLabel(CFrame node) {

			return node.getIdentity().getLabel();
		}

		OptionsFilterPanel() {

			PanelEntitler.entitle(this, FILTER_PANEL_TITLE);
		}
	}

	FrameSelector(
		JComponent parent,
		CFrame rootFrame,
		boolean forQuery,
		boolean multiSelect,
		boolean clearRequired) {

		super(parent, getTitle(multiSelect), multiSelect, clearRequired);

		this.rootFrame = rootFrame;
		this.forQuery = forQuery;

		optionsTree.initialise();
	}

	Dimension getWindowSize() {

		return WINDOW_SIZE;
	}

	abstract void onSelection(CFrame selected);

	JPanel createSelectorPanel() {

		JPanel panel = new JPanel(new BorderLayout());

		panel.add(new JScrollPane(optionsTree), BorderLayout.CENTER);
		panel.add(optionsFilterPanel, BorderLayout.SOUTH);

		return panel;
	}

	GCellDisplay getFrameCellDisplay(CFrame frame) {

		return new GCellDisplay(frame.getDisplayLabel(), getFrameIcon());
	}

	private Icon getFrameIcon() {

		return forQuery ? MekonAppIcons.QUERY_VALUE : MekonAppIcons.ASSERTION_VALUE;
	}
}
