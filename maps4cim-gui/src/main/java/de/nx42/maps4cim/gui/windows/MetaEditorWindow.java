package de.nx42.maps4cim.gui.windows;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.TreeSet;
import java.util.prefs.Preferences;

import javax.imageio.ImageIO;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SpinnerDateModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.nx42.maps4cim.gui.MainWindow;
import de.nx42.maps4cim.gui.util.PrefsHelper;
import de.nx42.maps4cim.gui.util.TextAreaLogAppender;
import de.nx42.maps4cim.header.HeaderEditor;
import de.nx42.maps4cim.util.DateUtils;
import de.nx42.maps4cim.util.java2d.BitmapUtil;

public class MetaEditorWindow extends JDialog {

    private static final Logger log = LoggerFactory.getLogger(MetaEditorWindow.class);
    private static final long serialVersionUID = 2171600101047955620L;

    protected static final Date defaultDate = DateUtils.getDateUTC(2013, 1, 1, 12, 0, 0);

    protected int previewSize = 192;

    protected JTextField textFieldMapFilePath;
    protected JTextField textFieldMapName;

    protected JSpinner spinnerDateCreated;
    protected JSpinner spinnerDateLastModified;
    protected JSpinner spinnerDateUnused1;
    protected JSpinner spinnerDateUnused2;
    protected JSpinner spinnerLongWorkHours1;
    protected JSpinner spinnerLongWorkHours2;

    protected JLabel lblCurrentlyLoadedMapName;
    protected JLabel labelPreviewImage;

    protected FileNameExtensionFilter mapFilter = new FileNameExtensionFilter("CiM 2 Map", "map");
    protected FileNameExtensionFilter pngFilter = new FileNameExtensionFilter("Portable Network Graphic (.png)", "png");
    protected FileNameExtensionFilter imageFilter = getSupportedImageFileNameExtensionFilter();
    protected JFileChooser saveMap = MainWindow.setupFileChooser("Save map", JFileChooser.SAVE_DIALOG, mapFilter, new File("new-header.map"));
    protected JFileChooser loadMap = MainWindow.setupFileChooser("Load map", JFileChooser.OPEN_DIALOG, mapFilter, new File("my.map"));
    protected JFileChooser saveImage = MainWindow.setupFileChooser("Save preview image (as PNG)", JFileChooser.SAVE_DIALOG, pngFilter, new File("preview.png"));
    protected JFileChooser loadImage = MainWindow.setupFileChooser("Load preview image", JFileChooser.OPEN_DIALOG, imageFilter, new File("preview.png"));


