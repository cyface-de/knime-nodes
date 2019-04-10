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
