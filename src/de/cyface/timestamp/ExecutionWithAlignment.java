package de.cyface.timestamp;

import org.knime.core.data.DataCell;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTable;
import org.knime.core.data.IntValue;
import org.knime.core.data.LongValue;

public final class ExecutionWithAlignment extends ExecutionWithoutAlignment {
	protected long calcTableAlignment(final DataTable table, final String columnName) {
		long min = Long.MAX_VALUE;
		int valueColumnIndex = table.getDataTableSpec().findColumnIndex(columnName);

		for(DataRow row:table) {
			DataCell cell = row.getCell(valueColumnIndex);
			if(cell.getType().isCompatible(LongValue.class)) {
				min = findMin(min,LongValue.class.cast(cell));
			} else {
				min = findMin(min,IntValue.class.cast(cell));
			}
		}
		return min;
	}
	
	protected long findMin(final long currentMin, final LongValue cell) {
		long cellValue = cell.getLongValue();
		return cellValue < currentMin ? cellValue : currentMin;
	}
	
	protected long findMin(final long currentMin, final IntValue cell) {
		return findMin(currentMin,cell);
	}
}
