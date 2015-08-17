/*
 *   Copyright 2015 JBoss Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package org.uberfire.preferences.impl;

import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.preferences.PreferenceStorage;
import org.uberfire.preferences.PreferenceStore;
import org.uberfire.preferences.Scope;

import static org.uberfire.commons.validation.PortablePreconditions.*;

public class ScopedPreferenceStoreImpl implements PreferenceStore.ScopedPreferenceStore {

    private final PreferenceStorage storage;
    private final Scope scope;

    public ScopedPreferenceStoreImpl( final PreferenceStorage storage,
                                      final Scope scope ) {
        this.storage = checkNotNull( "storage", storage );
        this.scope = checkNotNull( "scope", scope );
    }

    @Override
    public Scope scope() {
        return scope;
    }

    @Override
    public void put( final String key,
                     final Object value ) {
        storage.write( scope, key, value );
    }

    @Override
    public <T> void get( final String key,
                         final Class<T> clazz,
                         final ParameterizedCommand<T> callback ) {
        checkNotEmpty( "key", key );
        checkNotNull( "clazz", clazz );
        checkNotNull( "callback", callback );

        get( key, new ParameterizedCommand<T>() {
            @Override
            public void execute( final T value ) {
                callback.execute( value );
            }
        } );
    }

    @Override
    public <T> void get( final String key,
                         final ParameterizedCommand<T> callback ) {
        checkNotEmpty( "key", key );
        checkNotNull( "callback", callback );

        storage.read( scope, key, callback );
    }

    @Override
    public void remove( final String key ) {
        checkNotEmpty( "key", key );
        storage.delete( scope, key );
    }
}
