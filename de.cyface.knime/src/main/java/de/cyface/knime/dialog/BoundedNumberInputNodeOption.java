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
import org.knime.core.node.defaultnodesettings.SettingsModelOddIntegerBounded;

/**
 * An option taking a number with a minimum value as input.
 *
 * @author Klemens Muthmann
 * @version 1.0.0
 * @since 1.0.0
 */
public final class BoundedNumberInputNodeOption extends NodeOption<SettingsModelOddIntegerBounded, Integer> {

    /**
     * The minimum value this option uses as input.
     */
    private final static int MIN_VALUE = 3;
    /**
     * The step size how to increase the number with each click on the arrow up or decrease it with each click on the
     * arrow down button.
     */
    private final static int STEP_SIZE = 2;

    /**
     * Creates a new completely initialized instance of this class.
     *
     * @param configIdentifier The KNIME configuration identifier used to access the settings model.
     * @param label The label to explain to purpose of this option to the user.
     */
    public BoundedNumberInputNodeOption(final String configIdentifier, final String label) {
        super(configIdentifier, MIN_VALUE);

        DialogComponentNumber component = new DialogComponentNumber(getSettingsModel(), label, STEP_SIZE);
        setComponent(component);
    }

    @Override
    public SettingsModelOddIntegerBounded getSettingsModel() {
        return new SettingsModelOddIntegerBounded(getConfigIdentifier(), getDefaultValue(), MIN_VALUE,
                Integer.MAX_VALUE);
    }

}
