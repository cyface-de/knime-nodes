package de.cyface.timestamp;

import org.knime.core.data.DataTable;
import org.knime.core.data.DataTableSpec;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.ExecutionContext;

public interface Execution {

	BufferedDataTable[] execute(InputData input, DataTableSpec outputTableSpec, ExecutionContext context);

}
