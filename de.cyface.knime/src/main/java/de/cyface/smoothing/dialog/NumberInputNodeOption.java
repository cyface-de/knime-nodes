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
