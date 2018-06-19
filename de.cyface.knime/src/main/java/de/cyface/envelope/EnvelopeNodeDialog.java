package de.cyface.envelope;

import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;

import de.cyface.smoothing.dialog.ColumnSelectionNodeOption;

public class EnvelopeNodeDialog extends DefaultNodeSettingsPane {

	public EnvelopeNodeDialog(final ColumnSelectionNodeOption inputColumnSelection) {
		addDialogComponent(inputColumnSelection.getComponent());
	}

}
