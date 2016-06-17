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

import java.lang.reflect.Array;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;

/** Collection <code>Converter</code> classes. */
public class CollectionConverters implements ConverterLoader {

    @SuppressWarnings("unchecked")
    public void loadConverters() {
        Converters.loadContainedConverters(CollectionConverters.class);
        Converters.registerConverter(new GenericToStringConverter<Collection>(Collection.class));
        Converters.registerConverter(new GenericSingletonToList<Map>(Map.class));
        Converters.registerConverter(new GenericSingletonToSet<Map>(Map.class));
    }

    private static class ArrayClassToArrayList<S, T> extends AbstractConverter<S, T> {
        public ArrayClassToArrayList(Class<S> sourceClass, Class<T> targetClass) {
            super(sourceClass, targetClass);
        }

        public boolean canConvert(Class<?> sourceClass, Class<?> targetClass) {
            return sourceClass == this.getSourceClass() && targetClass == this.getTargetClass();
        }

        public T convert(S obj) throws ConversionException {
            List<Object> list = new ArrayList<Object>();
            int len = Array.getLength(obj);
            for (int i = 0; i < len; i++) {
                list.add(Array.get(obj, i));
            }
            return Util.<T>cast(list);
        }
    }

    private static class ArrayClassToHashSet<S, T> extends AbstractConverter<S, T> {
        public ArrayClassToHashSet(Class<S> sourceClass, Class<T> targetClass) {
            super(sourceClass, targetClass);
        }

        public boolean canConvert(Class<?> sourceClass, Class<?> targetClass) {
            return sourceClass == this.getSourceClass() && targetClass == this.getTargetClass();
        }

        public T convert(S obj) throws ConversionException {
            Set<Object> set = new HashSet<Object>();
            int len = Array.getLength(obj);
            for (int i = 0; i < len; i++) {
                set.add(Array.get(obj, i));
            }
            return Util.<T>cast(set);
        }
    }

    private static class ArrayClassToList<S, T> extends AbstractConverter<S, T> {
        public ArrayClassToList(Class<S> sourceClass, Class<T> targetClass) {
            super(sourceClass, targetClass);
        }

        public boolean canConvert(Class<?> sourceClass, Class<?> targetClass) {
            return sourceClass == this.getSourceClass() && targetClass == this.getTargetClass();
        }

        @SuppressWarnings("unchecked")
        public T convert(S obj) throws ConversionException {
            List<Object> list = null;
            try {
                list = (List<Object>) this.getTargetClass().newInstance();
            } catch (Exception e) {
                throw new ConversionException(e);
            }
            int len = Array.getLength(obj);
            for (int i = 0; i < len; i++) {
                list.add(Array.get(obj, i));
            }
            return Util.<T>cast(list);
        }
    }

    private static class ArrayClassToSet<S, T> extends AbstractConverter<S, T> {
        public ArrayClassToSet(Class<S> sourceClass, Class<T> targetClass) {
            super(sourceClass, targetClass);
        }

        public boolean canConvert(Class<?> sourceClass, Class<?> targetClass) {
            return sourceClass == this.getSourceClass() && targetClass == this.getTargetClass();
        }

        @SuppressWarnings("unchecked")
        public T convert(S obj) throws ConversionException {
            Set<Object> set = new HashSet<Object>();
            try {
                set = (Set<Object>) this.getTargetClass().newInstance();
            } catch (Exception e) {
                throw new ConversionException(e);
            }
            int len = Array.getLength(obj);
            for (int i = 0; i < len; i++) {
                set.add(Array.get(obj, i));
            }
            return Util.<T>cast(set);
        }
    }

    public static class ArrayCreator implements ConverterCreator, ConverterLoader {
        @SuppressWarnings("unchecked")
        public <S, T> Converter<S, T> createConverter(Class<S> sourceClass, Class<T> targetClass) {
            if (!targetClass.isArray()) {
               return null;
            }
            if (!Collection.class.isAssignableFrom(sourceClass)) {
               return null;
            }
            if (targetClass.getComponentType() == Boolean.TYPE) {
                return Util.cast(new CollectionToBooleanArray(sourceClass, targetClass));
            }
            if (targetClass.getComponentType() == Byte.TYPE) {
                return Util.cast(new CollectionToByteArray(sourceClass, targetClass));
            }
            if (targetClass.getComponentType() == Character.TYPE) {
                return Util.cast(new CollectionToCharArray(sourceClass, targetClass));
            }
            if (targetClass.getComponentType() == Double.TYPE) {
                return Util.cast(new CollectionToDoubleArray(sourceClass, targetClass));
            }
            if (targetClass.getComponentType() == Float.TYPE) {
                return Util.cast(new CollectionToFloatArray(sourceClass, targetClass));
            }
            if (targetClass.getComponentType() == Integer.TYPE) {
                return Util.cast(new CollectionToIntArray(sourceClass, targetClass));
            }
            if (targetClass.getComponentType() == Long.TYPE) {
                return Util.cast(new CollectionToLongArray(sourceClass, targetClass));
            }
            if (targetClass.getComponentType() == Short.TYPE) {
                return Util.cast(new CollectionToShortArray(sourceClass, targetClass));
            }
            return Util.cast(new CollectionToObjectArray(sourceClass, targetClass));
        }

