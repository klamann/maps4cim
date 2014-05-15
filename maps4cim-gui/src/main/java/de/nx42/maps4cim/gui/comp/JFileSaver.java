/**
 * maps4cim - a real world map generator for CiM 2
 * Copyright 2013 - 2014 Sebastian Straub
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.nx42.maps4cim.gui.comp;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 * source: http://geek.starbean.net/?p=275
 */
public class JFileSaver extends JFileChooser {

    private static final long serialVersionUID = -5688954075587649125L;

    private String extension;

    public JFileSaver(String extension) {
        super();
        this.extension = extension;
    }

    @Override
    public File getSelectedFile() {
        File selectedFile = super.getSelectedFile();

        if (selectedFile != null) {
            String name = selectedFile.getName();
            if (!name.contains(".")) {
                selectedFile = new File(selectedFile.getParentFile(), name + '.' + extension);
            }
        }

        return selectedFile;
    }

    @Override
    public void approveSelection() {
        if (getDialogType() == SAVE_DIALOG) {
            File selectedFile = getSelectedFile();
            if ((selectedFile != null) && selectedFile.exists()) {
                int response = JOptionPane.showConfirmDialog(this,
                    "The file " + selectedFile.getName() + " already exists. Do you want to replace the existing file?",
                    "Ovewrite file",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
                );
                if (response != JOptionPane.YES_OPTION)
                    return;
            }
        }
        super.approveSelection();
    }
}