/*
 * Copyright 2018 Cyface GmbH
 * 
 * This file is part of the Cyface Nodes.
 *
 * The Cyface Nodes is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * The Cyface Nodes is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Cyface Nodes. If not, see <http://www.gnu.org/licenses/>.
 */
package de.cyface.knime.nodes.smoothing.algorithm;

/**
 * A smoothing algorithm that smoothes a value using the weighted mean of the predecessors and successors.
 * The used weights decrease with distance from the smoothed value.
 * 
 * @author Klemens Muthmann
 * @version 1.0.0
 * @since 1.0.0
 */
public final class TriangularSmoothing implements Algorithm {

	@Override
	public double smooth(double[] window) {
		double smoothedValue = 0.0;
		double denominator = 0.0;
		for(int i=0; i < window.length; i++) {
			double weight = (i-(window.length/2)>0) ? Integer.valueOf(window.length-i).doubleValue() : Integer.valueOf(i+1).doubleValue();
			denominator += weight;
			smoothedValue += weight * window[i];
		}
		return smoothedValue/denominator;
	}

}