    public MetaEditorWindow() {
        setTitle("maps4cim - Metadata Editor");
        setBounds(200, 200, 567, 548);
        setMinimumSize(new Dimension(350, 300));
        setModalityType(ModalityType.APPLICATION_MODAL);
        setLocationByPlatform(true);

        getContentPane().setLayout(new BorderLayout(0, 0));

        JPanel contentPanel = new JPanel();
        getContentPane().add(contentPanel, BorderLayout.CENTER);

        JLabel lblLoadMap = new JLabel("Load map:");

        textFieldMapFilePath = new JTextField();
        textFieldMapFilePath.setToolTipText("Path to the map to load");
        textFieldMapFilePath.setColumns(10);

        JButton btnBrowse = new JButton("Browse");
        btnBrowse.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openMap();
            }
        });
        btnBrowse.setToolTipText("Find a map using the file browser");

        JSeparator separator = new JSeparator();

        JLabel lblCurrentlyLoadedMap = new JLabel("Currently loaded map:");

        lblCurrentlyLoadedMapName = new JLabel("<<none>>");

        JButton btnLoad = new JButton("Load");
        btnLoad.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadMap(textFieldMapFilePath.getText());
            }
        });
        btnLoad.setToolTipText("Try to load the currently selected file");

        JScrollPane scrollPane = new JScrollPane();
        GroupLayout gl_contentPanel = new GroupLayout(contentPanel);
        gl_contentPanel.setHorizontalGroup(
            gl_contentPanel.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_contentPanel.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(gl_contentPanel.createParallelGroup(Alignment.TRAILING)
                        .addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 595, Short.MAX_VALUE)
                        .addComponent(separator, GroupLayout.DEFAULT_SIZE, 595, Short.MAX_VALUE)
                        .addGroup(gl_contentPanel.createSequentialGroup()
                            .addComponent(lblLoadMap)
                            .addPreferredGap(ComponentPlacement.UNRELATED)
                            .addComponent(textFieldMapFilePath, GroupLayout.DEFAULT_SIZE, 397, Short.MAX_VALUE)
                            .addPreferredGap(ComponentPlacement.UNRELATED)
                            .addComponent(btnBrowse)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(btnLoad))
                        .addGroup(Alignment.LEADING, gl_contentPanel.createSequentialGroup()
                            .addComponent(lblCurrentlyLoadedMap)
                            .addPreferredGap(ComponentPlacement.UNRELATED)
                            .addComponent(lblCurrentlyLoadedMapName)))
                    .addContainerGap())
        );
        gl_contentPanel.setVerticalGroup(
            gl_contentPanel.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_contentPanel.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(gl_contentPanel.createParallelGroup(Alignment.BASELINE)
                        .addComponent(lblLoadMap)
                        .addComponent(textFieldMapFilePath, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnLoad)
                        .addComponent(btnBrowse))
                    .addPreferredGap(ComponentPlacement.UNRELATED)
                    .addComponent(separator, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(ComponentPlacement.UNRELATED)
                    .addGroup(gl_contentPanel.createParallelGroup(Alignment.BASELINE)
                        .addComponent(lblCurrentlyLoadedMap)
                        .addComponent(lblCurrentlyLoadedMapName))
                    .addPreferredGap(ComponentPlacement.UNRELATED)
                    .addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 313, Short.MAX_VALUE)
                    .addContainerGap())
        );

        JPanel mapDataPanel = new JPanel();
        scrollPane.setViewportView(mapDataPanel);

                JPanel imageWrapperPanel = new JPanel();
                imageWrapperPanel.setToolTipText("This is the preview image of the map. Always a 256x256px PNG.");
                imageWrapperPanel.setBorder(new TitledBorder(null, "Preview Image", TitledBorder.LEADING, TitledBorder.TOP, null, null));

        JPanel headerFieldsPanel = new JPanel();

        JButton btnSelectPreviewImage = new JButton("Select New Image...");
        btnSelectPreviewImage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadPreview();
            }
        });

        JScrollPane scrollPaneLogOutput = new JScrollPane();

        JButton btnSaveImage = new JButton("Save Image...");
        btnSaveImage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                savePreview();
            }
        });
        GroupLayout gl_mapDataPanel = new GroupLayout(mapDataPanel);
        gl_mapDataPanel.setHorizontalGroup(
            gl_mapDataPanel.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_mapDataPanel.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(gl_mapDataPanel.createParallelGroup(Alignment.TRAILING, false)
                        .addComponent(scrollPaneLogOutput, Alignment.LEADING)
                        .addGroup(Alignment.LEADING, gl_mapDataPanel.createParallelGroup(Alignment.TRAILING)
                            .addGroup(gl_mapDataPanel.createSequentialGroup()
                                .addComponent(headerFieldsPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(ComponentPlacement.UNRELATED)
                                .addComponent(imageWrapperPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                            .addGroup(gl_mapDataPanel.createSequentialGroup()
                                .addComponent(btnSaveImage)
                                .addPreferredGap(ComponentPlacement.RELATED)
                                .addComponent(btnSelectPreviewImage)))))
        );
        gl_mapDataPanel.setVerticalGroup(
            gl_mapDataPanel.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_mapDataPanel.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(gl_mapDataPanel.createParallelGroup(Alignment.LEADING)
                        .addComponent(imageWrapperPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(headerFieldsPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(gl_mapDataPanel.createParallelGroup(Alignment.BASELINE)
                        .addComponent(btnSelectPreviewImage)
                        .addComponent(btnSaveImage))
                    .addGap(18)
                    .addComponent(scrollPaneLogOutput, GroupLayout.PREFERRED_SIZE, 76, GroupLayout.PREFERRED_SIZE))
        );

        JTextPane logOutput = new JTextPane();
        logOutput.setFont(UIManager.getFont("Button.font"));
        logOutput.setEditable(false);
        scrollPaneLogOutput.setViewportView(logOutput);

        labelPreviewImage = new JLabel("");
        GroupLayout gl_imageWrapperPanel = new GroupLayout(imageWrapperPanel);
        gl_imageWrapperPanel.setHorizontalGroup(
            gl_imageWrapperPanel.createParallelGroup(Alignment.LEADING)
                .addComponent(labelPreviewImage, GroupLayout.DEFAULT_SIZE, previewSize, Short.MAX_VALUE)
        );
        gl_imageWrapperPanel.setVerticalGroup(
            gl_imageWrapperPanel.createParallelGroup(Alignment.LEADING)
                .addComponent(labelPreviewImage, GroupLayout.DEFAULT_SIZE, previewSize, Short.MAX_VALUE)
        );
        imageWrapperPanel.setLayout(gl_imageWrapperPanel);

                JLabel lblName = new JLabel("Name");

                JLabel lblDateLastSaved = new JLabel("Date Last Modified");

                JLabel lblHoursWorkedA = new JLabel("Hours Worked (A)");

                JLabel lblHoursWorkedB = new JLabel("Hours Worked (B)");

                JLabel lblUnusedDate1 = new JLabel("Unused Date 1");

                JLabel lblDateCreated = new JLabel("Date Created");

                JLabel lblUnusedDate2 = new JLabel("Unused Date 2");

        textFieldMapName = new JTextField();
        textFieldMapName.setEnabled(false);
        textFieldMapName.setToolTipText("The name of the map. Should be the same as the filename.");
        textFieldMapName.setColumns(10);

        spinnerDateCreated = new JSpinner();
        spinnerDateCreated.setEnabled(false);
        spinnerDateCreated.setToolTipText("Date when the map was first created (in the map editor or using maps4cim)");
        spinnerDateCreated.setModel(new SpinnerDateModel(defaultDate, null, null, Calendar.DAY_OF_YEAR));

        spinnerDateLastModified = new JSpinner();
        spinnerDateLastModified.setEnabled(false);
        spinnerDateLastModified.setToolTipText("Date when the map was last modified (in the map editor)");
        spinnerDateLastModified.setModel(new SpinnerDateModel(defaultDate, null, null, Calendar.DAY_OF_YEAR));

        spinnerDateUnused1 = new JSpinner();
        spinnerDateUnused1.setEnabled(false);
        spinnerDateUnused1.setToolTipText("This is an unused date that is always set to 2013-04-01 12:00:00 UTC, if you save your map in the map editor");
        spinnerDateUnused1.setModel(new SpinnerDateModel(defaultDate, null, null, Calendar.DAY_OF_YEAR));

        spinnerDateUnused2 = new JSpinner();
        spinnerDateUnused2.setEnabled(false);
        spinnerDateUnused2.setToolTipText("This is another unused date that is always set to 2013-04-01 12:00:00 UTC, if you save your map in the map editor");
        spinnerDateUnused2.setModel(new SpinnerDateModel(defaultDate, null, null, Calendar.DAY_OF_YEAR));

        spinnerLongWorkHours1 = new JSpinner();
        spinnerLongWorkHours1.setEnabled(false);
        spinnerLongWorkHours1.setToolTipText("Total time worked on the map (if you know the difference between A and B, let me know!)");
        spinnerLongWorkHours1.setModel(new SpinnerNumberModel(0.0, 0.0, 876000000.0, 0.01));
        spinnerLongWorkHours1.setEditor(new JSpinner.NumberEditor(spinnerLongWorkHours1, "0.0##### h"));

        spinnerLongWorkHours2 = new JSpinner();
        spinnerLongWorkHours2.setEnabled(false);
        spinnerLongWorkHours2.setToolTipText("Total time worked on the map (if you know the difference between A and B, let me know!)");
        spinnerLongWorkHours2.setModel(new SpinnerNumberModel(0.0, 0.0, 876000000.0, 0.01));
        spinnerLongWorkHours2.setEditor(new JSpinner.NumberEditor(spinnerLongWorkHours2, "0.0##### h"));

        GroupLayout gl_headerFieldsPanel = new GroupLayout(headerFieldsPanel);
        gl_headerFieldsPanel.setHorizontalGroup(
            gl_headerFieldsPanel.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_headerFieldsPanel.createSequentialGroup()
                    .addGroup(gl_headerFieldsPanel.createParallelGroup(Alignment.LEADING)
                        .addComponent(lblHoursWorkedA)
                        .addComponent(lblHoursWorkedB)
                        .addComponent(lblDateLastSaved)
                        .addComponent(lblDateCreated)
                        .addComponent(lblName)
                        .addComponent(lblUnusedDate1)
                        .addComponent(lblUnusedDate2))
                    .addPreferredGap(ComponentPlacement.UNRELATED)
                    .addGroup(gl_headerFieldsPanel.createParallelGroup(Alignment.TRAILING)
                        .addComponent(textFieldMapName, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 154, Short.MAX_VALUE)
                        .addComponent(spinnerDateCreated, GroupLayout.DEFAULT_SIZE, 154, Short.MAX_VALUE)
                        .addComponent(spinnerDateLastModified, GroupLayout.DEFAULT_SIZE, 154, Short.MAX_VALUE)
                        .addComponent(spinnerDateUnused1, GroupLayout.DEFAULT_SIZE, 154, Short.MAX_VALUE)
                        .addComponent(spinnerDateUnused2, GroupLayout.DEFAULT_SIZE, 154, Short.MAX_VALUE)
                        .addComponent(spinnerLongWorkHours1, GroupLayout.DEFAULT_SIZE, 154, Short.MAX_VALUE)
                        .addComponent(spinnerLongWorkHours2, GroupLayout.DEFAULT_SIZE, 154, Short.MAX_VALUE)))
        );
        gl_headerFieldsPanel.setVerticalGroup(
            gl_headerFieldsPanel.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_headerFieldsPanel.createSequentialGroup()
                    .addGap(3)
                    .addGroup(gl_headerFieldsPanel.createParallelGroup(Alignment.BASELINE)
                        .addComponent(lblName)
                        .addComponent(textFieldMapName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addGap(10)
                    .addGroup(gl_headerFieldsPanel.createParallelGroup(Alignment.BASELINE)
                        .addComponent(lblDateCreated)
                        .addComponent(spinnerDateCreated, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addGap(9)
                    .addGroup(gl_headerFieldsPanel.createParallelGroup(Alignment.BASELINE)
                        .addComponent(lblDateLastSaved)
                        .addComponent(spinnerDateLastModified, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addGap(9)
                    .addGroup(gl_headerFieldsPanel.createParallelGroup(Alignment.BASELINE)
                        .addComponent(lblHoursWorkedA)
                        .addComponent(spinnerLongWorkHours1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addGap(9)
                    .addGroup(gl_headerFieldsPanel.createParallelGroup(Alignment.BASELINE)
                        .addComponent(lblHoursWorkedB)
                        .addComponent(spinnerLongWorkHours2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addGap(9)
                    .addGroup(gl_headerFieldsPanel.createParallelGroup(Alignment.BASELINE)
                        .addComponent(lblUnusedDate1)
                        .addComponent(spinnerDateUnused1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addGap(9)
                    .addGroup(gl_headerFieldsPanel.createParallelGroup(Alignment.BASELINE)
                        .addComponent(lblUnusedDate2)
                        .addComponent(spinnerDateUnused2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        headerFieldsPanel.setLayout(gl_headerFieldsPanel);
        mapDataPanel.setLayout(gl_mapDataPanel);
        contentPanel.setLayout(gl_contentPanel);

        JPanel buttonPanel = new JPanel();
        FlowLayout fl_buttonPanel = (FlowLayout) buttonPanel.getLayout();
        fl_buttonPanel.setAlignment(FlowLayout.RIGHT);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        JButton btnSaveAs = new JButton("Save As...");
        btnSaveAs.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveMap();
            }
        });
        buttonPanel.add(btnSaveAs);

        JButton btnCancel = new JButton("Cancel");
        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        buttonPanel.add(btnCancel);


        // Custom appendix
        TextAreaLogAppender tap = new TextAreaLogAppender(logOutput);
        org.apache.log4j.Logger.getRootLogger().addAppender(tap);
        restorePrefs();
    }



    protected void openMap() {
        if(lastLoaded != null) {
            loadMap.setSelectedFile(lastLoaded);
        }
        File userInput = new File(textFieldMapFilePath.getText());
        if(userInput.exists()) {
            loadMap.setSelectedFile(userInput);
        }

        if (loadMap.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File f = loadMap.getSelectedFile();
            lastLoaded = f;
            try {
                textFieldMapFilePath.setText(f.getCanonicalPath());
            } catch (IOException e) {
                log.warn("Could not write canocial path for the selected file", e);
                textFieldMapFilePath.setText(f.getPath());
            }
            loadMap(f.getAbsolutePath());
        }
    }

    protected void saveMap() {
        if(!mapLoaded) {
            log.warn("No map loaded, nothing to save!");
            return;
        }
        if(lastLoaded != null) {
            saveMap.setSelectedFile(lastLoaded);
        }

        updateFields();
        if (saveMap.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File f = saveMap.getSelectedFile();
            lastLoaded = f;
            try {
                editor.writeChanges(f);
                log.info("Map with new header was successfully written to {}", f);
            } catch (IOException e) {
                log.error("Error writing new header", e);
            }
        }
    }

    protected void savePreview() {
        if(!mapLoaded) {
            log.warn("Can't save preview image: No map loaded!");
            return;
        }

        if(lastPreview != null) {
            saveImage.setSelectedFile(lastPreview);
        }
        if (saveImage.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File f = saveImage.getSelectedFile();
            lastPreview = f;
            try {
                ImageIO.write(editor.getPreviewImage(), "png", f);
            } catch (IOException e) {
                log.error("Could not save the preview image!", e);
            }
        }
    }

    protected void loadPreview() {
        if(!mapLoaded) {
            log.warn("Can't select a new preview image: No map loaded!");
            return;
        }

        if(lastPreview != null) {
            loadImage.setSelectedFile(lastPreview);
        }
        if (loadImage.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File f = loadImage.getSelectedFile();
            lastPreview = f;
            try {
                BufferedImage img = ImageIO.read(f);
                updatePreviewImage(img);
            } catch (IOException e) {
                log.error("Could not read the selected image!", e);
            }
        }
    }






    protected HeaderEditor editor;

    protected boolean mapLoaded = false;
    protected BufferedImage previewInternal;

    protected File lastLoaded;
    protected File lastPreview;
    protected int maxImgSize = 512;


    protected void loadMap(String path) {
        try {
            editor = new HeaderEditor(path);

            // write contents to form fields
            this.textFieldMapName.setText(editor.getMapName());
            this.lblCurrentlyLoadedMapName.setText(editor.getMapName());
            this.spinnerDateCreated.setValue(editor.getMapCreated());
            this.spinnerDateLastModified.setValue(editor.getLastSaved());
            this.spinnerDateUnused1.setValue(editor.getUnusedDate1());
            this.spinnerDateUnused2.setValue(editor.getUnusedDate2());
            this.spinnerLongWorkHours1.setValue(editor.getWorkHours1());
            this.spinnerLongWorkHours2.setValue(editor.getWorkHours2());

            // store preview image and resize for display
            this.previewInternal = editor.getPreviewImage();
            labelPreviewImage.setIcon(new ImageIcon(BitmapUtil.resize(previewInternal, previewSize)));

            // enable fields, if necessary
            if (!mapLoaded) {
                setFieldsEnabled(true);
                mapLoaded = true;
            }
            log.info("Map {} successfully loaded!", editor.getMapName());

        } catch (IOException e) {
            log.error("Unable to read map file", e);
        } catch (ParseException e) {
            log.error("Error parsing the map's header", e);
        }
    }

    protected void updateFields() {
        editor.setMapName(textFieldMapName.getText());
        editor.setMapCreated((Date) spinnerDateCreated.getValue());
        editor.setLastSaved((Date) spinnerDateLastModified.getValue());
        editor.setUnusedDate1((Date) spinnerDateUnused1.getValue());
        editor.setUnusedDate2((Date) spinnerDateUnused2.getValue());
        editor.setWorkHours1((Double) spinnerLongWorkHours1.getValue());
        editor.setWorkHours2((Double) spinnerLongWorkHours2.getValue());
        editor.setPreviewImage(previewInternal);
    }

    protected void updatePreviewImage(BufferedImage img) {
        editor.setPreviewImage(img);
        this.previewInternal = editor.getPreviewImage();
        labelPreviewImage.setIcon(new ImageIcon(BitmapUtil.resize(previewInternal, previewSize)));
    }

    protected void setFieldsEnabled(final boolean enabled) {
        textFieldMapName.setEnabled(enabled);
        spinnerDateCreated.setEnabled(enabled);
        spinnerDateLastModified.setEnabled(enabled);
        spinnerDateUnused1.setEnabled(enabled);
        spinnerDateUnused2.setEnabled(enabled);
        spinnerLongWorkHours1.setEnabled(enabled);
        spinnerLongWorkHours2.setEnabled(enabled);
    }


    protected static FileNameExtensionFilter getSupportedImageFileNameExtensionFilter() {
        // get supported image formats
        String[] formatNames = ImageIO.getReaderFormatNames();
        // remove duplicates & sort
        for (int i = 0; i < formatNames.length; i++) {
            formatNames[i] = formatNames[i].toLowerCase();
        }
        Collection<String> filteredFormatNames = new TreeSet<String>(ImmutableSet.copyOf(formatNames));
        // get nice list of file extensions
        String extensions = Joiner.on(" ,").join(filteredFormatNames);
        return new FileNameExtensionFilter(String.format("Supported image files (%s)", extensions), formatNames);
    }

    // preferences

    protected static final Preferences prefs = PrefsHelper.getPrefs();
    protected static final String regKeyBase = "metadata.";
    protected static final String regKeyLastMap = regKeyBase + "map";
    protected static final String regKeyLastImage = regKeyBase + "image";

    protected void storePrefs() {
        if(lastLoaded != null) {
            prefs.put(regKeyLastMap, lastLoaded.getPath());
        }
        if(lastPreview != null) {
            prefs.put(regKeyLastImage, lastPreview.getPath());
        }
    }

    protected void restorePrefs() {
        if(PrefsHelper.exists(regKeyLastMap)) {
            File map = new File(prefs.get(regKeyLastMap, null));
            this.lastLoaded = map;
            this.textFieldMapFilePath.setText(map.getPath());
        }
        if(PrefsHelper.exists(regKeyLastImage)) {
            File img = new File(prefs.get(regKeyLastImage, null));
            this.lastPreview = img;
        }
    }

    /*
     * Write preferences when closing window
     * @see java.awt.Window#dispose()
     */
    @Override
    public void dispose() {
        storePrefs();
        super.dispose();
    }

}
