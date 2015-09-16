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
import org.uberfire.preferences.ScopeType;

import static org.uberfire.commons.validation.PortablePreconditions.*;

public class PreferenceStoreImpl implements PreferenceStore {

    private final PreferenceStorage storage;
    private final ScopeType[] resolutionOrder;
    private final Scope defaultScope;

    public PreferenceStoreImpl( final PreferenceStorage storage,
                                final Scope defaultScope,
                                final ScopeType... resolutionOrder ) {
        this.storage = checkNotNull( "storage", storage );
        this.defaultScope = checkNotNull( "defaultScope", defaultScope );
        this.resolutionOrder = resolutionOrder;
    }

    @Override
    public ScopedPreferenceStore forScope( final Scope scope ) {
        return new ScopedPreferenceStoreImpl( storage, scope );
    }

    @Override
    public Scope defaultScope() {
        return defaultScope;
    }

    @Override
    public ScopeType[] resolutionOrder() {
        return resolutionOrder;
    }

    @Override
    public void put( String key,
                     Object value ) {
        forScope( this.defaultScope ).put( key, value );
    }

    @Override
    public <T> void get( final String key,
                         final Class<T> clazz,
                         final ParameterizedCommand<T> callback ) {
        storage.read( defaultScope, key, resolutionOrder, callback );
    }

    @Override
    public <T> void get( final String key,
                         final ParameterizedCommand<T> callback ) {
        storage.read( defaultScope, key, resolutionOrder, callback );
    }

    @Override
    public void remove( final String key ) {
        storage.delete( defaultScope, key );
    }

    @Override
    public void clear( final String key ) {
        storage.delete( key );
    }

}
