package de.cyface.smoothing;

import de.cyface.smoothing.algorithm.Algorithm;
import de.cyface.smoothing.algorithm.RectangularSmoothing;
import de.cyface.smoothing.algorithm.TriangularSmoothing;

/**
 * A listing of the available filters for smoothing.
 * 
 * @author Klemens Muthmann
 * @version 1.0.0
 * @since 1.0.0
 */
public enum Filter {
	/**
	 * The Rectangular filter for smoothing a signal. It calculates the smoothed
	 * value as the mean between the successor and the predecessor.
	 */
	RECTANGULAR("Rectangular", new RectangularSmoothing()), TRIANGULAR("Triangular", new TriangularSmoothing());

	/**
	 * The algorithms name as it is going to appear on the UI.
	 */
	private final String name;
	
	private final Algorithm algorithm;

	/**
	 * Creates a new completely initialized {@link Filter}.
	 * 
	 * @param name
	 *            The algorithms name as it is going to appear on the UI.
	 */
	private Filter(final String name, final Algorithm algorithm) {
		this.name = name;
		this.algorithm = algorithm;
	}

	/**
	 * @return The algorithms name as it is going to appear on the UI.
	 */
	public String getName() {
		return name;
	}
	
	public Algorithm getAlgorithm() {
		return algorithm;
	}

	@Override
	public String toString() {
		return name;
	}
}
