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

package uk.ac.manchester.cs.mekon.model.serial;

import java.util.*;

import uk.ac.manchester.cs.mekon.*;
import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.xdoc.*;

/**
 * Renderer for the standard XML serialisation of {@link IFrame}/{@link ISlot}
 * networks.
 *
 * @author Colin Puleston
 */
public class IFrameRenderer extends ISerialiser {

	private boolean renderAsTree = false;
	private ISchemaRender schemaRender = ISchemaRender.NONE;

	private class OneTimeRenderer {

		private XNode containerNode;
		private IFrameXDocIds iFrameXDocIds;

		private class ISlotValueTypeRenderer extends CValueVisitor {

			private XNode slotNode;

			protected void visit(CFrame value) {

				renderCFrame(value, slotNode);
			}

			protected void visit(CNumber value) {

				renderCNumber(value, slotNode);
			}

			protected void visit(CString value) {

				renderCString(value, slotNode);
			}

			protected void visit(MFrame value) {

				renderMFrame(value, slotNode);
			}

			ISlotValueTypeRenderer(ISlot slot, XNode slotNode) {

				this.slotNode = slotNode;

				visit(slot.getValueType());
			}
		}

		private class ISlotValuesRenderer extends ISlotValuesVisitor {

			private XNode valuesNode;

			protected void visit(CFrame valueType, List<IFrame> values) {

				for (IFrame value : values) {

					renderIFrame(value, valuesNode);
				}
			}

			protected void visit(CNumber valueType, List<INumber> values) {

				for (INumber value : values) {

					renderINumber(value, valuesNode);
				}
			}

			protected void visit(CString valueType, List<IString> values) {

				for (IString value : values) {

					renderIString(value, valuesNode);
				}
			}

			protected void visit(MFrame valueType, List<CFrame> values) {

				for (CFrame value : values) {

					renderCFrame(value, valuesNode);
				}
			}

			ISlotValuesRenderer(ISlot slot, XNode slotNode) {

				valuesNode = slotNode.addChild(IVALUES_ID);

				visit(slot);
			}
		}

		OneTimeRenderer(XNode containerNode, IFrameXDocIds iFrameXDocIds) {

			this.containerNode = containerNode;
			this.iFrameXDocIds = iFrameXDocIds;
		}

		void render(IFrame frame) {

			renderAtomicIFrame(frame, containerNode, true);
		}

		private void renderIFrame(IFrame frame, XNode parentNode) {

			if (frame.getCategory().disjunction()) {

				renderDisjunctionIFrame(frame, parentNode);
			}
			else {

				renderAtomicIFrame(frame, parentNode, renderAsTree);
			}
		}

		private void renderAtomicIFrame(IFrame frame, XNode parentNode, boolean direct) {

			IFrameXDocIds.Resolution xidRes = iFrameXDocIds.resolve(frame);

			if (direct) {

				renderAtomicIFrameDirect(frame, parentNode, xidRes.getId());
			}
			else {

				if (xidRes.newFrame()) {

					renderAtomicIFrameIndirect(frame, parentNode, xidRes.getId());
				}
			}
		}

		private void renderAtomicIFrameDirect(IFrame frame, XNode parentNode, String xid) {

			XNode node = renderAtomicIFrameCommon(parentNode, xid, IFRAME_XDOC_ID_ATTR);

			renderCFrame(frame.getType(), node);

			for (ISlot slot : frame.getSlots().asList()) {

				if (slotToBeRendered(slot)) {

					renderISlot(slot, node);
				}
			}
		}

		private void renderAtomicIFrameIndirect(IFrame frame, XNode parentNode, String xid) {

			renderAtomicIFrameCommon(parentNode, xid, IFRAME_XDOC_ID_REF_ATTR);
			renderAtomicIFrameDirect(frame, containerNode, xid);
		}

		private XNode renderAtomicIFrameCommon(XNode parentNode, String xid, String xidTag) {

			XNode node = parentNode.addChild(IFRAME_ID);

			node.addValue(xidTag, xid);

			return node;
		}

		private void renderDisjunctionIFrame(IFrame frame, XNode parentNode) {

			XNode node = parentNode.addChild(IFRAME_ID);

			for (IFrame disjunct : frame.asDisjuncts()) {

				renderIFrame(disjunct, node);
			}
		}

		private void renderCFrame(CFrame frame, XNode parentNode) {

			renderCFrame(frame, parentNode, CFRAME_ID);
		}

		private void renderCFrame(CFrame frame, XNode parentNode, String tag) {

			XNode node = parentNode.addChild(tag);

			if (frame.getCategory().disjunction()) {

				for (CFrame disjunct : frame.getSubs()) {

					renderIdentity(disjunct, node.addChild(tag));
				}
			}
			else {

				renderIdentity(frame, node);
			}
		}

		private void renderMFrame(MFrame frame, XNode parentNode) {

			renderCFrame(frame.getRootCFrame(), parentNode, MFRAME_ID);
		}

		private void renderCNumber(CNumber number, XNode parentNode) {

			XNode node = parentNode.addChild(CNUMBER_ID);

			renderNumberType(number, node);
			renderNumberRange(number, node);
		}

		private void renderCString(CString number, XNode parentNode) {

			parentNode.addChild(CSTRING_ID);
		}

		private void renderINumber(INumber number, XNode parentNode) {

			XNode node = parentNode.addChild(INUMBER_ID);

			if (number.indefinite()) {

				renderNumberRange(number.getType(), node);
			}
			else {

				node.addValue(NUMBER_VALUE_ATTR, number.asTypeNumber());
			}
		}

