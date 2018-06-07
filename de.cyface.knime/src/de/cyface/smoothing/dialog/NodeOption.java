package de.cyface.smoothing.dialog;

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
 */
public abstract class NodeOption<T extends SettingsModel> {
	/**
	 * The dialog component representing the {@link NodeOption} on the
	 * {@link NodeDialog}.
	 */
	private DialogComponent component;
	/**
	 * The identifier used by KNIME to identifie the {@link SettingsModel}.
	 */
	private final String configIdentifier;
	/**
	 * An initial default value.
	 */
	private final String defaultValue;

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
	public NodeOption(final String configIdentifier, final String defaultValue) {
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
	final String getDefaultValue() {
		return defaultValue;
	}

	/**
	 * @return The {@link SettingsModel} used by the {@link NodeOption} to store
	 *         the current value.
	 */
	public abstract T getSettingsModel();
}
