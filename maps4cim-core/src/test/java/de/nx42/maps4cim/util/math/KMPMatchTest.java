package de.nx42.maps4cim.util.math;


import static org.junit.Assert.assertEquals;

import java.io.InputStream;

import javax.xml.bind.DatatypeConverter;

import com.google.common.io.ByteSource;

import org.junit.Test;

public class KMPMatchTest {

    private static final byte[] data = DatatypeConverter.parseHexBinary("abababcbababcababcab");
    private static final byte[] pattern = DatatypeConverter.parseHexBinary("ababcaba");
    private static final int expected = 4;

    @Test
    public void testIndexOfByteArrayByteArray() throws Exception {
        int actual = KMPMatch.indexOf(data, pattern);
        assertEquals(expected, actual);
    }

    @Test
    public void testIndexOfByteArrayByteArray2() throws Exception {
        int actual = KMPMatch.indexOf(data, pattern, 2);
        assertEquals(expected, actual);
    }

    @Test
    public void testIndexOfInputStreamByteArray() throws Exception {
        // convert to stream first
        ByteSource bs = ByteSource.wrap(data);
        InputStream is = bs.openBufferedStream();

        // compare as usual
        int actual = KMPMatch.indexOf(is, pattern);
        assertEquals(expected, actual);
    }

    @Test
    public void testIndexOfInputStreamByteArray2() throws Exception {
        byte[] data2 = { 0,1,2,3,0,1,2,3,4,0,1,2,3,4,5,0,1,2,3,4,5,6,0,1,2,3,4,5,6,7,0,1,2 };
        byte[] pattern2 = { 4,5,6,7 };

        ByteSource bs = ByteSource.wrap(data2);
        InputStream is = bs.openBufferedStream();

        // compare as usual
        int actual = KMPMatch.indexOf(is, pattern2);
        assertEquals(26, actual);
    }

}
