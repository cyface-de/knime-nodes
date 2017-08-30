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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.knime.core.data.DataCell;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DoubleValue;
import org.knime.core.data.StringValue;
import org.knime.core.data.collection.ListDataValue;
import org.knime.core.node.BufferedDataContainer;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.defaultnodesettings.SettingsModelString;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.streamable.InputPortRole;
import org.knime.core.node.streamable.OutputPortRole;
import org.knime.core.node.streamable.PartitionInfo;
import org.knime.core.node.streamable.PortInput;
import org.knime.core.node.streamable.PortObjectInput;
import org.knime.core.node.streamable.PortOutput;
import org.knime.core.node.streamable.RowInput;
import org.knime.core.node.streamable.RowOutput;
import org.knime.core.node.streamable.StreamableOperator;

import com.google.common.collect.Iterables;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;

import de.bund.bfr.knime.esri.EsriUtils;

/**
 * This is the model implementation of CoordinateRowFilter.
 * 
 *
 * @author Christian Thoens
 */
public class CoordinateRowFilterNodeModel extends NodeModel {

	protected static final String CFG_LATITUDE_COLUMN = "LatitudeColumn";
	protected static final String CFG_LONGITUDE_COLUMN = "LongitudeColumn";
	protected static final String CFG_POLYGON_COLUMN = "PolygonColumn";
	protected static final String CFG_POLYGON_LATITUDE_COLUMN = "PolygonLatitudeColumn";
	protected static final String CFG_POLYGON_LONGITUDE_COLUMN = "PolygonLongitudeColumn";

	private SettingsModelString latitudeColumm;
	private SettingsModelString longitudeColumm;
	private SettingsModelString polygonColumm;
	private SettingsModelString polygonLatitudeColumm;
	private SettingsModelString polygonLongitudeColumm;

