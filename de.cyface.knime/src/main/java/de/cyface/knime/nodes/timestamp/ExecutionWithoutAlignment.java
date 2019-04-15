/*
 * Copyright 2018 Cyface GmbH
 * 
 * This file is part of the Cyface KNIME Nodes.
 *
 * The Cyface KNIME Nodes is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * The Cyface KNIME Nodes is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Cyface KNIME Nodes. If not, see <http://www.gnu.org/licenses/>.
 */
package de.cyface.knime.nodes.timestamp;

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
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.NodeLogger;

/**
 * An executor that runs timestamp association without aligning the timestamp to
 * zero. This means timestamp ranges captured in non overlapping time ranges
 * will produce an empty table.
 * 
 * @author Klemens Muthmann
 * @version 1.0.2
 * @since 1.0.0
 */
public class ExecutionWithoutAlignment implements Execution {

	/**
	 * The logger used for objects of this class.
	 */
	private static final NodeLogger LOGGER = NodeLogger.getLogger(ExecutionWithoutAlignment.class);

	@Override
	public BufferedDataTable[] execute(final InputData input, final DataTableSpec outputTableSpec,
			final ExecutionContext context) throws CanceledExecutionException {
		final DataTable firstTable = input.getFirstTable();
		final DataTable secondTable = input.getSecondTable();
		final String firstTimestampColumnName = input.getFirstTimestampColumnName();
		final String secondTimestampColumnName = input.getSecondTimestampColumnName();
		final DataTableSpec secondTableSpec = secondTable.getDataTableSpec();
		final DataTableSpec firstTableSpec = firstTable.getDataTableSpec();
		final BufferedDataContainer res = context.createDataContainer(outputTableSpec);

		final RowIterator secondTableIter = secondTable.iterator();
		final int secondTableTimestampColumnIndex = secondTableSpec.findColumnIndex(secondTimestampColumnName);
		final int firstTableTimestampColumnIndex = firstTableSpec.findColumnIndex(firstTimestampColumnName);

		final long firstTableAlignment = calcTableAlignment(firstTable, firstTimestampColumnName);
		final long secondTableAlignment = calcTableAlignment(secondTable, secondTimestampColumnName);

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

				// Handle progress
				final ExecutionMonitor monitor = context.createSubProgress(1.0);
				int i = 0;
				
				for (DataRow row : firstTable) {
				    context.checkCanceled();
				    
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
					} finally {
						monitor.setProgress(((double)i)/((BufferedDataTable)firstTable).size());
						i++;
					}
				}
			} catch (MissingCellException e) {
				throw new IllegalStateException(e);
			}
		}

		res.close();
		return new BufferedDataTable[] { res.getTable() };

	}

	/**
	 * For this implementation the {@link Execution} always zero.
	 * 
	 * @param table The table to align.
     * @param columnName The name of the column to align
     * @return The alignment to apply to the column to align it.
	 */
	protected long calcTableAlignment(final DataTable firstTable, final String columnName) {
		return 0;
	}

	/**
	 * Aligns a timestamp using the provided alignment.
	 * 
	 * @param timestamp The timestamp to align.
	 * @param alignment The alignment to use.
	 * @return The aligned timestamp.
	 */
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

	/**
	 * Appends one data row to the second, aligning values in both.
	 * 
	 * @param firstRow The first row, which is the one to keep.
	 * @param secondRow The second row, which is the one to concatenate.
	 * @param firstRowAlignmentCellIndex The column number of the cell in the first row to align.
	 * @param secondRowAlignmentCellIndex The column number of the cell in the second row to align.
	 * @param firstRowAlignedValue The alignment value for the first row.
	 * @param secondRowAlignedValue The alignment value for the second row.
	 * @return The concatenated data rows as a new row.
	 */
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
