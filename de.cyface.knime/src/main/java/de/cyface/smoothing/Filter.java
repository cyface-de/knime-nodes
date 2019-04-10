/*
 * Copyright 2018 Cyface GmbH
 * 
 * This file is part of the Cyface KNIME Nodes.
 *
 * The Cyface KNIME Nodes is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * The Cyface KNIME Nodes is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Cyface KNIME Nodes. If not, see <http://www.gnu.org/licenses/>.
 */
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
