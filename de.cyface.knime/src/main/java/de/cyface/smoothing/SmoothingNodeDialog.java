package de.cyface.smoothing;

import org.knime.core.node.NodeDialog;
import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;

import de.cyface.smoothing.dialog.ButtonGroupNodeOption;
import de.cyface.smoothing.dialog.ColumnSelectionNodeOption;
import de.cyface.smoothing.dialog.NodeOption;
import de.cyface.smoothing.dialog.NumberInputNodeOption;
import de.cyface.smoothing.dialog.StringSelectionNodeOption;
import de.cyface.smoothing.dialog.TextFieldNodeOption;

/**
 * <p>
 * The {@link NodeDialog} shown by the smoothing node, to configure its
 * settings.
 * </p>
 * 
 * @author Klemens Muthmann
 * @version 1.0.0
 * @since 1.0.0
 */
public class SmoothingNodeDialog extends DefaultNodeSettingsPane {

	/**
	 * <p>
	 * Creates a new completely initialized {@link SmoothingNodeDialog}.
	 * </p>
	 * 
	 * @param filterTypeSelection
	 *            A {@link NodeOption} for selecting which filter to use for
	 *            smoothing.
	 * @param inputColSelection
	 *            A {@link NodeOption} for selecting the input column containing
	 *            the signal to smooth.
	 * @param appendReplaceChooser
	 *            A {@link NodeOption} for choosing to either append or replace
	 *            the output to/with the input.
	 * @param appendColumnNameInput
	 *            A {@link NodeOption} allowing the user to set a name for the
	 *            column containing the result if the append option has been
	 *            choosen.
	 *            @param windowSizeSelector A {@link NodeOption} use to select the size of the smoothing window.
	 */
	protected SmoothingNodeDialog(final StringSelectionNodeOption filterTypeSelection,
			final ColumnSelectionNodeOption inputColSelection, final ButtonGroupNodeOption appendReplaceChooser,
			final TextFieldNodeOption appendColumnNameInput, final NumberInputNodeOption windowSizeSelector) {
		super();

		addDialogComponent(filterTypeSelection.getComponent());
		addDialogComponent(inputColSelection.getComponent());
		addDialogComponent(appendReplaceChooser.getComponent());
		addDialogComponent(appendColumnNameInput.getComponent());
		addDialogComponent(windowSizeSelector.getComponent());
	}

}
