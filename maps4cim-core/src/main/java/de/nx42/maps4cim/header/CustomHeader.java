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
package de.nx42.maps4cim.header;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Date;

import javax.xml.bind.DatatypeConverter;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.nx42.maps4cim.util.DateUtils;
import de.nx42.maps4cim.util.java2d.BitmapUtil;

/**
 * Contains all available information of the map header.
 * Can parse headers of existing maps and write a new header based on the
 * provided data.
 *
 * @author Sebastian Straub <sebastian-straub@gmx.net>
 */
public class CustomHeader extends Header {

    private static Logger log = LoggerFactory.getLogger(CustomHeader.class);

    // Instance Data

    /** the first 2-7 bytes of the file */
    protected byte[] intro;
    /** the first unused date value, usually set to 2013-04-01 08:00:00 */
    protected Date unusedDate1;
    /** the second unused date value, usually set to 2013-04-01 08:00:00 */
    protected Date unusedDate2;
    /** the last time the map has been saved */
    protected Date lastSaved;
    /** the time when the map has been created */
    protected Date mapCreated;
    /** the time that has been spent on the map in the map editor. or something
        like that. distinction to {@link CustomHeader#workTime2} unknown */
    protected long workTime1;
    /** the time that has been spent on the map in the map editor. or something
        like that. distinction to {@link CustomHeader#workTime1} unknown */
    protected long workTime2;
    /** the name of the map (equals the file name without the ".map"-extension */
    protected String mapName;
    /** the length of the embedded map overview PNG (as int24 in 3 bytes).
        causes errors, if not equal to {@link CustomHeader#png}.length */
    protected byte[] pngLength;
    /** the embedded map overview, as binary PNG */
    protected byte[] png;

    // Static contents

    /** the first 7 bytes of a default user-generated map: fd 77 fc b6 e8 fe fe */
    protected static final byte[] introDefault = hex("fd 77 fc b6 e8 fe fe");
    /** the String after {@link CustomHeader#introDefault}: "GameState+SerializableMetaData" */
    protected static final String staticString01 = "GameState+SerializableMetaData";
    /** the default date that is used in the unused date fields: 2013-04-01 08:00:00 */
    protected static final Date unusedDateDefault = DateUtils.getDateUTC(2013, 4, 1, 8, 0, 0);
    /** the initial time (in .net "ticks") worked on the map. should be 0, of course */
    protected static final long workTimeDefault = 42;
    /** binary data which follows {@link CustomHeader#workTime2}: 00 00 01 fe fe */
    protected static final byte[] staticBinary01 = hex("00 00 01 fe fe");
    /** the String after {@link CustomHeader#staticBinary01}: "PlayerData" */
    protected static final String staticString02 = "PlayerData";
    /** default width of the map preview image */
    protected static final int pngWidth = 256;
    /** default height of the map preview image */
    protected static final int pngHeight = 256;
    /** binary data which follows {@link CustomHeader#png}:
        ff 00 64 00 64 00 00 00 00 00 00 00 00 64 00 64 00 64 01 00 00 */
    protected static final byte[] staticBinary02 =
            hex("ff 00 64 00 64 00 00 00 00 00 00 00 00 64 00 64 00 64 01 00 00");
    /** the String after {@link CustomHeader#staticBinary02}: "Editor Player" */
    protected static final String staticString03 = "Editor Player";
    /** binary data which follows the gap after {@link CustomHeader#staticString03}:
        ff ff 00 00 ff 00 00 ff 00 fe fe */
    protected static final byte[] staticBinary03 = hex("ff ff 00 00 ff 00 00 ff 00 fe fe");
    /** the String after {@link CustomHeader#staticBinary03}: "CompanyData" */
    protected static final String staticString04 = "CompanyData";
    /** the String after {@link CustomHeader#staticString04}: "Editor Company" */
    protected static final String staticString05 = "Editor Company";
    /** the String after the gap that follows {@link CustomHeader#staticString05}:
        "GameState+SerializableTerrainData" */
    protected static final String staticString06 = "GameState+SerializableTerrainData";
    /** binary data which follows {@link CustomHeader#staticString06}: 00 00 04 */
    protected static final byte[] staticBinary04 = hex("00 00 04");
    /** the Strings after {@link CustomHeader#staticBinary04}, in this order */
    protected static final String[] staticStrings07 =
        { "Grass", "Rough Grass", "Mud", "Dirt", "Ruined", "Cliff", "Pavement" };
    /** binary data which follows {@link CustomHeader#staticString07}: 00 00 08 01 */
    protected static final byte[] staticBinary05 = hex("00 00 08 01");



