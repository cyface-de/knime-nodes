/**
 * 
 */
package de.cyface.timestamp;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.knime.core.data.IntValue;
import org.knime.core.data.LongValue;
import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentBoolean;
import org.knime.core.node.defaultnodesettings.DialogComponentColumnNameSelection;
import org.knime.core.node.defaultnodesettings.DialogComponentString;
import org.knime.core.node.defaultnodesettings.SettingsModelBoolean;
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
 * @version 1.1.0
 * @since 1.0.0
 */
public final class TimestampAlignerNodeDialog extends DefaultNodeSettingsPane {

	private final static String FIRST_TABLE_LABEL = "Select the column to align.";
	private final static String RENAME_FIRST_TABLE_COLUMN_CHECKBOX_LABEL = "Check to rename first column";
	private final static String RENAME_FIRST_TABLE_COLUMN_NAME_LABEL = "New name for first column";
	private final static String SECOND_TABLE_LABEL = "Select the column to align to.";
	private final static String RENAME_SECOND_TABLE_COLUMN_CHECKBOX_LABEL = "Check to rename second column";
	private final static String RENAME_SECOND_TABLE_COLUMN_NAME_LABEL = "New name for second column";
	private final static String VALUE_RANGE_ALIGNMENT_LABEL = "Align value ranges";

	/**
	 * <p>
	 * Creates a new completely initialized {@link TimestampAlignerNodeDialog}
	 * as required to be returned by {@link TimestampAlignerNodeFactory}.
	 * </p>
	 */
	@SuppressWarnings("unchecked")
	public TimestampAlignerNodeDialog() {
		super();

		createNewGroup("Low frequency timestamp settings.");
		addDialogComponent(new DialogComponentColumnNameSelection(
				new SettingsModelString(TimestampAlignerNodeModel.CFGKEY_FIRST_TABLE_COLUMN_NAME, "Select"),
				FIRST_TABLE_LABEL, TimestampAlignerNodeModel.FIRST_IN_PORT, LongValue.class, IntValue.class));
		setHorizontalPlacement(true);
		SettingsModelBoolean firstTimestampColumnRenameCheckbox = new SettingsModelBoolean(
				TimestampAlignerNodeModel.CFGKEY_RENAME_FIRST_TABLE_COLUMN_CHECKBOX, false);
		addDialogComponent(new DialogComponentBoolean(firstTimestampColumnRenameCheckbox,
				RENAME_FIRST_TABLE_COLUMN_CHECKBOX_LABEL));
		SettingsModelString firstTimestampColumnRenameName = new SettingsModelString(
				TimestampAlignerNodeModel.CFGKEY_RENAME_FIRST_TABLE_COLUMN_NAME, "");
		firstTimestampColumnRenameName.setEnabled(false);
		addDialogComponent(new DialogComponentString(firstTimestampColumnRenameName, ""));
		setHorizontalPlacement(false);
		closeCurrentGroup();

		createNewGroup("High frequency timestamp settings.");
		addDialogComponent(new DialogComponentColumnNameSelection(
				new SettingsModelString(TimestampAlignerNodeModel.CFGKEY_SECOND_TABLE_COLUMN_NAME, "Select"),
				SECOND_TABLE_LABEL, TimestampAlignerNodeModel.SECOND_IN_PORT, LongValue.class, IntValue.class));
		setHorizontalPlacement(true);
		SettingsModelBoolean secondTimestampColumnRenameCheckbox = new SettingsModelBoolean(
				TimestampAlignerNodeModel.CFGKEY_RENAME_SECOND_TABLE_COLUMN_CHECKBOX, false);
		addDialogComponent(new DialogComponentBoolean(secondTimestampColumnRenameCheckbox,
				RENAME_SECOND_TABLE_COLUMN_CHECKBOX_LABEL));
		SettingsModelString secondTimestampColumnRenameName = new SettingsModelString(
				TimestampAlignerNodeModel.CFGKEY_RENAME_SECOND_TABLE_COLUMN_NAME, "");
		secondTimestampColumnRenameName.setEnabled(false);
		addDialogComponent(new DialogComponentString(secondTimestampColumnRenameName, ""));
		setHorizontalPlacement(false);
		closeCurrentGroup();

		addDialogComponent(new DialogComponentBoolean(
				new SettingsModelBoolean(TimestampAlignerNodeModel.CFGKEY_VALUE_RANGE_ALIGNMENT_NAME, false),
				VALUE_RANGE_ALIGNMENT_LABEL));

		firstTimestampColumnRenameCheckbox.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				firstTimestampColumnRenameName.setEnabled(firstTimestampColumnRenameCheckbox.getBooleanValue());
			}
		});
		secondTimestampColumnRenameCheckbox.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				secondTimestampColumnRenameName.setEnabled(secondTimestampColumnRenameCheckbox.getBooleanValue());
			}

		});
	}

}
