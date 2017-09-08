package de.cyface.smoothing;

import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.SettingsModelString;

import de.cyface.smoothing.dialog.ButtonGroupNodeOption;
import de.cyface.smoothing.dialog.ColumnSelectionNodeOption;
import de.cyface.smoothing.dialog.StringSelectionNodeOption;
import de.cyface.smoothing.dialog.TextFieldNodeOption;

/**
 * @author Klemens Muthmann
 * @version 1.0.0
 * @since 1.0.0
 */
public class SmoothingNodeDialog extends DefaultNodeSettingsPane {
	
	protected SmoothingNodeDialog(final StringSelectionNodeOption filterTypeSelection, final ColumnSelectionNodeOption inputColSelection, final ButtonGroupNodeOption appendReplaceChooser, final TextFieldNodeOption appendColumnNameInput) {
		super();
				
		addDialogComponent(filterTypeSelection.getComponent());
		addDialogComponent(inputColSelection.getComponent());
		addDialogComponent(appendReplaceChooser.getComponent());
		addDialogComponent(appendColumnNameInput.getComponent());
	}
	

}
