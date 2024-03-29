/*
 * Copyright 2019 Cyface GmbH
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
package de.cyface.knime.nodes.reader;

import java.io.File;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StreamCorruptedException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.RowKey;
import org.knime.core.data.def.DefaultRow;
import org.knime.core.data.def.DoubleCell;
import org.knime.core.data.def.IntCell;
import org.knime.core.data.def.LongCell;
import org.knime.core.data.def.StringCell;
import org.knime.core.node.BufferedDataContainer;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.defaultnodesettings.SettingsModelString;

import de.cyface.knime.dialog.FileSelectionNodeOption;
import de.cyface.knime.dialog.StringSelectionNodeOption;

/**
 * The <code>NodeModel</code> for the Cyface binary reader node. This class
 * represents the heart of the node. It provides capabilities to validate input
 * data and contains the algorithm producing the output.
 *
 * @author Klemens Muthmann
 * @author Armin Schnabel
 * @version 2.1.1
 * @since 2.2.0
 */
public final class BinaryFormatReaderNodeModel extends NodeModel {

    /**
     * Provides access to the configuration option for the location of the input
     * file.
     */
    private final SettingsModelString inputFileSettings;
    /**
     * The settings model storing the information about which type of file to read.
     */
    private final SettingsModelString inputTypeSettings;

    /**
     * Creates a new completely initialized instance of this class, providing a node
     * with no input and four output ports.
     *
     * @param inputFileSelectionOption
     * @param stringSelectionOption
     */
    protected BinaryFormatReaderNodeModel(final FileSelectionNodeOption inputFileSelectionOption,
            final StringSelectionNodeOption stringSelectionOption) {
        super(0, 5);
        this.inputFileSettings = inputFileSelectionOption.getSettingsModel();
        this.inputTypeSettings = stringSelectionOption.getSettingsModel();
    }

    @Override
    protected DataTableSpec[] configure(final DataTableSpec[] inSpecs) throws InvalidSettingsException {
        final String inputFilePath = inputFileSettings.getStringValue();

        final File inputFile = new File(inputFilePath);
        if (!(inputFile.exists())) {
            throw new InvalidSettingsException("Input file to read Cyface binary data from, does not exist!");
        }
        final String inputType = inputTypeSettings.getStringValue();
        if (inputType.equals("Measurement")) {
            try (final InputStream inputFileStream = new FileInputStream(inputFilePath)) {
                if (readShort(inputFileStream) != (short)1) {
                    throw new InvalidSettingsException(
                            "Either the input file was no uncompressed file in Cyface binary format or it has the wrong version. Only version 1 is supported at the moment.");
                }
            } catch (final IOException e) {
                throw new InvalidSettingsException(e);
            }
        } else if (inputType.equals("Events")) {
            try (final InputStream inputFileStream = new FileInputStream(inputFilePath)) {
                if (readShort(inputFileStream) != (short)1) {
                    throw new InvalidSettingsException(
                            "Either the input file was no uncompressed file in Cyface Events binary format or it has the wrong version. Only version 1 is supported at the moment.");
                }
            } catch (final IOException e) {
                throw new InvalidSettingsException(e);
            }
        } else {
            if ((inputFile.length() % 32) != 0) {
                throw new InvalidSettingsException(
                        "The input file seems to be no valid uncompressed 3D point data file (i.e. accelerations, rotations or directions). If you selected a measurement file please select Measurement as file type. Otherwise check that your file is actually valid.");
            }
        }

        return new DataTableSpec[] {getGeoLocationsTableSpec(), getPoint3DTableSpec(), getPoint3DTableSpec(),
                getPoint3DTableSpec(), getEventsTableSpec()};
    }

