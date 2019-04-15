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
package de.cyface.knime.nodes.export;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.def.DoubleCell;
import org.knime.core.data.def.IntCell;
import org.knime.core.data.def.LongCell;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.defaultnodesettings.SettingsModelString;

/**
 * The <code>NodeModel</code> of the Cyface binary format exporter. This class handles validation of the input and writes the validated data to a file in Cyface binary format.
 * 
 * @author Klemens Muthmann
 * @version 1.0.1
 * @since 1.0.0
 */
public class ExportNodeModel extends NodeModel {

    /**
     * The Cyface binary format version, currently handled by this node.
     */
	private final static short DATA_FORMAT_VERSION = 1;
	/**
	 * The index of the input port reading geo locations.
	 */
	public final static int GEO_TABLE_INDEX = 0;
	/**
	 * The index of the input port reading accelerations.
	 */
	public final static int ACCELERATION_TABLE_INDEX = 1;
	/**
	 * The index of the input port reading rotations.
	 */
	public final static int ROTATION_TABLE_INDEX = 2;
	/**
	 * The index of the input port reading directions.
	 */
	public final static int DIRECTION_TABLE_INDEX = 3;
	
	/**
	 * The setting key for the name of the output file.
	 */
	public final static String OUTPUT_FILE_NAME_SETTING = "de.cyface.cfgkey.output";
	/**
	 * The setting storing the path to the output file.
	 */
	private final SettingsModelString outputFileNameSetting = new SettingsModelString(OUTPUT_FILE_NAME_SETTING, "");
	
	/**
	 * The setting key for the column containing acceleration timestamps.
	 */
	public final static String ACC_TIME_COL_SETTING = "de.cyface.cfgkey.acc_time_col";
	/**
	 * The setting storing the column containing the acceleration timestamps.
	 */
	private final SettingsModelString accTimeColSetting = new SettingsModelString(ACC_TIME_COL_SETTING,"");
	/**
     * The setting key for the column containing acceleration values in x direction.
     */
	public final static String ACC_X_COL_SETTING = "de.cyface.cfgkey.acc_x_col";
	/**
	 * The setting storing the column containing the acceleration values in x direction.
	 */
	private final SettingsModelString accXColSetting = new SettingsModelString(ACC_X_COL_SETTING,"");
	/**
     * The setting key for the column containing acceleration values in y direction.
     */
	public final static String ACC_Y_COL_SETTING = "de.cyface.cfgkey.acc_y_col";
	/**
	 * The setting storing the column containing the acceleration values in y direction.
	 */
	private final SettingsModelString accYColSetting = new SettingsModelString(ACC_Y_COL_SETTING,"");
	/**
     * The setting key for the column containing acceleration values in z direction.
     */
	public final static String ACC_Z_COL_SETTING = "de.cyface.cfgkey.acc_z_col";
	/**
	 * The setting storing the column containing the acceleration values in z direction.
	 */
	private final SettingsModelString accZColSetting = new SettingsModelString(ACC_Z_COL_SETTING,"");
	
	/**
     * The setting key for the column containing rotation timestamps.
     */
	public final static String ROT_TIME_COL_SETTING = "de.cyface.cfgkey.rot_time_col";
	/**
	 * The setting storing the column containing the rotations timestamps.
	 */
	private final SettingsModelString rotTimeColSetting = new SettingsModelString(ROT_TIME_COL_SETTING,"");
	/**
     * The setting key for the column containing rotation values in x direction.
     */
	public final static String ROT_X_COL_SETTING = "de.cyface.cfgkey.rot_x_col";
	/**
	 * The setting storing the column containing the rotations values in x direction.
	 */
	private final SettingsModelString rotXColSetting = new SettingsModelString(ROT_X_COL_SETTING,"");
	/**
     * The setting key for the column containing rotation values in y direction.
     */
	public final static String ROT_Y_COL_SETTING = "de.cyface.cfgkey.rot_y_col";
	/**
	 * The setting storing the column containing the rotations values in y direction.
	 */
	private final SettingsModelString rotYColSetting = new SettingsModelString(ROT_Y_COL_SETTING,"");
	/**
     * The setting key for the column containing rotation values in z direction.
     */
	public final static String ROT_Z_COL_SETTING = "de.cyface.cfgkey.rot_z_col";
	/**
	 * The setting storing the column containing the rotations values in z direction.
	 */
	private final SettingsModelString rotZColSetting = new SettingsModelString(ROT_Z_COL_SETTING,"");
	
