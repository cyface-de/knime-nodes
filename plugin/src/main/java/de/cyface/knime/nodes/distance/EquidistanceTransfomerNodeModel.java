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
 */package de.cyface.knime.nodes.distance;

import java.io.File;
import java.io.IOException;

import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DoubleValue;
import org.knime.core.data.IntValue;
import org.knime.core.data.LongValue;
import org.knime.core.data.container.CloseableRowIterator;
import org.knime.core.data.date.DateAndTimeCell;
import org.knime.core.data.date.DateAndTimeValue;
import org.knime.core.data.def.DefaultRow;
import org.knime.core.data.def.DoubleCell;
import org.knime.core.data.def.IntCell;
import org.knime.core.data.def.LongCell;
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
import org.knime.core.node.defaultnodesettings.SettingsModelDouble;
import org.knime.core.node.defaultnodesettings.SettingsModelString;

/**
 * This is the model implementation of EquidistanceTransfomer. It takes a function
 * f(x) defined by two columns containing the values for x and for f(x) and
 * calculates a new function on x where f(x) are equdistant points which are
 * interpolated between original values of f(x) if f(x) is not equidistant
 * itself. The columns containing x and f(x) need to be numeric and sorted by x.
 *
 * @author Klemens Muthmann
 * @version 1.0.1
 * @since 2.0.0
 */
public class EquidistanceTransfomerNodeModel extends NodeModel {

    /**
     * The logger used for objects of this class.
     */
    private static final NodeLogger LOGGER = NodeLogger.getLogger(EquidistanceTransfomerNodeModel.class);

    /**
     * The key identifying the setting used to set the distance to use for equidistance calculation.
     */
    static final String CFGKEY_DISTANCE = "de.cyface.cfgkey.distance";
    /**
     * The key identifying the setting used to select the X input column.
     */
    static final String CFGKEY_X_COLUMN = "de.cyface.cfgkey.xcolumn";
    /**
     * The key identifying the setting used to select the f(x) input column.
     */
    static final String CFGKEY_FX_COLUMN = "de.cyface.cfgkey.fxcolumn";
    /**
     * The key identifying the setting used to set the new name for the output X values column.
     */
    static final String CFGKEY_NEW_X_COLUMN = "de.cyface.cfgkey.newxcolumn";
    /**
     * The key identifying the setting used to set the new name for the output f(x) values column.
     */
    static final String CFGKEY_NEW_FX_COLUMN = "de.cyface.cfgkey.newfxcolumn";

    /** initial default count value. */
    static final int DEFAULT_COUNT = 100;

    /**
     * The index of the input port to read the data from.
     */
    static final int IN_PORT = 0;

    /**
     * <code>SettingsModel</code> used to store the distance to use between points.
     */
    private final SettingsModelDouble distanceSettings = new SettingsModelDouble(CFGKEY_DISTANCE, 1.0);
    /**
     * <code>SettingsModel</code> used to store the column to read input x values from.
     */
    private final SettingsModelString xColumnSettings = new SettingsModelString(CFGKEY_X_COLUMN, "");
    /**
     * <code>SettingsModel</code> used to store the column to read input f(x) values from.
     */
    private final SettingsModelString fxColumnSettings = new SettingsModelString(CFGKEY_FX_COLUMN, "");
    /**
     * <code>SettingsModel</code> used to store the column to write output x values to.
     */
    private final SettingsModelString newColumnXSettings = new SettingsModelString(CFGKEY_NEW_X_COLUMN, "");
    /**
     * <code>SettingsModel</code> used to store the column to write output f(x) values to.
     */
    private final SettingsModelString newColumFxSettings = new SettingsModelString(CFGKEY_NEW_FX_COLUMN, "");

    /**
     * Constructor for the node model.
     */
    protected EquidistanceTransfomerNodeModel() {
        super(1, 1);
    }

    @Override
    protected BufferedDataTable[] execute(final BufferedDataTable[] inData, final ExecutionContext exec)
            throws Exception {

        DataTableSpec inputTableSpecification = inData[IN_PORT].getDataTableSpec();
        int xColumnIndex = inputTableSpecification.findColumnIndex(xColumnSettings.getStringValue());
        int fxColumnIndex = inputTableSpecification.findColumnIndex(fxColumnSettings.getStringValue());
        DataTableSpec outputTableSpecification = createOutputTableSpecification(inputTableSpecification);
        BufferedDataContainer container = exec.createDataContainer(outputTableSpecification);
        double sampleRate = distanceSettings.getDoubleValue();
        try (CloseableRowIterator rowIter = inData[IN_PORT].iterator();) {
            if (!rowIter.hasNext()) {
                return new BufferedDataTable[] {container.getTable()};
            }
            DataRow row = rowIter.next();
            double i = 1.0;
            long currentRowIndex = 0L;
            double x = get(row, xColumnIndex);
            double fx = get(row, fxColumnIndex);
            container.addRowToTable(new DefaultRow("Row_" + currentRowIndex++, new DoubleCell(x), new DoubleCell(fx)));
            double nextInterpolatedX = x + sampleRate;
            while (rowIter.hasNext()) {
                exec.checkCanceled();
                exec.setProgress(i/inData[IN_PORT].size());
                i += 1.0;

                row = rowIter.next();
                double prevX = x;
                double prevFx = fx;
                x = get(row, xColumnIndex);
                fx = get(row, fxColumnIndex);

                while (x >= nextInterpolatedX) {
                    double interpolatedFx = linearlyInterpolate(prevX, x, prevFx, fx, nextInterpolatedX);
                    row = new DefaultRow("Row_" + currentRowIndex++, new DoubleCell(nextInterpolatedX),
                            new DoubleCell(interpolatedFx));
                    container.addRowToTable(row);
                    nextInterpolatedX += sampleRate;
                }

            }
        } finally {
            if (container != null && container.isOpen()) {
                container.close();
            }
        }

        return new BufferedDataTable[] {container.getTable()};
    }

