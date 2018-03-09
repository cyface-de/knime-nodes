package de.cyface.export;

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
