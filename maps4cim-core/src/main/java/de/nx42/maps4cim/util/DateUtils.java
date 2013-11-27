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
package de.nx42.maps4cim.util;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Some utilities to work with dates and convert between java and .net Date
 * formats
 *
 * @author Sebastian Straub <sebastian-straub@gmx.net>
 */
public class DateUtils {

    /** the UTC (aka GMT) timezone */
    public static final TimeZone UTC = TimeZone.getTimeZone("GMT");
    /** the begin of the unix epoche (used by java.util.Date) in ticks (used
        by .net System.DateTime) */
    public static final long beginUnixEpoche = 621355968000000000L;
    /** the amount of ticks per millisecond used by .net System.DateTime */
    public static final long ticksPerMs = 10000L;

    /**
     * Converts a java.util.Date to the amount ticks that define a .net
     * System.DateTime
     * @param dt the java date to convert
     * @return the amount of ticks as defined in the .net DateTime for the
     * specified date
     */
    public static long dateToTicks(Date dt) {
        return beginUnixEpoche + dt.getTime() * ticksPerMs;
    }

    /**
     * Converts the ticks of a .net System.DateTime to a java.util.Date
     * @param ticks the amount of ticks as defined in the .net DateTime
     * @return the corresponding java date
     */
    public static Date ticksToDate(long ticks) {
        return new Date((ticks - beginUnixEpoche) / ticksPerMs);
    }

    /**
     * Creates a new date object. The time is adjusted to fit the current
     * timezone of the JVM.
     * @param year the value used to set the YEAR calendar field.
     * @param month the value used to set the MONTH calendar field. Month value is 1-based. e.g. 1 for January.
     * @param day the value used to set the DAY_OF_MONTH calendar field.
     * @param hour the value used to set the HOUR_OF_DAY calendar field.
     * @param minute the value used to set the MINUTE calendar field.
     * @param second the value used to set the SECOND calendar field.
     * @return a Date object representing the date and time as defined
     */
    public static Date getDateLocal(int year, int month, int day, int hour, int minute, int second) {
        Calendar gc = Calendar.getInstance();
        gc.set(year, month-1, day, hour, minute, second);
        return gc.getTime();
    }

    /**
     * Creates a new date object. The time is set in UTC, the local timezone
     * of the JVM is ignored.
     * @param year the value used to set the YEAR calendar field.
     * @param month the value used to set the MONTH calendar field. Month value is 1-based. e.g. 1 for January.
     * @param day the value used to set the DAY_OF_MONTH calendar field.
     * @param hour the value used to set the HOUR_OF_DAY calendar field.
     * @param minute the value used to set the MINUTE calendar field.
     * @param second the value used to set the SECOND calendar field.
     * @return a Date object representing the date and time as defined
     */
    public static Date getDateUTC(int year, int month, int day, int hour, int minute, int second) {
        Calendar gc = Calendar.getInstance();
        gc.setTimeZone(UTC);
        gc.set(Calendar.MILLISECOND, 0);
        gc.set(year, month-1, day, hour, minute, second);
        return gc.getTime();
    }


}
