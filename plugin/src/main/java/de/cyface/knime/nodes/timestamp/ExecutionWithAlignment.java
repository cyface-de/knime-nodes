/*
 * Copyright 2018 Cyface GmbH
 *
 * This file is part of the Cyface Nodes.
 *
 * The Cyface Nodes is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Cyface Nodes is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Cyface Nodes. If not, see <http://www.gnu.org/licenses/>.
 */
package de.cyface.knime.nodes.timestamp;

import org.knime.core.data.DataCell;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTable;
import org.knime.core.data.IntValue;
import org.knime.core.data.LongValue;

/**
 * An executor that aligns the timestamp from both tables to zero prior to association.
 *
 * @author Klemens Muthmann
 * @version 1.0.1
 * @since 1.0.0
 */
public final class ExecutionWithAlignment extends ExecutionWithoutAlignment {

    /**
     * Calculates the alignment necessary to normalize all timestamps to a time starting with zero.
     *
     * @param table The table to align.
     * @param columnName The name of the column to align
     * @return The alignment to apply to the column to align it.
     */
    protected long calcTableAlignment(final DataTable table, final String columnName) {
        long min = Long.MAX_VALUE;
        int valueColumnIndex = table.getDataTableSpec().findColumnIndex(columnName);

        for (DataRow row : table) {
            DataCell cell = row.getCell(valueColumnIndex);
            if (cell.getType().isCompatible(LongValue.class)) {
                min = findMin(min, LongValue.class.cast(cell));
            } else {
                min = findMin(min, IntValue.class.cast(cell));
            }
        }
        return min;
    }

    /**
     * Provides the minimum of two <code>long</code> values, one of which is stored in a KNIME <code>LongValue</code>.
     *
     * @param currentMin The current min Java <code>long</code> value
     * @param cell The value from a KNIME table to compare against.
     * @return The minimum <code>long</code> value with respect to the two provided inputs.
     * @see #findMin(long, IntValue) for a similar functions with <code>int</code> values.
     */
    protected long findMin(final long currentMin, final LongValue cell) {
        long cellValue = cell.getLongValue();
        return cellValue < currentMin ? cellValue : currentMin;
    }

    /**
     * Provides the minimum of two <code>int</code> values, one of which is stored in a KNIME <code>IntValue</code>.
     *
     * @param currentMin The current min Java <code>int</code> value
     * @param cell The value from a KNIME table to compare against.
     * @return The minimum <code>int</code> value with respect to the two provided inputs.
     * @see #findMin(long, IntValue) for a similar functions with <code>long</code> values.
     */
    protected long findMin(final long currentMin, final IntValue cell) {
        return findMin(currentMin, cell);
    }
}
