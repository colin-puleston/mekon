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

package uk.ac.manchester.cs.hobo.user.app.basic.custom;

import java.awt.Dimension;
import java.awt.event.*;
import javax.swing.*;

import uk.ac.manchester.cs.mekon_util.gui.*;

import uk.ac.manchester.cs.hobo.user.app.basic.model.*;

/**
 * @author Colin Puleston
 */
class TextBlockViewer extends GDialog {

	static private final long serialVersionUID = -1;

	static private final String TITLE = "Text";

	static private final Dimension WINDOW_SIZE = new Dimension(500, 300);

	private class Disposer extends WindowAdapter {

		public void windowClosing(WindowEvent e) {

			dispose();
		}
	}

	TextBlockViewer(TextBlock valueObj) {

		super(TITLE, true);

		setPreferredSize(WINDOW_SIZE);
		addWindowListener(new Disposer());

		display(new JScrollPane(createTextArea(valueObj)));
	}

	private JTextArea createTextArea(TextBlock valueObj) {

		JTextArea area = new JTextArea();

		area.setEditable(false);
		area.setFont(GFonts.toMedium(area.getFont()));

		if (valueObj.text.isSet()) {

			area.setText(valueObj.text.get());
		}

		return area;
	}
}
