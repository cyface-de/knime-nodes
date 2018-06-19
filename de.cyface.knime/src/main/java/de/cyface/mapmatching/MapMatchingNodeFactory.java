package de.cyface.mapmatching;

import org.knime.core.data.DoubleValue;
import org.knime.core.data.IntValue;
import org.knime.core.data.LongValue;
import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.defaultnodesettings.SettingsModelString;

import de.cyface.smoothing.dialog.ColumnSelectionNodeOption;
import de.cyface.smoothing.dialog.StringSelectionNodeOption;
import de.cyface.smoothing.dialog.TextFieldNodeOption;

public final class MapMatchingNodeFactory extends NodeFactory<MapMatchingNodeModel> {

	private final StringSelectionNodeOption mapMatchingFrameworkOption;
	private final TextFieldNodeOption serverAddressInputOption;
	private final TextFieldNodeOption serverPortInputOption;
	private final TextFieldNodeOption databaseNameInputOption;
	private final TextFieldNodeOption databaseTableInputOption;
	private final TextFieldNodeOption databaseUserInputOption;
	private final TextFieldNodeOption databasePasswordInputOption;
	private final TextFieldNodeOption databaseRoadTypesInputOption;
	private final ColumnSelectionNodeOption latInputColumnSelector;
	private final ColumnSelectionNodeOption lonInputColumnSelector;
	private final ColumnSelectionNodeOption headingInputColumnSelector;
	private final ColumnSelectionNodeOption idInputColumnSelector;
	private final ColumnSelectionNodeOption timestampInputColumnSelector;

	public MapMatchingNodeFactory() {
		String roadTypes = this.getClass().getResource("/road-types.json").getFile();
		
		mapMatchingFrameworkOption = new StringSelectionNodeOption(
				MapMatchingNodeConstants.MAP_MATCHING_FRAMEWORK_OPTION_CONFIG_IDENTIFIER,
				MapMatchingNodeConstants.MAP_MATCHING_FRAMEWORK_OPTION_LABEL, 0, "Barefoot");
		serverAddressInputOption = new TextFieldNodeOption(
				MapMatchingNodeConstants.SERVER_ADDRESS_INPUT_OPTION_CONFIG_IDENTIFIER,
				MapMatchingNodeConstants.SERVER_ADDRESS_INPUT_OPTION_LABEL, "localhost");
		serverPortInputOption = new TextFieldNodeOption(
				MapMatchingNodeConstants.SERVER_PORT_INPUT_OPTION_CONFIG_IDENTIFIER,
				MapMatchingNodeConstants.SERVER_PORT_INPUT_OPTION_LABEL, "5432");
		databaseNameInputOption = new TextFieldNodeOption(
				MapMatchingNodeConstants.DATABASE_NAME_INPUT_OPTION_CONFIG_IDENTIFIER,
				MapMatchingNodeConstants.DATABASE_NAME_INPUT_OPTION_LABEL, "sachsen");
		databaseTableInputOption = new TextFieldNodeOption(
				MapMatchingNodeConstants.DATABASE_TABLE_INPUT_OPTION_CONFIG_IDENTIFIER,
				MapMatchingNodeConstants.DATABASE_TABLE_INPUT_OPTION_LABEL, "bfmap_ways");
		databaseUserInputOption = new TextFieldNodeOption(
				MapMatchingNodeConstants.DATABASE_USER_INPUT_OPTION_CONFIG_IDENTIFIER,
				MapMatchingNodeConstants.DATABASE_USER_INPUT_OPTION_LABEL, "osmuser");
		databasePasswordInputOption = new TextFieldNodeOption(
				MapMatchingNodeConstants.DATABASE_PASSWORD_INPUT_OPTION_CONFIG_IDENTIFIER,
				MapMatchingNodeConstants.DATABASE_PASSWORD_INPUT_OPTION_LABEL, "pass");
		databaseRoadTypesInputOption = new TextFieldNodeOption(
				MapMatchingNodeConstants.DATABASE_ROAD_TYPES_INPUT_OPTION_CONFIG_IDENTIFIER,
				MapMatchingNodeConstants.DATABASE_ROAD_TYPES_INPUT_OPTION_LABEL, roadTypes);
		latInputColumnSelector = new ColumnSelectionNodeOption(
				MapMatchingNodeConstants.LAT_INPUT_COLUMN_SELECTOR_CONFIG_IDENTIFIER,
				MapMatchingNodeConstants.LAT_INPUT_COLUMN_SELECTOR_LABEL, DoubleValue.class);
		lonInputColumnSelector = new ColumnSelectionNodeOption(
				MapMatchingNodeConstants.LON_INPUT_COLUMN_SELECTOR_CONFIG_IDENTIFIER,
				MapMatchingNodeConstants.LON_INPUT_COLUMN_SELECTOR_LABEL, DoubleValue.class);
		headingInputColumnSelector = new ColumnSelectionNodeOption(
				MapMatchingNodeConstants.HEADING_INPUT_COLUMN_SELECTOR_CONFIG_IDENTIFIER,
				MapMatchingNodeConstants.HEADING_INPUT_COLUMN_SELECTOR_LABEL, DoubleValue.class);
		idInputColumnSelector = new ColumnSelectionNodeOption(
				MapMatchingNodeConstants.ID_INPUT_COLUMN_SELECTOR_CONFIG_IDENTIFIER,
				MapMatchingNodeConstants.ID_INPUT_COLUMN_SELECTOR_LABEL);
		timestampInputColumnSelector = new ColumnSelectionNodeOption(
				MapMatchingNodeConstants.TIMESTAMP_INPUT_COLUMN_SELECTOR_CONFIG_IDENTIFIER,
				MapMatchingNodeConstants.TIMESTAMP_INPUT_COLUMN_SELECTOR_LABEL, LongValue.class, IntValue.class);
	}

	@Override
	public MapMatchingNodeModel createNodeModel() {
		return new MapMatchingNodeModel(mapMatchingFrameworkOption.getSettingsModel(),
				serverAddressInputOption.getSettingsModel(), serverPortInputOption.getSettingsModel(),
				databaseNameInputOption.getSettingsModel(), databaseTableInputOption.getSettingsModel(),
				databaseUserInputOption.getSettingsModel(), databasePasswordInputOption.getSettingsModel(),
				databaseRoadTypesInputOption.getSettingsModel(), latInputColumnSelector.getSettingsModel(),
				lonInputColumnSelector.getSettingsModel(), headingInputColumnSelector.getSettingsModel(),
				idInputColumnSelector.getSettingsModel(), new SettingsModelString(
						MapMatchingNodeConstants.TIMESTAMP_INPUT_COLUMN_SELECTOR_CONFIG_IDENTIFIER, ""));
	}

	@Override
	protected int getNrNodeViews() {
		return 0;
	}

	@Override
	public MapMatchingNodeView createNodeView(final int viewIndex, final MapMatchingNodeModel nodeModel) {
		return null;
	}

	@Override
	protected boolean hasDialog() {
		return true;
	}

	@Override
	protected NodeDialogPane createNodeDialogPane() {
		return new MapMatchingNodeDialog(mapMatchingFrameworkOption, serverAddressInputOption, serverPortInputOption,
				databaseNameInputOption, databaseTableInputOption, databaseUserInputOption, databasePasswordInputOption,
				databaseRoadTypesInputOption, latInputColumnSelector, lonInputColumnSelector,
				headingInputColumnSelector, idInputColumnSelector, timestampInputColumnSelector);
	}

}
