/**
 * Copyright (C) 2010, University of Manchester
 *
 * Bio Health Informatics Group
 */
package uk.ac.manchester.cs.mekon.appmodeller;

import java.util.*;
import javax.swing.*;

import uk.ac.manchester.cs.mekon.gui.util.*;

import uk.ac.manchester.cs.mekon.appmodeller.model.*;

/**
 * @author Colin Puleston
 */

abstract class ConceptTree extends GTree {

	static private final long serialVersionUID = -1;

	static Concept extractConcept(GNode selectedNode) {

		return extractConcept((ConceptTreeNode)selectedNode);
	}

	static private Concept extractConcept(ConceptTreeNode selectedNode) {

		return selectedNode != null ? selectedNode.getConcept() : null;
	}

	private abstract class ConceptTreeNode extends GNode {

		protected boolean orderedChildren() {

			return true;
		}

		ConceptTreeNode() {

			super(ConceptTree.this);
		}

		void addChildrenFor(Set<Concept> concepts) {

			for (Concept concept : concepts) {

				addChildFor(concept);
			}
		}

		void addChildFor(Concept concept) {

			addChild(new ConceptNode(concept));
		}

		abstract Concept getConcept();

		ConceptNode findDescendantNode(Concept forConcept) {

			for (GNode child : getChildren()) {

				ConceptNode found = ((ConceptNode)child).findNode(forConcept);

				if (found != null) {

					return found;
				}
			}

			return null;
		}
	}

	private class RootNode extends ConceptTreeNode {

		private Set<Concept> rootConcepts;

		protected void addInitialChildren() {

			addChildrenFor(rootConcepts);
		}

		protected GCellDisplay getDisplay() {

			return GCellDisplay.NO_DISPLAY;
		}

		RootNode(Set<Concept> rootConcepts) {

			this.rootConcepts = rootConcepts;
		}

		Concept getConcept() {

			throw new Error("Cannot get concept from root node!");
		}
	}

	private class ConceptNode extends ConceptTreeNode {

		private Concept concept;

		private class ModelUpdateTracker implements ConceptListener {

			public String toString() {

				return "ConceptTreeNode: " + concept;
			}

			public void onConceptMoved(Concept concept) {

				remove();

				ConceptTreeNode parentNode = findParentNodeFor(concept);

				if (parentNode != null) {

					parentNode.addChild(ConceptNode.this);
				}
			}

			public void onConceptRemoved(Concept concept) {

				remove();
			}

			public void onChildAdded(Concept child) {

				addChildFor(child);
			}

			ModelUpdateTracker() {

				concept.addListener(this);
			}
		}

		protected void addInitialChildren() {

			addChildrenFor(concept.getChildren());
		}

		protected GCellDisplay getDisplay() {

			return getConceptDisplay(concept).getFor(concept);
		}

		ConceptNode(Concept concept) {

			this.concept = concept;

			new ModelUpdateTracker();
		}

		Concept getConcept() {

			return concept;
		}

		ConceptNode findNode(Concept forConcept) {

			return concept.equals(forConcept) ? this : findDescendantNode(forConcept);
		}
	}

	ConceptTree() {

		setRootVisible(false);
		setShowsRootHandles(true);
	}

	void initialise(Concept rootConcept) {

		initialise(Collections.singleton(rootConcept));
	}

	void initialise(Set<Concept> rootConcepts) {

		initialise(new RootNode(rootConcepts));
	}

	Concept getSelectedConcept() {

		return extractConcept((ConceptTreeNode)getSelectedNode());
	}

	abstract ConceptCellDisplay getConceptDisplay(Concept concept);

	private ConceptTreeNode findParentNodeFor(Concept concept) {

		ConceptTreeNode root = (ConceptTreeNode)getRootNode();

		return concept.isRoot() ? root : root.findDescendantNode(concept.getParent());
	}
}
