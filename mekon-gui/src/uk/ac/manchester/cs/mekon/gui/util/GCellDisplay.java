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

package uk.ac.manchester.cs.mekon.gui.util;

import java.awt.Font;
import javax.swing.*;

/**
 * @author Colin Puleston
 */
public class GCellDisplay implements Comparable<GCellDisplay> {

	static public final GCellDisplay NO_DISPLAY = new GCellDisplay("");

	private String label;
	private Icon icon;
	private int fontStyleId;

	public GCellDisplay(GCellDisplay template) {

		this(template.label, template.icon, template.fontStyleId);
	}

	public GCellDisplay(String label) {

		this(label, null);
	}

	public GCellDisplay(String label, Icon icon) {

		this(label, icon, Font.PLAIN);
	}

	public GCellDisplay(String label, Icon icon, int fontStyleId) {

		this.label = label;
		this.icon = icon;
		this.fontStyleId = fontStyleId;
	}

	public int compareTo(GCellDisplay other) {

		return label.toLowerCase().compareTo(other.label.toLowerCase());
	}

	public String getLabel() {

		return label;
	}

	public Icon getIcon() {

		return icon;
	}

	public Font customiseFont(Font font) {

		return font.deriveFont(fontStyleId);
	}
}
