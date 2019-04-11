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
package de.cyface.knime.dialog;

import org.knime.core.node.NodeDialog;
import org.knime.core.node.NodeModel;
import org.knime.core.node.defaultnodesettings.DialogComponent;
import org.knime.core.node.defaultnodesettings.SettingsModel;

/**
 * A {@link NodeOption} is something you can set via the {@link NodeDialog}. It
 * is usually represented by a Swing UI element and its current state is stored
 * as a {@link SettingsModel}. The {@link SettingsModel} is the connection
 * between the {@link NodeDialog} and the {@link NodeModel}.
 * 
 * @author Klemens Muthmann
 * @version 1.0.0
 * @since 1.0.0
 *
 * @param <T>
 *            The type of {@link SettingsModel} used by the {@link NodeOption}.
 *            @param <D> The type of value wrapped  by the <code>SettingsModel</code>.
 */
public abstract class NodeOption<T extends SettingsModel, D> {
	/**
	 * The dialog component representing the {@link NodeOption} on the
	 * {@link NodeDialog}.
	 */
	private DialogComponent component;
	/**
	 * The identifier used by KNIME to identifier the {@link SettingsModel}.
	 */
	private final String configIdentifier;
	/**
	 * An initial default value.
	 */
	private final D defaultValue;

	/**
	 * Creates a new {@link NodeOption} without a valid component. Before using
	 * this object you need to call {@link #setComponent(DialogComponent)}. This
	 * is usually done inside the constructor of this classes subclasses.
	 * 
	 * @param configIdentifier
	 *            The identifier used by KNIME to identifie the
	 *            {@link SettingsModel}.
	 * @param defaultValue
	 *            An initial default value.
	 */
	public NodeOption(final String configIdentifier, final D defaultValue) {
		this.configIdentifier = configIdentifier;
		this.defaultValue = defaultValue;
	}

	/**
	 * @param component
	 *            The dialog component representing the {@link NodeOption} on
	 *            the {@link NodeDialog}.
	 */
	final void setComponent(final DialogComponent component) {
		if (component == null)
			throw new IllegalArgumentException("Encountered null parameter 'component'.");
		this.component = component;
	}

	/**
	 * @return The dialog component representing the {@link NodeOption} on the
	 *         {@link NodeDialog}.
	 */
	public final DialogComponent getComponent() {
		return component;
	}

	/**
	 * @return The identifier used by KNIME to identifie the
	 *         {@link SettingsModel}.
	 */
	final String getConfigIdentifier() {
		return configIdentifier;
	}

	/**
	 * @return An initial default value.
	 */
	final D getDefaultValue() {
		return defaultValue;
	}

	/**
	 * @return The {@link SettingsModel} used by the {@link NodeOption} to store
	 *         the current value.
	 */
	public abstract T getSettingsModel();
}
