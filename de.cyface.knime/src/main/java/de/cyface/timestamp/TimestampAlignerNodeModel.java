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

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DataType;
import org.knime.core.data.LongValue;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.defaultnodesettings.SettingsModelBoolean;
import org.knime.core.node.defaultnodesettings.SettingsModelString;

/**
 * This is the model implementation of TimestampAligner. Takes two tables with
 * timestamp columns and and assigns to each column from the first table the
 * column from the second table with the closest previous timestamp.
 *
 * @author Klemens Muthmann
 * @version 1.0.1
 * @since 1.0.0
 */
public class TimestampAlignerNodeModel extends NodeModel {

    // the logger instance
    private static final NodeLogger LOGGER = NodeLogger.getLogger(TimestampAlignerNodeModel.class);

    /**
     * The settings model containing the name of the timestamp column from the first table.
     */
    private final SettingsModelString firstTimestampColumnName = new SettingsModelString(CFGKEY_FIRST_TABLE_COLUMN_NAME,
            "");
    /**
     * The settings model containing the option to rename the timestamp column from the first table in the output table.
     */
    private final SettingsModelBoolean firstTimestampColumnRenameCheckbox = new SettingsModelBoolean(
            CFGKEY_RENAME_FIRST_TABLE_COLUMN_CHECKBOX,
            false);
    /**
     * The settings model containing the new name of the timestamp column from the first table, if the rename option was selected.
     */
    private final SettingsModelString firstTimestampColumnRenameName = new SettingsModelString(
            CFGKEY_RENAME_FIRST_TABLE_COLUMN_NAME,
            "");
    /**
     * The settings model containing the name of the timestamp column from the second table.
     */
    private final SettingsModelString secondTimestampColumnName = new SettingsModelString(
            CFGKEY_SECOND_TABLE_COLUMN_NAME, "");
    /**
     * The settings model containing the option to rename the timestamp column from the second table in the output table.
     */
    private final SettingsModelBoolean secondTimestampColumnRenameCheckbox = new SettingsModelBoolean(
            CFGKEY_RENAME_SECOND_TABLE_COLUMN_CHECKBOX,
            false);
    /**
     * The settings model containing the new name of the timestamp column from the second table, if the rename option was selected.
     */
    private final SettingsModelString secondTimestampColumnRenameName = new SettingsModelString(
            CFGKEY_RENAME_SECOND_TABLE_COLUMN_NAME,
            "");
    /**
     * The settings model containing the option to align the timestamp values to each other.
     */
    private final SettingsModelBoolean valueRangeAlignment = new SettingsModelBoolean(CFGKEY_VALUE_RANGE_ALIGNMENT_NAME,
            false);
    /**
     * The configuration key for the option to select the timestamp column from the first table.
     */
    static final String CFGKEY_FIRST_TABLE_COLUMN_NAME = "de.cyface.knime.firsttablecolumnname";
    /**
     * The configuration key for the option to rename the timestamp column from the first table.
     */
    static final String CFGKEY_RENAME_FIRST_TABLE_COLUMN_CHECKBOX = "de.cyface.knime.firsttablerenamecheckbox";
    /**
     * The configuration key for the new column name of the timestamp column from the first table, if the rename option has been selected.
     */
    static final String CFGKEY_RENAME_FIRST_TABLE_COLUMN_NAME = "de.cyface.knime.firsttablerename";
    /**
     * The configuration key for the option to select the timestamp column from the second table.
     */
    static final String CFGKEY_SECOND_TABLE_COLUMN_NAME = "de.cyface.knime.secondtablecolumnname";
    /**
     * The configuration key for the option to rename the timestamp column from the second table.
     */
    static final String CFGKEY_RENAME_SECOND_TABLE_COLUMN_CHECKBOX = "de.cyface.knime.secondtablerenamecheckbox";
    /**
     * The configuration key for the new column name of the timestamp column from the second table, if the rename option has been selected.
     */
    static final String CFGKEY_RENAME_SECOND_TABLE_COLUMN_NAME = "de.cyface.knime.secondtablerename";
    /**
     * The configuration key for the option to align timestamp value ranges.
     */
    static final String CFGKEY_VALUE_RANGE_ALIGNMENT_NAME = "de.cyface.knime.valuerangealignment";
    /**
     * The index of the first input port
     */
    static final int FIRST_IN_PORT = 0;
    /**
     * The index of the second input port
     */
    static final int SECOND_IN_PORT = 1;

