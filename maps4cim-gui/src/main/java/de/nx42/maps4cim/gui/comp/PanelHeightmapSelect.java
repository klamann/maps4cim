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

import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;

import de.nx42.maps4cim.gui.window.HeightmapWindow;

public class PanelHeightmapSelect extends PanelFileSelect {

    private static final long serialVersionUID = -9113819786466620682L;

    protected HeightmapWindow hw;
    protected ActionListener actionDone;

    // TODO this is bad design, remove it / integrate in heightmapwindow

    public PanelHeightmapSelect() {
        super();
    }

    public PanelHeightmapSelect(String selectedPath, String btnBrowseText, JFileChooser fileChooser) {
        super(selectedPath, btnBrowseText, fileChooser);
    }

    public PanelHeightmapSelect(String selectedPath, String btnBrowseText, JFileChooser fileChooser, ActionListener actionDone) {
        super(selectedPath, btnBrowseText, fileChooser);
        this.actionDone = actionDone;
    }

    public void openHeightmapWindow() {
        if(hw != null) {
            hw.setVisible(true);
        } else {
            openFile();
        }
    }

    public void openHeightmapWindow(String min, String max) {
        if(hw != null) {
            hw.setMinValue(min);
            hw.setMaxValue(max);
        }
        openHeightmapWindow();
    }

    /* (non-Javadoc)
     * @see de.nx42.maps4cim.gui.comp.PanelFileSelect#validFileSelectedAction(java.io.File)
     */
    @Override
    protected void validFileSelectedAction(File f) {
        if(hw == null) {
            hw = new HeightmapWindow();
            if(actionDone != null) {
                hw.registerListenerDone(actionDone);
            }
        }
        hw.updateFile(f);
        hw.setVisible(true);

        super.validFileSelectedAction(f);
    }

    /**
     * @return the HeightmapWindow
     */
    public HeightmapWindow getHeightmapWindow() {
        return hw;
    }

}
