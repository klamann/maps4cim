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
package de.nx42.maps4cim.header;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.Date;

import javax.imageio.ImageIO;

import com.google.common.io.ByteSource;
import com.google.common.io.ByteStreams;
import com.google.common.io.Files;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.nx42.maps4cim.config.header.HeaderDef.BuildingSet;
import de.nx42.maps4cim.util.DateUtils;
import de.nx42.maps4cim.util.java2d.BitmapUtil;

/**
 * Provides functions to edit and replace the header of an existing map
 *
 * @author Sebastian Straub <sebastian-straub@gmx.net>
 */
public class HeaderEditor {

    private static Logger log = LoggerFactory.getLogger(HeaderEditor.class);
    protected static final int previewImageSize = 256;

    protected File mapFile;
    protected CustomHeader header;


    public HeaderEditor(String mapFilePath) throws IOException, ParseException {
        this(parseFileString(mapFilePath));
    }

    public HeaderEditor(File mapFile) throws ParseException, IOException {
        this.mapFile = mapFile;
        if(mapFile.isFile()) {
            if(mapFile.canRead()) {
                this.header = HeaderParser.parse(mapFile);
            } else {
                throw new IOException(mapFile + " can't be accessed!");
            }
        } else {
            throw new IOException(mapFile + " does not point to a file!");
        }
    }

    public void writeChanges(String dest) throws IOException {
        writeChanges(parseFileString(dest));
    }

    public void writeChanges(File dest) throws IOException {

        /*
         * - Find header end of old file
         * - write new header to byte array
         * - create new writer, append new header & main part of old file
         */

        // write to temp, rename if successful (useful if src == dest and errors occur...)
        File tmp = new File(dest.getParentFile(), dest.getName() + ".tmp");

        // create streams
        InputStream is = null;
        FileOutputStream fos = null;

        try {
            // open the source file and find the end of the header
            ByteSource source = Files.asByteSource(mapFile);
            int headerEnd = HeaderParser.findEndOfHeader(source);

            // open the destination file
            fos = new FileOutputStream(tmp);

            // write the header
            fos.write(header.generateHeader());

            // copy the contents (without header) from the source file to the destination
            is = source.openBufferedStream();
            long skipped = is.skip(headerEnd);
            if (skipped != headerEnd) {
                throw new IOException("Could not read from existing map file!");
            }
            ByteStreams.copy(is, fos);

            // move tmp file
            fos.close();
            if(dest.exists()) {
                dest.delete();
            }
            Files.move(tmp, dest);
        } finally {
            // close streams
            if (is != null) {
                is.close();
            }
            if (fos != null) {
                fos.close();
            }
        }
    }

    public void writeChanges() throws IOException {
        writeChanges(mapFile);
    }



    /**
     * Parses a String and returns it as File object, if the path is well-formed.
     * This function makes some more checks than new File(String), so empty file
     * Strings or invalid paths will be rejected right away.
     * @param filePath the file path String to parse
     * @return the File object represented by this path
     * @throws IOException if the file path is not well-formed or empty
     */
    protected static File parseFileString(String filePath) throws IOException {
        if(filePath == null || "".equals(filePath.trim())) {
            throw new FileNotFoundException("file path empty!");
        } else {
            File file = new File(filePath);
            // check if path is valid
            file.getCanonicalPath();
            return file;
        }
    }

    protected static final double ticksPerHour = DateUtils.ticksPerMs * 1000 * 60 * 60;

    protected static double ticksToHours(long ticks) {
        return ticks / ticksPerHour;
    }

    protected static long hoursToTicks(double hours) {
        return (long) (hours * ticksPerHour);
    }

