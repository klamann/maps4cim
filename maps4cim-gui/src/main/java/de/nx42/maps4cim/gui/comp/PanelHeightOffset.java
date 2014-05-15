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

import java.awt.FlowLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import com.google.common.base.Strings;

public class PanelHeightOffset extends JPanel {

    private static final long serialVersionUID = 6621817594353732652L;

    protected JFormattedTextField inputHeightOffset;
    protected JCheckBox chckbxHeightOffsetAuto;

    public PanelHeightOffset() {
        this("Height offset:", "auto", null, null);
    }

    public PanelHeightOffset(String labelText, String checkBoxText, String inputTooltip, String offsetAutoTooltip) {

        LayoutManager layout = new FlowLayout(FlowLayout.LEADING);
        this.setLayout(layout);

        // 1. Label
        JLabel lblHeightOffset = new JLabel(labelText);
        this.add(lblHeightOffset);

        // 2. Input
        inputHeightOffset = new JFormattedTextFieldColored(FormattedComponents.getDecimalFormatter(-10000, 10000));
        inputHeightOffset.setEnabled(false);
        inputHeightOffset.setValue(0);
        inputHeightOffset.setHorizontalAlignment(SwingConstants.TRAILING);
        inputHeightOffset.setColumns(4);
        if(!Strings.isNullOrEmpty(inputTooltip)) {
            inputHeightOffset.setToolTipText(inputTooltip);
        }
        this.add(inputHeightOffset);

        // 3. meter
        JLabel lblMeter = new JLabel("m");
        this.add(lblMeter);

        // 4. Checkbox HeightOffset Auto
        chckbxHeightOffsetAuto = new JCheckBox(checkBoxText);
        chckbxHeightOffsetAuto.addActionListener(heightOffsetCheckBoxAction);
        chckbxHeightOffsetAuto.setSelected(true);
        if(!Strings.isNullOrEmpty(offsetAutoTooltip)) {
            chckbxHeightOffsetAuto.setToolTipText(offsetAutoTooltip);
        }
        this.add(chckbxHeightOffsetAuto);

    }

    protected ActionListener heightOffsetCheckBoxAction = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            inputHeightOffset.setEnabled(!chckbxHeightOffsetAuto.isSelected());
        }
    };


    public Double getHeightOffset() {
        return (Double) inputHeightOffset.getValue();
    }

    public void setHeightOffset(double offset) {
        inputHeightOffset.setValue(offset);
    }

    public boolean isHeightOffsetAuto() {
        return chckbxHeightOffsetAuto.isSelected();
    }

    public void setHeightOffsetAuto(boolean selected) {
        // do not use setSelected, does not call listeners...
        if(chckbxHeightOffsetAuto.isSelected() != selected) {
            chckbxHeightOffsetAuto.doClick();
        }
    }
    
    public void setDefaults() {
        inputHeightOffset.setValue(0);
        chckbxHeightOffsetAuto.setSelected(true);
    }

}
