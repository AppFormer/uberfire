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

package org.uberfire.client.workbench;

import javax.enterprise.context.Dependent;

import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.preferences.PreferenceStorage;
import org.uberfire.preferences.ResolutionStrategy;
import org.uberfire.preferences.Scope;

@Dependent
public class PreferenceStorageServiceClientImpl implements PreferenceStorage {

    //LOCAL STORAGE?!

    @Override
    public <T> void read( final Scope store,
                          final String key,
                          final ResolutionStrategy resolutionStrategy,
                          final ParameterizedCommand<T> value ) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> void read( final Scope scope,
                          final String key,
                          final ParameterizedCommand<T> callback ) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void write( final Scope store,
                       final String key,
                       final Object value ) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete( final Scope store,
                        final String key ) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete( final String key ) {
        throw new UnsupportedOperationException();
    }

}
