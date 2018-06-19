package de.cyface.smoothing;

/**
 * <p>
 * A utility class for storing all the constants required by the smoothing node.
 * </p>
 * 
 * @author Klemens Muthmann
 * @version 1.0.0
 * @since 1.0.0
 */
public final class SmoothingNodeConstants {

	/**
	 * <p>
	 * The {@link String} denoting the append option appending the result column
	 * to the input table. This {@link String} is shown in the UI and used
	 * internally to identify if this option has been choosen.
	 * </p>
	 */
	public static final String APPEND_OPTION = "Append";
	/**
	 * <p>
	 * The {@link String} denoting the replace option for replacing the input
	 * column with result column. This {@link String} is shown in the UI and
	 * used internally to identify if this option has been choosen.
	 * </p>
	 */
	public static final String REPLACE_OPTION = "Replace";

	/**
	 * <p>
	 * Label for the filter type selection UI element.
	 * </p>
	 */
	public static final String FILTER_TYPE_SELECTION_LABEL = "Filter Type";
	/**
	 * <p>
	 * Label for the input column selection field.
	 * </p>
	 */
	public static final String INPUT_COL_SELECTION_LABEL = "Input Column";
	/**
	 * <p>
	 * Label for the selection of either to replace or to append the result to the input column.
	 * </p>
	 */
	public static final String APPEND_REPLACE_CHOOSER_LABEL = "Append new column or replace input column";
	/**
	 * <p>
	 * Label for the input text field for the name of the appended column if the append option was choosen.
	 * </p>
	 */
	public static final String APPEND_COLUMN_NAME_INPUT_LABEL = "Appended column name";

	/**
	 * <p>
	 * The identifier used by KNIME to identify the model for the filter type option.
	 * </p>
	 */
	public final static String FILTER_TYPE_SELECTION_SETTINGS_MODEL_CONFIG_NAME = "de.cyface.smoothing.settings.filtertype";
	/**
	 * <p>
	 * The identifier used by KNIME to identify the model for the input column selection.
	 * </p>
	 */
	public final static String INPUT_COL_SELECTION_SETTINGS_MODEL_CONFIG_NAME = "de.cyface.smoothing.settings.inputcol";
	/**
	 * <p>
	 * The identifier used by KNIME to identify the model for choosing between appending the result to the input table or replacing the input column.
	 * </p>
	 */
	public final static String APPEND_REPLACE_CHOOSER_SETTINGS_MODEL_CONFIG_NAME = "de.cyface.smooting.settings.appendreplace";
	/**
	 * <p>
	 * The identifier used by KNIME to identify the model for the text input field for the appended column name. 
	 * </p>
	 */
	public final static String APPEND_COLUMN_NAME_INPUT_SETTINGS_MODEL_CONFIG_NAME = "de.cyface.smoothing.appendcolumnname";

	/**
	 * <p>
	 * Since this is a utility class, this private constructor should never be called.
	 * </p>
	 */
	private SmoothingNodeConstants() {
		// Nothing to do here.
	}

}
