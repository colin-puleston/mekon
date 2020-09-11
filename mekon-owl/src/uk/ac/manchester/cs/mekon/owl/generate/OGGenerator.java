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

package uk.ac.manchester.cs.mekon.owl.generate;

import java.io.*;
import java.util.*;

import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.apibinding.*;
import org.semanticweb.owlapi.vocab.*;
import org.semanticweb.owlapi.rdf.rdfxml.renderer.*;

import uk.ac.manchester.cs.mekon.manage.*;
import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.owl.util.*;
import uk.ac.manchester.cs.mekon_util.config.*;

/**
 * @author Colin Puleston
 */
public class OGGenerator {

	static private final IRI LABEL_ANNOTATION_IRI = OWLRDFVocabulary.RDFS_LABEL.getIRI();

	static private OWLOntology createOntology(IRI ontologyIRI) {

		try {

			return OWLManager.createOWLOntologyManager().createOntology(ontologyIRI);
		}
		catch (OWLOntologyCreationException e) {

			throw new KSystemConfigException(e);
		}
	}

	private OWLOntology ontology;
	private OWLDataFactory dataFactory;

	private IRIGenerator iriGenerator;
	private ONumberRangeRenderer numberRangeRenderer;
	private OWLDataRange stringDataType;
	private OWLAnnotationProperty labelAnnotationProperty;

	private ObjectPropertyResolver objectProperties = new ObjectPropertyResolver();
	private DataPropertyResolver dataProperties = new DataPropertyResolver();

	private abstract class PropertyResolver<P extends OWLProperty> {

		private Map<IRI, P> propertiesByIRI = new HashMap<IRI, P>();

		P resolve(IRI iri, CIdentity sourceId) {

			P property = propertiesByIRI.get(iri);

			if (property == null) {

				property = create(iri);

				addEntity(property, sourceId);
				propertiesByIRI.put(iri, property);
			}

			return property;
		}

		abstract P create(IRI iri);
	}

	private class ObjectPropertyResolver extends PropertyResolver<OWLObjectProperty> {

		OWLObjectProperty create(IRI iri) {

			return dataFactory.getOWLObjectProperty(iri);
		}
	}

	private class DataPropertyResolver extends PropertyResolver<OWLDataProperty> {

		OWLDataProperty create(IRI iri) {

			return dataFactory.getOWLDataProperty(iri);
		}
	}

	private abstract class RestrictionGenerator extends CValueVisitor {

		private OWLClass concept;
		private IRI propertyIRI;

		private CIdentity slotId;

		protected void visit(CFrame value) {

			generateForFrameValued(value);
		}

		protected void visit(CNumber value) {

			generateForDataValued(numberRangeRenderer.render(value));
		}

		protected void visit(CString value) {

			generateForDataValued(stringDataType);
		}

		protected void visit(MFrame value) {

			generateForFrameValued(value.getRootCFrame());
		}

		RestrictionGenerator(OWLClass concept, CFrame frame, CIdentity slotId) {

			this.concept = concept;
			this.slotId = slotId;

			propertyIRI = iriGenerator.generateForSlot(frame, slotId);
		}

		void generate(CValue<?> slotValue) {

			visit(slotValue);
		}

		abstract OWLRestriction createObjectRestriction(
									OWLObjectProperty property,
									OWLClassExpression filler);

		abstract OWLRestriction createDataRestriction(
									OWLDataProperty property,
									OWLDataRange filler);

		private void generateForFrameValued(CFrame value) {

			OWLClassExpression filler = getFillerExpression(value);

			addRestriction(createObjectRestriction(resolveObjectProperty(), filler));
		}

		private void generateForDataValued(OWLDataRange filler) {

			addRestriction(createDataRestriction(resolveDataProperty(), filler));
		}

		private OWLObjectProperty resolveObjectProperty() {

			return objectProperties.resolve(propertyIRI, slotId);
		}

		private OWLDataProperty resolveDataProperty() {

			return dataProperties.resolve(propertyIRI, slotId);
		}

		private OWLClassExpression getFillerExpression(CFrame frame) {

			return frame.getCategory().disjunction() ? toUnion(frame) : getConcept(frame);
		}

		private OWLObjectUnionOf toUnion(CFrame frame) {

			Set<OWLClassExpression> operands = new HashSet<OWLClassExpression>();

			for (CFrame disjunct : frame.asDisjuncts()) {

				operands.add(getConcept(disjunct));
			}

			return dataFactory.getOWLObjectUnionOf(operands);
		}

		private void addRestriction(OWLRestriction restriction) {

			addSubClassAxiom(concept, restriction);
		}
	}

	private class SlotRestrictionGenerator extends RestrictionGenerator {

		private boolean singleValued;

		SlotRestrictionGenerator(OWLClass concept, CFrame frame, CSlot slot) {

			super(concept, frame, slot.getIdentity());

			singleValued = slot.getCardinality().singleValue();

			generate(slot.getValueType());
		}

		OWLRestriction createObjectRestriction(
							OWLObjectProperty property,
							OWLClassExpression filler) {

			return singleValued
					? dataFactory.getOWLObjectMaxCardinality(1, property, filler)
					: dataFactory.getOWLObjectAllValuesFrom(property, filler);
		}

