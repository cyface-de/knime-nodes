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

import org.knime.core.data.DataTable;

public final class InputData {
	
	private final DataTable firstTable;
	private final DataTable secondTable;
	private final String firstTimestampColumnName;
	private final String secondTimestampColumnName;
	

	public InputData(final DataTable firstTable, final DataTable secondTable, final String firstTimestampColumnName, final String secondTimestampColumnName) {
		this.firstTable = firstTable;
		this.secondTable = secondTable;
		this.firstTimestampColumnName = firstTimestampColumnName;
		this.secondTimestampColumnName = secondTimestampColumnName;
	}


	public DataTable getFirstTable() {
		return firstTable;
	}


	public DataTable getSecondTable() {
		return secondTable;
	}


	public String getFirstTimestampColumnName() {
		return firstTimestampColumnName;
	}


	public String getSecondTimestampColumnName() {
		return secondTimestampColumnName;
	}
	
	

}