	/**
     * The setting key for the column containing direction timestamps.
     */
	public final static String DIR_TIME_COL_SETTING = "de.cyface.cfgkey.dir_time_col";
	/**
	 * The setting storing the column containing the directions timestamps.
	 */
	private final SettingsModelString dirTimeColSetting = new SettingsModelString(DIR_TIME_COL_SETTING,"");
	/**
     * The setting key for the column containing direction values in x direction.
     */
	public final static String DIR_X_COL_SETTING = "de.cyface.cfgkey.dir_x_col";
	/**
	 * The setting storing the column containing the directions value in x direction.
	 */
	private final SettingsModelString dirXColSetting = new SettingsModelString(DIR_X_COL_SETTING,"");
	/**
     * The setting key for the column containing direction values in y direction.
     */
	public final static String DIR_Y_COL_SETTING = "de.cyface.cfgkey.dir_y_col";
	/**
	 * The setting storing the column containing the directions value in y direction.
	 */
	private final SettingsModelString dirYColSetting = new SettingsModelString(DIR_Y_COL_SETTING,"");
	/**
     * The setting key for the column containing direction values in z direction.
     */
	public final static String DIR_Z_COL_SETTING = "de.cyface.cfgkey.dir_z_col";
	/**
	 * The setting storing the column containing the directions value in z direction.
	 */
	private final SettingsModelString dirZColSetting = new SettingsModelString(DIR_Z_COL_SETTING,"");
	
	/**
     * The setting key for the column containing geo location timestamps.
     */
	public final static String GEO_TIME_COL_SETTING = "de.cyface.cfgkey.geo_time_col";
	/**
	 * The setting storing the column containing the geo locations timestamps.
	 */
	private final SettingsModelString geoTimeColSetting = new SettingsModelString(GEO_TIME_COL_SETTING,"");
	/**
     * The setting key for the column containing geo location latitude values.
     */
	public final static String GEO_LAT_COL_SETTING = "de.cyface.cfgkey.geo_lat_col";
	/**
	 * The setting storing the column containing the geo locations latitude values.
	 */
	private final SettingsModelString geoLatColSetting = new SettingsModelString(GEO_LAT_COL_SETTING,"");
	/**
     * The setting key for the column containing geo location longitude values.
     */
	public final static String GEO_LON_COL_SETTING = "de.cyface.cfgkey.geo_lon_col";
	/**
	 * The setting storing the column containing the geo locations longitude values.
	 */
	private final SettingsModelString geoLonColSetting = new SettingsModelString(GEO_LON_COL_SETTING,"");
	/**
     * The setting key for the column containing geo location speed values.
     */
	public final static String GEO_SPEED_COL_SETTING = "de.cyface.cfgkey.geo_speed_col";
	/**
	 * The setting storing the column containing the geo locations speed values.
	 */
	private final SettingsModelString geoSpeedColSetting = new SettingsModelString(GEO_SPEED_COL_SETTING,"");
	/**
     * The setting key for the column containing geo location accuracy values.
     */
	public final static String GEO_ACCURACY_COL_SETTING = "de.cyface.cfgkey.geo_accuracy_col";
	/**
	 * The setting storing the column containing the geo locations accuracy values.
	 */
	private final SettingsModelString geoAccuracyColSetting = new SettingsModelString(GEO_ACCURACY_COL_SETTING,"");

