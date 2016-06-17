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

import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.imageio.spi.ServiceRegistry;

/** A <code>Converter</code> factory and utility class.
 * <p>The factory loads {@link org.apache.commons.convert.Converter} instances
 * via the Java service provider registry. Applications can extend the
 * converter framework by:<br />
 * <ul>
 * <li>Creating classes that implement the
 * {@link org.apache.commons.convert.Converter} class</li>
 * <li>Create a {@link org.apache.commons.convert.ConverterLoader} class that
 * registers those classes</li>
 * <li>Create a <code>META-INF/services/org.apache.commons.convert.ConverterLoader</code>
 * file that contains the {@link org.apache.commons.convert.ConverterLoader} class name(s)</li>
 * </ul></p>
 * <p>To convert an object, call the static {@link #getConverter(Class, Class)} method to
 * get a {@link org.apache.commons.convert.Converter} instance, then call the converter's
 * {@link org.apache.commons.convert.Converter#convert(Object)} method.
 * </p>
 */
public class Converters {
    protected static final String DELIMITER = "->";
    protected static final ConcurrentHashMap<String, Converter<?, ?>> converterMap = new ConcurrentHashMap<String, Converter<?, ?>>();
    protected static final Set<ConverterCreator> creators = Collections.synchronizedSet(new HashSet<ConverterCreator>());
    protected static final Set<String> noConversions = Collections.synchronizedSet(new HashSet<String>());

