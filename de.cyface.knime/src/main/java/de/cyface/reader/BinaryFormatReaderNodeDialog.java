/*
 * Copyright 2019 Cyface GmbH
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
package de.cyface.reader;

import javax.swing.JFileChooser;

import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentFileChooser;
import org.knime.core.node.defaultnodesettings.SettingsModelString;

/**
 * Configures the dialog shown to the user, when configuring the node.
 * This dialog is for example reached by selecting configure from the nodes context menu.
 * The user may configure the location of the input file from the dialog.
 * 
 * @author Klemens Muthmann
 * @version 1.0.0
 * @since 2.2.0
 */
public final class BinaryFormatReaderNodeDialog extends DefaultNodeSettingsPane {
	/**
	 * Creates a new completely initialized configuration dialog object.
	 */
	public BinaryFormatReaderNodeDialog() {
		final SettingsModelString stringModel = new SettingsModelString("inputfile", "");
		final DialogComponentFileChooser fileChooserDialogComponent = new DialogComponentFileChooser(stringModel,
				"de.cyface.knime.reader.inputfile", JFileChooser.OPEN_DIALOG, false);
		addDialogComponent(fileChooserDialogComponent);
	}
}
