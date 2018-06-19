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
 * <p>
 * This is the model implementation of TimestampAligner. Takes two tables with
 * timestamp columns and and assigns to each column from the first table the
 * column from the second table with the closest previous timestamp.
 * </p>
 *
 * @author Klemens Muthmann
 * @version 1.0.0
 * @since 1.0.0
 */
public class TimestampAlignerNodeModel extends NodeModel {

	// the logger instance
	private static final NodeLogger LOGGER = NodeLogger.getLogger(TimestampAlignerNodeModel.class);

	private final SettingsModelString firstTimestampColumnName = new SettingsModelString(CFGKEY_FIRST_TABLE_COLUMN_NAME,
			"");
	private final SettingsModelBoolean firstTimestampColumnRenameCheckbox = new SettingsModelBoolean(CFGKEY_RENAME_FIRST_TABLE_COLUMN_CHECKBOX,
			false);
	private final SettingsModelString firstTimestampColumnRenameName = new SettingsModelString(CFGKEY_RENAME_FIRST_TABLE_COLUMN_NAME,
			"");
	private final SettingsModelString secondTimestampColumnName = new SettingsModelString(
			CFGKEY_SECOND_TABLE_COLUMN_NAME, "");
	private final SettingsModelBoolean secondTimestampColumnRenameCheckbox = new SettingsModelBoolean(CFGKEY_RENAME_SECOND_TABLE_COLUMN_CHECKBOX,
			false);
	private final SettingsModelString secondTimestampColumnRenameName = new SettingsModelString(CFGKEY_RENAME_SECOND_TABLE_COLUMN_NAME,
			"");
	private final SettingsModelBoolean valueRangeAlignment = new SettingsModelBoolean(CFGKEY_VALUE_RANGE_ALIGNMENT_NAME,
			false);
	static final String CFGKEY_FIRST_TABLE_COLUMN_NAME = "de.cyface.knime.firsttablecolumnname";
	static final String CFGKEY_RENAME_FIRST_TABLE_COLUMN_CHECKBOX = "de.cyface.knime.firsttablerenamecheckbox"; 
	static final String CFGKEY_RENAME_FIRST_TABLE_COLUMN_NAME = "de.cyface.knime.firsttablerename";
	static final String CFGKEY_SECOND_TABLE_COLUMN_NAME = "de.cyface.knime.secondtablecolumnname";
	static final String CFGKEY_RENAME_SECOND_TABLE_COLUMN_CHECKBOX = "de.cyface.knime.secondtablerenamecheckbox";
	static final String CFGKEY_RENAME_SECOND_TABLE_COLUMN_NAME = "de.cyface.knime.secondtablerename"; 
	static final String CFGKEY_VALUE_RANGE_ALIGNMENT_NAME = "de.cyface.knime.valuerangealignment";
	static final int FIRST_IN_PORT = 0;
	static final int SECOND_IN_PORT = 1;

	/**
	 * Constructor for the node model.
	 */
	protected TimestampAlignerNodeModel() {
		super(2, 1);
		firstTimestampColumnRenameCheckbox.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				firstTimestampColumnRenameName.setEnabled(firstTimestampColumnRenameCheckbox.getBooleanValue());
			}
		});
		secondTimestampColumnRenameCheckbox.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				secondTimestampColumnRenameName.setEnabled(secondTimestampColumnRenameCheckbox.getBooleanValue());
			}
			
		});
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected BufferedDataTable[] execute(final BufferedDataTable[] inData, final ExecutionContext exec)
			throws Exception {
		try {
			BufferedDataTable firstTable = inData[FIRST_IN_PORT];
			BufferedDataTable secondTable = inData[SECOND_IN_PORT];
			InputData input = new InputData(firstTable, secondTable, firstTimestampColumnName.getStringValue(),
					secondTimestampColumnName.getStringValue());
			if (valueRangeAlignment.getBooleanValue()) {
				return new ExecutionWithAlignment().execute(input,
						createResultSpec(firstTable.getDataTableSpec(), secondTable.getDataTableSpec()), exec);
			} else {
				return new ExecutionWithoutAlignment().execute(input,
						createResultSpec(firstTable.getDataTableSpec(), secondTable.getDataTableSpec()), exec);
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void reset() {
		// TODO Code executed on reset.
		// Models build during execute are cleared here.
		// Also data handled in load/saveInternals will be erased here.
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected DataTableSpec[] configure(final DataTableSpec[] inSpecs) throws InvalidSettingsException {
		DataTableSpec firstTable = inSpecs[0];
		DataTableSpec secondTable = inSpecs[1];

		checkForValidTimestampColumn(firstTable, firstTimestampColumnName.getStringValue());
		checkForValidTimestampColumn(secondTable, secondTimestampColumnName.getStringValue());

		return new DataTableSpec[] { createResultSpec(firstTable, secondTable) };
	}

	/**
	 * {@inheritDoc}
	 */
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

	/**
	 * {@inheritDoc}
	 */
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

	/**
	 * {@inheritDoc}
	 */
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

	/**
	 * {@inheritDoc}
	 */
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
	
	private void renameOutputColumnsIfNecessary(final DataColumnSpec[] colSpecs, final DataTableSpec firstTableSpec, final DataTableSpec secondTableSpec) {
		int offset = 0;
		if(firstTimestampColumnRenameCheckbox.getBooleanValue()) {
			offset = renameColumn(firstTableSpec, firstTimestampColumnName.getStringValue(), firstTimestampColumnRenameName.getStringValue(), colSpecs, offset);
		}
		if(secondTimestampColumnRenameCheckbox.getBooleanValue()) {
			offset = renameColumn(secondTableSpec,secondTimestampColumnName.getStringValue(), secondTimestampColumnRenameName.getStringValue(), colSpecs, offset );
		}
	}
	
	private int renameColumn(final DataTableSpec tableSpec, final String originalColumnName, final String renamedColumnName, final DataColumnSpec[] colSpecs, final int offset) {
		int indexOfFirstTimestampColumnInFirstInputSpec = offset + tableSpec.findColumnIndex(originalColumnName);
		DataType type = colSpecs[indexOfFirstTimestampColumnInFirstInputSpec].getType();
		colSpecs[indexOfFirstTimestampColumnInFirstInputSpec] = new DataColumnSpecCreator(renamedColumnName,type).createSpec();
		return tableSpec.getNumColumns();
	}

	private void checkForValidTimestampColumn(final DataTableSpec tableSpec, final String columnName)
			throws InvalidSettingsException {
		if (!tableSpec.containsName(columnName)
				|| !tableSpec.getColumnSpec(columnName).getType().isCompatible(LongValue.class)) {
			throw new InvalidSettingsException(
					String.format("No valid timestamp column called %s in table %s", columnName, tableSpec.getName()));
		}
	}

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