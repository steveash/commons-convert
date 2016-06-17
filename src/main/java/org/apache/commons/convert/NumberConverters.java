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
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.TimeZone;

/** Number Converter classes. */
public class NumberConverters implements ConverterLoader {

    protected static final Class<?>[] classArray = {BigDecimal.class, BigInteger.class, Byte.class, Double.class, Integer.class, Float.class, Long.class, Short.class};

    protected static Number fromString(String str, NumberFormat nf) throws ConversionException {
        try {
            return nf.parse(str);
        } catch (ParseException e) {
            throw new ConversionException(e);
        }
    }

    protected static <S, T> void registerConverter(Converter<S, T> converter) {
        if (converter.getSourceClass() != converter.getTargetClass()) {
            Converters.registerConverter(converter);
        }
    }

    @SuppressWarnings("unchecked")
    public void loadConverters() {
        Converters.loadContainedConverters(NumberConverters.class);
        for (Class<?> sourceClass : classArray) {
            registerConverter(new GenericNumberToBigDecimal(sourceClass));
            registerConverter(new GenericNumberToBigInteger(sourceClass));
            registerConverter(new GenericNumberToByte(sourceClass));
            registerConverter(new GenericNumberToDouble(sourceClass));
            registerConverter(new GenericNumberToInteger(sourceClass));
            registerConverter(new GenericNumberToFloat(sourceClass));
            registerConverter(new GenericNumberToLong(sourceClass));
            registerConverter(new GenericNumberToShort(sourceClass));
            registerConverter(new GenericSingletonToList(sourceClass));
            registerConverter(new GenericSingletonToSet(sourceClass));
        }
    }

    /**
     * An abstract <code>Number</code> to <code>String</code> converter class
     * that implements some of the <code>LocalizedConverter</code> methods. 
     */
    public static abstract class AbstractNumberToStringConverter<N extends Number> extends AbstractLocalizedConverter<N, String> {
        public AbstractNumberToStringConverter(Class<N> sourceClass) {
            super(sourceClass, String.class);
        }

        public String convert(N obj) throws ConversionException {
            return obj.toString();
        }

        public String convert(N obj, Locale locale, TimeZone timeZone, String formatString) throws ConversionException {
            if (formatString == null) {
                return format(obj, NumberFormat.getNumberInstance(locale));
            } else {
                return format(obj, new DecimalFormat(formatString));
            }
        }

        protected abstract String format(N obj, NumberFormat nf) throws ConversionException;
    }

    /**
     * An abstract <code>String</code> to <code>Number</code> converter class
     * that implements some of the <code>LocalizedConverter</code> methods. 
     */
    public static abstract class AbstractStringToNumberConverter<N extends Number> extends AbstractLocalizedConverter<String, N> {
        public AbstractStringToNumberConverter(Class<N> targetClass) {
            super(String.class, targetClass);
        }

        protected abstract N convert(Number number) throws ConversionException;

        public N convert(String obj, Locale locale, TimeZone timeZone, String formatString) throws ConversionException {
            if (formatString == null) {
                return convert(fromString(obj, NumberFormat.getNumberInstance(locale)));
            } else {
                return convert(fromString(obj, new DecimalFormat(formatString)));
            }
        }
    }

    /**
     * An object that converts a <code>BigDecimal</code> to a
     * <code>String</code>.
     */
    public static class BigDecimalToString extends AbstractNumberToStringConverter<BigDecimal> {
        public BigDecimalToString() {
            super(BigDecimal.class);
        }

        protected String format(BigDecimal obj, NumberFormat nf) throws ConversionException {
            return nf.format(obj.doubleValue());
        }
    }

    /**
     * An object that converts a <code>BigInteger</code> to a
     * <code>String</code>.
     */
    public static class BigIntegerToString extends AbstractNumberToStringConverter<BigInteger> {
        public BigIntegerToString() {
            super(BigInteger.class);
        }

        protected String format(BigInteger obj, NumberFormat nf) throws ConversionException {
            return nf.format(obj.doubleValue());
        }
    }

    /**
     * An object that converts a <code>Byte</code> to a
     * <code>String</code>.
     */
    public static class ByteToString extends AbstractNumberToStringConverter<Byte> {
        public ByteToString() {
            super(Byte.class);
        }

        protected String format(Byte obj, NumberFormat nf) throws ConversionException {
            return nf.format(obj.floatValue());
        }
    }

