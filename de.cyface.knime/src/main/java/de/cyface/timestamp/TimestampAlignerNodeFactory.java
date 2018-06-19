package de.cyface.timestamp;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * <p>
 * <code>NodeFactory</code> for the "GpsPreAndNextAssigner" Node. Takes two
 * tables with timestamp columns and and assigns to each column from the first
 * table the column from the second table with the closest previous timestamp.
 * </p>
 *
 * @author Klemens Muthmann
 * @version 1.0.0
 * @since 1.0.0
 */
public class TimestampAlignerNodeFactory extends NodeFactory<TimestampAlignerNodeModel> {

	@Override
	public TimestampAlignerNodeModel createNodeModel() {
		return new TimestampAlignerNodeModel();
	}

	@Override
	public int getNrNodeViews() {
		return 0;
	}

	@Override
	public NodeView<TimestampAlignerNodeModel> createNodeView(final int viewIndex,
			final TimestampAlignerNodeModel nodeModel) {
		throw new IndexOutOfBoundsException("This node supports no views.");
	}

	@Override
	public boolean hasDialog() {
		return true;
	}

	@Override
	public NodeDialogPane createNodeDialogPane() {
		return new TimestampAlignerNodeDialog();
	}

}
