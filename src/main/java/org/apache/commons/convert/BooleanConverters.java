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

/** Boolean <code>Converter</code> classes. */
public class BooleanConverters implements ConverterLoader {
    /**
     * An object that converts a <code>Boolean</code> to an <code>Integer</code>.
     */
    public static class BooleanToInteger extends AbstractConverter<Boolean, Integer> {
        public BooleanToInteger() {
            super(Boolean.class, Integer.class);
        }

        /**
         * Returns 1 if <code>obj</code> is true, or zero if 
         * <code>obj</code> is false.
         */
        public Integer convert(Boolean obj) throws ConversionException {
             return obj.booleanValue() ? 1 : 0;
        }
    }

    /**
     * An object that converts an <code>Integer</code> to a <code>Boolean</code>.
     */
    public static class IntegerToBoolean extends AbstractConverter<Integer, Boolean> {
        public IntegerToBoolean() {
            super(Integer.class, Boolean.class);
        }

        /**
         * Returns <code>true</code> if <code>obj</code> is non-zero, or
         * <code>false</code> if <code>obj</code> is zero.
         */
        public Boolean convert(Integer obj) throws ConversionException {
             return obj.intValue() == 0 ? Boolean.FALSE : Boolean.TRUE;
        }
    }

    /**
     * An object that converts a <code>String</code> to a <code>Boolean</code>.
     */
    public static class StringToBoolean extends AbstractConverter<String, Boolean> {
        public StringToBoolean() {
            super(String.class, Boolean.class);
        }

        /**
         * Returns <code>true</code> if <code>obj</code> equals "true", or
         * <code>false</code> if <code>obj</code> is any other value. The
         * test for "true" is case-insensitive.
         */
        public Boolean convert(String obj) throws ConversionException {
            return "TRUE".equals(obj.trim().toUpperCase());
        }
    }

    public void loadConverters() {
        Converters.loadContainedConverters(BooleanConverters.class);
        Converters.registerConverter(new GenericSingletonToList<Boolean>(Boolean.class));
        Converters.registerConverter(new GenericSingletonToSet<Boolean>(Boolean.class));
        Converters.registerConverter(new GenericToStringConverter<Boolean>(Boolean.class));
    }
}
