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

import org.semanticweb.owlapi.model.*;

/**
 * @author Colin Puleston
 */
abstract class NameExpression extends Expression {

	private NestedNames nestedNames = new NestedNames();
	private NestedNameSubsumers nestedNameSubsumers = new NestedNameSubsumers();
	private CompulsoryNestedNames compulsoryNestedNames = new CompulsoryNestedNames();

	private abstract class NestedNameReferences {

		private NameSet refs = null;

		NameSet get() {

			if (refs == null) {

				refs = new NameSet();

				initialise();
			}

			return refs;
		}

		void reset() {

			refs = null;
		}

		abstract void addSubExpressionRefs(NameSet refs, NameExpression s);

		private void initialise() {

			for (Expression s : getSubExpressions()) {

				NameExpression ns = s.asNameExpression();

				if (ns != null) {

					addSubExpressionRefs(refs, ns);
				}
			}
		}
	}

	private abstract class PrimaryNestedNameReferences extends NestedNameReferences {

		void addSubExpressionRefs(NameSet refs, NameExpression s) {

			for (Name n : getDirectNames()) {

				addNameRefs(refs, n);
			}

			addNextNestedRefs(refs, s);
		}

		void addNameRefs(NameSet refs, Name name) {

			refs.add(name);
		}

		abstract void addNextNestedRefs(NameSet refs, NameExpression s);
	}

	private class NestedNames extends PrimaryNestedNameReferences {

		void addNextNestedRefs(NameSet refs, NameExpression s) {

			refs.addAll(s.getNestedNames());
		}
	}

	private class NestedNameSubsumers extends PrimaryNestedNameReferences {

		private Set<Name> activeNames;

		void setActiveNames(Set<Name> activeNames) {

			this.activeNames = activeNames;
		}

		void addNameRefs(NameSet refs, Name name) {

			super.addNameRefs(refs, name);

			for (Name a : name.getAncestors()) {

				if (activeNames == null || activeNames.contains(a)) {

					refs.add(a);
				}
			}
		}

		void addNextNestedRefs(NameSet refs, NameExpression s) {

			refs.addAll(s.getNestedNameSubsumers());
		}
	}

	private class CompulsoryNestedNames extends NestedNameReferences {

		void addSubExpressionRefs(NameSet refs, NameExpression s) {

			refs.addAll(s.getCompulsoryNestedNames());
		}
	}

	void setActiveNames(Set<Name> activeNames) {

		nestedNameSubsumers.setActiveNames(activeNames);
	}

	void resetNameReferences() {

		nestedNames.reset();
		nestedNameSubsumers.reset();
		compulsoryNestedNames.reset();

		for (Expression s : getSubExpressions()) {

			NameExpression ns = s.asNameExpression();

			if (ns != null) {

				ns.resetNameReferences();
			}
		}
	}

	boolean possibleNestedSubsumption(NameExpression e) {

		return e.nestedNameSubsumers.get().containsAll(compulsoryNestedNames.get());
	}

	Set<Name> getAllNames() {

		Set<Name> all = new HashSet<Name>();

		all.addAll(getDirectNames());
		all.addAll(nestedNames.get().getSet());

		return all;
	}

	NameSet getNestedNames() {

		return nestedNames.get();
	}

	NameSet getNestedNameSubsumers() {

		return nestedNameSubsumers.get();
	}

	abstract NameSet getCompulsoryNestedNames();

 	abstract Set<Name> getDirectNames();

	abstract Set<? extends Expression> getSubExpressions();
}
