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
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/** java.net Converter classes. */
public class NetConverters implements ConverterLoader {

    public void loadConverters() {
        Converters.loadContainedConverters(NetConverters.class);
        Converters.registerConverter(new GenericToStringConverter<URI>(URI.class));
        Converters.registerConverter(new GenericToStringConverter<URL>(URL.class));
        Converters.registerConverter(new GenericSingletonToList<InetAddress>(InetAddress.class));
        Converters.registerConverter(new GenericSingletonToList<URI>(URI.class));
        Converters.registerConverter(new GenericSingletonToList<URL>(URL.class));
        Converters.registerConverter(new GenericSingletonToSet<InetAddress>(InetAddress.class));
        Converters.registerConverter(new GenericSingletonToSet<URI>(URI.class));
        Converters.registerConverter(new GenericSingletonToSet<URL>(URL.class));
    }

    /**
     * An object that converts an <code>InetAddress</code> to a
     * <code>String</code>.
     */
    public static class InetAddressToString extends AbstractConverter<InetAddress, String> {
        public InetAddressToString() {
            super(InetAddress.class, String.class);
        }

        public String convert(InetAddress obj) throws ConversionException {
            String hostName = obj.getHostName();
            if (hostName != null) return hostName;
            return obj.getHostAddress();
        }
    }

    /**
     * An object that converts a <code>String</code> to an
     * <code>InetAddress</code>.
     */
    public static class StringToInetAddress extends AbstractConverter<String, InetAddress> {
        public StringToInetAddress() {
            super(String.class, InetAddress.class);
        }

        public InetAddress convert(String obj) throws ConversionException {
            try {
                return InetAddress.getByName(obj);
            } catch (IOException e) {
                throw (ConversionException) new ConversionException(e.getMessage()).initCause(e);
            }
        }
    }

    /**
     * An object that converts a <code>String</code> to a
     * <code>URI</code>.
     */
    public static class StringToURI extends AbstractConverter<String, URI> {
        public StringToURI() {
            super(String.class, URI.class);
        }

        public URI convert(String obj) throws ConversionException {
            try {
                return new URI(obj);
            } catch (URISyntaxException e) {
                throw (ConversionException) new ConversionException(e.getMessage()).initCause(e);
            }
        }
    }

    /**
     * An object that converts a <code>String</code> to a
     * <code>URL</code>.
     */
    public static class StringToURL extends AbstractConverter<String, URL> {
        public StringToURL() {
            super(String.class, URL.class);
        }

        public URL convert(String obj) throws ConversionException {
            try {
                return new URL(obj);
            } catch (MalformedURLException e) {
                throw (ConversionException) new ConversionException(e.getMessage()).initCause(e);
            }
        }
    }

    /**
     * An object that converts a <code>URI</code> to a
     * <code>URL</code>.
     */
    public static class URIToURL extends AbstractConverter<URI, URL> {
        public URIToURL() {
            super(URI.class, URL.class);
        }

        public URL convert(URI obj) throws ConversionException {
            try {
                return obj.toURL();
            } catch (MalformedURLException e) {
                throw (ConversionException) new ConversionException(e.getMessage()).initCause(e);
            }
        }
    }

    /**
     * An object that converts a <code>URL</code> to a
     * <code>URI</code>.
     */
    public static class URLToURI extends AbstractConverter<URL, URI> {
        public URLToURI() {
            super(URL.class, URI.class);
        }

        public URI convert(URL obj) throws ConversionException {
            try {
                return obj.toURI();
            } catch (URISyntaxException e) {
                throw (ConversionException) new ConversionException(e.getMessage()).initCause(e);
            }
        }
    }
}
