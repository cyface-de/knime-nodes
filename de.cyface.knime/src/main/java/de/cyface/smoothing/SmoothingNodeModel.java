/**
 * 
 */
package de.cyface.smoothing;

import java.io.File;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Queue;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DataType;
import org.knime.core.data.DoubleValue;
import org.knime.core.data.container.CloseableRowIterator;
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
import org.knime.core.node.defaultnodesettings.SettingsModel;
import org.knime.core.node.defaultnodesettings.SettingsModelInteger;
import org.knime.core.node.defaultnodesettings.SettingsModelString;

import de.cyface.smoothing.algorithm.Algorithm;

/**
 * A {@link NodeModel} for the smoothing node, which smoothes an input signal,
 * writing the result to an output signal.
 * 
 * @author Klemens Muthmann
 * @version 1.0.0
 * @since 1.0.0
 */
public class SmoothingNodeModel extends NodeModel {
	private static final NodeLogger LOGGER = NodeLogger.getLogger(SmoothingNodeModel.class);

	/**
	 * The executor used to either replace the input data or append results.
	 */
	private Execution executor;

	/**
	 * Settings model with the type of filter to use for smoothing.
	 */
	private final SettingsModelString filterTypeSelectionSettingsModel;
	/**
	 * {@link SettingsModel} containing the name of the input column containing
	 * the input signal.
	 */
	private final SettingsModelString inputColSelectionSettingsModel;
	/**
	 * {@link SettingsModel} with the selection on whether to append the result
	 * signal to the input table or replace the input signal column.
	 */
	private final SettingsModelString appendReplaceChooserSettingsModel;
	/**
	 * The name of the column to append, if the
	 * {@link #appendColumnNameInputSettingsModel} is set to append.
	 */
	private final SettingsModelString appendColumnNameInputSettingsModel;
	/**
	 * The window size specifying the number of data points to use for
	 * smoothing.
	 */
	private final SettingsModelInteger windowSizeSelectorSettingsModel;

	private Algorithm algorithm;

