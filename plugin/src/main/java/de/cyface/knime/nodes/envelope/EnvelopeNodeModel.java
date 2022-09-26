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
package de.cyface.knime.nodes.envelope;

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

import de.cyface.knime.dialog.ColumnSelectionNodeOption;
import de.cyface.knime.dialog.NodeOption;
import de.cyface.knime.nodes.timestamp.TimestampAlignerNodeModel;

/**
 * The Node model validating the input and executing the envelope function algorithm on the data.
 * This is the heart of the implementation of this node.
 *
 * @author Klemens Muthmann
 * @version 2.0.0
 * @since 1.2.0
 */
public final class EnvelopeNodeModel extends NodeModel {

    /**
     * The logger used by objects of this class.
     */
    private static final NodeLogger LOGGER = NodeLogger.getLogger(TimestampAlignerNodeModel.class);

    /**
     * The settings model storing which input column to use to calculate the envelope function for.
     */
    private final SettingsModelString inputColumnSelectionModel;

    /**
     * Create a new completely initialized <code>NodeModel</code> with one input and one output.
     *
     * @param inputColumnSelection A {@link NodeOption} for input column selection.
     */
    public EnvelopeNodeModel(final ColumnSelectionNodeOption inputColumnSelection) {
        super(1, 1);
        inputColumnSelectionModel = inputColumnSelection.getSettingsModel();
    }

    @Override
    protected void loadInternals(final File nodeInternDir, final ExecutionMonitor exec)
            throws IOException, CanceledExecutionException {
        // Nothing to do here!

    }

    @Override
    protected void saveInternals(final File nodeInternDir, final ExecutionMonitor exec)
            throws IOException, CanceledExecutionException {
        // Nothing to do here!

    }

    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) {
        inputColumnSelectionModel.saveSettingsTo(settings);

    }

    @Override
    protected void validateSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        inputColumnSelectionModel.validateSettings(settings);

    }

    @Override
    protected void loadValidatedSettingsFrom(final NodeSettingsRO settings) throws InvalidSettingsException {
        inputColumnSelectionModel.loadSettingsFrom(settings);

    }

    @Override
    protected void reset() {
        // Nothing to do here!
    }

    @Override
    protected BufferedDataTable[] execute(final BufferedDataTable[] inData, final ExecutionContext exec)
            throws Exception {
        BufferedDataTable inputTable = inData[0];
        BufferedDataContainer outputContainer = exec
                .createDataContainer(createOutputSpec(inputTable.getDataTableSpec()));
        DataTableSpec inputSpecification = inputTable.getSpec();
        int inputColumnIndex = inputSpecification.findColumnIndex(inputColumnSelectionModel.getStringValue());

        DataRow previousRow = null;
        DataRow currentRow = null;
        DataRow nextRow = null;
        double itemsProcessed = .0;

        for (DataRow row : inputTable) {
            exec.checkCanceled();
            exec.setProgress(itemsProcessed / inputTable.size());
            itemsProcessed += 1.0;

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

                double previousValue = ((DoubleCell)previousCell).getDoubleValue();
                double currentValue = ((DoubleCell)currentCell).getDoubleValue();
                double nextValue = ((DoubleCell)nextCell).getDoubleValue();

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

        return new BufferedDataTable[] {outputContainer.getTable()};
    }

    @Override
    protected DataTableSpec[] configure(final DataTableSpec[] inSpecs) throws InvalidSettingsException {
        return new DataTableSpec[] {createOutputSpec(inSpecs[0])};
    }

    /**
     * Creates the KNIME table specification for the output table based on the input spcification.
     *
     * @param inSpec The input table specification.
     * @return A table specification for the envelope node output table.
     */
    private DataTableSpec createOutputSpec(final DataTableSpec inSpec) {
        return inSpec;
    }

}
