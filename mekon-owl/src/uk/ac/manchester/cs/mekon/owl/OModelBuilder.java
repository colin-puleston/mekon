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

package uk.ac.manchester.cs.mekon.owl;

import java.io.*;
import java.util.*;

import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.*;
import org.semanticweb.owlapi.apibinding.*;

import uk.ac.manchester.cs.mekon.*;
import uk.ac.manchester.cs.mekon.config.*;

/**
 * Builder for creating a {@link OModel} object.
 *
 * @author Colin Puleston
 */
public class OModelBuilder {

	private OWLOntologyManager manager;
	private OWLOntology mainOntology;
	private OWLReasonerFactory reasonerFactory;
	private OWLDataProperty indirectNumericProperty = null;

	/**
	 * Creates builder for model defined via the appropriately-tagged
	 * child of the specified parent-configuration-node.
	 *
	 * @param parentConfigNode Parent of configuration node defining
	 * model
	 * @throws KConfigException if required child-node does not exist
	 * or does not contain correctly specified configuration
	 * information
	 */
	public OModelBuilder(KConfigNode parentConfigNode) {

		this(parentConfigNode, null);
	}

	/**
	 * Creates builder for model defined via the appropriately-tagged
	 * child of the specified parent-configuration-node, assuming that
	 * the path of the main ontology file specified therein is relative
	 * to the specified base-directory.
	 *
	 * @param parentConfigNode Parent of configuration node defining
	 * model
	 * @param baseDirectory Base-directory for main ontology file
	 * @throws KConfigException if required child-node does not exist
	 * or does not contain correctly specified configuration
	 * information
	 */
	public OModelBuilder(KConfigNode parentConfigNode, File baseDirectory) {

		new OModelConfig(parentConfigNode).configure(this, baseDirectory);
	}

	/**
	 * Creates builder for model with main ontology loaded from specified
	 * OWL file, manager created for that ontology and it's imports-closure,
	 * and reasoner created by a factory of the specified type. The OWL
	 * files containing the imports should all be located in the same
	 * directory as the main OWL file, or a descendant directory of
	 * that one.
	 *
	 * @param mainOWLFile OWL file containing main ontology
	 * @param reasonerFactoryType Type of factory for creating required
	 * reasoner
	 */
	public OModelBuilder(
			File mainOWLFile,
			Class<? extends OWLReasonerFactory> reasonerFactoryType) {

		initialise(mainOWLFile, reasonerFactoryType);
	}

	/**
	 * Creates builder for model with specified manager, main ontology
	 * and reasoner created by specified factory.
	 *
	 * @param manager Manager for set of ontologies
	 * @param mainOntology Main ontology
	 * @param reasonerFactory Factory for creating required reasoner
	 */
	public OModelBuilder(
			OWLOntologyManager manager,
			OWLOntology mainOntology,
			OWLReasonerFactory reasonerFactory) {

		this.manager = manager;
		this.mainOntology = mainOntology;
		this.reasonerFactory = reasonerFactory;
	}

	/**
	 * Sets the "indirect-numeric-property" for the model.
	 *
	 * @param indirectNumericPropertyIRI IRI of indirect-numeric-property
	 * for model, or null if not defined
	 */
	public void setIndirectNumericProperty(IRI indirectNumericPropertyIRI) {

		indirectNumericProperty = getIndirectNumericProperty(indirectNumericPropertyIRI);
	}

	/**
	 * Provides the manager for the set of ontologies.
	 *
	 * @return Manager for set of ontologies
	 */
	public OWLOntologyManager getManager() {

		return manager;
	}

	/**
	 * Provides the main ontology.
	 *
	 * @return Main ontology
	 */
	public OWLOntology getMainOntology() {

		return mainOntology;
	}

	/**
	 * Provides the complete set of ontologies.
	 *
	 * @return Complete set of ontologies
	 */
	public Set<OWLOntology> getAllOntologies() {

		return manager.getOntologies();
	}

	/**
	 * Provides the factory that will be used to create the required
	 * reasoner.
	 *
	 * @return Factory for required reasoner
	 */
	public OWLReasonerFactory getReasonerFactory() {

		return reasonerFactory;
	}

	/**
	 * Creates and then initialises the {@link OModel}, which includes
	 * classifying the ontology.
	 *
	 * @return Created model
	 */
	public OModel create() {

		return new OModel(manager, mainOntology, reasonerFactory, indirectNumericProperty);
	}

	void initialise(File mainOWLFile, Class<? extends OWLReasonerFactory> reasonerFactoryType) {

		manager = createOntologyManager(mainOWLFile);
		mainOntology = loadOntology(mainOWLFile);
		reasonerFactory = createReasonerFactory(reasonerFactoryType);
	}

	private OWLOntology loadOntology(File owlFile) {

		try {

			OMonitor.pollForPreOntologyLoad(owlFile);
			OWLOntology ontology = manager.loadOntologyFromOntologyDocument(owlFile);
			OMonitor.pollForOntologyLoaded();

			return ontology;
		}
		catch (OWLOntologyCreationException e) {

			throw new KModelException(e);
		}
	}

	private OWLOntologyManager createOntologyManager(File owlFile) {

		OWLOntologyManager om = OWLManager.createOWLOntologyManager();

		om.addIRIMapper(createOntologyIRIMapper(owlFile));

		return om;
	}

	private OWLOntologyIRIMapper createOntologyIRIMapper(File owlFile) {

		return new PathSearchOntologyIRIMapper(owlFile.getParentFile());
	}

	private OWLReasonerFactory createReasonerFactory(Class<? extends OWLReasonerFactory> type) {

		return new KConfigObjectConstructor<OWLReasonerFactory>(type).construct();
	}

	private OWLDataProperty getIndirectNumericProperty(IRI iri) {

		if (iri == null) {

			return null;
		}

		if (mainOntology.containsDataPropertyInSignature(iri, true)) {

			return manager.getOWLDataFactory().getOWLDataProperty(iri);
		}

		throw new KModelException("Cannot find indirect-numeric-property: " + iri);
	}
}
