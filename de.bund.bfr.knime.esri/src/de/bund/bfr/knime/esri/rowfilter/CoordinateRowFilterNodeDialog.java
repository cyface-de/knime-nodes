/*******************************************************************************
 * Copyright (c) 2016 German Federal Institute for Risk Assessment (BfR)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Contributors:
 *     Department Biological Safety - BfR
 *******************************************************************************/
package de.bund.bfr.knime.esri.rowfilter;

import org.knime.core.data.DoubleValue;
import org.knime.core.data.collection.ListDataValue;
import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentColumnNameSelection;
import org.knime.core.node.defaultnodesettings.SettingsModelString;

/**
 * <code>NodeDialog</code> for the "CoordinateRowFilter" Node.
 * 
 *
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more
 * complex dialog please derive directly from
 * {@link org.knime.core.node.NodeDialogPane}.
 * 
 * @author Christian Thoens
 */
public class CoordinateRowFilterNodeDialog extends DefaultNodeSettingsPane {

	/**
	 * New pane for configuring the CoordinateRowFilter node.
	 */
	@SuppressWarnings("unchecked")
	protected CoordinateRowFilterNodeDialog() {
		createNewGroup("Coordinates to filter");
		addDialogComponent(new DialogComponentColumnNameSelection(
				new SettingsModelString(CoordinateRowFilterNodeModel.CFG_LATITUDE_COLUMN, null), "Latitude Column", 0,
				DoubleValue.class));
		addDialogComponent(new DialogComponentColumnNameSelection(
				new SettingsModelString(CoordinateRowFilterNodeModel.CFG_LONGITUDE_COLUMN, null), "Longitude Column", 0,
				DoubleValue.class));
		createNewGroup("Polygon");
		addDialogComponent(new DialogComponentColumnNameSelection(
				new SettingsModelString(CoordinateRowFilterNodeModel.CFG_POLYGON_COLUMN, null), "Polygon Column", 1,
				ListDataValue.class));
		createNewGroup("Polygon Coordinates");
		addDialogComponent(new DialogComponentColumnNameSelection(
				new SettingsModelString(CoordinateRowFilterNodeModel.CFG_POLYGON_LATITUDE_COLUMN, null),
				"Latitude Column", 2, DoubleValue.class));
		addDialogComponent(new DialogComponentColumnNameSelection(
				new SettingsModelString(CoordinateRowFilterNodeModel.CFG_POLYGON_LONGITUDE_COLUMN, null),
				"Longitude Column", 2, DoubleValue.class));
	}
}
