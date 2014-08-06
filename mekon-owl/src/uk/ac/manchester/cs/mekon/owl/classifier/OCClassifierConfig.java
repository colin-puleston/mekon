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

package uk.ac.manchester.cs.mekon.owl.classifier;

import uk.ac.manchester.cs.mekon.config.*;
import uk.ac.manchester.cs.mekon.owl.*;
import uk.ac.manchester.cs.mekon.owl.frames.*;

/**
 * @author Colin Puleston
 */
class OCClassifierConfig implements OCClassifierConfigVocab {

	static boolean configNodeExists(KConfigNode parentConfigNode) {

		return parentConfigNode.getChildOrNull(ROOT_ID) != null;
	}

	private KConfigNode configNode;

	OCClassifierConfig(KConfigNode parentConfigNode) {

		configNode = parentConfigNode.getChild(ROOT_ID);
	}

	void configure(OCClassifier classifier) {

		checkSetSlotSemantics(classifier.getSlotSemantics());
		checkEnableLogging();
	}

	private void checkSetSlotSemantics(OFSlotSemantics slotSemantics) {

		KConfigNode slotSemsNode = configNode.getChildOrNull(SEMANTICS_ID);

		if (slotSemsNode != null) {

			setSlotSemanticsDefault(slotSemsNode, slotSemantics);
			setSlotSemanticsOverrides(slotSemsNode, slotSemantics);
		}
	}

	private void setSlotSemanticsDefault(
					KConfigNode slotSemsNode,
					OFSlotSemantics slotSemantics) {

		slotSemantics.setDefaultSemantics(getDefaultSemantics(slotSemsNode));
	}

	private void setSlotSemanticsOverrides(
					KConfigNode slotSemsNode,
					OFSlotSemantics slotSemantics) {

		for (KConfigNode expPropNode : slotSemsNode.getChildren(EXCEPTION_PROP_ID)) {

			slotSemantics.addExceptionProperty(getExceptionPropertyURI(expPropNode));
		}
	}

	private OFSemantics getDefaultSemantics(KConfigNode semanticsNode) {

		return semanticsNode.getEnum(DEFAULT_SEMANTICS_ATTR, OFSemantics.class);
	}

	private String getExceptionPropertyURI(KConfigNode expPropNode) {

		return expPropNode.getString(EXCEPTION_PROP_URI_ATTR);
	}

	private void checkEnableLogging() {

		OCLoggingMode mode = getLoggingMode();

		if (mode != OCLoggingMode.DISABLED) {

			OCLogger.start();

			if (mode != OCLoggingMode.TIMES_ONLY) {

				OCLogger.setShowRequests(true);
				OCLogger.setShowResults(true);
			}
		}
	}

	private OCLoggingMode getLoggingMode() {

		return configNode.getEnum(LOGGING_MODE_ATTR, OCLoggingMode.class);
	}
}
