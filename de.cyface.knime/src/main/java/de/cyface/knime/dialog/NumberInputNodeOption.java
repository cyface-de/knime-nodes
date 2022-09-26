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

import org.knime.core.node.defaultnodesettings.DialogComponentNumber;
import org.knime.core.node.defaultnodesettings.SettingsModelInteger;

/**
 * Node option taking a number as input.
 *
 * @author Klemens Muthmann
 * @version 1.0.0
 * @since 1.0.0
 */
public class NumberInputNodeOption extends NodeOption<SettingsModelInteger, Integer> {

    /**
     * Creates a new completely initialized instance of this class.
     *
     * @param configIdentifier The KNIME configuration identifier used to access the settings model.
     * @param label The label to explain to purpose of this option to the user.
     * @param defaultValue The default value to initialize this option with.
     * @param stepSize The amount each click on the up or down buttons to increase or decrease changes the value of this
     *            option.
     */
    public NumberInputNodeOption(final String configIdentifier, final String label, final int defaultValue,
            final int stepSize) {
        super(configIdentifier, defaultValue);
        setComponent(new DialogComponentNumber(getSettingsModel(), label, stepSize));
    }

    @Override
    public SettingsModelInteger getSettingsModel() {
        return new SettingsModelInteger(getConfigIdentifier(), getDefaultValue());
    }

}