    @Override
    protected BufferedDataTable[] execute(final BufferedDataTable[] inData, final ExecutionContext exec)
            throws Exception {
        final String inputFilePath = inputFileSettings.getStringValue();
        final File inputFile = new File(inputFilePath);

        try (final InputStream inputFileStream = new FileInputStream(inputFile)) {
            final String inputType = inputTypeSettings.getStringValue();
            if (inputType.equals("Measurement") || inputType.equals("Events")) {
                inputFileStream.skip(2); // Read over the file format version
            }

            final int geoLocationsCount = inputType.equals("Measurement") ? readInt(inputFileStream) : 0;
            final int accelerationsCount = inputType.equals("Measurement") ? readInt(inputFileStream)
                    : inputType.equals("Accelerations") ? (int)inputFile.length() / 32 : 0;
            final int rotationsCount = inputType.equals("Measurement") ? readInt(inputFileStream)
                    : inputType.equals("Rotations") ? (int)inputFile.length() / 32 : 0;
            final int directionsCount = inputType.equals("Measurement") ? readInt(inputFileStream)
                    : inputType.equals("Directions") ? (int)inputFile.length() / 32 : 0;
            final int eventsCount = inputType.equals("Events") ? readInt(inputFileStream) : 0;
            exec.checkCanceled();
            final int processingSteps = geoLocationsCount + accelerationsCount + rotationsCount + directionsCount + eventsCount;

            final ExecutionMonitor geoLocationsProgressMonitor = exec
                    .createSubProgress((double)geoLocationsCount / processingSteps);
            final BufferedDataTable geoLocationsTable = inputType.equals("Measurement")
                    ? readGeoLocationsTable(inputFileStream, geoLocationsCount, exec,
                            geoLocationsProgressMonitor, processingSteps)
                    : createEmptyTable(getGeoLocationsTableSpec(), exec);

            exec.checkCanceled();
            final ExecutionMonitor accelerationsProgressMonitor = exec
                    .createSubProgress((double)accelerationsCount / processingSteps);
            final BufferedDataTable accelerationsTable = inputType.equals("Measurement")
                    || inputType.equals("Accelerations")
                            ? readPoint3DTable(inputFileStream, accelerationsCount, exec,
                                    accelerationsProgressMonitor, processingSteps)
                            : createEmptyTable(getPoint3DTableSpec(), exec);

            exec.checkCanceled();
            final ExecutionMonitor rotationsProgressMonitor = exec
                    .createSubProgress((double)rotationsCount / processingSteps);
            final BufferedDataTable rotationsTable = inputType.equals("Measurement") || inputType.equals("Rotations")
                    ? readPoint3DTable(inputFileStream, rotationsCount, exec,
                            rotationsProgressMonitor, processingSteps)
                    : createEmptyTable(getPoint3DTableSpec(), exec);

            exec.checkCanceled();
            final ExecutionMonitor directionsProgressMonitor = exec
                    .createSubProgress((double)directionsCount / processingSteps);
            final BufferedDataTable directionsTable = inputType.equals("Measurement") || inputType.equals("Directions")
                    ? readPoint3DTable(inputFileStream, directionsCount, exec,
                            directionsProgressMonitor, processingSteps)
                    : createEmptyTable(getPoint3DTableSpec(), exec);

            final ExecutionMonitor eventsProgressMonitor = exec
                    .createSubProgress((double)eventsCount / processingSteps);
            final BufferedDataTable eventsTable = inputType.equals("Events")
                    ? readEventsTable(inputFileStream, eventsCount, exec,
                            eventsProgressMonitor, processingSteps)
                    : createEmptyTable(getEventsTableSpec(), exec);

            return new BufferedDataTable[] {geoLocationsTable, accelerationsTable, rotationsTable, directionsTable, eventsTable};
        }
    }

    /**
     * Creates a new empty data table for the provided <code>DataTableSpec</code>.
     *
     * @param spec The specification for which to create an empty data table.
     * @param context The KNIME <code>ExecutionContext</code> within the current execution.
     * @return An empty <code>BufferedDataTable</code> for the provided <code>DataTableSpec</code>.
     */
    private BufferedDataTable createEmptyTable(final DataTableSpec spec, final ExecutionContext context) {
        final BufferedDataContainer container = context.createDataContainer(spec);
        container.close();
        return container.getTable();
    }

    /**
     * Reads the next two byte from the <code>input</code> as a <code>short</code>
     * value. The bytes should be ordered in Java typical big endian format.
     *
     * @param input An open input stream capable of providing at least two bytes of
     *            data.
     * @return The <code>short</code> value read from the input stream.
     * @throws IOException If reading the stream fails
     */
    private short readShort(final InputStream input) throws IOException {
        return read(input, Short.BYTES).getShort();
    }