	/**
	 * Constructor for the node model.
	 */
	protected CoordinateRowFilterNodeModel() {
		super(3, 1);
		latitudeColumm = new SettingsModelString(CFG_LATITUDE_COLUMN, null);
		longitudeColumm = new SettingsModelString(CFG_LONGITUDE_COLUMN, null);
		polygonColumm = new SettingsModelString(CFG_POLYGON_COLUMN, null);
		polygonLatitudeColumm = new SettingsModelString(CFG_POLYGON_LATITUDE_COLUMN, null);
		polygonLongitudeColumm = new SettingsModelString(CFG_POLYGON_LONGITUDE_COLUMN, null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected BufferedDataTable[] execute(final BufferedDataTable[] inData, final ExecutionContext exec)
			throws Exception {
		BufferedDataTable coordinateTable = inData[0];
		MultiPolygon polygon = createPolygon(inData[1], inData[2]);
		BufferedDataContainer container = exec.createDataContainer(coordinateTable.getSpec());
		int index = 0;

		try {
			for (DataRow row : coordinateTable) {
				if (isRowInFilter(row, coordinateTable.getSpec(), polygon)) {
					container.addRowToTable(row);
				}

				exec.checkCanceled();
				exec.setProgress((double) index / (double) coordinateTable.size());
				index++;
			}
		} finally {
			container.close();
		}

		return new BufferedDataTable[] { container.getTable() };
	}

	@Override
	public StreamableOperator createStreamableOperator(final PartitionInfo partitionInfo,
			final PortObjectSpec[] inSpecs) throws InvalidSettingsException {
		return new StreamableOperator() {

			@Override
			public void runFinal(final PortInput[] inputs, final PortOutput[] outputs, final ExecutionContext exec)
					throws Exception {
				MultiPolygon polygon = createPolygon((BufferedDataTable) ((PortObjectInput) inputs[1]).getPortObject(),
						(BufferedDataTable) ((PortObjectInput) inputs[2]).getPortObject());
				RowInput input = (RowInput) inputs[0];
				RowOutput output = (RowOutput) outputs[0];
				DataRow inputRow;
				long index = 0;

				try {
					while ((inputRow = input.poll()) != null) {
						exec.checkCanceled();
						exec.setProgress("Adding row " + index + ".");
						index++;

						if (isRowInFilter(inputRow, input.getDataTableSpec(), polygon)) {
							output.push(inputRow);
						}
					}
				} finally {
					input.close();
					output.close();
				}
			}
		};
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public InputPortRole[] getInputPortRoles() {
		return new InputPortRole[] { InputPortRole.DISTRIBUTED_STREAMABLE, InputPortRole.NONDISTRIBUTED_NONSTREAMABLE,
				InputPortRole.NONDISTRIBUTED_NONSTREAMABLE };
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public OutputPortRole[] getOutputPortRoles() {
		return new OutputPortRole[] { OutputPortRole.DISTRIBUTED };
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void reset() {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected DataTableSpec[] configure(final DataTableSpec[] inSpecs) throws InvalidSettingsException {
		EsriUtils.checkColumn("Latitude", latitudeColumm, inSpecs[0]);
		EsriUtils.checkColumn("Longitude", longitudeColumm, inSpecs[0]);
		EsriUtils.checkColumn("Polygon", polygonColumm, inSpecs[1]);
		EsriUtils.checkColumn("Polygon Latitude", polygonLatitudeColumm, inSpecs[2]);
		EsriUtils.checkColumn("Polygon Longitude", polygonLongitudeColumm, inSpecs[2]);

		return new DataTableSpec[] { inSpecs[0] };
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void saveSettingsTo(final NodeSettingsWO settings) {
		latitudeColumm.saveSettingsTo(settings);
		longitudeColumm.saveSettingsTo(settings);
		polygonColumm.saveSettingsTo(settings);
		polygonLatitudeColumm.saveSettingsTo(settings);
		polygonLongitudeColumm.saveSettingsTo(settings);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void loadValidatedSettingsFrom(final NodeSettingsRO settings) throws InvalidSettingsException {
		latitudeColumm.loadSettingsFrom(settings);
		longitudeColumm.loadSettingsFrom(settings);
		polygonColumm.loadSettingsFrom(settings);
		polygonLatitudeColumm.loadSettingsFrom(settings);
		polygonLongitudeColumm.loadSettingsFrom(settings);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void validateSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
		latitudeColumm.validateSettings(settings);
		longitudeColumm.validateSettings(settings);
		polygonColumm.validateSettings(settings);
		polygonLatitudeColumm.validateSettings(settings);
		polygonLongitudeColumm.validateSettings(settings);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void loadInternals(final File internDir, final ExecutionMonitor exec)
			throws IOException, CanceledExecutionException {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void saveInternals(final File internDir, final ExecutionMonitor exec)
			throws IOException, CanceledExecutionException {
	}

	private MultiPolygon createPolygon(BufferedDataTable polygonTable, BufferedDataTable polygonCoordinateTable) {
		Map<String, Coordinate> polygonCoordinateMap = new LinkedHashMap<>();

		for (DataRow row : polygonCoordinateTable) {
			DataCell latitudeCell = row
					.getCell(polygonCoordinateTable.getSpec().findColumnIndex(polygonLatitudeColumm.getStringValue()));
			DataCell longitudeCell = row
					.getCell(polygonCoordinateTable.getSpec().findColumnIndex(polygonLongitudeColumm.getStringValue()));

			if (latitudeCell instanceof DoubleValue && longitudeCell instanceof DoubleValue) {
				polygonCoordinateMap.put(row.getKey().getString(), new Coordinate(
						((DoubleValue) latitudeCell).getDoubleValue(), ((DoubleValue) longitudeCell).getDoubleValue()));
			}
		}

		List<Polygon> polygons = new ArrayList<>();

		for (DataRow row : polygonTable) {
			DataCell polygonCell = row.getCell(polygonTable.getSpec().findColumnIndex(polygonColumm.getStringValue()));
			List<Coordinate> coordinates = new ArrayList<>();

			if (polygonCell instanceof ListDataValue) {
				for (StringValue cell : Iterables.filter((ListDataValue) polygonCell, StringValue.class)) {
					Coordinate c = polygonCoordinateMap.get(cell.getStringValue());

					if (c != null) {
						coordinates.add(c);
					}
				}
			}

			try {
				polygons.add(EsriUtils.createPolygon(coordinates));
			} catch (IndexOutOfBoundsException | IllegalArgumentException e) {
				setWarningMessage("Invalid Polygon in " + row.getKey());
			}
		}

		return EsriUtils.GEO_FACTORY.createMultiPolygon(polygons.toArray(new Polygon[0]));
	}

	private boolean isRowInFilter(DataRow row, DataTableSpec spec, MultiPolygon polygon) {
		DataCell latitudeCell = row.getCell(spec.findColumnIndex(latitudeColumm.getStringValue()));
		DataCell longitudeCell = row.getCell(spec.findColumnIndex(longitudeColumm.getStringValue()));

		return latitudeCell instanceof DoubleValue && longitudeCell instanceof DoubleValue
				&& polygon.contains(
						EsriUtils.GEO_FACTORY.createPoint(new Coordinate(((DoubleValue) latitudeCell).getDoubleValue(),
								((DoubleValue) longitudeCell).getDoubleValue())));
	}
}
