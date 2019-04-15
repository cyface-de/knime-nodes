/*
 * Copyright 2019 Cyface GmbH
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
package de.cyface.knime.dialog;

import javax.swing.JFileChooser;

import org.knime.core.node.defaultnodesettings.DialogComponentFileChooser;

/**
 * A node option presenting the user with the choice to select content from the file system to load into the node.
 * 
 * @author Klemens Muthmann
 * @since 2.3.0
 * @version 1.0.0
 */
public final class FileSelectionNodeOption extends StringSettingsModelOption {

    /**
     * @param configIdentifier
     * @param historyID
     * @param type
     * @param selectionType
     */
    public FileSelectionNodeOption(final String configIdentifier, final String historyID,
            final DialogType type, final SelectionType selectionType) {
        super(configIdentifier, System.getProperty("user.home"));
        setComponent(new DialogComponentFileChooser(getSettingsModel(), historyID, type.getFileChooserValue(),
                selectionType == SelectionType.DIRECTORIES ? true : false));
    }

    /**
     * The type of dialog to use. This maps directly to the appropriate constants provided by <code>JFileChooser</code>.
     * This class only exists to avoid usage of old style Java 4 int constant enums.
     * 
     * @author Klemens Muthmann
     * @version 1.0.0
     * @since 2.3.0
     */
    public static enum DialogType {
        /**
         * A dialog that allows to open files.
         */
        OPEN_DIALOG(JFileChooser.OPEN_DIALOG),
        /**
         * A dialog that allows to save files
         */
        SAVE_DIALOG(JFileChooser.SAVE_DIALOG),
        /**
         * A developer specified file operation dialog.
         */
        CUSTOM_DIALOG(
                JFileChooser.CUSTOM_DIALOG);

        /**
         * The <code>JFileChooser</code> value corresponding to this option.
         */
        private final int fileChooserValue;

        /**
         * Creates a new completely initialized object of this class.
         * 
         * @param fileChooserValue The <code>JFileChooser</code> value corresponding to this option.
         */
        private DialogType(final int fileChooserValue) {
            this.fileChooserValue = fileChooserValue;
        }

        /**
         * @return The <code>JFileChooser</code> value corresponding to this option.
         */
        int getFileChooserValue() {
            return fileChooserValue;
        }
    }

    /**
     * Specifies what kind of objects the dialog should support to select
     * 
     * @author Klemens Muthmann
     * @since 2.3.0
     * @version 1.0.0
     */
    public static enum SelectionType {
        /**
         * The file supports only the selection of directories. The node knows which files to use from the selected
         * directory.
         */
        DIRECTORIES,
        /**
         * The file supports the selection of a single file to process by the node.
         */
        FILES;
    }

}
