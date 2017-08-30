package de.cyface.distance;

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
 * <p>
 * This is the model implementation of EquidistanceTransfomer. Takes a function
 * f(x) defined by two columns containing the values for x and for f(x) and
 * calculate a new function on x where f(x) are equdistant points which are
 * interpolated between original values of f(x) if f(x) is not equidistant
 * itself. The columns containing x and f(x) need to be numeric and sorted by x.
 * </p>
 *
 * @author Klemens Muthmann
 * @version 1.0
 * @since 2.0.0
 */
public class EquidistanceTransfomerNodeModel extends NodeModel {

	// the logger instance
	private static final NodeLogger logger = NodeLogger.getLogger(EquidistanceTransfomerNodeModel.class);

	// /** the settings key which is used to retrieve and
	// store the settings (from the dialog or from a settings file)
	// (package visibility to be usable from the dialog). */
	// static final String CFGKEY_COUNT = "Count";
	static final String CFGKEY_DISTANCE = "de.cyface.cfgkey.distance";
	static final String CFGKEY_X_COLUMN = "de.cyface.cfgkey.xcolumn";
	static final String CFGKEY_FX_COLUMN = "de.cyface.cfgkey.fxcolumn";
	static final String CFGKEY_NEW_X_COLUMN = "de.cyface.cfgkey.newxcolumn";
	static final String CFGKEY_NEW_FX_COLUMN = "de.cyface.cfgkey.newfxcolumn";

	/** initial default count value. */
	static final int DEFAULT_COUNT = 100;

	static final int IN_PORT = 0;

