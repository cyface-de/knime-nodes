package de.cyface.smoothing;

import org.knime.base.data.replace.ReplacedColumnsDataRow;
import org.knime.core.data.DataCell;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;

public class ReplaceColumnExecutor implements Execution {

	@Override
	public DataTableSpec getOutputSpec(final DataTableSpec inputSpec) {
		return inputSpec;
	}

	@Override
	public DataRow createResultRow(final DataRow baseRow, final DataCell resultCell, final int inputColumnIndex) {
		return new ReplacedColumnsDataRow(baseRow, resultCell, inputColumnIndex);
	}

}
