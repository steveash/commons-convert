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

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.sql.Blob;
import java.sql.Clob;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Pattern;

/** Miscellaneous Converter classes. */
public class MiscConverters implements ConverterLoader {

    public void loadConverters() {
        Converters.loadContainedConverters(MiscConverters.class);
        Converters.registerConverter(new GenericToStringConverter<Locale>(Locale.class));
        Converters.registerConverter(new GenericToStringConverter<UUID>(UUID.class));
        Converters.registerConverter(new GenericToStringConverter<Pattern>(Pattern.class));
    }

    /**
     * An object that converts a <code>java.sql.Blob</code> to a
     * byte array.
     */
    public static class BlobToByteArray extends AbstractConverter<Blob, byte[]> {
        public BlobToByteArray() {
            super(Blob.class, byte[].class);
        }

        public byte[] convert(Blob obj) throws ConversionException {
            InputStream inStream = null;
            try {
                inStream = obj.getBinaryStream();
                int blobLength = (int) obj.length();
                byte[] byteBuffer = new byte[blobLength];
                int offset = 0;
                int bytesRead = inStream.read(byteBuffer, offset, blobLength);
                while (bytesRead > 0) {
                    offset += bytesRead;
                    bytesRead = inStream.read(byteBuffer, offset, blobLength);
                }
                return byteBuffer;
            } catch (Exception e) {
                throw new ConversionException(e);
            }
            finally {
                if (inStream != null) {
                    try {
                        inStream.close();
                    } catch (IOException e) {}
                }
            }
        }
    }

    /**
     * An object that converts a byte array to a
     * <code>ByteBuffer</code>.
     */
    public static class ByteArrayToByteBuffer extends AbstractConverter<byte[], ByteBuffer> {
        public ByteArrayToByteBuffer() {
            super(byte[].class, ByteBuffer.class);
        }

        public ByteBuffer convert(byte[] obj) throws ConversionException {
            try {
                return ByteBuffer.wrap(obj);
            } catch (Exception e) {
                throw new ConversionException(e);
            }
        }
    }

    /**
     * An object that converts a <code>ByteBuffer</code> to a
     * byte array.
     */
    public static class ByteBufferToByteArray extends AbstractConverter<ByteBuffer, byte[]> {
        public ByteBufferToByteArray() {
            super(ByteBuffer.class, byte[].class);
        }

        public byte[] convert(ByteBuffer obj) throws ConversionException {
            try {
                return obj.hasArray() ? obj.array() : null;
            } catch (Exception e) {
                throw new ConversionException(e);
            }
        }
    }

    /**
     * An object that converts a <code>Charset</code> to a
     * character set name <code>String</code>.
     */
    public static class CharsetToString extends AbstractConverter<Charset, String> {
        public CharsetToString() {
            super(Charset.class, String.class);
        }

        public String convert(Charset obj) throws ConversionException {
            return obj.name();
        }
    }

    /**
     * An object that converts a <code>java.sql.Clob</code> to a
     * <code>String</code>.
     */
    public static class ClobToString extends AbstractConverter<Clob, String> {
        public ClobToString() {
            super(Clob.class, String.class);
        }

        public String convert(Clob obj) throws ConversionException {
            Reader clobReader = null;
            try {
                clobReader = obj.getCharacterStream();
                int clobLength = (int) obj.length();
                char[] charBuffer = new char[clobLength];
                int offset = 0;
                int charsRead = clobReader.read(charBuffer, offset, clobLength);
                while (charsRead > 0) {
                    offset += charsRead;
                    charsRead = clobReader.read(charBuffer, offset, clobLength);
                }
                return new String(charBuffer);
            } catch (Exception e) {
                throw new ConversionException(e);
            }
            finally {
                if (clobReader != null) {
                    try {
                        clobReader.close();
                    } catch (IOException e) {}
                }
            }
        }
    }

    /**
     * An object that converts a <code>DecimalFormat</code> to a
     * format pattern <code>String</code>.
     */
    public static class DecimalFormatToString extends AbstractConverter<DecimalFormat, String> {
        public DecimalFormatToString() {
            super(DecimalFormat.class, String.class);
        }

        public String convert(DecimalFormat obj) throws ConversionException {
            return obj.toPattern();
        }
    }

    public static class EnumToString extends AbstractConverter<Enum<?>, String> {
        public EnumToString() {
            super(Enum.class, String.class);
        }

