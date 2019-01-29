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

import java.io.File;
import java.net.URL;

import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.*;

import uk.ac.manchester.cs.factplusplus.owlapiv3.FaCTPlusPlusReasonerFactory;
import uk.ac.manchester.cs.rekon.RekonReasonerFactory;

import uk.ac.manchester.cs.mekon.demomodel.*;
import uk.ac.manchester.cs.mekon.owl.*;

/**
 * @author Colin Puleston
 */
public class ODemoModel {

	static private final String OWL_FILE = "demo.owl";
	static private final String NUMERIC_PROPERTY = "numericValue";

	static private final Class<? extends OWLReasonerFactory> REASONER_FACTORY = RekonReasonerFactory.class;

	static public OModel create() {

		OModelBuilder bldr = createBuilder();

		bldr.setIndirectNumericProperty(nameToIRI(NUMERIC_PROPERTY));

		return bldr.create(true);
	}

	static private OModelBuilder createBuilder() {

		return new OModelBuilder(getSourceFile(), REASONER_FACTORY);
	}

	static private IRI nameToIRI(String name) {

		return IRI.create(DemoModelBasedTest.nameToIdentifier(name));
	}

	static private File getSourceFile() {

		URL url = getClassLoader().getResource(OWL_FILE);

		if (url == null) {

			throw new RuntimeException("Cannot access OWL file: " + OWL_FILE);
		}

		return new File(url.getFile());
	}

	static private ClassLoader getClassLoader() {

		return Thread.currentThread().getContextClassLoader();
	}
}