    /**
     * Reads the next four byte from the <code>input</code> as an <code>int</code>
     * value. The bytes should be ordered in Java typical big endian format.
     *
     * @param input An open input stream capable of providing at least four bytes of
     *            data.
     * @return The <code>double</code> value read from the input stream.
     * @throws IOException If reading the stream fails
     */
    private int readInt(final InputStream input) throws IOException {
        return read(input, Integer.BYTES).getInt();
    }

    /**
     * Reads the next eight byte from the <code>input</code> as a <code>long</code>
     * value. The bytes should be ordered in Java typical big endian format.
     *
     * @param input An open input stream capable of providing at least eight bytes
     *            of data.
     * @return The <code>long</code> value read from the input stream.
     * @throws IOException If reading the stream fails
     */
    private long readLong(final InputStream input) throws IOException {
        return read(input, Long.BYTES).getLong();
    }

    /**
     * Reads the next eight byte from the <code>input</code> as a
     * <code>double</code> value. The bytes should be ordered in Java typical big
     * endian format.
     *
     * @param input An open input stream capable of providing at least eight bytes
     *            of data
     * @return The <code>double</code> value read from the input stream
     * @throws IOException If reading the stream fails
     */
    private double readDouble(final InputStream input) throws IOException {
        return read(input, Double.BYTES).getDouble();
    }

    /**
     * Reads the next bytes from the <code>input</code> as a <code>String</code> value.
     * The number of bytes read is defined by the parameter <code>bytes</code>.
     * The bytes should be ordered in Java typical big endian format.
     * The <code>StandardCharsets.UTF_8</code> is expected.
     *
     * @param input An open input stream capable of providing at least eight bytes
     *            of data
     * @return The <code>double</code> value read from the input stream
     * @throws IOException If reading the stream fails
     */
    private String readString(final InputStream input, final int bytes) throws IOException {
        final ByteBuffer byteBuffer = read(input, bytes);
        return StandardCharsets.UTF_8.decode(byteBuffer).toString();
    }

    /**
     * Read a certain amount of bytes from the input stream and return them in form of a <code>ByteBuffer</code>.
     *
     * @param input An open <code>InputStream</code> capable of providing at least <code>bytes</code> bytes of data.
     * @param bytes The number of bytes to read.
     * @return A <code>ByteBuffer</code> containing the read bytes
     * @throws IOException If reading was not successful.
     * @throws StreamCorruptedException If end of stream was reached during this read operation.
     */
    private ByteBuffer read(final InputStream input, int bytes) throws IOException {
        final ByteBuffer buffer = ByteBuffer.allocate(bytes);
        for (int i = 0; i < bytes; i++) {
            int readByteAsInt = input.read();
            if (readByteAsInt == -1) {
                throw new StreamCorruptedException("Unexpected end of stream reached!");
            }
            byte readByte = (byte)readByteAsInt;
            buffer.put(readByte);
        }
        buffer.position(0);
        return buffer;
    }

    /**
     * Reads <code>count</code> geo locations from the <code>input</code> and
     * provides them in the form of a new <code>BufferedDataTable</code>.
     *
     * @param input The stream to read the geo locations from
     * @param count The number of geo locations to read
     * @param context The KNIME <code>ExecutionContext</code>
     * @param monitor An <code>ExecutionMonitor</code> used to track reading
     *            progress. This should be a sub monitor of the
     *            <code>ExecutionContext</code>
     * @param processingSteps The number of total processing steps during execution
     *            of this node. This is required to track progress
     *            appropriately
     * @return A new <code>BufferedDataTable</code> containing all the geo locations
     *         read
     * @throws IOException If reading the stream fails
     * @throws CanceledExecutionException If execution was canceled by the user
     *             while reading geo locations.
     */
    private BufferedDataTable readGeoLocationsTable(final InputStream input, final int count,
            final ExecutionContext context, final ExecutionMonitor monitor, final int processingSteps)
            throws IOException, CanceledExecutionException {
        final BufferedDataContainer geoLocationsContainer = context.createDataContainer(getGeoLocationsTableSpec());
        for (int i = 0; i < count; i++) {
            final long timestamp = readLong(input);
            final double latitude = readDouble(input);
            final double longitude = readDouble(input);
            final double speed = readDouble(input);
            final int accuracy = readInt(input);

            final DataCell timestampCell = new LongCell(timestamp);
            final DataCell latitudeCell = new DoubleCell(latitude);
            final DataCell longitudeCell = new DoubleCell(longitude);
            final DataCell speedCell = new DoubleCell(speed);
            final DataCell accuracyCell = new IntCell(accuracy);
            final DataRow row = new DefaultRow(new RowKey(String.valueOf(i)), timestampCell, latitudeCell,
                    longitudeCell, speedCell, accuracyCell);
            geoLocationsContainer.addRowToTable(row);
            context.checkCanceled();
            monitor.setProgress((double)i / processingSteps);
        }
        geoLocationsContainer.close();
        return geoLocationsContainer.getTable();
    }

