package de.cyface.smoothing.dialog;

import org.knime.core.node.defaultnodesettings.DialogComponentStringSelection;
import org.knime.core.node.defaultnodesettings.SettingsModel;

/**
 * <p>
 * A {@link NodeOption} for selecting a single {@link String} from a list of
 * {@link String}s. This is usually visalized as a drop down menu.
 * </p>
 * 
 * @author Klemens Muthmann
 * @version 1.0.0
 * @since 1.0.0
 */
public final class StringSelectionNodeOption extends StringSettingsModelOption {

	/**
	 * <p>
	 * Creates a new completely initialized {@link StringSelectionNodeOption}.
	 * </p>
	 * 
	 * @param configIdentifier
	 *            The identifier used by KNIME to find the corresponding
	 *            {@link SettingsModel}.
	 * @param label
	 *            The label appearing on the UI (usually to the left of the
	 *            input field).
	 * @param defaultValueIndex
	 *            An index into the array of <code>options</code> to the
	 *            position with the initial starting value.
	 * @param options
	 *            All possible values of the {@link StringSelectionNodeOption}.
	 */
	public StringSelectionNodeOption(final String configIdentifier, final String label, final int defaultValueIndex,
			final String... options) {
		super(configIdentifier, options[defaultValueIndex]);
		setComponent(new DialogComponentStringSelection(getSettingsModel(), label, options));
	}
}
