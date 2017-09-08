package de.cyface.smoothing.dialog;

import org.knime.core.node.defaultnodesettings.SettingsModel;
import org.knime.core.node.defaultnodesettings.SettingsModelString;

/**
 * <p>
 * Abstract base class for all {@link NodeOption}s using a
 * {@link SettingsModelString} as the underlying model.
 * </p>
 * 
 * @author Klemens Muthmann
 * @version 1.0.0
 * @since 1.0.0
 */
public abstract class StringSettingsModelOption extends NodeOption<SettingsModelString> {

	/**
	 * <p>
	 * The {@link SettingsModelString} used by the {@link NodeOption} to store
	 * the current value.
	 * </p>
	 */
	private SettingsModelString settingsModel;

	/**
	 * <p>
	 * Creates a new completely initialized {@link StringSettingsModelOption}.
	 * </p>
	 * 
	 * @param configIdentifier
	 *            The identifier used by KNIME to find the corresponding
	 *            {@link SettingsModel}.
	 * @param defaultValue
	 *            The initial starting value.
	 */
	public StringSettingsModelOption(String configIdentifier, String defaultValue) {
		super(configIdentifier, defaultValue);
		settingsModel = new SettingsModelString(getConfigIdentifier(), getDefaultValue());
	}

	@Override
	public final SettingsModelString getSettingsModel() {
		return settingsModel;
	}
}
