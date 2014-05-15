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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;

import com.google.common.base.Strings;

public class PanelFileSelect extends JPanel {

    private static final long serialVersionUID = 3293174146734423659L;

    protected JTextField inputFilePath;
    protected JFileChooser fileChooser;

    public PanelFileSelect() {
        this("", "Browse", new JFileChooser());
    }

    public PanelFileSelect(String selectedPath, String btnBrowseText, JFileChooser fileChooser) {
        this.fileChooser = fileChooser;

        // text field for path
        inputFilePath = new JTextField();
        inputFilePath.setText(selectedPath);
        inputFilePath.setToolTipText("The selected file");
        inputFilePath.setColumns(10);

        // browse button
        JButton btnBrowse = new JButton(btnBrowseText);
        btnBrowse.setToolTipText("Choose a file");
        btnBrowse.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openFile();
            }
        });

        // layout
        GroupLayout layout = new GroupLayout(this);
        layout.setHorizontalGroup(
            layout.createParallelGroup(Alignment.LEADING)
                .addGroup(Alignment.LEADING, layout.createSequentialGroup()
                    .addComponent(inputFilePath, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(btnBrowse))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(btnBrowse)
                        .addComponent(inputFilePath, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
        );
        this.setLayout(layout);
    }

    public void openFile() {
        if(!Strings.isNullOrEmpty(getFilePath())) {
            fileChooser.setSelectedFile(new File(getFilePath()));
        }

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            validFileSelectedAction(fileChooser.getSelectedFile());
        }
    }

    protected void validFileSelectedAction(File f) {
        setFilePath(f.getPath());
    }

    public String getFilePath() {
        return inputFilePath.getText();
    }

    public void setFilePath(String path) {
        inputFilePath.setText(path);
    }



}
