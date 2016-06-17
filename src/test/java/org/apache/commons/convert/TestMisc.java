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

import java.lang.reflect.Constructor;
import java.math.BigDecimal;
import java.net.URL;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import junit.framework.TestCase;

public class TestMisc extends TestCase {

    public static <S> void assertPassThru(Object wanted, Class<S> sourceClass) throws Exception {
        assertPassThru(wanted, sourceClass, sourceClass);
    }

    public static <S> void assertPassThru(Object wanted, Class<S> sourceClass, Class<? super S> targetClass) throws Exception {
        Converter<S, ? super S> converter = Converters.getConverter(sourceClass, targetClass);
        Object result = converter.convert(Util.<S>cast(wanted));
        assertEquals("pass thru convert", wanted, result);
        assertSame("pass thru exact equals", wanted, result);
        assertTrue("pass thru can convert wanted", converter.canConvert(wanted.getClass(), targetClass));
        assertTrue("pass thru can convert source", converter.canConvert(sourceClass, targetClass));
        assertEquals("pass thru source class", wanted.getClass(), converter.getSourceClass());
        assertEquals("pass thru target class", targetClass, converter.getTargetClass());
    }

    public static void assertStaticHelperClass(Class<?> clz) throws Exception {
        Constructor<?>[] constructors = clz.getDeclaredConstructors();
        assertEquals(clz.getName() + " constructor count", 1, constructors.length);
        assertEquals(clz.getName() + " private declared constructor", 1 << Constructor.DECLARED, constructors[0].getModifiers() & ~(1 << Constructor.PUBLIC) & (1 << Constructor.DECLARED));
        constructors[0].setAccessible(true);
        constructors[0].newInstance();
    }

    public void testConversionException() {
        java.util.Date nullDate = null;
        Converter<String, java.util.Date> converter = new DateTimeConverters.StringToDate();
        try {
            nullDate = converter.convert("");
        } catch (ConversionException e) {
            @SuppressWarnings("unused")
            Exception ex = new ConversionException("Test case");
            ex = new ConversionException("Test case", e);
        }
        assertEquals("ConversionException", null, nullDate);
    }

    public void testConverterFactory() {
        Converter<BigDecimal, URL> notFound = null;
        try {
            notFound = Converters.getConverter(BigDecimal.class, URL.class);
        } catch (ClassNotFoundException e) {}
        assertEquals("Converter not found", null, notFound);
        try {
            notFound = Converters.getConverter(BigDecimal.class, URL.class);
        } catch (ClassNotFoundException e) {}
        assertEquals("Converter not found", null, notFound);
    }

    public void testLoadContainedConvertersIgnoresException() {
        Converters.loadContainedConverters(TestMisc.class);
    }

    public void testPassthru() throws Exception {
        String string = "convert";
        BigDecimal bigDecimal = new BigDecimal("1.234");
        URL url = new URL("http://www.apache.org");
        List<String> baseList = Arrays.asList(new String[]{"a", "1"});
        ArrayList<String> arrayList = new ArrayList<String>(baseList);
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("a", "1");
        Object[] testObjects = new Object[] {
            string,
            bigDecimal,
            url,
            arrayList,
            hashMap,
        };
        for (Object testObject: testObjects) {
            assertPassThru(testObject, testObject.getClass());
        }
        assertPassThru(arrayList, arrayList.getClass(), List.class);
        assertPassThru(hashMap, hashMap.getClass(), Map.class);
    }

    public void testStaticHelperClass() throws Exception {
        assertStaticHelperClass(Converters.class);
        assertStaticHelperClass(Util.class);
    }

    public void testUtil() throws Exception {
        assertTrue("Is instance of", Util.instanceOf(SortedMap.class, Map.class));
        assertTrue("Is instance of", Util.instanceOf(Map.class, AbstractMap.class));
        assertTrue("Is instance of", Util.instanceOf(HashMap.class, Map.class));
        assertTrue("Is instance of", Util.instanceOf(HashMap.class, AbstractMap.class));
        assertFalse("Is not instance of", Util.instanceOf(URL.class, Map.class));
        assertFalse("Is not instance of", Util.instanceOf(List.class, Map.class));
        assertTrue("Is empty (null)", Util.isEmpty(null));
        assertTrue("Is empty (empty String)", Util.isEmpty(""));
        assertFalse("Is not empty", Util.isEmpty("convert"));
    }

    public static class ConverterLoaderImpl implements ConverterLoader {
        public void loadConverters() {
            throw new RuntimeException();
        }
    }
}
