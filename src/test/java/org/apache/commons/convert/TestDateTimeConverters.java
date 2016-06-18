/*******************************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *******************************************************************************/
package org.apache.commons.convert;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;

import junit.framework.TestCase;

public class TestDateTimeConverters extends TestCase {

    public static <S, T> void assertConversion(String label, Converter<S, T> converter, S source, T target) throws Exception {
        assertConversion(label, converter, source, target, true);
    }

    @SuppressWarnings("unchecked")
    public static <S, T> void assertConversion(String label, Converter<S, T> converter, S source, T target, boolean testRegistration) throws Exception {
        assertTrue(label + " can convert", converter.canConvert(source.getClass(), target.getClass()));
        if (testRegistration) {
            assertEquals(label + " registered", converter.getClass(), Converters.getConverter(source.getClass(), target.getClass()).getClass());
        }
        assertEquals(label + " converted", target, converter.convert(source));
        Converter<T, S> reflectiveConverter = null;
        try {
            reflectiveConverter = (Converter<T, S>) Converters.getConverter(target.getClass(), source.getClass());
            assertEquals(label + " reflection converted", source, reflectiveConverter.convert(target));
        } catch (UnconvertableException e) {
            System.out.println(converter.getClass() + " is not reflective");
        }
        try {
            LocalizedConverter<S,T> localizedConverter = (LocalizedConverter) converter;
            T localizedResult = localizedConverter.convert(source, Locale.getDefault(), TimeZone.getDefault());
            T formattedResult = localizedConverter.convert(source, Locale.getDefault(), TimeZone.getDefault(), DateTimeConverters.CALENDAR_FORMAT);
            if (reflectiveConverter != null) {
                LocalizedConverter<T, S> localizedReflectiveConverter = (LocalizedConverter) reflectiveConverter;
                assertEquals(label + " localized reflection converted", source, localizedReflectiveConverter.convert(localizedResult, Locale.getDefault(), TimeZone.getDefault()));
                assertEquals(label + " formatted reflection converted", source, localizedReflectiveConverter.convert(formattedResult, Locale.getDefault(), TimeZone.getDefault(), DateTimeConverters.CALENDAR_FORMAT));
            }
        } catch (ClassCastException e) {}
    }

    @SuppressWarnings("unchecked")
    public static <S> void assertToCollection(String label, S source) throws Exception {
        Converter<S, ? extends Collection> toList = (Converter<S, ? extends Collection>) Converters.getConverter(source.getClass(), List.class);
        Collection<S> listResult = toList.convert(source);
        assertEquals(label + " converted to List", source, listResult.toArray()[0]);
        Converter<S, ? extends Collection> toSet = (Converter<S, ? extends Collection>) Converters.getConverter(source.getClass(), Set.class);
        Collection<S> setResult = toSet.convert(source);
        assertEquals(label + " converted to Set", source, setResult.toArray()[0]);
    }

    public TestDateTimeConverters(String name) {
        super(name);
    }

    @SuppressWarnings("deprecation")
    public void testDateTimeConverters() throws Exception {
        ConverterLoader loader = new DateTimeConverters();
        loader.loadConverters();
        // Java date-related classes default to Jan 1, 1970 00:00:00 in some methods,
        // so we use it here for simplicity.
        java.util.Date utilDate = new java.util.Date(70, 0, 1, 0, 0, 0);
        long currentTime = utilDate.getTime();
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.setTimeInMillis(currentTime);
        java.sql.Date sqlDate = new java.sql.Date(70, 0, 1);
        java.sql.Time sqlTime = new java.sql.Time(0, 0, 0);
        java.sql.Timestamp timestamp = new java.sql.Timestamp(currentTime);
        java.sql.Timestamp stampWithNanos = new java.sql.Timestamp(currentTime);
        stampWithNanos.setNanos(10);
        // Source class = java.util.Calendar
        DateFormat df = new SimpleDateFormat(DateTimeConverters.CALENDAR_FORMAT);
        df.setCalendar(cal);
        assertConversion("CalendarToDate", new DateTimeConverters.CalendarToDate(), cal, utilDate);
        assertConversion("CalendarToLong", new DateTimeConverters.CalendarToLong(), cal, currentTime);
        assertConversion("CalendarToString", new DateTimeConverters.CalendarToString(), cal, df.format(cal.getTime()));
        assertConversion("CalendarToTimestamp", new DateTimeConverters.CalendarToTimestamp(), cal, timestamp);
        assertToCollection("CalendarToCollection", cal);
        // Source class = java.util.Date
        assertConversion("DateToCalendar", new DateTimeConverters.DateToCalendar(), utilDate, cal);
        assertConversion("DateToLong", new DateTimeConverters.GenericDateToLong<java.util.Date>(java.util.Date.class), utilDate, currentTime);
        assertConversion("DateToSqlDate", new DateTimeConverters.DateToSqlDate(), utilDate, sqlDate);
        assertConversion("DateToSqlTime", new DateTimeConverters.DateToSqlTime(), utilDate, sqlTime);
        assertConversion("DateToString", new DateTimeConverters.DateToString(), utilDate, df.format(cal.getTime()));
        assertConversion("DateToTimestamp", new DateTimeConverters.DateToTimestamp(), utilDate, timestamp);
        assertConversion("DateToTimestamp", new DateTimeConverters.DateToTimestamp(), timestamp, timestamp, false);
        assertToCollection("DateToCollection", utilDate);
        // Source class = java.sql.Date
        assertConversion("SqlDateToDate", new DateTimeConverters.SqlDateToDate(), sqlDate, new java.util.Date(sqlDate.getTime()));
        assertConversion("SqlDateToLong", new DateTimeConverters.GenericDateToLong<java.sql.Date>(java.sql.Date.class), sqlDate, sqlDate.getTime());
        assertConversion("SqlDateToString", new DateTimeConverters.SqlDateToString(), sqlDate, sqlDate.toString());
        assertConversion("SqlDateToTimestamp", new DateTimeConverters.SqlDateToTimestamp(), sqlDate, new java.sql.Timestamp(sqlDate.getTime()));
        assertToCollection("SqlDateToCollection", sqlDate);
        // Source class = java.sql.Time
        assertConversion("SqlTimeToLong", new DateTimeConverters.GenericDateToLong<java.sql.Time>(java.sql.Time.class), sqlTime, sqlTime.getTime());
        assertToCollection("SqlTimeToCollection", sqlTime);
        assertConversion("SqlTimeToString", new DateTimeConverters.SqlTimeToString(), sqlTime, sqlTime.toString());
        // Source class = java.sql.Timestamp
        assertConversion("TimestampToLong", new DateTimeConverters.GenericDateToLong<java.sql.Timestamp>(java.sql.Timestamp.class), timestamp, currentTime);
        assertConversion("TimestampToSqlDate", new DateTimeConverters.TimestampToSqlDate(), new java.sql.Timestamp(sqlDate.getTime()), sqlDate);
        assertConversion("TimestampToSqlTime", new DateTimeConverters.TimestampToSqlTime(), new java.sql.Timestamp(sqlDate.getTime()), sqlTime);
        assertConversion("TimestampToString", new DateTimeConverters.TimestampToString(), stampWithNanos, stampWithNanos.toString());
        assertToCollection("TimestampToCollection", timestamp);
        // TimeZone tests
        TimeZone tz = TimeZone.getDefault();
        assertConversion("TimeZoneToString", new DateTimeConverters.TimeZoneToString(), tz, tz.getID());
        assertToCollection("TimeZoneToCollection", tz);
    }
}
