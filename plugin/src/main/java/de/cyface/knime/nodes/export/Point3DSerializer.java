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
package de.cyface.knime.nodes.export;

import java.nio.ByteBuffer;

import org.knime.core.data.DataRow;
import org.knime.core.data.def.DoubleCell;
import org.knime.core.data.def.LongCell;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;

/**
 * Serializes a point with 3 coordinates (i.e. acceleration, rotation, direction) into the Cyface binary format. An
 * actual implementation needs to provide mappings from database column names to the properties required for
 * serialization.
 *
 * @author Klemens Muthmann
 * @version 1.0.1
 * @since 1.0.0
 */
abstract class Point3DSerializer {

    /**
     * @return The database name of the column containing the point's X values.
     */
    protected abstract int getXColumnIndex();

    /**
     * @return The database name of the column containing the point's Y values.
     */
    protected abstract int getYColumnIndex();

    /**
     * @return The database name of the column containing the point's Z values.
     */
    protected abstract int getZColumnIndex();

    /**
     * @return The database name of the column containing the point's timestamp.
     */
    protected abstract int getTimestampColumnIndex();

    /**
     * Serializes all the points from the measurement identified by the provided
     * <code>measurementIdentifier</code>.
     *
     * @param measurementIdentifier The device wide unqiue identifier of the measurement to serialize.
     * @param context The KNIME <code>ExecutionContext</code> used to handle cancellation and report progress.
     * @param itemsToProcess The total amount of items to process (geo locations and 3D points).
     * @return A <code>byte</code> array containing all the data.
     * @throws CanceledExecutionException Called if execution is cancelled during processing of these 3D points.
     */
    byte[] serialize(final BufferedDataTable pointTAble, final ExecutionContext context, final long itemsToProcess)
            throws CanceledExecutionException {
        final ByteBuffer buffer = ByteBuffer
                .allocate((int)pointTAble.size() * (Long.BYTES + 3 * Double.BYTES));
        double processedItems = .0;
        final ExecutionMonitor monitor = context.createSubProgress(((double)pointTAble.size())/itemsToProcess);

        for (final DataRow row : pointTAble) {
            context.checkCanceled();
            monitor.setProgress(processedItems/pointTAble.size());
            processedItems += 1.0;

            buffer.putLong(((LongCell)row.getCell(getTimestampColumnIndex())).getLongValue());
            buffer.putDouble(((DoubleCell)row.getCell(getXColumnIndex())).getDoubleValue());
            buffer.putDouble(((DoubleCell)row.getCell(getYColumnIndex())).getDoubleValue());
            buffer.putDouble(((DoubleCell)row.getCell(getZColumnIndex())).getDoubleValue());
        }
        final byte[] payload = new byte[buffer.capacity()];
        ((ByteBuffer)buffer.duplicate().clear()).get(payload);
        // if we want to switch from write to read mode on the byte buffer we need to .flip() !!
        return payload;
    }
}