		OWLRestriction createDataRestriction(
							OWLDataProperty property,
							OWLDataRange filler) {

			return dataFactory.getOWLDataMaxCardinality(1, property, filler);
		}
	}

	private class SlotValuesRestrictionGenerator extends RestrictionGenerator {

		SlotValuesRestrictionGenerator(
			OWLClass concept,
			CFrame frame,
			CIdentity slotId,
			CValue<?> slotValue) {

			super(concept, frame, slotId);

			generate(slotValue);
		}

		OWLRestriction createObjectRestriction(
							OWLObjectProperty property,
							OWLClassExpression filler) {

			return dataFactory.getOWLObjectSomeValuesFrom(property, filler);
		}

		OWLRestriction createDataRestriction(
							OWLDataProperty property,
							OWLDataRange filler) {

			return dataFactory.getOWLDataSomeValuesFrom(property, filler);
		}
	}

	public OGGenerator(IRI ontologyIRI, IRIGenerator iriGenerator) {

		this(createOntology(ontologyIRI), iriGenerator);
	}

	public OGGenerator(OWLOntology ontology, IRIGenerator iriGenerator) {

		this.ontology = ontology;
		this.iriGenerator = iriGenerator;

		dataFactory = ontology.getOWLOntologyManager().getOWLDataFactory();
		numberRangeRenderer = new ONumberRangeRenderer(dataFactory);
		stringDataType = getStringDataType();
		labelAnnotationProperty = getLabelAnnotationProperty();
	}

	public void generate() {

		generate(CManager.createBuilder().build());
	}

	public void generate(File mekonConfigFile) {

		generate(CManager.createBuilder(new KConfigFile(mekonConfigFile)).build());
	}

	public void generate(CModel mekonModel) {

		generateForFrames(mekonModel);
		generateForFrameLinks(mekonModel);
	}

	public void save(File ontologyFile) {

		PrintWriter writer = null;

		try {

			writer = new PrintWriter(new FileWriter(ontologyFile));

			new RDFXMLRenderer(ontology, writer).render();
		}
		catch (IOException e) {

			throw new KSystemConfigException(e);
		}
		finally {

			if (writer != null) {

				writer.close();
			}
		}
	}

	private void generateForFrames(CModel mekonModel) {

		for (CFrame frame : mekonModel.getFrames().asList()) {

			addEntity(getConcept(frame), frame.getIdentity());
		}
	}

	private void generateForFrameLinks(CModel mekonModel) {

		for (CFrame frame : mekonModel.getFrames().asList()) {

			OWLClass concept = getConcept(frame);

			generateForSubLinks(frame, concept);
			generateForSlots(frame, concept);
			generateForSlotValues(frame, concept);
		}
	}

	private void generateForSubLinks(CFrame frame, OWLClass concept) {

		for (CFrame sub : frame.getSubs()) {

			addAxiom(dataFactory.getOWLSubClassOfAxiom(getConcept(sub), concept));
		}
	}

	private void generateForSlots(CFrame frame, OWLClass concept) {

		for (CSlot slot : frame.getSlots().asList()) {

			new SlotRestrictionGenerator(concept, frame, slot);
		}
	}

	private void generateForSlotValues(CFrame frame, OWLClass concept) {

		CSlotValues values = frame.getSlotValues();

		for (CIdentity slotId : values.getSlotIdentities()) {

			for (CValue<?> slotValue : values.getValues(slotId)) {

				new SlotValuesRestrictionGenerator(concept, frame, slotId, slotValue);
			}
		}
	}

	private OWLClass getConcept(CFrame frame) {

		return dataFactory.getOWLClass(iriGenerator.generateForFrame(frame));
	}

	private void addEntity(OWLEntity entity, CIdentity sourceId) {

		addDeclarationAxiom(entity);
		addEntityLabel(entity, sourceId);
	}

	private void addEntityLabel(OWLEntity entity, CIdentity sourceId) {

		addAxiom(createLabelAxiom(entity, sourceId.getLabel()));
	}

	private OWLAxiom createLabelAxiom(OWLEntity entity, String label) {

		return dataFactory
				.getOWLAnnotationAssertionAxiom(
					labelAnnotationProperty,
					entity.getIRI(),
					dataFactory.getOWLLiteral(label));
	}

	private void addDeclarationAxiom(OWLEntity entity) {

		addAxiom(dataFactory.getOWLDeclarationAxiom(entity));
	}

	private void addSubClassAxiom(OWLClassExpression sub, OWLClassExpression sup) {

		addAxiom(dataFactory.getOWLSubClassOfAxiom(sub, sup));
	}

	private void addAxiom(OWLAxiom axiom) {

		OWLAPIVersion.addAxiom(ontology, axiom);
	}

	private OWLDataRange getStringDataType() {

		return dataFactory.getOWLDatatype(OWL2Datatype.XSD_STRING.getIRI());
	}

	private OWLAnnotationProperty getLabelAnnotationProperty() {

		return dataFactory.getOWLAnnotationProperty(LABEL_ANNOTATION_IRI);
	}
}
