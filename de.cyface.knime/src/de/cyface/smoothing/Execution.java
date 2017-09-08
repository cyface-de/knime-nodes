package de.cyface.smoothing;

import org.knime.core.data.DataCell;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.node.NodeModel;

/**
 * <p>
 * An {@link Execution} is a strategy providing detailed functionality for the
 * {@link NodeModel}s execute method. Usually it depends on the selected
 * options, which {@link Execution} is used.
 * </p>
 * 
 * @author Klemens Muthmann
 * @version 1.0.0
 * @since 1.0.0
 */
public interface Execution {
	/**
	 * <p>
	 * Converts the specification from the input table to an output
	 * specification.
	 * </p>
	 * 
	 * @param inputSpec
	 *            The specification from the input table.
	 * @return The expected specification for the output table.
	 */
	DataTableSpec getOutputSpec(DataTableSpec inputSpec);

	/**
	 * <p>
	 * Creates a new result row from a base row from the input table a
	 * {@link DataCell} containing the calculated smoothed signal and an index
	 * denoting the input table column with the input data.
	 * </p>
	 * 
	 * @param baseRow
	 *            The input table base row containing the input values.
	 * @param resultCell
	 *            The result value already wrapped in an appropriate
	 *            {@link DataCell}.
	 * @param inputColumnIndex
	 *            The index of the column with the input data from the input
	 *            table.
	 * @return A new {@link DataRow} for the output table.
	 */
	DataRow createResultRow(DataRow baseRow, DataCell resultCell, int inputColumnIndex);
}
