package de.cyface.mapmatching;

import org.knime.core.data.DoubleValue;
import org.knime.core.data.IntValue;
import org.knime.core.data.LongValue;
import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;

import de.cyface.smoothing.dialog.ColumnSelectionNodeOption;
import de.cyface.smoothing.dialog.StringSelectionNodeOption;
import de.cyface.smoothing.dialog.TextFieldNodeOption;

public final class MapMatchingNodeFactory extends NodeFactory<MapMatchingNodeModel> {

	private final StringSelectionNodeOption mapMatchingFrameworkOption;
	private final TextFieldNodeOption serverAddressInputOption;
	private final ColumnSelectionNodeOption latInputColumnSelector;
	private final ColumnSelectionNodeOption lonInputColumnSelector;
	private final ColumnSelectionNodeOption headingInputColumnSelector;
	private final ColumnSelectionNodeOption idInputColumnSelector;
	private final ColumnSelectionNodeOption timestampInputColumnSelector;

	private final static String MAP_MATCHING_FRAMEWORK_OPTION_CONFIG_IDENTIFIER = "de.cyface.mapmatching.id.framework";
	private final static String SERVER_ADDRESS_INPUT_OPTION_CONFIG_IDENTIFIER = "de.cyface.mapmatching.id.serveraddress";
	private final static String LAT_INPUT_COLUMN_SELECTOR_CONFIG_IDENTIFIER = "de.cyface.mapmatching.id.latinput";
	private final static String LON_INPUT_COLUMN_SELECTOR_CONFIG_IDENTIFIER = "de.cyface.mapmatching.id.loninput";
	private final static String HEADING_INPUT_COLUMN_SELECTOR_CONFIG_IDENTIFIER = "de.cyface.mapmatching.id.headinginput";
	private final static String ID_INPUT_COLUMN_SELECTOR_CONFIG_IDENTIFIER = "de.cyface.mapmatching.id.idinput";
	private final static String TIMESTAMP_INPUT_COLUMN_SELECTOR_CONFIG_IDENTIFIER = "de.cyface.mapmatching.id.timestampinput";

	private final static String MAP_MATCHING_FRAMEWORK_OPTION_LABEL = "Select Framework to use";
	private final static String SERVER_ADDRESS_INPUT_OPTION_LABEL = "Provide Matching Server Address";
	private final static String LAT_INPUT_COLUMN_SELECTOR_LABEL = "Latitude Column";
	private final static String LON_INPUT_COLUMN_SELECTOR_LABEL = "Longitude Column";
	private final static String HEADING_INPUT_COLUMN_SELECTOR_LABEL = "Heading Column (Optional)";
	private final static String ID_INPUT_COLUMN_SELECTOR_LABEL = "Identifier Column";
	private final static String TIMESTAMP_INPUT_COLUMN_SELECTOR_LABEL = "de.cyface.mapmatching.id.timestampinput";

	public MapMatchingNodeFactory() {
		mapMatchingFrameworkOption = new StringSelectionNodeOption(MAP_MATCHING_FRAMEWORK_OPTION_CONFIG_IDENTIFIER,
				MAP_MATCHING_FRAMEWORK_OPTION_LABEL, 0, "Barefoot");
		serverAddressInputOption = new TextFieldNodeOption(SERVER_ADDRESS_INPUT_OPTION_CONFIG_IDENTIFIER,
				SERVER_ADDRESS_INPUT_OPTION_LABEL, "");
		latInputColumnSelector = new ColumnSelectionNodeOption(LAT_INPUT_COLUMN_SELECTOR_CONFIG_IDENTIFIER,
				LAT_INPUT_COLUMN_SELECTOR_LABEL, "", DoubleValue.class);
		lonInputColumnSelector = new ColumnSelectionNodeOption(LON_INPUT_COLUMN_SELECTOR_CONFIG_IDENTIFIER,
				LON_INPUT_COLUMN_SELECTOR_LABEL, "", DoubleValue.class);
		headingInputColumnSelector = new ColumnSelectionNodeOption(HEADING_INPUT_COLUMN_SELECTOR_CONFIG_IDENTIFIER,
				HEADING_INPUT_COLUMN_SELECTOR_LABEL, "", DoubleValue.class);
		idInputColumnSelector = new ColumnSelectionNodeOption(ID_INPUT_COLUMN_SELECTOR_CONFIG_IDENTIFIER,
				ID_INPUT_COLUMN_SELECTOR_LABEL, "");
		timestampInputColumnSelector = new ColumnSelectionNodeOption(TIMESTAMP_INPUT_COLUMN_SELECTOR_CONFIG_IDENTIFIER,
				TIMESTAMP_INPUT_COLUMN_SELECTOR_LABEL, "", LongValue.class, IntValue.class);
	}

	@Override
	public MapMatchingNodeModel createNodeModel() {
		return new MapMatchingNodeModel(mapMatchingFrameworkOption.getSettingsModel(),
				serverAddressInputOption.getSettingsModel(), latInputColumnSelector.getSettingsModel(),
				lonInputColumnSelector.getSettingsModel(), headingInputColumnSelector.getSettingsModel(),
				idInputColumnSelector.getSettingsModel(), timestampInputColumnSelector.getSettingsModel());
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
		return new MapMatchingNodeDialog(mapMatchingFrameworkOption, serverAddressInputOption, latInputColumnSelector,
				lonInputColumnSelector, headingInputColumnSelector, idInputColumnSelector, timestampInputColumnSelector);
	}

}
