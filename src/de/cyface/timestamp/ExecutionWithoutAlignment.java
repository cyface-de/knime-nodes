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

public class ExecutionWithoutAlignment implements Execution {

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

		DataRow currentSecondTableRow = secondTableIter.next();
		long currentSecondTableTimestamp = align(
				getTimestamp(secondTimestampColumnName, currentSecondTableRow, secondTableTimestampColumnIndex),
				secondTableAlignment);
		DataRow nextSecondTableRow = secondTableIter.next();
		long nextSecondTableTimestamp = align(
				getTimestamp(secondTimestampColumnName, nextSecondTableRow, secondTableTimestampColumnIndex),
				secondTableAlignment);

		for (DataRow row : firstTable) {
			long timestamp = align(getTimestamp(firstTimestampColumnName, row, firstTableTimestampColumnIndex),
					firstTableAlignment);
			if (timestamp < currentSecondTableTimestamp && timestamp < nextSecondTableTimestamp) {
				continue;
			} else if (timestamp >= currentSecondTableTimestamp
					&& (nextSecondTableRow == null || timestamp < nextSecondTableTimestamp)) {
				DataRow combinedRow = concatenateRows(row, currentSecondTableRow,firstTableTimestampColumnIndex,secondTableTimestampColumnIndex,timestamp,currentSecondTableTimestamp);
				res.addRowToTable(combinedRow);
			} else {
				currentSecondTableRow = nextSecondTableRow;
				currentSecondTableTimestamp = nextSecondTableTimestamp;
				if (secondTableIter.hasNext()) {
					nextSecondTableRow = secondTableIter.next();
					nextSecondTableTimestamp = align(getTimestamp(secondTimestampColumnName, nextSecondTableRow,
							secondTableTimestampColumnIndex), secondTableAlignment);
				} else {
					nextSecondTableRow = null;
				}
				DataRow combinedRow = concatenateRows(row, currentSecondTableRow,firstTableTimestampColumnIndex,secondTableTimestampColumnIndex,timestamp,currentSecondTableTimestamp);
				res.addRowToTable(combinedRow);
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

	private long getTimestamp(final String columnName, final DataRow row, final int timestampColumnIndex) {
		if (row.getCell(timestampColumnIndex) instanceof LongCell) {
			return ((LongCell) row.getCell(timestampColumnIndex)).getLongValue();
		} else if (row.getCell(timestampColumnIndex) instanceof IntCell) {
			return ((IntCell) row.getCell(timestampColumnIndex)).getLongValue();
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
				cells[i] = new LongCell(firstRowAlignedValue);
			} else {
				cells[i] = cell;
			}
			i++;
		}
		int j = 0;
		for (DataCell cell : secondRow) {
			if (j == secondRowAlignmentCellIndex) {
				cells[i] = new LongCell(secondRowAlignedValue);
			} else {
				cells[i] = cell;
			}
			i++;
			j++;
		}
		return new DefaultRow(firstRow.getKey(), cells);
	}
}
