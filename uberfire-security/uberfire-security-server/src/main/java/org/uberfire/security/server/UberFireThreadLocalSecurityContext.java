/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.security.server;

import java.util.HashMap;
import java.util.Map;

public class UberFireThreadLocalSecurityContext {

    private static final ThreadLocal<Map<Key, Object>> THREAD_LOCAL_SECURITY_CONTEXT = new ThreadLocal<Map<Key, Object>>();

    private UberFireThreadLocalSecurityContext() {
    }

    public static void put( Key key,
                            Object payload ) {
        if ( THREAD_LOCAL_SECURITY_CONTEXT.get() == null ) {
            THREAD_LOCAL_SECURITY_CONTEXT.set( new HashMap<Key, Object>() );
        }
        THREAD_LOCAL_SECURITY_CONTEXT.get().put( key, payload );
    }

    public static Object get( Key key ) {
        if ( THREAD_LOCAL_SECURITY_CONTEXT.get() == null ) {
            return null;
        } else {
            return THREAD_LOCAL_SECURITY_CONTEXT.get().get( key );
        }
    }

    public static void cleanupThread() {
        THREAD_LOCAL_SECURITY_CONTEXT.remove();
    }

    public enum Key {
        HTTP_SESSION;
    }
}
