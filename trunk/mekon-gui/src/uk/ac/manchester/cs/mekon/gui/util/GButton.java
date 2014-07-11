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
package uk.ac.manchester.cs.mekon.gui.util;

import java.awt.event.*;
import javax.swing.*;

/**
 * @author Colin Puleston
 */
public abstract class GButton extends JButton implements ActionListener {

	static private final long serialVersionUID = -1;

	static private final float STANDARD_FONT_SIZE = 16;
	static private final float SMALL_FONT_SIZE = 12;

	public GButton(String label) {

		super(label);

		setFontSize(STANDARD_FONT_SIZE);
		addActionListener(this);
	}

	public void actionPerformed(ActionEvent event) {

		doButtonThing();
	}

	public void setSmallFont() {

		setFontSize(SMALL_FONT_SIZE);
	}

	public void setFontSize(float size) {

		setFont(getFont().deriveFont(size));
	}

	protected abstract void doButtonThing();
}
