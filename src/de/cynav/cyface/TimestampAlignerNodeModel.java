package de.cynav.cyface;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.LongValue;
import org.knime.core.data.container.CloseableRowIterator;
import org.knime.core.data.def.DefaultRow;
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

/**
 * <p>
 * This is the model implementation of GpsPreAndNextAssigner. Takes two tables
 * with timestamp columns and and assigns to each column from the first table
 * the column from the second table with the closest previous timestamp.
 * </p>
 *
 * @author Klemens Muthmann
 * @version 1.0.0
 * @since 1.0.0
 */
public class TimestampAlignerNodeModel extends NodeModel {

	// the logger instance
	private static final NodeLogger LOGGER = NodeLogger.getLogger(TimestampAlignerNodeModel.class);

	private static final String FIRST_TIMESTAMP_COLUMN_NAME = "time";
	private static final String SECOND_TIMESTAMP_COLUMN_NAME = "time";

	/**
	 * Constructor for the node model.
	 */
	protected TimestampAlignerNodeModel() {
		super(2, 1);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected BufferedDataTable[] execute(final BufferedDataTable[] inData, final ExecutionContext exec)
			throws Exception {
		try {
			BufferedDataTable firstTable = inData[0];
			BufferedDataTable secondTable = inData[1];

			BufferedDataContainer res = exec.createDataContainer(
					createResultSpec(firstTable.getDataTableSpec(), secondTable.getDataTableSpec()));

			CloseableRowIterator secondTableIter = secondTable.iterator();
			int secondTableTimestampColumnIndex = secondTable.getDataTableSpec()
					.findColumnIndex(SECOND_TIMESTAMP_COLUMN_NAME);
			int firstTableTimestampColumnIndex = firstTable.getDataTableSpec()
					.findColumnIndex(FIRST_TIMESTAMP_COLUMN_NAME);
			DataRow currentSecondTableRow = secondTableIter.next();
			long currentSecondTableTimestamp = getTimestamp(currentSecondTableRow, secondTableTimestampColumnIndex);
			DataRow nextSecondTableRow = secondTableIter.next();
			long nextSecondTableTimestamp = getTimestamp(nextSecondTableRow, secondTableTimestampColumnIndex);

			for (DataRow row : firstTable) {
				long timestamp = getTimestamp(row, firstTableTimestampColumnIndex);
				if (timestamp < currentSecondTableTimestamp && timestamp < nextSecondTableTimestamp) {
					continue;
				} else if (timestamp >= currentSecondTableTimestamp
						&& (nextSecondTableRow == null || timestamp < nextSecondTableTimestamp)) {
					DataRow combinedRow = concatenateRows(row, currentSecondTableRow);
					res.addRowToTable(combinedRow);
				} else {
					currentSecondTableRow = nextSecondTableRow;
					currentSecondTableTimestamp = nextSecondTableTimestamp;
					if (secondTableIter.hasNext()) {
						nextSecondTableRow = secondTableIter.next();
						nextSecondTableTimestamp = getTimestamp(nextSecondTableRow, secondTableTimestampColumnIndex);
					} else {
						nextSecondTableRow = null;
					}
					DataRow combinedRow = concatenateRows(row, currentSecondTableRow);
					res.addRowToTable(combinedRow);
				}
			}

			res.close();
			return new BufferedDataTable[] { res.getTable() };
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	private long getTimestamp(final DataRow row, final int timestampColumnIndex) {
		return ((LongCell) row.getCell(timestampColumnIndex)).getLongValue();
	}

	private DataRow concatenateRows(final DataRow firstRow, final DataRow secondRow) {
		DataCell[] cells = new DataCell[firstRow.getNumCells() + secondRow.getNumCells()];
		int i = 0;
		for (DataCell cell : firstRow) {
			cells[i] = cell;
			i++;
		}
		for (DataCell cell : secondRow) {
			cells[i] = cell;
			i++;
		}
		return new DefaultRow(firstRow.getKey(), cells);
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

		checkForValidTimestampColumn(firstTable, FIRST_TIMESTAMP_COLUMN_NAME);
		checkForValidTimestampColumn(secondTable, SECOND_TIMESTAMP_COLUMN_NAME);

		return new DataTableSpec[] { createResultSpec(firstTable, secondTable) };
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

		return new DataTableSpec(colSpecs);
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

	private void checkForValidTimestampColumn(final DataTableSpec tableSpec, final String columnName)
			throws InvalidSettingsException {
		if (!tableSpec.containsName(columnName)
				|| !tableSpec.getColumnSpec(columnName).getType().isCompatible(LongValue.class)) {
			throw new InvalidSettingsException(
					String.format("No valid timestamp column called %s in table %s", columnName, tableSpec.getName()));
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void saveSettingsTo(final NodeSettingsWO settings) {

		// TODO save user settings to the config object.

		// m_count.saveSettingsTo(settings);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void loadValidatedSettingsFrom(final NodeSettingsRO settings) throws InvalidSettingsException {

		// TODO load (valid) settings from the config object.
		// It can be safely assumed that the settings are valided by the
		// method below.

		// m_count.loadSettingsFrom(settings);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void validateSettings(final NodeSettingsRO settings) throws InvalidSettingsException {

		// TODO check if the settings could be applied to our model
		// e.g. if the count is in a certain range (which is ensured by the
		// SettingsModel).
		// Do not actually set any values of any member variables.

		// m_count.validateSettings(settings);

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

}