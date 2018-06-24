package de.cyface.smoothing.dialog;

import org.knime.core.node.defaultnodesettings.DialogComponentNumber;
import org.knime.core.node.defaultnodesettings.SettingsModelInteger;

public class NumberInputNodeOption extends NodeOption<SettingsModelInteger, Integer> {
	
	//private final SettingsModelInteger settingsModel;
	
	public NumberInputNodeOption(final String configIdentifier, final String label, final int defaultValue, final int stepSize) {
		super(configIdentifier, defaultValue);
		setComponent(new DialogComponentNumber(getSettingsModel(), label, stepSize));
	}

	@Override
	public SettingsModelInteger getSettingsModel() {
		return new SettingsModelInteger(getConfigIdentifier(), getDefaultValue());
	}

}
