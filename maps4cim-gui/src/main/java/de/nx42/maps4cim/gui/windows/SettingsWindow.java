/**
 * maps4cim - a real world map generator for CiM 2
 * Copyright 2013 Sebastian Straub
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
package de.nx42.maps4cim.gui.windows;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.nx42.maps4cim.gui.MainWindow;
import de.nx42.maps4cim.gui.util.ProxyHelper;
import de.nx42.maps4cim.gui.util.ProxyHelper.ProxySetting;

public class SettingsWindow extends JDialog {

    private static final long serialVersionUID = 4676259976079185885L;
    private static final Logger log = LoggerFactory.getLogger(SettingsWindow.class);

    private MainWindow main;

    private JPanel panelCustomProxy;
    private JTextField inputServer;
    private JTextField inputPort;

    private ButtonGroup proxyButtonGroup;
    private JRadioButton rdbtnDirectConnection;
    private JRadioButton rdbtnSystemProxy;
    private JRadioButton rdbtnCustomProxySettings;

    public SettingsWindow(MainWindow main) {
        this.main = main;

        setResizable(false);
        setTitle("Settings - maps4cim");
        setBounds(200, 200, 460, 290);
        setModalityType(ModalityType.APPLICATION_MODAL);
        setLocationByPlatform(true);

        JTabbedPane tabbedSettings = new JTabbedPane(SwingConstants.TOP);
        getContentPane().add(tabbedSettings, BorderLayout.CENTER);

        JPanel panelNetwork = new JPanel();
        tabbedSettings.addTab("Network", null, panelNetwork, null);

        JPanel panelProxy = new JPanel();
        panelProxy.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Proxy", TitledBorder.LEADING, TitledBorder.TOP, null, null));

        rdbtnDirectConnection = new JRadioButton("direct connection (no proxy)");
        rdbtnDirectConnection.setSelected(true);

        rdbtnSystemProxy = new JRadioButton("system proxy");

        rdbtnCustomProxySettings = new JRadioButton("custom proxy settings:");
        rdbtnCustomProxySettings.addItemListener(rdbtnCustomProxySettingsListener);

        proxyButtonGroup = new ButtonGroup();
        proxyButtonGroup.add( rdbtnDirectConnection );
        proxyButtonGroup.add( rdbtnSystemProxy );
        proxyButtonGroup.add( rdbtnCustomProxySettings );

        panelCustomProxy = new JPanel();
        FlowLayout fl_panelCustomProxy = (FlowLayout) panelCustomProxy.getLayout();
        fl_panelCustomProxy.setAlignment(FlowLayout.LEFT);
        GroupLayout gl_panelProxy = new GroupLayout(panelProxy);
        gl_panelProxy.setHorizontalGroup(
            gl_panelProxy.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_panelProxy.createSequentialGroup()
                    .addGap(19)
                    .addComponent(panelCustomProxy, GroupLayout.DEFAULT_SIZE, 364, Short.MAX_VALUE)
                    .addContainerGap())
                .addGroup(gl_panelProxy.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(gl_panelProxy.createParallelGroup(Alignment.LEADING)
                        .addComponent(rdbtnSystemProxy)
                        .addGroup(gl_panelProxy.createSequentialGroup()
                            .addGroup(gl_panelProxy.createParallelGroup(Alignment.LEADING)
                                .addComponent(rdbtnDirectConnection)
                                .addComponent(rdbtnCustomProxySettings))
                            .addContainerGap(0, Short.MAX_VALUE))))
        );
        gl_panelProxy.setVerticalGroup(
            gl_panelProxy.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_panelProxy.createSequentialGroup()
                    .addComponent(rdbtnDirectConnection)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(rdbtnSystemProxy)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(rdbtnCustomProxySettings)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(panelCustomProxy, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(17, Short.MAX_VALUE))
        );

        JLabel lblServer = new JLabel("Server:");
        panelCustomProxy.add(lblServer);

        inputServer = new JTextField();
        inputServer.setEnabled(false);
        inputServer.setToolTipText("The adress of the proxy server, e.g. 127.0.0.1 or proxy.example.com");
        panelCustomProxy.add(inputServer);
        inputServer.setColumns(18);

        JLabel lblPort = new JLabel("Port:");
        panelCustomProxy.add(lblPort);

        inputPort = new JTextField();
        inputPort.setEnabled(false);
        inputPort.setHorizontalAlignment(SwingConstants.TRAILING);
        inputPort.setToolTipText("The port used to connect to the proxy, e.g. 8080");
        panelCustomProxy.add(inputPort);
        inputPort.setColumns(5);
        panelProxy.setLayout(gl_panelProxy);

        JButton btnCancel = new JButton("Cancel");
        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        JButton btnOk = new JButton("OK");
        btnOk.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                applyProxy();
                dispose();
            }
        });

        GroupLayout gl_panelNetwork = new GroupLayout(panelNetwork);
        gl_panelNetwork.setHorizontalGroup(
            gl_panelNetwork.createParallelGroup(Alignment.LEADING)
                .addGroup(Alignment.TRAILING, gl_panelNetwork.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(gl_panelNetwork.createParallelGroup(Alignment.TRAILING)
                        .addComponent(panelProxy, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 419, Short.MAX_VALUE)
                        .addGroup(gl_panelNetwork.createSequentialGroup()
                            .addComponent(btnOk, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(btnCancel, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)))
                    .addContainerGap())
        );
        gl_panelNetwork.setVerticalGroup(
            gl_panelNetwork.createParallelGroup(Alignment.TRAILING)
                .addGroup(gl_panelNetwork.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(panelProxy, GroupLayout.DEFAULT_SIZE, 161, Short.MAX_VALUE)
                    .addGap(18)
                    .addGroup(gl_panelNetwork.createParallelGroup(Alignment.BASELINE)
                        .addComponent(btnCancel)
                        .addComponent(btnOk))
                    .addContainerGap())
        );
        panelNetwork.setLayout(gl_panelNetwork);


        restoreLastSettings();
    }


    protected void restoreLastSettings() {
        if(ProxyHelper.hasProxySettings()) {
            // select correct proxy setting
            ProxySetting ps = ProxyHelper.getProxySettings();
            getRadioButton(ps).doClick();

            // restore server & port
            inputServer.setText(ProxyHelper.getProxyServer());
            inputPort.setText(ProxyHelper.getProxyPort());
        }
    }

    protected void applyProxy() {
        // apply proxy settings and save for later
        ProxySetting ps = getSelectedSettings();
        ProxyHelper.setProxySettings(ps, inputServer.getText(), inputPort.getText());
        main.restartJXM();
    }

    protected ItemListener rdbtnCustomProxySettingsListener = new ItemListener() {
        @Override
        public void itemStateChanged(ItemEvent e) {
            // enable/disable custom proxy input
            if(e.getStateChange() == ItemEvent.SELECTED || e.getStateChange() == ItemEvent.DESELECTED) {
                setCustomProxyEnabled(e.getStateChange() == ItemEvent.SELECTED);
            }
        }
    };

    protected void setCustomProxyEnabled(boolean enabled) {
        inputServer.setEnabled(enabled);
        inputPort.setEnabled(enabled);
    }


    public ProxySetting getSelectedSettings() {
        if(rdbtnDirectConnection.isSelected()) {
            return ProxySetting.DIRECT;
        } else if(rdbtnSystemProxy.isSelected()) {
            return ProxySetting.SYSTEM;
        } else if(rdbtnCustomProxySettings.isSelected()) {
            return ProxySetting.CUSTOM;
        } else {
            log.warn("Proxy settings not recognized");
            return null;
        }
    }

    public JRadioButton getRadioButton(ProxySetting ps) {
        switch(ps) {
            case DIRECT: return rdbtnDirectConnection;
            case SYSTEM: return rdbtnSystemProxy;
            case CUSTOM: return rdbtnCustomProxySettings;
            default: return null;
        }
    }

}
