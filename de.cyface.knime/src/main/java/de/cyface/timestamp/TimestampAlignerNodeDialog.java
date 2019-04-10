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
 * Provides the dialog for selecting the two columns to align to each other. It
 * provides two drop down menus for selecting appropriate columns from the first
 * and the second table. Appropriate columns contain either integer or long
 * values.
 * 
 * @author Klemens Muthmann
 * @version 1.1.0
 * @since 1.0.0
 */
public final class TimestampAlignerNodeDialog extends DefaultNodeSettingsPane {

	/**
	 * The label explaining the function of the first column selection to the user.
	 */
	private final static String FIRST_TABLE_LABEL = "Select the column to align.";
	/**
	 * The label allowing the user to rename the first timestamp column.
	 */
	private final static String RENAME_FIRST_TABLE_COLUMN_CHECKBOX_LABEL = "Check to rename first column";
	/**
	 * The new name of the first table column if the checkbox to rename was checked.
	 */
	private final static String RENAME_FIRST_TABLE_COLUMN_NAME_LABEL = "New name for first column";
	/**
	 * The label explaining the function of the second column selection to the user.
	 */
	private final static String SECOND_TABLE_LABEL = "Select the column to align to.";
	/**
	 * The label allowing the user to rename the second timestamp column.
	 */
	private final static String RENAME_SECOND_TABLE_COLUMN_CHECKBOX_LABEL = "Check to rename second column";
	/**
	 * The new name of the first table column if the checkbox to rename was checked.
	 */
	private final static String RENAME_SECOND_TABLE_COLUMN_NAME_LABEL = "New name for second column";
	/**
	 * The label of the option to allow the user to align value ranges.
	 */
	private final static String VALUE_RANGE_ALIGNMENT_LABEL = "Align value ranges";

	/**
	 * Creates a new completely initialized {@link TimestampAlignerNodeDialog}
	 * as required to be returned by {@link TimestampAlignerNodeFactory}.
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