	/**
	 * Creates a new completely initialized model for an Cyface binary format exporter.
	 */
	public ExportNodeModel() {
		super(4, 0);
	}

	@Override
	protected void loadInternals(File nodeInternDir, ExecutionMonitor exec)
			throws IOException, CanceledExecutionException {
		// Node has no views and thus no internal data.

	}

	@Override
	protected void saveInternals(File nodeInternDir, ExecutionMonitor exec)
			throws IOException, CanceledExecutionException {
		// Node has no views and thus no internal data.

	}

	@Override
	protected void saveSettingsTo(NodeSettingsWO settings) {
		outputFileNameSetting.saveSettingsTo(settings);
		
		accTimeColSetting.saveSettingsTo(settings);
		accXColSetting.saveSettingsTo(settings);
		accYColSetting.saveSettingsTo(settings);
		accZColSetting.saveSettingsTo(settings);
		
		rotTimeColSetting.saveSettingsTo(settings);
		rotXColSetting.saveSettingsTo(settings);
		rotYColSetting.saveSettingsTo(settings);
		rotZColSetting.saveSettingsTo(settings);
		
		dirTimeColSetting.saveSettingsTo(settings);
		dirXColSetting.saveSettingsTo(settings);
		dirYColSetting.saveSettingsTo(settings);
		dirZColSetting.saveSettingsTo(settings);
		
		geoTimeColSetting.saveSettingsTo(settings);
		geoLatColSetting.saveSettingsTo(settings);
		geoLonColSetting.saveSettingsTo(settings);
		geoSpeedColSetting.saveSettingsTo(settings);
		geoAccuracyColSetting.saveSettingsTo(settings);

	}

	@Override
	protected void validateSettings(NodeSettingsRO settings) throws InvalidSettingsException {
		outputFileNameSetting.validateSettings(settings);
		
		accTimeColSetting.validateSettings(settings);
		accXColSetting.validateSettings(settings);
		accYColSetting.validateSettings(settings);
		accZColSetting.validateSettings(settings);
		
		rotTimeColSetting.validateSettings(settings);
		rotXColSetting.validateSettings(settings);
		rotYColSetting.validateSettings(settings);
		rotZColSetting.validateSettings(settings);
		
		dirTimeColSetting.validateSettings(settings);
		dirXColSetting.validateSettings(settings);
		dirYColSetting.validateSettings(settings);
		dirZColSetting.validateSettings(settings);
		
		geoTimeColSetting.validateSettings(settings);
		geoLatColSetting.validateSettings(settings);
		geoLonColSetting.validateSettings(settings);
		geoSpeedColSetting.validateSettings(settings);
		geoAccuracyColSetting.validateSettings(settings);

	}

	@Override
	protected void loadValidatedSettingsFrom(NodeSettingsRO settings) throws InvalidSettingsException {
		outputFileNameSetting.loadSettingsFrom(settings);
		
		accTimeColSetting.loadSettingsFrom(settings);
		accXColSetting.loadSettingsFrom(settings);
		accYColSetting.loadSettingsFrom(settings);
		accZColSetting.loadSettingsFrom(settings);
		
		rotTimeColSetting.loadSettingsFrom(settings);
		rotXColSetting.loadSettingsFrom(settings);
		rotYColSetting.loadSettingsFrom(settings);
		rotZColSetting.loadSettingsFrom(settings);
		
		dirTimeColSetting.loadSettingsFrom(settings);
		dirXColSetting.loadSettingsFrom(settings);
		dirYColSetting.loadSettingsFrom(settings);
		dirZColSetting.loadSettingsFrom(settings);
		
		geoTimeColSetting.loadSettingsFrom(settings);
		geoLatColSetting.loadSettingsFrom(settings);
		geoLonColSetting.loadSettingsFrom(settings);
		geoSpeedColSetting.loadSettingsFrom(settings);
		geoAccuracyColSetting.loadSettingsFrom(settings);
	}

	@Override
	protected void reset() {
		// Nothing to do here.

	}

