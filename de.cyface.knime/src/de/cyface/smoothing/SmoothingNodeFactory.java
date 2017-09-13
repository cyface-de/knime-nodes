package de.cyface.smoothing;

import org.knime.core.data.DoubleValue;
import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;
import org.knime.core.node.defaultnodesettings.SettingsModelString;

import de.cyface.smoothing.dialog.ButtonGroupNodeOption;
import de.cyface.smoothing.dialog.ColumnSelectionNodeOption;
import de.cyface.smoothing.dialog.NodeOption;
import de.cyface.smoothing.dialog.StringSelectionNodeOption;
import de.cyface.smoothing.dialog.TextFieldNodeOption;

/**
 * <p>
 * The factory for all components required to realize a smoothing node. See the
 * KNIME documentation for further details.
 * </p>
 * 
 * @author Klemens Muthmann
 * @version 1.0.0
 * @since 1.0.0
 */
public class SmoothingNodeFactory extends NodeFactory<SmoothingNodeModel> {

	/**
	 * <p>
	 * A {@link NodeOption} for selecting which filter to use for smoothing.
	 * </p>
	 * 
	 * @see Filter
	 */
	StringSelectionNodeOption filterTypeSelection = new StringSelectionNodeOption(
			SmoothingNodeConstants.FILTER_TYPE_SELECTION_SETTINGS_MODEL_CONFIG_NAME,
			SmoothingNodeConstants.FILTER_TYPE_SELECTION_LABEL, 0, Filter.RECTANGULAR.getName());
	/**
	 * <p>
	 * A {@link NodeOption} for selecting the input column containing the signal
	 * to smooth.
	 * </p>
	 */
	ColumnSelectionNodeOption inputColSelection = new ColumnSelectionNodeOption(
			SmoothingNodeConstants.INPUT_COL_SELECTION_SETTINGS_MODEL_CONFIG_NAME,
			SmoothingNodeConstants.INPUT_COL_SELECTION_LABEL, "", DoubleValue.class);
	/**
	 * <p>
	 * A {@link NodeOption} for choosing to either append or replace the output
	 * to/with the input.
	 * </p>
	 */
	ButtonGroupNodeOption appendReplaceChooser = new ButtonGroupNodeOption(
			SmoothingNodeConstants.APPEND_REPLACE_CHOOSER_SETTINGS_MODEL_CONFIG_NAME,
			SmoothingNodeConstants.APPEND_REPLACE_CHOOSER_LABEL, 1, SmoothingNodeConstants.REPLACE_OPTION,
			SmoothingNodeConstants.APPEND_OPTION);
	/**
	 * <p>
	 * A {@link NodeOption} allowing the user to set a name for the column
	 * containing the result if the append option has been choosen.
	 * </p>
	 */
	TextFieldNodeOption appendColumnNameInput = new TextFieldNodeOption(
			SmoothingNodeConstants.APPEND_COLUMN_NAME_INPUT_SETTINGS_MODEL_CONFIG_NAME,
			SmoothingNodeConstants.APPEND_COLUMN_NAME_INPUT_LABEL, "");

	@Override
	public SmoothingNodeModel createNodeModel() {
		SettingsModelString appendReplaceChooserModel = appendReplaceChooser.getSettingsModel();
		final SettingsModelString appendColumnNameInputSettingsModel = appendColumnNameInput.getSettingsModel();
		final SmoothingNodeModel ret = new SmoothingNodeModel(
				new AppendColumnExecutor(appendColumnNameInputSettingsModel), filterTypeSelection.getSettingsModel(),
				inputColSelection.getSettingsModel(), appendReplaceChooser.getSettingsModel(),
				appendColumnNameInputSettingsModel);

		appendReplaceChooserModel.addChangeListener(event -> {
			if (appendReplaceChooserModel.getStringValue().equals(SmoothingNodeConstants.REPLACE_OPTION)) {
				appendColumnNameInputSettingsModel.setEnabled(false);
				ret.setExecutor(new ReplaceColumnExecutor());
			} else {
				appendColumnNameInputSettingsModel.setEnabled(true);
				ret.setExecutor(new AppendColumnExecutor(appendColumnNameInputSettingsModel));
			}
		});

		return ret;
	}

	@Override
	protected int getNrNodeViews() {
		return 0;
	}

	@Override
	public NodeView<SmoothingNodeModel> createNodeView(int viewIndex, SmoothingNodeModel nodeModel) {
		throw new IndexOutOfBoundsException("This node supports no views.");
	}

	@Override
	protected boolean hasDialog() {
		return true;
	}

	@Override
	protected NodeDialogPane createNodeDialogPane() {
		return new SmoothingNodeDialog(filterTypeSelection, inputColSelection, appendReplaceChooser,
				appendColumnNameInput);
	}

}
