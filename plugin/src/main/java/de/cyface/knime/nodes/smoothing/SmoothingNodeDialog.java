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

import org.knime.core.node.NodeDialog;
import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;

import de.cyface.knime.dialog.BoundedNumberInputNodeOption;
import de.cyface.knime.dialog.ButtonGroupNodeOption;
import de.cyface.knime.dialog.ColumnSelectionNodeOption;
import de.cyface.knime.dialog.NodeOption;
import de.cyface.knime.dialog.StringSelectionNodeOption;
import de.cyface.knime.dialog.TextFieldNodeOption;

/**
 * The {@link NodeDialog} shown by the smoothing node, to configure its
 * settings.
 *
 * @author Klemens Muthmann
 * @version 1.0.0
 * @since 1.0.0
 */
public class SmoothingNodeDialog extends DefaultNodeSettingsPane {

	/**
	 * Creates a new completely initialized {@link SmoothingNodeDialog}.
	 *
	 * @param filterTypeSelection
	 *            A {@link NodeOption} for selecting which filter to use for
	 *            smoothing.
	 * @param inputColSelection
	 *            A {@link NodeOption} for selecting the input column containing
	 *            the signal to smooth.
	 * @param appendReplaceChooser
	 *            A {@link NodeOption} for choosing to either append or replace
	 *            the output to/with the input.
	 * @param appendColumnNameInput
	 *            A {@link NodeOption} allowing the user to set a name for the
	 *            column containing the result if the append option has been
	 *            choosen.
	 * @param windowSizeSelector A {@link NodeOption} use to select the size of the smoothing window.
	 */
	protected SmoothingNodeDialog(final StringSelectionNodeOption filterTypeSelection,
			final ColumnSelectionNodeOption inputColSelection, final ButtonGroupNodeOption appendReplaceChooser,
			final TextFieldNodeOption appendColumnNameInput, final BoundedNumberInputNodeOption windowSizeSelector) {
		super();

		createNewGroup("Output Configuration");
		addDialogComponent(inputColSelection.getComponent());
		addDialogComponent(appendReplaceChooser.getComponent());
		addDialogComponent(appendColumnNameInput.getComponent());
		createNewGroup("Algorithm Configuration");
		addDialogComponent(filterTypeSelection.getComponent());
		addDialogComponent(windowSizeSelector.getComponent());
	}

}
