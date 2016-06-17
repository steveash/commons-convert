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

/** Utility methods. */
public class Util {

    /** Convenience method to cast parameterized types.
     * 
     * @param <V> The type to cast to
     * @param object The object to cast
     * @return <code>obj</code> cast to type <code>V</code>
     */
    @SuppressWarnings("unchecked")
    public static <V> V cast(Object object) {
        return (V) object;
    }

    /**
     * Tests if a class is the same class as, or sub-class of, or implements <code>typeClass</code>.
     * @param objectClass Class to test
     * @param typeClass Class to test against
     * @return <code>true</code> if <code>objectClass</code> is the same class as, or sub-class of, or implements <code>typeClass</code>
     */
    public static boolean instanceOf(Class<?> objectClass, Class<?> typeClass) {
        if (objectClass == typeClass) {
            return true;
        }
        if (objectClass.isInterface()) {
            if (typeClass.isInterface()) {
                // objectClass == interface, typeClass == interface
                Class<?>[] ifaces = objectClass.getInterfaces();
                for (Class<?> iface: ifaces) {
                    if (iface == typeClass) {
                        return true;
                    }
                }
            } else {
                // objectClass == interface, typeClass != interface
                Class<?>[] ifaces = typeClass.getInterfaces();
                for (Class<?> iface: ifaces) {
                    if (iface == objectClass) {
                        return true;
                    }
                }
            }
        } else {
            if (typeClass.isInterface()) {
                // objectClass != interface, typeClass == interface
                while (objectClass != null) {
                    Class<?>[] ifaces = objectClass.getInterfaces();
                    for (Class<?> iface: ifaces) {
                        if (iface == typeClass) {
                            return true;
                        }
                    }
                    objectClass = objectClass.getSuperclass();
                }
            } else {
                // objectClass != interface, typeClass != interface
                while (objectClass != null) {
                    if (objectClass == typeClass) {
                        return true;
                    }
                    objectClass = objectClass.getSuperclass();
                }
            }
        }
        return false;
    }

    /** Returns <code>true</code> if <code>str</code> is <code>null</code>
     * or empty.
     * 
     * @param str The <code>String</code> to test
     * @return <code>true</code> if <code>str</code> is <code>null</code>
     * or empty
     */
    public static boolean isEmpty(String str) {
        return str == null || str.trim().length() == 0;
    }

    private Util() {}
}
