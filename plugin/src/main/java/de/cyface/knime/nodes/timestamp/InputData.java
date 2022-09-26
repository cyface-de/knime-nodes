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

import org.knime.core.data.DataTable;

/**
 * A value object wrapping everything required as input for one execution of this node.
 *
 * @author Klemens Muthmann
 * @version 1.0.0
 * @since 1.0.0
 */
public final class InputData {
    /**
     * The first table to align.
     */
    private final DataTable firstTable;
    /**
     * The second table to align.
     */
    private final DataTable secondTable;
    /**
     * The name of the column to align from the first table.
     */
    private final String firstTimestampColumnName;
    /**
     * The name of the column to align from the second table.
     */
    private final String secondTimestampColumnName;

    /**
     * Creates a new completely initialized object of this class.
     *
     * @param firstTable The first table to align.
     * @param secondTable The second table to align.
     * @param firstTimestampColumnName The name of the column to align from the first table.
     * @param secondTimestampColumnName The name of the column to align from the second table.
     */
    public InputData(final DataTable firstTable, final DataTable secondTable, final String firstTimestampColumnName,
            final String secondTimestampColumnName) {
        this.firstTable = firstTable;
        this.secondTable = secondTable;
        this.firstTimestampColumnName = firstTimestampColumnName;
        this.secondTimestampColumnName = secondTimestampColumnName;
    }

    /**
     * @return The first table to align.
     */
    public DataTable getFirstTable() {
        return firstTable;
    }

    /**
     * @return The second table to align.
     */
    public DataTable getSecondTable() {
        return secondTable;
    }

    /**
     * @return The name of the column to align from the first table.
     */
    public String getFirstTimestampColumnName() {
        return firstTimestampColumnName;
    }

    /**
     * @return The name of the column to align from the second table.
     */
    public String getSecondTimestampColumnName() {
        return secondTimestampColumnName;
    }
}
