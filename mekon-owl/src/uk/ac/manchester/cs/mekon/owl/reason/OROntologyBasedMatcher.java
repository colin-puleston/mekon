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

package uk.ac.manchester.cs.mekon.owl.reason;

import java.util.*;

import org.semanticweb.owlapi.model.*;

import uk.ac.manchester.cs.mekon.config.*;
import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.store.motor.*;
import uk.ac.manchester.cs.mekon.network.*;
import uk.ac.manchester.cs.mekon.owl.*;
import uk.ac.manchester.cs.mekon.owl.util.*;

/**
 * @author Colin Puleston
 */
abstract class OROntologyBasedMatcher extends ORMatcher {

	public boolean rebuildOnStartup() {

		return true;
	}

	protected List<IRI> matchInOWLStore(NNode query) {

		ConceptExpression expr = createConceptExpression(query);
		OWLObject owlConstruct = expr.getOWLConstruct();

		ORMonitor.pollForMatcherRequest(getModel(), owlConstruct);

		List<IRI> matches = purgeMatches(matchInOWLStore(expr));

		ORMonitor.pollForMatchesFound(getModel(), matches);
		ORMonitor.pollForMatcherDone(getModel(), owlConstruct);

		return matches;
	}

	protected boolean matchesInOWL(NNode query, NNode instance) {

		return matchesInOWL(createConceptExpression(query), instance);
	}

	OROntologyBasedMatcher(OModel model) {

		super(model);
	}

	OROntologyBasedMatcher(KConfigNode parentConfigNode) {

		super(parentConfigNode);
	}

	OROntologyBasedMatcher(OModel model, KConfigNode parentConfigNode) {

		super(model, parentConfigNode);
	}

	abstract List<IRI> matchInOWLStore(ConceptExpression queryExpr);

	abstract boolean matchesInOWL(ConceptExpression queryExpr, NNode instance);

	ConceptExpression createConceptExpression(NNode node) {

		return new ConceptExpression(getReasoningModel(), node);
	}

	private List<IRI> purgeMatches(List<IRI> matches) {

		List<IRI> purged = new ArrayList<IRI>();

		for (IRI match : matches) {

			if (instanceIRI(match)) {

				purged.add(match);
			}
		}

		return purged;
	}
}
