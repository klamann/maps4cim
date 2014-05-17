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

import ij.ImagePlus;
import ij.process.ImageProcessor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.nx42.maps4cim.ResourceLoader;
import de.nx42.maps4cim.gui.comp.JSliderBounded;
import de.nx42.maps4cim.gui.comp.PanelImage;
import de.nx42.maps4cim.gui.util.Components;
import de.nx42.maps4cim.map.texture.data.TexHexTriplet;
import de.nx42.maps4cim.util.ImageJUtils;

public class TextureChooser extends JDialog {

    private static final long serialVersionUID = -5218266535798385906L;

    /** Return value if cancel is chosen. */
    public static final int CANCEL_OPTION = 1;
    /** Return value if approve (yes, ok) is chosen. */
    public static final int APPROVE_OPTION = 0;
    /** Return value if an error occured. */
    public static final int ERROR_OPTION = -1;

    protected static final String resFolder = ResourceLoader.addBasePath("img/");
    protected static final String resTextureGrass = resFolder + "texture-grass.png";
    protected static final String resTextureRoughgrass = resFolder + "texture-roughgrass.png";
    protected static final String resTextureMud = resFolder + "texture-mud.png";
    protected static final String resTextureDirt = resFolder + "texture-dirt.png";

    protected static final ImagePlus baseImage = ImageJUtils.openFromResource(resTextureGrass);
    protected static final int[] pixelsGrass = getPixelsForImageResource(resTextureGrass);
    protected static final int[] pixelsRoughGrass = getPixelsForImageResource(resTextureRoughgrass);
    protected static final int[] pixelsMud = getPixelsForImageResource(resTextureMud);
    protected static final int[] pixelsDirt = getPixelsForImageResource(resTextureDirt);

    protected static final Color colorGrass = new Color(88, 110, 29);
    protected static final Color colorRoughgrass = new Color(49, 49, 8);
    protected static final Color colorMud = new Color(142, 68, 41);
    protected static final Color colorDirt = new Color(203, 175, 147);
    protected static final int colorPreviewSize = 32;

    private JTextField textFieldColorCode;
    private JTextField textFieldDirt;
    private JSliderBounded sliderRoughgrass;
    private JSliderBounded sliderMud;
    private JSliderBounded sliderDirt;
    private PanelImage panelPreview;
    private JTextField textFieldRoughgrass;
    private JTextField textFieldMud;
    private JLabel labelUsedCurrent;

    protected static final int colorPointsMax = 255;
    protected int colorPoints = 255;
    protected int returnValue = ERROR_OPTION;


