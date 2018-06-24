package de.cyface.smoothing.dialog;

import org.knime.core.node.defaultnodesettings.DialogComponentNumber;
import org.knime.core.node.defaultnodesettings.SettingsModelOddIntegerBounded;

public final class BoundedNumberInputNodeOption extends NodeOption<SettingsModelOddIntegerBounded, Integer>{
	
	private final static int MIN_VALUE = 3;
	private final static int STEP_SIZE = 2;
	
	//private final SettingsModelOddIntegerBounded settingsModel;
	
	public BoundedNumberInputNodeOption(final String configIdentifier, final String label) {
		super(configIdentifier, MIN_VALUE);
		
		DialogComponentNumber component = new DialogComponentNumber(getSettingsModel(), label, STEP_SIZE);
		setComponent(component);
	}

	@Override
	public SettingsModelOddIntegerBounded getSettingsModel() {
		return new SettingsModelOddIntegerBounded(getConfigIdentifier(), getDefaultValue(), MIN_VALUE, Integer.MAX_VALUE);
	}

}
