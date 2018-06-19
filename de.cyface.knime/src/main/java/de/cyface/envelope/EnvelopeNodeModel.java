package de.cyface.envelope;

import java.io.File;
import java.io.IOException;

import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.def.DefaultRow;
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

import de.cyface.smoothing.dialog.ColumnSelectionNodeOption;

public class EnvelopeNodeModel extends NodeModel {
	
	private final SettingsModelString inputColumnSelectionModel;

	public EnvelopeNodeModel(final ColumnSelectionNodeOption inputColumnSelection) {
		super(1,1);
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
		BufferedDataContainer outputContainer = exec.createDataContainer(createOutputSpec(inputTable.getDataTableSpec()));
		int inputColumnIndex = inputTable.getSpec().findColumnIndex(inputColumnSelectionModel.getStringValue());
		
		DataRow previousRow = null;
		DataRow currentRow = null;
		DataRow nextRow = null;

		for (DataRow row : inputTable) {
			previousRow = currentRow;
			currentRow = nextRow;
			nextRow = row;

			//DoubleCell resultCell = smooth(previousRow, currentRow, nextRow, inputColumnIndex);
			if (previousRow != null && currentRow != null) { // Always null on first iteration.
				// TODO create for long and int tables.
				double previousValue = ((DoubleCell)previousRow.getCell(inputColumnIndex)).getDoubleValue();
				double currentValue = ((DoubleCell)currentRow.getCell(inputColumnIndex)).getDoubleValue();
				double nextValue = ((DoubleCell)nextRow.getCell(inputColumnIndex)).getDoubleValue();
				
				if(previousValue<=currentValue && nextValue<=currentValue) {
					DataRow resultRow = new DefaultRow(currentRow.getKey(), currentValue);
					outputContainer.addRowToTable(resultRow);
				}
			}
		}
		outputContainer.close();
		
		return new BufferedDataTable[]{outputContainer.getTable()};
	}
	
	@Override
	protected DataTableSpec[] configure(DataTableSpec[] inSpecs) throws InvalidSettingsException {
		return new DataTableSpec[]{createOutputSpec(inSpecs[0])};
	}
	
	private DataTableSpec createOutputSpec(final DataTableSpec inSpec) {
		String inputDataColumnName = inputColumnSelectionModel.getStringValue();
		DataColumnSpec inputColumnSpec = inSpec.getColumnSpec(inputDataColumnName);
		DataTableSpec outputTableSpec = new DataTableSpec(inputColumnSpec);
		return outputTableSpec;
	}

}
