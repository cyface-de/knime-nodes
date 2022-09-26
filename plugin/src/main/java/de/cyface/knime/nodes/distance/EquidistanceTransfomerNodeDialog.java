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
package de.cyface.knime.nodes.distance;

import org.knime.core.data.DoubleValue;
import org.knime.core.data.IntValue;
import org.knime.core.data.LongValue;
import org.knime.core.data.date.DateAndTimeValue;
import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentColumnNameSelection;
import org.knime.core.node.defaultnodesettings.DialogComponentNumberEdit;
import org.knime.core.node.defaultnodesettings.DialogComponentString;
import org.knime.core.node.defaultnodesettings.SettingsModelDouble;
import org.knime.core.node.defaultnodesettings.SettingsModelString;

/**
 * <p>
 * <code>NodeDialog</code> for the "EquidistanceTransfomer" Node. Takes a
 * function f(x) defined by two columns containing the values for x and for f(x)
 * and calculate a new function on x where f(x) are equdistant points which are
 * interpolated between original values of f(x) if f(x) is not equidistant
 * itself. The columns containing x and f(x) need to be numeric and sorted by x.
 *
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more
 * complex dialog please derive directly from
 * {@link org.knime.core.node.NodeDialogPane}.
 * </p>
 *
 * @author Klemens Muthmann
 * @version 1.0.0
 * @since 1.0.0
 */
public class EquidistanceTransfomerNodeDialog extends DefaultNodeSettingsPane {

	/**
	 * <p>
	 * New pane for configuring EquidistanceTransfomer node dialog. This is just
	 * a suggestion to demonstrate possible default dialog components.
	 * </p>
	 */
	@SuppressWarnings("unchecked")
	protected EquidistanceTransfomerNodeDialog() {
		super();

		addDialogComponent(new DialogComponentNumberEdit(
				new SettingsModelDouble(EquidistanceTransfomerNodeModel.CFGKEY_DISTANCE, 0.0), "Distance"));
		addDialogComponent(new DialogComponentColumnNameSelection(
				new SettingsModelString(EquidistanceTransfomerNodeModel.CFGKEY_X_COLUMN, ""), "x column",
				EquidistanceTransfomerNodeModel.IN_PORT, IntValue.class, DoubleValue.class, LongValue.class,
				DateAndTimeValue.class));
		addDialogComponent(new DialogComponentColumnNameSelection(
				new SettingsModelString(EquidistanceTransfomerNodeModel.CFGKEY_FX_COLUMN, ""), "f(x) column",
				EquidistanceTransfomerNodeModel.IN_PORT, IntValue.class, DoubleValue.class, LongValue.class,
				DateAndTimeValue.class));
		addDialogComponent(new DialogComponentString(
				new SettingsModelString(EquidistanceTransfomerNodeModel.CFGKEY_NEW_X_COLUMN, ""),
				"Column name for transformed X values.", true, 20));
		addDialogComponent(new DialogComponentString(
				new SettingsModelString(EquidistanceTransfomerNodeModel.CFGKEY_NEW_FX_COLUMN, ""),
				"Column name for transformed f(x) values.", true, 20));
	}
}
