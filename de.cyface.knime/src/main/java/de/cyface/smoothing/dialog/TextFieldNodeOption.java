package de.cyface.smoothing.dialog;

import org.knime.core.node.defaultnodesettings.DialogComponentString;
import org.knime.core.node.defaultnodesettings.SettingsModel;

/**
 * {@link NodeOption} representing a single input text field for any
 * {@link String}.
 * 
 * @author Klemens Muthmann
 * @version 1.0.0
 * @since 1.0.0
 */
public final class TextFieldNodeOption extends StringSettingsModelOption {

	/**
	 * <p>
	 * Creates a new completely initialized {@link TextFieldNodeOption}.
	 * </p>
	 * 
	 * @param configIdentifier
	 *            The identifier used by KNIME to find the corresponding
	 *            {@link SettingsModel}.
	 * @param label
	 *            The label appearing on the UI (usually to the left of the
	 *            input field).
	 * @param defaultValue
	 *            The initial starting value.
	 */
	public TextFieldNodeOption(final String configIdentifier, final String label, final String defaultValue) {
		super(configIdentifier, defaultValue);
		setComponent(new DialogComponentString(getSettingsModel(), label));
	}

}
