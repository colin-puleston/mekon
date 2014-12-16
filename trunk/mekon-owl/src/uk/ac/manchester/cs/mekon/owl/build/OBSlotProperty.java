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

import java.util.*;

import org.semanticweb.owlapi.model.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.mechanism.*;
import uk.ac.manchester.cs.mekon.owl.util.*;

/**
 * @author Colin Puleston
 */
class OBSlotProperty extends OIdentified {

	private OWLProperty property;
	private boolean mirrorAsFrame = false;
	private boolean abstractAssertable = false;

	private SortedSet<OBSlotProperty> subProperties = new TreeSet<OBSlotProperty>();
	private CFrame mirroredCFrame = null;

	OBSlotProperty(OWLProperty property, String label) {

		super(property, label);

		this.property = property;
	}

	void setMirrorAsFrame() {

		mirrorAsFrame = true;
	}

	void setAbstractAssertable() {

		abstractAssertable = true;
	}

	void addSubProperty(OBSlotProperty subProperty) {

		subProperties.add(subProperty);
	}

	boolean mirrorAsFrame() {

		return mirrorAsFrame;
	}

	boolean abstractAssertable() {

		return abstractAssertable;
	}

	void createCProperty(CBuilder builder, OBAnnotations annotations) {

		CProperty cProperty = builder.resolveProperty(getIdentity());

		annotations.checkAdd(builder, cProperty, property);
	}

	void ensureMirrorCFrameStructure(CBuilder builder) {

		if (mirrorAsFrame) {

			ensureSubFrameLinkedCFrame(builder);
		}
	}

	private void ensureSubFrameLinkedCFrame(CBuilder builder) {

		CFrame cFrame = ensureMirrorCFrame(builder);

		for (OBSlotProperty subProp : subProperties) {

			CFrame subCFrame = subProp.ensureMirrorCFrame(builder);

			builder.getFrameEditor(subCFrame).addSuper(cFrame);
		}
	}

	private CFrame ensureMirrorCFrame(CBuilder builder) {

		if (mirroredCFrame == null) {

			mirroredCFrame = builder.resolveFrame(getIdentity(), false);
		}

		return mirroredCFrame;
	}
}