        public boolean canConvert(Class<?> sourceClass, Class<?> targetClass) {
            return Enum.class.isAssignableFrom(sourceClass) && String.class.isAssignableFrom(targetClass);
        }

        public String convert(Enum<?> obj) throws ConversionException {
            return obj.toString();
        }

        public Class<? super Enum<?>> getSourceClass() {
            return null;
        }
    }

    /**
     * A class used for testing <code>ConverterLoader</code>
     * implementations.
     */
    public static class NotAConverter {
        protected NotAConverter() {
            throw new Error("Should not be loaded");
        }
    }

    /**
     * An object that converts a <code>SimpleDateFormat</code> to a
     * format pattern <code>String</code>.
     */
    public static class SimpleDateFormatToString extends AbstractConverter<SimpleDateFormat, String> {
        public SimpleDateFormatToString() {
            super(SimpleDateFormat.class, String.class);
        }

        public String convert(SimpleDateFormat obj) throws ConversionException {
            return obj.toPattern();
        }
    }

    /**
     * An object that converts a character set name <code>String</code> to a
     * <code>Charset</code>.
     */
    public static class StringToCharset extends AbstractConverter<String, Charset> {
        public StringToCharset() {
            super(String.class, Charset.class);
        }

        public Charset convert(String obj) throws ConversionException {
            return Charset.forName(obj);
        }
    }

    /**
     * An object that converts a format pattern <code>String</code> to a
     * <code>DecimalFormat</code>.
     */
    public static class StringToDecimalFormat extends AbstractConverter<String, DecimalFormat> {
        public StringToDecimalFormat() {
            super(String.class, DecimalFormat.class);
        }

        public DecimalFormat convert(String obj) throws ConversionException {
            return new DecimalFormat(obj);
        }
    }

    private static class StringToEnum<E extends Enum<E>> extends AbstractConverter<String, E> {
        public StringToEnum() {
            super(String.class, Enum.class);
        }

        public boolean canConvert(Class<?> sourceClass, Class<?> targetClass) {
            return String.class.isAssignableFrom(sourceClass) && Enum.class.isAssignableFrom(targetClass);
        }

        public E convert(String obj) throws ConversionException {
            throw new UnsupportedOperationException();
        }

        public Class<? super Enum> getTargetClass() {
            return null;
        }
    }

    public static class StringToEnumConverterCreator implements ConverterCreator, ConverterLoader {
        public <S, T> Converter<S, T> createConverter(Class<S> sourceClass, Class<T> targetClass) {
            if (String.class == sourceClass && Enum.class.isAssignableFrom(targetClass)) {
                return Util.cast(new StringToEnum());
            } else {
                return null;
            }
        }

        public void loadConverters() {
            Converters.registerCreator(this);
        }
    }

    /**
     * An object that converts a <code>Locale</code> ID
     * <code>String</code> to a <code>Locale</code>.
     */
    public static class StringToLocale extends AbstractConverter<String, Locale> {
        public StringToLocale() {
            super(String.class, Locale.class);
        }

        public Locale convert(String obj) throws ConversionException {
            return new Locale(obj);
        }
    }

    /**
     * An object that converts a pattern <code>String</code> to a
     * <code>Pattern</code>.
     */
    public static class StringToRegexPattern extends AbstractConverter<String, Pattern> {
        public StringToRegexPattern() {
            super(String.class, Pattern.class);
        }

        public Pattern convert(String obj) throws ConversionException {
            return Pattern.compile(obj);
        }
    }

    /**
     * An object that converts a format <code>String</code> to a
     * <code>SimpleDateFormat</code>.
     */
    public static class StringToSimpleDateFormat extends AbstractConverter<String, SimpleDateFormat> {
        public StringToSimpleDateFormat() {
            super(String.class, SimpleDateFormat.class);
        }

        public SimpleDateFormat convert(String obj) throws ConversionException {
            return new SimpleDateFormat(obj);
        }
    }

    /**
     * An object that converts a UUID <code>String</code> to a
     * <code>UUID</code>.
     */
    public static class StringToUUID extends AbstractConverter<String, UUID> {
        public StringToUUID() {
            super(String.class, UUID.class);
        }

        public UUID convert(String obj) throws ConversionException {
            return UUID.fromString(obj);
        }
    }
}
