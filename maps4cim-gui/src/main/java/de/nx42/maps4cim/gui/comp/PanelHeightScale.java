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

public class PanelHeightScale extends JPanel {
    
    // TODO create common base class with height offset!

    private static final long serialVersionUID = 8801859451728852792L;

    protected JFormattedTextField inputHeightScale;
    protected JCheckBox chckbxHeightScaleAuto;

    public PanelHeightScale() {
        this("Height scale:", "auto", null, null);
    }

    public PanelHeightScale(String labelText, String checkBoxText, String inputTooltip, String scaleAutoTooltip) {

        LayoutManager layout = new FlowLayout(FlowLayout.LEADING);
        this.setLayout(layout);

        // 1. Label
        JLabel lblHeightScale = new JLabel(labelText);
        this.add(lblHeightScale);

        // 2. Input
        inputHeightScale = new JFormattedTextFieldColored(FormattedComponents.getDecimalFormatter(0, 10000));
        inputHeightScale.setEnabled(false);
        inputHeightScale.setHorizontalAlignment(SwingConstants.TRAILING);
        inputHeightScale.setValue(100);
        inputHeightScale.setColumns(4);
        if(!Strings.isNullOrEmpty(inputTooltip)) {
            inputHeightScale.setToolTipText(inputTooltip);
        }
        this.add(inputHeightScale);

        // 3. %
        JLabel lblPercent = new JLabel("%");
        this.add(lblPercent);

        // 4. Checkbox Heightscale Auto
        chckbxHeightScaleAuto = new JCheckBox(checkBoxText);
        chckbxHeightScaleAuto.addActionListener(heightOffsetCheckBoxAction);
        chckbxHeightScaleAuto.setSelected(true);
        if(!Strings.isNullOrEmpty(scaleAutoTooltip)) {
            chckbxHeightScaleAuto.setToolTipText(scaleAutoTooltip);
        }
        this.add(chckbxHeightScaleAuto);
    }

    protected ActionListener heightOffsetCheckBoxAction = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            inputHeightScale.setEnabled(!chckbxHeightScaleAuto.isSelected());
        }
    };


    public String getHeightScale() {
        return inputHeightScale.getText();
    }

    public void setHeightScale(double scale) {
        inputHeightScale.setText(String.valueOf(Math.round(scale * 100)));
    }

    public boolean isHeightScaleAuto() {
        return chckbxHeightScaleAuto.isSelected();
    }

    public void setHeightScaleAuto(boolean selected) {
        // do not use setSelected, does not call listeners...
        if(chckbxHeightScaleAuto.isSelected() != selected) {
            chckbxHeightScaleAuto.doClick();
        }
    }

    public void setDefaults() {
        inputHeightScale.setText("0");
        chckbxHeightScaleAuto.setSelected(true);
    }
}
