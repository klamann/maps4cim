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
package de.nx42.maps4cim.gui.window;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.nx42.maps4cim.LoggerConfig;
import de.nx42.maps4cim.ResourceLoader;
import de.nx42.maps4cim.gui.MainWindow;
import de.nx42.maps4cim.gui.service.CacheJanitorTask;
import de.nx42.maps4cim.gui.util.Components;
import de.nx42.maps4cim.gui.util.PrefsHelper;
import de.nx42.maps4cim.gui.util.ProxyHelper;
import de.nx42.maps4cim.gui.util.ProxyHelper.ProxySetting;
import de.nx42.maps4cim.map.Cache;
import de.nx42.maps4cim.update.Update.Branch;

public class SettingsWindow extends JDialog {

    private static final long serialVersionUID = 4676259976079185885L;
    private static final Logger log = LoggerFactory.getLogger(SettingsWindow.class);

    private static final ResourceBundle MESSAGES = ResourceLoader.getMessages();
    protected static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    private MainWindow main;

    // General
    private JTextField textFieldAppdata;

    // Proxy
    private JPanel panelCustomProxy;
    private JTextField inputServer;
    private JTextField inputPort;
    private ButtonGroup proxyButtonGroup;
    private JRadioButton rdbtnDirectConnection;
    private JRadioButton rdbtnSystemProxy;
    private JRadioButton rdbtnCustomProxySettings;

    // cache
    private JCheckBox chckbxMapTiles;
    private JCheckBox chckbxSrtm;
    private JCheckBox chckbxOsmXml;

    // log
    private JCheckBox chckbxLogEnabled;
    private JComboBox comboBoxLogLevel;
    private JCheckBox chckbxLogFile;

    // update
    private JCheckBox chckbxUpdateEnabled;
    private JTextField textFieldUpdateInterval;
    private JLabel labelUpdateLast;
    private JComboBox comboBoxUpdateBranch;
    private JCheckBox chckbxUpdateNotifications;

    // cache
    private JCheckBox chckbxJanitorEnabled;



