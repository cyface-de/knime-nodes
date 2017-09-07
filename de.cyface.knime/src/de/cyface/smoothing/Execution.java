package de.cyface.smoothing;

import org.knime.core.data.DataCell;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;

public interface Execution {
	DataTableSpec getOutputSpec(DataTableSpec inputSpec);
	DataRow createResultRow(DataRow baseRow, DataCell resultCell, int inputColumnIndex);
}
