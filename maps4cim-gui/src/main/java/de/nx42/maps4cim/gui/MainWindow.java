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
package de.nx42.maps4cim.gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;
import javax.xml.bind.JAXBException;

import com.google.common.base.Strings;

import net.sf.oval.ConstraintViolation;
import net.sf.oval.Validator;

import org.jdesktop.swingx.JXMapViewer;
import org.jdesktop.swingx.mapviewer.GeoPosition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.nx42.maps4cim.LoggerConfig;
import de.nx42.maps4cim.ResourceLoader;
import de.nx42.maps4cim.config.Config;
import de.nx42.maps4cim.config.bounds.BoundsDef;
import de.nx42.maps4cim.config.bounds.CenterDef;
import de.nx42.maps4cim.config.bounds.CenterDef.Unit;
import de.nx42.maps4cim.config.header.HeaderDef;
import de.nx42.maps4cim.config.header.HeaderDef.BuildingSet;
import de.nx42.maps4cim.config.relief.HeightmapDef;
import de.nx42.maps4cim.config.relief.PlanarReliefDef;
import de.nx42.maps4cim.config.relief.ReliefDef;
import de.nx42.maps4cim.config.relief.SrtmDef;
import de.nx42.maps4cim.config.texture.ImageDef;
import de.nx42.maps4cim.config.texture.OsmDef;
import de.nx42.maps4cim.config.texture.OsmFileDef;
import de.nx42.maps4cim.config.texture.SingleTextureDef;
import de.nx42.maps4cim.config.texture.TextureDef;
import de.nx42.maps4cim.gui.action.CenterOnClickListener;
import de.nx42.maps4cim.gui.action.SelectionAdapter;
import de.nx42.maps4cim.gui.comp.FormattedComponents;
import de.nx42.maps4cim.gui.comp.ImageJFileLoader;
import de.nx42.maps4cim.gui.comp.JFileSaver;
import de.nx42.maps4cim.gui.comp.JFormattedTextFieldColored;
import de.nx42.maps4cim.gui.comp.JSlider2;
import de.nx42.maps4cim.gui.comp.PanelFileSelect;
import de.nx42.maps4cim.gui.comp.PanelHeightOffset;
import de.nx42.maps4cim.gui.comp.PanelHeightScale;
import de.nx42.maps4cim.gui.comp.PanelHeightmapSelect;
import de.nx42.maps4cim.gui.comp.PanelTextureDetail;
import de.nx42.maps4cim.gui.layout.AdaptiveCardLayout;
import de.nx42.maps4cim.gui.service.BackgroundTaskScheduler;
import de.nx42.maps4cim.gui.service.CacheJanitorTask;
import de.nx42.maps4cim.gui.service.UpdateCheckTask;
import de.nx42.maps4cim.gui.util.Components;
import de.nx42.maps4cim.gui.util.FileExtensionFilter;
import de.nx42.maps4cim.gui.util.MapViewerFactory;
import de.nx42.maps4cim.gui.util.ProxyHelper;
import de.nx42.maps4cim.gui.util.event.Event;
import de.nx42.maps4cim.gui.util.event.Observer;
import de.nx42.maps4cim.gui.util.xmleditor.XmlTextPane;
import de.nx42.maps4cim.gui.window.AboutWindow;
import de.nx42.maps4cim.gui.window.ConstraintViolationReportWindow;
import de.nx42.maps4cim.gui.window.GettingStartedWindow;
import de.nx42.maps4cim.gui.window.HeightmapWindow;
import de.nx42.maps4cim.gui.window.MetaEditorWindow;
import de.nx42.maps4cim.gui.window.RenderWindow;
import de.nx42.maps4cim.gui.window.SettingsWindow;
import de.nx42.maps4cim.gui.window.TextureChooser;
import de.nx42.maps4cim.map.ex.ConfigValidationException;
import de.nx42.maps4cim.map.texture.data.TexHexTriplet;
import de.nx42.maps4cim.update.ProgramVersion;
import de.nx42.maps4cim.update.Update.Branch;
import de.nx42.maps4cim.util.Serializer;
import de.nx42.maps4cim.util.ValidatorUtils;
import de.nx42.maps4cim.util.gis.Area;
import de.nx42.maps4cim.util.gis.Coordinate;

public class MainWindow extends JFrame {

    private static final long serialVersionUID = -5741327205041428356L;
	private static final Logger log = LoggerFactory.getLogger(MainWindow.class);

    public static final ProgramVersion version = new ProgramVersion("1.0.0");
    public static final Branch branch = Branch.stable;
    public static final String attributionURL = "http://www.openstreetmap.org/copyright";

	private static final ResourceBundle MESSAGES = ResourceLoader.getMessages();
	private static final MapViewerFactory fact = new MapViewerFactory();

	private static final int settingsPanelDefaultWidth = 325;
	private static final int settingsPanelContentWidth = 240;
	private static final int extSliderMax = 32;

	// root panels
	private JTabbedPane tabs;
	private final JXMapViewer jxm;
    private JPanel map;
    private JEditorPane xmlEditor;
	private JPanel panelTextureCards;
	private JPanel panelReliefCards;

	// bounds
	private JFormattedTextField inputLat;	// Double
	private JFormattedTextField inputLon;	// Double
	private JSlider2 sliderExt;
	private JFormattedTextField inputExtent;	// Double
	private JButton btnResetExtent;

	// relief
	private JComboBox comboReliefSource;
	private JFormattedTextField textFieldReliefPlanarHeight;	// Double
	private PanelHeightOffset panelHeightOffset;
	private PanelHeightScale panelHeightScale;
	private PanelHeightmapSelect panelHeightmapSelect;
	private JFormattedTextField textFieldHeightmapMin;		// Double
    private JFormattedTextField textFieldHeightmapMax;		// Double

	// texture
	private JComboBox comboTextureSource;
	private PanelTextureDetail panelTextureDetailDownload;
	private PanelTextureDetail panelTextureDetailFile;
	private PanelFileSelect panelOsmXmlFileSelect;
	private PanelFileSelect panelTextureImageSelect;
	private JFormattedTextField inputTextureSelection;		// Hex (Formatted String)

	// header
	private JComboBox comboBoxBuildingSet;

	// current state
	protected Config config = null;
	protected ReliefSource rs;
	protected TextureSource ts;
	private SelectionAdapter selection;
	private Tab currentTab = Tab.Settings;
	private TextureChooser tc;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		LoggerConfig.initLogger();

