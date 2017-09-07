package de.cyface.smoothing;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

public class SmoothingNodeFactory extends NodeFactory<SmoothingNodeModel> {

	@Override
	public SmoothingNodeModel createNodeModel() {
		return new SmoothingNodeModel();
	}

	@Override
	protected int getNrNodeViews() {
		return 0;
	}

	@Override
	public NodeView<SmoothingNodeModel> createNodeView(int viewIndex, SmoothingNodeModel nodeModel) {
		throw new IndexOutOfBoundsException("This node supports no views.");
	}

	@Override
	protected boolean hasDialog() {
		return true;
	}

	@Override
	protected NodeDialogPane createNodeDialogPane() {
		return new SmoothingNodeDialog();
	}

}
