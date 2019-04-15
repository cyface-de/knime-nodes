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
package de.cyface.knime.nodes.mapmatching;

import org.knime.core.data.DataColumnSpec;
import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentColumnNameSelection;
import org.knime.core.node.defaultnodesettings.SettingsModelString;
import org.knime.core.node.util.ColumnFilter;

import de.cyface.knime.dialog.ColumnSelectionNodeOption;
import de.cyface.knime.dialog.StringSelectionNodeOption;
import de.cyface.knime.dialog.TextFieldNodeOption;

public final class MapMatchingNodeDialog extends DefaultNodeSettingsPane {

	public MapMatchingNodeDialog(final StringSelectionNodeOption mapMatchingFrameworkOption,
			final TextFieldNodeOption serverAddressInputOption, final TextFieldNodeOption serverPortInputOption,
			final TextFieldNodeOption databaseNameInputOption, final TextFieldNodeOption databaseTableInputOption,
			final TextFieldNodeOption databaseUserInputOption, final TextFieldNodeOption databasePasswordInputOption,
			final TextFieldNodeOption databaseRoadTypesInputOption,
			final ColumnSelectionNodeOption latInputColumnSelector,
			final ColumnSelectionNodeOption lonInputColumnSelector,
			final ColumnSelectionNodeOption headingInputColunSelector,
			final ColumnSelectionNodeOption idInputColumnSelector,
			final ColumnSelectionNodeOption timestampInputColumnSelector) {
		createNewGroup("Matching Server Settings");
		addDialogComponent(mapMatchingFrameworkOption.getComponent());
		addDialogComponent(serverAddressInputOption.getComponent());
		createNewGroup("Input Data Settings");
		addDialogComponent(latInputColumnSelector.getComponent());
		addDialogComponent(lonInputColumnSelector.getComponent());
		addDialogComponent(headingInputColunSelector.getComponent());
		addDialogComponent(idInputColumnSelector.getComponent());
		addDialogComponent(new DialogComponentColumnNameSelection(
				new SettingsModelString(MapMatchingNodeConstants.TIMESTAMP_INPUT_COLUMN_SELECTOR_CONFIG_IDENTIFIER, ""),
				MapMatchingNodeConstants.TIMESTAMP_INPUT_COLUMN_SELECTOR_LABEL, 0, new ColumnFilter() {

					@Override
					public boolean includeColumn(DataColumnSpec colSpec) {
						return true;
					}

					@Override
					public String allFilteredMsg() {
						return "All filtered";
					}
				}));
	}

}
