package de.nx42.maps4cim.util;


import static org.junit.Assert.assertEquals;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Test;

public class DateUtilsTest {

    protected static final SimpleDateFormat isoDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

    // different representations of 2013-04-01 12:00:00 (UTC)
    protected static final String expectedDate = "2013-04-01T12:00:00-0000";
    protected static final long expectedTicks = 635004144000000000L;
    protected static final long expectedUnixMs = 1364817600000L;


    @Test
    public void testDateToTicks() throws Exception {
        long expected = expectedTicks;
        long actual = DateUtils.dateToTicks(getExpectedDate());
        assertEquals(expected, actual);
    }

    @Test
    public void testTicksToDate() throws Exception {
        Date expected = getExpectedDate();
        Date actual = DateUtils.ticksToDate(expectedTicks);
        assertEquals(expected.compareTo(actual), 0);
    }

    @Test
    public void testGetDateUTC() throws Exception {
        // 2013-04-01 12:00:00 UTC
        long expected = expectedUnixMs;
        Date dt = DateUtils.getDateUTC(2013, 4, 1, 12, 0, 0);
        long actual = (dt.getTime());

        assertEquals(expected, actual);
    }

    @Test
    public void testGetExpectedDate() throws Exception {
        Date dt = getExpectedDate();
        assertEquals(expectedUnixMs, dt.getTime());
    }

    private static Date getExpectedDate() throws ParseException {
        return isoDate.parse(expectedDate);
    }

}
