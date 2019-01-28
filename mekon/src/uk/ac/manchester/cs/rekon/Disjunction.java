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

package uk.ac.manchester.cs.rekon;

import java.util.*;

/**
 * @author Colin Puleston
 */
class Disjunction extends NameExpression {

	private Set<Description> disjuncts;

	Disjunction(Set<Description> disjuncts) {

		this.disjuncts = disjuncts;
	}

	NameExpression asNameExpression() {

		return this;
	}

	Disjunction asDisjunction() {

		return this;
	}

	Name getNameOrNull() {

		return null;
	}

	Set<? extends Expression> getSubExpressions() {

		return disjuncts;
	}

	boolean subsumesOther(Expression e) {

		Description de = e.asDescription();

		if (de != null) {

			return subsumesAllNestedNames(de) && subsumesDescription(de);
		}

		Disjunction di = e.asDisjunction();

		if (di != null) {

			return subsumesAllNestedNames(di) && subsumesDisjunction(di);
		}

		return false;
	}

	void render(ExpressionRenderer r) {

		r.addLine("OR");

		r = r.nextLevel();

		for (Description d : disjuncts) {

			d.render(r);
		}
	}

	private boolean subsumesDisjunction(Disjunction d) {

		for (Description disjunct : d.disjuncts) {

			if (!subsumesDescription(disjunct)) {

				return false;
			}
		}

		return true;
	}

	private boolean subsumesDescription(Description d) {

		for (Description disjunct : disjuncts) {

			if (disjunct.subsumes(d)) {

				return true;
			}
		}

		return false;
	}
}