    /**
     * Provides the linearly interpolated value between the two points <tt>(prevX, prevFx)</tt> and
     * <tt>(nextX, nextFx)</tt>.
     * 
     * @param prevX The x value of the earlier point.
     * @param nextX The x value of the later point.
     * @param prevFx The f(x) value of the earlier point.
     * @param nextFx The f(x) value of the later point.
     * @param interpolationPoint The distance between the two points, where the linear interpolation should happen.
     * @return Considering <code>interpolationPoint</code> is an f(x) value on the linear function formed by the two
     *         input points, the return value is the x value belonging to that f(x).
     */
    private double linearlyInterpolate(final double prevX, final double nextX, final double prevFx, final double nextFx,
            final double interpolationPoint) {
        return prevFx + ((nextFx - prevFx) * (interpolationPoint - prevX) / (nextX - prevX));
    }

    /**
     * Transforms every valid input to a double value for further processing.
     * Longs and integers are just parsed, while dates are intepreted as longs
     * in UTC milliseconds and then transformed to a double.
     * 
     * @param row
     *            The row to get the value from.
     * @param index
     *            The index of the column to get the value from.
     * @return The value from the cell identified by row and column as a double
     *         value.
     */
    private double get(final DataRow row, final int index) {
        final DataCell cell = row.getCell(index);
        if (cell.getType().isCompatible(DoubleValue.class)) {
            return ((DoubleCell)cell).getDoubleValue();
        } else if (cell.getType().isCompatible(IntValue.class)) {
            return Integer.valueOf(((IntCell)cell).getIntValue()).doubleValue();
        } else if (cell.getType().isCompatible(LongValue.class)) {
            return Long.valueOf(((LongCell)cell).getLongValue()).doubleValue();
        } else if (cell.getType().isCompatible(DateAndTimeValue.class)) {
            return Long.valueOf(((DateAndTimeCell)cell).getUTCTimeInMillis()).doubleValue();
        } else {
            throw new IllegalStateException(String.format("Incompatible Type %s in Row %s and Column %d.",
                    cell.getType().getName(), row.getKey().getString(), index));
        }
    }

    @Override
    protected void reset() {
        // Nothing to do here.
    }

    @Override
    protected DataTableSpec[] configure(final DataTableSpec[] inSpecs) throws InvalidSettingsException {
        validateInSpec(inSpecs[IN_PORT]);
        return new DataTableSpec[] {createOutputTableSpecification(inSpecs[IN_PORT])};
    }

    /**
     * Validates the provided input specification for compatiblity with this node.
     * To be compatible it requires at least one numeric or time column.
     * 
     * @param inSpec The input specification to validate.
     * @throws InvalidSettingsException If validation was not successful.
     */
    private void validateInSpec(final DataTableSpec inSpec) throws InvalidSettingsException {
        if (!(inSpec.containsCompatibleType(DateAndTimeValue.class) || inSpec.containsCompatibleType(LongValue.class)
                || inSpec.containsCompatibleType(IntValue.class) || inSpec.containsCompatibleType(DoubleValue.class))) {
            throw new InvalidSettingsException("No valid numeric or time column in input table.");
        }
    }

    /**
     * Creates the KNIME table specification for the output table of the equidistance transformer node.
     * 
     * @param inSpec The specification of the input table as a basis to create the output specification on.
     * @return The table specification for the output table.
     */
    private DataTableSpec createOutputTableSpecification(final DataTableSpec inSpec) {
        final DataColumnSpec newXValuesColumnSpec = new DataColumnSpecCreator(newColumnXSettings.getStringValue(),
                DoubleCell.TYPE).createSpec();
        final DataColumnSpec newFxValuesColumnSpec = new DataColumnSpecCreator(newColumFxSettings.getStringValue(),
                DoubleCell.TYPE).createSpec();
        return new DataTableSpec(newXValuesColumnSpec, newFxValuesColumnSpec);

    }

    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) {
        distanceSettings.saveSettingsTo(settings);
        xColumnSettings.saveSettingsTo(settings);
        fxColumnSettings.saveSettingsTo(settings);
        newColumnXSettings.saveSettingsTo(settings);
        newColumFxSettings.saveSettingsTo(settings);

    }

    @Override
    protected void loadValidatedSettingsFrom(final NodeSettingsRO settings) throws InvalidSettingsException {
        distanceSettings.loadSettingsFrom(settings);
        xColumnSettings.loadSettingsFrom(settings);
        fxColumnSettings.loadSettingsFrom(settings);
        newColumnXSettings.loadSettingsFrom(settings);
        newColumFxSettings.loadSettingsFrom(settings);
    }

    @Override
    protected void validateSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        distanceSettings.validateSettings(settings);
        xColumnSettings.validateSettings(settings);
        fxColumnSettings.validateSettings(settings);
        newColumnXSettings.validateSettings(settings);
        newColumFxSettings.validateSettings(settings);
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

}
