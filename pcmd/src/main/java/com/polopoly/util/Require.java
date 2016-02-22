package com.polopoly.util;

public final class Require {

	private Require() {
		// prevent instantiation of utility class
	}

	/**
	 * Intended to be used in constructors.
	 * 
	 * @throws IllegalArgumentException
	 *             Thrown when required object is {@code null}.
	 */
	public static <T> T require(T object) throws IllegalArgumentException {
		if (object == null) {
			throw new IllegalArgumentException("The object was null.");
		}

		return object;
	}

}
