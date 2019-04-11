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
package de.cyface.knime.nodes.envelope;

import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;

import de.cyface.knime.dialog.ColumnSelectionNodeOption;

/**
 * Responsible for displaying the configuration dialog shown for example when
 * clicking configure in the nodes context menu.
 * 
 * @author Klemens Muthmann
 * @version 1.0.0
 * @since 1.0.0
 */
public final class EnvelopeNodeDialog extends DefaultNodeSettingsPane {

	/**
	 * Creates the dialog displayed when configuring an envelope function node.
	 * 
	 * @param inputColumnSelection An option to select the input column from all
	 *                             available and compatible columns from the input
	 *                             table.
	 */
	public EnvelopeNodeDialog(final ColumnSelectionNodeOption inputColumnSelection) {
		addDialogComponent(inputColumnSelection.getComponent());
	}

}
