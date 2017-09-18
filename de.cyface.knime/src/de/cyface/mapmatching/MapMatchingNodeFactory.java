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
	private final ColumnSelectionNodeOption latInputColumnSelector;
	private final ColumnSelectionNodeOption lonInputColumnSelector;
	private final ColumnSelectionNodeOption headingInputColumnSelector;
	private final ColumnSelectionNodeOption idInputColumnSelector;
	private final ColumnSelectionNodeOption timestampInputColumnSelector;

	public MapMatchingNodeFactory() {
		mapMatchingFrameworkOption = new StringSelectionNodeOption(MapMatchingNodeConstants.MAP_MATCHING_FRAMEWORK_OPTION_CONFIG_IDENTIFIER,
				MapMatchingNodeConstants.MAP_MATCHING_FRAMEWORK_OPTION_LABEL, 0, "Barefoot");
		serverAddressInputOption = new TextFieldNodeOption(MapMatchingNodeConstants.SERVER_ADDRESS_INPUT_OPTION_CONFIG_IDENTIFIER,
				MapMatchingNodeConstants.SERVER_ADDRESS_INPUT_OPTION_LABEL, "");
		latInputColumnSelector = new ColumnSelectionNodeOption(MapMatchingNodeConstants.LAT_INPUT_COLUMN_SELECTOR_CONFIG_IDENTIFIER,
				MapMatchingNodeConstants.LAT_INPUT_COLUMN_SELECTOR_LABEL, DoubleValue.class);
		lonInputColumnSelector = new ColumnSelectionNodeOption(MapMatchingNodeConstants.LON_INPUT_COLUMN_SELECTOR_CONFIG_IDENTIFIER,
				MapMatchingNodeConstants.LON_INPUT_COLUMN_SELECTOR_LABEL, DoubleValue.class);
		headingInputColumnSelector = new ColumnSelectionNodeOption(MapMatchingNodeConstants.HEADING_INPUT_COLUMN_SELECTOR_CONFIG_IDENTIFIER,
				MapMatchingNodeConstants.HEADING_INPUT_COLUMN_SELECTOR_LABEL, DoubleValue.class);
		idInputColumnSelector = new ColumnSelectionNodeOption(MapMatchingNodeConstants.ID_INPUT_COLUMN_SELECTOR_CONFIG_IDENTIFIER,
				MapMatchingNodeConstants.ID_INPUT_COLUMN_SELECTOR_LABEL);
		timestampInputColumnSelector = new ColumnSelectionNodeOption(MapMatchingNodeConstants.TIMESTAMP_INPUT_COLUMN_SELECTOR_CONFIG_IDENTIFIER,
				MapMatchingNodeConstants.TIMESTAMP_INPUT_COLUMN_SELECTOR_LABEL, LongValue.class, IntValue.class);
	}

	@Override
	public MapMatchingNodeModel createNodeModel() {
		return new MapMatchingNodeModel(mapMatchingFrameworkOption.getSettingsModel(),
				serverAddressInputOption.getSettingsModel(), latInputColumnSelector.getSettingsModel(),
				lonInputColumnSelector.getSettingsModel(), headingInputColumnSelector.getSettingsModel(),
				idInputColumnSelector.getSettingsModel(),
				new SettingsModelString(MapMatchingNodeConstants.TIMESTAMP_INPUT_COLUMN_SELECTOR_CONFIG_IDENTIFIER, ""));
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
				lonInputColumnSelector, headingInputColumnSelector, idInputColumnSelector,
				timestampInputColumnSelector);
	}

}
