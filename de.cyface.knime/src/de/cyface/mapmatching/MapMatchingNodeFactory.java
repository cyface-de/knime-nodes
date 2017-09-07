package de.cyface.mapmatching;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;

public final class MapMatchingNodeFactory extends NodeFactory<MapMatchingNodeModel> {

	@Override
	public MapMatchingNodeModel createNodeModel() {
		return new MapMatchingNodeModel();
	}

	@Override
	protected int getNrNodeViews() {
		return 0;
	}

	@Override
	public MapMatchingNodeView createNodeView(final int viewIndex, final MapMatchingNodeModel nodeModel) {
		return null;
	}

	@Override
	protected boolean hasDialog() {
		return true;
	}

	@Override
	protected NodeDialogPane createNodeDialogPane() {
		return new MapMatchingNodeDialog();
	}

}
