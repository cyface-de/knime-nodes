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

import org.knime.core.data.DataTableSpec;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;

/**
 * An interface describing the different ways to run timestamp alignment.
 * 
 * @author Klemens Muthmann
 * @version 1.0.0
 * @since 1.0.0
 */
public interface Execution {

    /**
     * Executs the <code>Execution</code> according to its implementation.
     * 
     * @param input The input data to process.
     * @param outputTableSpec A table specification describing the output table.
     * @param context The KNIME <code>ExecutionContext</code>.
     * @return The result table with aligned timestamps.
     * @throws CanceledExecutionException If execution was canceled by the user.
     */
    BufferedDataTable[] execute(InputData input, DataTableSpec outputTableSpec, ExecutionContext context)
            throws CanceledExecutionException;

}
