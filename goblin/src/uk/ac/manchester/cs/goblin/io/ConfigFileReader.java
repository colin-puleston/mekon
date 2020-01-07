package uk.ac.manchester.cs.goblin.io;

import java.io.*;
import java.util.*;

import uk.ac.manchester.cs.mekon.config.*;
import uk.ac.manchester.cs.goblin.model.*;

/**
 * @author Colin Puleston
 */
class ConfigFileReader {

	static private final String CONFIG_FILE_NAME = "goblin.xml";

	static private final String HIERARCHY_TAG = "Hierarchy";
	static private final String ANCHORED_CONSTRAINT_TYPE_TAG = "AnchoredConstraintType";
	static private final String SIMPLE_CONSTRAINT_TYPE_TAG = "SimpleConstraintType";

	static private final String CORE_NAMESPACE_ATTR = "coreNamespace";
	static private final String CONTENT_NAMESPACE_ATTR = "contentNamespace";
	static private final String CONTENT_FILEATTR = "contentFilename";

	static private final String ROOT_CONCEPT_ATTR = "rootConcept";

	static private final String ANCHOR_CONCEPT_ATTR = "anchorConcept";
	static private final String SOURCE_PROPERTY_ATTR = "sourceProperty";
	static private final String TARGET_PROPERTY_ATTR = "targetProperty";
	static private final String LINKING_PROPERTY_ATTR = "linkingProperty";
	static private final String ROOT_TARGET_CONCEPT_ATTR = "rootTargetConcept";

	static private KConfigNode loadFile() {

		return new KConfigFile(CONFIG_FILE_NAME).getRootNode();
	}

	private KConfigNode rootNode = loadFile();

	private abstract class ConstraintTypesLoader {

		private Model model;

		ConstraintTypesLoader(Model model) {

			this.model = model;

			Iterator<Hierarchy> hierarchies = model.getHierarchies().iterator();

			for (KConfigNode hierarchyNode : rootNode.getChildren(HIERARCHY_TAG)) {

				loadHierarchyTypes(hierarchyNode, hierarchies.next());
			}
		}

		abstract String getTypeTag();

		abstract ConstraintType loadType(KConfigNode node, Concept rootSrc, Concept rootTgt);

		EntityId getCoreId(KConfigNode node, String tag) {

			return model.toCoreId(getEntityIdSpec(node, tag));
		}

		private void loadHierarchyTypes(KConfigNode hierarchyNode, Hierarchy hierarchy) {

			for (KConfigNode typeNode : hierarchyNode.getChildren(getTypeTag())) {

				hierarchy.addConstraintType(loadType(typeNode, hierarchy));
			}
		}

		private ConstraintType loadType(KConfigNode node, Hierarchy hierarchy) {

			return loadType(node, hierarchy.getRoot(), getRootTargetConcept(node));
		}

		private Concept getRootTargetConcept(KConfigNode node) {

			return model.getHierarchy(getRootTargetConceptIdSpec(node)).getRoot();
		}

		private EntityIdSpec getRootTargetConceptIdSpec(KConfigNode node) {

			return getEntityIdSpec(node, ROOT_TARGET_CONCEPT_ATTR);
		}
	}

	private class SimpleConstraintTypesLoader extends ConstraintTypesLoader {

		SimpleConstraintTypesLoader(Model model) {

			super(model);
		}

		String getTypeTag() {

			return SIMPLE_CONSTRAINT_TYPE_TAG;
		}

		ConstraintType loadType(KConfigNode node, Concept rootSrc, Concept rootTgt) {

			EntityId lnkProp = getCoreId(node, LINKING_PROPERTY_ATTR);

			return new SimpleConstraintType(lnkProp, rootSrc, rootTgt);
		}
	}

	private class AnchoredConstraintTypesLoader extends ConstraintTypesLoader {

		AnchoredConstraintTypesLoader(Model model) {

			super(model);
		}

		String getTypeTag() {

			return ANCHORED_CONSTRAINT_TYPE_TAG;
		}

		ConstraintType loadType(KConfigNode node, Concept rootSrc, Concept rootTgt) {

			EntityId anchor = getCoreId(node, ANCHOR_CONCEPT_ATTR);

			EntityId srcProp = getCoreId(node, SOURCE_PROPERTY_ATTR);
			EntityId tgtProp = getCoreId(node, TARGET_PROPERTY_ATTR);

			return new AnchoredConstraintType(anchor, srcProp, tgtProp, rootSrc, rootTgt);
		}
	}

	Model loadCoreModel() {

		Model model = new Model(getCoreNamespace(), getContentNamespace());

		loadHierarchies(model);

		new SimpleConstraintTypesLoader(model);
		new AnchoredConstraintTypesLoader(model);

		return model;
	}

	File getContentFile() {

		return rootNode.getResource(CONTENT_FILEATTR, KConfigResourceFinder.FILES);
	}

	String getContentNamespace() {

		return rootNode.getString(CONTENT_NAMESPACE_ATTR);
	}

	private void loadHierarchies(Model model) {

		for (KConfigNode node : rootNode.getChildren(HIERARCHY_TAG)) {

			model.addHierarchy(getRootConceptIdSpec(node));
		}
	}

	private String getCoreNamespace() {

		return rootNode.getString(CORE_NAMESPACE_ATTR);
	}

	private EntityIdSpec getRootConceptIdSpec(KConfigNode hierarchyNode) {

		return getEntityIdSpec(hierarchyNode, ROOT_CONCEPT_ATTR);
	}

	private EntityIdSpec getEntityIdSpec(KConfigNode node, String tag) {

		return EntityIdSpec.fromName(node.getString(tag));
	}
}