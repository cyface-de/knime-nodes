package de.cyface.smoothing.dialog;

import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataValue;
import org.knime.core.node.defaultnodesettings.DialogComponentColumnNameSelection;
import org.knime.core.node.defaultnodesettings.SettingsModel;
import org.knime.core.node.util.ColumnFilter;

/**
 * A {@link NodeOption} allowing to select a column from the input table.
 * 
 * @author Klemens Muthmann
 * @version 1.0.0
 * @since 1.0.0
 */
public class ColumnSelectionNodeOption extends StringSettingsModelOption {

	/**
	 * Creates a new completely initialized {@link ColumnSelectionNodeOption}.
	 * 
	 * @param configIdentifier
	 *            The identifier used by KNIME to find the corresponding
	 *            {@link SettingsModel}.
	 * @param label
	 *            The label appearing on the UI (usually to the left of the
	 *            input field).
	 */
	public ColumnSelectionNodeOption(final String configIdentifier, final String label) {
		super(configIdentifier, "Select a column");
		setComponent(new DialogComponentColumnNameSelection(getSettingsModel(), label, 0, new ColumnFilter() {

			@Override
			public boolean includeColumn(DataColumnSpec colSpec) {
				return true;
			}

			@Override
			public String allFilteredMsg() {
				return "All columns filtered from input. IF YOU READ THIS MESSAGE, SOMETHING IS WRONG WITH THE CODE OF THIS NODE.";
			}
		}));
	}

	/**
	 * Creates a new completely initialized {@link ColumnSelectionNodeOption}.
	 * 
	 * @param configIdentifier
	 *            The identifier used by KNIME to find the corresponding
	 *            {@link SettingsModel}.
	 * @param label
	 *            The label appearing on the UI (usually to the left of the
	 *            input field).
	 * @param columnFilter
	 *            A column filter, filtering all columns not usable as input.
	 *            Only columns passing the filter are available for selection.
	 */
	public ColumnSelectionNodeOption(final String configIdentifier, final String label,
			final ColumnFilter columnFilter) {
		super(configIdentifier, "Select a column");
		setComponent(new DialogComponentColumnNameSelection(getSettingsModel(), label, 0, columnFilter));
	}

	/**
	 * Creates a new completely initialized {@link ColumnSelectionNodeOption}.
	 * 
	 * @param configIdentifier
	 *            The identifier used by KNIME to find the corresponding
	 *            {@link SettingsModel}.
	 * @param label
	 *            The label appearing on the UI (usually to the left of the
	 *            input field).
	 * @param acceptedClasses
	 *            A list of possible input data values. Only columns with input
	 *            values of one of the provided types are shown in the dialog.
	 */
	public ColumnSelectionNodeOption(final String configIdentifier, final String label,
			Class<? extends DataValue>... acceptedClasses) {
		super(configIdentifier, "Select a column");
		setComponent(new DialogComponentColumnNameSelection(getSettingsModel(), label, 0, acceptedClasses));
	}
}
