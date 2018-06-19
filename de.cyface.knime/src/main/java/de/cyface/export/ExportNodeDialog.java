package de.cyface.export;

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