    /**
     * Reads <code>count</code> 3D points from the <code>input</code> and provides them in the form of a new
     * <code>BufferedDataTable</code>. These might be accelerations, rotations or directions ins space.
     *
     * @param input The stream to read the points from
     * @param count The number of points to read
     * @param context The KNIME <code>ExecutionContext</code>
     * @param monitor An <code>ExecutionMonitor</code> used to track reading
     *            progress. This should be a sub monitor of the
     *            <code>ExecutionContext</code>
     * @param processingSteps The number of total processing steps during execution
     *            of this node. This is required to track progress
     *            appropriately
     * @return
     * @throws IOException If reading the stream fails
     * @throws CanceledExecutionException If execution was canceled by the user
     *             while reading accelerations
     */
    private BufferedDataTable readPoint3DTable(final InputStream input, final int count, final ExecutionContext context,
            final ExecutionMonitor monitor, final int processingSteps) throws IOException, CanceledExecutionException {
        final BufferedDataContainer point3DContainer = context.createDataContainer(getPoint3DTableSpec());
        for (int i = 0; i < count; i++) {
            final long timestamp = readLong(input);
            final double xValue = readDouble(input);
            final double yValue = readDouble(input);
            final double zValue = readDouble(input);

            final DataCell timestampCell = new LongCell(timestamp);
            final DataCell xCell = new DoubleCell(xValue);
            final DataCell yCell = new DoubleCell(yValue);
            final DataCell zCell = new DoubleCell(zValue);
            final DataRow row = new DefaultRow(new RowKey(String.valueOf(i)), timestampCell, xCell, yCell, zCell);
            point3DContainer.addRowToTable(row);
            context.checkCanceled();
            monitor.setProgress((double)i / processingSteps);
        }
        point3DContainer.close();
        return point3DContainer.getTable();
    }

    /**
     * Reads <code>count</code> events from the <code>input</code> and
     * provides them in the form of a new <code>BufferedDataTable</code>.
     *
     * @param input The stream to read the events from
     * @param count The number of events to read
     * @param context The KNIME <code>ExecutionContext</code>
     * @param monitor An <code>ExecutionMonitor</code> used to track reading
     *            progress. This should be a sub monitor of the
     *            <code>ExecutionContext</code>
     * @param processingSteps The number of total processing steps during execution
     *            of this node. This is required to track progress
     *            appropriately
     * @return A new <code>BufferedDataTable</code> containing all the events
     *         read
     * @throws IOException If reading the stream fails
     * @throws CanceledExecutionException If execution was canceled by the user
     *             while reading events.
     */
    private BufferedDataTable readEventsTable(final InputStream input, final int count,
            final ExecutionContext context, final ExecutionMonitor monitor, final int processingSteps)
            throws IOException, CanceledExecutionException {
        final BufferedDataContainer eventsContainer = context.createDataContainer(getEventsTableSpec());
        for (int i = 0; i < count; i++) {
        	// Bytes: long timestamp, short event type enum, short value byte length, variable value UTF-8 bytes
            final long timestamp = readLong(input);
            final short eventType = readShort(input);
            final short valueByteLength = readShort(input);
            final String value = readString(input, (int) valueByteLength);

            final DataCell timestampCell = new LongCell(timestamp);
            final DataCell eventTypeCell = new IntCell(eventType);
            final DataCell valueCell = new StringCell(value);
            final DataRow row = new DefaultRow(new RowKey(String.valueOf(i)), timestampCell, eventTypeCell,
            		valueCell);
            eventsContainer.addRowToTable(row);
            context.checkCanceled();
            monitor.setProgress((double)i / processingSteps);
        }
        eventsContainer.close();
        return eventsContainer.getTable();
    }

