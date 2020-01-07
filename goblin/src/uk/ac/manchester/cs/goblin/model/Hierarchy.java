package uk.ac.manchester.cs.goblin.model;

import java.util.*;

/**
 * @author Colin Puleston
 */
public class Hierarchy {

	private Model model;

	private RootConcept root;
	private List<ConstraintType> constraintTypes = new ArrayList<ConstraintType>();

	private Map<EntityId, Concept> conceptsById = new HashMap<EntityId, Concept>();

	private class RootConcept extends Concept {

		public boolean resetId(EntityIdSpec newIdSpec) {

			throw createInvalidRootOperationException();
		}

		public boolean move(Concept newParent) {

			throw createInvalidRootOperationException();
		}

		public void remove() {

			throw createInvalidRootOperationException();
		}

		public boolean addConstraint(ConstraintType type, Collection<Concept> targetValues) {

			throw createInvalidRootOperationException();
		}

		public boolean isRoot() {

			return true;
		}

		public Concept getParent() {

			throw createInvalidRootOperationException();
		}

		public boolean descendantOf(Concept test) {

			return false;
		}

		public Constraint getClosestAncestorConstraint(ConstraintType type) {

			throw createInvalidRootOperationException();
		}

		RootConcept(EntityId rootConceptId) {

			super(Hierarchy.this, rootConceptId);
		}

		void doRemoveConstraint(Constraint constraint) {

			throw createInvalidRootOperationException();
		}

		private RuntimeException createInvalidRootOperationException() {

			return new RuntimeException("Cannot perform operation on root concept!");
		}
	}

	public Hierarchy(Model model, EntityId rootConceptId) {

		this.model = model;

		root = new RootConcept(rootConceptId);
	}

	public void addConstraintType(ConstraintType type) {

		constraintTypes.add(type);
		root.addRootConstraint(type);
	}

	public Model getModel() {

		return model;
	}

	public Concept getRoot() {

		return root;
	}

	public boolean hasConcept(EntityId conceptId) {

		return conceptsById.containsKey(conceptId);
	}

	public Concept getConcept(EntityId conceptId) {

		Concept concept = conceptsById.get(conceptId);

		if (concept == null) {

			throw new RuntimeException("Cannot find concept: " + conceptId);
		}

		return concept;
	}

	public boolean isConstrained() {

		return !constraintTypes.isEmpty();
	}

	public List<ConstraintType> getConstraintTypes() {

		return new ArrayList<ConstraintType>(constraintTypes);
	}

	void registerConcept(Concept concept) {

		conceptsById.put(concept.getConceptId(), concept);
	}

	void deregisterConcept(Concept concept) {

		conceptsById.remove(concept.getConceptId());
	}
}
