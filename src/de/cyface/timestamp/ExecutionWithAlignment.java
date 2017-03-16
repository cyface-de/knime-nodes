package de.cyface.timestamp;

import org.knime.core.data.DataRow;
import org.knime.core.data.DataTable;
import org.knime.core.data.LongValue;
import org.knime.core.data.def.IntCell;
import org.knime.core.data.def.LongCell;

public final class ExecutionWithAlignment extends ExecutionWithoutAlignment {
	protected long calcTableAlignment(final DataTable table, final String columnName) {
		long min = Long.MAX_VALUE;
		int valueColumnIndex = table.getDataTableSpec().findColumnIndex(columnName);
		boolean isLongValue = table.getDataTableSpec().getColumnSpec(valueColumnIndex).getType().isCompatible(LongValue.class);

		for(DataRow row:table) {
			if(isLongValue) {
				min = findMin(min,LongCell.class.cast(row.getCell(valueColumnIndex)));
			} else {
				min = findMin(min,IntCell.class.cast(row.getCell(valueColumnIndex)));
			}
		}
		return min;
	}
	
	protected long findMin(final long currentMin, final LongCell cell) {
		long cellValue = cell.getLongValue();
		return cellValue < currentMin ? cellValue : currentMin;
	}
	
	protected long findMin(final long currentMin, final IntCell cell) {
		return findMin(currentMin,cell);
	}
}
