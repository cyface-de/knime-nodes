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

import org.knime.core.data.DoubleValue;
import org.knime.core.data.IntValue;
import org.knime.core.data.LongValue;
import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

import de.cyface.knime.dialog.ColumnSelectionNodeOption;
import de.cyface.knime.dialog.NodeOption;

/**
 * Factory class for the <code>EnvelopeNodeModel</code> and the <code>EnvelopeNodeDialog</code>.
 * 
 * @author Klemens Muthmann
 * @version 1.0.0
 * @since 1.0.0
 */
public final class EnvelopeNodeFactory extends NodeFactory<EnvelopeNodeModel> {

    /**
     * A {@link NodeOption} for the selection of input columns.
     */
    private final ColumnSelectionNodeOption inputColumnSelection;

    /**
     * Creates a new completely initialized <code>EnvelopeNodeFactory</code> instance.
     */
    public EnvelopeNodeFactory() {
        this.inputColumnSelection = new ColumnSelectionNodeOption(
                EnvelopeNodeConstants.INPUT_COLUMN_SELECTION_IDENTIFIER,
                EnvelopeNodeConstants.INPUT_COLUMN_SELECTION_LABEL, DoubleValue.class, IntValue.class, LongValue.class);
    }

    @Override
    public EnvelopeNodeModel createNodeModel() {
        return new EnvelopeNodeModel(inputColumnSelection);
    }

    @Override
    protected int getNrNodeViews() {
        return 0;
    }

    @Override
    public NodeView<EnvelopeNodeModel> createNodeView(int viewIndex, EnvelopeNodeModel nodeModel) {
        return null;
    }

    @Override
    protected boolean hasDialog() {
        return true;
    }

    @Override
    protected NodeDialogPane createNodeDialogPane() {
        return new EnvelopeNodeDialog(inputColumnSelection);
    }

}
