package de.cyface.export;

import java.nio.ByteBuffer;

import org.knime.core.data.DataRow;
import org.knime.core.data.def.DoubleCell;
import org.knime.core.data.def.LongCell;
import org.knime.core.node.BufferedDataTable;

/**
 * Serializes a point with 3 coordinates (i.e. acceleration, rotation, direction) into the Cyface binary format. An
 * actual implementation needs to provide mappings from database column names to the properties required for
 * serialization.
 *
 * @author Klemens Muthmann
 * @version 1.0.0
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
     * @return A <code>byte</code> array containing all the data.
     */
    byte[] serialize(final BufferedDataTable pointCursor) {
        ByteBuffer buffer = ByteBuffer
                .allocate((int)pointCursor.size() * (Long.BYTES + 3 * Double.BYTES));
        for(DataRow row:pointCursor) {
            buffer.putLong(((LongCell)row.getCell(getTimestampColumnIndex())).getLongValue());
            buffer.putDouble(((DoubleCell)row.getCell(getXColumnIndex())).getDoubleValue());
            buffer.putDouble(((DoubleCell)row.getCell(getYColumnIndex())).getDoubleValue());
            buffer.putDouble(((DoubleCell)row.getCell(getZColumnIndex())).getDoubleValue());
        }
        byte[] payload = new byte[buffer.capacity()];
        ((ByteBuffer)buffer.duplicate().clear()).get(payload);
        // if we want to switch from write to read mode on the byte buffer we need to .flip() !!
        return payload;
    }
}
