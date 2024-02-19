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

import org.semanticweb.owlapi.reasoner.*;

import uk.ac.manchester.cs.factplusplus.owlapiv3.FaCTPlusPlusReasonerFactory;

/**
 * @author Colin Puleston
 */
class DefaultTestReasoner {

	static private final String OVERRIDE_PROPERTY = "mekon.test.default-reasoner";

	static private Class<? extends OWLReasonerFactory> factoryClass
									= FaCTPlusPlusReasonerFactory.class;

	static {

		String className = System.getProperty(OVERRIDE_PROPERTY);

		if (className != null) {

			factoryClass = toFactoryClass(className);
		}
	}

	static Class<? extends OWLReasonerFactory> getFactoryClass() {

		return factoryClass;
	}

	static private Class<? extends OWLReasonerFactory> toFactoryClass(String className) {

		try {

			return Class.forName(className).asSubclass(OWLReasonerFactory.class);
		}
		catch (ClassNotFoundException e) {

			throw new RuntimeException("Cannot find class: " + className);
		}
	}
}
