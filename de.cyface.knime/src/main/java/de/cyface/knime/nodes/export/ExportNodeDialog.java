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
package de.cyface.knime.nodes.export;

import java.awt.JobAttributes.DialogType;

import javax.swing.JFileChooser;

import org.knime.core.data.DoubleValue;
import org.knime.core.data.IntValue;
import org.knime.core.data.LongValue;
import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentColumnNameSelection;
import org.knime.core.node.defaultnodesettings.DialogComponentFileChooser;
import org.knime.core.node.defaultnodesettings.DialogComponentLabel;
import org.knime.core.node.defaultnodesettings.SettingsModelString;

public class ExportNodeDialog extends DefaultNodeSettingsPane {

	private final static String OUTPUT_FILE_HISTORY_ID = "de.cyface.outputfilehistory";

	public ExportNodeDialog() {
		super();

		addDialogComponent(
				new DialogComponentFileChooser(new SettingsModelString(ExportNodeModel.OUTPUT_FILE_NAME_SETTING, ""),
						OUTPUT_FILE_HISTORY_ID, JFileChooser.SAVE_DIALOG, false, "cyf"));
		
		addDialogComponent(new DialogComponentLabel("Choose Geo Location Columns:"));
		addDialogComponent(new DialogComponentColumnNameSelection(new SettingsModelString(ExportNodeModel.GEO_TIME_COL_SETTING,""), "Capture Time", ExportNodeModel.GEO_TABLE_INDEX, true, false, LongValue.class));
		addDialogComponent(new DialogComponentColumnNameSelection(new SettingsModelString(ExportNodeModel.GEO_LAT_COL_SETTING,""), "Latitude",ExportNodeModel.GEO_TABLE_INDEX,true,false,DoubleValue.class));
		addDialogComponent(new DialogComponentColumnNameSelection(new SettingsModelString(ExportNodeModel.GEO_LON_COL_SETTING,""), "Longitude", ExportNodeModel.GEO_TABLE_INDEX,true,false,DoubleValue.class));
		addDialogComponent(new DialogComponentColumnNameSelection(new SettingsModelString(ExportNodeModel.GEO_SPEED_COL_SETTING,""), "Speed", ExportNodeModel.GEO_TABLE_INDEX,true,false,DoubleValue.class));
		addDialogComponent(new DialogComponentColumnNameSelection(new SettingsModelString(ExportNodeModel.GEO_ACCURACY_COL_SETTING,""), "Accuracy", ExportNodeModel.GEO_TABLE_INDEX,true,false,IntValue.class));
		
		addDialogComponent(new DialogComponentLabel("Choose Accelerations Table Columns:"));
		addDialogComponent(new DialogComponentColumnNameSelection(new SettingsModelString(ExportNodeModel.ACC_TIME_COL_SETTING,""),"Capture Time", ExportNodeModel.ACCELERATION_TABLE_INDEX,true,false,LongValue.class));
		addDialogComponent(new DialogComponentColumnNameSelection(new SettingsModelString(ExportNodeModel.ACC_X_COL_SETTING,""),"X",ExportNodeModel.ACCELERATION_TABLE_INDEX,true,false,DoubleValue.class));
		addDialogComponent(new DialogComponentColumnNameSelection(new SettingsModelString(ExportNodeModel.ACC_Y_COL_SETTING,""),"Y",ExportNodeModel.ACCELERATION_TABLE_INDEX,true,false,DoubleValue.class));
		addDialogComponent(new DialogComponentColumnNameSelection(new SettingsModelString(ExportNodeModel.ACC_Z_COL_SETTING,""),"Z",ExportNodeModel.ACCELERATION_TABLE_INDEX,true,false,DoubleValue.class));
		
		addDialogComponent(new DialogComponentLabel("Choose Rotations Table Columns:"));
		addDialogComponent(new DialogComponentColumnNameSelection(new SettingsModelString(ExportNodeModel.ROT_TIME_COL_SETTING,""),"Capture Time", ExportNodeModel.ROTATION_TABLE_INDEX,true,false,LongValue.class));
		addDialogComponent(new DialogComponentColumnNameSelection(new SettingsModelString(ExportNodeModel.ROT_X_COL_SETTING,""),"X",ExportNodeModel.ROTATION_TABLE_INDEX,true,false,DoubleValue.class));
		addDialogComponent(new DialogComponentColumnNameSelection(new SettingsModelString(ExportNodeModel.ROT_Y_COL_SETTING,""),"Y",ExportNodeModel.ROTATION_TABLE_INDEX,true,false,DoubleValue.class));
		addDialogComponent(new DialogComponentColumnNameSelection(new SettingsModelString(ExportNodeModel.ROT_Z_COL_SETTING,""),"Z",ExportNodeModel.ROTATION_TABLE_INDEX,true,false,DoubleValue.class));
		
		addDialogComponent(new DialogComponentLabel("Choose Directions Table Columns:"));
		addDialogComponent(new DialogComponentColumnNameSelection(new SettingsModelString(ExportNodeModel.DIR_TIME_COL_SETTING,""),"Capture Time", ExportNodeModel.DIRECTION_TABLE_INDEX,true,false,LongValue.class));
		addDialogComponent(new DialogComponentColumnNameSelection(new SettingsModelString(ExportNodeModel.DIR_X_COL_SETTING,""),"X",ExportNodeModel.DIRECTION_TABLE_INDEX,true,false,DoubleValue.class));
		addDialogComponent(new DialogComponentColumnNameSelection(new SettingsModelString(ExportNodeModel.DIR_Y_COL_SETTING,""),"Y",ExportNodeModel.DIRECTION_TABLE_INDEX,true,false,DoubleValue.class));
		addDialogComponent(new DialogComponentColumnNameSelection(new SettingsModelString(ExportNodeModel.DIR_Z_COL_SETTING,""),"Z",ExportNodeModel.DIRECTION_TABLE_INDEX,true,false,DoubleValue.class));
		
	}

}