	/**
	 * Creates a new completely initialized {@link SmoothingNodeModel}, ready to
	 * be executed.
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
	 * @param windowSizeSelectorSettingsModel
	 *            The {@link SettingsModel} containing the windows size to use
	 *            for smoothing.
	 */
	protected SmoothingNodeModel(final Execution executor, final SettingsModelString filterTypeSelectionSettingsModel,
			final SettingsModelString inputColSelectionSettingsModel,
			final SettingsModelString appendReplaceChooserSettingsModel,
			final SettingsModelString appendColumnNameInputSettingsModel,
			final SettingsModelInteger windowSizeSelectorSettingsModel) {
		super(1, 1);
		this.filterTypeSelectionSettingsModel = filterTypeSelectionSettingsModel;
		selectAlgorithm();
		filterTypeSelectionSettingsModel.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				selectAlgorithm();
			}
		});
		this.inputColSelectionSettingsModel = inputColSelectionSettingsModel;// new
																				// SettingsModelString(SmoothingNodeConstants.INPUT_COL_SELECTION_SETTINGS_MODEL_CONFIG_NAME,"");//
		this.appendReplaceChooserSettingsModel = appendReplaceChooserSettingsModel;
		this.appendColumnNameInputSettingsModel = appendColumnNameInputSettingsModel;
		this.windowSizeSelectorSettingsModel = windowSizeSelectorSettingsModel;
		this.executor = executor;
	}

	private Algorithm selectAlgorithm() {
		String value = filterTypeSelectionSettingsModel.getStringValue();
		if (Filter.RECTANGULAR.getName().equals(value)) {
			return Filter.RECTANGULAR.getAlgorithm();
		} else if (Filter.TRIANGULAR.getName().equals(value)) {
			return Filter.TRIANGULAR.getAlgorithm();
		} else {
			throw new IllegalStateException("Unsupported algorithm " + value + " selected!");
		}
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
		windowSizeSelectorSettingsModel.saveSettingsTo(settings);
	}

	@Override
	protected void validateSettings(NodeSettingsRO settings) throws InvalidSettingsException {
		filterTypeSelectionSettingsModel.validateSettings(settings);
		inputColSelectionSettingsModel.validateSettings(settings);
		appendReplaceChooserSettingsModel.validateSettings(settings);
		appendColumnNameInputSettingsModel.validateSettings(settings);
		if (appendReplaceChooserSettingsModel.getStringValue().equals(SmoothingNodeConstants.APPEND_OPTION)
				&& (appendColumnNameInputSettingsModel.getStringValue() == null
						|| appendColumnNameInputSettingsModel.getStringValue().isEmpty()))
			throw new InvalidSettingsException("Please provide a name for the appended output column.");
		windowSizeSelectorSettingsModel.validateSettings(settings);
	}

	@Override
	protected void loadValidatedSettingsFrom(NodeSettingsRO settings) throws InvalidSettingsException {
		filterTypeSelectionSettingsModel.loadSettingsFrom(settings);
		inputColSelectionSettingsModel.loadSettingsFrom(settings);
		appendReplaceChooserSettingsModel.loadSettingsFrom(settings);
		appendColumnNameInputSettingsModel.loadSettingsFrom(settings);
		windowSizeSelectorSettingsModel.loadSettingsFrom(settings);
	}

	@Override
	protected void reset() {

	}

	@Override
	protected BufferedDataTable[] execute(final BufferedDataTable[] inData, final ExecutionContext exec)
			throws Exception {
		BufferedDataTable inputTable = inData[0];
		String inputColumnName = inputColSelectionSettingsModel.getStringValue();
		LOGGER.debug(String.format("Reading input from column %s.", inputColumnName));
		DataTableSpec inputSpec = inputTable.getSpec();

		DataTableSpec outputSpec = executor.getOutputSpec(inputSpec);
		BufferedDataContainer outputContainer = exec.createDataContainer(outputSpec);

		int inputColumnIndex = inputSpec.findColumnIndex(inputColumnName);
		if (!inputSpec.getColumnSpec(inputColumnIndex).getType().isCompatible(DoubleValue.class)) {
			LOGGER.warn("Invalid data type in input column. " + inputColumnName);
			return new BufferedDataTable[] { outputContainer.getTable() };
		}
		int windowSize = windowSizeSelectorSettingsModel.getIntValue();

		LinkedList<DataRow> window = new LinkedList<>();
		CloseableRowIterator iter = inputTable.iterator();
		// Ramp up
		try {
			for (int i = 0; i < windowSize; i++) {
				window.offer(iter.next());
			}

			double[] windowValues = new double[windowSize];
			while (iter.hasNext()) {
				DataRow currentRow = window.get((windowSize / 2) + 1);

				for (int i = 0; i < windowSize; i++) {
					windowValues[i] = ((DoubleCell) window.get(i).getCell(inputColumnIndex)).getDoubleValue();
				}
				LOGGER.info("Calculating smoothing with window "+windowValues);
				double smoothedValue = algorithm.smooth(windowValues);
				LOGGER.info("Result: "+smoothedValue);
				DoubleCell resultCell = new DoubleCell(smoothedValue);
				DataRow extendedRow = executor.createResultRow(currentRow, resultCell, inputColumnIndex);
				outputContainer.addRowToTable(extendedRow);

				window.offer(iter.next());
			}

			outputContainer.close();
		} catch (NoSuchElementException e) {
			LOGGER.warn("Window Size was larger than table. No smoothing possible!");
		}
		return new BufferedDataTable[] { outputContainer.getTable() };
	}

	@Override
	protected DataTableSpec[] configure(DataTableSpec[] inSpecs) throws InvalidSettingsException {
		// TODO Testen was passiert wenn kein Double Input vorliegt.
		if (!inSpecs[0].containsCompatibleType(DoubleValue.class))
			throw new InvalidSettingsException(
					"Smoothing node input contains no valid column, compatible to double values. Add a column with double values to the input, to use this node.");

		if (!inSpecs[0].containsName(inputColSelectionSettingsModel.getStringValue())) {
			for (DataColumnSpec columnSpec : inSpecs[0]) {
				if (columnSpec.getType().isCompatible(DoubleValue.class)) {
					inputColSelectionSettingsModel.setStringValue(columnSpec.getName());
					break;
				}
			}
		}

		List<DataColumnSpec> outColumnSpecs = new ArrayList<>();

		for (DataColumnSpec inColumnSpec : inSpecs[0]) {
			outColumnSpecs.add(inColumnSpec);
		}

		if (appendReplaceChooserSettingsModel.getStringValue().equals(SmoothingNodeConstants.APPEND_OPTION)) {
			String columnName = appendColumnNameInputSettingsModel.getStringValue();
			outColumnSpecs.add(new DataColumnSpecCreator(columnName, DataType.getType(DoubleCell.class)).createSpec());
		}

		return new DataTableSpec[] {
				new DataTableSpec(outColumnSpecs.toArray(new DataColumnSpec[outColumnSpecs.size()])) };
		// return new DataTableSpec[]{};
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
