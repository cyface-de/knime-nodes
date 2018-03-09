package de.cyface.mapmatching;

import org.knime.core.data.DataColumnSpec;
import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentColumnNameSelection;
import org.knime.core.node.defaultnodesettings.SettingsModelString;
import org.knime.core.node.util.ColumnFilter;

import de.cyface.smoothing.dialog.ColumnSelectionNodeOption;
import de.cyface.smoothing.dialog.StringSelectionNodeOption;
import de.cyface.smoothing.dialog.TextFieldNodeOption;

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
