/*
 * Copyright 2018 Cyface GmbH
 *
 * This file is part of the Cyface Nodes.
 *
 * The Cyface Nodes is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Cyface Nodes is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Cyface Nodes. If not, see <http://www.gnu.org/licenses/>.
 */
package de.cyface.knime.dialog;

import org.knime.core.node.defaultnodesettings.DialogComponentButtonGroup;
import org.knime.core.node.defaultnodesettings.SettingsModel;

/**
 * A {@link NodeOption} with group of buttons to select one option from.
 *
 * @author Klemens Muthmann
 * @version 1.0.0
 * @since 1.0.0
 */
public final class ButtonGroupNodeOption extends StringSettingsModelOption {

	/**
	 * Creates a new completely initialized {@link ButtonGroupNodeOption}.
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
