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

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import junit.framework.TestCase;

public class TestCollectionConverters extends TestCase {

    @SuppressWarnings("unchecked")
    public static <S> void assertArrayToCollection(String label, S source, Class<? extends Collection> targetClass, int resultSize) throws Exception {
        Class<?> sourceClass = source.getClass();
        Converter<S, Collection<?>> converter = (Converter<S, Collection<?>>) Converters.getConverter(sourceClass, targetClass);
        assertTrue(label + " can convert", converter.canConvert(sourceClass, targetClass));
        Collection<?> result = converter.convert(source);
        assertTrue(label + " converted", targetClass.isAssignableFrom(result.getClass()));
        assertEquals(label + " result size", resultSize, result.size());
        try {
            Converter<Collection<?>, S> reflectiveConverter = (Converter<Collection<?>, S>) Converters.getConverter(targetClass, sourceClass);
            assertEquals(label + " reflection converted", sourceClass, reflectiveConverter.convert(result).getClass());
            assertTrue(label + " can convert", reflectiveConverter.canConvert(targetClass, sourceClass));
        } catch (UnconvertableException e) {
            System.out.println(converter.getClass() + " not reflective");
        }
    }

    public TestCollectionConverters(String name) {
        super(name);
    }

    public void testCollectionConverters() throws Exception {
        ConverterLoader loader = new CollectionConverters();
        loader.loadConverters();
        int[] intArray = {0, 1, 2, 3, 3};
        assertArrayToCollection("int[] to List", intArray, List.class, intArray.length);
        assertArrayToCollection("int[] to LinkedList", intArray, LinkedList.class, intArray.length);
        assertArrayToCollection("int[] to Set", intArray, Set.class, intArray.length - 1);
        assertArrayToCollection("int[] to TreeSet", intArray, TreeSet.class, intArray.length - 1);
        boolean[] booleanArray = {true, false};
        assertArrayToCollection("boolean[] to List", booleanArray, List.class, booleanArray.length);
        byte[] byteArray = {0, 1, 2, 3};
        assertArrayToCollection("byte[] to List", byteArray, List.class, byteArray.length);
        char[] charArray = {'a', 'b', 'c'};
        assertArrayToCollection("char[] to List", charArray, List.class, charArray.length);
        double[] doubleArray = {0, 1, 2, 3};
        assertArrayToCollection("double[] to List", doubleArray, List.class, doubleArray.length);
        float[] floatArray = {0, 1, 2, 3};
        assertArrayToCollection("float[] to List", floatArray, List.class, floatArray.length);
        long[] longArray = {0, 1, 2, 3};
        assertArrayToCollection("long[] to List", longArray, List.class, longArray.length);
        short[] shortArray = {0, 1, 2, 3};
        assertArrayToCollection("short[] to List", shortArray, List.class, shortArray.length);
        String[] stringArray = {"a", "b", "c"};
        assertArrayToCollection("String[] to List", stringArray, List.class, stringArray.length);
    }
}