	@Override
	protected BufferedDataTable[] execute(BufferedDataTable[] inData, ExecutionContext exec) throws Exception {
		BufferedDataTable geoLocationsTable = inData[0];
		BufferedDataTable accelerationsTable = inData[1];
		DataTableSpec accelerationsTableSpec = inData[1].getDataTableSpec();
		BufferedDataTable rotationsTable = inData[2];
		DataTableSpec rotationsTableSpec = inData[2].getDataTableSpec();
		BufferedDataTable directionsTable = inData[3];
		DataTableSpec directionsTableSpec = inData[3].getDataTableSpec();

		byte[] header = createHeader((int) geoLocationsTable.size(), (int) accelerationsTable.size(),
				(int) rotationsTable.size(), (int) directionsTable.size());
		long itemsToProcess = geoLocationsTable.size() + accelerationsTable.size() + rotationsTable.size() + directionsTable.size();
		byte[] geoLocations = serializeGeoLocations(geoLocationsTable, exec, itemsToProcess);
		byte[] accelerations = new Point3DSerializer() {

			@Override
			protected int getZColumnIndex() {
				return accelerationsTableSpec.findColumnIndex(accZColSetting.getStringValue());
			}

			@Override
			protected int getYColumnIndex() {
				return accelerationsTableSpec.findColumnIndex(accYColSetting.getStringValue());
			}

			@Override
			protected int getXColumnIndex() {
				return accelerationsTableSpec.findColumnIndex(accXColSetting.getStringValue());
			}

			@Override
			protected int getTimestampColumnIndex() {
				return accelerationsTableSpec.findColumnIndex(accTimeColSetting.getStringValue());
			}
		}.serialize(accelerationsTable, exec, itemsToProcess);
		byte[] rotations = new Point3DSerializer() {

			@Override
			protected int getXColumnIndex() {
				return rotationsTableSpec.findColumnIndex(rotXColSetting.getStringValue());
			}

			@Override
			protected int getYColumnIndex() {
				return rotationsTableSpec.findColumnIndex(rotYColSetting.getStringValue());
			}

			@Override
			protected int getZColumnIndex() {
				return rotationsTableSpec.findColumnIndex(rotZColSetting.getStringValue());
			}

			@Override
			protected int getTimestampColumnIndex() {
				return rotationsTableSpec.findColumnIndex(rotTimeColSetting.getStringValue());
			}

		}.serialize(rotationsTable, exec, itemsToProcess);
		byte[] directions = new Point3DSerializer() {

			@Override
			protected int getZColumnIndex() {
				return directionsTableSpec.findColumnIndex(dirZColSetting.getStringValue());
			}

			@Override
			protected int getYColumnIndex() {
				return directionsTableSpec.findColumnIndex(dirYColSetting.getStringValue());
			}

			@Override
			protected int getXColumnIndex() {
				return directionsTableSpec.findColumnIndex(dirXColSetting.getStringValue());
			}

			@Override
			protected int getTimestampColumnIndex() {
				return directionsTableSpec.findColumnIndex(dirTimeColSetting.getStringValue());
			}
		}.serialize(directionsTable, exec, itemsToProcess);

		ByteBuffer buffer = ByteBuffer.allocate(
				header.length + geoLocations.length + accelerations.length + rotations.length + directions.length);
		buffer.put(header);
		buffer.put(geoLocations);
		buffer.put(accelerations);
		buffer.put(rotations);
		buffer.put(directions);

		try (OutputStream dataWriter = new FileOutputStream(outputFileNameSetting.getStringValue())) {
			dataWriter.write(buffer.array());
		}

		return new BufferedDataTable[0];
	}

