package de.cyface.smoothing.dialog;

import org.knime.core.node.defaultnodesettings.DialogComponentButtonGroup;
import org.knime.core.node.defaultnodesettings.SettingsModel;

/**
 * <p>
 * A {@link NodeOption} with group of buttons to select one option from.
 * </p>
 * 
 * @author Klemens Muthmann
 * @version 1.0.0
 * @since 1.0.0
 */
public final class ButtonGroupNodeOption extends StringSettingsModelOption {

	/**
	 * <p>
	 * Creates a new completely initialized {@link ButtonGroupNodeOption}.
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
	 *            All possible values of the {@link ButtonGroupNodeOption}.
	 */
	public ButtonGroupNodeOption(String configIdentifier, final String label, final int defaultValueIndex,
			String... options) {
		super(configIdentifier, options[defaultValueIndex]);
		setComponent(new DialogComponentButtonGroup(getSettingsModel(), true, label, options));
	}

}