    static {
        registerCreator(new PassThruConverterCreator());
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        Iterator<ConverterLoader> converterLoaders = ServiceRegistry.lookupProviders(ConverterLoader.class, loader);
        while (converterLoaders.hasNext()) {
            try {
                ConverterLoader converterLoader = converterLoaders.next();
                converterLoader.loadConverters();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private Converters() {}


    /** Returns an appropriate <code>Converter</code> instance for
     * <code>sourceClass</code> and <code>targetClass</code>. If no matching
     * <code>Converter</code> is found, the method throws
     * <code>ClassNotFoundException</code>.
     *
     * <p>This method is intended to be used when the source or
     * target <code>Object</code> types are unknown at compile time.
     * If the source and target <code>Object</code> types are known
     * at compile time, then the appropriate converter instance should be used.</p>
     *
     * @param sourceClass The object class to convert from
     * @param targetClass The object class to convert to
     * @return A matching <code>Converter</code> instance
     * @throws ClassNotFoundException
     */
    public static <S, T> Converter<S, T> getConverter(Class<S> sourceClass, Class<T> targetClass) throws ClassNotFoundException {
        String key = sourceClass.getName().concat(DELIMITER).concat(targetClass.getName());
        OUTER:
            do {
                Converter<?, ?> result = converterMap.get(key);
                if (result != null) {
                    return Util.cast(result);
                }
                if (noConversions.contains(key)) {
                    throw new ClassNotFoundException("No converter found for " + key);
                }
                Class<?> foundSourceClass = null;
                Converter<?, ?> foundConverter = null;
                for (Converter<?, ?> value : converterMap.values()) {
                    if (value.canConvert(sourceClass, targetClass)) {
                        // this converter can deal with the source/target pair
                        if (foundSourceClass == null || foundSourceClass.isAssignableFrom(value.getSourceClass())) {
                            // remember the current target source class; if we find another converter, check
                            // to see if it's source class is assignable to this one, and if so, it means it's
                            // a child class, so we'll then take that converter.
                            foundSourceClass = value.getSourceClass();
                            foundConverter = value;
                        }
                    }
                }
                if (foundConverter != null) {
                    converterMap.putIfAbsent(key, foundConverter);
                    continue OUTER;
                }
                for (ConverterCreator value : creators) {
                    result = createConverter(value, sourceClass, targetClass);
                    if (result != null) {
                        converterMap.putIfAbsent(key, result);
                        continue OUTER;
                    }
                }
                noConversions.add(key);
                throw new ClassNotFoundException("No converter found for " + key);
            } while (true);
    }

    private static <S, SS extends S, T, TT extends T> Converter<SS, TT> createConverter(ConverterCreator creater, Class<SS> sourceClass, Class<TT> targetClass) {
        return creater.createConverter(sourceClass, targetClass);
    }

    /** Load all classes that implement {@link org.apache.commons.convert.Converter} and are
     * contained in <code>containerClass</code>.
     *
     * @param containerClass A class that contains {@link org.apache.commons.convert.Converter}
     * implementations
     */
    public static void loadContainedConverters(Class<?> containerClass) {
        // This only returns public classes and interfaces
        for (Class<?> clz: containerClass.getClasses()) {
            try {
                // non-abstract, which means no interfaces or abstract classes
                if ((clz.getModifiers() & Modifier.ABSTRACT) == 0) {
                    Object value;
                    try {
                        value = clz.getConstructor().newInstance();
                    } catch (NoSuchMethodException e) {
                        // ignore this, as this class might be some other helper class,
                        // with a non-pubilc constructor
                        continue;
                    }
                    if (value instanceof ConverterLoader) {
                        ConverterLoader loader = (ConverterLoader) value;
                        loader.loadConverters();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /** Registers a <code>ConverterCreator</code> instance to be used by the
     * {@link org.apache.commons.convert.Converters#getConverter(Class, Class)}
     * method, when a converter can't be found.
     *
     * @param <S> The source object type
     * @param <T> The target object type
     * @param creator The <code>ConverterCreator</code> instance to register
     */
    public static <S, T> void registerCreator(ConverterCreator creator) {
        creators.add(creator);
    }

    /** Registers a <code>Converter</code> instance to be used by the
     * {@link org.apache.commons.convert.Converters#getConverter(Class, Class)}
     * method.
     *
     * @param <S> The source object type
     * @param <T> The target object type
     * @param converter The <code>Converter</code> instance to register
     */
    public static <S, T> void registerConverter(Converter<S, T> converter) {
        registerConverter(converter, converter.getSourceClass(), converter.getTargetClass());
    }

    /** Registers a <code>Converter</code> instance to be used by the
     * {@link org.apache.commons.convert.Converters#getConverter(Class, Class)}
     * method.
     * 
     * @param <S> The source object type
     * @param <T> The target object type
     * @param converter
     * @param sourceClass
     * @param targetClass
     */
    public static <S, T> void registerConverter(Converter<S, T> converter, Class<?> sourceClass, Class<?> targetClass) {
        StringBuilder sb = new StringBuilder();
        if (sourceClass != null) {
            sb.append(sourceClass.getName());
        } else {
            sb.append("<null>");
        }
        sb.append(DELIMITER);
        if (targetClass != null) {
            sb.append(targetClass.getName());
        } else {
            sb.append("<null>");
        }
        String key = sb.toString();
        converterMap.putIfAbsent(key, converter);
    }

    protected static class PassThruConverterCreator implements ConverterCreator{
        protected PassThruConverterCreator() {
        }

        public <S, T> Converter<S, T> createConverter(Class<S> sourceClass, Class<T> targetClass) {
            if (sourceClass == targetClass || targetClass == Object.class || Util.instanceOf(sourceClass, targetClass)) {
                return new PassThruConverter<S, T>(sourceClass, targetClass);
            } else {
                return null;
            }
        }
    }

    /** Pass-through converter used when the source and target java object
     * types are the same. The <code>convert</code> method returns the
     * source object.
     *
     */
    protected static class PassThruConverter<S, T> implements Converter<S, T> {
        private final Class<S> sourceClass;
        private final Class<T> targetClass;

        public PassThruConverter(Class<S> sourceClass, Class<T> targetClass) {
            this.sourceClass = sourceClass;
            this.targetClass = targetClass;
        }

        public boolean canConvert(Class<?> sourceClass, Class<?> targetClass) {
            return this.sourceClass == sourceClass && this.targetClass == targetClass;
        }

        @SuppressWarnings("unchecked")
        public T convert(S obj) throws ConversionException {
            return (T) obj;
        }

        public Class<?> getSourceClass() {
            return sourceClass;
        }

        public Class<?> getTargetClass() {
            return targetClass;
        }
    }
}