    public SettingsWindow(final MainWindow main) {
        this.main = main;

        setTitle(MESSAGES.getString("SettingsWindow.this.title")); //$NON-NLS-1$
        setBounds(200, 200, 460, 317);
        setModalityType(ModalityType.APPLICATION_MODAL);
        setLocationByPlatform(true);

        JTabbedPane tabs = new JTabbedPane(SwingConstants.TOP);
        getContentPane().add(tabs, BorderLayout.CENTER);

        JPanel panelGeneral = new JPanel();
        tabs.addTab(MESSAGES.getString("SettingsWindow.panelAppdata.title"), null, panelGeneral, null);

        JPanel panelBorderCleanup = new JPanel();
        FlowLayout fl_panelBorderCleanup = (FlowLayout) panelBorderCleanup.getLayout();
        fl_panelBorderCleanup.setAlignment(FlowLayout.LEADING);
        panelBorderCleanup.setBorder(new TitledBorder(new LineBorder(new Color(184, 207, 229)), "Cleanup", TitledBorder.LEADING, TitledBorder.TOP, null, null));

        JPanel panelBorderAppdata = new JPanel();
        panelBorderAppdata.setBorder(new TitledBorder(null, "Appdata", TitledBorder.LEADING, TitledBorder.TOP, null, null));

        JPanel panelBorderLogging = new JPanel();
        FlowLayout flowLayout_2 = (FlowLayout) panelBorderLogging.getLayout();
        flowLayout_2.setAlignment(FlowLayout.LEADING);
        panelBorderLogging.setBorder(new TitledBorder(null, "Logging", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        GroupLayout gl_panelGeneral = new GroupLayout(panelGeneral);
        gl_panelGeneral.setHorizontalGroup(
            gl_panelGeneral.createParallelGroup(Alignment.TRAILING)
                .addGroup(gl_panelGeneral.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(gl_panelGeneral.createParallelGroup(Alignment.LEADING)
                        .addComponent(panelBorderAppdata, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
                        .addComponent(panelBorderLogging, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
                        .addComponent(panelBorderCleanup, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE))
                    .addContainerGap())
        );
        gl_panelGeneral.setVerticalGroup(
            gl_panelGeneral.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_panelGeneral.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(panelBorderAppdata, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(panelBorderLogging, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(panelBorderCleanup, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(144, Short.MAX_VALUE))
        );

        chckbxLogEnabled = new JCheckBox("enabled");
        chckbxLogEnabled.setSelected(true);
        chckbxLogEnabled.setEnabled(false);
        panelBorderLogging.add(chckbxLogEnabled);

        comboBoxLogLevel = new JComboBox();
        comboBoxLogLevel.setEnabled(false);
        comboBoxLogLevel.setModel(new DefaultComboBoxModel(new String[] {"debug", "info", "warn", "error"}));
        panelBorderLogging.add(comboBoxLogLevel);

        chckbxLogFile = new JCheckBox("log to file");
        chckbxLogFile.setSelected(true);
        chckbxLogFile.setEnabled(false);
        panelBorderLogging.add(chckbxLogFile);

                JButton btnOpenLog = new JButton(MESSAGES.getString("SettingsWindow.btnOpenLog.text")); //$NON-NLS-1$
                panelBorderLogging.add(btnOpenLog);
                btnOpenLog.setToolTipText(MESSAGES.getString("SettingsWindow.btnOpenLog.toolTipText")); //$NON-NLS-1$
                btnOpenLog.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        openFile(LoggerConfig.getLogFile().getPath());
                    }
                });

        JButton btnUninstall = new JButton(MESSAGES.getString("SettingsWindow.btnUninstall.text")); //$NON-NLS-1$
        btnUninstall.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                uninstall();
            }
        });
        btnUninstall.setToolTipText("Removes all data that has been stored by maps4cim on this computer. On next launch, maps4cim will not know it has ever run on this machine.\r\nDoes not remove the executable (maps4cim.jar)!"); //$NON-NLS-1$
        panelBorderCleanup.add(btnUninstall);

        textFieldAppdata = new JTextField();
        textFieldAppdata.setEditable(false);
        textFieldAppdata.setText(ResourceLoader.appdata.getPath());

        JButton btnBrowseAppdata = new JButton(MESSAGES.getString("SettingsWindow.btnBrowseAppdata.text"));
        btnBrowseAppdata.setToolTipText(MESSAGES.getString("SettingsWindow.btnBrowseAppdata.toolTipText")); //$NON-NLS-1$
        btnBrowseAppdata.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openFile(textFieldAppdata.getText());
            }
        });
        panelBorderAppdata.setLayout(new FlowLayout(FlowLayout.LEADING, 5, 5));
        panelBorderAppdata.add(textFieldAppdata);
        panelBorderAppdata.add(btnBrowseAppdata);
        panelGeneral.setLayout(gl_panelGeneral);

        JPanel panelNetwork = new JPanel();
        tabs.addTab(MESSAGES.getString("SettingsWindow.panelNetwork.title"), null, panelNetwork, MESSAGES.getString("SettingsWindow.panelNetwork.tooltip")); //$NON-NLS-1$ //$NON-NLS-2$

        JPanel panelBorderProxy = new JPanel();
        panelBorderProxy.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), MESSAGES.getString("SettingsWindow.panelProxy.borderTitle"), TitledBorder.LEADING, TitledBorder.TOP, null, null)); //$NON-NLS-2$

        rdbtnDirectConnection = new JRadioButton(MESSAGES.getString("SettingsWindow.rdbtnDirectConnection.text")); //$NON-NLS-1$
        rdbtnDirectConnection.setToolTipText(MESSAGES.getString("SettingsWindow.rdbtnDirectConnection.toolTipText")); //$NON-NLS-1$
        rdbtnDirectConnection.setSelected(true);

        rdbtnSystemProxy = new JRadioButton(MESSAGES.getString("SettingsWindow.rdbtnSystemProxy.text")); //$NON-NLS-1$
        rdbtnSystemProxy.setToolTipText(MESSAGES.getString("SettingsWindow.rdbtnSystemProxy.toolTipText")); //$NON-NLS-1$

        rdbtnCustomProxySettings = new JRadioButton(MESSAGES.getString("SettingsWindow.rdbtnCustomProxySettings.text")); //$NON-NLS-1$
        rdbtnCustomProxySettings.setToolTipText(MESSAGES.getString("SettingsWindow.rdbtnCustomProxySettings.toolTipText")); //$NON-NLS-1$
        rdbtnCustomProxySettings.addItemListener(rdbtnCustomProxySettingsListener);

        proxyButtonGroup = new ButtonGroup();
        proxyButtonGroup.add( rdbtnDirectConnection );
        proxyButtonGroup.add( rdbtnSystemProxy );
        proxyButtonGroup.add( rdbtnCustomProxySettings );

        panelCustomProxy = new JPanel();
        FlowLayout fl_panelCustomProxy = (FlowLayout) panelCustomProxy.getLayout();
        fl_panelCustomProxy.setAlignment(FlowLayout.LEFT);
        GroupLayout gl_panelBorderProxy = new GroupLayout(panelBorderProxy);
        gl_panelBorderProxy.setHorizontalGroup(
            gl_panelBorderProxy.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_panelBorderProxy.createSequentialGroup()
                    .addGap(19)
                    .addComponent(panelCustomProxy, GroupLayout.DEFAULT_SIZE, 364, Short.MAX_VALUE)
                    .addContainerGap())
                .addGroup(gl_panelBorderProxy.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(gl_panelBorderProxy.createParallelGroup(Alignment.LEADING)
                        .addComponent(rdbtnSystemProxy)
                        .addGroup(gl_panelBorderProxy.createSequentialGroup()
                            .addGroup(gl_panelBorderProxy.createParallelGroup(Alignment.LEADING)
                                .addComponent(rdbtnDirectConnection)
                                .addComponent(rdbtnCustomProxySettings))
                            .addContainerGap(0, Short.MAX_VALUE))))
        );
        gl_panelBorderProxy.setVerticalGroup(
            gl_panelBorderProxy.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_panelBorderProxy.createSequentialGroup()
                    .addComponent(rdbtnDirectConnection)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(rdbtnSystemProxy)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(rdbtnCustomProxySettings)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(panelCustomProxy, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(17, Short.MAX_VALUE))
        );

        JLabel lblServer = new JLabel(MESSAGES.getString("SettingsWindow.lblServer.text")); //$NON-NLS-1$
        lblServer.setToolTipText(MESSAGES.getString("SettingsWindow.inputServer.toolTipText")); //$NON-NLS-1$
        panelCustomProxy.add(lblServer);

        inputServer = new JTextField();
        inputServer.setEnabled(false);
        inputServer.setToolTipText(MESSAGES.getString("SettingsWindow.inputServer.toolTipText")); //$NON-NLS-1$
        panelCustomProxy.add(inputServer);
        inputServer.setColumns(18);

        JLabel lblPort = new JLabel(MESSAGES.getString("SettingsWindow.lblPort.text")); //$NON-NLS-1$
        lblPort.setToolTipText(MESSAGES.getString("SettingsWindow.inputPort.toolTipText")); //$NON-NLS-1$
        panelCustomProxy.add(lblPort);

        inputPort = new JTextField();
        inputPort.setEnabled(false);
        inputPort.setHorizontalAlignment(SwingConstants.TRAILING);
        inputPort.setToolTipText(MESSAGES.getString("SettingsWindow.inputPort.toolTipText")); //$NON-NLS-1$
        panelCustomProxy.add(inputPort);
        inputPort.setColumns(5);
        panelBorderProxy.setLayout(gl_panelBorderProxy);

        GroupLayout gl_panelNetwork = new GroupLayout(panelNetwork);
        gl_panelNetwork.setHorizontalGroup(
            gl_panelNetwork.createParallelGroup(Alignment.TRAILING)
                .addGroup(gl_panelNetwork.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(panelBorderProxy, GroupLayout.DEFAULT_SIZE, 429, Short.MAX_VALUE)
                    .addContainerGap())
        );
        gl_panelNetwork.setVerticalGroup(
            gl_panelNetwork.createParallelGroup(Alignment.TRAILING)
                .addGroup(gl_panelNetwork.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(panelBorderProxy, GroupLayout.DEFAULT_SIZE, 171, Short.MAX_VALUE)
                    .addGap(52))
        );
        panelNetwork.setLayout(gl_panelNetwork);

        JPanel panelUpdates = new JPanel();
        tabs.addTab("Updates", null, panelUpdates, null);

        JPanel panel = new JPanel();
        panel.setBorder(new TitledBorder(null, "maps4cim updates", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        GroupLayout gl_panelUpdates = new GroupLayout(panelUpdates);
        gl_panelUpdates.setHorizontalGroup(
            gl_panelUpdates.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_panelUpdates.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(panel, GroupLayout.DEFAULT_SIZE, 429, Short.MAX_VALUE)
                    .addContainerGap())
        );
        gl_panelUpdates.setVerticalGroup(
            gl_panelUpdates.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_panelUpdates.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(panel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(107, Short.MAX_VALUE))
        );

        chckbxUpdateEnabled = new JCheckBox("check for updates");
        chckbxUpdateEnabled.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean enabled = chckbxUpdateEnabled.isSelected();
                textFieldUpdateInterval.setEnabled(enabled);
            }
        });
        chckbxUpdateEnabled.setToolTipText("Checks for updates in the selected interval. Does not transfer any personal information.");
        chckbxUpdateEnabled.setSelected(true);

        JLabel lblEvery = new JLabel("every");

        textFieldUpdateInterval = new JTextField();
        textFieldUpdateInterval.setToolTipText("Check for updates next time when maps4cim is started, but not before the specified number of days has passed.\r\nmaps4cim will only check for updates when it is started - there will be no background service running on your system!");
        textFieldUpdateInterval.setHorizontalAlignment(SwingConstants.TRAILING);
        textFieldUpdateInterval.setText("7");
        textFieldUpdateInterval.setColumns(3);

        JLabel lblDays = new JLabel("days");

        JButton btnCheckNow = new JButton("check now...");
        btnCheckNow.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateCheck();
            }
        });
        btnCheckNow.setToolTipText("Connect to the update server now and see if there are any updates!");

        JLabel lblBranch = new JLabel("Branch:");

        comboBoxUpdateBranch = new JComboBox();
        comboBoxUpdateBranch.setToolTipText("Most users should retrieve updates from the stable branch, but if you want to have a look at the latest features (and do not mind to run into occasional bugs), try the testing branch.");
        comboBoxUpdateBranch.setModel(new DefaultComboBoxModel(Branch.values()));
        comboBoxUpdateBranch.setSelectedIndex(0);

        chckbxUpdateNotifications = new JCheckBox("important notifications");
        chckbxUpdateNotifications.setEnabled(false);
        chckbxUpdateNotifications.setToolTipText("maps4cim may display a notification window to all users, if something really important is going on. No ads, of course...");

        JLabel lbllastUpdate = new JLabel("(last checked:");

        labelUpdateLast = new JLabel("2014-04-14");

        JLabel label_1 = new JLabel(")");
        GroupLayout gl_panel = new GroupLayout(panel);
        gl_panel.setHorizontalGroup(
            gl_panel.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_panel.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
                        .addGroup(gl_panel.createSequentialGroup()
                            .addGap(21)
                            .addComponent(lblBranch)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(comboBoxUpdateBranch, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(ComponentPlacement.UNRELATED)
                            .addComponent(btnCheckNow))
                        .addGroup(gl_panel.createSequentialGroup()
                            .addComponent(chckbxUpdateEnabled)
                            .addComponent(lblEvery)
                            .addGap(4)
                            .addComponent(textFieldUpdateInterval, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addGap(4)
                            .addComponent(lblDays)
                            .addPreferredGap(ComponentPlacement.UNRELATED)
                            .addComponent(lbllastUpdate)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(labelUpdateLast)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(label_1))
                        .addComponent(chckbxUpdateNotifications))
                    .addContainerGap(25, Short.MAX_VALUE))
        );
        gl_panel.setVerticalGroup(
            gl_panel.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_panel.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
                        .addGroup(gl_panel.createSequentialGroup()
                            .addGap(1)
                            .addComponent(chckbxUpdateEnabled))
                        .addGroup(gl_panel.createSequentialGroup()
                            .addGap(5)
                            .addComponent(lblEvery))
                        .addGroup(gl_panel.createSequentialGroup()
                            .addGap(2)
                            .addComponent(textFieldUpdateInterval, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addGroup(gl_panel.createSequentialGroup()
                            .addGap(5)
                            .addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
                                .addComponent(lblDays)
                                .addComponent(lbllastUpdate)
                                .addComponent(labelUpdateLast)
                                .addComponent(label_1))))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
                        .addComponent(lblBranch)
                        .addComponent(comboBoxUpdateBranch, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnCheckNow))
                    .addPreferredGap(ComponentPlacement.UNRELATED)
                    .addComponent(chckbxUpdateNotifications)
                    .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panel.setLayout(gl_panel);
        panelUpdates.setLayout(gl_panelUpdates);

        JPanel panelCache = new JPanel();
        tabs.addTab(MESSAGES.getString("SettingsWindow.panelCache.title"), null, panelCache, null); //$NON-NLS-1$

        JLabel lblCacheFolder = new JLabel(MESSAGES.getString("SettingsWindow.lblCacheFolder.text")); //$NON-NLS-1$

        textFieldCacheFolder = new JTextField();
        textFieldCacheFolder.setEditable(false);
        textFieldCacheFolder.setText(Cache.cacheDir.getPath());
        textFieldCacheFolder.setColumns(10);

        JButton btnBrowseCache = new JButton(MESSAGES.getString("SettingsWindow.btnBrowseCache.text")); //$NON-NLS-1$
        btnBrowseCache.setToolTipText(MESSAGES.getString("SettingsWindow.btnBrowseCache.toolTipText")); //$NON-NLS-1$
        btnBrowseCache.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openFile(textFieldCacheFolder.getText());
            }
        });

        JPanel panelClearCache = new JPanel();
        FlowLayout fl_panelClearCache = (FlowLayout) panelClearCache.getLayout();
        fl_panelClearCache.setAlignment(FlowLayout.LEADING);
        panelClearCache.setBorder(new TitledBorder(null, "Clear Cache", TitledBorder.LEADING, TitledBorder.TOP, null, null));

        JPanel panelCacheJanitor = new JPanel();
        panelCacheJanitor.setToolTipText(MESSAGES.getString("SettingsWindow.panel_1.toolTipText")); //$NON-NLS-1$
        FlowLayout fl_panelCacheJanitor = (FlowLayout) panelCacheJanitor.getLayout();
        fl_panelCacheJanitor.setAlignment(FlowLayout.LEADING);
        panelCacheJanitor.setBorder(new TitledBorder(null, "Your friendly cache janitor", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        GroupLayout gl_panelCache = new GroupLayout(panelCache);
        gl_panelCache.setHorizontalGroup(
            gl_panelCache.createParallelGroup(Alignment.LEADING)
                .addGroup(Alignment.TRAILING, gl_panelCache.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(gl_panelCache.createParallelGroup(Alignment.TRAILING)
                        .addComponent(panelCacheJanitor, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 425, Short.MAX_VALUE)
                        .addComponent(panelClearCache, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 425, Short.MAX_VALUE)
                        .addGroup(Alignment.LEADING, gl_panelCache.createSequentialGroup()
                            .addComponent(lblCacheFolder)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(textFieldCacheFolder, GroupLayout.DEFAULT_SIZE, 246, Short.MAX_VALUE)
                            .addPreferredGap(ComponentPlacement.UNRELATED)
                            .addComponent(btnBrowseCache)))
                    .addContainerGap())
        );
        gl_panelCache.setVerticalGroup(
            gl_panelCache.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_panelCache.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(gl_panelCache.createParallelGroup(Alignment.BASELINE)
                        .addComponent(lblCacheFolder)
                        .addComponent(textFieldCacheFolder, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnBrowseCache))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(panelClearCache, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(panelCacheJanitor, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(30, Short.MAX_VALUE))
        );

        chckbxJanitorEnabled = new JCheckBox(MESSAGES.getString("SettingsWindow.chckbxEnabled.text")); //$NON-NLS-1$
        chckbxJanitorEnabled.setToolTipText(MESSAGES.getString("SettingsWindow.chckbxEnabled.toolTipText")); //$NON-NLS-1$
        chckbxJanitorEnabled.setSelected(true);
        panelCacheJanitor.add(chckbxJanitorEnabled);

        final JButton btnRunNow = new JButton(MESSAGES.getString("SettingsWindow.btnRunNow.text")); //$NON-NLS-1$
        btnRunNow.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnRunNow.setEnabled(false);
                CacheJanitorTask janitor = new CacheJanitorTask();
                Thread t = new Thread(janitor, "CacheJanitor");
                t.start();
            }
        });
        btnRunNow.setToolTipText(MESSAGES.getString("SettingsWindow.btnRunNow.toolTipText"));
        panelCacheJanitor.add(btnRunNow);

        chckbxMapTiles = new JCheckBox(MESSAGES.getString("SettingsWindow.chckbxMapTiles.text")); //$NON-NLS-1$
        chckbxMapTiles.setToolTipText("Removes all cached map tiles that are used by the map view in the main window of maps4cim.");
        panelClearCache.add(chckbxMapTiles);

        chckbxSrtm = new JCheckBox(MESSAGES.getString("SettingsWindow.chckbxSrtm.text")); //$NON-NLS-1$
        chckbxSrtm.setToolTipText("Removes all cached SRTM-tiles, which are used to generate the real-world elevations in maps4cim.");
        panelClearCache.add(chckbxSrtm);

        chckbxOsmXml = new JCheckBox(MESSAGES.getString("SettingsWindow.chckbxOsmXml.text")); //$NON-NLS-1$
        chckbxOsmXml.setToolTipText("Removes all cached OpenStreetMap-XML-files, which are used to draw the texture on the ground.");
        panelClearCache.add(chckbxOsmXml);

        JButton btnClear = new JButton(MESSAGES.getString("SettingsWindow.btnClear.text_1")); //$NON-NLS-1$
        btnClear.setToolTipText("Removes the selected data types from the cache folder.");
        btnClear.addActionListener(clearCacheAction);
        panelClearCache.add(btnClear);
        panelCache.setLayout(gl_panelCache);

        JPanel buttons = new JPanel();
        FlowLayout fl_buttons = (FlowLayout) buttons.getLayout();
        fl_buttons.setAlignment(FlowLayout.TRAILING);
        getContentPane().add(buttons, BorderLayout.SOUTH);

        JButton btnOk = new JButton(MESSAGES.getString("SettingsWindow.btnOk.text")); //$NON-NLS-1$
        Components.setPreferredWidth(btnOk, 70);
        buttons.add(btnOk);

        JButton btnApply = new JButton("Apply");
        btnApply.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                storePrefs();
            }
        });
        Components.setPreferredWidth(btnApply, 70);
        buttons.add(btnApply);

        JButton btnCancel = new JButton(MESSAGES.getString("SettingsWindow.btnCancel.text")); //$NON-NLS-1$
        Components.setPreferredWidth(btnCancel, 70);
        buttons.add(btnCancel);
        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        btnOk.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                storePrefs();
                dispose();
            }
        });


        restorePrefs();
    }

    // preferences

    protected static final Preferences prefs = PrefsHelper.getPrefs();
    protected static final String regKeyLogEnabled = "logging.enabled";
    protected static final String regKeyLogLevel = "logging.level";
    protected static final String regKeyLogFile = "logging.file";
    protected static final String regKeyUpdateEnabled = "update.enabled";
    protected static final String regKeyUpdateLast = "update.lastrun";
    protected static final String regKeyUpdateInterval = "update.interval";
    protected static final String regKeyUpdateBranch = "update.branch";
    protected static final String regKeyUpdateNotifications = "update.notifications";
    protected static final String regKeyJanitorEnabled = "janitor.enabled";

    protected void storePrefs() {
        // proxy
        applyProxy();

        // logging
        prefs.putBoolean(regKeyLogEnabled, chckbxLogEnabled.isSelected());
        prefs.putInt(regKeyLogLevel, comboBoxLogLevel.getSelectedIndex());
        prefs.putBoolean(regKeyLogFile, chckbxLogFile.isSelected());

        // updates
        prefs.putBoolean(regKeyUpdateEnabled, chckbxUpdateEnabled.isSelected());
        prefs.put(regKeyUpdateInterval, textFieldUpdateInterval.getText());
        prefs.put(regKeyUpdateBranch, comboBoxUpdateBranch.getSelectedItem().toString());
        prefs.putBoolean(regKeyUpdateNotifications, chckbxUpdateNotifications.isSelected());

        // cache
        prefs.putBoolean(regKeyJanitorEnabled, chckbxJanitorEnabled.isSelected());

    }

    protected void restorePrefs() {
        // proxy
        if(ProxyHelper.hasProxySettings()) {
            // select correct proxy setting
            ProxySetting ps = ProxyHelper.getProxySettings();
            getRadioButton(ps).doClick();

            // restore server & port
            inputServer.setText(ProxyHelper.getProxyServer());
            inputPort.setText(ProxyHelper.getProxyPort());
        }

        // logging
        chckbxLogEnabled.setSelected(prefs.getBoolean(regKeyLogEnabled, true));
        comboBoxLogLevel.setSelectedItem(prefs.getInt(regKeyLogLevel, 0));
        chckbxLogFile.setSelected(prefs.getBoolean(regKeyLogFile, true));

        // updates
        boolean updateEnabled = prefs.getBoolean(regKeyUpdateEnabled, true);
        chckbxUpdateEnabled.setSelected(updateEnabled);
        textFieldUpdateInterval.setEnabled(updateEnabled);
        textFieldUpdateInterval.setText(prefs.get(regKeyUpdateInterval, "7"));
        long lastUpdate = prefs.getLong(regKeyUpdateLast, -1);
        labelUpdateLast.setText(lastUpdate == -1 ? "never" : sdf.format(new Date(lastUpdate)));
        comboBoxUpdateBranch.setSelectedItem(Branch.valueOf(prefs.get(regKeyUpdateBranch, Branch.stable.name())));

        // cache
        chckbxJanitorEnabled.setSelected(prefs.getBoolean(regKeyJanitorEnabled, true));
    }

    protected void applyProxy() {
        // apply proxy settings and save for later
        ProxySetting ps = getSelectedSettings();
        ProxyHelper.setProxySettings(ps, inputServer.getText(), inputPort.getText());
        main.restartJXM();
    }


    protected void updateCheck() {
        MainWindow.updateCheck(this);
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
    private JTextField textFieldCacheFolder;

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

    protected void openFile(String path) {
        try {
            Desktop.getDesktop().open(new File(path));
        } catch (IOException e) {
            log.warn("Cannot open path " + path, e);
        }
    }

    protected ActionListener clearCacheAction = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if(chckbxMapTiles.isSelected() && chckbxSrtm.isSelected() && chckbxOsmXml.isSelected()) {
                Cache.clearCache();
            } else {
                Cache.clearCache(chckbxMapTiles.isSelected(), chckbxSrtm.isSelected(), chckbxOsmXml.isSelected());
            }
        }
    };

    private void uninstall() {
        if(JOptionPane.showConfirmDialog(SettingsWindow.this, "Do you want to remove all user data stored by maps4cim?\nNote: This action cannot be reverted. Your maps will not be deleted.\nmaps4cim will exit if you proceed.", "Uninstall maps4cim", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE) == 0) {
            // close all windows (releases locks on cached tiles)
            this.dispose();
            main.dispose();

            // call uninstaller
            ResourceLoader.uninstall();

            // exit
            System.exit(0);
        }
    }

    public static Branch getSelectedBranch() {
        return Branch.valueOf(prefs.get(regKeyUpdateBranch, MainWindow.branch.name()));
    }

}
