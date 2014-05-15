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
package de.nx42.maps4cim.gui.window;

import ij.IJ;
import ij.ImagePlus;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.text.DecimalFormat;
import java.util.concurrent.ExecutionException;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.nx42.maps4cim.gui.comp.ImageJFileLoader;
import de.nx42.maps4cim.gui.comp.JSliderBounded;
import de.nx42.maps4cim.gui.comp.PanelBoxPlot;
import de.nx42.maps4cim.gui.comp.PanelBoxPlotWorld;
import de.nx42.maps4cim.gui.util.Components;
import de.nx42.maps4cim.util.ImageJUtils;
import de.nx42.maps4cim.util.math.Statistics;

public class HeightmapWindow extends JDialog {

    private static final long serialVersionUID = -6903095863690245446L;
    private static final Logger log = LoggerFactory.getLogger(HeightmapWindow.class);

    protected static final DecimalFormat dfStats = new DecimalFormat("0.#");
    protected static final DecimalFormat dfStatsFloat = new DecimalFormat("0.####");

    private JTextField textFieldImagePath;
    private JFileChooser loadImage;
    private PanelBoxPlot panelBoxPlot;
    private PanelBoxPlot panelWorld;
    private JButton btnDone;

    private JTextField textFieldImgMax;
    private JTextField textFieldImgUpperQuartile;
    private JTextField textFieldImgMedian;
    private JTextField textFieldImgMean;
    private JTextField textFieldImgLowerQuartile;
    private JTextField textFieldImgMin;
    private JTextField textFieldMapMax;
    private JTextField textFieldMapUpperQuartile;
    private JTextField textFieldMapMedian;
    private JTextField textFieldMapMean;
    private JTextField textFieldMapLowerQuartile;
    private JTextField textFieldMapMin;
    private JTextField textFieldSamples;
    private JTextField textFieldUniqueSamples;

    private JSliderBounded sliderMin;
    private JSliderBounded sliderMax;
    private JTextField textFieldBoundLower;
    private JTextField textFieldBoundUpper;


    protected File imageFile;
    protected Statistics stats;

    // TODO change processing: make filechooser a part of this window, on first openDialog() -> open chooser. chooser on top of open HeightMapWindow!
    // TODO resolve dependency to PanelHeightmapSelect

    // TODO set preview image
    // TODO red dots for min/max if != lower/upper whisker

