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
package de.cyface.knime.nodes.smoothing;

/**
 * A utility class for storing all the constants required by the smoothing node.
 * 
 * @author Klemens Muthmann
 * @version 1.1.0
 * @since 1.0.0
 */
public final class SmoothingNodeConstants {

	/**
	 * The {@link String} denoting the append option appending the result column
	 * to the input table. This {@link String} is shown in the UI and used
	 * internally to identify if this option has been choosen.
	 */
	public static final String APPEND_OPTION = "Append";
	/**
	 * The {@link String} denoting the replace option for replacing the input
	 * column with result column. This {@link String} is shown in the UI and
	 * used internally to identify if this option has been choosen.
	 */
	public static final String REPLACE_OPTION = "Replace";

	/**
	 * Label for the filter type selection UI element.
	 */
	public static final String FILTER_TYPE_SELECTION_LABEL = "Filter Type";
	/**
	 * Label for the input column selection field.
	 */
	public static final String INPUT_COL_SELECTION_LABEL = "Input Column";
	/**
	 * Label for the selection of either to replace or to append the result to the input column.
	 */
	public static final String APPEND_REPLACE_CHOOSER_LABEL = "Append new column or replace input column";
	/**
	 * Label for the input text field for the name of the appended column if the append option was choosen.
	 */
	public static final String APPEND_COLUMN_NAME_INPUT_LABEL = "Appended column name";
	/**
	 * Label for the number input field for the window size to use for smoothing.
	 */
	public static final String WINDOW_SIZE_SELECTOR_LABEL = "Window Size";

	/**
	 * The identifier used by KNIME to identify the model for the filter type option.
	 */
	public final static String FILTER_TYPE_SELECTION_SETTINGS_MODEL_CONFIG_NAME = "de.cyface.smoothing.settings.filtertype";
	/**
	 * The identifier used by KNIME to identify the model for the input column selection.
	 */
	public final static String INPUT_COL_SELECTION_SETTINGS_MODEL_CONFIG_NAME = "de.cyface.smoothing.settings.inputcol";
	/**
	 * The identifier used by KNIME to identify the model for choosing between appending the result to the input table or replacing the input column.
	 */
	public final static String APPEND_REPLACE_CHOOSER_SETTINGS_MODEL_CONFIG_NAME = "de.cyface.smooting.settings.appendreplace";
	/**
	 * The identifier used by KNIME to identify the model for the text input field for the appended column name. 
	 */
	public final static String APPEND_COLUMN_NAME_INPUT_SETTINGS_MODEL_CONFIG_NAME = "de.cyface.smoothing.appendcolumnname";
	/**
	 * The identifier used by KNIME to identify the model for the input spinner for the appended column name.
	 */
	public static final String WINDOW_SIZE_SELECTOR_SETTINGS_MODEL_CONFIG_NAME = "de.cyface.smoothing.windowsize";

	/**
	 * Since this is a utility class, this private constructor should never be called.
	 */
	private SmoothingNodeConstants() {
		// Nothing to do here.
	}

}