    /**
     * An object that converts a <code>Double</code> to a
     * <code>String</code>.
     */
    public static class DoubleToString extends AbstractNumberToStringConverter<Double> {
        public DoubleToString() {
            super(Double.class);
        }

        protected String format(Double obj, NumberFormat nf) throws ConversionException {
            return nf.format(obj.doubleValue());
        }
    }

    /**
     * An object that converts a <code>Float</code> to a
     * <code>String</code>.
     */
    public static class FloatToString extends AbstractNumberToStringConverter<Float> {
        public FloatToString() {
            super(Float.class);
        }

        protected String format(Float obj, NumberFormat nf) throws ConversionException {
            return nf.format(obj.floatValue());
        }
    }

    /**
     * An object that converts a <code>Number</code> to a
     * <code>BigDecimal</code>.
     */
    public static class GenericNumberToBigDecimal<N extends Number> extends AbstractConverter<N, BigDecimal> {
        public GenericNumberToBigDecimal(Class<N> sourceClass) {
            super(sourceClass, BigDecimal.class);
        }

        public BigDecimal convert(N obj) throws ConversionException {
            return new BigDecimal(obj.doubleValue());
        }
    }

    /**
     * An object that converts a <code>Number</code> to a
     * <code>BigInteger</code>.
     */
    public static class GenericNumberToBigInteger<N extends Number> extends AbstractConverter<N, BigInteger> {
        public GenericNumberToBigInteger(Class<N> sourceClass) {
            super(sourceClass, BigInteger.class);
        }

        public BigInteger convert(N obj) throws ConversionException {
            return BigInteger.valueOf(obj.longValue());
        }
    }

    /**
     * An object that converts a <code>Number</code> to a
     * <code>Byte</code>.
     */
    public static class GenericNumberToByte<N extends Number> extends AbstractConverter<N, Byte> {
        public GenericNumberToByte(Class<N> sourceClass) {
            super(sourceClass, Byte.class);
        }

        public Byte convert(N obj) throws ConversionException {
            return Byte.valueOf(obj.byteValue());
        }
    }

    /**
     * An object that converts a <code>Number</code> to a
     * <code>Double</code>.
     */
    public static class GenericNumberToDouble<N extends Number> extends AbstractConverter<N, Double> {
        public GenericNumberToDouble(Class<N> sourceClass) {
            super(sourceClass, Double.class);
        }

        public Double convert(N obj) throws ConversionException {
            return obj.doubleValue();
        }
    }

    /**
     * An object that converts a <code>Number</code> to a
     * <code>Float</code>.
     */
    public static class GenericNumberToFloat<N extends Number> extends AbstractConverter<N, Float> {
        public GenericNumberToFloat(Class<N> sourceClass) {
            super(sourceClass, Float.class);
        }

        public Float convert(N obj) throws ConversionException {
            return obj.floatValue();
        }
    }

    /**
     * An object that converts a <code>Number</code> to an
     * <code>Integer</code>.
     */
    public static class GenericNumberToInteger<N extends Number> extends AbstractConverter<N, Integer> {
        public GenericNumberToInteger(Class<N> sourceClass) {
            super(sourceClass, Integer.class);
        }

        public Integer convert(N obj) throws ConversionException {
            return obj.intValue();
        }
    }

    /**
     * An object that converts a <code>Number</code> to a
     * <code>Long</code>.
     */
    public static class GenericNumberToLong<N extends Number> extends AbstractConverter<N, Long> {
        public GenericNumberToLong(Class<N> sourceClass) {
            super(sourceClass, Long.class);
        }

        public Long convert(N obj) throws ConversionException {
            return obj.longValue();
        }
    }

    /**
     * An object that converts a <code>Number</code> to a
     * <code>Short</code>.
     */
    public static class GenericNumberToShort<N extends Number> extends AbstractConverter<N, Short> {
        public GenericNumberToShort(Class<N> sourceClass) {
            super(sourceClass, Short.class);
        }

        public Short convert(N obj) throws ConversionException {
            return obj.shortValue();
        }
    }

    /**
     * An object that converts an <code>Integer</code> to a
     * <code>String</code>.
     */
    public static class IntegerToString extends AbstractNumberToStringConverter<Integer> {
        public IntegerToString() {
            super(Integer.class);
        }

        protected String format(Integer obj, NumberFormat nf) throws ConversionException {
            return nf.format(obj.intValue());
        }
    }

