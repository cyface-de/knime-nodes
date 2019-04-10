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