        public void loadConverters() {
            Converters.registerCreator(this);
        }
    }

    /**
     * An object that converts an array to a <code>List</code>.
     */
    public static class ArrayToList<T> extends AbstractConverter<T[], List<T>> {
        public ArrayToList() {
            super(Object[].class, List.class);
        }

        @Override
        public boolean canConvert(Class<?> sourceClass, Class<?> targetClass) {
            if (!sourceClass.isArray()) {
                return false;
            }
            if (!List.class.isAssignableFrom(targetClass)) {
                return false;
            }
            if (Object[].class.isAssignableFrom(sourceClass)) {
                return true;
            }
            return false;
        }

        /**
         * Returns a <code>List</code> that contains the elements in
         * <code>obj</code>.
         */
        public List<T> convert(T[] obj) throws ConversionException {
            return Arrays.asList(obj);
        }
    }

    @SuppressWarnings("unchecked")
    private static class CollectionToBooleanArray<T> extends AbstractConverter<Collection, T> {
        public CollectionToBooleanArray(Class<Collection> sourceClass, Class<T> targetClass) {
            super(sourceClass, targetClass);
        }

        public boolean canConvert(Class<?> sourceClass, Class<?> targetClass) {
            return sourceClass == this.getSourceClass() && targetClass == this.getTargetClass();
        }

        public T convert(Collection obj) throws ConversionException {
            boolean[] array = new boolean[obj.size()];
            int index = 0;
            Iterator<Boolean> iterator = obj.iterator();
            while (iterator.hasNext()) {
                array[index] = iterator.next().booleanValue();
                index++;
            }
            return Util.<T>cast(array);
        }
    }

    @SuppressWarnings("unchecked")
    private static class CollectionToByteArray<T> extends AbstractConverter<Collection, T> {
        public CollectionToByteArray(Class<Collection> sourceClass, Class<T> targetClass) {
            super(sourceClass, targetClass);
        }

        public boolean canConvert(Class<?> sourceClass, Class<?> targetClass) {
            return sourceClass == this.getSourceClass() && targetClass == this.getTargetClass();
        }

        public T convert(Collection obj) throws ConversionException {
            byte[] array = new byte[obj.size()];
            int index = 0;
            Iterator<Byte> iterator = obj.iterator();
            while (iterator.hasNext()) {
                array[index] = iterator.next().byteValue();
                index++;
            }
            return Util.<T>cast(array);
        }
    }

    @SuppressWarnings("unchecked")
    private static class CollectionToCharArray<T> extends AbstractConverter<Collection, T> {
        public CollectionToCharArray(Class<Collection> sourceClass, Class<T> targetClass) {
            super(sourceClass, targetClass);
        }

        public boolean canConvert(Class<?> sourceClass, Class<?> targetClass) {
            return sourceClass == this.getSourceClass() && targetClass == this.getTargetClass();
        }

        public T convert(Collection obj) throws ConversionException {
            char[] array = new char[obj.size()];
            int index = 0;
            Iterator<Character> iterator = obj.iterator();
            while (iterator.hasNext()) {
                array[index] = iterator.next().charValue();
                index++;
            }
            return Util.<T>cast(array);
        }
    }

    @SuppressWarnings("unchecked")
    private static class CollectionToDoubleArray<T> extends AbstractConverter<Collection, T> {
        public CollectionToDoubleArray(Class<Collection> sourceClass, Class<T> targetClass) {
            super(sourceClass, targetClass);
        }

        public boolean canConvert(Class<?> sourceClass, Class<?> targetClass) {
            return sourceClass == this.getSourceClass() && targetClass == this.getTargetClass();
        }

        public T convert(Collection obj) throws ConversionException {
            double[] array = new double[obj.size()];
            int index = 0;
            Iterator<Double> iterator = obj.iterator();
            while (iterator.hasNext()) {
                array[index] = iterator.next().doubleValue();
                index++;
            }
            return Util.<T>cast(array);
        }
    }

    @SuppressWarnings("unchecked")
    private static class CollectionToFloatArray<T> extends AbstractConverter<Collection, T> {
        public CollectionToFloatArray(Class<Collection> sourceClass, Class<T> targetClass) {
            super(sourceClass, targetClass);
        }

        public boolean canConvert(Class<?> sourceClass, Class<?> targetClass) {
            return sourceClass == this.getSourceClass() && targetClass == this.getTargetClass();
        }

