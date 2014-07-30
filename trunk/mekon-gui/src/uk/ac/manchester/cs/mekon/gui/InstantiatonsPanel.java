/**
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

import java.awt.*;
import javax.swing.*;

import uk.ac.manchester.cs.mekon.model.*;

import uk.ac.manchester.cs.mekon.gui.util.*;

/**
 * @author Colin Puleston
 */
class InstantiatonsPanel extends JPanel {

	static private final long serialVersionUID = -1;

	static private final String CONCRETE_BUTTON_LABEL = "Concrete Instance";
	static private final String QUERY_BUTTON_LABEL = "Query Instance";

	private CFramesTree modelTree;
	private CFrame frame;

	private abstract class InstantiateButton extends GButton {

		static private final long serialVersionUID = -1;

		protected void doButtonThing() {

			new InstantiationFrame(modelTree, instantiate()).display();
		}

		InstantiateButton(String label) {

			super(label + "...");

			setEnabled(instantiable());
		}

		boolean instantiable() {

			return frame.instantiable();
		}

		abstract IFrame instantiate();
	}

	private class InstantiateConcreteButton extends InstantiateButton {

		static private final long serialVersionUID = -1;

		InstantiateConcreteButton() {

			super(CONCRETE_BUTTON_LABEL);
		}

		IFrame instantiate() {

			return frame.instantiate();
		}
	}

	private class InstantiateQueryButton extends InstantiateButton {

		static private final long serialVersionUID = -1;

		InstantiateQueryButton() {

			super(QUERY_BUTTON_LABEL);
		}

		boolean instantiable() {

			return queriesEnabled() && super.instantiable();
		}

		IFrame instantiate() {

			return frame.instantiateQuery();
		}
	}

	InstantiatonsPanel(CFramesTree modelTree, CFrame frame) {

		super(new FlowLayout());

		this.modelTree = modelTree;
		this.frame = frame;

		add(new InstantiateConcreteButton());
		add(new InstantiateQueryButton());
	}

	private boolean queriesEnabled() {

		return frame.getModel().queriesEnabled();
	}
}
