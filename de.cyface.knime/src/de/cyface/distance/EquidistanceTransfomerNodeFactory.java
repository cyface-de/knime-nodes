package de.cyface.distance;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * <code>NodeFactory</code> for the "EquidistanceTransfomer" Node. Takes a
 * function f(x) defined by two columns containing the values for x and for f(x)
 * and calculate a new function on x where f(x) are equdistant points which are
 * interpolated between original values of f(x) if f(x) is not equdistant
 * itself. The columns containing x and f(x) need to be numeric and sorted by x.
 *
 * @author Klemens Muthmann
 * @version 1.0.0
 * @since 1.0.0
 */
public class EquidistanceTransfomerNodeFactory extends NodeFactory<EquidistanceTransfomerNodeModel> {

	@Override
	public EquidistanceTransfomerNodeModel createNodeModel() {
		return new EquidistanceTransfomerNodeModel();
	}

	@Override
	public int getNrNodeViews() {
		return 0;
	}

	@Override
	public NodeView<EquidistanceTransfomerNodeModel> createNodeView(final int viewIndex,
			final EquidistanceTransfomerNodeModel nodeModel) {
		throw new IndexOutOfBoundsException("This node supports no views.");
	}

	@Override
	public boolean hasDialog() {
		return true;
	}

	@Override
	public NodeDialogPane createNodeDialogPane() {
		return new EquidistanceTransfomerNodeDialog();
	}

}
