package de.cyface.smoothing.dialog;

import org.knime.core.data.DataValue;
import org.knime.core.node.defaultnodesettings.DialogComponentColumnNameSelection;
import org.knime.core.node.defaultnodesettings.SettingsModel;
import org.knime.core.node.util.ColumnFilter;

/**
 * <p>
 * A {@link NodeOption} allowing to select a column from the input table.
 * </p>
 * 
 * @author Klemens Muthmann
 * @version 1.0.0
 * @since 1.0.0
 */
public class ColumnSelectionNodeOption extends StringSettingsModelOption {

	/**
	 * <p>
	 * Creates a new completely initialized {@link ColumnSelectionNodeOption}.
	 * </p>
	 * 
	 * @param configIdentifier
	 *            The identifier used by KNIME to find the corresponding
	 *            {@link SettingsModel}.
	 * @param label
	 *            The label appearing on the UI (usually to the left of the
	 *            input field).
	 * @param defaultValue
	 *            The initial starting value.
	 * @param columnFilter
	 *            A column filter, filtering all columns not usable as input.
	 *            Only columns passing the filter are available for selection.
	 */
	public ColumnSelectionNodeOption(final String configIdentifier, final String label, final String defaultValue,
			final ColumnFilter columnFilter) {
		super(configIdentifier, defaultValue);
		setComponent(new DialogComponentColumnNameSelection(getSettingsModel(), label, 0, columnFilter));
	}

	/**
	 * <p>
	 * Creates a new completely initialized {@link ColumnSelectionNodeOption}.
	 * </p>
	 * 
	 * @param configIdentifier
	 *            The identifier used by KNIME to find the corresponding
	 *            {@link SettingsModel}.
	 * @param label
	 *            The label appearing on the UI (usually to the left of the
	 *            input field).
	 * @param defaultValue
	 *            The initial starting value.
	 * @param acceptedClasses
	 *            A list of possible input data values. Only columns with input
	 *            values of one of the provided types are shown in the dialog.
	 */
	public ColumnSelectionNodeOption(final String configIdentifier, final String label, final String defaultValue,
			Class<? extends DataValue>... acceptedClasses) {
		super(configIdentifier, defaultValue);
		setComponent(new DialogComponentColumnNameSelection(getSettingsModel(), label, 0, acceptedClasses));
	}
}
