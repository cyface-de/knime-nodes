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