		private void renderIString(IString number, XNode parentNode) {

			XNode node = parentNode.addChild(ISTRING_ID);

			node.addValue(STRING_VALUE_ATTR, number.get());
		}

		private void renderNumberType(CNumber number, XNode node) {

			renderClassId(number.getNumberType(), node, NUMBER_TYPE_ATTR);
		}

		private void renderNumberRange(CNumber number, XNode node) {

			if (number.hasMin()) {

				node.addValue(NUMBER_MIN_ATTR, number.getMin().asTypeNumber());
			}

			if (number.hasMax()) {

				node.addValue(NUMBER_MAX_ATTR, number.getMax().asTypeNumber());
			}
		}

		private void renderISlot(ISlot slot, XNode parentNode) {

			XNode node = parentNode.addChild(ISLOT_ID);

			renderCSlot(slot.getType(), node);

			if (schemaRender.includesBasics()) {

				new ISlotValueTypeRenderer(slot, node);
			}

			if (schemaRender.includesDetails()) {

				node.addValue(EDITABILITY_ATTR, slot.getEditability());
			}

			if (!slot.getValues().isEmpty()) {

				new ISlotValuesRenderer(slot, node);
			}
		}

		private void renderCSlot(CSlot slot, XNode parentNode) {

			XNode node = parentNode.addChild(CSLOT_ID);

			renderIdentity(slot, node);

			if (schemaRender.includesDetails()) {

				node.addValue(CARDINALITY_ATTR, slot.getCardinality());
			}
		}
	}

	/**
	 * Sets whether the recursive frame description should be rendered
	 * as a tree, rather than a graph. By default it will be rendered
	 * as a graph.
	 *
	 * @param renderAsTree True if tree rendering required
	 */
	public void setRenderAsTree(boolean renderAsTree) {

		this.renderAsTree = renderAsTree;
	}

	/**
	 * Sets the type of schema information to be rendered. Defaults to
	 * {@link ISchemaRender#NONE}.
	 *
	 * @param schemaRender Required schema-level
	 */
	public void setSchemaRender(ISchemaRender schemaRender) {

		this.schemaRender = schemaRender;
	}

	/**
	 * Renders the specified frame to produce an XML document.
	 *
	 * @param frame Frame to render
	 * @return Rendered document
	 */
	public XDocument render(IFrame frame) {

		return renderDocument(frame, new IFrameXDocIds());
	}

	/**
	 * Renders the specified frame to produce an XML document, and writes
	 * the document-specific frame-identifiers that are generated into the
	 * provided map. This map can also be pre-populated with any identifiers
	 * that are to be used in the rendering.
	 *
	 * @param frame Frame to render
	 * @param frameXDocIds Map for document-specific frame-identifiers
	 * (possibly pre-populated)
	 * @return Rendered document
	 */
	public XDocument render(IFrame frame, Map<IFrame, String> frameXDocIds) {

		return renderDocument(frame, new IFrameXDocIds(frameXDocIds));
	}

	/**
	 * Renders the specified frame to the specified parent-node.
	 *
	 * @param frame Frame to render
	 * @param parentNode Parent-node for rendering
	 */
	public void render(IFrame frame, XNode parentNode) {

		renderToChild(frame, parentNode, new IFrameXDocIds());
	}

	/**
	 * Renders the specified frame to the specified parent-node, and writes
	 * the document-specific frame-identifiers that are generated into the
	 * provided map. This map can also be pre-populated with any identifiers
	 * that are to be used in the rendering.
	 *
	 * @param frame Frame to render
	 * @param parentNode Parent-node for rendering
	 * @param frameXDocIds Map for document-specific frame-identifiers
	 * (possibly pre-populated)
	 */
	public void render(IFrame frame, XNode parentNode, Map<IFrame, String> frameXDocIds) {

		renderToChild(frame, parentNode, new IFrameXDocIds(frameXDocIds));
	}

	private XDocument renderDocument(IFrame frame, IFrameXDocIds frameXDocIds) {

		XDocument document = new XDocument(getTopLevelId());

		render(frame, document.getRootNode(), frameXDocIds);

		return document;
	}

	private void renderToChild(IFrame frame, XNode parentNode, IFrameXDocIds frameXDocIds) {

		render(frame, parentNode.addChild(getTopLevelId()), frameXDocIds);
	}

	private void render(IFrame frame, XNode containerNode, IFrameXDocIds frameXDocIds) {

		checkAtomicTopLevelFrame(frame);
		checkNonCyclicIfRenderingAsTree(frame);

		new OneTimeRenderer(containerNode, frameXDocIds).render(frame);
	}

	private boolean slotToBeRendered(ISlot slot) {

		return schemaRender.includesBasics() || !slot.getValues().isEmpty();
	}

	private void checkAtomicTopLevelFrame(IFrame frame) {

		if (frame.getCategory().disjunction()) {

			throw new KAccessException(
						"Cannot render instance whose top-level "
						+ "frame has DISJUNCTION category: " + frame);
		}
	}

	private void checkNonCyclicIfRenderingAsTree(IFrame frame) {

		if (renderAsTree && frame.leadsToCycle()) {

			throw new KAccessException(
						"Cannot render cyclic instance as tree: "
						+ "Top-level frame: " + frame);
		}
	}

	private String getTopLevelId() {

		return renderAsTree ? ITREE_ID : IGRAPH_ID;
	}
}
