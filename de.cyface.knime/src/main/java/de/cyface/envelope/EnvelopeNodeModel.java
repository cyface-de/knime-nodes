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
package de.cyface.envelope;

import java.io.File;
import java.io.IOException;

import org.knime.core.data.DataCell;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DoubleValue;
import org.knime.core.data.def.DefaultRow;
import org.knime.core.data.def.DoubleCell;
import org.knime.core.node.BufferedDataContainer;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.defaultnodesettings.SettingsModelString;

import de.cyface.smoothing.dialog.ColumnSelectionNodeOption;
import de.cyface.timestamp.TimestampAlignerNodeModel;

/**
 * 
 * @author Klemens Muthmann
 * @version 2.0.0
 * @since 1.2.0
 */
public class EnvelopeNodeModel extends NodeModel {

	private static final NodeLogger LOGGER = NodeLogger.getLogger(TimestampAlignerNodeModel.class);

	private final SettingsModelString inputColumnSelectionModel;

	public EnvelopeNodeModel(final ColumnSelectionNodeOption inputColumnSelection) {
		super(1, 1);
		inputColumnSelectionModel = inputColumnSelection.getSettingsModel();
	}

	@Override
	protected void loadInternals(File nodeInternDir, ExecutionMonitor exec)
			throws IOException, CanceledExecutionException {
		// Nothing to do here!

	}

	@Override
	protected void saveInternals(File nodeInternDir, ExecutionMonitor exec)
			throws IOException, CanceledExecutionException {
		// Nothing to do here!

	}

	@Override
	protected void saveSettingsTo(NodeSettingsWO settings) {
		inputColumnSelectionModel.saveSettingsTo(settings);

	}

	@Override
	protected void validateSettings(NodeSettingsRO settings) throws InvalidSettingsException {
		inputColumnSelectionModel.validateSettings(settings);

	}

	@Override
	protected void loadValidatedSettingsFrom(NodeSettingsRO settings) throws InvalidSettingsException {
		inputColumnSelectionModel.loadSettingsFrom(settings);

	}

	@Override
	protected void reset() {
		// Nothing to do here!
	}

	@Override
	protected BufferedDataTable[] execute(BufferedDataTable[] inData, ExecutionContext exec) throws Exception {
		BufferedDataTable inputTable = inData[0];
		BufferedDataContainer outputContainer = exec
				.createDataContainer(createOutputSpec(inputTable.getDataTableSpec()));
		DataTableSpec inputSpecification = inputTable.getSpec();
		int inputColumnIndex = inputSpecification.findColumnIndex(inputColumnSelectionModel.getStringValue());

		DataRow previousRow = null;
		DataRow currentRow = null;
		DataRow nextRow = null;

		for (DataRow row : inputTable) {
			previousRow = currentRow;
			currentRow = nextRow;
			nextRow = row;

			if (previousRow != null && currentRow != null) { // Always null on first iteration.
				// TODO create for long and int tables.
				DataCell previousCell = previousRow.getCell(inputColumnIndex);
				DataCell currentCell = currentRow.getCell(inputColumnIndex);
				DataCell nextCell = nextRow.getCell(inputColumnIndex);

				if (previousCell.isMissing() || currentCell.isMissing() || nextCell.isMissing()) {
					continue;
				}

				double previousValue = ((DoubleCell) previousCell).getDoubleValue();
				double currentValue = ((DoubleCell) currentCell).getDoubleValue();
				double nextValue = ((DoubleCell) nextCell).getDoubleValue();

				if (previousValue <= currentValue && nextValue <= currentValue) {
					DataCell[] cells = new DataCell[inputSpecification.getNumColumns()];
					for (int i = 0; i < inputSpecification.getNumColumns(); i++) {
						cells[i] = currentRow.getCell(i);
					}
					DataRow resultRow = new DefaultRow(currentRow.getKey(), cells);
					outputContainer.addRowToTable(resultRow);
				}
			}
		}
		if (outputContainer.size() == 0) {
			LOGGER.warn(
					"It seems your input column did not contain any valid data. Maybe there are only missing cells or no maximums.");
		}
		outputContainer.close();

		return new BufferedDataTable[] { outputContainer.getTable() };
	}

	@Override
	protected DataTableSpec[] configure(DataTableSpec[] inSpecs) throws InvalidSettingsException {
		return new DataTableSpec[] { createOutputSpec(inSpecs[0]) };
	}

	private DataTableSpec createOutputSpec(final DataTableSpec inSpec) {
		return inSpec;
	}

}