    public TextureChooser() {
        super();
        setTitle("Pick-a-Texture");
        setBounds(200, 100, 630, 305);
        setMinimumSize(new Dimension(630, 305));
        setModalityType(ModalityType.APPLICATION_MODAL);
        setLocationByPlatform(true);
        Components.setIconImages(this);
        
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                returnValue = CANCEL_OPTION;
            }
        });

        JButton btnDone = new JButton("Done");
        btnDone.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                returnValue = APPROVE_OPTION;
                setVisible(false);
            }
        });

        JButton btnCancel = new JButton("Cancel");
        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                returnValue = CANCEL_OPTION;
                setVisible(false);
            }
        });

        JPanel panelColorPicker = new JPanel();

        JPanel panelPreviewContainer = new JPanel();
        GroupLayout groupLayout = new GroupLayout(getContentPane());
        groupLayout.setHorizontalGroup(
            groupLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                    .addGroup(groupLayout.createSequentialGroup()
                        .addComponent(panelColorPicker, GroupLayout.PREFERRED_SIZE, 360, Short.MAX_VALUE)
                        .addPreferredGap(ComponentPlacement.UNRELATED)
                        .addComponent(panelPreviewContainer, GroupLayout.DEFAULT_SIZE, 360, Short.MAX_VALUE))
                    .addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
                        .addComponent(btnCancel)
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addComponent(btnDone)))
                .addContainerGap()
        );
        groupLayout.setVerticalGroup(
            groupLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(groupLayout.createParallelGroup(Alignment.TRAILING, true)
                    .addComponent(panelColorPicker, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
                    .addComponent(panelPreviewContainer, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE))
                .addPreferredGap(ComponentPlacement.UNRELATED)
                .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
                    .addComponent(btnDone)
                    .addComponent(btnCancel))
                .addContainerGap()
        );
        panelPreviewContainer.setLayout(new BorderLayout(0, 0));

        JLabel lblPreviewNote = new JLabel("Warning: Preview may be inaccurate");
        panelPreviewContainer.add(lblPreviewNote, BorderLayout.SOUTH);
        lblPreviewNote.setHorizontalAlignment(SwingConstants.TRAILING);

        JPanel panelPreviewWrapper = new JPanel();
        panelPreviewContainer.add(panelPreviewWrapper, BorderLayout.CENTER);
        panelPreviewWrapper.setBorder(new TitledBorder(new LineBorder(new Color(184, 207, 229)), "Preview", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        panelPreviewWrapper.setLayout(new BorderLayout(0, 0));

        JPanel panelPreviewBackground = new PanelImage(ResourceLoader.getImageFromResource(resTextureGrass), true);
        panelPreviewBackground.setBackground(colorGrass);
        panelPreviewWrapper.add(panelPreviewBackground, BorderLayout.CENTER);

        panelPreview = new PanelImage(ResourceLoader.getImageFromResource(resTextureGrass), true);
        panelPreview.setBackground(colorGrass);

        GroupLayout gl_panelPreviewBackground = new GroupLayout(panelPreviewBackground);
        gl_panelPreviewBackground.setHorizontalGroup(
            gl_panelPreviewBackground.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_panelPreviewBackground.createSequentialGroup()
                    .addGap(25)
                    .addComponent(panelPreview, 0, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
                    .addGap(25))
        );
        gl_panelPreviewBackground.setVerticalGroup(
            gl_panelPreviewBackground.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_panelPreviewBackground.createSequentialGroup()
                    .addGap(25)
                    .addComponent(panelPreview, 0, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
                    .addGap(25))
        );
        panelPreviewBackground.setLayout(gl_panelPreviewBackground);

        JLabel lblColorCode = new JLabel("Color-Code:");

        textFieldColorCode = new JTextField();
        textFieldColorCode.addKeyListener(textureCodeEntered);
        textFieldColorCode.setHorizontalAlignment(SwingConstants.TRAILING);
        textFieldColorCode.setText("000000");
        textFieldColorCode.setColumns(6);

        JLabel lblRgrass = new JLabel("RGrass");

        sliderRoughgrass = new JSliderBounded();
        sliderRoughgrass.setValue(0);
        sliderRoughgrass.setPaintTicks(true);
        sliderRoughgrass.setPaintLabels(true);
        sliderRoughgrass.setMajorTickSpacing(255);
        sliderRoughgrass.setMinorTickSpacing(32);
        sliderRoughgrass.setMaximum(255);
        sliderRoughgrass.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                updatePreviewFromSliders();
                JSliderBounded.sliderChange(sliderRoughgrass, textFieldRoughgrass);
            }
        });

        textFieldRoughgrass = new JTextField();
        textFieldRoughgrass.setHorizontalAlignment(SwingConstants.TRAILING);
        textFieldRoughgrass.setText("0");
        textFieldRoughgrass.addKeyListener(new KeyAdapter(){
            @Override
            public void keyReleased(KeyEvent ke) {
                JSliderBounded.textFieldChange(textFieldRoughgrass, sliderRoughgrass, textFieldRoughgrass.getText());
            }
        });
        textFieldRoughgrass.setColumns(3);

        JPanel panelRoughgrassPreview = new PanelImage(ResourceLoader.getImageFromResource(resTextureRoughgrass), true);
        panelRoughgrassPreview.setBackground(colorRoughgrass);

        JPanel panelMudPreview = new PanelImage(ResourceLoader.getImageFromResource(resTextureMud), true);
        panelMudPreview.setBackground(colorMud);

        JLabel lblMud = new JLabel("Mud");

        sliderMud = new JSliderBounded();
        sliderMud.setValue(0);
        sliderMud.setPaintLabels(true);
        sliderMud.setPaintTicks(true);
        sliderMud.setMinorTickSpacing(32);
        sliderMud.setMajorTickSpacing(255);
        sliderMud.setMaximum(255);
        sliderMud.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                updatePreviewFromSliders();
                JSliderBounded.sliderChange(sliderMud, textFieldMud);
            }
        });

        textFieldMud = new JTextField();
        textFieldMud.setHorizontalAlignment(SwingConstants.TRAILING);
        textFieldMud.setText("0");
        textFieldMud.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                JSliderBounded.textFieldChange(textFieldMud, sliderMud, textFieldMud.getText());
            }
        });
        textFieldMud.setColumns(3);

        sliderDirt = new JSliderBounded();
        sliderDirt.setValue(0);
        sliderDirt.setMajorTickSpacing(255);
        sliderDirt.setPaintTicks(true);
        sliderDirt.setPaintLabels(true);
        sliderDirt.setMinorTickSpacing(32);
        sliderDirt.setMaximum(255);
        sliderDirt.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                updatePreviewFromSliders();
                JSliderBounded.sliderChange(sliderDirt, textFieldDirt);
            }
        });

        textFieldDirt = new JTextField();
        textFieldDirt.setHorizontalAlignment(SwingConstants.TRAILING);
        textFieldDirt.setText("0");
        textFieldDirt.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                JSliderBounded.textFieldChange(textFieldDirt, sliderDirt, textFieldDirt.getText());
            }
        });
        textFieldDirt.setColumns(3);

        JPanel panelDirtPreview = new PanelImage(ResourceLoader.getImageFromResource(resTextureDirt), true);
        panelDirtPreview.setBackground(colorDirt);

        JLabel lblDirt = new JLabel("Dirt");

        JLabel labelSlash = new JLabel("/");

        labelUsedCurrent = new JLabel("255");

        JLabel labelUsedMax = new JLabel("255");

        JLabel lblUsed = new JLabel("Points left to assign:");
        lblUsed.setToolTipText("You may only mix your textures up to an overall opacity of 100% or they will look very broken (usually pink)");
        GroupLayout gl_panelColorPicker = new GroupLayout(panelColorPicker);
        gl_panelColorPicker.setHorizontalGroup(
            gl_panelColorPicker.createParallelGroup(Alignment.TRAILING)
                .addGroup(Alignment.LEADING, gl_panelColorPicker.createSequentialGroup()
                    .addComponent(lblColorCode)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(textFieldColorCode, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addContainerGap())
                .addGroup(gl_panelColorPicker.createSequentialGroup()
                    .addGroup(gl_panelColorPicker.createParallelGroup(Alignment.LEADING)
                        .addComponent(panelDirtPreview, colorPreviewSize, colorPreviewSize, colorPreviewSize)
                        .addComponent(panelMudPreview, colorPreviewSize, colorPreviewSize, colorPreviewSize)
                        .addComponent(panelRoughgrassPreview, colorPreviewSize, colorPreviewSize, colorPreviewSize))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(gl_panelColorPicker.createParallelGroup(Alignment.LEADING)
                        .addComponent(lblRgrass)
                        .addComponent(lblMud)
                        .addComponent(lblDirt))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(gl_panelColorPicker.createParallelGroup(Alignment.LEADING)
                        .addComponent(sliderRoughgrass, 0, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
                        .addComponent(sliderDirt, 0, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
                        .addComponent(sliderMud, 0, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(gl_panelColorPicker.createParallelGroup(Alignment.LEADING)
                        .addComponent(textFieldRoughgrass, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(textFieldMud, Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(textFieldDirt, Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
                .addGroup(gl_panelColorPicker.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(lblUsed)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(labelUsedCurrent)
                    .addGap(3)
                    .addComponent(labelSlash)
                    .addGap(3)
                    .addComponent(labelUsedMax))
        );
        gl_panelColorPicker.setVerticalGroup(
            gl_panelColorPicker.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_panelColorPicker.createSequentialGroup()
                    .addGroup(gl_panelColorPicker.createParallelGroup(Alignment.BASELINE)
                        .addComponent(lblColorCode)
                        .addComponent(textFieldColorCode, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(ComponentPlacement.UNRELATED)
                    .addGroup(gl_panelColorPicker.createParallelGroup(Alignment.CENTER)
                        .addComponent(textFieldRoughgrass, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(panelRoughgrassPreview, colorPreviewSize, colorPreviewSize, colorPreviewSize)
                        .addComponent(lblRgrass)
                        .addComponent(sliderRoughgrass, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(gl_panelColorPicker.createParallelGroup(Alignment.CENTER)
                        .addComponent(textFieldMud, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(sliderMud, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(panelMudPreview, colorPreviewSize, colorPreviewSize, colorPreviewSize)
                        .addComponent(lblMud))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(gl_panelColorPicker.createParallelGroup(Alignment.CENTER)
                        .addComponent(textFieldDirt, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(sliderDirt, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(panelDirtPreview, colorPreviewSize, colorPreviewSize, colorPreviewSize)
                        .addComponent(lblDirt))
                    .addPreferredGap(ComponentPlacement.UNRELATED)
                    .addGroup(gl_panelColorPicker.createParallelGroup(Alignment.BASELINE)
                        .addComponent(lblUsed)
                        .addComponent(labelUsedCurrent)
                        .addComponent(labelSlash)
                        .addComponent(labelUsedMax)))
        );
        panelColorPicker.setLayout(gl_panelColorPicker);

        getContentPane().setLayout(groupLayout);
    }

    // action listeners

    protected void updatePreviewFromSliders() {
        TexHexTriplet tex = TexHexTriplet.of(sliderRoughgrass.getValue(), sliderMud.getValue(), sliderDirt.getValue());

        // update preview
        BufferedImage b = mixTextures(tex);
        panelPreview.setImage(b);

        // update bounds
        updateBounds(tex);

        // update labels
        labelUsedCurrent.setText(String.valueOf(colorPoints));
        textFieldColorCode.setText(tex.getHexString());
    }

    protected KeyAdapter textureCodeEntered = new KeyAdapter() {
        @Override
        public void keyReleased(KeyEvent e) {
            try {
                updateFromHexCode(textFieldColorCode.getText());
            } catch(NumberFormatException ex) {
                // format errors are expected while typing, just ignore
            }
        }
    };

    protected void updateFromHexCode(String hexTriplet) {
        TexHexTriplet tex = TexHexTriplet.parse(hexTriplet);
        updateBounds(tex);
        updateSliderValues(tex);
    }

    protected void updateBounds(TexHexTriplet tex) {
        colorPoints = colorPointsMax - tex.roughgrass - tex.mud - tex.dirt;
        sliderRoughgrass.setUpperBound(tex.roughgrass + colorPoints);
        sliderMud.setUpperBound(tex.mud + colorPoints);
        sliderDirt.setUpperBound(tex.dirt + colorPoints);
    }

    protected void updateSliderValues(TexHexTriplet tex) {
        sliderRoughgrass.setValue(tex.roughgrass);
        sliderMud.setValue(tex.mud);
        sliderDirt.setValue(tex.dirt);
    }

    // --------------


    public int showDialog() {
        setVisible(true);
        return returnValue;
    }

    public int showDialog(String texture) {
        try {
            updateFromHexCode(texture);
        } catch(NumberFormatException ex) { /* no valid code -> keep the last */ }

        return showDialog();
    }

    public TexHexTriplet getTexture() {
        return new TexHexTriplet(sliderRoughgrass.getValue(), sliderMud.getValue(), sliderDirt.getValue());
    }

    // texture calculation

    protected BufferedImage mixTextures(TexHexTriplet tex) {
        return mixTextures(tex.roughgrass, tex.mud, tex.dirt);
    }

    protected BufferedImage mixTextures(int roughGrass, int mud, int dirt) {
        return mixTextures(roughGrass / 255.0, mud / 255.0, dirt / 255.0);
    }

    protected BufferedImage mixTextures(double roughGrass, double mud, double dirt) {

        /*
         * - get each argb int[] for grass, roughgrass, mud, dirt
         * - in a single loop, fill a new argb int[]
         *   * always take 100% green as base
         *   * blend with other images, according to their alpha value
         */

        ImagePlus img = (ImagePlus) baseImage.clone();
        ImageProcessor ip = img.getProcessor();

        double a = Math.max(1.0 - roughGrass - mud - dirt, 0.0);
        int[] pixels = new int[pixelsGrass.length];
        for (int i = 0; i < pixelsGrass.length; i++) {

            int gr = pixelsGrass[i];
            int rg = pixelsRoughGrass[i];
            int md = pixelsMud[i];
            int dr = pixelsDirt[i];

            pixels[i] = pixelBlend(gr, a, rg, roughGrass, md, mud, dr, dirt);
        }

        ip.setPixels(pixels);
        return ip.getBufferedImage();
    }

    protected static int pixelBlend(int rgb1, double a1, int rgb2, double a2, int rgb3, double a3, int rgb4, double a4) {
        return  0xFF000000                                                                                                                                   // alpha
              | (int) (((rgb1 >>> 16) & 0xFF) * a1 + ((rgb2 >>> 16) & 0xFF) * a2 + ((rgb3 >>> 16) & 0xFF) * a3 + ((rgb4 >>> 16) & 0xFF) * a4 + 0.5) << 16    // red
              | (int) (((rgb1 >>>  8) & 0xFF) * a1 + ((rgb2 >>>  8) & 0xFF) * a2 + ((rgb3 >>>  8) & 0xFF) * a3 + ((rgb4 >>>  8) & 0xFF) * a4 + 0.5) <<  8    // green
              | (int) (( rgb1         & 0xFF) * a1 + ( rgb2         & 0xFF) * a2 + ( rgb3         & 0xFF) * a3 + ( rgb4         & 0xFF) * a4 + 0.5);         // blue
    }

    protected static int[] getPixelsForImageResource(String resource) {
        ImagePlus img = ImageJUtils.openFromResource(resource);
        return (int[]) img.getProcessor().getPixels();
    }
}
