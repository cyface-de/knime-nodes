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
package de.cyface.knime.nodes.smoothing;

import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DataTableSpecCreator;
import org.knime.core.data.append.AppendedColumnRow;
import org.knime.core.data.def.DoubleCell;
import org.knime.core.node.defaultnodesettings.SettingsModel;
import org.knime.core.node.defaultnodesettings.SettingsModelString;

/**
 * An {@link Execution} for this node appending the results in a new column.
 *
 * @author Klemens Muthmann
 * @version 1.0.0
 * @since 1.0.0
 */
public class AppendColumnExecutor implements Execution {

	/**
	 * The {@link SettingsModel} containing the name of the column to append.
	 */
	private final SettingsModelString appendColumnNameInputSettingsModel;

	/**
	 * Creates a new completely initialized {@link AppendColumnExecutor}.
	 *
	 * @param appendColumnNameInputSettingsModel
	 *            The {@link SettingsModel} containing the name of the column to
	 *            append.
	 */
	public AppendColumnExecutor(final SettingsModelString appendColumnNameInputSettingsModel) {
		if (appendColumnNameInputSettingsModel == null)
			throw new IllegalArgumentException("Invalid null argument: appendColumnNameInputSettingsModel");

		this.appendColumnNameInputSettingsModel = appendColumnNameInputSettingsModel;
	}

	@Override
	public DataTableSpec getOutputSpec(final DataTableSpec inputSpec) {
		if (inputSpec == null)
			throw new IllegalArgumentException("Invalid null argument: inputSpec");

		String outputColumnName = appendColumnNameInputSettingsModel.getStringValue();
		return (new DataTableSpecCreator()).addColumns(inputSpec).addColumns(
				new DataColumnSpecCreator(outputColumnName == null ? "<unknown>" : outputColumnName, DoubleCell.TYPE)
						.createSpec())
				.createSpec();
	}

	@Override
	public DataRow createResultRow(final DataRow baseRow, final DataCell resultCell, final int inputColumnIndex) {
		if (baseRow == null)
			throw new IllegalArgumentException("Invalid null argument: baseRow");
		if (resultCell == null)
			throw new IllegalArgumentException("Invalid null argument: resultCell");

		return new AppendedColumnRow(baseRow, resultCell);
	}

}