    /**
     * @return The KNIME table specification for the geo locations table.
     */
    private DataTableSpec getGeoLocationsTableSpec() {
        final DataColumnSpec timestampColumnSpec = new DataColumnSpecCreator("Timestamp", LongCell.TYPE).createSpec();
        final DataColumnSpec latitudeColumnSpec = new DataColumnSpecCreator("Latitude", DoubleCell.TYPE).createSpec();
        final DataColumnSpec longitudeColumnSpec = new DataColumnSpecCreator("Longitude", DoubleCell.TYPE).createSpec();
        final DataColumnSpec speedColumnSpec = new DataColumnSpecCreator("Speed", DoubleCell.TYPE).createSpec();
        final DataColumnSpec accuracyColumnSpec = new DataColumnSpecCreator("Accuracy", IntCell.TYPE).createSpec();
        final DataTableSpec spec = new DataTableSpec(timestampColumnSpec, latitudeColumnSpec, longitudeColumnSpec,
                speedColumnSpec, accuracyColumnSpec);
        return spec;
    }

    /**
     * @return The KNIME table specification for each of the 3D point tables (i.e. accelerations, rotations,
     *         directions).
     */
    private DataTableSpec getPoint3DTableSpec() {
        final DataColumnSpec timestampColumnSpec = new DataColumnSpecCreator("Timestamp", LongCell.TYPE).createSpec();
        final DataColumnSpec xColumnSpec = new DataColumnSpecCreator("x", DoubleCell.TYPE).createSpec();
        final DataColumnSpec yColumnSpec = new DataColumnSpecCreator("y", DoubleCell.TYPE).createSpec();
        final DataColumnSpec zColumnSpec = new DataColumnSpecCreator("z", DoubleCell.TYPE).createSpec();
        final DataTableSpec spec = new DataTableSpec(timestampColumnSpec, xColumnSpec, yColumnSpec, zColumnSpec);
        return spec;
    }

    /**
     * @return The KNIME table specification for the events table.
     */
    private DataTableSpec getEventsTableSpec() {
        final DataColumnSpec timestampColumnSpec = new DataColumnSpecCreator("Timestamp", LongCell.TYPE).createSpec();
        final DataColumnSpec eventTypeColumnSpec = new DataColumnSpecCreator("EventType", IntCell.TYPE).createSpec();
        final DataColumnSpec valueColumnSpec = new DataColumnSpecCreator("Value", StringCell.TYPE).createSpec();
        final DataTableSpec spec = new DataTableSpec(timestampColumnSpec, eventTypeColumnSpec, valueColumnSpec);
        return spec;
    }

    @Override
    protected void loadInternals(final File nodeInternDir, final ExecutionMonitor exec)
            throws IOException, CanceledExecutionException {
        // No internal data. Nothing to do here.

    }

    @Override
    protected void saveInternals(final File nodeInternDir, final ExecutionMonitor exec)
            throws IOException, CanceledExecutionException {
        // No internal data. Nothing to do here.

    }

    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) {
        inputFileSettings.saveSettingsTo(settings);
        inputTypeSettings.saveSettingsTo(settings);
    }

    @Override
    protected void validateSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        inputFileSettings.validateSettings(settings);
        inputTypeSettings.validateSettings(settings);

    }

    @Override
    protected void loadValidatedSettingsFrom(final NodeSettingsRO settings) throws InvalidSettingsException {
        inputFileSettings.loadSettingsFrom(settings);
        inputTypeSettings.loadSettingsFrom(settings);
    }

    @Override
    protected void reset() {

    }

}