		EventQueue.invokeLater(new Runnable() {
			@Override
            public void run() {
				try {
				    beforeStart();
					MainWindow frame = new MainWindow();
					afterStart();
					frame.setVisible(true);
				} catch (Exception e) {
					log.error("Uncaught exeption", e);
				}
			}
		});
	}

	protected static void beforeStart() {
	    Locale.setDefault(Locale.US);  // no i8n for now...
	    ProxyHelper.restoreProxy();

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch(Exception e) {
            log.error("Could not set look and feel to preferred value...");
        }
	}

	protected static void afterStart() {
	    // start the BackgroundTaskScheduler
	    BackgroundTaskScheduler scheduler = new BackgroundTaskScheduler(
	            new CacheJanitorTask(),
	            new UpdateCheckTask()
	    );
	    scheduler.launch();
    }


	/**
	 * Create the frame.
	 */
	public MainWindow() {
		super();
		setTitle(MESSAGES.getString("MainWindow.title")); //$NON-NLS-1$

		ToolTipManager.sharedInstance().setDismissDelay(30000);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize();
//		setLocationByPlatform(true);
		Components.setIconImages(this);
        initFileChoosers();

        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        JMenu mnFile = new JMenu(MESSAGES.getString("MainWindow.mnFile.text")); //$NON-NLS-1$
        mnFile.setMnemonic('F');
        menuBar.add(mnFile);

        JMenuItem mntmOpenConfiguration = new JMenuItem(MESSAGES.getString("MainWindow.mntmOpenConfiguration.text")); //$NON-NLS-1$
        mntmOpenConfiguration.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
        mntmOpenConfiguration.addActionListener(menuOpenAction);
        
        JMenuItem mntmNew = new JMenuItem("New");
        mntmNew.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setConfig(Config.getMinimalConfig());
            }
        });
        mntmNew.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_MASK));
        mnFile.add(mntmNew);
        mnFile.add(mntmOpenConfiguration);

        JMenuItem mntmSaveConfiguration = new JMenuItem(MESSAGES.getString("MainWindow.mntmSaveConfiguration.text")); //$NON-NLS-1$
        mntmSaveConfiguration.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));
        mntmSaveConfiguration.addActionListener(menuSaveAction);
        mnFile.add(mntmSaveConfiguration);

        JSeparator separator = new JSeparator();
        mnFile.add(separator);

        JMenuItem mntmExit = new JMenuItem(MESSAGES.getString("MainWindow.mntmExit.text")); //$NON-NLS-1$
        mntmExit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, InputEvent.ALT_MASK));
        mntmExit.addActionListener(menuExitAction);
        mnFile.add(mntmExit);

        JMenu mnEdit = new JMenu(MESSAGES.getString("MainWindow.mnEdit.text")); //$NON-NLS-1$
        mnEdit.setMnemonic('T');
        menuBar.add(mnEdit);

        JMenuItem mntmSettings = new JMenuItem(MESSAGES.getString("MainWindow.mntmSettings.text")); //$NON-NLS-1$
        mntmSettings.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F12, 0));
        mntmSettings.addActionListener(menuSettingsAction);

        JMenuItem mntmMetadataeditor = new JMenuItem(MESSAGES.getString("MainWindow.mntmMetadataeditor.text")); //$NON-NLS-1$
        mntmMetadataeditor.addActionListener(menuMetadataEditorAction);
        mntmMetadataeditor.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M, InputEvent.CTRL_MASK));
        mnEdit.add(mntmMetadataeditor);

        JSeparator separator_1 = new JSeparator();
        mnEdit.add(separator_1);
        mnEdit.add(mntmSettings);

        JMenu mnHelp = new JMenu(MESSAGES.getString("MainWindow.mnHelp.text")); //$NON-NLS-1$
        mnHelp.setMnemonic('H');
        menuBar.add(mnHelp);

        JMenuItem mntmAbout = new JMenuItem(MESSAGES.getString("MainWindow.mntmAbout.text")); //$NON-NLS-1$
        mntmAbout.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0));
        mntmAbout.addActionListener(menuAboutAction);

        JMenuItem mntmOnlinehelp = new JMenuItem(MESSAGES.getString("MainWindow.mntmOnlinehelp.text")); //$NON-NLS-1$
        mntmOnlinehelp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openWeb(MESSAGES.getString("MainWindow.mntmOnlinehelp.dest"));
            }
        });
        mntmOnlinehelp.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
        mnHelp.add(mntmOnlinehelp);

        JMenuItem mntmCheckForUpdates = new JMenuItem("Check for updates");
        mntmCheckForUpdates.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateCheck(MainWindow.this);
            }
        });

        JMenuItem mntmQuickStartGuide = new JMenuItem("Quick start guide");
        mntmQuickStartGuide.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new GettingStartedWindow(MainWindow.this).setVisible(true);
            }
        });
        mntmQuickStartGuide.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, InputEvent.CTRL_MASK));
        mnHelp.add(mntmQuickStartGuide);
        mntmCheckForUpdates.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_U, InputEvent.CTRL_MASK));
        mnHelp.add(mntmCheckForUpdates);

        JSeparator separator_2 = new JSeparator();
        mnHelp.add(separator_2);
        mnHelp.add(mntmAbout);
        getContentPane().setLayout(new GridLayout(1, 0, 0, 0));

        jxm = getJxmInstance();
        selection = fact.getSelectionAdapter();

        JSplitPane splitPane = new JSplitPane();
        splitPane.setResizeWeight(1.0);
        splitPane.setOneTouchExpandable(true);
        splitPane.setDividerLocation(this.getWidth() - settingsPanelDefaultWidth);

        getContentPane().add(splitPane);
        map = jxm;
        splitPane.setLeftComponent(map);

        JPanel settingsPanel = new JPanel();
        splitPane.setRightComponent(settingsPanel);
        settingsPanel.setMinimumSize(new Dimension(200, 50));

        tabs = new JTabbedPane(SwingConstants.TOP);

        JPanel tabSettings = new JPanel();
        tabSettings.setBorder(null);

        JScrollPane tabSettingsScroller = new JScrollPane(tabSettings);
        tabSettingsScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        tabSettingsScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        tabSettingsScroller.setBorder(null);

        tabs.addTab(Tab.Settings.name, null, tabSettingsScroller, MESSAGES.getString("MainWindow.tabSettingsScroller.tooltip")); //$NON-NLS-1$

        JPanel panelRelief = new JPanel();
        panelRelief.setToolTipText(MESSAGES.getString("MainWindow.panel_relief.toolTipText")); //$NON-NLS-1$
        panelRelief.setBorder(new TitledBorder(null, MESSAGES.getString("MainWindow.panel_relief.borderTitle"), //$NON-NLS-1$
                TitledBorder.LEADING, TitledBorder.TOP, null, null));

        JPanel panelTexture = new JPanel();
        panelTexture.setToolTipText(MESSAGES.getString("MainWindow.panel_texture.toolTipText")); //$NON-NLS-1$
        panelTexture.setBorder(new TitledBorder(null, MESSAGES.getString("MainWindow.panel_texture.borderTitle"), //$NON-NLS-1$
                TitledBorder.LEADING, TitledBorder.TOP, null, null));

        JPanel panelGameSettings = new JPanel();
        panelGameSettings.setToolTipText("Settings concerning some metadata of this map");
        panelGameSettings.setBorder(new TitledBorder(null, "Game", TitledBorder.LEADING, TitledBorder.TOP, null, null));

        JPanel panelLocation = new JPanel();
        panelLocation.setBorder(new TitledBorder(null, "Location", TitledBorder.LEADING, TitledBorder.TOP, null, null));

        JPanel panelHelp = new JPanel();
        FlowLayout fl_panelHelp = (FlowLayout) panelHelp.getLayout();
        fl_panelHelp.setAlignment(FlowLayout.LEADING);

        GroupLayout gl_tabSettings = new GroupLayout(tabSettings);
        gl_tabSettings.setHorizontalGroup(
            gl_tabSettings.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_tabSettings.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(gl_tabSettings.createParallelGroup(Alignment.LEADING)
                        .addComponent(panelLocation, GroupLayout.PREFERRED_SIZE, settingsPanelContentWidth, Short.MAX_VALUE)
                        .addComponent(panelRelief, GroupLayout.PREFERRED_SIZE, settingsPanelContentWidth, Short.MAX_VALUE)
                        .addComponent(panelTexture, GroupLayout.PREFERRED_SIZE, settingsPanelContentWidth, Short.MAX_VALUE)
                        .addComponent(panelGameSettings, GroupLayout.PREFERRED_SIZE, settingsPanelContentWidth, Short.MAX_VALUE)
                        .addComponent(panelHelp, GroupLayout.PREFERRED_SIZE, settingsPanelContentWidth, Short.MAX_VALUE))
                    .addContainerGap())
        );

        gl_tabSettings.setVerticalGroup(
            gl_tabSettings.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_tabSettings.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(panelLocation, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(panelRelief, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(panelTexture, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(panelGameSettings, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(panelHelp, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addContainerGap())
        );

        JLabel lblNeedHelp = new JLabel("Need help?");
        panelHelp.add(lblNeedHelp);

        JButton btnGettingStarted = new JButton("Quick start guide");
        btnGettingStarted.setToolTipText("<html>Open the quick start guide to maps4cim.<br>\r\nAlso note that there are tooltips for almost everything in here,<br>\r\njust hover with the mouse over a component if you are not sure what it does :)</html>");
        panelHelp.add(btnGettingStarted);
        btnGettingStarted.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new GettingStartedWindow(MainWindow.this).setVisible(true);
            }
        });

        JPanel center = new JPanel();
        center.setToolTipText(MESSAGES.getString("MainWindow.center.toolTipText")); //$NON-NLS-1$
        FlowLayout fl_center = (FlowLayout) center.getLayout();
        fl_center.setAlignment(FlowLayout.LEADING);

        JLabel lblCenter = new JLabel(MESSAGES.getString("MainWindow.lblCenter.text")); //$NON-NLS-1$
        center.add(lblCenter);

        inputLat = new JFormattedTextFieldColored(FormattedComponents.getDecimalFormatter(-90, 90));
        inputLat.setToolTipText(MESSAGES.getString("MainWindow.inputLat.toolTipText")); //$NON-NLS-1$
        inputLat.setText("48.0");
        inputLat.setColumns(6);
        inputLat.addKeyListener(coordinateUpdate);
        center.add(inputLat);

        JLabel lblComma = new JLabel(",");
        center.add(lblComma);

        inputLon = new JFormattedTextFieldColored(FormattedComponents.getDecimalFormatter(-180, 180));
        inputLon.setToolTipText(MESSAGES.getString("MainWindow.inputLon.toolTipText")); //$NON-NLS-1$
        inputLon.setText("11.0");
        inputLon.setColumns(6);
        inputLon.addKeyListener(coordinateUpdate);
        center.add(inputLon);

        sliderExt = new JSlider2(SwingConstants.HORIZONTAL, 0, extSliderMax, 8);
        sliderExt.setToolTipText(MESSAGES.getString("MainWindow.sliderExt.toolTipText")); //$NON-NLS-1$
        sliderExt.addChangeListener(extSliderChange);
        sliderExt.setSnapToTicks(true);
        sliderExt.setPaintLabels(true);
        sliderExt.setPaintTicks(true);
        sliderExt.setMinorTickSpacing(1);
        sliderExt.setMajorTickSpacing(4);

        JPanel extent = new JPanel();
        extent.setToolTipText(MESSAGES.getString("MainWindow.extent.toolTipText")); //$NON-NLS-1$
        FlowLayout fl_extent = (FlowLayout) extent.getLayout();
        fl_extent.setAlignment(FlowLayout.LEADING);

        JLabel lblExtent = new JLabel(MESSAGES.getString("MainWindow.lblExtent.text")); //$NON-NLS-1$
        extent.add(lblExtent);

        inputExtent = new JFormattedTextFieldColored(FormattedComponents.getDecimalFormatter(0.1, 1000));
        inputExtent.setToolTipText(MESSAGES.getString("MainWindow.inputExtent.toolTipText")); //$NON-NLS-1$
        inputExtent.setHorizontalAlignment(SwingConstants.TRAILING);
        inputExtent.setText("8");
        inputExtent.setColumns(4);
        inputExtent.addKeyListener(inputExtentListener);
        extent.add(inputExtent);

        JLabel lblKm = new JLabel("km");
        extent.add(lblKm);

        btnResetExtent = new JButton(MESSAGES.getString("MainWindow.btnResetExtent.text")); //$NON-NLS-1$
        btnResetExtent.setToolTipText(MESSAGES.getString("MainWindow.btnResetExtent.toolTipText")); //$NON-NLS-1$
        btnResetExtent.addActionListener(btnResetExtentAction);
        extent.add(btnResetExtent);
        GroupLayout gl_panelLocation = new GroupLayout(panelLocation);
        gl_panelLocation.setHorizontalGroup(
            gl_panelLocation.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_panelLocation.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(gl_panelLocation.createParallelGroup(Alignment.LEADING)
                        .addComponent(sliderExt, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(extent, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(center, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addContainerGap())
        );
        gl_panelLocation.setVerticalGroup(
            gl_panelLocation.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_panelLocation.createSequentialGroup()
                    .addComponent(center, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(sliderExt, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(extent, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addContainerGap())
        );
        panelLocation.setLayout(gl_panelLocation);

        JLabel lblBuildingSet = new JLabel("Building Set");

        comboBoxBuildingSet = new JComboBox();
        comboBoxBuildingSet.setToolTipText("<html>Choose between the american and european building set.<br>\r\nNote that the \"European Cities\"-DLC is required to play maps generated with the European Building set.<br>\r\nYou may change the building set of your map using the Metadata-Editor (under the Tools-Menu)</html>");
        comboBoxBuildingSet.setModel(new DefaultComboBoxModel(BuildingSet.values()));
        GroupLayout gl_panelGameSettings = new GroupLayout(panelGameSettings);
        gl_panelGameSettings.setHorizontalGroup(
            gl_panelGameSettings.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_panelGameSettings.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(lblBuildingSet)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(comboBoxBuildingSet, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap())
        );
        gl_panelGameSettings.setVerticalGroup(
            gl_panelGameSettings.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_panelGameSettings.createSequentialGroup()
                    .addGroup(gl_panelGameSettings.createParallelGroup(Alignment.BASELINE)
                        .addComponent(lblBuildingSet)
                        .addComponent(comboBoxBuildingSet, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addContainerGap())
        );
        panelGameSettings.setLayout(gl_panelGameSettings);

        JLabel lblTextureSource = new JLabel("Source:");

        comboTextureSource = new JComboBox();
        comboTextureSource.setToolTipText("Choose a source from which the ground texture of this map shall be generated");
        comboTextureSource.setModel(new DefaultComboBoxModel(TextureSource.values()));

        panelTextureCards = new JPanel();
        GroupLayout gl_panelTexture = new GroupLayout(panelTexture);
        gl_panelTexture.setHorizontalGroup(
            gl_panelTexture.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_panelTexture.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(gl_panelTexture.createParallelGroup(Alignment.LEADING)
                        .addComponent(panelTextureCards, GroupLayout.DEFAULT_SIZE, 218, Short.MAX_VALUE)
                        .addGroup(gl_panelTexture.createSequentialGroup()
                            .addComponent(lblTextureSource, GroupLayout.PREFERRED_SIZE, 44, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(comboTextureSource, 0, 193, Short.MAX_VALUE)))
                    .addContainerGap())
        );
        gl_panelTexture.setVerticalGroup(
            gl_panelTexture.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_panelTexture.createSequentialGroup()
                    .addGroup(gl_panelTexture.createParallelGroup(Alignment.BASELINE)
                        .addComponent(comboTextureSource, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblTextureSource))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(panelTextureCards, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
                    .addContainerGap())
        );
        panelTextureCards.setLayout(new AdaptiveCardLayout(0, 0));

        JPanel textureNone = new JPanel();
        textureNone.setToolTipText("just the default grass texture");
        FlowLayout flowLayout = (FlowLayout) textureNone.getLayout();
        flowLayout.setAlignment(FlowLayout.LEADING);
        panelTextureCards.add(textureNone, TextureSource.None.toString());

        JLabel lblNoGroundTexture = new JLabel(MESSAGES.getString("MainWindow.lblNoGroundTexture.text")); //$NON-NLS-1$
        textureNone.add(lblNoGroundTexture);

        JPanel textureSingle = new JPanel();
        panelTextureCards.add(textureSingle, TextureSource.Single.toString());

        JButton btnChooseTexture = new JButton(MESSAGES.getString("MainWindow.btnChooseTexture.text"));
        btnChooseTexture.setToolTipText("Pick a texture using the texture chooser");
        btnChooseTexture.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(tc == null) {
                    tc = new TextureChooser();
                }
                if(tc.showDialog(inputTextureSelection.getText()) == TextureChooser.APPROVE_OPTION) {
                    TexHexTriplet tex = tc.getTexture();
                    inputTextureSelection.setText(tex.getHexString());
                }
            }
        });

        inputTextureSelection = new JFormattedTextFieldColored(FormattedComponents.getHexFormatter(6));
        inputTextureSelection.setToolTipText("<html>The \"color\" of the texture to use for the entire map, stored as hex triplet, similar to HTML colors<br>\r\nOpen the texture chooser to get a preview of your selected texture</html>");
        inputTextureSelection.setText("000000");
        inputTextureSelection.setColumns(10);
        GroupLayout gl_textureSingle = new GroupLayout(textureSingle);
        gl_textureSingle.setHorizontalGroup(
            gl_textureSingle.createParallelGroup(Alignment.LEADING)
                .addGroup(Alignment.TRAILING, gl_textureSingle.createSequentialGroup()
                    .addComponent(inputTextureSelection, GroupLayout.DEFAULT_SIZE, 129, Short.MAX_VALUE)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(btnChooseTexture))
        );
        gl_textureSingle.setVerticalGroup(
            gl_textureSingle.createParallelGroup(Alignment.BASELINE)
                .addComponent(inputTextureSelection, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addComponent(btnChooseTexture)
        );
        textureSingle.setLayout(gl_textureSingle);

        panelTextureDetailDownload = new PanelTextureDetail(
                MESSAGES.getString("MainWindow.rdbtnPresetDetail.text"),
                MESSAGES.getString("MainWindow.rdbtnPresetDetail.toolTipText"),
                MESSAGES.getString("MainWindow.rdbtnCustomDetail.text"),
                MESSAGES.getString("MainWindow.rdbtnCustomDetail.toolTipText"),
                MESSAGES.getString("MainWindow.comboTextureDetail.toolTipText"),
                MESSAGES.getString("MainWindow.btnDefine.text"));

        panelTextureCards.add(panelTextureDetailDownload, TextureSource.OsmDownload.toString());
        panelTextureDetailDownload.setToolTipText(MESSAGES.getString("MainWindow.textureOsmDownload.toolTipText"));

        JPanel textureOsmFile = new JPanel();
        panelTextureCards.add(textureOsmFile, TextureSource.OsmFile.toString());

        panelTextureDetailFile = new PanelTextureDetail(
                MESSAGES.getString("MainWindow.rdbtnPresetDetail.text"),
                MESSAGES.getString("MainWindow.rdbtnPresetDetail.toolTipText"),
                MESSAGES.getString("MainWindow.rdbtnCustomDetail.text"),
                MESSAGES.getString("MainWindow.rdbtnCustomDetail.toolTipText"),
                MESSAGES.getString("MainWindow.comboTextureDetail.toolTipText"),
                MESSAGES.getString("MainWindow.btnDefine.text"));


        panelOsmXmlFileSelect = new PanelFileSelect("", MESSAGES.getString("MainWindow.btnBrowse.text"), loadOsmXml);
        panelOsmXmlFileSelect.setToolTipText("The OSM XML file to use as data source");
        GroupLayout gl_textureOsmFile = new GroupLayout(textureOsmFile);
        gl_textureOsmFile.setHorizontalGroup(
            gl_textureOsmFile.createParallelGroup(Alignment.LEADING)
                .addComponent(panelOsmXmlFileSelect, GroupLayout.DEFAULT_SIZE, 210, Short.MAX_VALUE)
                .addComponent(panelTextureDetailFile, GroupLayout.DEFAULT_SIZE, 210, Short.MAX_VALUE)
        );
        gl_textureOsmFile.setVerticalGroup(
            gl_textureOsmFile.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_textureOsmFile.createSequentialGroup()
                    .addComponent(panelOsmXmlFileSelect, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(panelTextureDetailFile, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
        );

        textureOsmFile.setLayout(gl_textureOsmFile);

        JPanel textureImage = new JPanel();
        panelTextureCards.add(textureImage, TextureSource.Image.toString());

        comboTextureSource.addItemListener(textureSourceListener);

        panelTextureImageSelect = new PanelFileSelect("", "Browse", loadImage);
        panelTextureImageSelect.setToolTipText("Choose an image that will be translated into a ground texture for the entire map");

        JPanel panelColorMapping = new JPanel();
        FlowLayout fl_panelColorMapping = (FlowLayout) panelColorMapping.getLayout();
        fl_panelColorMapping.setAlignment(FlowLayout.LEADING);
        GroupLayout gl_textureImage = new GroupLayout(textureImage);
        gl_textureImage.setHorizontalGroup(
            gl_textureImage.createParallelGroup(Alignment.LEADING)
                .addComponent(panelTextureImageSelect, GroupLayout.DEFAULT_SIZE, 210, Short.MAX_VALUE)
                .addComponent(panelColorMapping, GroupLayout.DEFAULT_SIZE, 210, Short.MAX_VALUE)
        );
        gl_textureImage.setVerticalGroup(
            gl_textureImage.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_textureImage.createSequentialGroup()
                    .addComponent(panelTextureImageSelect, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(panelColorMapping, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
        );

        JLabel lblColorMapping = new JLabel(MESSAGES.getString("MainWindow.lblColorMapping.text")); //$NON-NLS-1$
        panelColorMapping.add(lblColorMapping);

        JButton btnDefineColorMapping = new JButton(MESSAGES.getString("MainWindow.btnDefineColorMapping.text")); //$NON-NLS-1$
        btnDefineColorMapping.setToolTipText("define a custom color mapping");
        btnDefineColorMapping.setEnabled(false);
        panelColorMapping.add(btnDefineColorMapping);
        textureImage.setLayout(gl_textureImage);
        panelTexture.setLayout(gl_panelTexture);

        comboReliefSource = new JComboBox();
        comboReliefSource.setToolTipText("Choose a source from which the relief of this map shall be generated");
        comboReliefSource.setModel(new DefaultComboBoxModel(ReliefSource.values()));

        JLabel lblReliefSource = new JLabel(MESSAGES.getString("MainWindow.lblReliefSource.text")); //$NON-NLS-1$

        panelReliefCards = new JPanel();
        GroupLayout gl_panelRelief = new GroupLayout(panelRelief);
        gl_panelRelief.setHorizontalGroup(
            gl_panelRelief.createParallelGroup(Alignment.TRAILING)
                .addGroup(gl_panelRelief.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(gl_panelRelief.createParallelGroup(Alignment.TRAILING)
                        .addComponent(panelReliefCards, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 206, Short.MAX_VALUE)
                        .addGroup(gl_panelRelief.createSequentialGroup()
                            .addComponent(lblReliefSource)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(comboReliefSource, 0, 150, Short.MAX_VALUE)))
                    .addContainerGap())
        );
        gl_panelRelief.setVerticalGroup(
            gl_panelRelief.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_panelRelief.createSequentialGroup()
                    .addGroup(gl_panelRelief.createParallelGroup(Alignment.BASELINE)
                        .addComponent(comboReliefSource, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblReliefSource))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(panelReliefCards, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addContainerGap())
        );
        panelReliefCards.setLayout(new AdaptiveCardLayout(0, 0));

        JPanel reliefNone = new JPanel();
        reliefNone.setToolTipText("that's right, your map will be flat...");
        FlowLayout flowLayout_3 = (FlowLayout) reliefNone.getLayout();
        flowLayout_3.setAlignment(FlowLayout.LEFT);
        panelReliefCards.add(reliefNone, ReliefSource.None.toString());

        JLabel lblNoRelief = new JLabel(MESSAGES.getString("MainWindow.lblNoRelief.text")); //$NON-NLS-1$
        reliefNone.add(lblNoRelief);

        JPanel reliefPlanar = new JPanel();
        reliefPlanar.setToolTipText("The map will have a flat surface with the specified height (note that negative values will create water)");
        FlowLayout flowLayout_4 = (FlowLayout) reliefPlanar.getLayout();
        flowLayout_4.setAlignment(FlowLayout.LEADING);
        panelReliefCards.add(reliefPlanar, ReliefSource.Planar.toString());

        JLabel lblHeight = new JLabel(MESSAGES.getString("MainWindow.lblHeight.text")); //$NON-NLS-1$
        reliefPlanar.add(lblHeight);

        textFieldReliefPlanarHeight = new JFormattedTextFieldColored(FormattedComponents.getDecimalFormatter(-1024, 1024));
        textFieldReliefPlanarHeight.setToolTipText("the static height of the map");
        textFieldReliefPlanarHeight.setHorizontalAlignment(SwingConstants.TRAILING);
        textFieldReliefPlanarHeight.setText("0");
        reliefPlanar.add(textFieldReliefPlanarHeight);
        textFieldReliefPlanarHeight.setColumns(4);

        JLabel lblM = new JLabel("m");
        reliefPlanar.add(lblM);

        JPanel reliefSrtm = new JPanel();
        panelReliefCards.add(reliefSrtm, ReliefSource.SRTM.toString());

        panelHeightOffset = new PanelHeightOffset(
                MESSAGES.getString("MainWindow.lblHeightOffset.text"),
                MESSAGES.getString("MainWindow.chckbxHeightOffsetAuto.text"),
                MESSAGES.getString("MainWindow.inputHeightOffset.toolTipText"),
                MESSAGES.getString("MainWindow.chckbxHeightOffsetAuto.toolTipText")); //$NON-NLS-1$
        panelHeightOffset.setToolTipText(MESSAGES.getString("MainWindow.heightOffset.toolTipText")); //$NON-NLS-1$

        panelHeightScale = new PanelHeightScale(
                MESSAGES.getString("MainWindow.lblHeightScale.text"),
                MESSAGES.getString("MainWindow.chckbxHeightScaleAuto.text"),
                MESSAGES.getString("MainWindow.inputHeightScale.toolTipText"),
                MESSAGES.getString("MainWindow.chckbxHeightScaleAuto.toolTipText")); //$NON-NLS-1$
        panelHeightScale.setToolTipText(MESSAGES.getString("MainWindow.heightScale.toolTipText")); //$NON-NLS-1$

        GroupLayout gl_reliefSrtm = new GroupLayout(reliefSrtm);
        gl_reliefSrtm.setHorizontalGroup(
            gl_reliefSrtm.createParallelGroup(Alignment.LEADING)
                .addComponent(panelHeightOffset, GroupLayout.DEFAULT_SIZE, 221, Short.MAX_VALUE)
                .addComponent(panelHeightScale, GroupLayout.DEFAULT_SIZE, 221, Short.MAX_VALUE)
        );
        gl_reliefSrtm.setVerticalGroup(
            gl_reliefSrtm.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_reliefSrtm.createSequentialGroup()
                    .addComponent(panelHeightOffset, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(panelHeightScale, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
        );
        reliefSrtm.setLayout(gl_reliefSrtm);

        JPanel reliefHeightmap = new JPanel();
        panelReliefCards.add(reliefHeightmap, ReliefSource.Heightmap.toString());

        comboReliefSource.addItemListener(reliefSourceListener);

        panelHeightmapSelect = new PanelHeightmapSelect("", "Browse", loadImage, heightMapWindowActionListener);
        panelHeightmapSelect.setToolTipText("Select a greyscale image as heightmap");

        JPanel panelHeightmapBounds = new JPanel();
        panelHeightmapBounds.setToolTipText("The lightness of the selected image will be mapped within these bounds");
        FlowLayout fl_panelHeightmapBounds = (FlowLayout) panelHeightmapBounds.getLayout();
        fl_panelHeightmapBounds.setAlignment(FlowLayout.LEADING);
        GroupLayout gl_reliefHeightmap = new GroupLayout(reliefHeightmap);
        gl_reliefHeightmap.setHorizontalGroup(
            gl_reliefHeightmap.createParallelGroup(Alignment.LEADING)
                .addComponent(panelHeightmapBounds, GroupLayout.DEFAULT_SIZE, 206, Short.MAX_VALUE)
                .addComponent(panelHeightmapSelect, GroupLayout.DEFAULT_SIZE, 206, Short.MAX_VALUE)
        );
        gl_reliefHeightmap.setVerticalGroup(
            gl_reliefHeightmap.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_reliefHeightmap.createSequentialGroup()
                    .addComponent(panelHeightmapSelect, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(panelHeightmapBounds, GroupLayout.PREFERRED_SIZE, 34, GroupLayout.PREFERRED_SIZE))
        );

        JLabel lblMin = new JLabel(MESSAGES.getString("MainWindow.lblMin.text")); //$NON-NLS-1$
        panelHeightmapBounds.add(lblMin);

        textFieldHeightmapMin = new JFormattedTextFieldColored(FormattedComponents.getDecimalFormatter(-1024, 1024));
        textFieldHeightmapMin.setToolTipText("the lowest point of the map");
        textFieldHeightmapMin.setHorizontalAlignment(SwingConstants.TRAILING);
        textFieldHeightmapMin.setText(MESSAGES.getString("MainWindow.textFieldHeightmapMin.text")); //$NON-NLS-1$
        panelHeightmapBounds.add(textFieldHeightmapMin);
        textFieldHeightmapMin.setColumns(3);

        JLabel lblMax = new JLabel(MESSAGES.getString("MainWindow.lblMax.text")); //$NON-NLS-1$
        panelHeightmapBounds.add(lblMax);

        textFieldHeightmapMax = new JFormattedTextFieldColored(FormattedComponents.getDecimalFormatter(-1024, 1024));
        textFieldHeightmapMax.setToolTipText("the highest point of the map");
        textFieldHeightmapMax.setHorizontalAlignment(SwingConstants.TRAILING);
        textFieldHeightmapMax.setText(MESSAGES.getString("MainWindow.textFieldHeightmapMax.text")); //$NON-NLS-1$
        panelHeightmapBounds.add(textFieldHeightmapMax);
        textFieldHeightmapMax.setColumns(3);

        JButton btnHeightmapDefine = new JButton(MESSAGES.getString("MainWindow.btnHeightmapDefine.text")); //$NON-NLS-1$
        btnHeightmapDefine.setToolTipText("Define the boundries of your heightmap in the graphical editor");
        btnHeightmapDefine.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!Strings.isNullOrEmpty(textFieldHeightmapMin.getText()) && !Strings.isNullOrEmpty(textFieldHeightmapMax.getText())) {
                    panelHeightmapSelect.openHeightmapWindow(textFieldHeightmapMin.getText(), textFieldHeightmapMax.getText());
                } else {
                    panelHeightmapSelect.openHeightmapWindow();
                }
            }
        });
        panelHeightmapBounds.add(btnHeightmapDefine);
        reliefHeightmap.setLayout(gl_reliefHeightmap);

        panelRelief.setLayout(gl_panelRelief);
        tabSettings.setLayout(gl_tabSettings);

        JPanel tabXML = new JPanel();

        tabs.addTab(Tab.XML.name, null, tabXML, MESSAGES.getString("MainWindow.tabXML.tooltip")); //$NON-NLS-1$
        tabXML.setLayout(new BorderLayout(0, 0));

        xmlEditor = new XmlTextPane();
        xmlEditor.setFont(new Font("Monospaced", Font.PLAIN, 11));
        xmlEditor.setText(MESSAGES.getString("MainWindow.xmlEditor.text")); //$NON-NLS-1$

        JScrollPane scrollPane = new JScrollPane(xmlEditor);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(null);
        tabXML.add(scrollPane, BorderLayout.CENTER);

        JButton btnRender = new JButton(MESSAGES.getString("MainWindow.btnRender.text")); //$NON-NLS-1$
        btnRender.setToolTipText(MESSAGES.getString("MainWindow.btnRender.toolTipText")); //$NON-NLS-1$
        GroupLayout gl_settingsPanel = new GroupLayout(settingsPanel);
        gl_settingsPanel.setHorizontalGroup(gl_settingsPanel.createParallelGroup(Alignment.LEADING).addGroup(
                gl_settingsPanel.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(gl_settingsPanel.createParallelGroup(Alignment.LEADING)
                        .addComponent(tabs, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 382, Short.MAX_VALUE)
                        .addComponent(btnRender, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 382, Short.MAX_VALUE))
                    .addContainerGap())
        );
        gl_settingsPanel.setVerticalGroup(gl_settingsPanel.createParallelGroup(Alignment.LEADING)
                .addGroup(Alignment.TRAILING, gl_settingsPanel.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(tabs, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
                    .addPreferredGap(ComponentPlacement.UNRELATED)
                    .addComponent(btnRender)
                    .addContainerGap())
        );
        settingsPanel.setLayout(gl_settingsPanel);
        btnRender.addActionListener(btnRenderAction);

        // listeners last
        tabSettingsScroller.addComponentListener(formTabOpened);
        tabXML.addComponentListener(xmlTabOpened);

        // default settings
        comboTextureSource.setSelectedItem(TextureSource.OsmDownload);
        comboReliefSource.setSelectedItem(ReliefSource.SRTM);

        loadLastConfig();
	}

	// gui helpers

	/**
	 * set the window size to a reasonable value, depending on the display size
	 */
	protected void setSize() {
	    Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
	    int width = (int) screen.getWidth();
	    int height = (int) screen.getHeight();

	    if(width / height > 2) {	// very wide screen
	    	width = (int) (height * 1.5);
	    } else if(width / height < 0.7) {	// rotated widescreen (or unusually high)
	    	height = width;
	    	width = (int) (width * 1.5);
	    }
	    setBounds(100, 50, (int)(width*0.8), (int)(height*0.8));
//	    setBounds(100, 50, 900, 800);
        setMinimumSize(new Dimension(350, 200));
    }

	/**
	 * Update the contents of the view. Shall be called when the current
	 * config changes
	 */
	protected void updateView() {
		if(currentTab == Tab.Settings) {
			updateViewForm();
		} else {
			updateViewXml();
		}
	}

	/**
	 * Update the contents of the XML tab
	 */
	protected void updateViewXml() {
		try {
			String xml = Serializer.serializeToString(Config.class, config);
			xmlEditor.setText(xml);
		} catch (Exception e) {
			log.error("Error while updating XML tab", e);
		}
	}

	/**
	 * Update the contents of the settings tab (form fields)
	 */
	protected void updateViewForm() {

		// rectangle: center + extent
	    Area area = Area.of(config.getBoundsTrans());
        Coordinate center = area.getCenter();
        setCenter(center.getLatitude(), center.getLongitude());
        double extent = Math.round(area.getWidthKm() * 100) / 100;
        inputExtent.setValue(extent);
        
        // relief
        try {
            ReliefDef def = config.getReliefTrans();
            ReliefSource src = ReliefSource.of(def.getClass());
            comboReliefSource.setSelectedItem(src);

            switch (src) {
                case None:
                    break;
                case Planar:
                    PlanarReliefDef defP = (PlanarReliefDef) def;
                    textFieldReliefPlanarHeight.setValue(defP.height);
                    break;
                case SRTM:
                    SrtmDef defS = (SrtmDef) def;
                    panelHeightOffset.setHeightOffset(defS.getHeightOffset());
                    panelHeightOffset.setHeightOffsetAuto(defS.isHeightOffsetAuto());
                    panelHeightScale.setHeightScale(defS.getHeightScale());
                    panelHeightScale.setHeightScaleAuto(defS.isHeighScaleAuto());
                    break;
                case Heightmap:
                    HeightmapDef defH = (HeightmapDef) def;
                    panelHeightmapSelect.setFilePath(defH.heightMapPath);
                    textFieldHeightmapMin.setValue(defH.heightMapMinimum);
                    textFieldHeightmapMax.setValue(defH.heightMapMaximum);
                    break;
                default:
                    log.warn("Unexpected ReliefSource {}", src);
                    break;
            }
        } catch (Exception e) {
            log.warn("Could not load relief config into the main window", e);
            comboReliefSource.setSelectedItem(ReliefSource.SRTM);
            panelHeightOffset.setDefaults();
            panelHeightScale.setDefaults();
        }

        // texture
        try {
            TextureDef def = config.getTextureTrans();
            TextureSource src = TextureSource.of(def.getClass());
            comboTextureSource.setSelectedItem(src);

            switch (src) {
                case None:
                    break;
                case Single:
                    SingleTextureDef defS = (SingleTextureDef) def;
                    TexHexTriplet hex = TexHexTriplet.of(defS.getGround());
                    inputTextureSelection.setText(hex.getHexString());
                    break;
                case OsmDownload:
                    OsmDef defO = (OsmDef) def;
                    panelTextureDetailDownload.setTextureDetail(TextureDetail.highestForExtent(area.getWidthKm()));
                    break;
                case OsmFile:
                    OsmFileDef defOf = (OsmFileDef) def;
                    panelTextureDetailFile.setTextureDetail(TextureDetail.highestForExtent(area.getWidthKm()));
                    panelOsmXmlFileSelect.setFilePath(defOf.osmXmlFilePath);
                    break;
                case Image:
                    ImageDef defI = (ImageDef) def;
                    panelTextureImageSelect.setFilePath(defI.imageFilePath);
                    break;
                default:
                    log.warn("Unexpected TextureSource {}", src);
                    break;
            }
        } catch (Exception e) {
            log.warn("Could not load texture config into the main window", e);
            comboTextureSource.setSelectedItem(TextureSource.OsmDownload);
        }

        // header
        if(config.header != null && config.header.buildingSet != null) {
            comboBoxBuildingSet.setSelectedItem(config.header.buildingSet);
        } else {
            comboBoxBuildingSet.setSelectedIndex(0);
        }

	}

	protected JXMapViewer getJxmInstance() {
	    JXMapViewer jxm = fact.getMapViewer();
        jxm.addMouseListener(new CenterOnClickListener(this, jxm));
        return jxm;
	}

	public void restartJXM() {
	    map = getJxmInstance();
	}

    // menu actions

    protected ActionListener menuExitAction = new ActionListener() {
    	@Override
		public void actionPerformed(ActionEvent e) {
    		exit();
		}
	};

	protected ActionListener menuSaveAction = new ActionListener() {
    	@Override
		public void actionPerformed(ActionEvent e) {
    		safeConfig();
		}
	};

	protected ActionListener menuOpenAction = new ActionListener() {
    	@Override
		public void actionPerformed(ActionEvent e) {
    		openConfig();
		}
	};

	protected ActionListener menuSettingsAction = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            SettingsWindow sw = new SettingsWindow(MainWindow.this);
            sw.setVisible(true);
        }
    };

	protected ActionListener menuAboutAction = new ActionListener() {
    	@Override
		public void actionPerformed(ActionEvent e) {
    		AboutWindow aw = new AboutWindow(MainWindow.this);
    		aw.setVisible(true);
		}
	};

	protected ActionListener menuMetadataEditorAction = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            MetaEditorWindow mew = new MetaEditorWindow();
            mew.setVisible(true);
        }
    };

	// main window actions

	protected ActionListener btnRenderAction = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if(updateConfigAndValidate()) {
                openRenderWindow(config);
            }
        }
    };

    protected ChangeListener extSliderChange = new ChangeListener() {
        @Override
        public void stateChanged(ChangeEvent e) {
        	int sliderVal = sliderExt.getValue();
        	double inputVal = ((Number) inputExtent.getValue()).doubleValue();
        	if(Math.abs(sliderVal - inputVal) > 0.01) {
        		inputExtent.setValue((double) sliderVal);
        		updateExtentSlider(sliderVal);
        	}
        }
    };

    protected ActionListener btnResetExtentAction = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if(Math.abs(((Number) inputExtent.getValue()).doubleValue() - 8.0) > 0.00001) {
                inputExtent.setValue(8.0);
                updateExtentSlider(8.0);
            }
        }
    };

	protected KeyAdapter inputExtentListener = new KeyAdapter(){
        @Override
        public void keyReleased(KeyEvent ke) {
            updateExtentSlider();
        }
    };


	protected void updateExtentSlider() {
	    if(Strings.isNullOrEmpty(inputExtent.getText())) {
	        return;
	    }
	    updateExtentSlider(((Number) inputExtent.getValue()).doubleValue());
	}
	
	protected void updateExtentSlider(final double extent) {
        final int iExtent = (int) extent;
        if(iExtent != sliderExt.getValue()) {
            if(iExtent < extSliderMax) {
                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        sliderExt.setValue(iExtent, false);
                    }
                });
            } else if(iExtent > extSliderMax && sliderExt.getValue() < extSliderMax) {
                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        sliderExt.setValue(extSliderMax, false);
                        inputExtent.setValue(extent);
                    }
                });
            }
        }
        selection.updateExtent(extent);
    }

	protected KeyAdapter coordinateUpdate = new KeyAdapter(){
        @Override
        public void keyReleased(KeyEvent ke) {
            updateCenter();
            selection.centerView();
            selection.resetZoom();
        }
    };

	protected void updateCenter() {
		try {
			double lat = Double.parseDouble(inputLat.getText());
			double lon = Double.parseDouble(inputLon.getText());
			selection.updateCenter(lat, lon);
		} catch(NumberFormatException e) {
			// just keep the last center for invalid values, so ignore...
		}
	}

	protected ItemListener reliefSourceListener = new ItemListener() {
        @Override
        public void itemStateChanged(ItemEvent e) {
            cardStateChanged(panelReliefCards, e);
            rs = (ReliefSource) e.getItem();
        }
    };

    protected ItemListener textureSourceListener = new ItemListener() {
        @Override
        public void itemStateChanged(ItemEvent e) {
            cardStateChanged(panelTextureCards, e);
            ts = (TextureSource) e.getItem();
        }
    };

	protected static void cardStateChanged(JComponent cards, ItemEvent e) {
        CardLayout cl = (CardLayout)(cards.getLayout());
        cl.show(cards, e.getItem().toString());
    }

	protected ActionListener heightMapWindowActionListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            HeightmapWindow hw = panelHeightmapSelect.getHeightmapWindow();
            textFieldHeightmapMin.setValue(hw.getMinValue());
            textFieldHeightmapMax.setValue(hw.getMaxValue());
            panelHeightmapSelect.setFilePath(hw.getImageFile().getPath());
        }
    };

	// tabs

	private boolean invalidate = false;

	/**
	 * gets called when the xml tab is being opened
	 */
    protected ComponentAdapter xmlTabOpened = new ComponentAdapter() {
		@Override
		public void componentShown(ComponentEvent e) {
			if(invalidate) {
				invalidate = false;
				return;
			}

			if(updateConfigAndValidate()) {
				updateViewXml();
				currentTab = Tab.XML;
			} else {
				// prevent loop by setting invalidate flag
				invalidate = true;
				tabs.setSelectedIndex(Tab.Settings.index);
			}
		}
	};

	/**
	 * gets called when the settings tab with the form fields is being opened
	 */
	protected ComponentAdapter formTabOpened = new ComponentAdapter() {
		@Override
		public void componentShown(ComponentEvent e) {
			if(invalidate) {
				invalidate = false;
				return;
			}

			if(updateConfigAndValidate()) {
				updateViewForm();
				currentTab = Tab.Settings;
			} else {
				// prevent loop by setting invalidate flag
				invalidate = true;
				tabs.setSelectedIndex(Tab.XML.index);
			}
		}
	};

    // shared actions

	protected void exit() {
		dispose();
		System.exit(0);
	}

	private JFileChooser saveConfig;
	private JFileChooser loadConfig;
	private JFileChooser saveMap;
	private JFileChooser loadImage = new ImageJFileLoader("Open image");
	private JFileChooser loadOsmXml;

	protected void initFileChoosers() {

		FileExtensionFilter xmlFilter = new FileExtensionFilter("XML", "xml");
		FileExtensionFilter mapFilter = new FileExtensionFilter("CiM 2 Map", "map");
		FileExtensionFilter osmFilter = new FileExtensionFilter("OSM XML", true, "osm", "osm.gz", "osm.bz2", "xml", "xml.gz", "xml.bz2");

		saveConfig = setupFileSaver("Save configuration", xmlFilter, new File("config.xml"), "xml");
		loadConfig = setupFileLoader("Load configuration", xmlFilter, new File("config.xml"));
		loadOsmXml = setupFileLoader("Load OpenStreetMap XML", osmFilter, new File("export.osm"));

		if(System.getProperty("os.name").startsWith("Windows")) {
		    File mapDir = new File(ResourceLoader.appdata.getParentFile().getParentFile(), "LocalLow/Colossal Order/Cities in Motion 2/Maps");
            saveMap    = setupFileSaver("Save generated map", mapFilter, new File(mapDir, "maps4cim.map"), "map");
        } else {
            saveMap    = setupFileSaver("Save generated map", mapFilter, new File("maps4cim.map"), "map");
        }

	}

	public static JFileChooser setupFileLoader(String title, FileFilter filter, File selected) {
	    JFileChooser load = new JFileChooser();
	    return setupFileChooser(load, title, JFileChooser.OPEN_DIALOG, filter, selected);
	}

	public static JFileChooser setupFileSaver(String title, FileFilter filter, File selected, String extension) {
	    JFileSaver save = new JFileSaver(extension);
	    return setupFileChooser(save, title, JFileChooser.SAVE_DIALOG, filter, selected);
    }

	private static JFileChooser setupFileChooser(JFileChooser chooser, String title, int type, FileFilter filter, File selected) {
	    chooser.setDialogTitle(title);
	    chooser.setDialogType(type);
	    chooser.setFileFilter(filter);
	    chooser.setSelectedFile(selected);
	    chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
	    chooser.setMultiSelectionEnabled(false);
		return chooser;
	}

	/**
	 * Safes the current configuration. Updates all config values, before
	 * actually writing to file. If the config contains errors, the
	 * user gets warned
	 */
    protected void safeConfig() {
        if(updateConfigAndValidate()) {
            if (saveConfig.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                File f = saveConfig.getSelectedFile();
                try {
                    Serializer.serialize(Config.class, config, f);
                } catch (JAXBException e) {
                    log.error("Could not serialize config to file", e);
                    Throwable linked = e.getLinkedException();
                    popupError("Could not save the current configuration:<br>" + linked.getMessage());
                }
            }
        }
    }

	/**
	 * Loads the selected configuration. If the config contains errors, the
	 * user gets a notification and the previous config remains in place.
	 */
	protected void openConfig() {
		if (loadConfig.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File f = loadConfig.getSelectedFile();

            try {
				Config c = Serializer.deserialize(Config.class, f, true);
				
				// validate
				List<ConstraintViolation> cvs = ValidatorUtils.validateR(c);
	            if(cvs.isEmpty()) {
	                setConfig(c);
	            } else {
	                handleConstraintViolations(cvs);
	            }
	            
			} catch (JAXBException e) {
				Throwable linked = e.getLinkedException();
				log.error("Could not open config", e);
				popupError("Could not open config:<br>" + linked.getMessage());
			} catch (Exception e) {
				log.error("Could not open config", e);
				popupError("Could not open config:<br>" + e.getMessage());
			}
        }
	}
	
	/**
	 * sets this config and updates the GUI.
	 * Warning: This is a low-level function, does not run any checks!
	 * @param c the config to set
	 */
	protected void setConfig(Config c) {
	    this.config = c;
        updateViewForm();
        updateViewXml();
        updateCenterInMap(c);
        updateExtentSlider();
	}

	/**
	 * Opens the render window and starts to build the map, based on the
	 * selected user configuration. First, a file selector will appear, so
	 * the user may choose where to store the resulting map
	 * @param conf the configuration from which the map is generated
	 */
    protected void openRenderWindow(final Config conf) {
        if(updateConfigAndValidate()) {
            if (saveMap.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                final File selection = saveMap.getSelectedFile();

                try {
                    selection.createNewFile();
                    if (selection.exists() && selection.canWrite())
                        EventQueue.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    RenderWindow frame = new RenderWindow();
                                    frame.setVisible(true);
                                    frame.runMapGenerator(conf, selection);
                                } catch (Exception e) {
                                    log.error("Error in the Rendering-Window", e);
                                }
                            }
                        });
                } catch (IOException e) {
                    log.error("Cannot write the resulting map to the specified file", e);
                    popupError("Cannot write the resulting map to "
                            + selection.toString() + "<br>" + e.getMessage());
                }
            }
        }
    }

	/**
	 * Sets the center form field to the specified values
	 * @param lat the latitude to set
	 * @param lon the longitude to set
	 */
	public void setCenter(final double lat, final double lon) {
		EventQueue.invokeLater(new Runnable() {
			@Override
            public void run() {
				inputLat.setValue(lat);
				inputLon.setValue(lon);
			}
		});
	}

	// application logic

	/**
	 * Updates the configuration based on the current settings (depending on the
     * selected tab) and validates the settings.
     * If everything is fine, this function returns true.
     * If an exception occurs or a setting is invalid, the user is notified with
     * a popup message (if available) and the function returns false.
	 * @return true, iff no errors occur while updating the configuration
	 */
	protected boolean updateConfigAndValidate() {
	    try {
            Config c = parseConfig();
            List<ConstraintViolation> cvs = ValidatorUtils.validateR(c);
            if(cvs.isEmpty()) {
                this.config = c;
                return true;
            } else {
                handleConstraintViolations(cvs);
                return false;
            }
        } catch (ConfigValidationException e) {
            handleConfigException(e);
            return false;
        }
	}
	
	private void handleConfigException(ConfigValidationException e) {
	    log.error("Error updating the config", e.getCause());
	    if(Strings.isNullOrEmpty(e.getErrorPopupMessage())) {
	        popupErrorConfig("Your configuration seems to be invalid: " + e.getMessage());
	    } else {
	        popupErrorConfig(e.getErrorPopupMessage());
	    }
	}

	/**
	 * Parses the configuration based on the current settings (depending on the
     * selected tab).
	 * @return the syntactically correct configuration as defined by the user
	 * @throws ConfigValidationException if there is an error parsing the
	 *         configuration (note that the config will NOT be validated,
	 *         use a {@link Validator} for this!)
	 */
    protected Config parseConfig() throws ConfigValidationException {
    	if(currentTab == Tab.Settings) {
    	    return parseConfigForm();
		} else {
		    return parseConfigXML();
		}
    }

    /**
     * Creates a new Configuration from the XML document in the xml tab.
     * Check for validity first with {@link MainWindow#validateXmlInput()},
     * or exeptions may be thrown...
     * @return the new config based on the user defined xml
     * @throws ConfigValidationException if the XML config contains errors
     */
    protected Config parseConfigXML() throws ConfigValidationException {
        try {
            return Serializer.deserializeFromString(Config.class, xmlEditor.getText());
        } catch (JAXBException e) {
            throw new ConfigValidationException("The XML-configuration could not be parsed", e,
                    "Your XML seems to be invalid: " + e.getLinkedException().getMessage());
        } catch(RuntimeException e) {
            throw new ConfigValidationException("Unexpected exception while reading XML-configuration", e);
        }
    }

    /**
     * Creates a new Configuration from the form fields of the settings tab.
     * @return the new config based on the form details
     * @throws ConfigValidationException
     */
    protected Config parseConfigForm() throws ConfigValidationException {
        Config c = new Config();
        c.setBoundsTrans(parseConfigFormBounds());
        c.setReliefTrans(parseConfigFormRelief());
        c.setTextureTrans(parseConfigFormTexture());
        c.setHeader(parseConfigFormHeader());
        return c;
    }

    protected BoundsDef parseConfigFormBounds() {
        double lat = ((Number) inputLat.getValue()).doubleValue();
        double lon = ((Number) inputLon.getValue()).doubleValue();
        double extent = ((Number) inputExtent.getValue()).doubleValue();
        return CenterDef.of(lat, lon, extent, Unit.KM);
    }

    protected ReliefDef parseConfigFormRelief() throws ConfigValidationException {
        switch(rs) {
            case None:
                return new ReliefDef.ReliefDefNone();
            case Planar:
                PlanarReliefDef planar = new PlanarReliefDef();
                planar.height = ((Number) textFieldReliefPlanarHeight.getValue()).doubleValue();
                return planar;
            case SRTM:
                try {
                    SrtmDef srtm = new SrtmDef();
                    srtm.heightOffset = panelHeightOffset.isHeightOffsetAuto() ? "auto" : panelHeightOffset.getHeightOffset().toString();
                    srtm.heightScale  = panelHeightScale.isHeightScaleAuto()   ? "auto" : String.valueOf(Double.parseDouble(panelHeightScale.getHeightScale()) / 100d);
                    return srtm;
                } catch(RuntimeException e) {
                    String message = "Error parsing input for the SRTM-based relief";
                    throw new ConfigValidationException(message, e, getFormattedErrorMsg(message + ": " + e.getMessage()));
                }
            case Heightmap:
                HeightmapDef heightmap = new HeightmapDef();
                heightmap.heightMapPath = panelHeightmapSelect.getFilePath();
                heightmap.heightMapMinimum = ((Number) textFieldHeightmapMin.getValue()).doubleValue();
                heightmap.heightMapMaximum = ((Number) textFieldHeightmapMax.getValue()).doubleValue();
                return heightmap;
            default:
                String error = String.format("ReliefSource %s is not supported!", rs);
                throw new ConfigValidationException(error, error);
        }
    }

    protected TextureDef parseConfigFormTexture() throws ConfigValidationException {
        switch (ts) {
            case None:
                return new TextureDef.TextureDefNone();
            case Single:
                String input = inputTextureSelection.getText();
                try {
                    TexHexTriplet hex = TexHexTriplet.parse(input);
                    SingleTextureDef single = new SingleTextureDef();
                    single.ground = hex.getColorDef();
                    return single;
                } catch(NumberFormatException e) {
                    String message;
                    if(Strings.isNullOrEmpty(input)) {
                        message = "No ground texture has been selected!";
                    } else {
                        message = "Texture definition seems to be invalid";
                    }
                    throw new ConfigValidationException(message, e, getFormattedErrorMsg(message + ": " + e.getMessage()));
                }
            case OsmDownload:
                try {
                    TextureDetail td = TextureDetail.byIndex(panelTextureDetailDownload.getTextureDetailIndex());
                    return ConfigPresets.get(td);
                } catch(Exception e) {
                    String message = "Error parsing input for the ground texture";
                    throw new ConfigValidationException(message, e, getFormattedErrorMsg(message + ": " + e.getMessage()));
                }
            case OsmFile:
                try {
                    TextureDetail td = TextureDetail.byIndex(panelTextureDetailFile.getTextureDetailIndex());
                    return ConfigPresets.get(td, panelOsmXmlFileSelect.getFilePath());
                } catch(Exception e) {
                    String message = "Error parsing input for the ground texture";
                    throw new ConfigValidationException(message, e, getFormattedErrorMsg(message + ": " + e.getMessage()));
                }
            case Image:
                ImageDef img = new ImageDef();
                img.imageFilePath = panelTextureImageSelect.getFilePath();
                return img;
            default:
                String error = String.format("TextureSource %s is not supported!", ts);
                throw new ConfigValidationException(error, error);
        }
    }

    protected HeaderDef parseConfigFormHeader() {
        HeaderDef header = new HeaderDef();
        header.buildingSet = (BuildingSet) comboBoxBuildingSet.getSelectedItem();
        return header;
    }

    protected boolean isConfigValid(Config c) {
        return ValidatorUtils.validate(c).isEmpty();
    }
    
    protected void handleConstraintViolations(List<ConstraintViolation> cvs) {
        ConstraintViolationReportWindow report = new ConstraintViolationReportWindow(this, cvs);
        
        Event resetConfigEvent = new Event();
        resetConfigEvent.addObserver(resetConfigObserver);
        report.setResetConfigEvent(resetConfigEvent);
        
        report.setVisible(true);
    }
    
    protected Observer resetConfigObserver = new Observer() {
        @Override
        public void update() {
            setConfig(Config.getMinimalConfig());
        }
    };

    /**
     * Loads the last configuration that successfully rendered a map
     * If the config does not exist or fails loading, the default
     * settings are put in place...
     */
    protected void loadLastConfig() {
    	File last = new File(ResourceLoader.getAppDir(), "config-last.xml");
    	if(last.exists()) {
    		try {
				Config conf = Serializer.deserialize(Config.class, last, true);
				if(isConfigValid(conf)) {
				    setConfig(conf);
					log.debug("The last used config has been successfully loaded");
				} else {
				    log.warn("The last used config seems to be invalid, falling back to defaults");
				    log.debug(ValidatorUtils.formatCausesRecursively(ValidatorUtils.validateR(conf)));
				}
			} catch (Exception e) {
				log.error("Could not open the last used config (note: this is expected after a major upgrade)", e);
			}
    	} else {
    		setConfig(Config.getMinimalConfig());
    	}
    }

    protected void updateCenterInMap(final Config conf) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                Area ar = Area.of(conf.getBoundsTrans());
                Coordinate center = ar.getCenter();
                selection.updateCenter(center.getLatitude(), center.getLongitude());
                jxm.setCenterPosition(new GeoPosition(center.getLatitude(), center.getLongitude()));
            }
        });
    }

    // popups and error handling

    /**
     *
     * @param message the message to format
     * @return
     */
    protected static String getFormattedErrorMsg(String message) {
		return String.format("<html><center>%s</center></html>", message);
	}

	protected void popupError(String message) {
	    JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
	}
	
	protected void popupErrorConfig(String message) {
        JOptionPane.showMessageDialog(this, "<html><center>" + message
                + "<br>Note: Using the menu option File -> New, you can create "
                + "a clean configuration.</center></html>", "Configuration Error",
                JOptionPane.ERROR_MESSAGE);
    }

	// enums for combo boxes / tabs

    protected enum Tab {
        Settings("Settings", 0),
        XML("XML", 1);

        public final String name;
        public final int index;

        Tab(String name, int index) {
            this.name = name;
            this.index = index;
        }

        public int getIndex() {
            return index;
        }

        public static Tab byIndex(int index) {
            return Tab.values()[index];
        }

        @Override
        public String toString() {
            return name;
        }
    }

    protected enum ReliefSource {
        None("None", 0, ReliefDef.ReliefDefNone.class),
        Planar("Planar Relief", 1, PlanarReliefDef.class),
        SRTM("Real-World (SRTM / Download)", 2, SrtmDef.class),
        Heightmap("File (Grayscale-Heightmap)", 3, HeightmapDef.class);

        public final String name;
        public final int index;
        public final Class<? extends ReliefDef> configMatch;

        ReliefSource(String name, int index, Class<? extends ReliefDef> configMatch) {
            this.name = name;
            this.index = index;
            this.configMatch = configMatch;
        }

        public int getIndex() {
            return index;
        }

        public static Tab byIndex(int index) {
            return Tab.values()[index];
        }

        @Override
        public String toString() {
            return name;
        }

        @SuppressWarnings("rawtypes")
        public static ReliefSource of(Class clazz) throws ClassNotFoundException {
            // find exact match
            for (ReliefSource r : ReliefSource.values()) {
                if(r.configMatch.equals(clazz)) {
                    return r;
                }
            }
            // include matching subclasses
            for (ReliefSource r : ReliefSource.values()) {
                if(r.configMatch.isAssignableFrom(clazz)) {
                    return r;
                }
            }

            log.warn("Class {} is not a known relief source.", clazz);
            return None;
        }
    }

    protected enum TextureSource {
        None("None", 0, TextureDef.TextureDefNone.class),
        Single("Single Texture", 1, SingleTextureDef.class),
        OsmDownload("OpenStreetMap (Download)", 2, OsmDef.class),
        OsmFile("OpenStreetMap (XML File)", 3, OsmFileDef.class),
        Image("Custom Image (File)", 4, ImageDef.class);

        public final String name;
        public final int index;
        public final Class<? extends TextureDef> configMatch;

        TextureSource(String name, int index, Class<? extends TextureDef> configMatch) {
            this.name = name;
            this.index = index;
            this.configMatch = configMatch;
        }

        public int getIndex() {
            return index;
        }

        public static Tab byIndex(int index) {
            return Tab.values()[index];
        }

        @Override
        public String toString() {
            return name;
        }

        @SuppressWarnings("rawtypes")
        public static TextureSource of(Class clazz) throws ClassNotFoundException {
            // find exact match
            for (TextureSource ts : TextureSource.values()) {
                if(ts.configMatch.equals(clazz)) {
                    return ts;
                }
            }
            // include matching subclasses
            for (TextureSource ts : TextureSource.values()) {
                if(ts.configMatch.isAssignableFrom(clazz)) {
                    return ts;
                }
            }

            log.warn("Class {} is not a known relief source.", clazz);
            return None;
        }
    }

	/**
	 * Defines how detailed the textures shall be. For large maps, textures
	 * with lower detail are mandatory, as the servers where the source data
	 * comes from won't be able to deal with the requests otherwise.
	 */
    public enum TextureDetail {
    	/** no texture will be drawn */
    	off("off", Integer.MAX_VALUE),

    	min("lowest (<150)", 150),

    	low("low (<100)", 100),
    	/** only main routes and waters are drawn. best for huge maps */
    	med("medium (<60)", 60),
    	/** most of the large roads and areas are drawn */
    	high("high (<40)", 40),
    	/** all roads, buildings and areas are drawn. Use this only for small (1:1) maps */
    	ultra("ultra (<24)", 24);

    	public final double maxAllowedSize;
    	public final String display;

        private TextureDetail(String s, int size) {
            this.display = s;
            this.maxAllowedSize = size;
        }

        @Override
        public String toString() {
            return display;
        }

        public boolean isSizeAllowed(double size) {
        	return size <= maxAllowedSize;
        }

        public static TextureDetail byIndex(int i) {
        	return TextureDetail.values()[i];
        }

        /**
         * Retrieves the highest allowed detail level for the specified
         * extent (in km)
         * @param extent the extent of the map in km
         * @return the highes allowed detail for this size
         */
        public static TextureDetail highestForExtent(double extent) {
        	TextureDetail[] values = TextureDetail.values();
        	for (int i = values.length-1; i >= 0; i--) {
				if(values[i].isSizeAllowed(extent)) {
					return values[i];
				}
			}
        	return TextureDetail.off;
        }
    }

    // statics

    public static void updateCheck(Window owner) {
        UpdateCheckTask update = new UpdateCheckTask(true, owner);
        Thread t = new Thread(update, "UpdateCheck");
        t.start();
    }

    public static void openWeb(URI uri) {
        if (Desktop.isDesktopSupported()) {
            Desktop desktop = Desktop.getDesktop();
            try {
                desktop.browse(uri);
            } catch (IOException ex) {
                log.error("Could not open URL: the user default browser is not found, or it fails to be launched, or the default handler application failed to be launched", ex);
            }
        } else {
            JOptionPane.showMessageDialog(null, String.format("Your java runtime does not seem to support the opening of weblinks.\n"
                    + "You can open the link manually though:\n%s", uri.toString()), "Unable to open weblink", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void openWeb(URL url) {
        try {
            openWeb(url.toURI());
        } catch (URISyntaxException ex) {
            log.error("this URL is not formatted strictly according to RFC2396 and cannot be converted to a URI", ex);
        }
    }

    public static void openWeb(String uri) {
        try {
            openWeb(new URI(uri));
        } catch (URISyntaxException ex) {
            log.error("the given string violates RFC 2396, therefore cannot be converted to a URI object", ex);
        }
    }
    
    public static void openAttributionPage() {
        openWeb(attributionURL);
    }
    
}
