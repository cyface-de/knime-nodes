package de.cyface.envelope;

import org.knime.core.data.DoubleValue;
import org.knime.core.data.IntValue;
import org.knime.core.data.LongValue;
import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

import de.cyface.smoothing.dialog.ColumnSelectionNodeOption;

public class EnvelopeNodeFactory extends NodeFactory<EnvelopeNodeModel> {
	
	private final ColumnSelectionNodeOption inputColumnSelection;

	public EnvelopeNodeFactory() {
		this.inputColumnSelection = new ColumnSelectionNodeOption(EnvelopeNodeConstants.INPUT_COLUMN_SELECTION_IDENTIFIER, EnvelopeNodeConstants.INPUT_COLUMN_SELECTION_LABEL, DoubleValue.class, IntValue.class, LongValue.class);
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
