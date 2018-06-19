package de.cyface.smoothing;

//TODO This should probably be a class for filters also encapsulating the algorithm.
/**
 * <p>
 * A listing of the available filters for smoothing.
 * </p>
 * 
 * @author Klemens Muthmann
 * @version 1.0.0
 * @since 1.0.0
 */
public enum Filter {
	/**
	 * <p>
	 * The Rectangular filter for smoothing a signal. It calculates the smoothed
	 * value as the mean between the successor and the predecessor.
	 * </p>
	 */
	RECTANGULAR("Rectangular"), TRIANGULAR("Triangular");

	/**
	 * <p>
	 * The algorithms name as it is going to appear on the UI.
	 * </p>
	 */
	private final String name;

	/**
	 * <p>
	 * Creates a new completely initialized {@link Filter}.
	 * </p>
	 * 
	 * @param name
	 *            The algorithms name as it is going to appear on the UI.
	 */
	private Filter(final String name) {
		this.name = name;
	}

	/**
	 * @return The algorithms name as it is going to appear on the UI.
	 */
	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return name;
	}
}
