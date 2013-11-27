package de.nx42.maps4cim.header;

import static de.nx42.maps4cim.header.CustomHeader.formatHeaderString;
import static de.nx42.maps4cim.header.CustomHeader.staticString03;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.Arrays;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteSource;
import com.google.common.io.ByteStreams;
import com.google.common.io.Files;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.nx42.maps4cim.util.DateUtils;
import de.nx42.maps4cim.util.math.KMPMatch;

public class HeaderParser {

    private static Logger log = LoggerFactory.getLogger(HeaderParser.class);

    // public accessors

    /**
     * Takes a CiM 2 map file as argument, parses the header and returns a
     * CustomHeader-object representing the relevant contents of this header.
     * Expects an existing and fully accessible CiM 2 map file.
     * @param map the map to parse
     * @return the CustomHeader-object containing the data of this header
     * @throws ParseException if there is an error parsing the header
     * @throws IOException if there is an error reading the file's contents
     */
    public static CustomHeader parse(File map) throws ParseException, IOException {
        ByteSource bs = Files.asByteSource(map);
        return parse(bs);
    }

    /**
     * Takes a byte-array representing a full CiM 2 map (or at least the full
     * header of the map), parses the header and returns a CustomHeader-object
     * representing the relevant contents of this header.
     * @param map the map to parse
     * @return the CustomHeader-object containing the data of this header
     * @throws ParseException if there is an error parsing the header
     * @throws IOException if there is an error accessing the array-contents
     * (highly unlikely, this is owed to the InputStream-abstraction that is used)
     */
    public static CustomHeader parse(byte[] map) throws ParseException, IOException {
        ByteSource bs = ByteSource.wrap(map);
        return parse(bs);
    }

    /**
     * Takes any ByteSource representing a full CiM 2 map (or at least the full
     * header of the map), parses the header and returns a CustomHeader-object
     * representing the relevant contents of this header.
     * @param source the map to parse
     * @return the CustomHeader-object containing the data of this header
     * @throws ParseException if there is an error parsing the header
     * @throws IOException if there is an error accessing the array-contents
     */
    public static CustomHeader parse(ByteSource source) throws ParseException, IOException {
        InputStream is = null;
        try {
            is = source.openBufferedStream();
            byte[] relevant = getRelevantPart(is);
            return execute(relevant);
        } finally {
            if(is != null) {
                is.close();
            }
        }
    }

    // workers

    /**
     * Reads the input stream of a file, determines the end of the relevant
     * information and copies this (rather short) subset into a new byte-array
     * for faster access. Does not close the stream.
     * @param is an open input stream to the header to parse
     * @return a copy of the relevant portion of the header as byte-array
     * @throws IOException if anything goes wrong while accessing the stream
     */
    protected static byte[] getRelevantPart(InputStream is) throws IOException {
        // read until "Editor Player" and store as byte[]
        byte[] readUntil = formatHeaderString(staticString03);
        int len = KMPMatch.indexOf(is, readUntil);
        byte[] header = new byte[len];
        is.read(header, 0, len);
        return header;
    }

    /**
     * Returns the first index after the end of the header
     * @param source the stream to find the end of the header in
     * @return the end of the header / start of the body
     * @throws IOException if the stream can't be read
     */
    public static int findEndOfHeader(ByteSource source) throws IOException {
        InputStream is = null;
        try {
            is = source.openBufferedStream();
            // find "GameState+SerializableTerrainData"
            int lastBlockStart = KMPMatch.indexOf(is, CustomHeader.formatHeaderString(CustomHeader.staticString06));
            // header length: how many multiples of 4096 (+256)
            int headerMulti = lastBlockStart / 4096;
            return headerMulti * 4096 + 256;
        } finally {
            if(is != null) {
                is.close();
            }
        }
    }

    /**
     * Actually parses the relevant part of the header and writes the results
     * into a new CustomHeader-object
     * @param header a byte-array containing at least the relevant part of the
     * map's header (can be retrieved via {@link HeaderParser#getRelevantPart(InputStream)})
     * @return the CustomHeader-object containing the data of this header
     * @throws ParseException if there is an error parsing the header
     */
    protected static CustomHeader execute(byte[] header) throws ParseException {
        CustomHeader ch = CustomHeader.newEmpty();

        // read intro
        int introEnd = readToString(header, 0);
        ch.intro = Arrays.copyOfRange(header, 0, introEnd);

        // read date/times
        int dateStart = readAfterGap(header, introEnd, 3);
        int dateEnd = readAfterBytes(header, dateStart, CustomHeader.staticBinary01) - CustomHeader.staticBinary01.length;

        // read 64 bit integers (date/time) until end is reached
        int dateAmount = (dateEnd - dateStart) / 8;
        long[] dateTimeStamps = new long[dateAmount];
        ByteArrayDataInput bin = ByteStreams.newDataInput(header, dateStart);

        for (int i = 0; i < dateAmount; i++) {
            dateTimeStamps[i] = bin.readLong();
        }

        // interpret dates
        if(dateAmount >= 6) {
            // current format (custom map)
            ch.unusedDate1 = DateUtils.ticksToDate(dateTimeStamps[0]);
            ch.unusedDate2 = DateUtils.ticksToDate(dateTimeStamps[1]);
            ch.lastSaved = DateUtils.ticksToDate(dateTimeStamps[2]);
            ch.mapCreated = DateUtils.ticksToDate(dateTimeStamps[3]);
            ch.workTime1 = dateTimeStamps[4];
            ch.workTime2 = dateTimeStamps[5];
        } else if(dateAmount >= 4) {
            // old format (campaign map), without mapCreated and workTime2
            ch.unusedDate1 = DateUtils.ticksToDate(dateTimeStamps[0]);
            ch.unusedDate2 = DateUtils.ticksToDate(dateTimeStamps[1]);
            ch.lastSaved = ch.mapCreated = DateUtils.ticksToDate(dateTimeStamps[2]);
            ch.workTime1 = ch.workTime2 = dateTimeStamps[3];
        } else {
            // fail, just write some default values
            log.warn("Can't read date & time values: unexpected format");
            ch.unusedDate1 = ch.unusedDate2 = CustomHeader.unusedDateDefault;
            ch.lastSaved = ch.mapCreated = DateUtils.getDateUTC(2013, 1, 1, 12, 0, 0);
        }

        // read map name
        int nameStart = readAfterString(header, dateEnd, CustomHeader.staticString02);
        ch.mapName = parseHeaderString(header, nameStart);

        // read minimap image
        int beginPngLen = readAfterString(header, nameStart);
        ch.pngLength = Arrays.copyOfRange(header, beginPngLen, beginPngLen + 3);

        int beginPng = beginPngLen + 3;
        int pngLength = int24parse(ch.pngLength);
        ch.png = Arrays.copyOfRange(header, beginPng, beginPng + pngLength);

        return ch;
    }

