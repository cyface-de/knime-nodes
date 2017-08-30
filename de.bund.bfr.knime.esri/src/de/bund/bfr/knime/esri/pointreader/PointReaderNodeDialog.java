/*******************************************************************************
 * Copyright (c) 2016 German Federal Institute for Risk Assessment (BfR)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Contributors:
 *     Department Biological Safety - BfR
 *******************************************************************************/
package de.bund.bfr.knime.esri.pointreader;

import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentFileChooser;
import org.knime.core.node.defaultnodesettings.DialogComponentStringSelection;
import org.knime.core.node.defaultnodesettings.SettingsModelString;

import de.bund.bfr.knime.esri.EsriUtils;

/**
 * <code>NodeDialog</code> for the "PointReader" Node.
 * 
 * 
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more
 * complex dialog please derive directly from
 * {@link org.knime.core.node.NodeDialogPane}.
 * 
 * @author Christian Thoens
 */
public class PointReaderNodeDialog extends DefaultNodeSettingsPane {

	/**
	 * New pane for configuring the PointReader node.
	 */
	protected PointReaderNodeDialog() {
		DialogComponentFileChooser shpDialog = new DialogComponentFileChooser(
				new SettingsModelString(PointReaderNodeModel.CFG_SHP_FILE, null), "ShpFileHistory", ".shp");
		DialogComponentStringSelection charsetDialog = new DialogComponentStringSelection(
				new SettingsModelString(PointReaderNodeModel.CFG_CHARSET, PointReaderNodeModel.DEFAULT_CHARSET),
				"Character Encoding", EsriUtils.CHARSETS);

		shpDialog.setBorderTitle("SHP File");
		addDialogComponent(shpDialog);
		addDialogComponent(charsetDialog);
	}
}
