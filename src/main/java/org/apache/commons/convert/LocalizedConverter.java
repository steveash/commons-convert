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

import java.util.Locale;
import java.util.TimeZone;

/** Localized converter interface. Classes implement this interface
 * to convert one object type to another. Methods are provided to
 * localize the conversion.
 * <p>Localized converters are necessary for things like dates, times, and numbers. Those
 * conversions are dependent on a locale and/or time zone. Java's default conversions
 * (<code>toString</code>, <code>valueOf</code>) use the JVM's default locale and
 * time zone - which might not be what the application requires. Implementations of
 * <code>LocalizedConverter</code> will use the specified locale and time zone instead of
 * the JVM defaults.</p>
 */
public interface LocalizedConverter<S, T> extends Converter<S, T> {
    /** Converts <code>obj</code> to <code>T</code>.
     *
     * @param obj The source <code>Object</code> to convert
     * @param locale The locale used for conversion - must not be <code>null</code>
     * @param timeZone The time zone used for conversion - must not be <code>null</code>
     * @return The converted <code>Object</code>
     * @throws ConversionException
     */
    public T convert(S obj, Locale locale, TimeZone timeZone) throws ConversionException;

    /** Converts <code>obj</code> to <code>T</code>.
     *
     * @param obj The source <code>Object</code> to convert
     * @param locale The locale used for conversion - must not be <code>null</code>
     * @param timeZone The time zone used for conversion - must not be <code>null</code>
     * @param formatString Optional formatting string
     * @return The converted <code>Object</code>
     * @throws ConversionException
     */
    public T convert(S obj, Locale locale, TimeZone timeZone, String formatString) throws ConversionException;
}
