/*
 * Copyright 2018 Cyface GmbH
 * 
 * This file is part of the Cyface Nodes.
 *
 * The Cyface Nodes is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * The Cyface Nodes is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Cyface Nodes. If not, see <http://www.gnu.org/licenses/>.
 */
package de.cyface.knime.nodes.smoothing;

import java.util.Arrays;

import org.knime.core.data.DoubleValue;
import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;
import org.knime.core.node.defaultnodesettings.SettingsModelString;

import de.cyface.knime.dialog.BoundedNumberInputNodeOption;
import de.cyface.knime.dialog.ButtonGroupNodeOption;
import de.cyface.knime.dialog.ColumnSelectionNodeOption;
import de.cyface.knime.dialog.NodeOption;
import de.cyface.knime.dialog.StringSelectionNodeOption;
import de.cyface.knime.dialog.TextFieldNodeOption;

/**
 * The factory for all components required to realize a smoothing node. See the
 * KNIME documentation for further details.
 * 
 * @author Klemens Muthmann
 * @version 1.1.0
 * @since 1.0.0
 */
public class SmoothingNodeFactory extends NodeFactory<SmoothingNodeModel> {

	/**
	 * A {@link NodeOption} for selecting which filter to use for smoothing.
	 * 
	 * @see Filter
	 */
	StringSelectionNodeOption filterTypeSelection = new StringSelectionNodeOption(
			SmoothingNodeConstants.FILTER_TYPE_SELECTION_SETTINGS_MODEL_CONFIG_NAME,
			SmoothingNodeConstants.FILTER_TYPE_SELECTION_LABEL, 0, Arrays.stream(Filter.values()).map(v -> v.getName()).toArray(String[]::new));
	/**
	 * A {@link NodeOption} for selecting the input column containing the signal
	 * to smooth.
	 */
	ColumnSelectionNodeOption inputColSelection = new ColumnSelectionNodeOption(
			SmoothingNodeConstants.INPUT_COL_SELECTION_SETTINGS_MODEL_CONFIG_NAME,
			SmoothingNodeConstants.INPUT_COL_SELECTION_LABEL, DoubleValue.class);
	/**
	 * A {@link NodeOption} for choosing to either append or replace the output
	 * to/with the input.
	 */
	ButtonGroupNodeOption appendReplaceChooser = new ButtonGroupNodeOption(
			SmoothingNodeConstants.APPEND_REPLACE_CHOOSER_SETTINGS_MODEL_CONFIG_NAME,
			SmoothingNodeConstants.APPEND_REPLACE_CHOOSER_LABEL, 1, SmoothingNodeConstants.REPLACE_OPTION,
			SmoothingNodeConstants.APPEND_OPTION);
	/**
	 * A {@link NodeOption} allowing the user to set a name for the column
	 * containing the result if the append option has been choosen.
	 */
	TextFieldNodeOption appendColumnNameInput = new TextFieldNodeOption(
			SmoothingNodeConstants.APPEND_COLUMN_NAME_INPUT_SETTINGS_MODEL_CONFIG_NAME,
			SmoothingNodeConstants.APPEND_COLUMN_NAME_INPUT_LABEL, "");

	BoundedNumberInputNodeOption windowSizeSelector = new BoundedNumberInputNodeOption(
			SmoothingNodeConstants.WINDOW_SIZE_SELECTOR_SETTINGS_MODEL_CONFIG_NAME,
			SmoothingNodeConstants.WINDOW_SIZE_SELECTOR_LABEL);

	@Override
	public SmoothingNodeModel createNodeModel() {
		final SettingsModelString appendReplaceChooserModel = appendReplaceChooser.getSettingsModel();
		final SettingsModelString appendColumnNameInputSettingsModel = appendColumnNameInput.getSettingsModel();
		final SmoothingNodeModel ret = new SmoothingNodeModel(
				new AppendColumnExecutor(appendColumnNameInputSettingsModel), filterTypeSelection.getSettingsModel(),
				inputColSelection.getSettingsModel(), appendReplaceChooser.getSettingsModel(),
				appendColumnNameInputSettingsModel, windowSizeSelector.getSettingsModel());

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
				appendColumnNameInput, windowSizeSelector);
	}

}
