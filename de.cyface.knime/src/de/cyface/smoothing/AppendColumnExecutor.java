/**
 * 
 */
package de.cyface.smoothing;

import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DataTableSpecCreator;
import org.knime.core.data.append.AppendedColumnRow;
import org.knime.core.data.def.DoubleCell;
import org.knime.core.node.defaultnodesettings.SettingsModelString;

/**
 * @author Klemens Muthmann
 * @version 1.0.0
 * @since 1.0.0
 */
public class AppendColumnExecutor implements Execution {
	
	private final SettingsModelString appendColumnNameInputSettingsModel;

	public AppendColumnExecutor(final SettingsModelString appendColumnNameInputSettingsModel) {
		if(appendColumnNameInputSettingsModel==null) throw new IllegalArgumentException("Invalid null argument: appendColumnNameInputSettingsModel");
		
		this.appendColumnNameInputSettingsModel = appendColumnNameInputSettingsModel;
	}

	@Override
	public DataTableSpec getOutputSpec(final DataTableSpec inputSpec) {
		if(inputSpec==null) throw new IllegalArgumentException("Invalid null argument: inputSpec");
		
		String outputColumnName = appendColumnNameInputSettingsModel.getStringValue();
		return (new DataTableSpecCreator()).addColumns(inputSpec).addColumns(new DataColumnSpecCreator(outputColumnName==null?"<unknown>":outputColumnName, DoubleCell.TYPE).createSpec()).createSpec();
	}

	@Override
	public DataRow createResultRow(final DataRow baseRow, final DataCell resultCell, final int inputColumnIndex) {
		if(baseRow==null) throw new IllegalArgumentException("Invalid null argument: baseRow");
		if(resultCell==null) throw new IllegalArgumentException("Invalid null argument: resultCell");
		
		return new AppendedColumnRow(baseRow, resultCell);
	}

}
