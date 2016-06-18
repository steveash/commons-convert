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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import junit.framework.TestCase;

public class TestNumberConverters extends TestCase {

    protected static final Class<?>[] classArray = {BigDecimal.class, BigInteger.class, Byte.class, Double.class, Integer.class, Float.class, Long.class, Short.class, String.class};

    @SuppressWarnings("unchecked")
    public static <S> void assertConversion(S source) throws Exception {
        Class<?> sourceClass = source.getClass();
        for (Class<?> targetClass : classArray) {
            Converter<S, ?> converter = ( Converter<S, ?>) Converters.getConverter(sourceClass, targetClass);
            String label = converter.getClass().getSimpleName();
            assertTrue(label + " can convert", converter.canConvert(sourceClass, targetClass));
            Object result = converter.convert(source);
            assertEquals(label + " converted", targetClass, result.getClass());
            try {
                LocalizedConverter<S, Object> localizedConverter = (LocalizedConverter) converter;
                Object localizedResult = localizedConverter.convert(source, Locale.getDefault(), null, null);
                Converter<Object, S> reflectiveConverter = null;
                try {
                    reflectiveConverter = (Converter<Object, S>) Converters.getConverter(targetClass, sourceClass);
                    assertEquals(label + " reflection converted", source, reflectiveConverter.convert(result));
                    LocalizedConverter<Object, S> localizedReflectiveConverter = (LocalizedConverter) reflectiveConverter;
                    assertEquals(label + " localized reflection converted", source, localizedReflectiveConverter.convert(localizedResult, Locale.getDefault(), null));
                } catch (UnconvertableException e) {
                    System.out.println(converter.getClass() + " not reflective");
                }
            } catch (ClassCastException e) {}
            assertToCollection(label, source);
        }
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

    public TestNumberConverters(String name) {
        super(name);
    }

    public void testNumberConverters() throws Exception {
        ConverterLoader loader = new NumberConverters();
        loader.loadConverters();
        String strDecimal = "1234567.89";
        String strInteger = "1234567";
        String strShort = "123";
        assertConversion(new BigDecimal(strDecimal));
        assertConversion(new BigInteger(strInteger));
        assertConversion(new Byte(strShort));
        assertConversion(new Double(strDecimal));
        assertConversion(new Integer(strInteger));
        assertConversion(new Float(strDecimal));
        assertConversion(new Long(strInteger));
        assertConversion(new Short(strShort));
    }
}