    // navigate within byte[]

    /**
     * Reads to the start of the next String
     * @param header the bytes to read from
     * @param off the offset to start the search at
     * @return the index position of the first byte of the next String
     */
    protected static int readToString(byte[] header, int off) {
        int limit = header.length - 3;
        int i = off;

        while(i < limit) {
            i = readAfterGap(header, i, 2);
            if(header[i] != 0 && header[i+1] == 0 && header[i+2] != 0 && header[i+3] == 0 ) {
                // String detected: 0, 0, len, 0, char, 0, ...
                return i-2;
            }
            i++;
        }

        return -1;
    }

    /**
     * Reads to the end of the next String
     * @param header the bytes to read from
     * @param off the offset to start the search at
     * @return the index position of the first byte after the next String
     */
    protected static int readAfterString(byte[] header, int off) {
        int start = readToString(header, off);
        int len = header[start + 2] & 0xFF;

        return start + len*2 + 3;
    }

    /**
     * Reads to the end of the next String that matches the specified String
     * @param header the bytes to read from
     * @param off the offset to start the search at
     * @param s the String to match
     * @return the index position of the first byte after the specified String
     */
    protected static int readAfterString(byte[] header, int off, String s) {
        return readAfterBytes(header, off, formatHeaderString(s));
    }

    /**
     * Reads to the end of the next occurrence of the specified bytes
     * @param header the bytes to read from
     * @param off the offset to start the search at
     * @param b the bytes to match
     * @return the index position of the first byte after the specified byte-array
     */
    protected static int readAfterBytes(byte[] header, int off, byte[] b) {
        int i = KMPMatch.indexOf(header, b, off);
        if(i >= 0) {
            return i + b.length;
        } else {
            return -1;
        }
    }

    /**
     * Reads to the start of the next gap with at least the specified length.
     * @param header the bytes to read from
     * @param off the offset to start the search at
     * @param len the minimum length of the gap
     * @return the index position of the first byte of the gap
     */
    protected static int readToGap(byte[] header, int off, int len) {
        return KMPMatch.indexOf(header, new byte[len], off);
    }

    /**
     * Reads to the end of the next gap with at least the specified length.
     * Will always read to the first non-zero byte.
     * @param header the bytes to read from
     * @param off the offset to start the search at
     * @param len the minimum length of the gap
     * @return the index position of the first non-zero byte after the specified gap
     */
    protected static int readAfterGap(byte[] header, int off, int len) {
        int i = readToGap(header, off, len) + len;
        if(i < 0) {
            return i;
        }

        while(i < header.length && header[i] == 0) {
            i++;
        }
        return i;
    }

    // parse datatypes

    /**
     * Converts a int24, stored in 3 bytes, into a default java int32
     * @param int24 the 24bit-integer to convert
     * @return 32bit representation of the int24 as primitive java int
     */
    protected static int int24parse(byte[] int24) {
        return int24[2] & 0xFF |
              (int24[1] & 0xFF) << 8 |
              (int24[0] & 0xFF) << 16;
    }


    // parse string that begins at offset, detect end automatically

    /**
     * Converts a String from the binary format that is used in the map format
     * back to a native java String. Reads from the specified offset to the
     * end of the String (String length is part of the binary String format).
     * This is basically the reverse function to
     * {@link CustomHeader#formatHeaderString(CharSequence)}.
     * @param b the byte-array that contains the desired string
     * @param off the 0-based offset where the String begins
     * @return the String which starts at the specified offset
     */
    protected static String parseHeaderString(byte[] b, int off) {
        int len = b[off+2] & 0xFF;
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append((char) b[off + 4 + i*2]);
        }
        return sb.toString();
    }

    /**
     *  Converts a String from the binary format that is used in the map format
     * back to a native java String. Reads from the start of the array to the
     * end of the String (not the array!)
     * @param b the byte-array that contains the desired string
     * @return the String which is represented by the specified array
     */
    protected static String parseHeaderString(byte[] b) {
        return parseHeaderString(b, 0);
    }


}
