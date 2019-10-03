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

package uk.ac.manchester.cs.mekon.model.util;

import java.util.*;

import uk.ac.manchester.cs.mekon.model.*;

/**
 * Represents an intersection of {@link CFrame} objects.
 *
 * @author Colin Puleston
 */
public class CFrameIntersection extends CTypeValueIntersection<CFrame> {

	private MostSpecificCFrames mostSpecifics = new MostSpecificCFrames();

	private class Intersector {

		private List<CFrame> disjuncts = new ArrayList<CFrame>();

		Intersector() {

			if (findDisjuncts()) {

				purgeDisjuncts();
			}
		}

		CFrame intersect() {

			if (disjuncts.isEmpty()) {

				return null;
			}

			if (disjuncts.size() == 1) {

				return disjuncts.get(0);
			}

			return CFrame.resolveDisjunction(disjuncts);
		}

		private boolean findDisjuncts() {

			for (CFrame operand : mostSpecifics.getCurrents()) {

				List<CFrame> subsumeds = operand.getSubsumeds();

				if (disjuncts.isEmpty()) {

					disjuncts.addAll(subsumeds);
				}
				else {

					disjuncts.retainAll(subsumeds);

					if (disjuncts.isEmpty()) {

						return false;
					}
				}
			}

			return true;
		}

		private void purgeDisjuncts() {

			Set<CFrame> allDisjunctSubs = new HashSet<CFrame>();

			for (CFrame disjunct : disjuncts) {

				allDisjunctSubs.addAll(disjunct.getSubs());
			}

			disjuncts.removeAll(allDisjunctSubs);
		}
	}

	/**
	 * Constructor.
	 */
	public CFrameIntersection() {
	}

	/**
	 * Constructor.
	 *
	 * @param operands Initial operands to add
	 */
	public CFrameIntersection(Collection<CFrame> operands) {

		addOperands(operands);
	}

	/**
	 * {@inheritDoc}
	 */
	public void addOperand(CFrame operand) {

		mostSpecifics.update(operand);
	}

	/**
	 * {@inheritDoc}
	 */
	public CFrame getCurrent() {

		return new Intersector().intersect();
	}

	Class<CFrame> getOperandType() {

		return CFrame.class;
	}
}
