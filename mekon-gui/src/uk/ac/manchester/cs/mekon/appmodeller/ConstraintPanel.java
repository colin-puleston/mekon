/**
 * Copyright (C) 2010, University of Manchester
 *
 * Bio Health Informatics Group
 */
package uk.ac.manchester.cs.mekon.appmodeller;

import java.awt.BorderLayout;
import java.util.*;
import javax.swing.*;

import uk.ac.manchester.cs.mekon.gui.util.*;

import uk.ac.manchester.cs.mekon.appmodeller.model.*;

/**
 * @author Colin Puleston
 */
class ConstraintPanel extends JPanel {

	static private final long serialVersionUID = -1;

	static private final String CONSTRAINT_UPDATE_PANEL_TITLE = "Current constraint";
	static private final String CONSTRAINT_CREATE_PANEL_TITLE = "New constraint";

	static private final String EDIT_CONSTRAINT_LABEL = "Edit...";
	static private final String CLEAR_CONSTRAINT_LABEL = "Clear";

	static private final String ADD_TARGET_LABEL = "Add target";
	static private final String REMOVE_TARGET_LABEL = "Del target";
	static private final String CREATE_CONSTRAINT_LABEL = "Create";

	private ConstraintType type;

	private abstract class Populator {

		static private final long serialVersionUID = -1;

		final Concept source;
		final Constraint constraint;

		final ConstraintTargetsTree targetsTree;

		Populator() {

			this(getRootSourceValue());
		}

		Populator(Concept source) {

			this(source, source.lookForLocalConstraint(type));
		}

		Populator(Concept source, Constraint constraint) {

			this.source = source;
			this.constraint = constraint;

			targetsTree = new ConstraintTargetsTree(constraint, getCellDisplay());
		}

		void populate() {

			add(new JScrollPane(targetsTree), BorderLayout.CENTER);
		}

		void repopulate() {

			removeAll();
			populate();
			revalidate();
		}

		void addSecondaryComponent(JComponent component) {

			add(component, BorderLayout.SOUTH);
		}

		abstract ConceptCellDisplay getCellDisplay();
	}

	private class DefaultPopulator extends Populator {

		static private final long serialVersionUID = -1;

		ConceptCellDisplay getCellDisplay() {

			return ConceptCellDisplay.INDIRECT_CONSTRAINT_TARGETS;
		}
	}

	private class RootConstraintPopulator extends Populator {

		static private final long serialVersionUID = -1;

		ConceptCellDisplay getCellDisplay() {

			return ConceptCellDisplay.DIRECT_CONSTRAINT_TARGETS;
		}
	}

	private class LocalConstraintPopulator extends Populator {

		static private final long serialVersionUID = -1;

		private class ConstraintEditButton extends GButton {

			static private final long serialVersionUID = -1;

			protected void doButtonThing() {

				repopulateForAncestorConstraint(true);
			}

			ConstraintEditButton() {

				super(EDIT_CONSTRAINT_LABEL);
			}
		}

		private class ConstraintClearButton extends GButton {

			static private final long serialVersionUID = -1;

			protected void doButtonThing() {

				constraint.remove();
				repopulateForAncestorConstraint(false);
			}

			ConstraintClearButton() {

				super(CLEAR_CONSTRAINT_LABEL);
			}
		}

		LocalConstraintPopulator(Concept source, Constraint constraint) {

			super(source, constraint);
		}

		void populate() {

			super.populate();

			addSecondaryComponent(createConstraintUpdatePanel());
		}

		ConceptCellDisplay getCellDisplay() {

			return ConceptCellDisplay.DIRECT_CONSTRAINT_TARGETS;
		}

		private void repopulateForAncestorConstraint(boolean forEditing) {

			AncestorConstraintPopulator populator = new AncestorConstraintPopulator(source);

			if (forEditing) {

				populator.initialiseTargetSelections(constraint.getTargetValues());
			}

			populator.repopulate();
		}

		private JComponent createConstraintUpdatePanel() {

			return TitledPanels.setTitle(
						createConstraintUpdateControls(),
						CONSTRAINT_UPDATE_PANEL_TITLE);
		}

		private JPanel createConstraintUpdateControls() {

			JPanel panel = new JPanel(new BorderLayout());

			panel.add(createConstraintUpdateButtons(), BorderLayout.EAST);

			return panel;
		}

		private JComponent createConstraintUpdateButtons() {

			return ControlsPanel.horizontal(
						new ConstraintEditButton(),
						new ConstraintClearButton());
		}
	}

	private class AncestorConstraintPopulator extends Populator {

		static private final long serialVersionUID = -1;

		private TargetSelectionsList targetSelectionsList = new TargetSelectionsList();

		private class TargetSelectionsList extends GList<Concept> {

			static private final long serialVersionUID = -1;

			TargetSelectionsList() {

				super(true);
			}

			void initialise(Set<Concept> targets) {

				for (Concept target : targets) {

					addEntity(target);
				}
			}

			void addEntity(Concept target) {

				addEntity(target, createCellDisplay(target));
			}

			private GCellDisplay createCellDisplay(Concept target) {

				return ConceptCellDisplay.INDIRECT_CONSTRAINT_TARGETS.getFor(target);
			}
		}