    /**
     * Constructor for the node model.
     */
    protected TimestampAlignerNodeModel() {
        super(2, 1);
        firstTimestampColumnRenameCheckbox.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(final ChangeEvent e) {
                firstTimestampColumnRenameName.setEnabled(firstTimestampColumnRenameCheckbox.getBooleanValue());
            }
        });
        secondTimestampColumnRenameCheckbox.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(final ChangeEvent e) {
                secondTimestampColumnRenameName.setEnabled(secondTimestampColumnRenameCheckbox.getBooleanValue());
            }

        });
    }

    @Override
    protected BufferedDataTable[] execute(final BufferedDataTable[] inData, final ExecutionContext exec)
            throws Exception {
        try {
            final BufferedDataTable firstTable = inData[FIRST_IN_PORT];
            final BufferedDataTable secondTable = inData[SECOND_IN_PORT];
            final InputData input = new InputData(firstTable, secondTable, firstTimestampColumnName.getStringValue(),
                    secondTimestampColumnName.getStringValue());
            if (valueRangeAlignment.getBooleanValue()) {
                return new ExecutionWithAlignment().execute(input,
                        createResultSpec(firstTable.getDataTableSpec(), secondTable.getDataTableSpec()), exec);
            } else {
                return new ExecutionWithoutAlignment().execute(input,
                        createResultSpec(firstTable.getDataTableSpec(), secondTable.getDataTableSpec()), exec);
            }

        } catch (final Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    protected void reset() {
        // TODO Code executed on reset.
        // Models build during execute are cleared here.
        // Also data handled in load/saveInternals will be erased here.
    }

    @Override
    protected DataTableSpec[] configure(final DataTableSpec[] inSpecs) throws InvalidSettingsException {
        DataTableSpec firstTable = inSpecs[0];
        DataTableSpec secondTable = inSpecs[1];

        checkForValidTimestampColumn(firstTable, firstTimestampColumnName.getStringValue());
        checkForValidTimestampColumn(secondTable, secondTimestampColumnName.getStringValue());

        return new DataTableSpec[] {createResultSpec(firstTable, secondTable)};
    }

    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) {
        firstTimestampColumnName.saveSettingsTo(settings);
        secondTimestampColumnName.saveSettingsTo(settings);
        valueRangeAlignment.saveSettingsTo(settings);
        firstTimestampColumnRenameCheckbox.saveSettingsTo(settings);
        firstTimestampColumnRenameName.saveSettingsTo(settings);
        secondTimestampColumnRenameCheckbox.saveSettingsTo(settings);
        secondTimestampColumnRenameName.saveSettingsTo(settings);
    }

    @Override
    protected void loadValidatedSettingsFrom(final NodeSettingsRO settings) throws InvalidSettingsException {
        firstTimestampColumnName.loadSettingsFrom(settings);
        secondTimestampColumnName.loadSettingsFrom(settings);
        valueRangeAlignment.loadSettingsFrom(settings);
        firstTimestampColumnRenameCheckbox.loadSettingsFrom(settings);
        firstTimestampColumnRenameName.loadSettingsFrom(settings);
        secondTimestampColumnRenameCheckbox.loadSettingsFrom(settings);
        secondTimestampColumnRenameName.loadSettingsFrom(settings);
    }

    @Override
    protected void validateSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        firstTimestampColumnName.validateSettings(settings);
        secondTimestampColumnName.validateSettings(settings);
        valueRangeAlignment.validateSettings(settings);
        firstTimestampColumnRenameCheckbox.validateSettings(settings);
        firstTimestampColumnRenameName.validateSettings(settings);
        secondTimestampColumnRenameCheckbox.validateSettings(settings);
        secondTimestampColumnRenameName.validateSettings(settings);
    }

    @Override
    protected void loadInternals(final File internDir, final ExecutionMonitor exec)
            throws IOException, CanceledExecutionException {
        // Nothing to do here.
    }

    @Override
    protected void saveInternals(final File internDir, final ExecutionMonitor exec)
            throws IOException, CanceledExecutionException {
        // Nothing to do here.
    }

    /**
     * Creates the result table specification from the two input table specifications.
     * 
     * @param firstTableSpec The first input table specification.
     * @param secondTableSpec The second input table specification.
     * @return The result table specification.
     */
    private DataTableSpec createResultSpec(final DataTableSpec firstTableSpec, final DataTableSpec secondTableSpec) {
        DataColumnSpec[] colSpecs = new DataColumnSpec[firstTableSpec.getNumColumns()
                + secondTableSpec.getNumColumns()];

        Map<String, Integer> names = new HashMap<>();

        int i = 0;
        while (i < firstTableSpec.getNumColumns()) {
            colSpecs[i] = getColumnSpec(firstTableSpec, i, names);
            i++;
        }
        while (i < firstTableSpec.getNumColumns() + secondTableSpec.getNumColumns()) {
            colSpecs[i] = getColumnSpec(secondTableSpec, i - firstTableSpec.getNumColumns(), names);
            i++;
        }
        renameOutputColumnsIfNecessary(colSpecs, firstTableSpec, secondTableSpec);

        return new DataTableSpec(colSpecs);
    }

    /**
     * Renames the output columns if requested by the user.
     * 
     * @param colSpecs The column specifications for the result table.
     * @param firstTableSpec The table specification of the first input table.
     * @param secondTableSpec The table specification of the second input table
     */
    private void renameOutputColumnsIfNecessary(final DataColumnSpec[] colSpecs, final DataTableSpec firstTableSpec,
            final DataTableSpec secondTableSpec) {
        int offset = 0;
        if (firstTimestampColumnRenameCheckbox.getBooleanValue()) {
            offset = renameColumn(firstTableSpec, firstTimestampColumnName.getStringValue(),
                    firstTimestampColumnRenameName.getStringValue(), colSpecs, offset);
        }
        if (secondTimestampColumnRenameCheckbox.getBooleanValue()) {
            offset = renameColumn(secondTableSpec, secondTimestampColumnName.getStringValue(),
                    secondTimestampColumnRenameName.getStringValue(), colSpecs, offset);
        }
    }

    /**
     * Renames a column in a table specification.
     * 
     * @param tableSpec The table specification with the column to rename.
     * @param originalColumnName The original name of the column to rename.
     * @param renamedColumnName The new name of the column to rename.
     * @param colSpecs The column specifications of the table containing the column to rename.
     * @param offset The offset of columns before the columns of the input table spec starts.
     * @return The number of columns in the table specification.
     */
    private int renameColumn(final DataTableSpec tableSpec, final String originalColumnName,
            final String renamedColumnName, final DataColumnSpec[] colSpecs, final int offset) {
        int indexOfFirstTimestampColumnInFirstInputSpec = offset + tableSpec.findColumnIndex(originalColumnName);
        DataType type = colSpecs[indexOfFirstTimestampColumnInFirstInputSpec].getType();
        colSpecs[indexOfFirstTimestampColumnInFirstInputSpec] = new DataColumnSpecCreator(renamedColumnName, type)
                .createSpec();
        return tableSpec.getNumColumns();
    }

    /**
     * Checks if the provided column in the table is a processable timestamp column.
     * 
     * @param tableSpec The table specification containing the timestamp column.
     * @param columnName The name of the timestamp column to check.
     * @throws InvalidSettingsException If the provided column is no valid timestamp column.
     */
    private void checkForValidTimestampColumn(final DataTableSpec tableSpec, final String columnName)
            throws InvalidSettingsException {
        if (!tableSpec.containsName(columnName)
                || !tableSpec.getColumnSpec(columnName).getType().isCompatible(LongValue.class)) {
            throw new InvalidSettingsException(
                    String.format("No valid timestamp column called %s in table %s", columnName, tableSpec.getName()));
        }
    }

    /**
     * Provides a duplicate free column specification.
     * 
     * @param tableSpec The table spec to check the column in.
     * @param columnSpecIndex The index of the column.
     * @param names The already used names together with their index.
     * @return A new valid <code>DataColumnSpec</code>.
     */
    private DataColumnSpec getColumnSpec(final DataTableSpec tableSpec, final int columnSpecIndex,
            final Map<String, Integer> names) {
        DataColumnSpec columnSpec = tableSpec.getColumnSpec(columnSpecIndex);
        Integer counter = names.get(columnSpec.getName());
        if (counter == null) {
            counter = 1;
        } else {
            String qualifiedName = "" + counter + "_" + columnSpec.getName();
            DataColumnSpec qualifiedSpec = new DataColumnSpecCreator(qualifiedName, columnSpec.getType()).createSpec();
            columnSpec = qualifiedSpec;
            counter++;
        }
        names.put(columnSpec.getName(), counter);
        return columnSpec;
    }
}