    /**
     * Draws a preview image with the specified message
     * @return the bufferedimage with the message
     */
    protected static BufferedImage drawErrorImage(String message) {
        int width = 256, height = 256;
        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D ig2 = bi.createGraphics();

        Font font = new Font("Tahoma", Font.BOLD, 20);
        ig2.setFont(font);
        FontMetrics fontMetrics = ig2.getFontMetrics();
        int stringWidth = fontMetrics.stringWidth(message);
        int stringHeight = fontMetrics.getAscent();
        ig2.setPaint(Color.red);
        ig2.drawString(message, (width - stringWidth) / 2, height / 2 + stringHeight / 4);

        return bi;
    }




    /**
     * @return the header
     */
    public CustomHeader getHeader() {
        return header;
    }

    /**
     * @param header the header to set
     */
    public void setHeader(CustomHeader header) {
        this.header = header;
    }

    /**
     * @return the unusedDate1
     */
    public Date getUnusedDate1() {
        return header.unusedDate1;
    }

    /**
     * @param unusedDate1 the unusedDate1 to set
     */
    public void setUnusedDate1(Date unusedDate1) {
        header.unusedDate1 = unusedDate1;
    }

    /**
     * @return the unusedDate2
     */
    public Date getUnusedDate2() {
        return header.unusedDate2;
    }

    /**
     * @param unusedDate2 the unusedDate2 to set
     */
    public void setUnusedDate2(Date unusedDate2) {
        header.unusedDate2 = unusedDate2;
    }

    /**
     * @return the lastSaved
     */
    public Date getLastSaved() {
        return header.lastSaved;
    }

    /**
     * @param lastSaved the lastSaved to set
     */
    public void setLastSaved(Date lastSaved) {
        header.lastSaved = lastSaved;
    }

    /**
     * @return the mapCreated
     */
    public Date getMapCreated() {
        return header.mapCreated;
    }

    /**
     * @param mapCreated the mapCreated to set
     */
    public void setMapCreated(Date mapCreated) {
        header.mapCreated = mapCreated;
    }

    /**
     * @return the workTime1
     */
    public double getWorkHours1() {
        return ticksToHours(header.workTime1);
    }

    /**
     * @param workTime1 the workTime1 to set
     */
    public void setWorkHours1(double workHours1) {
        header.workTime1 = hoursToTicks(workHours1);
    }

    /**
     * @return the workTime2
     */
    public double getWorkHours2() {
        return ticksToHours(header.workTime2);
    }

    /**
     * @param workTime2 the workTime2 to set
     */
    public void setWorkHours2(double workHours2) {
        header.workTime2 = hoursToTicks(workHours2);
    }

    /**
     * @return the mapName
     */
    public String getMapName() {
        return header.mapName;
    }

    /**
     * @param mapName the mapName to set
     */
    public void setMapName(String mapName) {
        header.mapName = mapName;
    }

    /**
     * @return the buildingSet
     */
    public BuildingSet getBuildingSet() {
        return header.buildingSet;
    }

    /**
     * @param buildingSet the buildingSet to set
     */
    public void setBuildingSet(BuildingSet buildingSet) {
        header.setBuildingSet(buildingSet);
    }

    /**
     * @return the png
     */
    public BufferedImage getPreviewImage() {
        try {
            return ImageIO.read(new ByteArrayInputStream(header.png));
        } catch (IOException e) {
            log.error("Unable to convert PNG from header as BufferedImage", e);
            return drawErrorImage("Could not load preview image!");
        }
    }

    public void setPreviewImage(BufferedImage image) {
        setPreviewImage(image, previewImageSize, true);
    }

    public void setPreviewImage(BufferedImage image, int customEdgeLen, boolean forceResize) {
        // resize if desired or necessary (image too large or not squarish)
        BufferedImage render = image;
        if(forceResize || image.getWidth() != image.getHeight() || image.getWidth() > customEdgeLen) {
            render = BitmapUtil.resizeAndRectify(image, customEdgeLen);
        }

        // write png
        try {
            header.png = BitmapUtil.writePng(render);
            header.pngLength = CustomHeader.int24write(header.png.length);
        } catch (IOException e) {
            log.error("Could not convert buffered image to PNG byte[]", e);
        }
    }


}
