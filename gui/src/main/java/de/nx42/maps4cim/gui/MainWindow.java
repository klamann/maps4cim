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
package de.nx42.maps4cim.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.bind.JAXBException;

import org.jdesktop.swingx.JXMapViewer;
import org.jdesktop.swingx.mapviewer.GeoPosition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.nx42.maps4cim.Launcher;
import de.nx42.maps4cim.ResourceLoader;
import de.nx42.maps4cim.config.Config;
import de.nx42.maps4cim.config.ReliefDef;
import de.nx42.maps4cim.config.bounds.CenterDef;
import de.nx42.maps4cim.config.bounds.CenterDef.Unit;
import de.nx42.maps4cim.gui.action.CenterOnClickListener;
import de.nx42.maps4cim.gui.action.SelectionAdapter;
import de.nx42.maps4cim.util.Serializer;
import de.nx42.maps4cim.util.gis.Area;
import de.nx42.maps4cim.util.gis.Coordinate;

public class MainWindow extends JFrame {

	private static final Logger log = LoggerFactory.getLogger(MainWindow.class);
	private static final long serialVersionUID = -5741327205041428356L;

	private JTabbedPane tabs;

	private JTextField inputLat;
	private JTextField inputLon;
	private JSlider sliderExt;
	private JTextField inputExtent;
	private JButton btnResetExtent;
	private JCheckBox chckbxHeightOffsetAuto;
	private JTextField inputHeightOffset;
	private JTextField inputHeightScale;
	private JComboBox comboTextureDetail;
	private JEditorPane xmlEditor;
	private JCheckBox chckbxReliefEnabled;
	private JCheckBox chckbxTextureEnabled;
	private final JXMapViewer jxm;

	private boolean reliefEnabled = true;
	private boolean textureEnabled = true;
	private boolean heightOffsetAuto = true;
	protected Config config = null;
	
	protected SelectionAdapter selection;
	protected Tab currentTab = Tab.Settings;


	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		Launcher.initLogger();

		EventQueue.invokeLater(new Runnable() {
			@Override
            public void run() {
				try {
					MainWindow frame = new MainWindow();
					frame.setVisible(true);
				} catch (Exception e) {
					log.error("Uncaught exeption", e);
				}
			}
		});
	}


	/**
	 * Create the frame.
	 */
	public MainWindow() {
		super("maps4cim - a real-world map generator for CiM 2");

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch(Exception e) {
			log.error("Could not set look and feel to preferred value...");
		}

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize();


		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);

		JMenuItem mntmOpenConfiguration = new JMenuItem("Open Configuration...");
		mntmOpenConfiguration.addActionListener(menuOpenAction);
		mnFile.add(mntmOpenConfiguration);

		JMenuItem mntmSaveConfiguration = new JMenuItem("Save Configuration...");
		mntmSaveConfiguration.addActionListener(menuSaveAction);
		mnFile.add(mntmSaveConfiguration);

		JSeparator separator = new JSeparator();
		mnFile.add(separator);

		JMenuItem mntmExit = new JMenuItem("Exit");
		mntmExit.addActionListener(menuExitAction);
		mnFile.add(mntmExit);

		JMenu mnHelp = new JMenu("Help");
		menuBar.add(mnHelp);

		JMenuItem mntmAbout = new JMenuItem("About");
		mntmAbout.addActionListener(menuAboutAction);
		mnHelp.add(mntmAbout);
		getContentPane().setLayout(new GridLayout(1, 0, 0, 0));

		JPanel wrapper = new JPanel();
		getContentPane().add(wrapper);

		MapViewerFactory fact = new MapViewerFactory();
		jxm = fact.getMapViewer();
		selection = fact.getSelectionAdapter();
		jxm.addMouseListener(new CenterOnClickListener(this, jxm));
		JPanel map = jxm;
