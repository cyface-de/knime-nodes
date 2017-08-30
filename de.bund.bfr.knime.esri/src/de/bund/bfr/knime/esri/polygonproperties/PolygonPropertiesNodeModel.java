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
package de.bund.bfr.knime.esri.polygonproperties;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DataType;
import org.knime.core.data.DoubleValue;
import org.knime.core.data.StringValue;
import org.knime.core.data.collection.ListDataValue;
import org.knime.core.data.container.AbstractCellFactory;
import org.knime.core.data.container.ColumnRearranger;
import org.knime.core.data.def.DoubleCell;
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
import org.knime.core.node.streamable.StreamableOperator;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

import de.bund.bfr.knime.esri.EsriUtils;

/**
 * This is the model implementation of PolygonProperties.
 * 
 * 
 * @author Christian Thoens
 */
public class PolygonPropertiesNodeModel extends NodeModel {

	protected static final String CFG_POLYGON_COLUMN = "PolygonColumn";
	protected static final String CFG_LATITUDE_COLUMN = "LatitudeColumn";
	protected static final String CFG_LONGITUDE_COLUMN = "LongitudeColumn";

	private static final String CENTER_LATITUDE_COLUMN = "PolygonCenterLatitude";
	private static final String CENTER_LONGITUDE_COLUMN = "PolygonCenterLongitude";
	private static final String AREA_COLUMN = "PolygonArea";
	private static final ImmutableSet<String> NEW_COLUMNS = ImmutableSet.of(CENTER_LATITUDE_COLUMN,
			CENTER_LONGITUDE_COLUMN, AREA_COLUMN);

	private SettingsModelString polygonColumm;
	private SettingsModelString latitudeColumm;
	private SettingsModelString longitudeColumm;

