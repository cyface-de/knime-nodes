/**
 * 
 */
package de.cyface.timestamp;

import org.knime.core.data.IntValue;
import org.knime.core.data.LongValue;
import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentColumnNameSelection;
import org.knime.core.node.defaultnodesettings.SettingsModelString;

/**
 * <p>
 * Provides the dialog for selecting the two columns to align to each other. It
 * provides two drop down menus for selecting appropriate columns from the first
 * and the second table. Appropriate columns contain either integer or long
 * values.
 * </p>
 * 
 * @author Klemens Muthmann
 * @version 1.0.0
 * @since 1.0.0
 */
public final class TimestampAlignerNodeDialog extends DefaultNodeSettingsPane {

	/**
	 * <p>
	 * Creates a new completely initialized {@link TimestampAlignerNodeDialog}
	 * as required to be returned by {@link TimestampAlignerNodeFactory}.
	 * </p>
	 */
	@SuppressWarnings("unchecked")
	public TimestampAlignerNodeDialog() {
		super();

		addDialogComponent(new DialogComponentColumnNameSelection(
				new SettingsModelString(TimestampAlignerNodeModel.CFGKEY_FIRST_TABLE_COLUMN_NAME, "Select"),
				"Select the column to align.", TimestampAlignerNodeModel.FIRST_IN_PORT, LongValue.class,
				IntValue.class));
		addDialogComponent(new DialogComponentColumnNameSelection(
				new SettingsModelString(TimestampAlignerNodeModel.CFGKEY_SECOND_TABLE_COLUMN_NAME, "Select"),
				"Select the column to align to.", TimestampAlignerNodeModel.SECOND_IN_PORT, LongValue.class,
				IntValue.class));
	}

}
