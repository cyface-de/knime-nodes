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
package de.cyface.timestamp;

import org.knime.core.data.DataCell;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTable;
import org.knime.core.data.IntValue;
import org.knime.core.data.LongValue;

/**
 * An executor that aligns the timestamp from both tables to zero prior to association.
 * 
 * @author Klemens Muthmann
 * @version 1.0.0
 * @since 1.0.0
 */
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