        public T convert(Collection obj) throws ConversionException {
            float[] array = new float[obj.size()];
            int index = 0;
            Iterator<Float> iterator = obj.iterator();
            while (iterator.hasNext()) {
                array[index] = iterator.next().floatValue();
                index++;
            }
            return Util.<T>cast(array);
        }
    }

    @SuppressWarnings("unchecked")
    private static class CollectionToIntArray<T> extends AbstractConverter<Collection, T> {
        public CollectionToIntArray(Class<Collection> sourceClass, Class<T> targetClass) {
            super(sourceClass, targetClass);
        }

        public boolean canConvert(Class<?> sourceClass, Class<?> targetClass) {
            return sourceClass == this.getSourceClass() && targetClass == this.getTargetClass();
        }

        public T convert(Collection obj) throws ConversionException {
            int[] array = new int[obj.size()];
            int index = 0;
            Iterator<Integer> iterator = obj.iterator();
            while (iterator.hasNext()) {
                array[index] = iterator.next().intValue();
                index++;
            }
            return Util.<T>cast(array);
        }
    }

    @SuppressWarnings("unchecked")
    private static class CollectionToLongArray<T> extends AbstractConverter<Collection, T> {
        public CollectionToLongArray(Class<Collection> sourceClass, Class<T> targetClass) {
            super(sourceClass, targetClass);
        }

        public boolean canConvert(Class<?> sourceClass, Class<?> targetClass) {
            return sourceClass == this.getSourceClass() && targetClass == this.getTargetClass();
        }

        public T convert(Collection obj) throws ConversionException {
            long[] array = new long[obj.size()];
            int index = 0;
            Iterator<Long> iterator = obj.iterator();
            while (iterator.hasNext()) {
                array[index] = iterator.next().longValue();
                index++;
            }
            return Util.<T>cast(array);
        }
    }

    @SuppressWarnings("unchecked")
    private static class CollectionToObjectArray<T> extends AbstractConverter<Collection, T> {
        public CollectionToObjectArray(Class<Collection> sourceClass, Class<T> targetClass) {
            super(sourceClass, targetClass);
        }

        public boolean canConvert(Class<?> sourceClass, Class<?> targetClass) {
            return sourceClass == this.getSourceClass() && targetClass == this.getTargetClass();
        }

        public T convert(Collection obj) throws ConversionException {
            return Util.<T>cast(obj.toArray());
        }
    }

    @SuppressWarnings("unchecked")
    private static class CollectionToShortArray<T> extends AbstractConverter<Collection, T> {
        public CollectionToShortArray(Class<Collection> sourceClass, Class<T> targetClass) {
            super(sourceClass, targetClass);
        }

        public boolean canConvert(Class<?> sourceClass, Class<?> targetClass) {
            return sourceClass == this.getSourceClass() && targetClass == this.getTargetClass();
        }

        public T convert(Collection obj) throws ConversionException {
            short[] array = new short[obj.size()];
            int index = 0;
            Iterator<Short> iterator = obj.iterator();
            while (iterator.hasNext()) {
                array[index] = iterator.next().shortValue();
                index++;
            }
            return Util.<T>cast(array);
        }
    }

    public static class ListCreator implements ConverterCreator, ConverterLoader {
        public <S, T> Converter<S, T> createConverter(Class<S> sourceClass, Class<T> targetClass) {
            if (!sourceClass.isArray()) {
               return null;
            }
            if (!List.class.isAssignableFrom(targetClass)) {
               return null;
            }
            if (!(sourceClass.getComponentType() instanceof Object)) {
                return null;
            }
            if ((targetClass.getModifiers() & Modifier.ABSTRACT) == 0) {
                return Util.cast(new ArrayClassToList<S, T>(sourceClass, targetClass));
            } else {
                return Util.cast(new ArrayClassToArrayList<S, T>(sourceClass, targetClass));
            }
        }

        public void loadConverters() {
            Converters.registerCreator(this);
        }
    }

    public static class SetCreator implements ConverterCreator, ConverterLoader {
        public <S, T> Converter<S, T> createConverter(Class<S> sourceClass, Class<T> targetClass) {
            if (!sourceClass.isArray()) {
               return null;
            }
            if (!Set.class.isAssignableFrom(targetClass)) {
               return null;
            }
            if (!(sourceClass.getComponentType() instanceof Object)) {
                return null;
            }
            if ((targetClass.getModifiers() & Modifier.ABSTRACT) == 0) {
                return Util.cast(new ArrayClassToSet<S, T>(sourceClass, targetClass));
            } else {
                return Util.cast(new ArrayClassToHashSet<S, T>(sourceClass, targetClass));
            }
        }

        public void loadConverters() {
            Converters.registerCreator(this);
        }
    }
}
