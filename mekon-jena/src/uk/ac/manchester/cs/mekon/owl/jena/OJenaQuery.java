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

package uk.ac.manchester.cs.mekon.owl.jena;

import java.util.*;

import org.apache.jena.rdf.model.*;
import org.apache.jena.query.*;

import uk.ac.manchester.cs.mekon.owl.triples.*;

/**
 * @author Colin Puleston
 */
class OJenaQuery implements OTQuery {

	private Model model;

	private ValueConverter valueConverter;

	public boolean namedGraphs() {

		return false;
	}

	public boolean executeAsk(String query, OTQueryConstants constants) {

		QueryExecution exec = createExecution(query, constants);
		boolean result = exec.execAsk();

		exec.close();

		return result;
	}

	public Set<OT_URI> executeSelect(String query, OTQueryConstants constants) {

		Set<OT_URI> bindings = new HashSet<OT_URI>();
		QueryExecution exec = createExecution(query, constants);
		ResultSet results = exec.execSelect();

		while (results.hasNext()) {

			bindings.add(getSingleBoundURI(results.next()));
		}

		exec.close();

		return bindings;
	}

	OJenaQuery(Model model) {

		this.model = model;

		valueConverter = new ValueConverter(model);
	}

	private QueryExecution createExecution(String query, OTQueryConstants constants) {

		return createExecution(QueryFactory.create(query), constants);
	}

	private QueryExecution createExecution(Query query, OTQueryConstants constants) {

		QuerySolutionMap constantsMap = createConstantsMap(constants);

		return QueryExecutionFactory.create(query, model, constantsMap);
	}

	private QuerySolutionMap createConstantsMap(OTQueryConstants constants) {

		QuerySolutionMap map = new QuerySolutionMap();

		for (OTValue constant : constants.getConstants()) {

			String varName = constants.getVariableName(constant);

			map.add(varName, valueConverter.convert(constant));
		}

		return map;
	}

	private OT_URI getSingleBoundURI(QuerySolution solution) {

		String varName = solution.varNames().next();

		return new OT_URI(solution.getResource(varName).toString());
	}
}
