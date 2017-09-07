package de.cyface.smoothing;

import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DoubleValue;
import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentButtonGroup;
import org.knime.core.node.defaultnodesettings.DialogComponentColumnNameSelection;
import org.knime.core.node.defaultnodesettings.DialogComponentString;
import org.knime.core.node.defaultnodesettings.DialogComponentStringSelection;
import org.knime.core.node.util.ColumnFilter;

/**
 * @author Klemens Muthmann
 * @version 1.0.0
 * @since 1.0.0
 */
public class SmoothingNodeDialog extends DefaultNodeSettingsPane {
	
	public static final String APPEND_OPTION = "Append";
	public static final String REPLACE_OPTION = "Replace";
	
	private static final String FILTER_TYPE_SELECTION_LABEL = "Filter Type";
	private static final String INPUT_COL_SELECTION_LABEL = "Input Column";
	private static final String APPEND_REPLACE_CHOOSER_LABEL = "Append new column or replace input column";
	private static final String APPEND_COLUMN_NAME_INPUT_LABEL = "Appended column name";
	
	private final DialogComponentStringSelection filterTypeSelection;
	private final DialogComponentColumnNameSelection inputColSelection;
	private final DialogComponentButtonGroup appendReplaceChooser;
	private final DialogComponentString appendColumnNameInput;
	
	
	protected SmoothingNodeDialog() {
		super();
		
		ColumnFilter inputColumnFilter = new ColumnFilter() {
			
			@Override
			public boolean includeColumn(DataColumnSpec colSpec) {
				return colSpec.getType().isCompatible(DoubleValue.class);
			}
			
			@Override
			public String allFilteredMsg() {
				return "No valid input columns available! Please provide numeric input columns";
			}
		};
		
		filterTypeSelection = new DialogComponentStringSelection(SmoothingNodeModel.createFilterTypeSelectionSettingsModel(), FILTER_TYPE_SELECTION_LABEL, Filter.RECTANGULAR.getName());
		inputColSelection = new DialogComponentColumnNameSelection(SmoothingNodeModel.createInputColSelectionSettingsModel(), INPUT_COL_SELECTION_LABEL, 1, inputColumnFilter);
		appendReplaceChooser = new DialogComponentButtonGroup(SmoothingNodeModel.createAppendReplaceChooserSettingsModel(), true, APPEND_REPLACE_CHOOSER_LABEL, REPLACE_OPTION, APPEND_OPTION);
		appendColumnNameInput = new DialogComponentString(SmoothingNodeModel.createAppendColumnNameInputSettingsModel(), APPEND_COLUMN_NAME_INPUT_LABEL);
				
		addDialogComponent(filterTypeSelection);
		addDialogComponent(inputColSelection);
		addDialogComponent(appendReplaceChooser);
		addDialogComponent(appendColumnNameInput);
	}
	

}
