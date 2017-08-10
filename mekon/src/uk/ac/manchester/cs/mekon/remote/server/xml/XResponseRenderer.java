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

package uk.ac.manchester.cs.mekon.remote.server.xml;

import java.util.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.model.serial.*;
import uk.ac.manchester.cs.mekon.store.*;
import uk.ac.manchester.cs.mekon.store.serial.*;
import uk.ac.manchester.cs.mekon.xdoc.*;
import uk.ac.manchester.cs.mekon.remote.xml.*;

/**
 * XXX.
 *
 * @author Colin Puleston
 */
public class XResponseRenderer extends XPackageSerialiser implements XResponseVocab {

	private IInstanceRenderer instanceRenderer = new IInstanceRenderer();

	/**
	 * XXX.
	 */
	public XResponseRenderer() {

		super(RESPONSE_ROOT_ID);
	}

	/**
	 * XXX.
	 */
	public void setBooleanResponse(boolean value) {

		addTopLevelAttribute(BOOLEAN_RESPONSE_ATTR, value);
	}

	/**
	 * XXX.
	 */
	public void setHierarchyResponse(CFrame rootFrame) {

		CHierarchyRenderer renderer = new CHierarchyRenderer();

		renderer.setVisibilityFilter(CVisibility.EXPOSED);
		renderer.render(rootFrame, addStructuredResponseNode());
	}

	/**
	 * XXX.
	 */
	public void setInstanceResponse(IFrame instance) {

		setInstanceResponse(new IInstanceRenderInput(instance));
	}

	/**
	 * XXX.
	 */
	public void setInstanceResponse(IInstanceRenderInput instance) {

		instanceRenderer.render(instance, addStructuredResponseNode());
	}

	/**
	 * XXX.
	 */
	public void setInstanceOrNullResponse(IFrame instance) {

		if (instance == null) {

			addNullResponseNode();
		}
		else {

			setInstanceResponse(instance);
		}
	}

	/**
	 * XXX.
	 */
	public void setIdentityListResponse(List<CIdentity> identities) {

		CIdentitySerialiser.renderList(identities, addStructuredResponseNode());
	}

	/**
	 * XXX.
	 */
	public void setMatchesResponse(IMatches matches) {

		IMatchesRenderer.render(matches, addStructuredResponseNode());
	}

	private XNode addStructuredResponseNode() {

		return addTopLevelNode(STRUCTURED_RESPONSE_ID);
	}

	private void addNullResponseNode() {

		addTopLevelNode(NULL_RESPONSE_ID);
	}
}
