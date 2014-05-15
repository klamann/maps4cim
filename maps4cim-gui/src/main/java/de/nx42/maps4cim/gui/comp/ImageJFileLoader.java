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

import ij.IJ;
import ij.ImagePlus;
import ij.io.Opener;

import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import de.nx42.maps4cim.gui.util.FileExtensionFilter;
import de.nx42.maps4cim.util.ImageJUtils;

/**
 * A JFileChooser to load images supported by the ImageJ library
 *
 */
public class ImageJFileLoader extends JFileChooser {

    private static final long serialVersionUID = 5782471220373180133L;

    protected static final FileExtensionFilter imageFilter = getImageJFileNameExtensionFilter();

    public ImageJFileLoader(String title) {
        super();
        this.setDialogTitle(title);
        this.setAcceptAllFileFilterUsed(true);
        this.setDialogType(JFileChooser.OPEN_DIALOG);
        this.setFileFilter(imageFilter);
        this.setFileSelectionMode(JFileChooser.FILES_ONLY);
        this.setMultiSelectionEnabled(false);
    }

    @Override
    public void approveSelection() {
        // get path
        String path = getSelectedFile().getAbsolutePath();

        // try to load it
        boolean recognized = (new Opener()).getFileType(path) != Opener.UNKNOWN;
        ImagePlus ip = IJ.openImage(path);

        // check cases
        if(ip == null) {
            // sorry, no chance!
            String msg = "The image you've selected cannot be opened.<br>" +
            		"Please use a common file type (e.g. jpg, png, tif) and " +
            		"make sure the file is not damaged.<br>" +
            		"Also note that certain bit depths (e.g. 24bit grayscale) " +
            		"and compression types are not supported!";
            JOptionPane.showMessageDialog(this, new JLabel(centerText(msg)),
                    "Error loading image",
                    JOptionPane.ERROR_MESSAGE);
        } else if(!recognized) {
            // your risk
            String msg = "The image you are trying to load was not recognized " +
            		"correctly, it might not work at all.<br>" +
            		"Do you want to proceed anyway?";
            int response = JOptionPane.showConfirmDialog(this, new JLabel(centerText(msg)),
                    "Probably unsupported image file",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
                );
                if (response == JOptionPane.YES_OPTION)
                    super.approveSelection();
        } else {
            // all right
            super.approveSelection();
        }
    }

    protected static FileExtensionFilter getImageJFileNameExtensionFilter() {
        // get supported image formats
        String[] formatNames = ImageJUtils.supportedFiles;

        return new FileExtensionFilter("Supported image files", true, formatNames);
    }

    protected static String centerText(String text) {
        return "<html><center>" + text + "</center></html>";
    }

}
