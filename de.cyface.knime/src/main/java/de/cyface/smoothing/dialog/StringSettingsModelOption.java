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

import org.knime.core.node.defaultnodesettings.SettingsModel;
import org.knime.core.node.defaultnodesettings.SettingsModelString;

/**
 * Abstract base class for all {@link NodeOption}s using a
 * {@link SettingsModelString} as the underlying model.
 * 
 * @author Klemens Muthmann
 * @version 1.0.0
 * @since 1.0.0
 */
public abstract class StringSettingsModelOption extends NodeOption<SettingsModelString, String> {

	/**
	 * The {@link SettingsModelString} used by the {@link NodeOption} to store
	 * the current value.
	 */
	//private final SettingsModelString settingsModel;

	/**
	 * Creates a new completely initialized {@link StringSettingsModelOption}.
	 * 
	 * @param configIdentifier
	 *            The identifier used by KNIME to find the corresponding
	 *            {@link SettingsModel}.
	 * @param defaultValue
	 *            The initial starting value.
	 */
	public StringSettingsModelOption(final String configIdentifier, final String defaultValue) {
		super(configIdentifier, defaultValue);
	}

	@Override
	public final SettingsModelString getSettingsModel() {
		return new SettingsModelString(getConfigIdentifier(), getDefaultValue());
	}
}
