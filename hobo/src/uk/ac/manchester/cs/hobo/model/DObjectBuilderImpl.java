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

package uk.ac.manchester.cs.hobo.model;

import java.util.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.mechanism.*;
import uk.ac.manchester.cs.hobo.*;
import uk.ac.manchester.cs.hobo.modeller.*;

/**
 * @author Colin Puleston
 */
class DObjectBuilderImpl implements DObjectBuilder {

	private DModel model;
	private IFrame frame;
	private DEditor dEditor = new DEditorImpl();

	private List<DObjectInitialiser> initialisers = new ArrayList<DObjectInitialiser>();
	private Map<DField<?>, FieldSlot> fieldSlots = new HashMap<DField<?>, FieldSlot>();

	private class DEditorImpl implements DEditor {

		public IEditor getIEditor() {

			return model.getIEditor();
		}


		public <V>DCell<V> getCell(DCellViewer<V> viewer) {

			return viewer.getField();
		}

		public <V>DArray<V> getArray(DArrayViewer<V> viewer) {

			return viewer.getField();
		}
	}

	public DCell<DConcept<DObject>> addConceptCell() {

		return addConceptCell(DObject.class);
	}

	public <D extends DObject>DCell<DConcept<D>> addConceptCell(Class<D> valueClass) {

		return addField(createConceptCell(valueClass));
	}

	public <D extends DObject>DCell<D> addObjectCell(Class<D> valueClass) {

		return addField(createObjectCell(valueClass));
	}

	public DCell<Integer> addIntegerCell() {

		return addNumberCell(DNumberRange.INTEGER);
	}

	public DCell<Long> addLongCell() {

		return addNumberCell(DNumberRange.LONG);
	}

	public DCell<Float> addFloatCell() {

		return addNumberCell(DNumberRange.FLOAT);
	}

	public DCell<Double> addDoubleCell() {

		return addNumberCell(DNumberRange.DOUBLE);
	}

	public <N extends Number>DCell<N> addNumberCell(DNumberRange<N> range) {

		return addField(new DCell<N>(model, new DNumberValueType<N>(range)));
	}

	public DArray<DConcept<DObject>> addConceptArray() {

		return addConceptArray(DObject.class);
	}

	public <D extends DObject>DArray<DConcept<D>> addConceptArray(Class<D> valueClass) {

		return addField(createConceptArray(valueClass));
	}

	public <D extends DObject>DArray<D> addObjectArray(Class<D> valueClass) {

		return addField(createObjectArray(valueClass));
	}

	public void setContainerClass(
					DField<?> field,
					Class<? extends DObject> containerClass) {

		getFieldSlot(field).setContainerClass(containerClass);
	}

	public void setFieldName(DField<?> field, String fieldName) {

		getFieldSlot(field).setFieldName(fieldName);
	}

	public void setSlotLabel(DField<?> field, String slotLabel) {

		getFieldSlot(field).setSlotLabel(slotLabel);
	}

	public void setEditability(DField<?> field, CEditability editability) {

		getFieldSlot(field).setEditability(editability);
	}

	public void setUniqueTypes(DArray<?> array, boolean uniqueTypes) {

		array.setUniqueTypes(uniqueTypes);
	}

	public void addInitialiser(DObjectInitialiser initialiser) {

		initialisers.add(initialiser);
	}

	public DModel getModel() {

		return model;
	}

	public DEditor getEditor() {

		return dEditor;
	}

	public IFrame getFrame() {

		return frame;
	}

	public <V>DCellViewer<V> getViewer(DCell<V> cell) {

		return cell.createViewer();
	}

	public <V>DArrayViewer<V> getViewer(DArray<V> array) {

		return array.createViewer();
	}

	public <D extends DObject>DCell<DDisjunction<D>> deriveDisjunctionCell(
														DCell<D> objectCell) {

		return FieldDeriver.deriveDisjunctionCell(model, objectCell);
	}

	public <N extends Number>DCell<DNumberRange<N>> deriveNumberRangeCell(
														DCell<N> numberCell) {

		return FieldDeriver.deriveNumberRangeCell(model, numberCell);
	}

	DObjectBuilderImpl(DModel model, IFrame frame) {

		this.model = model;
		this.frame = frame;
	}

	void configureFields(DObject containerObj) {

		for (DField<?> field : fieldSlots.keySet()) {

			field.setSlot(getFieldSlot(field).resolveSlot(containerObj));
		}
	}

	void invokeInitialisers() {

		for (DObjectInitialiser initialiser : initialisers) {

			initialiser.initialise();
		}
	}

	private <D extends DObject>DCell<DConcept<D>> createConceptCell(Class<D> valueClass) {

		return createCell(createConceptValueType(valueClass));
	}

	private <D extends DObject>DCell<D> createObjectCell(Class<D> valueClass) {

		return createCell(createObjectValueType(valueClass));
	}

	private <D extends DObject>DArray<DConcept<D>> createConceptArray(Class<D> valueClass) {

		return createArray(createConceptValueType(valueClass));
	}

	private <D extends DObject>DArray<D> createObjectArray(Class<D> valueClass) {

		return createArray(createObjectValueType(valueClass));
	}

	private <V>DCell<V> createCell(DValueType<V> valueType) {

		return new DCell<V>(model, valueType);
	}

	private <V>DArray<V> createArray(DValueType<V> valueType) {

		return new DArray<V>(model, valueType);
	}

	private <D extends DObject>DValueType<D> createObjectValueType(Class<D> valueClass) {

		return new DObjectValueType<D>(model, valueClass, getCFrame(valueClass));
	}

	private <D extends DObject>DValueType<DConcept<D>> createConceptValueType(Class<D> valueClass) {

		return new DConceptValueType<D>(model, valueClass, getCFrame(valueClass));
	}

	private <F extends DField<?>>F addField(F field) {

		fieldSlots.put(field, new FieldSlot(model, field));

		return field;
	}

	private FieldSlot getFieldSlot(DField<?> field) {

		FieldSlot fieldSlot = fieldSlots.get(field);

		if (fieldSlot == null) {

			throw new HAccessException(
						"Specified field has not "
						+ "been constructed by this class");
		}

		return fieldSlot;
	}

	private CFrame getCFrame(Class<? extends DObject> dClass) {

		if (dClass == DObject.class) {

			return model.getCModel().getRootFrame();
		}

		return model.getFrame(dClass);
	}
}
