package de.nx42.maps4cim.update;


import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ProgramVersionTest {

    /*
     * <major>.<minor>.<patch>-<qualifier>-<build number>
     * example: 4.5.11-RC1-3732
     */

    @Test
    public void testCompareMajorMinor() throws Exception {
        assertTrue(compareVersions("1.0", "1.0") == 0);
        assertTrue(compareVersions("1.0", "1.1") < 0);
        assertTrue(compareVersions("1.0", "0.9") > 0);
    }

    @Test
    public void testCompareMajorMinorPatch() {
        assertTrue(compareVersions("1.0", "1.0.1") < 0);
        assertTrue(compareVersions("1.0.1", "1.1") < 0);
        assertTrue(compareVersions("1.1", "1.1.0") == 0);
        assertTrue(compareVersions("1.1", "1.0.9.150") > 0);
    }

    @Test
    public void testCompareMajorMinorLetters() {
        assertTrue(compareVersions("1.a", "1.a.0") == 0);
        assertTrue(compareVersions("1.a", "1.b") < 0);

        // don't mix numbers and letters. If you do, numbers are preffered:
        assertTrue(compareVersions("1.1", "1.a") > 0);
        assertTrue(compareVersions("1.g", "1.1") < 0);
        assertTrue(compareVersions("1.0", "1.a") < 0);  // only exception
    }

    @Test
    public void testCompareQualifier() {
        assertTrue(compareVersions("1.0-alpha", "1.0-alpha") == 0);
        assertTrue(compareVersions("1.0-alpha", "1.0-beta") < 0);
        assertTrue(compareVersions("1.0-beta", "1.0-final") < 0);
        assertTrue(compareVersions("1.0", "1.0-beta") > 0);
        assertTrue(compareVersions("1.0-alpha", "1.0.1-alpha") < 0);
        assertTrue(compareVersions("1.0.1-final", "1.1-beta") < 0);
        assertTrue(compareVersions("1.a-beta", "2.0-final") < 0);
    }

    @Test
    public void testCompareQualifierBuildnumber() {
        assertTrue(compareVersions("1.1-alpha", "1.1-alpha-20140414") < 0);
        assertTrue(compareVersions("1.1-alpha-20140414", "1.1.0-alpha-20140417") < 0);
        assertTrue(compareVersions("1.1-beta", "1.1.0-alpha-20140417") > 0);
        assertTrue(compareVersions("1.1-beta", "1.1") < 0);
        assertTrue(compareVersions("1.1.0", "1.1.1-alpha") < 0);
    }

    private int compareVersions(String v1, String v2) {
        return new ProgramVersion(v1).compareTo(new ProgramVersion(v2));
    }

}