    public CustomHeader() {
        this.intro = introDefault;
        this.unusedDate1 = unusedDateDefault;
        this.unusedDate2 = unusedDateDefault;
        this.lastSaved = new Date();
        this.mapCreated = new Date();
        this.workTime1 = workTimeDefault;
        this.workTime2 = workTimeDefault;

        // TODO get file name
        // remove file extension, if existent
        this.mapName = "maps4cim";
//        this.mapName = fileName.replaceFirst("[.][^.]+$", "");

        this.png = getDefaultPNG();
        this.pngLength = int24write(png.length);
    }

    private CustomHeader(int empty) {
        // creates an empty object...
    }



    @Override
    public byte[] generateHeader() throws IOException {

        // first part
        ByteArrayDataOutput outP1 = ByteStreams.newDataOutput(4096);

        // static intro
        outP1.write(intro);
        outP1.write(formatHeaderString(staticString01));
        // gap of 4 bytes
        outP1.write(new byte[4]);

        // dates and timestamps
        outP1.writeLong(DateUtils.dateToTicks(unusedDate1));
        outP1.writeLong(DateUtils.dateToTicks(unusedDate2));
        outP1.writeLong(DateUtils.dateToTicks(lastSaved));
        outP1.writeLong(DateUtils.dateToTicks(mapCreated));
        outP1.writeLong(workTime1);
        outP1.writeLong(workTime2);

        // static data
        outP1.write(staticBinary01);
        outP1.write(formatHeaderString(staticString02));

        // map name
        outP1.write(formatHeaderString(mapName));
        // map overview image
        outP1.write(pngLength);
        outP1.write(png);

        // static data
        outP1.write(staticBinary02);
        outP1.write(formatHeaderString(staticString03));
        outP1.write(new byte[34]);
        outP1.write(staticBinary03);
        outP1.write(formatHeaderString(staticString04));
        outP1.write(formatHeaderString(staticString05));

        // second part
        ByteArrayDataOutput outP2 = ByteStreams.newDataOutput(256);

        // static data
        outP2.write(intro);
        outP2.write(formatHeaderString(staticString06));
        outP2.write(staticBinary04);
        for (String s : staticStrings07) {
            outP2.write(formatHeaderString(s));
        }
        outP2.write(staticBinary05);

        // combine the parts
        ByteArrayDataOutput out = ByteStreams.newDataOutput(4352);

        byte[] p1 = outP1.toByteArray();
        out.write(p1);
        // fill with 0s until next next free index % 4096 = 0
        out.write(new byte[((p1.length / 4096) + 1) * 4096 - p1.length]);

        byte[] p2 = outP2.toByteArray();
        out.write(p2);
        // fill with 0s until 256 bytes are filled after the beginning of p2
        out.write(new byte[256 - p2.length]);

        // return combined result
        return out.toByteArray();
    }

    // static stuff

    protected static byte[] getDefaultPNG() {
        BufferedImage bi = new BufferedImage(pngWidth, pngHeight, BufferedImage.TYPE_INT_ARGB);
        drawMaps4cimThumb(bi);

        try {
            return BitmapUtil.writePng(bi);
        } catch (IOException e) {
            log.error("Could not convert buffered image to PNG byte[]", e);
            return null;
        }
    }

    /**
     * Draws a simple default preview image
     * @param bi the image to draw into
     * @return the modified buffered image (not required, changes are written
     * inplace)
     */
    protected static BufferedImage drawMaps4cimThumb(BufferedImage bi) {
        Graphics2D ig2 = bi.createGraphics();

        Font font = new Font("Tahoma", Font.BOLD, 36);
        ig2.setFont(font);
        String title = "maps4cim";
        FontMetrics fontMetrics = ig2.getFontMetrics();
        int stringWidth = fontMetrics.stringWidth(title);
        int stringHeight = fontMetrics.getAscent();
        ig2.setPaint(Color.GREEN);
        ig2.drawString(title, (pngWidth - stringWidth) / 2, pngHeight / 2 + stringHeight / 4);

        Font fontSmall = new Font("Tahoma", Font.BOLD, 15);
        ig2.setFont(fontSmall);
        String subtitle = "another map rendered with";
        FontMetrics fontMetrics2 = ig2.getFontMetrics();
        int string2Width = fontMetrics2.stringWidth(subtitle);
        ig2.drawString(subtitle, (pngWidth - string2Width) / 2, pngHeight / 2 - stringHeight);

        return bi;
    }

