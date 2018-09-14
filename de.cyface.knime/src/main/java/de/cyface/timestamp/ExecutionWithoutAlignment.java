package de.cyface.timestamp;

import org.knime.core.data.DataCell;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTable;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.RowIterator;
import org.knime.core.data.def.DefaultRow;
import org.knime.core.data.def.IntCell;
import org.knime.core.data.def.LongCell;
import org.knime.core.node.BufferedDataContainer;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.NodeLogger;

/**
 * An executor that runs timestamp association without aligning the timestamp to
 * zero. This means timestamp ranges captured in non overlapping time ranges
 * will produce an empty table.
 * 
 * @author Klemens Muthmann
 * @version 1.0.1
 * @since 1.0.0
 */
public class ExecutionWithoutAlignment implements Execution {

	/**
	 * The logger used for objects of this class.
	 */
	private static final NodeLogger LOGGER = NodeLogger.getLogger(ExecutionWithoutAlignment.class);

	public BufferedDataTable[] execute(final InputData input, final DataTableSpec outputTableSpec,
			final ExecutionContext context) {
		DataTable firstTable = input.getFirstTable();
		DataTable secondTable = input.getSecondTable();
		String firstTimestampColumnName = input.getFirstTimestampColumnName();
		String secondTimestampColumnName = input.getSecondTimestampColumnName();
		DataTableSpec secondTableSpec = secondTable.getDataTableSpec();
		DataTableSpec firstTableSpec = firstTable.getDataTableSpec();
		BufferedDataContainer res = context.createDataContainer(outputTableSpec);

		RowIterator secondTableIter = secondTable.iterator();
		int secondTableTimestampColumnIndex = secondTableSpec.findColumnIndex(secondTimestampColumnName);
		int firstTableTimestampColumnIndex = firstTableSpec.findColumnIndex(firstTimestampColumnName);

		long firstTableAlignment = calcTableAlignment(firstTable, firstTimestampColumnName);
		long secondTableAlignment = calcTableAlignment(secondTable, secondTimestampColumnName);

		DataRow currentSecondTableRow = getNextValidRow(secondTableTimestampColumnIndex, secondTableIter);
		DataRow nextSecondTableRow = getNextValidRow(secondTableTimestampColumnIndex, secondTableIter);
		if (currentSecondTableRow == null) {
			LOGGER.warn("Not enough low frequency data to align to!");
		} else {
			try {
				long currentSecondTableTimestamp = align(
						getTimestamp(secondTimestampColumnName, currentSecondTableRow, secondTableTimestampColumnIndex),
						secondTableAlignment);
				long nextSecondTableTimestamp = nextSecondTableRow == null ? Long.MAX_VALUE
						: align(getTimestamp(secondTimestampColumnName, nextSecondTableRow,
								secondTableTimestampColumnIndex), secondTableAlignment);

				for (DataRow row : firstTable) {
					try {
						long timestamp = align(
								getTimestamp(firstTimestampColumnName, row, firstTableTimestampColumnIndex),
								firstTableAlignment);
						if (timestamp < currentSecondTableTimestamp && timestamp < nextSecondTableTimestamp) {
							continue;
						} else if (nextSecondTableRow != null && timestamp >= nextSecondTableTimestamp) {
							currentSecondTableRow = nextSecondTableRow;
							currentSecondTableTimestamp = nextSecondTableTimestamp;
							nextSecondTableRow = getNextValidRow(secondTableTimestampColumnIndex, secondTableIter);
							nextSecondTableTimestamp = nextSecondTableRow == null ? Long.MAX_VALUE
									: align(getTimestamp(secondTimestampColumnName, nextSecondTableRow,
											secondTableTimestampColumnIndex), secondTableAlignment);
						}
						DataRow combinedRow = concatenateRows(row, currentSecondTableRow,
								firstTableTimestampColumnIndex, secondTableTimestampColumnIndex, timestamp,
								currentSecondTableTimestamp);
						res.addRowToTable(combinedRow);
					} catch (MissingCellException e) {
						LOGGER.warn("Skipping missing cell in row " + row.getKey());
					}
				}
			} catch (MissingCellException e) {
				throw new IllegalStateException(e);
			}
		}

		res.close();
		return new BufferedDataTable[] { res.getTable() };

	}

	protected long calcTableAlignment(final DataTable firstTable, final String columnName) {
		return 0;
	}

	static long align(final long timestamp, final long alignment) {
		return timestamp - alignment;
	}

	/**
	 * Tries to find the next row with a non missing timestamp value in the table.
	 * 
	 * @param timestampColumnIndex
	 *            The index of the column to extract the timestamp from
	 * @param iter
	 *            A <code>RowIterator</code> on rows containing a timestamp column.
	 * @return The next row in the provided iterator with a valid timestamp value or
	 *         <code>null</code> if the end of the iterator has been reached.
	 */
	private DataRow getNextValidRow(final int timestampColumnIndex, final RowIterator iter) {
		while (iter.hasNext()) {
			DataRow row = iter.next();
			DataCell timestampCell = row.getCell(timestampColumnIndex);
			if (timestampCell.isMissing()) {
				LOGGER.warn("Skipping missing cell in row " + row.getKey());
			} else {
				return row;
			}
		}
		return null;
	}

	/**
	 * Extracts the timestamp value from one row of an input table. Valid input
	 * columns must contain cells of type <code>LongCell</code> or
	 * <code>IntCell</code>.
	 * 
	 * @param columnName
	 *            The name of the timestamp column
	 * @param row
	 *            The row to extract the timestamp from.
	 * @param timestampColumnIndex
	 *            The index of the column to extract the timestamp from
	 * @return The timestamp as a long value.
	 * @throws MissingCellException
	 *             If the timestamp columns contains a missing value in the provided
	 *             row.
	 */
	private long getTimestamp(final String columnName, final DataRow row, final int timestampColumnIndex)
			throws MissingCellException {
		DataCell timestampCell = row.getCell(timestampColumnIndex);
		if (timestampCell.isMissing()) {
			throw new MissingCellException();
		}

		if (timestampCell instanceof LongCell) {
			return ((LongCell) timestampCell).getLongValue();
		} else if (row.getCell(timestampColumnIndex) instanceof IntCell) {
			return ((IntCell) timestampCell).getLongValue();
		} else {
			throw new IllegalStateException(
					String.format("{} column of invalid type {}. Pleas use a type compatible to long values.",
							columnName, row.getCell(timestampColumnIndex).getType().getName()));
		}
	}

	private DataRow concatenateRows(final DataRow firstRow, final DataRow secondRow,
			final int firstRowAlignmentCellIndex, final int secondRowAlignmentCellIndex,
			final long firstRowAlignedValue, final long secondRowAlignedValue) {
		DataCell[] cells = new DataCell[firstRow.getNumCells() + secondRow.getNumCells()];
		int i = 0;
		for (DataCell cell : firstRow) {
			if (i == firstRowAlignmentCellIndex) {
				cells[i] = cell.getClass().equals(LongCell.class) ? new LongCell(firstRowAlignedValue)
						: new IntCell((int) firstRowAlignedValue);
			} else {
				cells[i] = cell;
			}
			i++;
		}
		int j = 0;
		for (DataCell cell : secondRow) {
			if (j == secondRowAlignmentCellIndex) {
				cells[i] = cell.getClass().equals(LongCell.class) ? new LongCell(secondRowAlignedValue)
						: new IntCell((int) secondRowAlignedValue);
			} else {
				cells[i] = cell;
			}
			i++;
			j++;
		}
		return new DefaultRow(firstRow.getKey(), cells);
	}
}
