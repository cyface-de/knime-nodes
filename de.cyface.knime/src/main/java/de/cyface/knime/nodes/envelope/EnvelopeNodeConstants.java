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
package de.cyface.knime.nodes.envelope;

/**
 * Constants used by the envelope node to identify settings and text that is displayed to the user.
 * 
 * @author Klemens Muthmann
 * @version 1.0.0
 * @since 1.0.0
 */
public final class EnvelopeNodeConstants {
	
    /**
     * The identifier of the input column to the values of the hull curve.
     */
	static final String INPUT_COLUMN_SELECTION_IDENTIFIER = "de.cyface.envelope.input";
	/**
	 * The label for the setting, where the user selects the input column to calculate the hull curve over.
	 */
	static final String INPUT_COLUMN_SELECTION_LABEL = "Select Input";

	/**
	 * Private constructor for utility class. This prevents erroneous instantiation of this class.
	 */
	private EnvelopeNodeConstants() {
		// Private constructor to avoid instantiation of this class.
	}

}
