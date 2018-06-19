package de.cyface.smoothing;

import org.knime.base.data.replace.ReplacedColumnsDataRow;
import org.knime.core.data.DataCell;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;

/**
 * <p>
 * An {@link Execution} replacing the input column with the result column.
 * </p>
 * 
 * @author Klemens Muthmann
 * @version 1.0.0
 * @since 1.0.0
 */
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