    public HeightmapWindow() {
        super();
        setTitle("Grayscale Heightmap");
        setBounds(200, 100, 750, 666);
        setMinimumSize(new Dimension(450, 280));
        setModalityType(ModalityType.APPLICATION_MODAL);
        setLocationByPlatform(true);
        Components.setIconImages(this);

        JPanel panelBounds = new JPanel();

        JLabel lblImageLoaded = new JLabel("Image loaded:");

        textFieldImagePath = new JTextField();

        JButton btnBrowse = new JButton("Browse...");
        btnBrowse.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openFile();
            }
        });

        JSeparator separatorUpper = new JSeparator();

        JPanel panelMain = new JPanel();

        JScrollPane panelMainScroller = new JScrollPane(panelMain);

        JButton btnCancel = new JButton("Cancel");
        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        btnDone = new JButton("Done");
        btnDone.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // parent events are handled via function registerListenerDone
                dispose();
            }
        });
        GroupLayout groupLayout = new GroupLayout(getContentPane());
        groupLayout.setHorizontalGroup(
            groupLayout.createParallelGroup(Alignment.TRAILING)
                .addGroup(groupLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                        .addComponent(panelMainScroller, 0, 710, Short.MAX_VALUE)
                        .addComponent(separatorUpper, GroupLayout.DEFAULT_SIZE, 710, Short.MAX_VALUE)
                        .addGroup(groupLayout.createSequentialGroup()
                            .addComponent(lblImageLoaded)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(textFieldImagePath, GroupLayout.DEFAULT_SIZE, 529, Short.MAX_VALUE)
                            .addPreferredGap(ComponentPlacement.UNRELATED)
                            .addComponent(btnBrowse))
                        .addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
                            .addComponent(panelBounds, GroupLayout.DEFAULT_SIZE, 625, Short.MAX_VALUE)
                            .addPreferredGap(ComponentPlacement.UNRELATED)
                            .addGroup(groupLayout.createParallelGroup(Alignment.TRAILING, false)
                                .addComponent(btnDone, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnCancel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                    .addContainerGap())
        );
        groupLayout.setVerticalGroup(
            groupLayout.createParallelGroup(Alignment.LEADING)
                .addGroup(groupLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(lblImageLoaded)
                        .addComponent(textFieldImagePath, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnBrowse))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(separatorUpper, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(panelMainScroller, 0, 476, Short.MAX_VALUE)
                    .addPreferredGap(ComponentPlacement.UNRELATED)
                    .addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
                        .addGroup(groupLayout.createSequentialGroup()
                            .addComponent(btnDone)
                            .addPreferredGap(ComponentPlacement.UNRELATED)
                            .addComponent(btnCancel))
                        .addComponent(panelBounds, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addContainerGap())
        );

        JPanel panelBoxplotWrapper = new JPanel();
        panelBoxplotWrapper.setBorder(new TitledBorder(null, "Boxplot", TitledBorder.LEADING, TitledBorder.TOP, null, null));

        panelBoxPlot = new PanelBoxPlot();
        GroupLayout gl_panelBoxplotWrapper = new GroupLayout(panelBoxplotWrapper);
        gl_panelBoxplotWrapper.setHorizontalGroup(
            gl_panelBoxplotWrapper.createParallelGroup(Alignment.LEADING)
                .addComponent(panelBoxPlot, 150, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        gl_panelBoxplotWrapper.setVerticalGroup(
            gl_panelBoxplotWrapper.createParallelGroup(Alignment.LEADING)
                .addComponent(panelBoxPlot, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        panelBoxplotWrapper.setLayout(gl_panelBoxplotWrapper);

        JPanel panelPreviewWrapper = new JPanel();
        panelPreviewWrapper.setBorder(new TitledBorder(null, "Preview", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        panelPreviewWrapper.setLayout(new BorderLayout(0, 0));

        JPanel panelPreview = new JPanel();
        panelPreviewWrapper.add(panelPreview, BorderLayout.CENTER);

        JPanel panelData = new JPanel();
        panelData.setBorder(new TitledBorder(null, "Statistics", TitledBorder.LEADING, TitledBorder.TOP, null, null));

        JLabel lblMaximum = new JLabel("Maximum");

        JLabel lblUpperQuartile = new JLabel("Upper Q.");

        JLabel lblMedian = new JLabel("Median");

        JLabel lblMean = new JLabel("Mean");

        JLabel lblLowerQuartile = new JLabel("Lower Q.");

        JLabel lblMinimum = new JLabel("Minimum");

        JLabel lblSamples = new JLabel("Samples");

        JLabel lblUniqueSamples = new JLabel("Unique S.");

        textFieldImgMax = new JTextField();
        textFieldImgMax.setHorizontalAlignment(SwingConstants.TRAILING);
        textFieldImgMax.setEditable(false);
        textFieldImgMax.setColumns(6);

        textFieldImgUpperQuartile = new JTextField();
        textFieldImgUpperQuartile.setHorizontalAlignment(SwingConstants.TRAILING);
        textFieldImgUpperQuartile.setEditable(false);
        textFieldImgUpperQuartile.setColumns(6);

        textFieldImgMedian = new JTextField();
        textFieldImgMedian.setHorizontalAlignment(SwingConstants.TRAILING);
        textFieldImgMedian.setEditable(false);
        textFieldImgMedian.setColumns(6);

        textFieldImgMean = new JTextField();
        textFieldImgMean.setHorizontalAlignment(SwingConstants.TRAILING);
        textFieldImgMean.setEditable(false);
        textFieldImgMean.setColumns(6);

        textFieldImgLowerQuartile = new JTextField();
        textFieldImgLowerQuartile.setHorizontalAlignment(SwingConstants.TRAILING);
        textFieldImgLowerQuartile.setEditable(false);
        textFieldImgLowerQuartile.setColumns(6);

        textFieldImgMin = new JTextField();
        textFieldImgMin.setHorizontalAlignment(SwingConstants.TRAILING);
        textFieldImgMin.setEditable(false);
        textFieldImgMin.setColumns(6);

        textFieldSamples = new JTextField();
        textFieldSamples.setHorizontalAlignment(SwingConstants.TRAILING);
        textFieldSamples.setEditable(false);
        textFieldSamples.setColumns(6);

        textFieldUniqueSamples = new JTextField();
        textFieldUniqueSamples.setHorizontalAlignment(SwingConstants.TRAILING);
        textFieldUniqueSamples.setEditable(false);
        textFieldUniqueSamples.setColumns(6);

        textFieldMapMax = new JTextField();
        textFieldMapMax.setHorizontalAlignment(SwingConstants.TRAILING);
        textFieldMapMax.setEditable(false);
        textFieldMapMax.setColumns(6);

        textFieldMapUpperQuartile = new JTextField();
        textFieldMapUpperQuartile.setHorizontalAlignment(SwingConstants.TRAILING);
        textFieldMapUpperQuartile.setEditable(false);
        textFieldMapUpperQuartile.setColumns(6);

        textFieldMapMedian = new JTextField();
        textFieldMapMedian.setHorizontalAlignment(SwingConstants.TRAILING);
        textFieldMapMedian.setEditable(false);
        textFieldMapMedian.setColumns(6);

        textFieldMapMean = new JTextField();
        textFieldMapMean.setHorizontalAlignment(SwingConstants.TRAILING);
        textFieldMapMean.setEditable(false);
        textFieldMapMean.setColumns(6);

        textFieldMapLowerQuartile = new JTextField();
        textFieldMapLowerQuartile.setHorizontalAlignment(SwingConstants.TRAILING);
        textFieldMapLowerQuartile.setEditable(false);
        textFieldMapLowerQuartile.setColumns(6);

        textFieldMapMin = new JTextField();
        textFieldMapMin.setHorizontalAlignment(SwingConstants.TRAILING);
        textFieldMapMin.setEditable(false);
        textFieldMapMin.setColumns(6);

        JLabel lblImg = new JLabel("Image");

        JLabel lblWorld = new JLabel("World");

        GroupLayout gl_panelData = new GroupLayout(panelData);
        gl_panelData.setHorizontalGroup(
            gl_panelData.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_panelData.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(gl_panelData.createParallelGroup(Alignment.LEADING)
                        .addComponent(lblMaximum)
                        .addComponent(lblUpperQuartile)
                        .addComponent(lblMedian)
                        .addComponent(lblMean)
                        .addComponent(lblLowerQuartile)
                        .addComponent(lblMinimum)
                        .addComponent(lblSamples)
                        .addComponent(lblUniqueSamples))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(gl_panelData.createParallelGroup(Alignment.LEADING)
                        .addComponent(textFieldSamples, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(textFieldUniqueSamples, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(Alignment.TRAILING, gl_panelData.createSequentialGroup()
                            .addGroup(gl_panelData.createParallelGroup(Alignment.CENTER)
                                .addComponent(textFieldImgMin, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(textFieldImgLowerQuartile, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(textFieldImgMean, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(textFieldImgMedian, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(textFieldImgUpperQuartile, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(textFieldImgMax, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(lblImg))
                            .addGap(3)
                            .addGroup(gl_panelData.createParallelGroup(Alignment.CENTER)
                                .addComponent(lblWorld)
                                .addComponent(textFieldMapMin, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(textFieldMapLowerQuartile, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(textFieldMapMean, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(textFieldMapMedian, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(textFieldMapUpperQuartile, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(textFieldMapMax, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                    .addContainerGap())
        );
        gl_panelData.setVerticalGroup(
            gl_panelData.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_panelData.createSequentialGroup()
                    .addGroup(gl_panelData.createParallelGroup(Alignment.BASELINE)
                        .addComponent(lblImg)
                        .addComponent(lblWorld))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(gl_panelData.createParallelGroup(Alignment.BASELINE)
                        .addComponent(lblMaximum)
                        .addComponent(textFieldImgMax, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(textFieldMapMax, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addGap(3)
                    .addGroup(gl_panelData.createParallelGroup(Alignment.BASELINE)
                        .addComponent(lblUpperQuartile)
                        .addComponent(textFieldImgUpperQuartile, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(textFieldMapUpperQuartile, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addGap(3)
                    .addGroup(gl_panelData.createParallelGroup(Alignment.BASELINE)
                        .addComponent(lblMedian)
                        .addComponent(textFieldImgMedian, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(textFieldMapMedian, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addGap(3)
                    .addGroup(gl_panelData.createParallelGroup(Alignment.BASELINE)
                        .addComponent(lblMean)
                        .addComponent(textFieldImgMean, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(textFieldMapMean, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addGap(3)
                    .addGroup(gl_panelData.createParallelGroup(Alignment.BASELINE)
                        .addComponent(lblLowerQuartile)
                        .addComponent(textFieldImgLowerQuartile, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(textFieldMapLowerQuartile, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(gl_panelData.createParallelGroup(Alignment.TRAILING)
                        .addComponent(lblMinimum)
                        .addComponent(textFieldImgMin, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(textFieldMapMin, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addGap(3)
                    .addGroup(gl_panelData.createParallelGroup(Alignment.BASELINE)
                        .addComponent(lblSamples)
                        .addComponent(textFieldSamples, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addGap(3)
                    .addGroup(gl_panelData.createParallelGroup(Alignment.BASELINE)
                        .addComponent(lblUniqueSamples)
                        .addComponent(textFieldUniqueSamples, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addContainerGap())
        );
        panelData.setLayout(gl_panelData);

        JPanel panelWorldWrapper = new JPanel();
        panelWorldWrapper.setBorder(new TitledBorder(null, "World", TitledBorder.LEADING, TitledBorder.TOP, null, null));

        panelWorld = new PanelBoxPlotWorld();
        GroupLayout gl_panelWorldWrapper = new GroupLayout(panelWorldWrapper);
        gl_panelWorldWrapper.setHorizontalGroup(
            gl_panelWorldWrapper.createParallelGroup(Alignment.LEADING)
                .addComponent(panelWorld, 150, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        gl_panelWorldWrapper.setVerticalGroup(
            gl_panelWorldWrapper.createParallelGroup(Alignment.LEADING)
                .addComponent(panelWorld, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        panelWorldWrapper.setLayout(gl_panelWorldWrapper);

        GroupLayout gl_panelMain = new GroupLayout(panelMain);
        gl_panelMain.setHorizontalGroup(
            gl_panelMain.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_panelMain.createSequentialGroup()
                    .addComponent(panelBoxplotWrapper, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addPreferredGap(ComponentPlacement.UNRELATED)
                    .addGroup(gl_panelMain.createParallelGroup(Alignment.LEADING)
                        .addComponent(panelPreviewWrapper, 224, 224, 224)
                        .addComponent(panelData, 224, 224, 224))
                    .addPreferredGap(ComponentPlacement.UNRELATED)
                    .addComponent(panelWorldWrapper, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        gl_panelMain.setVerticalGroup(
            gl_panelMain.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_panelMain.createSequentialGroup()
                    .addGroup(gl_panelMain.createParallelGroup(Alignment.BASELINE)
                        .addComponent(panelBoxplotWrapper, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(gl_panelMain.createSequentialGroup()
                            .addComponent(panelData, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(panelPreviewWrapper, 0, 224, 224)
                            .addGap(0, 0, Short.MAX_VALUE))
                        .addComponent(panelWorldWrapper, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        panelMain.setLayout(gl_panelMain);

        JLabel lblLowerBound = new JLabel("Lower Bound");

        textFieldBoundLower = new JTextField();
        textFieldBoundLower.setHorizontalAlignment(SwingConstants.TRAILING);
        textFieldBoundLower.setText("0");
        textFieldBoundLower.setColumns(4);
        textFieldBoundLower.addKeyListener(inputBoundLowerListener);

        JLabel lblM = new JLabel("m");

        sliderMin = new JSliderBounded();
        sliderMin.setMajorTickSpacing(500);
        sliderMin.setMinorTickSpacing(50);
        sliderMin.setPaintTicks(true);
        //sliderMin.setPaintLabels(true);
        sliderMin.setMinimum(-1000);
        sliderMin.setMaximum(1000);
        sliderMin.setValue(0);
        sliderMin.addChangeListener(sliderMinChange);

        textFieldBoundUpper = new JTextField();
        textFieldBoundUpper.setHorizontalAlignment(SwingConstants.TRAILING);
        textFieldBoundUpper.setText("100");
        textFieldBoundUpper.setColumns(4);
        textFieldBoundUpper.addKeyListener(inputBoundUpperListener);

        sliderMax = new JSliderBounded();
        sliderMax.setMajorTickSpacing(500);
        sliderMax.setMinorTickSpacing(50);
        sliderMax.setPaintTicks(true);
        sliderMax.setPaintLabels(true);
        sliderMax.setMinimum(-1000);
        sliderMax.setMaximum(1000);
        sliderMax.setValue(100);
        sliderMax.addChangeListener(sliderMaxChange);

        sliderMin.setUpperBound(sliderMax.getValue());
        sliderMax.setLowerBound(sliderMin.getValue());

        JLabel lblM_1 = new JLabel("m");

        JLabel lblUpperBound = new JLabel("Upper Bound");

        GroupLayout gl_panelBounds = new GroupLayout(panelBounds);
        gl_panelBounds.setHorizontalGroup(
            gl_panelBounds.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_panelBounds.createSequentialGroup()
                    .addGroup(gl_panelBounds.createParallelGroup(Alignment.LEADING)
                        .addComponent(lblLowerBound, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblUpperBound, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(gl_panelBounds.createParallelGroup(Alignment.LEADING)
                        .addComponent(textFieldBoundLower, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(textFieldBoundUpper, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(gl_panelBounds.createParallelGroup(Alignment.LEADING)
                        .addComponent(lblM, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblM_1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(gl_panelBounds.createParallelGroup(Alignment.LEADING)
                        .addGroup(gl_panelBounds.createSequentialGroup()
                            .addGap(9)
                            .addComponent(sliderMin, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGap(9))
                        .addComponent(sliderMax, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        gl_panelBounds.setVerticalGroup(
            gl_panelBounds.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_panelBounds.createSequentialGroup()
                    .addGroup(gl_panelBounds.createParallelGroup(Alignment.LEADING)
                        .addGroup(gl_panelBounds.createParallelGroup(Alignment.BASELINE)
                            .addComponent(lblLowerBound)
                            .addComponent(textFieldBoundLower, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblM))
                        .addComponent(sliderMin, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(gl_panelBounds.createParallelGroup(Alignment.LEADING)
                        .addGroup(gl_panelBounds.createParallelGroup(Alignment.BASELINE)
                            .addComponent(textFieldBoundUpper, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblM_1)
                            .addComponent(lblUpperBound))
                        .addComponent(sliderMax, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
        );
        panelBounds.setLayout(gl_panelBounds);
        getContentPane().setLayout(groupLayout);

    }


    protected void openFile() {
        if(loadImage == null) {
            loadImage = new ImageJFileLoader("Open Heightmap");
        }
        if(imageFile != null) {
            loadImage.setSelectedFile(new File(textFieldImagePath.getText()));
        }
        if (loadImage.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            updateFile(loadImage.getSelectedFile());
        }
    }

    public void updateFile(File imgFile) {
        if(imgFile != this.imageFile) {
            // reset previous contents, if any
            resetContents();

            // calculate image stats
            this.imageFile = imgFile;
            this.textFieldImagePath.setText(imgFile.getPath());

            this.panelBoxPlot.setCalculating(true);
            this.panelWorld.setCalculating(true);
            new StatsCalculator().execute();
        }
    }

    protected void resetContents() {
        // clear boxplots
        panelBoxPlot.resetStats();
        panelWorld.resetStats();

        // clear stats
        textFieldImgLowerQuartile.setText("");
        textFieldImgMax.setText("");
        textFieldImgMean.setText("");
        textFieldImgMedian.setText("");
        textFieldImgMin.setText("");
        textFieldImgUpperQuartile.setText("");

        textFieldMapMax.setText("");
        textFieldMapUpperQuartile.setText("");
        textFieldMapMedian.setText("");
        textFieldMapMean.setText("");
        textFieldMapLowerQuartile.setText("");
        textFieldMapMin.setText("");

        textFieldSamples.setText("");
        textFieldUniqueSamples.setText("");

    }

    protected void updateImageStats() {
        // update text fields
        DecimalFormat df = stats.getMax() > 1 ? dfStats : dfStatsFloat;
        textFieldImgLowerQuartile.setText(df.format(stats.getLowerQuartile()));
        textFieldImgMax.setText(df.format(stats.getMax()));
        textFieldImgMean.setText(df.format(stats.getMean()));
        textFieldImgMedian.setText(df.format(stats.getMedian()));
        textFieldImgMin.setText(df.format(stats.getMin()));
        textFieldImgUpperQuartile.setText(df.format(stats.getUpperQuartile()));
        textFieldSamples.setText(df.format(stats.getSamples()));
        textFieldUniqueSamples.setText(df.format(stats.getUniques()));

        // update boxplot
        panelBoxPlot.setStats(stats);
    }

    protected void updateWorldStats() {
        // calculate adjusted values
        int min = sliderMin.getValue();
        int max = sliderMax.getValue();
        int range = max - min;
        double mean = min + ((stats.getMean() - stats.getMin()) / stats.getRange()) * range;
        double median = min + ((stats.getMedian() - stats.getMin()) / stats.getRange()) * range;
        double lowerQ = min + ((stats.getLowerQuartile() - stats.getMin()) / stats.getRange()) * range;
        double upperQ = min + ((stats.getUpperQuartile() - stats.getMin()) / stats.getRange()) * range;

        // update text fields
        textFieldMapMax.setText(dfStats.format(max));
        textFieldMapUpperQuartile.setText(dfStats.format(upperQ));
        textFieldMapMedian.setText(dfStats.format(median));
        textFieldMapMean.setText(dfStats.format(mean));
        textFieldMapLowerQuartile.setText(dfStats.format(lowerQ));
        textFieldMapMin.setText(dfStats.format(min));

        // update boxplot
        Statistics adjusted = Statistics.of(stats.getSamples(), max, min, mean,
                median, lowerQ, upperQ, stats.getUniques()
        );
        panelWorld.setStats(adjusted);
    }

    // Getters

    public File getImageFile() {
        return imageFile;
    }

    public int getMinValue() {
        return getResult(textFieldBoundLower, 0);
    }

    public void setMinValue(String value) {
        JSliderBounded.textFieldChange(textFieldBoundLower, sliderMin, value);
    }

    public int getMaxValue() {
        return getResult(textFieldBoundUpper, 100);
    }

    public void setMaxValue(String value) {
        JSliderBounded.textFieldChange(textFieldBoundUpper, sliderMax, value);
    }

    public void registerListenerDone(ActionListener l) {
        btnDone.addActionListener(l);
    }

    private static int getResult(JTextField field, int fallback) {
        try {
            return (int) (Double.parseDouble(field.getText()) + 0.5);
        } catch(NumberFormatException e) {
            log.warn("Cannot parse \"{}\" as number", field.getText());
            return fallback;
        }
    }

    // Action Listeners

    protected ChangeListener sliderMinChange = new ChangeListener() {
        @Override
        public void stateChanged(ChangeEvent e) {
            JSliderBounded.sliderChange(sliderMin, textFieldBoundLower);
            sliderMax.setLowerBound(sliderMin.getValue());
            updateWorldStats();
        }
    };

    protected ChangeListener sliderMaxChange = new ChangeListener() {
        @Override
        public void stateChanged(ChangeEvent e) {
            JSliderBounded.sliderChange(sliderMax, textFieldBoundUpper);
            sliderMin.setUpperBound(sliderMax.getValue());
            updateWorldStats();
        }
    };

    protected KeyAdapter inputBoundUpperListener = new KeyAdapter(){
        @Override
        public void keyReleased(KeyEvent ke) {
            JSliderBounded.textFieldChange(textFieldBoundUpper, sliderMax, textFieldBoundUpper.getText());
        }
    };

    protected KeyAdapter inputBoundLowerListener = new KeyAdapter(){
        @Override
        public void keyReleased(KeyEvent ke) {
            JSliderBounded.textFieldChange(textFieldBoundLower, sliderMin, textFieldBoundLower.getText());
        }
    };

    // threading

    protected Statistics calculateStats() {
        ImagePlus img = IJ.openImage(imageFile.getAbsolutePath());
        return ImageJUtils.calculateStats(img);
    }

    protected void updateBoxPlotPanel() {
        panelBoxPlot.setCalculating(false);
        updateImageStats();
        panelWorld.setCalculating(false);
        updateWorldStats();
    }

    class StatsCalculator extends SwingWorker<Statistics, Object> {
        @Override
        public Statistics doInBackground() {
            log.debug("Calculating stats...");
            return calculateStats();
        }

        @Override
        protected void done() {
            try {
                log.debug("calculation done!");
                stats = get();
                updateBoxPlotPanel();
            } catch (InterruptedException e) {
                log.error("Threading error while creating box plot", e);
            } catch (ExecutionException e) {
                log.error("Error while calculating stats", e);
            }
        }
    }
}
