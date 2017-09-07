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
import org.knime.core.node.defaultnodesettings.SettingsModelString;

/**
 * @author Klemens Muthmann
 * @version 1.0.0
 * @since 1.0.0
 */
public class SmoothingNodeModel extends NodeModel {

	private Execution executor;

	private final static String FILTER_TYPE_SELECTION_SETTINGS_MODEL_CONFIG_NAME = "de.cyface.smoothing.settings.filtertype";
	private final static String INPUT_COL_SELECTION_SETTINGS_MODEL_CONFIG_NAME = "de.cyface.smoothing.settings.inputcol";
	private final static String APPEND_REPLACE_CHOOSER_SETTINGS_MODEL_CONFIG_NAME = "de.cyface.smooting.settings.appendreplace";
	private final static String APPEND_COLUMN_NAME_INPUT_SETTINGS_MODEL_CONFIG_NAME = "de.cyface.smoothing.appendcolumnname";

	private final SettingsModelString filterTypeSelectionSettingsModel;
	private final SettingsModelString inputColSelectionSettingsModel;
	private final SettingsModelString appendReplaceChooserSettingsModel;
	private final SettingsModelString appendColumnNameInputSettingsModel;

	static SettingsModelString createFilterTypeSelectionSettingsModel() {
		return new SettingsModelString(FILTER_TYPE_SELECTION_SETTINGS_MODEL_CONFIG_NAME, Filter.RECTANGULAR.getName());
	}

	static SettingsModelString createInputColSelectionSettingsModel() {
		return new SettingsModelString(INPUT_COL_SELECTION_SETTINGS_MODEL_CONFIG_NAME, "");
	}

	static SettingsModelString createAppendReplaceChooserSettingsModel() {
		return new SettingsModelString(APPEND_REPLACE_CHOOSER_SETTINGS_MODEL_CONFIG_NAME,
				SmoothingNodeDialog.APPEND_OPTION);
	}

	static SettingsModelString createAppendColumnNameInputSettingsModel() {
		return new SettingsModelString(APPEND_COLUMN_NAME_INPUT_SETTINGS_MODEL_CONFIG_NAME, "");
	}

	protected SmoothingNodeModel() {
		super(1, 1);
		filterTypeSelectionSettingsModel = createFilterTypeSelectionSettingsModel();
		inputColSelectionSettingsModel = createInputColSelectionSettingsModel();
		appendReplaceChooserSettingsModel = createAppendReplaceChooserSettingsModel();
		appendColumnNameInputSettingsModel = createAppendColumnNameInputSettingsModel();
		executor = new AppendColumnExecutor(appendColumnNameInputSettingsModel);

		appendReplaceChooserSettingsModel.addChangeListener(event -> {
			if (appendReplaceChooserSettingsModel.getStringValue().equals(SmoothingNodeDialog.REPLACE_OPTION)) {
				appendColumnNameInputSettingsModel.setEnabled(false);
				executor = new AppendColumnExecutor(appendColumnNameInputSettingsModel);
			} else {
				appendColumnNameInputSettingsModel.setEnabled(true);
				executor = new ReplaceColumnExecutor();
			}
		});
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
		/*if (appendReplaceChooserSettingsModel.getStringValue().equals(SmoothingNodeDialog.APPEND_OPTION)
				&& (appendColumnNameInputSettingsModel.getStringValue()==null ||appendColumnNameInputSettingsModel.getStringValue().isEmpty()))
			throw new InvalidSettingsException("Please provide a name for the appended output column.");*/
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

			if (previousRow == null || currentRow == null) {
				continue;
			}

			double previousValue = ((DoubleCell) previousRow.getCell(inputColumnIndex)).getDoubleValue();
			double nextValue = ((DoubleCell) nextRow.getCell(inputColumnIndex)).getDoubleValue();
			double smoothedCurrentValue = (previousValue + nextValue) / 2.0;
			DoubleCell resultCell = new DoubleCell(smoothedCurrentValue);

			DataRow extendedRow = executor.createResultRow(row, resultCell, inputColumnIndex);
			outputContainer.addRowToTable(extendedRow);
		}

		outputContainer.close();
		return new BufferedDataTable[] { outputContainer.getTable() };
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

		//return new DataTableSpec[] { executor.getOutputSpec(inSpecs[0]) };
		return new DataTableSpec[] { inSpecs[0] };
	}

}