	/**
	 * Constructor for the node model.
	 */
	protected PolygonPropertiesNodeModel() {
		super(2, 1);
		polygonColumm = new SettingsModelString(CFG_POLYGON_COLUMN, null);
		latitudeColumm = new SettingsModelString(CFG_LATITUDE_COLUMN, null);
		longitudeColumm = new SettingsModelString(CFG_LONGITUDE_COLUMN, null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected BufferedDataTable[] execute(final BufferedDataTable[] inData, final ExecutionContext exec)
			throws Exception {
		return new BufferedDataTable[] { exec.createColumnRearrangeTable(inData[0],
				createColumnRearranger(inData[0].getSpec(), inData[1]), exec) };
	}

	@Override
	public StreamableOperator createStreamableOperator(final PartitionInfo partitionInfo,
			final PortObjectSpec[] inSpecs) throws InvalidSettingsException {
		return new StreamableOperator() {

			@Override
			public void runFinal(final PortInput[] inputs, final PortOutput[] outputs, final ExecutionContext exec)
					throws Exception {
				ColumnRearranger rearranger = createColumnRearranger(((RowInput) inputs[0]).getDataTableSpec(),
						(BufferedDataTable) ((PortObjectInput) inputs[1]).getPortObject());

				rearranger.createStreamableFunction(0, 0).runFinal(inputs, outputs, exec);
			}
		};
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public InputPortRole[] getInputPortRoles() {
		return new InputPortRole[] { InputPortRole.DISTRIBUTED_STREAMABLE, InputPortRole.NONDISTRIBUTED_NONSTREAMABLE };
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
		return new DataTableSpec[] { createColumnRearranger(inSpecs[0], null).createSpec() };
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void saveSettingsTo(final NodeSettingsWO settings) {
		polygonColumm.saveSettingsTo(settings);
		latitudeColumm.saveSettingsTo(settings);
		longitudeColumm.saveSettingsTo(settings);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void loadValidatedSettingsFrom(final NodeSettingsRO settings) throws InvalidSettingsException {
		polygonColumm.loadSettingsFrom(settings);
		latitudeColumm.loadSettingsFrom(settings);
		longitudeColumm.loadSettingsFrom(settings);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void validateSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
		polygonColumm.validateSettings(settings);
		latitudeColumm.validateSettings(settings);
		longitudeColumm.validateSettings(settings);
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

	private ColumnRearranger createColumnRearranger(DataTableSpec spec, BufferedDataTable coordinateTable)
			throws InvalidSettingsException {
		for (String column : NEW_COLUMNS) {
			if (spec.containsName(column)) {
				throw new InvalidSettingsException("Column name \"" + column + "\" not allowed in first input table");
			}
		}

		EsriUtils.checkColumn("Polygon", polygonColumm, spec);

		Map<String, Coordinate> coordinateMap = new LinkedHashMap<>();

		if (coordinateTable != null) {
			EsriUtils.checkColumn("Latitude", latitudeColumm, coordinateTable.getSpec());
			EsriUtils.checkColumn("Longitude", longitudeColumm, coordinateTable.getSpec());

			for (DataRow row : coordinateTable) {
				DataCell latitudeCell = row
						.getCell(coordinateTable.getSpec().findColumnIndex(latitudeColumm.getStringValue()));
				DataCell longitudeCell = row
						.getCell(coordinateTable.getSpec().findColumnIndex(longitudeColumm.getStringValue()));

				if (latitudeCell instanceof DoubleValue && longitudeCell instanceof DoubleValue) {
					coordinateMap.put(row.getKey().getString(),
							new Coordinate(((DoubleValue) latitudeCell).getDoubleValue(),
									((DoubleValue) longitudeCell).getDoubleValue()));
				}
			}
		}

		DataColumnSpec[] newColumns = { new DataColumnSpecCreator(CENTER_LATITUDE_COLUMN, DoubleCell.TYPE).createSpec(),
				new DataColumnSpecCreator(CENTER_LONGITUDE_COLUMN, DoubleCell.TYPE).createSpec(),
				new DataColumnSpecCreator(AREA_COLUMN, DoubleCell.TYPE).createSpec() };
		DataTableSpec newSpec = new DataTableSpec(newColumns);
		ColumnRearranger rearranger = new ColumnRearranger(spec);

		rearranger.append(new AbstractCellFactory(newColumns) {

			@Override
			public DataCell[] getCells(DataRow row) {
				DataCell polygonCell = row.getCell(spec.findColumnIndex(polygonColumm.getStringValue()));
				List<Coordinate> coordinates = new ArrayList<>();

				if (polygonCell instanceof ListDataValue) {
					for (StringValue cell : Iterables.filter((ListDataValue) polygonCell, StringValue.class)) {
						Coordinate c = coordinateMap.get(cell.getStringValue());

						if (c != null) {
							coordinates.add(c);
						}
					}
				}

				DataCell[] cells = new DataCell[newSpec.getNumColumns()];

				try {
					Polygon poly = EsriUtils.createPolygon(coordinates);
					Point center = poly.getCentroid();

					cells[newSpec.findColumnIndex(CENTER_LATITUDE_COLUMN)] = new DoubleCell(center.getX());
					cells[newSpec.findColumnIndex(CENTER_LONGITUDE_COLUMN)] = new DoubleCell(center.getY());
					cells[newSpec.findColumnIndex(AREA_COLUMN)] = new DoubleCell(poly.getArea());
				} catch (IndexOutOfBoundsException | IllegalArgumentException e) {
					cells[newSpec.findColumnIndex(CENTER_LATITUDE_COLUMN)] = DataType.getMissingCell();
					cells[newSpec.findColumnIndex(CENTER_LONGITUDE_COLUMN)] = DataType.getMissingCell();
					cells[newSpec.findColumnIndex(AREA_COLUMN)] = DataType.getMissingCell();
					setWarningMessage("Invalid Polygon in " + row.getKey());
				}

				return cells;
			}
		});

		return rearranger;
	}
}
