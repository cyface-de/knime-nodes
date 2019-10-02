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
package de.cyface.knime.nodes.reader;

import javax.swing.JFileChooser;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;
import org.knime.core.node.defaultnodesettings.DialogComponentFileChooser;
import org.knime.core.node.defaultnodesettings.SettingsModelString;

import de.cyface.knime.dialog.FileSelectionNodeOption;
import de.cyface.knime.dialog.StringSelectionNodeOption;
import de.cyface.knime.dialog.FileSelectionNodeOption.DialogType;
import de.cyface.knime.dialog.FileSelectionNodeOption.SelectionType;

/**
 * The factory for the Cyface binary format reader node. Creates the node's model and dialog.
 * 
 * @author Klemens Muthmann
 * @author Armin Schnabel
 * @version 1.1.0
 * @since 2.2.0
 */
public class BinaryFormatReaderNodeFactory extends NodeFactory<BinaryFormatReaderNodeModel> {
    
    /**
     * The option where the user selects an input file to read data from.
     */
    private final FileSelectionNodeOption inputFileSelectionOption = new FileSelectionNodeOption("inputfile", "de.cyface.knime.reader.inputfile", DialogType.OPEN_DIALOG, SelectionType.FILES);
    /**
     * The option where the user selects the type of the input file to read from.
     */
    private final StringSelectionNodeOption fileTypeSelectionOption = new StringSelectionNodeOption("file-type", "Please select the type of the file you want to read!", 0, "Measurement", "Accelerations", "Rotations", "Directions", "Events");

	@Override
	public BinaryFormatReaderNodeModel createNodeModel() {
		return new BinaryFormatReaderNodeModel(inputFileSelectionOption, fileTypeSelectionOption);
	}

	@Override
	protected int getNrNodeViews() {
		return 0;
	}

	@Override
	public NodeView<BinaryFormatReaderNodeModel> createNodeView(int viewIndex, BinaryFormatReaderNodeModel nodeModel) {
		throw new UnsupportedOperationException();
	}

	@Override
	protected boolean hasDialog() {
		return true;
	}

	@Override
	protected NodeDialogPane createNodeDialogPane() {
		return new BinaryFormatReaderNodeDialog(inputFileSelectionOption, fileTypeSelectionOption);
	}

}