    /**
     * Converts a default java int32 to the more exotic int24 (3 bytes)
     * @param int32 integer to convert. any int > 2^24 will be capped
     * @return int24-representation of the input, as byte[3]
     */
    public static byte[] int24write(int int32) {
        return new byte[] { (byte) (int32 >>> 16), (byte) (int32 >>> 8), (byte) (int32) };
    }

    /**
     * Converts a String to the binary format that is used in the map's header.
     * Note that Unicode chars are not supported.
     * @param s the String to convert
     * @return the binary representation of this String
     */
    public static byte[] formatHeaderString(CharSequence s) {
        int len = s.length();
        byte[] result = new byte[len * 2 + 3];

        result[2] = (byte) len;
        for (int i = 0; i < s.length(); i++) {
            result[i*2 + 4] = (byte) s.charAt(i);
        }

        return result;
    }

    /**
     * Converts the string argument into an array of bytes using
     * javax.xml.bind.DatatypeConverter.parseHexBinary
     * Whitespaces within hex Strings are allowed & ignored!
     * @param hex
     * @return
     */
    protected static byte[] hex(final String hex) {
        return DatatypeConverter.parseHexBinary(hex.replaceAll("\\s+", ""));
    }

    public static CustomHeader newEmpty() {
        return new CustomHeader(0);
    }


    // Getters and Setters

    /**
     * @return the unusedDate1
     */
    public Date getUnusedDate1() {
        return unusedDate1;
    }

    /**
     * @param unusedDate1 the unusedDate1 to set
     */
    public void setUnusedDate1(Date unusedDate1) {
        this.unusedDate1 = unusedDate1;
    }

    /**
     * @return the unusedDate2
     */
    public Date getUnusedDate2() {
        return unusedDate2;
    }

    /**
     * @param unusedDate2 the unusedDate2 to set
     */
    public void setUnusedDate2(Date unusedDate2) {
        this.unusedDate2 = unusedDate2;
    }

    /**
     * @return the lastSaved
     */
    public Date getLastSaved() {
        return lastSaved;
    }

    /**
     * @param lastSaved the lastSaved to set
     */
    public void setLastSaved(Date lastSaved) {
        this.lastSaved = lastSaved;
    }

    /**
     * @return the mapCreated
     */
    public Date getMapCreated() {
        return mapCreated;
    }

    /**
     * @param mapCreated the mapCreated to set
     */
    public void setMapCreated(Date mapCreated) {
        this.mapCreated = mapCreated;
    }

    /**
     * @return the workTime1
     */
    public long getWorkTime1() {
        return workTime1;
    }

    /**
     * @param workTime1 the workTime1 to set
     */
    public void setWorkTime1(long workTime1) {
        this.workTime1 = workTime1;
    }

    /**
     * @return the workTime2
     */
    public long getWorkTime2() {
        return workTime2;
    }

    /**
     * @param workTime2 the workTime2 to set
     */
    public void setWorkTime2(long workTime2) {
        this.workTime2 = workTime2;
    }

    /**
     * @return the mapName
     */
    public String getMapName() {
        return mapName;
    }

    /**
     * @param mapName the mapName to set
     */
    public void setMapName(String mapName) {
        this.mapName = mapName;
    }

    /**
     * @return the pngLength
     */
    public byte[] getPngLength() {
        return pngLength;
    }

    /**
     * @param pngLength the pngLength to set
     */
    public void setPngLength(byte[] pngLength) {
        this.pngLength = pngLength;
    }

    /**
     * @return the png
     */
    public byte[] getPng() {
        return png;
    }

    /**
     * @param png the png to set
     */
    public void setPng(byte[] png) {
        this.png = png;
    }



}