	/**
	 * Creates the header field for a serialized measurement in big endian
	 * format.
	 *
	 * @param countOfGeoLocations
	 *            Number of geo locations in the serialized measurement.
	 * @param countOfAccelerations
	 *            Number of accelerations in the serialized measurement.
	 * @param countOfRotations
	 *            Number of rotations in the serialized measurement.
	 * @param countOfDirections
	 *            Number of directions in the serialized measurement.
	 * @return The header byte array.
	 */
	private byte[] createHeader(final int countOfGeoLocations, final int countOfAccelerations,
			final int countOfRotations, final int countOfDirections) {
		byte[] ret = new byte[18];
		ret[0] = (byte) (DATA_FORMAT_VERSION >> 8);
		ret[1] = (byte) DATA_FORMAT_VERSION;
		ret[2] = (byte) (countOfGeoLocations >> 24);
		ret[3] = (byte) (countOfGeoLocations >> 16);
		ret[4] = (byte) (countOfGeoLocations >> 8);
		ret[5] = (byte) countOfGeoLocations;
		ret[6] = (byte) (countOfAccelerations >> 24);
		ret[7] = (byte) (countOfAccelerations >> 16);
		ret[8] = (byte) (countOfAccelerations >> 8);
		ret[9] = (byte) countOfAccelerations;
		ret[10] = (byte) (countOfRotations >> 24);
		ret[11] = (byte) (countOfRotations >> 16);
		ret[12] = (byte) (countOfRotations >> 8);
		ret[13] = (byte) countOfRotations;
		ret[14] = (byte) (countOfDirections >> 24);
		ret[15] = (byte) (countOfDirections >> 16);
		ret[16] = (byte) (countOfDirections >> 8);
		ret[17] = (byte) countOfDirections;
		return ret;
	}

	/**
	 * Serializes all the geo locations from the provided table.
	 *
	 * @param geoLocationsTable The table containing the geo locations to process
	 * @param context The <code>ExecutionContext</code> used to report progress and support cancellation.
	 * @param itemsToProcess The number of items to process in total by the node (geo locations + 3D data points).
	 * @return A <code>byte</code> array containing all the data.
	 * @throws CanceledExecutionException If execution was canceled during geo location serialization.
	 */
	private byte[] serializeGeoLocations(final BufferedDataTable geoLocationsTable, final ExecutionContext context, final long itemsToProcess) throws CanceledExecutionException {
		// Allocate enough space for all geo locations
		final ByteBuffer buffer = ByteBuffer
				.allocate((int) geoLocationsTable.size() * (Long.BYTES + 3 * Double.BYTES + Integer.BYTES));
		final DataTableSpec geoLocationsSpec = geoLocationsTable.getDataTableSpec();
		ExecutionMonitor monitor = context.createSubProgress(((double)geoLocationsTable.size())/itemsToProcess);
		double processedItems = 0.0;

		for (final DataRow row : geoLocationsTable) {
		    context.checkCanceled();
		    monitor.setProgress(processedItems/itemsToProcess);
		    
			buffer.putLong(((LongCell) row.getCell(geoLocationsSpec.findColumnIndex(geoTimeColSetting.getStringValue()))).getLongValue());
			buffer.putDouble(((DoubleCell) row.getCell(geoLocationsSpec.findColumnIndex(geoLatColSetting.getStringValue()))).getDoubleValue());
			buffer.putDouble(((DoubleCell) row.getCell(geoLocationsSpec.findColumnIndex(geoLonColSetting.getStringValue()))).getDoubleValue());
			buffer.putDouble(((DoubleCell) row.getCell(geoLocationsSpec.findColumnIndex(geoSpeedColSetting.getStringValue()))).getDoubleValue());
			buffer.putInt(((IntCell) row.getCell(geoLocationsSpec.findColumnIndex(geoAccuracyColSetting.getStringValue()))).getIntValue());
		}
		final byte[] payload = new byte[buffer.capacity()];
		((ByteBuffer) buffer.duplicate().clear()).get(payload);
		// if we want to switch from write to read mode on the byte buffer we
		// need to .flip() !!
		return payload;
	}
	
	@Override
	protected DataTableSpec[] configure(final DataTableSpec[] inSpecs) throws InvalidSettingsException {
		return new DataTableSpec[0];
	}

}
