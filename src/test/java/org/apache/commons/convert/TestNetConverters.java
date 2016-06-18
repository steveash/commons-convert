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

import java.net.InetAddress;
import java.net.URI;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.commons.convert.Converter;
import org.apache.commons.convert.Converters;
import org.apache.commons.convert.NetConverters;

import junit.framework.TestCase;

public class TestNetConverters extends TestCase {

    @SuppressWarnings("unchecked")
    public static <S, T> void assertConversion(String label, Converter<S, T> converter, S source, T target) throws Exception {
        assertTrue(label + " can convert", converter.canConvert(source.getClass(), target.getClass()));
        assertEquals(label + " registered", converter.getClass(), Converters.getConverter(source.getClass(), target.getClass()).getClass());
        assertEquals(label + " converted", target, converter.convert(source));
        Converter<T, S> reflectiveConverter = null;
        try {
            reflectiveConverter = (Converter<T, S>) Converters.getConverter(target.getClass(), source.getClass());
            assertEquals(label + " reflection converted", source, reflectiveConverter.convert(target));
        } catch (UnconvertableException e) {
            System.out.println(converter.getClass() + " not reflective");
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

    public TestNetConverters(String name) {
        super(name);
    }

    public void testNetConverters() throws Exception {
        ConverterLoader loader = new NetConverters();
        loader.loadConverters();
        InetAddress address = InetAddress.getLocalHost();
        String strAddress = address.getHostName();
        String strURI = "mailto:dev@commons.apache.org";
        URI uri = new URI(strURI);
        String strURL = "http://www.apache.org/index.html";
        URL url = new URL(strURL);
        assertConversion("InetAddressToString", new NetConverters.InetAddressToString(), address, strAddress);
        assertToCollection("InetAddressToCollection", address);
        assertConversion("UriToString", new GenericToStringConverter<URI>(URI.class), uri, strURI);
        assertToCollection("UriToCollection", uri);
        assertConversion("UrlToString", new GenericToStringConverter<URL>(URL.class), url, strURL);
        assertToCollection("UrlToCollection", url);
        assertConversion("UriToUrl", new NetConverters.URIToURL(), url.toURI(), url);
    }
}