	// // example value: the models count variable filled from the dialog
	// // and used in the models execution method. The default components of the
	// // dialog work with "SettingsModels".
	// private final SettingsModelIntegerBounded m_count =
	// new
	// SettingsModelIntegerBounded(EquidistanceTransfomerNodeModel.CFGKEY_COUNT,
	// EquidistanceTransfomerNodeModel.DEFAULT_COUNT,
	// Integer.MIN_VALUE, Integer.MAX_VALUE);
	private final SettingsModelDouble distanceSettings = new SettingsModelDouble(CFGKEY_DISTANCE, 1.0);
	private final SettingsModelString xColumnSettings = new SettingsModelString(CFGKEY_X_COLUMN, "");
	private final SettingsModelString fxColumnSettings = new SettingsModelString(CFGKEY_FX_COLUMN, "");
	private final SettingsModelString newColumnXSettings = new SettingsModelString(CFGKEY_NEW_X_COLUMN, "");
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
				return new BufferedDataTable[] { container.getTable() };
			}
			DataRow row = rowIter.next();
			long currentRowIndex = 0L;
			double x = get(row, xColumnIndex);
			double fx = get(row, fxColumnIndex);
			container.addRowToTable(new DefaultRow("Row_" + currentRowIndex++, new DoubleCell(x), new DoubleCell(fx)));
			double nextInterpolatedX = x + sampleRate;
			while (rowIter.hasNext()) {
				
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

		// DataRow prevRow = rowIter.next();
		// double remainder = 0.0;
		// long currentRowIndex = 0L;
		// container.addRowToTable(new DefaultRow("Row_" + currentRowIndex++,
		// new DoubleCell(get(prevRow, xColumnIndex)), new
		// DoubleCell(get(prevRow, fxColumnIndex))));
		// while (rowIter.hasNext()) {
		// DataRow currentRow = rowIter.next();
		// double prevX = get(prevRow, xColumnIndex);
		// double prevFx = get(prevRow, fxColumnIndex);
		// double currentX = get(currentRow, xColumnIndex);
		// double currentFx = get(currentRow, fxColumnIndex);
		// double samplesInInterval = (currentX - prevX + sampleRate *
		// remainder) / sampleRate;
		// for (int i = 1; i <= Math.floor(samplesInInterval); i++) {
		// // interpolate
		// double interpolatedX = prevX + i * (sampleRate - (sampleRate *
		// remainder));
		// double interpolatedValue = prevFx
		// + (currentFx - prevFx) / (currentX - prevX) * i * (sampleRate -
		// (sampleRate * remainder));
		// container.addRowToTable(new DefaultRow("Row_" + currentRowIndex++,
		// new DoubleCell(interpolatedX),
		// new DoubleCell(interpolatedValue)));
		//// remainder = 0.0;
		// }
		// remainder = samplesInInterval - Math.floor(samplesInInterval);
		// prevRow = currentRow;
		// }
		// } finally {
		// if(container!=null && container.isOpen()) {
		// container.close();
		// }
		// }
		return new BufferedDataTable[] { container.getTable() };
	}

	private double linearlyInterpolate(final double prevX, final double nextX, final double prevFx, final double nextFx,
			final double interpolationPoint) {
		return prevFx + ((nextFx - prevFx) * (interpolationPoint - prevX) / (nextX - prevX));
	}

	/**
	 * <p>
	 * Transforms every valid input to a double value for further processing.
	 * Longs and integers are just parsed, while dates are intepreted as longs
	 * in UTC milliseconds and then transformed to a double.
	 * </p>
	 * 
	 * @param row
	 *            The row to get the value from.
	 * @param index
	 *            The index of the column to get the value from.
	 * @return The value from the cell identified by row and column as a double
	 *         value.
	 */
	private double get(final DataRow row, final int index) {
		DataCell cell = row.getCell(index);
		if (cell.getType().isCompatible(DoubleValue.class)) {
			return ((DoubleCell) cell).getDoubleValue();
		} else if (cell.getType().isCompatible(IntValue.class)) {
			return Integer.valueOf(((IntCell) cell).getIntValue()).doubleValue();
		} else if (cell.getType().isCompatible(LongValue.class)) {
			return Long.valueOf(((LongCell) cell).getLongValue()).doubleValue();
		} else if (cell.getType().isCompatible(DateAndTimeValue.class)) {
			return Long.valueOf(((DateAndTimeCell) cell).getUTCTimeInMillis()).doubleValue();
		} else {
			throw new IllegalStateException(String.format("Incompatible Type %s in Row %s and Column %d.",
					cell.getType().getName(), row.getKey().getString(), index));
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
		validateInSpec(inSpecs[IN_PORT]);
		return new DataTableSpec[] { createOutputTableSpecification(inSpecs[IN_PORT]) };
	}

	private void validateInSpec(DataTableSpec inSpec) throws InvalidSettingsException {
		if (!(inSpec.containsCompatibleType(DateAndTimeValue.class) || inSpec.containsCompatibleType(LongValue.class)
				|| inSpec.containsCompatibleType(IntValue.class) || inSpec.containsCompatibleType(DoubleValue.class))) {
			throw new InvalidSettingsException("No valid numeric or time column in input table.");
		}
	}

	private DataTableSpec createOutputTableSpecification(final DataTableSpec inSpec) {
		DataColumnSpec newXValuesColumnSpec = new DataColumnSpecCreator(newColumnXSettings.getStringValue(),
				DoubleCell.TYPE).createSpec();
		DataColumnSpec newFxValuesColumnSpec = new DataColumnSpecCreator(newColumFxSettings.getStringValue(),
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

		// TODO load internal data.
		// Everything handed to output ports is loaded automatically (data
		// returned by the execute method, models loaded in loadModelContent,
		// and user settings set through loadSettingsFrom - is all taken care
		// of). Load here only the other internals that need to be restored
		// (e.g. data used by the views).

	}

	@Override
	protected void saveInternals(final File internDir, final ExecutionMonitor exec)
			throws IOException, CanceledExecutionException {

		// TODO save internal models.
		// Everything written to output ports is saved automatically (data
		// returned by the execute method, models saved in the saveModelContent,
		// and user settings saved through saveSettingsTo - is all taken care
		// of). Save here only the other internals that need to be preserved
		// (e.g. data used by the views).

	}

}
