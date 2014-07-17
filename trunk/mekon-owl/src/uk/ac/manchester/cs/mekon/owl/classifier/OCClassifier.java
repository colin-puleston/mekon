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

import java.util.*;

import org.semanticweb.owlapi.model.*;

import uk.ac.manchester.cs.mekon.config.*;
import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.mechanism.*;
import uk.ac.manchester.cs.mekon.owl.*;
import uk.ac.manchester.cs.mekon.owl.classifier.frames.*;
import uk.ac.manchester.cs.mekon.owl.classifier.semantics.*;

/**
 * Provides OWL-classification-based versions of the reasoning
 * mechanisms defined by {@link IReasoner}.
 * <p>
 * The classification process can be customised in three distinct
 * ways:
 * <ul>
 *   <li>Overriding top-level {@link #classify} method
 *   <li>Overriding lower-level {@link #classifyPreProcessed} method
 *   <li>Adding one or more pre-processors, via {@link addPreProcessor}
 * </ul>
 *
 * @author Colin Puleston
 */
public class OCClassifier extends IClassifier {

	/**
	 * Constructs classifier with the configuration for both the
	 * classifier itself and the model over which it is to operate
	 * defined via the appropriately-tagged child of the specified
	 * parent-configuration-node, if such a child exists.
	 *
	 * @param parentConfigNode Parent of configuration node defining
	 * appropriate configuration information
	 * @return Created object, or null if required child node does
	 * not exist
	 * @throws KConfigException if required child-node exists but
	 * does not contain correctly specified configuration information
	 */
	static public OCClassifier createOrNull(KConfigNode parentConfigNode) {

		return OCClassifierConfig.configNodeExists(parentConfigNode)
					? new OCClassifier(parentConfigNode)
					: null;
	}

	/**
	 * Constructs classifier with the configuration for both the
	 * classifier defined via the appropriately-tagged child of the
	 * specified parent-configuration-node, if such a child exists.
	 *
	 * @param model Model over which sanctioning is to operate
	 * @param parentConfigNode Parent of configuration node defining
	 * appropriate configuration information
	 * @return Created object, or null if required child node does
	 * not exist
	 * @throws KConfigException if required child-node exists but
	 * does not contain correctly specified configuration information
	 */
	static public OCClassifier createOrNull(
									OModel model,
									KConfigNode parentConfigNode) {

		return OCClassifierConfig.configNodeExists(parentConfigNode)
					? new OCClassifier(model, parentConfigNode)
					: null;
	}

	private OModel model;
	private OCSlotSemantics slotSemantics;
	private List<OCPreProcessor> preProcessors = new ArrayList<OCPreProcessor>();

	/**
	 * Constructs classifier with the configuration for both the
	 * classifier itself and the model over which it is to operate
	 * defined via the appropriately-tagged child of the specified
	 * parent-configuration-node.
	 *
	 * @param parentConfigNode Parent of configuration node defining
	 * appropriate configuration information
	 * @throws KConfigException if required child-node does not exist
	 * or does not contain correctly specified configuration
	 * information
	 */
	public OCClassifier(KConfigNode parentConfigNode) {

		this(new OModelBuilder(parentConfigNode).create(), parentConfigNode);
	}

	/**
	 * Constructs classifier for specified model, with the classifier
	 * configuration defined via the appropriately-tagged child of
	 * the specified parent-configuration-node.
	 *
	 * @param model Model over which classifier is to operate
	 * @param parentConfigNode Parent of configuration node defining
	 * appropriate configuration information
	 * @throws KConfigException if required child-node does not exist
	 * or does not contain correctly specified configuration
	 * information
	 */
	public OCClassifier(OModel model, KConfigNode parentConfigNode) {

		this(model);

		new OCClassifierConfig(parentConfigNode).configure(this);
	}

	/**
	 * Constructs classifier for specified model.
	 *
	 * @param model Model over which classifier is to operate
	 */
	public OCClassifier(OModel model) {

		this.model = model;

		slotSemantics = new OCSlotSemantics(model);
	}

	/**
	 * Registers a pre-processor to perform certain required
	 * pre-classification modifications to appropriate
	 * representations of instances that are about to be classified.
	 *
	 * @param preProcessor Pre-processor for instances about to be
	 * classified
	 */
	public void addPreProcessor(OCPreProcessor preProcessor) {

		preProcessors.add(preProcessor);
	}

	/**
	 * Provides the model over which the classifier is operating
	 *
	 * @return Model over which classifier is operating
	 */
	public OModel getModel() {

		return model;
	}

	/**
	 * Provides the object used to specify the semantics that will
	 * apply to specific slots from the incoming frames-based
	 * instances.
	 *
	 * @return Object for specifying slot-semantics to be applied
	 * by classifier
	 */
	public OCSlotSemantics getSlotSemantics() {

		return slotSemantics;
	}

	/**
	 * Converts the specified instance-level frame to the
	 * pre-processable version, runs any registered pre-processors
	 * over it, then invokes {@link #classifyPreProcessed} to handle
	 * the classification of the result via invocation of the OWL
	 * reasoner.
	 *
	 * @param frame Instance-level frame to classify
	 * @return Results of classification operation
	 */
	protected IClassification classify(IFrame frame) {

		OCFrame ocFrame = toOCFrame(frame);

		for (OCPreProcessor p : preProcessors) {

			p.process(model, ocFrame);
		}

		return classifyPreProcessed(ocFrame);
	}

	/**
	 * Takes the pre-processed version of the instance-level frame
	 * that is to be classified, builds the corresponding OWL
	 * expression, and invokes the OWL reasoner to perform the
	 * classification.
	 *
	 * @param frame Instance-level frame to classify
	 * @return Results of classification operation
	 */
	protected IClassification classifyPreProcessed(OCFrame frame) {

		return new OCInstance(model, frame).classify();
	}

	private OCFrame toOCFrame(IFrame frame) {

		return createOCFramesInstance(frame).getRootFrame();
	}

	private OCFramesInstance createOCFramesInstance(IFrame frame) {

		return new OCFramesInstance(model, slotSemantics, frame);
	}
}
