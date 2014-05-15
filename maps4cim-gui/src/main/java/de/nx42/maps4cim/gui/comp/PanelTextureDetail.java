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

import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.LayoutStyle.ComponentPlacement;

import de.nx42.maps4cim.gui.MainWindow.TextureDetail;

public class PanelTextureDetail extends JPanel {

    private static final long serialVersionUID = 6377781740012480231L;

    protected JRadioButton rdbtnPresetDetail;
    protected JRadioButton rdbtnCustomDetail;
    protected JComboBox comboTextureDetail;

    public PanelTextureDetail() {
        this("Preset Detail", null, "Custom Detail", null, null, "Define...");
    }

    public PanelTextureDetail(
            String rdbtnPresetDetailText, String rdbtnPresetDetailTooltip,
            String rdbtnCustomDetailText,  String rdbtnCustomDetailTooltip,
            String comboTextureDetailTooltip, String btnDefineText) {

        // components
        rdbtnPresetDetail = new JRadioButton(rdbtnPresetDetailText);
        rdbtnPresetDetail.setToolTipText(rdbtnPresetDetailTooltip);
        rdbtnPresetDetail.setSelected(true);

        rdbtnCustomDetail = new JRadioButton(rdbtnCustomDetailText);
        rdbtnCustomDetail.setToolTipText(rdbtnCustomDetailTooltip);
        rdbtnCustomDetail.setEnabled(false);

        comboTextureDetail = new JComboBox();
        comboTextureDetail.setToolTipText(comboTextureDetailTooltip);
        comboTextureDetail.setModel(new DefaultComboBoxModel(TextureDetail.values()));
        comboTextureDetail.setSelectedIndex(TextureDetail.values().length - 1);

        JButton btnDefine = new JButton(btnDefineText);
        btnDefine.setToolTipText(rdbtnCustomDetailTooltip);
        btnDefine.setEnabled(false);

        // radiobuttongroup
        ButtonGroup rdbtnGroupDetail = new ButtonGroup();
        rdbtnGroupDetail.add(rdbtnPresetDetail);
        rdbtnGroupDetail.add(rdbtnCustomDetail);

        // layout
        GroupLayout layout = new GroupLayout(this);
        layout.setHorizontalGroup(
            layout.createParallelGroup(Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGroup(layout.createParallelGroup(Alignment.LEADING, false)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(rdbtnPresetDetail)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(comboTextureDetail, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE))
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(rdbtnCustomDetail)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(btnDefine))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(rdbtnPresetDetail)
                        .addComponent(comboTextureDetail, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(rdbtnCustomDetail)
                        .addComponent(btnDefine)))
        );
        this.setLayout(layout);
    }

    public JComboBox getTextureDetail() {
        return comboTextureDetail;
    }

    public int getTextureDetailIndex() {
        return comboTextureDetail.getSelectedIndex();
    }
    
    public void setTextureDetail(TextureDetail td) {
        comboTextureDetail.setSelectedItem(td);
    }

}
