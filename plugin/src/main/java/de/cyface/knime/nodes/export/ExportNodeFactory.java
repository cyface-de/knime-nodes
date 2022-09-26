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
package de.cyface.knime.nodes.export;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

public class ExportNodeFactory extends NodeFactory<ExportNodeModel> {

	@Override
	public ExportNodeModel createNodeModel() {
		return new ExportNodeModel();
	}

	@Override
	protected int getNrNodeViews() {
		return 0;
	}

	@Override
	public NodeView<ExportNodeModel> createNodeView(int viewIndex, ExportNodeModel nodeModel) {
		throw new IndexOutOfBoundsException("This node supports no views");
	}

	@Override
	protected boolean hasDialog() {
		return true;
	}

	@Override
	protected NodeDialogPane createNodeDialogPane() {
		return new ExportNodeDialog();
	}

}