//		JPanel map = new JPanel();

		tabs = new JTabbedPane(SwingConstants.TOP);

		JButton btnRender = new JButton("Render");
		btnRender.addActionListener(btnRenderAction);
		GroupLayout gl_wrapper = new GroupLayout(wrapper);
		gl_wrapper.setHorizontalGroup(
		    gl_wrapper.createParallelGroup(Alignment.TRAILING)
		        .addGroup(gl_wrapper.createSequentialGroup()
		            .addComponent(map, GroupLayout.DEFAULT_SIZE, 664, Short.MAX_VALUE)
		            .addPreferredGap(ComponentPlacement.UNRELATED)
		            .addGroup(gl_wrapper.createParallelGroup(Alignment.TRAILING, false)
		                .addComponent(btnRender, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
		                .addComponent(tabs, GroupLayout.DEFAULT_SIZE, 260, Short.MAX_VALUE))
		            .addContainerGap())
		);
		gl_wrapper.setVerticalGroup(
		    gl_wrapper.createParallelGroup(Alignment.TRAILING)
		        .addGroup(gl_wrapper.createSequentialGroup()
		            .addContainerGap()
		            .addComponent(tabs, GroupLayout.DEFAULT_SIZE, 531, Short.MAX_VALUE)
		            .addPreferredGap(ComponentPlacement.RELATED)
		            .addComponent(btnRender)
		            .addContainerGap())
		        .addComponent(map, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 589, Short.MAX_VALUE)
		);


		JPanel tabSettings = new JPanel();
		tabSettings.setBorder(null);

		JScrollPane tabSettingsScroller = new JScrollPane(tabSettings);
		tabSettingsScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		tabSettingsScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		tabSettingsScroller.setBorder(null);

		tabs.addTab(Tab.Settings.name, null, tabSettingsScroller, null);

		sliderExt = new JSlider(SwingConstants.HORIZONTAL, 0, 32, 8);
		sliderExt.setToolTipText("select the extent of your map (best results are achieved by selecting 8km)");
		sliderExt.addChangeListener(extSliderChange);
		sliderExt.setSnapToTicks(true);
		sliderExt.setPaintLabels(true);
		sliderExt.setPaintTicks(true);
		sliderExt.setMinorTickSpacing(1);
		sliderExt.setMajorTickSpacing(4);

		JPanel center = new JPanel();
		FlowLayout fl_center = (FlowLayout) center.getLayout();
		fl_center.setAlignment(FlowLayout.LEADING);

		JLabel lblCenter = new JLabel("Center:");
		center.add(lblCenter);

		inputLat = new JTextField();
		inputLat.setToolTipText("latitude (decimal degrees)");
		inputLat.setText("48.0");
		inputLat.setColumns(6);
		inputLat.getDocument().addDocumentListener(latUpdate);
		center.add(inputLat);
		
		JPanel panel_relief = new JPanel();
		panel_relief.setBorder(new TitledBorder(null, "Relief", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		JPanel panel_texture = new JPanel();
		panel_texture.setBorder(new TitledBorder(null, "Texture", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		JPanel panel_hints = new JPanel();
		panel_hints.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Hints", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        
        JPanel extent = new JPanel();
        FlowLayout fl_extent = (FlowLayout) extent.getLayout();
        fl_extent.setAlignment(FlowLayout.LEADING);
        
        JLabel lblExtent = new JLabel("Extent:");
        extent.add(lblExtent);
        
        inputExtent = new JTextField();
        inputExtent.setToolTipText("The edge length of the map.");
        inputExtent.setHorizontalAlignment(SwingConstants.TRAILING);
        inputExtent.setText("8");
        inputExtent.setColumns(4);
        inputExtent.getDocument().addDocumentListener(inputExtentListener);
        extent.add(inputExtent);
        
        JLabel lblKm = new JLabel("km");
        extent.add(lblKm);

		GroupLayout gl_tabSettings = new GroupLayout(tabSettings);
		gl_tabSettings.setHorizontalGroup(
		    gl_tabSettings.createParallelGroup(Alignment.TRAILING)
		        .addGroup(gl_tabSettings.createSequentialGroup()
		            .addContainerGap()
		            .addGroup(gl_tabSettings.createParallelGroup(Alignment.TRAILING)
		                .addComponent(panel_hints, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 235, Short.MAX_VALUE)
		                .addComponent(sliderExt, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 235, Short.MAX_VALUE)
		                .addComponent(center, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 235, Short.MAX_VALUE)
		                .addComponent(extent, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 235, Short.MAX_VALUE)
		                .addComponent(panel_relief, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 235, Short.MAX_VALUE)
		                .addComponent(panel_texture, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 235, Short.MAX_VALUE))
		            .addContainerGap())
		);
		gl_tabSettings.setVerticalGroup(
		    gl_tabSettings.createParallelGroup(Alignment.LEADING)
		        .addGroup(gl_tabSettings.createSequentialGroup()
		            .addContainerGap()
		            .addComponent(center, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE)
		            .addPreferredGap(ComponentPlacement.UNRELATED)
		            .addComponent(sliderExt, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
		            .addComponent(extent, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
		            .addPreferredGap(ComponentPlacement.RELATED)
		            .addComponent(panel_relief, GroupLayout.PREFERRED_SIZE, 121, GroupLayout.PREFERRED_SIZE)
		            .addPreferredGap(ComponentPlacement.RELATED)
		            .addComponent(panel_texture, GroupLayout.PREFERRED_SIZE, 84, GroupLayout.PREFERRED_SIZE)
		            .addPreferredGap(ComponentPlacement.RELATED)
		            .addComponent(panel_hints, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
		            .addPreferredGap(ComponentPlacement.RELATED))
		);
		
		btnResetExtent = new JButton("reset");
		btnResetExtent.setToolTipText("<html>Resets the extent of the map to the ingame size of 8x8 km.<br>Note that true to scale results can only be achieved for maps of 8km size.</html>");
		btnResetExtent.addActionListener(btnResetExtentAction);
		extent.add(btnResetExtent);
		
		JTextPane textPaneHints = new JTextPane();
		textPaneHints.setText("Use the right mouse button to define the center of your map.\nClick and hold the left mouse button on the map to drag your current view.\nUse the mouse wheel to scroll.\nAdjust your map settings using the form above or the XML tab.\nSwitch to the XML tab to review or to copy & share your current settings.\nHave fun :)");
		textPaneHints.setFont(new Font("Dialog", Font.PLAIN, 11));
		textPaneHints.setEditable(false);
		textPaneHints.setBackground(SystemColor.menu);
		GroupLayout gl_panel_hints = new GroupLayout(panel_hints);
		gl_panel_hints.setHorizontalGroup(
		    gl_panel_hints.createParallelGroup(Alignment.LEADING)
		        .addComponent(textPaneHints, GroupLayout.DEFAULT_SIZE, 223, Short.MAX_VALUE)
		);
		gl_panel_hints.setVerticalGroup(
		    gl_panel_hints.createParallelGroup(Alignment.LEADING)
		        .addComponent(textPaneHints, GroupLayout.DEFAULT_SIZE, 213, Short.MAX_VALUE)
		);
		panel_hints.setLayout(gl_panel_hints);
		
		chckbxTextureEnabled = new JCheckBox("Enabled");
		chckbxTextureEnabled.setSelected(true);
		chckbxTextureEnabled.addActionListener(chckbxTextureEnabledAction);
		
				JPanel panel = new JPanel();
				FlowLayout flowLayout = (FlowLayout) panel.getLayout();
				flowLayout.setAlignment(FlowLayout.LEADING);
				
						JLabel lblTextureDetail = new JLabel("Texture detail:");
						panel.add(lblTextureDetail);
						
								comboTextureDetail = new JComboBox();
								comboTextureDetail.setToolTipText("The amount of detail that shall be drawn on the ground.");
								comboTextureDetail.setModel(new DefaultComboBoxModel(TextureDetail.values()));
								comboTextureDetail.setSelectedIndex(TextureDetail.values().length - 1);
								panel.add(comboTextureDetail);
		GroupLayout gl_panel_texture = new GroupLayout(panel_texture);
		gl_panel_texture.setHorizontalGroup(
		    gl_panel_texture.createParallelGroup(Alignment.LEADING)
		        .addGroup(gl_panel_texture.createSequentialGroup()
		            .addContainerGap()
		            .addGroup(gl_panel_texture.createParallelGroup(Alignment.LEADING)
		                .addComponent(panel, GroupLayout.DEFAULT_SIZE, 203, Short.MAX_VALUE)
		                .addComponent(chckbxTextureEnabled, GroupLayout.PREFERRED_SIZE, 63, GroupLayout.PREFERRED_SIZE))
		            .addContainerGap())
		);
		gl_panel_texture.setVerticalGroup(
		    gl_panel_texture.createParallelGroup(Alignment.LEADING)
		        .addGroup(gl_panel_texture.createSequentialGroup()
		            .addComponent(chckbxTextureEnabled)
		            .addComponent(panel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
		);
		panel_texture.setLayout(gl_panel_texture);
		
				JPanel heightOffset = new JPanel();
				FlowLayout fl_heightOffset = (FlowLayout) heightOffset.getLayout();
				fl_heightOffset.setAlignment(FlowLayout.LEADING);
				
						JLabel lblHeightOffset = new JLabel("Height offset:");
						heightOffset.add(lblHeightOffset);
						
								inputHeightOffset = new JTextField();
								inputHeightOffset.setToolTipText("custom height offset. The overall map height will be decreased by this value.");
								inputHeightOffset.setText("0");
								inputHeightOffset.setHorizontalAlignment(SwingConstants.TRAILING);
								inputHeightOffset.setEnabled(false);
								heightOffset.add(inputHeightOffset);
								inputHeightOffset.setColumns(4);
								
										JLabel lblMeter = new JLabel("m");
										heightOffset.add(lblMeter);
										
										        chckbxHeightOffsetAuto = new JCheckBox("auto");
										        chckbxHeightOffsetAuto.setToolTipText("Automatic height offset. Sets the lowest point of the map as new virtual zero height. Highly recommended.");
										        chckbxHeightOffsetAuto.addActionListener(heightOffsetCheckBoxAction);
										        chckbxHeightOffsetAuto.setSelected(true);
										        heightOffset.add(chckbxHeightOffsetAuto);
										        
										        chckbxReliefEnabled = new JCheckBox("Enabled");
										        chckbxReliefEnabled.setSelected(true);
										        chckbxReliefEnabled.addActionListener(chckbxReliefEnabledAction);
										        
										                JPanel heightScale = new JPanel();
										                FlowLayout fl_heightScale = (FlowLayout) heightScale.getLayout();
										                fl_heightScale.setAlignment(FlowLayout.LEADING);
										                
										                        JLabel lblHeightScale = new JLabel("Height scale:");
										                        heightScale.add(lblHeightScale);
										                        
										                                inputHeightScale = new JTextField();
										                                inputHeightScale.setToolTipText("Scaling of height differences. For values below 100, hills will be flatter, for high values, hills will be exaggerated.");
										                                inputHeightScale.setHorizontalAlignment(SwingConstants.TRAILING);
										                                inputHeightScale.setText("100");
										                                inputHeightScale.setColumns(4);
										                                heightScale.add(inputHeightScale);
										                                
										                                        JLabel lblPercent = new JLabel("%");
										                                        heightScale.add(lblPercent);
										        GroupLayout gl_panel_relief = new GroupLayout(panel_relief);
										        gl_panel_relief.setHorizontalGroup(
										            gl_panel_relief.createParallelGroup(Alignment.LEADING)
										                .addGroup(gl_panel_relief.createSequentialGroup()
										                    .addContainerGap()
										                    .addGroup(gl_panel_relief.createParallelGroup(Alignment.LEADING)
										                        .addComponent(chckbxReliefEnabled)
										                        .addComponent(heightOffset, GroupLayout.DEFAULT_SIZE, 203, Short.MAX_VALUE)
										                        .addComponent(heightScale, GroupLayout.DEFAULT_SIZE, 203, Short.MAX_VALUE))
										                    .addContainerGap())
										        );
										        gl_panel_relief.setVerticalGroup(
										            gl_panel_relief.createParallelGroup(Alignment.LEADING)
										                .addGroup(gl_panel_relief.createSequentialGroup()
										                    .addComponent(chckbxReliefEnabled)
										                    .addComponent(heightOffset, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
										                    .addComponent(heightScale, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
										        );
										        panel_relief.setLayout(gl_panel_relief);

		JLabel lblComma = new JLabel(",");
		center.add(lblComma);

		inputLon = new JTextField();
		inputLon.setToolTipText("longitude (decimal degrees)");
		inputLon.setText("11.0");
		inputLon.setColumns(6);
		inputLon.getDocument().addDocumentListener(lonUpdate);
		center.add(inputLon);
		tabSettings.setLayout(gl_tabSettings);

		JPanel tabXML = new JPanel();

		tabs.addTab(Tab.XML.name, null, tabXML, null);
		tabXML.setLayout(new BorderLayout(0, 0));

        xmlEditor = new JEditorPane();
        xmlEditor.setFont(new Font("Courier New", Font.PLAIN, 11));
        xmlEditor.setText("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");

        JScrollPane scrollPane = new JScrollPane(xmlEditor);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(null);
        tabXML.add(scrollPane, BorderLayout.CENTER);

		wrapper.setLayout(gl_wrapper);


		// listeners last
		tabSettingsScroller.addComponentListener(formTabOpened);
		tabXML.addComponentListener(xmlTabOpened);

		initFileChoosers();
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
	    	width = (int) (height * 1.8);
	    } else if(width / height < 0.7) {	// rotated widescreen (or unusually high)
	    	height = width;
	    	width = (int) (width * 1.5);
	    }
	    setBounds(100, 100, (int) (width * 0.5), (int) (height * 0.6));
        setMinimumSize(new Dimension(350, 200));
//
//        // TODO remove
//        setBounds(100, 100, 800, 800);
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
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Update the contents of the settings tab (form fields)
	 */
	protected void updateViewForm() {
		// this is far from perfect, but so is this whole gui app, so...

		// bounds & texture detail (depend on center def)
		if(config.bounds instanceof CenterDef) {
			CenterDef bounds = (CenterDef) config.bounds;
			inputLat.setText(String.valueOf(bounds.centerLat));
			inputLon.setText(String.valueOf(bounds.centerLon));
			inputExtent.setText(String.valueOf(bounds.extent));

			comboTextureDetail.setSelectedItem(TextureDetail.highestForExtent(bounds.extent));
		}

		// height offset
		boolean hoAuto = config.relief.isHeightOffsetAuto();
		setHeightOffsetState(hoAuto);
		if(!hoAuto) {
			inputHeightOffset.setText(config.relief.heightOffset);
		}

		// height scale
		double scalePercent = config.relief.heightScale == null ? 100 : config.relief.heightScale * 100;
		inputHeightScale.setText(String.valueOf((int) Math.round(scalePercent)));
	}

	protected void setHeightOffsetState(boolean auto) {
		chckbxHeightOffsetAuto.setSelected(auto);
		inputHeightOffset.setEnabled(!auto);
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

	protected ActionListener menuAboutAction = new ActionListener() {
    	@Override
		public void actionPerformed(ActionEvent e) {
    		AboutWindow aw = new AboutWindow();
    		aw.setVisible(true);
		}
	};

	// main window actions

	protected ActionListener btnRenderAction = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if(validateInput()) {
            	updateConfig();
            	openRenderWindow(config);
            }
        }
    };

    protected ChangeListener extSliderChange = new ChangeListener() {
        @Override
        public void stateChanged(ChangeEvent e) {
        	String sliderVal = String.valueOf(sliderExt.getValue());
        	String inputVal = inputExtent.getText();
        	if(!sliderVal.equals(inputVal)) {
        		inputExtent.setText(sliderVal);
        	}
        }
    };
    
    protected ActionListener btnResetExtentAction = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if(!inputExtent.getText().equals("8.0")) {
                inputExtent.setText("8.0");
            }
        }
    };

    protected ActionListener heightOffsetCheckBoxAction = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
        	heightOffsetAuto = chckbxHeightOffsetAuto.isSelected();
        	inputHeightOffset.setEnabled(!heightOffsetAuto);
        }
    };
    
    protected ActionListener chckbxReliefEnabledAction = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            reliefEnabled = chckbxReliefEnabled.isSelected();
            inputHeightOffset.setEnabled(reliefEnabled);
            inputHeightScale.setEnabled(reliefEnabled);
            chckbxHeightOffsetAuto.setEnabled(reliefEnabled);
            if(reliefEnabled) {
                inputHeightOffset.setEnabled(!heightOffsetAuto);
            }
        }
    };
    
    protected ActionListener chckbxTextureEnabledAction = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            textureEnabled = chckbxTextureEnabled.isSelected();
            comboTextureDetail.setEnabled(textureEnabled);
        }
    };

    protected DocumentListener inputExtentListener = new DocumentListener() {
		@Override
		public void removeUpdate(DocumentEvent e) { /* ignore */ }
		@Override
		public void changedUpdate(DocumentEvent e) { /* ignore */ }
		@Override
		public void insertUpdate(DocumentEvent e) {
			String input = inputExtent.getText();
			try {
				final int val = (int) Math.round(Double.parseDouble(input));
				if(val != sliderExt.getValue()) {
					if(val < 32) {
						EventQueue.invokeLater(new Runnable() {
							@Override
                            public void run() {
								sliderExt.setValue(val);
							}
						});
					} else if(val > 32 && sliderExt.getValue() < 32) {
						EventQueue.invokeLater(new Runnable() {
							@Override
                            public void run() {
								sliderExt.setValue(32);
								inputExtent.setText(String.valueOf(val));
							}
						});
					}
				}
				selection.updateExtent(val);
			} catch(NumberFormatException ex) {
				log.debug("Cannot parse \"{}\" as decimal number", input);
			}
		}
	};

	protected DocumentListener latUpdate = new DocumentListener() {
		@Override
		public void removeUpdate(DocumentEvent e) { /* ignore */ }
		@Override
		public void changedUpdate(DocumentEvent e) { /* ignore */ }
		@Override
		public void insertUpdate(DocumentEvent e) {
			updateCenter();
		}
	};

	protected DocumentListener lonUpdate = new DocumentListener() {
		@Override
		public void removeUpdate(DocumentEvent e) { /* ignore */ }
		@Override
		public void changedUpdate(DocumentEvent e) { /* ignore */ }
		@Override
		public void insertUpdate(DocumentEvent e) {
			updateCenter();
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

			if(validateFormInput()) {
				updateConfig();
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

			if(validateXmlInput()) {
				updateConfig();
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

	protected void initFileChoosers() {

		FileNameExtensionFilter xmlFilter = new FileNameExtensionFilter("XML", "xml");
		FileNameExtensionFilter mapFilter = new FileNameExtensionFilter("CiM 2 Map", "map");

		saveConfig = setupFileChooser("Save configuration", JFileChooser.SAVE_DIALOG, xmlFilter, new File("config.xml"));
		loadConfig = setupFileChooser("Load configuration", JFileChooser.OPEN_DIALOG, xmlFilter, new File("config.xml"));
		saveMap    = setupFileChooser("Save generated map", JFileChooser.SAVE_DIALOG, mapFilter, new File("maps4cim.map"));
		if(System.getProperty("os.name").startsWith("Windows")) {
		    File mapDir = new File(ResourceLoader.appdata.getParentFile().getParentFile(), "LocalLow/Colossal Order/Cities in Motion 2/Maps");
            saveMap    = setupFileChooser("Save generated map", JFileChooser.SAVE_DIALOG, mapFilter, new File(mapDir, "maps4cim.map"));
        } else {
            saveMap    = setupFileChooser("Save generated map", JFileChooser.SAVE_DIALOG, mapFilter, new File("maps4cim.map"));
        }

	}

	protected static JFileChooser setupFileChooser(String title, int type, FileFilter filter, File selected) {
		JFileChooser choose = new JFileChooser();
		choose.setDialogTitle(title);
		choose.setDialogType(type);
		choose.setFileFilter(filter);
		choose.setSelectedFile(selected);
		choose.setFileSelectionMode(JFileChooser.FILES_ONLY);
		choose.setMultiSelectionEnabled(false);
		return choose;
	}

	/**
	 * Safes the current configuration. Updates all config values, before
	 * actually writing to file. If the config contains errors, the
	 * user gets warned
	 */
	protected void safeConfig() {
		if(validateInput()) {
			updateConfig();
			if (saveConfig.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
	            File f = saveConfig.getSelectedFile();
	            try {
					Serializer.serialize(Config.class, config, f);
				} catch (JAXBException e) {
					log.error("Could not serialize config to file", e);
					Throwable linked = e.getLinkedException();
					errorPopup("Could not save the current configuration:<br>" + linked.getMessage());
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
				config = Serializer.deserialize(Config.class, f, true);
				updateViewForm();
				updateViewXml();
			} catch (JAXBException e) {
				Throwable linked = e.getLinkedException();
				log.error("Could not open config", e);
				errorPopup("Could not open config:<br>" + linked.getMessage());
			} catch (Exception e) {
				log.error("Could not open config", e);
				errorPopup("Could not open config:<br>" + e.getMessage());
			}
        }
	}

	/**
	 * Opens the render window and starts to build the map, based on the
	 * selected user configuration. First, a file selector will appear, so
	 * the user may choose where to store the resulting map
	 * @param conf the configuration from which the map is generated
	 */
	protected void openRenderWindow(final Config conf) {
		if (validateInput()) {
			updateConfig();
			if (saveMap.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
				final File file = saveMap.getSelectedFile();
				try {
					file.createNewFile();
					if (file.exists() && file.canWrite())
						EventQueue.invokeLater(new Runnable() {
							@Override
                            public void run() {
								try {
									RenderWindow frame = new RenderWindow();
									frame.setVisible(true);
									frame.runMapGenerator(conf, file);
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						});
				} catch (IOException e) {
					log.error( "Cannot write the resulting map to the specified file", e);
					errorPopup("Cannot write the resulting map to "
							+ file.toString() + "<br>" + e.getMessage());
				}
			}
		}
	}

	protected static DecimalFormat getDecimalFormat(int fractionDigits) {
		NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
		nf.setMaximumFractionDigits(fractionDigits);
		return (DecimalFormat) nf;
	}

	protected static final DecimalFormat df = getDecimalFormat(4);

	/**
	 * Sets the center form field to the specified values
	 * @param lat the latitude to set
	 * @param lon the longitude to set
	 */
	public void setCenter(final double lat, final double lon) {
		EventQueue.invokeLater(new Runnable() {
			@Override
            public void run() {
				inputLat.setText(df.format(lat));
				inputLon.setText(df.format(lon));
			}
		});
	}

	// application logic

	/**
	 * Validates the user input, either the forms of the settings tab or
	 * the contents of the XML tab
	 * @return true, if all settings are valid
	 */
	protected boolean validateInput() {
		if(currentTab == Tab.Settings) {
			return validateFormInput();
		} else {
			return validateXmlInput();
		}
	}

	/**
	 * Validates the form fields of the settings tab
	 * @return true, if the values in the form fields are valid
	 */
    protected boolean validateFormInput() {
        try {
        	// location
            double lat = parseText(inputLat, "latitude (center)");
            double lon = parseText(inputLon, "longitude (center)");
            double extent = parseText(inputExtent, "extent");
            
            // relief
            double heightOffset = 0;
            double heightScale = 100;
            if(reliefEnabled) {
                if(!heightOffsetAuto) {
                	heightOffset = Math.abs(parseText(inputHeightOffset, "height offset"));
                }
                heightScale = parseText(inputHeightScale, "height scale");
            }
            
            // texture
            TextureDetail td = TextureDetail.off;
            if(textureEnabled) {
                td = TextureDetail.byIndex(comboTextureDetail.getSelectedIndex());
            }
            
            // validate
            return validateInput(lat, lon, extent, heightOffsetAuto, heightOffset, heightScale, td);
        } catch(Exception e) {
        	log.error("An error occured while parsing form details", e);
            return false;
        }
    }

    /**
     * Validates the document on the XML tab
     * @return true, if the xml can be parsed and the most important contents
     * are reasonable
     */
	protected boolean validateXmlInput() {
		try {
			// parse xml & validate
			Config conf = Serializer.deserializeFromString(Config.class, xmlEditor.getText());
			return validateInput(conf);
		} catch (JAXBException e) {
			log.error("The user defined XML seems to be invalid", e);
			Throwable linked = e.getLinkedException();
			errorPopup("Your XML seems to be invalid: " + linked.getMessage());
			return false;
		}
	}

	/**
	 * Validates the most important fields of the specified configuration
	 * @param conf the configuration object to validate
	 * @return true, if the most important contents are reasonable
	 */
    protected boolean validateInput(Config conf) {
    	if(conf.bounds instanceof CenterDef) {
			CenterDef bounds = (CenterDef) conf.bounds;
			return validateInput(
					bounds.centerLat,
					bounds.centerLon,
					bounds.extent,
					conf.relief.isHeightOffsetAuto(),
					conf.relief.getHeightOffset(),
					conf.relief.getHeightScale(),
					TextureDetail.off);
		}
    	// default to true for non-center input def
		return true;
	}

    /**
     * Validates the specified fields. Shows a popup to the user with the first
     * error that has been found.
     * @param lat the latitude (center) - must be between -90 and 90
     * @param lon the longitude (center) - must be between -180 and 180
     * @param extent the extent of the map - must be >=1 and <=150
     * @param hoAuto flag that indicates if the height ofsset is calculated automatically
     * @param ho the height offset, must be between 0 and 9000 (if hoAuto is disabled)
     * @param hs the height scale, must be between 0 and 2000%
     * @param td the texture detail, must not exceed the allowed amount of detail for the
     *           specified map size
     * @return true, if all fields are valid
     */
    protected boolean validateInput(double lat, double lon, double extent, boolean hoAuto, double ho, double hs, TextureDetail td) {
        if(lat > 90 || lat < -90) {
            return errorPopup("Latitude", "must be between -90 and 90 degrees");
        }
        if(lon > 180 || lon < -180) {
        	return errorPopup("Longitude", "must be between -180 and 180 degrees");
        }
        if(extent < 1) {
        	return errorPopup("Extent", "the smallest allowed map size is 1 km.<br>" +
        			"Please note that only 8 km maps allow 1:1 scale of ingame objects");
        }
        if(extent > 250) {
        	return errorPopup("Extent", "the largest allowed map size is 250 km.<br>" +
        			"Please note that only 8 km maps allow 1:1 scale of ingame objects");
        }
        if(!hoAuto && ho > 9000) {
        	return errorPopup("Height offset", "You have entered a very high height offset value.<br>" +
        			"Please note that the height offset is measured in meters and that there is<br>" +
        			"no elevation on earth higher than 9000 m above sea level.<br><br>" +
        			"If you have no idea, what this message is about, please enable the checkbox labeled \"auto\"<br>" +
        			"next to the height offset and I will figure out the best value myself.");
        }
        if(hs < 0 || hs > 2000) {
        	return errorPopup("Height scale", "the height scale must be between<br>" +
        			"0% (no elevation data) and 2.000% (which would already render pretty insane hills).<br>" +
        			"Why don't you leave it somewhere around 50 to 150% (which would be reasonable)?");
        }
        if(!td.isSizeAllowed(extent)) {
        	TextureDetail tdAllowed = TextureDetail.highestForExtent(extent);
        	return errorPopup("Texture Detail", String.format("I'm sorry, but your selected texture detail \"%s\"<br>" +
        			"is not available for a map of %s km. Decrease the texture detail to \"%s\"<br>" +
        			"or decrease the size of your map to %s km or less.<br><br>" +
        			"Please be aware that retrieving the data for high detail maps of this size<br>" +
        			"requires enormous server capacities. These limits were introduced for a good reason.",
        			td, extent, tdAllowed, td.maxAllowedSize));
        }
        return true;
	}

    /**
     * Updates the configuration based on the current settings (depending on the
     * selected tab).
     * Check for validity first with {@link MainWindow#validateInput()},
     * or exeptions may be thrown...
     */
    protected void updateConfig() {
    	if(currentTab == Tab.Settings) {
			config = getConfigFromForm();
		} else {
			try {
				config = getConfigFromXML();
			} catch (JAXBException e) {
				log.error("Unecpected exception while updating configuration", e);
			}
		}
    }

    /**
     * Creates a new Configuration from the form fields of the settings tab.
     * Check for validity first with {@link MainWindow#validateFormInput()},
     * or exeptions may be thrown...
     * @return the new config based on the form details
     */
    protected Config getConfigFromForm() {
    	// location
    	double lat = Double.parseDouble(inputLat.getText());
    	double lon = Double.parseDouble(inputLon.getText());
    	double extent = Double.parseDouble(inputExtent.getText());
    	CenterDef bounds = CenterDef.of(lat, lon, extent, Unit.KM);
    	
    	// relief
    	ReliefDef relief;
    	if(reliefEnabled) {
    	    double heightOffset = 0;
            if(!heightOffsetAuto) {
                heightOffset = Math.abs(Double.parseDouble(inputHeightOffset.getText()));
            }
            double heightScale = Double.parseDouble(inputHeightScale.getText()) / 100d;
            relief = ReliefDef.srtm(heightOffset, heightOffsetAuto, heightScale);
    	} else {
    	    relief = ReliefDef.none();
    	}
    	
        // texture
        TextureDetail td;
        if(textureEnabled) {
            td = TextureDetail.byIndex(comboTextureDetail.getSelectedIndex());
        } else {
            td = TextureDetail.off;
        }
        
        // Build config
        Config c = new Config();
        c.bounds = bounds;
        c.relief = relief;
        c.texture = ConfigPresets.get(td);
        return c;
    }

    /**
     * Creates a new Configuration from the XML document in the xml tab.
     * Check for validity first with {@link MainWindow#validateXmlInput()},
     * or exeptions may be thrown...
     * @return the new config based on the user defined xml
     * @throws JAXBException if anything goes wrong (srsly, validate first!)
     */
    protected Config getConfigFromXML() throws JAXBException {
    	return Serializer.deserializeFromString(Config.class, xmlEditor.getText());
    }

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
				if(validateInput(conf)) {
					this.config = conf;
					updateViewForm();
					updateViewXml();
					updateCenterInMap(conf);
					log.debug("The last used config has been successfully loaded");
				} else {
					errorPopup("You got this message because your last used config was messed up<br>" +
							"Loading a fresh config. Have fun!");
					log.warn("Last used config contained errors...");
				}
			} catch (Exception e) {
				log.error("Could not open last used config", e);
			}
    	}
    }

    protected void updateCenterInMap(final Config conf) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                Area ar = Area.of(conf.bounds);
                Coordinate center = ar.getCenter();
                selection.updateCenter(center.getLatitude(), center.getLongitude());
                jxm.setCenterPosition(new GeoPosition(center.getLatitude(), center.getLongitude()));
            }
        });
    }

    // getters

    public double getExtent() {
    	try {
    		return Double.parseDouble(inputExtent.getText());
    	} catch(NumberFormatException e) {
    		return 0;
    	}
    }

    // popups and error handling

    /**
     * Parses the contents of the specified field as double. Shows a popup to
     * the user, if the text cannot be parsed.
     * @param field the field to parse
     * @param fieldName the name of the field (for notification output)
     * @return the parsed double value
     * @throws NumberFormatException if the text input cannot be parsed
     */
    protected double parseText(JTextField field, String fieldName) {
        String value = field.getText();
        try {
            return Double.parseDouble(value);
        } catch(NumberFormatException e) {
            JOptionPane.showMessageDialog(null, String.format(
                    "<html><center>Your input \"%s\" from field %s is not a well-formatted decimal number.<br>Please enter something like 42 or 13.37</center></html>",
                    value, fieldName), "Error", JOptionPane.ERROR_MESSAGE);
            throw e;
        }
    }

    /**
     * Shows an error popup with the specified message for a field.
     * @param field the concerning field
     * @param error the error message
     * @return false - always, as errors are evil >;)
     */
    protected boolean errorPopup(String field, String error) {
        JOptionPane.showMessageDialog(this, String.format(
                "<html><center>%s has an invalid value: %s</center></html>",
                field, error), "Error", JOptionPane.ERROR_MESSAGE);
        return false;
    }

    /**
     * Shows a simple error popup with the specified centered text.
     * HTML formatting is supported by default.
     * @param message the message to print
     */
	protected void errorPopup(String message) {
		JOptionPane.showMessageDialog(this,
				String.format("<html><center>%s</center></html>", message),
				"Error", JOptionPane.ERROR_MESSAGE);
	}

	// enums for combo boxes / tabs

	/**
	 * Defines how detailed the textures shall be. For large maps, textures
	 * with lower detail are mandatory, as the servers where the source data
	 * comes from won't be able to deal with the requests otherwise.
	 */
    public enum TextureDetail {
    	/** no texture will be drawn */
    	off("off", Integer.MAX_VALUE),

    	min("lowest (150)", 150),

    	low("low (100)", 100),
    	/** only main routes and waters are drawn. best for huge maps */
    	med("medium (60)", 60),
    	/** most of the large roads and areas are drawn */
    	high("high (35)", 35),
    	/** most of the roads and additional details are drawn */
    	vhigh("very high (20)", 20),
    	/** all roads, buildings and areas are drawn. Use this only for small (1:1) maps */
    	ultra("ultra (10)", 10);

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
    }
}
