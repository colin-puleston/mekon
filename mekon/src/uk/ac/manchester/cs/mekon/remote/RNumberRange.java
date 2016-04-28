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

package uk.ac.manchester.cs.mekon.remote;

/**
 * Represents a numeric range.
 *
 * @author Colin Puleston
 */
public class RNumberRange {

	/**
	 * Represents an unconstrained integer-range.
	 */
	static public final RNumberRange INTEGER = new RNumberRange(Integer.class);

	/**
	 * Represents an unconstrained long-range.
	 */
	static public final RNumberRange LONG = new RNumberRange(Long.class);

	/**
	 * Represents an unconstrained float-range.
	 */
	static public final RNumberRange FLOAT = new RNumberRange(Float.class);

	/**
	 * Represents an unconstrained double-range.
	 */
	static public final RNumberRange DOUBLE = new RNumberRange(Double.class);

	/**
	 * Creates a integer-range with the specified limits.
	 *
	 * @param min Minimnum value for range
	 * @param max Maximnum value for range
	 * @return Created range
	 */
	static public RNumberRange range(Integer min, Integer max) {

		return new RNumberRange(Integer.class, min, max);
	}

	/**
	 * Creates a long-range with the specified limits.
	 *
	 * @param min Minimnum value for range
	 * @param max Maximnum value for range
	 * @return Created range
	 */
	static public RNumberRange range(Long min, Long max) {

		return new RNumberRange(Long.class, min, max);
	}

	/**
	 * Creates a float-range with the specified limits.
	 *
	 * @param min Minimnum value for range
	 * @param max Maximnum value for range
	 * @return Created range
	 */
	static public RNumberRange range(Float min, Float max) {

		return new RNumberRange(Float.class, min, max);
	}

	/**
	 * Creates a double-range with the specified limits.
	 *
	 * @param min Minimnum value for range
	 * @param max Maximnum value for range
	 * @return Created range
	 */
	static public RNumberRange range(Double min, Double max) {

		return new RNumberRange(Double.class, min, max);
	}

	/**
	 * Creates a integer-range with the specified minimum value.
	 *
	 * @param min Minimnum value for range
	 * @return Created range
	 */
	static public RNumberRange min(Integer min) {

		return range(min, null);
	}

	/**
	 * Creates a long-range with the specified minimum value.
	 *
	 * @param min Minimnum value for range
	 * @return Created range
	 */
	static public RNumberRange min(Long min) {

		return range(min, null);
	}


	/**
	 * Creates a float-range with the specified minimum value.
	 *
	 * @param min Minimnum value for range
	 * @return Created range
	 */
	static public RNumberRange min(Float min) {

		return range(min, null);
	}


	/**
	 * Creates a double-range with the specified minimum value.
	 *
	 * @param min Minimnum value for range
	 * @return Created range
	 */
	static public RNumberRange min(Double min) {

		return range(min, null);
	}

	/**
	 * Creates a integer-range with the specified maximum value.
	 *
	 * @param max Maximnum value for range
	 * @return Created range
	 */
	static public RNumberRange max(Integer max) {

		return range(null, max);
	}

	/**
	 * Creates a long-range with the specified maximum value.
	 *
	 * @param max Maximnum value for range
	 * @return Created range
	 */
	static public RNumberRange max(Long max) {

		return range(null, max);
	}

	/**
	 * Creates a float-range with the specified maximum value.
	 *
	 * @param max Maximnum value for range
	 * @return Created range
	 */
	static public RNumberRange max(Float max) {

		return range(null, max);
	}

	/**
	 * Creates a double-range with the specified maximum value.
	 *
	 * @param max Maximnum value for range
	 * @return Created range
	 */
	static public RNumberRange max(Double max) {

		return range(null, max);
	}

	/**
	 * Creates a integer-range with the specified exact value.
	 *
	 * @param exact Exact value for range
	 * @return Created range
	 */
	static public RNumberRange exact(Integer exact) {

		return range(exact, exact);
	}

	/**
	 * Creates a long-range with the specified exact value.
	 *
	 * @param exact Exact value for range
	 * @return Created range
	 */
	static public RNumberRange exact(Long exact) {

		return range(exact, exact);
	}

	/**
	 * Creates a float-range with the specified exact value.
	 *
	 * @param exact Exact value for range
	 * @return Created range
	 */
	static public RNumberRange exact(Float exact) {

		return range(exact, exact);
	}

	/**
	 * Creates a double-range with the specified exact value.
	 *
	 * @param exact Exact value for range
	 * @return Created range
	 */
	static public RNumberRange exact(Double exact) {

		return range(exact, exact);
	}

	private Class<? extends Number> type;
	private Number min;
	private Number max;

	/**
	 * Tests for equality between this and other specified object.
	 *
	 * @param other Object to test for equality with this one
	 * @return true if other object is another <code>RNumberRange</code>
	 * with same limit-values, including value-type, as this one
	 */
	public boolean equals(Object other) {

		if (other == this) {

			return true;
		}

		if (other instanceof RNumberRange) {

			return equalsRange((RNumberRange)other);
		}

		return false;
	}

	/**
	 * Provides hash-code based on number-type and limit-values.
	 *
	 * @return hash-code for this object
	 */
	public int hashCode() {

		return type.hashCode() + min.hashCode() + max.hashCode();
	}

	/**
	 * Provides the primitive Java <code>Number</code> type for the
	 * range.
	 *
	 * @return Relevant <code>Number</code> type
	 */
	public Class<? extends Number> getType() {

		return type;
	}

	/**
	 * {@inheritDoc}
	 */
	public String toString() {

		return getClass().getSimpleName() + "(" + limitsToString() + ")";
	}

	/**
	 * Specifies whether a minimnum value is defined.
	 *
	 * @return True if min value defined
	 */
	public boolean hasMin() {

		return min != null;
	}

	/**
	 * Specifies whether a maximnum value is defined.
	 *
	 * @return True if max value defined
	 */
	public boolean hasMax() {

		return max != null;
	}

	/**
	 * Provides the minimnum value, if defined.
	 *
	 * @return Minimnum value, or null in not defined
	 */
	public Number getMin() {

		return min;
	}

	/**
	 * Provides the maximnum value, if defined.
	 *
	 * @return Maximnum value, or null in not defined
	 */
	public Number getMax() {

		return max;
	}

	RNumberRange(Class<? extends Number> type, Number min, Number max) {

		this.type = type;
		this.min = min;
		this.max = max;
	}

	private RNumberRange(Class<? extends Number> type) {

		this(type, null, null);
	}

	private boolean equalsRange(RNumberRange other) {

		return type == other.type
				&& equalLimits(min, other.min)
				&& equalLimits(max, other.max);
	}

	private boolean equalLimits(Number l1, Number l2) {

		return l1 == l2 || (l1 != null && l1.equals(l2));
	}

	private String limitsToString() {

		String limits = limitToString(min);

		if (min.equals(max)) {

			limits += ("-" + limitToString(max));
		}

		return limits;
	}

	private String limitToString(Number limit) {

		return limit != null ? limit.toString() : "?";
	}
}

