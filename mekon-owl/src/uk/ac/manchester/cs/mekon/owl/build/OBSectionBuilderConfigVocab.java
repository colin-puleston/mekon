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

package uk.ac.manchester.cs.mekon.owl.build;

/**
 * Vocabulary used in the {@link OBSectionBuilder}-definition
 * section of the MEKON configuration file.
 *
 * @author Colin Puleston
 */
public interface OBSectionBuilderConfigVocab {

	static public final String ROOT_ID = "OWLSanctionedModel";
	static public final String CONCEPT_INCLUSION_ID = "ConceptInclusion";
	static public final String PROPERTY_INCLUSION_ID = "PropertyInclusion";
	static public final String ENTITY_GROUP_ID = "Group";
	static public final String LABEL_ANNO_PROPERTIES_ID = "LabelAnnotations";
	static public final String LABEL_ANNO_PROPERTY_ID = "AnnotationProperty";
	static public final String ENTITY_ANNO_TYPES_ID = "EntityAnnotationTypes";
	static public final String ENTITY_ANNO_TYPE_ID = "AnnotationType";
	static public final String ENTITY_ANNO_SUBSTITUTION_ID = "ValueSubstitution";

	static public final String METAFRAME_SLOTS_ENABLED_ATTR = "metaFrameSlotsEnabled";
	static public final String RETAIN_ONLY_DECLARATIONS_ATTR = "retainOnlyDeclarationAxioms";
	static public final String ROOT_ENTITY_URI_ATTR = "rootURI";
	static public final String ENTITY_INCLUSION_ATTR = "inclusion";
	static public final String CONCEPT_HIDING_CANDIDATES_ATTR = "conceptHidingCandidates";
	static public final String CONCEPT_HIDING_FILTER_ATTR = "conceptHidingFilter";
	static public final String MIRROR_PROPERTIES_AS_FRAMES_ATTR = "mirrorAsFrames";
	static public final String ANNO_PROPERTY_URI_ATTR = "uri";
	static public final String ENTITY_ANNO_ID_ATTR = "id";
	static public final String ENTITY_ANNO_VALUE_SEPARATORS_ATTR = "valueSeparators";
	static public final String ENTITY_ANNO_SUB_OWL_VALUE_ATTR = "owlValue";
	static public final String ENTITY_ANNO_SUB_FRAMES_VALUE_ATTR = "framesValue";
}