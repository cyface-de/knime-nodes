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

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * The factory for the Cyface binary format reader node. Creates the node's model and dialog.
 * 
 * @author muthmann
 * @version 1.0.0
 * @since 2.2.0
 */
public class BinaryFormatReaderNodeFactory extends NodeFactory<BinaryFormatReaderNodeModel> {

	@Override
	public BinaryFormatReaderNodeModel createNodeModel() {
		return new BinaryFormatReaderNodeModel();
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
		return new BinaryFormatReaderNodeDialog();
	}

}
