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

package uk.ac.manchester.cs.mekon.owl.triples;

import java.util.*;

import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.rdf.util.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.network.*;
import uk.ac.manchester.cs.mekon.owl.reason.*;

/**
 * @author Colin Puleston
 */
abstract class InstanceRenderer<TN extends OTValue> {

	private LinksRenderer linksRenderer = new LinksRenderer();
	private NumericsRenderer numericsRenderer = new NumericsRenderer();

	private int nodeCount = 0;

	private abstract class AttributesRenderer<V, A extends NAttribute<V>> {

		void render(TN subject, List<? extends A> attributes) {

			for (A attribute : attributes) {

				if (!attribute.getValues().isEmpty()) {

					renderValues(subject, renderType(attribute), attribute);
				}
			}
		}

		void renderValues(TN subject, OT_URI predicate, A attribute) {

			for (V value : attribute.getValues()) {

				renderValue(subject, predicate, value);
			}
		}

		abstract void renderValue(TN subject, OT_URI predicate, V value);
	}

	private class LinksRenderer extends AttributesRenderer<NNode, NLink> {

		void renderValues(TN subject, OT_URI predicate, NLink attribute) {

			if (attribute.disjunctionLink()) {

				renderUnion(subject, predicate, renderValues(attribute));
			}
			else {

				super.renderValues(subject, predicate, attribute);
			}
		}

		void renderValue(TN subject, OT_URI predicate, NNode value) {

			renderTriple(subject, predicate, renderNode(value));
		}

		private Set<OTValue> renderValues(NLink link) {

			Set<OTValue> tripleNodes = new HashSet<OTValue>();

			for (NNode value : link.getValues()) {

				tripleNodes.add(renderNode(value));
			}

			return tripleNodes;
		}
	}

	private class NumericsRenderer extends AttributesRenderer<INumber, NNumeric> {

		void renderValue(TN subject, OT_URI predicate, INumber value) {

			if (value.indefinite()) {

				renderRange(subject, predicate, value.getType());
			}
			else {

				renderTriple(subject, predicate, renderDefiniteNumber(value));
			}
		}

		private void renderRange(TN subject, OT_URI predicate, CNumber range) {

			if (range.hasMin()) {

				renderTriple(subject, predicate, renderMin(range.getMin()));
			}

			if (range.hasMax()) {

				renderTriple(subject, predicate, renderMax(range.getMax()));
			}
		}

		private OTValue renderMin(INumber min) {

			return renderNumberMin(renderDefiniteNumber(min));
		}

		private OTValue renderMax(INumber max) {

			return renderNumberMax(renderDefiniteNumber(max));
		}
	}

	TN renderNode(NNode node) {

		TN tripleNode = renderNode(nodeCount++);

		checkRenderType(node, tripleNode);
		renderAttributeValues(node, tripleNode);

		return tripleNode;
	}

	abstract TN renderNode(int index);

	abstract OTValue renderNumberMin(OTNumber value);

	abstract OTValue renderNumberMax(OTNumber value);

	abstract void renderTriple(TN subject, OT_URI predicate, OTValue object);

	abstract void renderUnion(TN subject, OT_URI predicate, Set<OTValue> objects);

	OT_URI renderURI(String uri) {

		return new OT_URI(uri);
	}

	OTNumber renderDefiniteNumber(INumber number) {

		return new OTNumber(number.asTypeNumber());
	}

	private void checkRenderType(NNode node, TN tripleNode) {

		OT_URI typePredicate = renderURI(RDFConstants.RDF_TYPE);

		if (node.atomicConcept()) {

			renderTriple(tripleNode, typePredicate, renderAtomicType(node));
		}
		else {

			renderUnion(tripleNode, typePredicate, renderTypeDisjuncts(node));
		}
	}

	private void renderAttributeValues(NNode node, TN tripleNode) {

		linksRenderer.render(tripleNode, node.getLinks());
		numericsRenderer.render(tripleNode, node.getNumerics());
	}

	private Set<OTValue> renderTypeDisjuncts(NNode node) {

		Set<OTValue> objects = new HashSet<OTValue>();

		for (IRI iri : NetworkIRIs.getConceptDisjuncts(node)) {

			objects.add(renderURI(iri));
		}

		return objects;
	}

	private OT_URI renderAtomicType(NNode node) {

		return renderURI(NetworkIRIs.getAtomicConcept(node));
	}

	private OT_URI renderType(NAttribute<?> attribute) {

		return renderURI(NetworkIRIs.getProperty(attribute));
	}

	private OT_URI renderURI(IRI iri) {

		return renderURI(iri.toString());
	}
}
