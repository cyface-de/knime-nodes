package de.cyface.mapmatching;

import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;

import de.cyface.smoothing.dialog.ColumnSelectionNodeOption;
import de.cyface.smoothing.dialog.StringSelectionNodeOption;
import de.cyface.smoothing.dialog.TextFieldNodeOption;

public final class MapMatchingNodeDialog extends DefaultNodeSettingsPane {

	public MapMatchingNodeDialog(final StringSelectionNodeOption mapMatchingFrameworkOption, final TextFieldNodeOption serverAddressInputOption, final ColumnSelectionNodeOption latInputColumnSelector, final ColumnSelectionNodeOption lonInputColumnSelector, final ColumnSelectionNodeOption headingInputColunSelector, final ColumnSelectionNodeOption idInputColumnSelector, final ColumnSelectionNodeOption timestampInputColumnSelector) {
		createNewGroup("Matching Server Settings");
		addDialogComponent(mapMatchingFrameworkOption.getComponent());
		addDialogComponent(serverAddressInputOption.getComponent());
		createNewGroup("Input Data Settings");
		addDialogComponent(latInputColumnSelector.getComponent());
		addDialogComponent(lonInputColumnSelector.getComponent());
		addDialogComponent(headingInputColunSelector.getComponent());
		addDialogComponent(idInputColumnSelector.getComponent());
		addDialogComponent(timestampInputColumnSelector.getComponent());
	}

}