    /**
     * An object that converts a <code>Long</code> to a
     * <code>BigDecimal</code>.
     */
    public static class LongToBigDecimal extends AbstractConverter<Long, BigDecimal> {
        public LongToBigDecimal() {
            super(Long.class, BigDecimal.class);
        }

        public BigDecimal convert(Long obj) throws ConversionException {
            return BigDecimal.valueOf(obj.longValue());
        }
    }

    /**
     * An object that converts a <code>Long</code> to a
     * <code>String</code>.
     */
    public static class LongToString extends AbstractNumberToStringConverter<Long> {
        public LongToString() {
            super(Long.class);
        }

        protected String format(Long obj, NumberFormat nf) throws ConversionException {
            return nf.format(obj.longValue());
        }
    }

    /**
     * An object that converts a <code>Short</code> to a
     * <code>String</code>.
     */
    public static class ShortToString extends AbstractNumberToStringConverter<Short> {
        public ShortToString() {
            super(Short.class);
        }

        protected String format(Short obj, NumberFormat nf) throws ConversionException {
            return nf.format(obj.floatValue());
        }
    }

    /**
     * An object that converts a <code>String</code> to a
     * <code>BigDecimal</code>.
     */
    public static class StringToBigDecimal extends AbstractStringToNumberConverter<BigDecimal> {
        public StringToBigDecimal() {
            super(BigDecimal.class);
        }

        protected BigDecimal convert(Number number) throws ConversionException {
            return BigDecimal.valueOf(number.doubleValue());
        }

        public BigDecimal convert(String obj) throws ConversionException {
            return BigDecimal.valueOf(Double.valueOf(obj));
        }
    }

    /**
     * An object that converts a <code>String</code> to a
     * <code>BigInteger</code>.
     */
    public static class StringToBigInteger extends AbstractStringToNumberConverter<BigInteger> {
        public StringToBigInteger() {
            super(BigInteger.class);
        }

        protected BigInteger convert(Number number) throws ConversionException {
            return BigInteger.valueOf(number.longValue());
        }

        public BigInteger convert(String obj) throws ConversionException {
            return new BigInteger(obj);
        }
    }

    /**
     * An object that converts a <code>String</code> to a
     * <code>Byte</code>.
     */
    public static class StringToByte extends AbstractConverter<String, Byte> {
        public StringToByte() {
            super(String.class, Byte.class);
        }

        public Byte convert(String obj) throws ConversionException {
            return Byte.valueOf(obj);
        }
    }

    /**
     * An object that converts a <code>String</code> to a
     * <code>Double</code>.
     */
    public static class StringToDouble extends AbstractStringToNumberConverter<Double> {
        public StringToDouble() {
            super(Double.class);
        }

        protected Double convert(Number number) throws ConversionException {
            return number.doubleValue();
        }

        public Double convert(String obj) throws ConversionException {
            return Double.valueOf(obj);
        }
    }

    /**
     * An object that converts a <code>String</code> to a
     * <code>Float</code>.
     */
    public static class StringToFloat extends AbstractStringToNumberConverter<Float> {
        public StringToFloat() {
            super(Float.class);
        }

        protected Float convert(Number number) throws ConversionException {
            return number.floatValue();
        }

        public Float convert(String obj) throws ConversionException {
            return Float.valueOf(obj);
        }
    }

    /**
     * An object that converts a <code>String</code> to an
     * <code>Integer</code>.
     */
    public static class StringToInteger extends AbstractStringToNumberConverter<Integer> {
        public StringToInteger() {
            super(Integer.class);
        }

        protected Integer convert(Number number) throws ConversionException {
            return number.intValue();
        }

        public Integer convert(String obj) throws ConversionException {
            return Integer.valueOf(obj);
        }
    }

    /**
     * An object that converts a <code>String</code> to a
     * <code>Long</code>.
     */
    public static class StringToLong extends AbstractStringToNumberConverter<Long> {
        public StringToLong() {
            super(Long.class);
        }

        protected Long convert(Number number) throws ConversionException {
            return number.longValue();
        }

        public Long convert(String obj) throws ConversionException {
            return Long.valueOf(obj);
        }
    }

    /**
     * An object that converts a <code>String</code> to a
     * <code>Short</code>.
     */
    public static class StringToShort extends AbstractConverter<String, Short> {
        public StringToShort() {
            super(String.class, Short.class);
        }

        public Short convert(String obj) throws ConversionException {
            return Short.valueOf(obj);
        }
    }
}
