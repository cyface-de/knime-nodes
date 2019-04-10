/*
 * Copyright 2018 Cyface GmbH
 * 
 * This file is part of the Cyface KNIME Nodes.
 *
 * The Cyface KNIME Nodes is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * The Cyface KNIME Nodes is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Cyface KNIME Nodes. If not, see <http://www.gnu.org/licenses/>.
 */
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