		private class TargetAddButton extends ConceptTreeSelectionDependentButton {

			static private final long serialVersionUID = -1;

			protected void doButtonThing() {

				addTargetSelection(targetsTree.getSelectedConcept());
				setEnabled(false);
			}

			TargetAddButton() {

				super(ADD_TARGET_LABEL, targetsTree);
			}

			boolean enableOnSelectedConcept(Concept selection) {

				return !selection.isRoot() && !selectionTarget(selection);
			}
		}

		private class TargetRemoveButton extends ListSelectionDependentButton<Concept> {

			static private final long serialVersionUID = -1;

			protected void doButtonThing() {

				removeTargetSelection(targetSelectionsList.getSelectedEntity());
			}

			TargetRemoveButton() {

				super(REMOVE_TARGET_LABEL, targetSelectionsList);
			}
		}

		private class ConstraintCreateButton extends GButton {

			static private final long serialVersionUID = -1;

			private class Enabler extends GListListener<Concept> {

				protected void onAdded(Concept entity) {

					setEnabled(true);
				}

				protected void onRemoved(Concept entity) {

					setEnabled(!getTargetSelections().isEmpty());
				}

				Enabler() {

					targetSelectionsList.addListListener(this);
				}
			}

			protected void doButtonThing() {

				if (source.addConstraint(type, getTargetSelections())) {

					resetSourceConcept(source);
				}
			}

			ConstraintCreateButton() {

				super(CREATE_CONSTRAINT_LABEL);

				setEnabled(false);

				new Enabler();
			}
		}

		AncestorConstraintPopulator(Concept source) {

			super(source, source.getClosestAncestorConstraint(type));
		}

		void populate() {

			super.populate();

			addSecondaryComponent(createConstraintCreationPanel());
		}

		void initialiseTargetSelections(Set<Concept> targets) {

			targetSelectionsList.initialise(targets);
		}

		ConceptCellDisplay getCellDisplay() {

			return ConceptCellDisplay.INDIRECT_CONSTRAINT_TARGETS;
		}

		private JPanel createConstraintCreationPanel() {

			JPanel panel = new JPanel(new BorderLayout());

			panel.add(new JScrollPane(targetSelectionsList), BorderLayout.CENTER);
			panel.add(createConstraintCreationButtons(), BorderLayout.SOUTH);

			return TitledPanels.setTitle(panel, CONSTRAINT_CREATE_PANEL_TITLE);
		}

		private JComponent createConstraintCreationButtons() {

			JPanel panel = new JPanel(new BorderLayout());

			panel.add(createConstraintTargetsButtons(), BorderLayout.WEST);
			panel.add(new ConstraintCreateButton(), BorderLayout.EAST);

			return panel;
		}

		private JComponent createConstraintTargetsButtons() {

			return ControlsPanel.horizontal(
						new TargetAddButton(),
						new TargetRemoveButton());
		}

		private void addTargetSelection(Concept newSelection) {

			for (Concept selection : getTargetSelections()) {

				if (conflictingConcepts(newSelection, selection)) {

					removeTargetSelection(selection);
				}
			}

			targetSelectionsList.addEntity(newSelection);
		}

		private void removeTargetSelection(Concept selection) {

			targetSelectionsList.removeEntity(selection);
		}

		private List<Concept> getTargetSelections() {

			return targetSelectionsList.getEntityList();
		}

		private boolean selectionTarget(Concept concept) {

			return targetSelectionsList.containsEntity(concept);
		}

		private boolean conflictingConcepts(Concept concept1, Concept concept2) {

			return concept1.descendantOf(concept2) || concept2.descendantOf(concept1);
		}
	}

	private class SourceConceptTracker extends GSelectionListener<GNode> {

		private ConceptTree sourcesTree;

		protected void onSelected(GNode node) {

			resetSourceConcept(sourcesTree.getSelectedConcept());
		}

		protected void onSelectionCleared() {

			clearSourceConcept();
		}

		SourceConceptTracker(ConceptTree sourcesTree) {

			this.sourcesTree = sourcesTree;

			sourcesTree.addNodeSelectionListener(this);
		}
	}

	ConstraintPanel(ConstraintType type, ConceptTree sourcesTree) {

		super(new BorderLayout());

		this.type = type;

		new DefaultPopulator().populate();
		new SourceConceptTracker(sourcesTree);
	}

	String getPanelTitle() {

		return type.getTargetLink().getValue().getConceptId().getLabel();
	}

	private void resetSourceConcept(Concept source) {

		createPopulator(source).repopulate();
	}

	private void clearSourceConcept() {

		new DefaultPopulator().repopulate();
	}

	private Populator createPopulator(Concept source) {

		if (source.equals(getRootSourceValue())) {

			return new RootConstraintPopulator();
		}

		Constraint constraint = source.lookForLocalConstraint(type);

		if (constraint != null) {

			return new LocalConstraintPopulator(source, constraint);
		}

		return new AncestorConstraintPopulator(source);
	}

	private Concept getRootSourceValue() {

		return type.getSourceLink().getValue();
	}
}
