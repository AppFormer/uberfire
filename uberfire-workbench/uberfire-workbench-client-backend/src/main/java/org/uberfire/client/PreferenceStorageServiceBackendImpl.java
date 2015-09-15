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

package org.uberfire.client;

import javax.enterprise.inject.Alternative;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.preferences.PreferenceStorage;
import org.uberfire.preferences.PreferenceStorageService;
import org.uberfire.preferences.Store;

@Alternative
public class PreferenceStorageServiceBackendImpl implements PreferenceStorage {

    @Inject
    private Caller<PreferenceStorageService> preferenceStorage;

    @Override
    public <T> void read( final Store store,
                          final String key,
                          final ParameterizedCommand<T> value ) {
        preferenceStorage.call( new RemoteCallback<T>() {
            @Override
            public void callback( final T o ) {
                value.execute( o );
            }
        } );
    }

    @Override
    public void write( final Store store,
                       final String key,
                       final Object value ) {
        preferenceStorage.call().write( store, key, value );

    }

    @Override
    public void delete( final Store store,
                        final String key ) {
        preferenceStorage.call().delete( store, key );

    }
}