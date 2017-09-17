/**
 * 
 */
package de.cyface.smoothing;

import java.io.File;
import java.io.IOException;

import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DoubleValue;
import org.knime.core.data.def.DoubleCell;
import org.knime.core.node.BufferedDataContainer;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.defaultnodesettings.SettingsModel;
import org.knime.core.node.defaultnodesettings.SettingsModelString;

/**
 * <p>
 * A {@link NodeModel} for the smoothing node, which smoothes an input signal,
 * writing the result to an output signal.
 * </p>
 * 
 * @author Klemens Muthmann
 * @version 1.0.0
 * @since 1.0.0
 */
public class SmoothingNodeModel extends NodeModel {

	/**
	 * <p>
	 * The executor used to either replace the input data or append results.
	 * </p>
	 */
	private Execution executor;

	/**
	 * <p>
	 * Settings model with the type of filter to use for smoothing.
	 * </p>
	 */
	private final SettingsModelString filterTypeSelectionSettingsModel;
	/**
	 * <p>
	 * {@link SettingsModel} containing the name of the input column containing
	 * the input signal.
	 * </p>
	 */
	private final SettingsModelString inputColSelectionSettingsModel;
	/**
	 * <p>
	 * {@link SettingsModel} with the selection on whether to append the result
	 * signal to the input table or replace the input signal column.
	 * </p>
	 */
	private final SettingsModelString appendReplaceChooserSettingsModel;
	/**
	 * <p>
	 * The name of the column to append if the
	 * {@link #appendColumnNameInputSettingsModel} is set to append.
	 * </p>
	 */
	private final SettingsModelString appendColumnNameInputSettingsModel;

	/**
	 * <p>
	 * Creates a new completely initialized {@link SmoothingNodeModel}, ready to
	 * be executed.
	 * </p>
	 * 
	 * @param executor
	 *            The executor used to either replace the input data or append
	 *            results.
	 * @param filterTypeSelectionSettingsModel
	 *            {@link SettingsModel} with the type of filter to use for
	 *            smoothing.
	 * @param inputColSelectionSettingsModel
	 *            {@link SettingsModel} containing the name of the input column
	 *            containing the input signal.
	 * @param appendReplaceChooserSettingsModel
	 *            {@link SettingsModel} with the selection on whether to append
	 *            the result signal to the input table or replace the input
	 *            signal column.
	 * @param appendColumnNameInputSettingsModel
	 *            The name of the column to append if the
	 *            {@link #appendColumnNameInputSettingsModel} is set to append.
	 */
	protected SmoothingNodeModel(final Execution executor, final SettingsModelString filterTypeSelectionSettingsModel,
			final SettingsModelString inputColSelectionSettingsModel,
			final SettingsModelString appendReplaceChooserSettingsModel,
			final SettingsModelString appendColumnNameInputSettingsModel) {
		super(1, 1);
		this.filterTypeSelectionSettingsModel = filterTypeSelectionSettingsModel;
		this.inputColSelectionSettingsModel = inputColSelectionSettingsModel;
		this.appendReplaceChooserSettingsModel = appendReplaceChooserSettingsModel;
		this.appendColumnNameInputSettingsModel = appendColumnNameInputSettingsModel;
		this.executor = executor;
	}

	@Override
	protected void loadInternals(File nodeInternDir, ExecutionMonitor exec)
			throws IOException, CanceledExecutionException {
		// TODO Auto-generated method stub

	}

	@Override
	protected void saveInternals(File nodeInternDir, ExecutionMonitor exec)
			throws IOException, CanceledExecutionException {
		// TODO Auto-generated method stub

	}

	@Override
	protected void saveSettingsTo(NodeSettingsWO settings) {
		filterTypeSelectionSettingsModel.saveSettingsTo(settings);
		inputColSelectionSettingsModel.saveSettingsTo(settings);
		appendReplaceChooserSettingsModel.saveSettingsTo(settings);
		appendColumnNameInputSettingsModel.saveSettingsTo(settings);
	}

	@Override
	protected void validateSettings(NodeSettingsRO settings) throws InvalidSettingsException {
		filterTypeSelectionSettingsModel.validateSettings(settings);
		inputColSelectionSettingsModel.validateSettings(settings);
		appendReplaceChooserSettingsModel.validateSettings(settings);
		if (appendReplaceChooserSettingsModel.getStringValue().equals(SmoothingNodeConstants.APPEND_OPTION)
				&& (appendColumnNameInputSettingsModel.getStringValue() == null
						|| appendColumnNameInputSettingsModel.getStringValue().isEmpty()))
			throw new InvalidSettingsException("Please provide a name for the appended output column.");
		appendColumnNameInputSettingsModel.validateSettings(settings);
	}

