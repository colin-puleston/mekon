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

package uk.ac.manchester.cs.mekon.owl.build;

import uk.ac.manchester.cs.mekon.model.*;

/**
 * Represents a set of attributes that will determine the details
 * of the entities generated in the Frames Model (FM) for a
 * particular OWL property.
 *
 * @author Colin Puleston
 */
public class OBPropertyAttributes extends OBAttributes<OBPropertyAttributes> {

	private boolean frameSource = false;

	private CCardinality slotCardinality = CCardinality.FREE;
	private CEditability slotEditability = CEditability.DEFAULT;

	/**
	 * Used to specifiy whether the property, in addition to any
	 * slots that it will be used to generate, will also be used to
	 * generate a frame in the frames-model. Defaults to false if
	 * method is never invoked.
	 *
	 * @param frameSource True if frames are to be generated for
	 * property
	 */
	public void setFrameSource(boolean frameSource) {

		this.frameSource = frameSource;
	}

	/**
	 * Sets the cardinality status for the frames-model slots that
	 * will be generated for the property. Defaults to
	 * {@link CCardinality#FREE} if method is never invoked.
	 *
	 * @param slotCardinality Cardinality status for generated slots
	 */
	public void setSlotCardinality(CCardinality slotCardinality) {

		this.slotCardinality = slotCardinality;
	}

	/**
	 * Sets the editability status for the frames-model slots that
	 * will be generated for the property. Defaults to
	 * {@link CEditability#DEFAULT} if method is never invoked.
	 *
	 * @param slotEditability Editability status for generated slots
	 */
	public void setSlotEditability(CEditability slotEditability) {

		this.slotEditability = slotEditability;
	}

	OBPropertyAttributes combineWith(OBPropertyAttributes other) {

		OBPropertyAttributes combined = new OBPropertyAttributes();

		combined.setFrameSource(frameSource || other.frameSource());
		combined.setSlotCardinality(combineSlotCardinalities(other));
		combined.setSlotEditability(combineSlotEditabilities(other));

		return combined;
	}

	boolean frameSource() {

		return frameSource;
	}

	CCardinality getSlotCardinality() {

		return slotCardinality;
	}

	CEditability getSlotEditability() {

		return slotEditability;
	}

	private CCardinality combineSlotCardinalities(OBPropertyAttributes other) {

		return slotCardinality.getMoreRestrictive(other.getSlotCardinality());
	}

	private CEditability combineSlotEditabilities(OBPropertyAttributes other) {

		return slotEditability.getStrongest(other.getSlotEditability());
	}
}