	@Override
	protected void loadValidatedSettingsFrom(NodeSettingsRO settings) throws InvalidSettingsException {
		filterTypeSelectionSettingsModel.loadSettingsFrom(settings);
		inputColSelectionSettingsModel.loadSettingsFrom(settings);
		appendReplaceChooserSettingsModel.loadSettingsFrom(settings);
		appendColumnNameInputSettingsModel.loadSettingsFrom(settings);
	}

	@Override
	protected void reset() {

	}

	@Override
	protected BufferedDataTable[] execute(final BufferedDataTable[] inData, final ExecutionContext exec)
			throws Exception {
		BufferedDataTable inputTable = inData[0];
		String inputColumnName = inputColSelectionSettingsModel.getStringValue();
		DataTableSpec inputSpec = inputTable.getSpec();

		DataTableSpec outputSpec = executor.getOutputSpec(inputSpec);
		BufferedDataContainer outputContainer = exec.createDataContainer(outputSpec);

		int inputColumnIndex = inputSpec.findColumnIndex(inputColumnName);
		//
		DataRow previousRow = null;
		DataRow currentRow = null;
		DataRow nextRow = null;

		for (DataRow row : inputTable) {
			previousRow = currentRow;
			currentRow = nextRow;
			nextRow = row;

			DoubleCell resultCell = smooth(previousRow, currentRow, nextRow, inputColumnIndex);
			if (resultCell != null) { // Always null on first iteration.
				DataRow extendedRow = executor.createResultRow(currentRow, resultCell, inputColumnIndex);
				outputContainer.addRowToTable(extendedRow);
			}
		}

		// Add the last row as is, since there is no next value anymore.
		DataRow extendedRow = executor.createResultRow(nextRow,
				new DoubleCell(((DoubleCell) nextRow.getCell(inputColumnIndex)).getDoubleValue()), inputColumnIndex);
		outputContainer.addRowToTable(extendedRow);

		outputContainer.close();
		return new BufferedDataTable[] { outputContainer.getTable() };
	}

	/**
	 * <p>
	 * Smooths the current signal based on three consecutive values.
	 * </p>
	 * 
	 * @param previousRow
	 *            The {@link DataRow} containing the predecessor value.
	 * @param currentRow
	 *            The {@link DataRow} containing the current value (i.e. the
	 *            value to recalculate).
	 * @param nextRow
	 *            The {@link DataRow} containing the successor value.
	 * @param inputColumnIndex
	 *            Index of the column containing the signal in the input table.
	 * @return A new {@link DoubleCell} with the smoothed value.
	 */
	private DoubleCell smooth(final DataRow previousRow, final DataRow currentRow, final DataRow nextRow,
			final int inputColumnIndex) {
		if (currentRow == null) {
			return null;

		} else if (previousRow == null) {
			return new DoubleCell(((DoubleCell) currentRow.getCell(inputColumnIndex)).getDoubleValue());
		} else {
			double previousValue = ((DoubleCell) previousRow.getCell(inputColumnIndex)).getDoubleValue();
			double nextValue = ((DoubleCell) nextRow.getCell(inputColumnIndex)).getDoubleValue();
			double smoothedCurrentValue = (previousValue + nextValue) / 2.0;
			return new DoubleCell(smoothedCurrentValue);
		}
	}

	@Override
	protected DataTableSpec[] configure(DataTableSpec[] inSpecs) throws InvalidSettingsException {
		if (!inSpecs[0].containsCompatibleType(DoubleValue.class))
			throw new InvalidSettingsException(
					"Smoothing node input contains no valid column, compatible to double values. Add a column with double values to the input, to use this node.");
		for (DataColumnSpec columnSpec : inSpecs[0]) {
			if (columnSpec.getType().isCompatible(DoubleValue.class)) {
				inputColSelectionSettingsModel.setStringValue(columnSpec.getName());
				break;
			}
		}

		return new DataTableSpec[] { executor.getOutputSpec(inSpecs[0]) };
	}

	/**
	 * @param executor
	 *            Changes the {@link Execution} used to either replace the input
	 *            data or append results.
	 */
	public void setExecutor(final Execution executor) {
		if (executor == null)
			throw new IllegalArgumentException("Invalid argument for parameter executor: null");
		this.executor = executor;

	}

}