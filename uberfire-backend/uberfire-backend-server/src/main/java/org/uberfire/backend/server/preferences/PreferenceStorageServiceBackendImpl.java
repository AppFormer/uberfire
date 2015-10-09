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

package org.uberfire.backend.server.preferences;

import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.thoughtworks.xstream.XStream;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.Path;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.preferences.DefaultScopeTypes;
import org.uberfire.preferences.PreferenceStorage;
import org.uberfire.preferences.PreferenceStorageService;
import org.uberfire.preferences.PreferenceStore;
import org.uberfire.preferences.ResolutionStrategy;
import org.uberfire.preferences.Scope;
import org.uberfire.rpc.SessionInfo;

@ApplicationScoped
public class PreferenceStorageServiceBackendImpl implements PreferenceStorageService,
                                                            PreferenceStorage {

    @Inject
    @Named("configIO")
    private IOService ioService;

    @Inject
    private SessionInfo sessionInfo;

    @Inject
    @Named("systemFS")
    private FileSystem fileSystem;

    private final XStream xs = new XStream();

    @Override
    public <T> T read( final Scope scope,
                       final String key,
                       final ResolutionStrategy resolutionStrategy ) {
        List<Scope> scopes = resolutionStrategy.order( scope );
        for ( Scope currentScope : scopes ) {
            Object result = read( currentScope, key );
            if ( result != null ) {
                return (T) result;
            }
        }
        return null;
    }

    @Override
    public Object read( final Scope store,
                        final String key ) {
        Path path = buildStoragePath( store, key );
        try {
            if ( ioService.exists( path ) ) {
                String content = ioService.readAllString( path );
                return xs.fromXML( content );
            }
        } catch ( final Exception e ) {
            throw new RuntimeException( e );
        }
        return null;
    }

    @Override
    public <T> void read( final Scope store,
                          final String key,
                          final ResolutionStrategy resolutionStrategy,
                          final ParameterizedCommand<T> value ) {
        value.execute( (T) read( store, key, resolutionStrategy ) );
    }

    @Override
    public <T> void read( final Scope store,
                          final String key,
                          final ParameterizedCommand<T> value ) {
        value.execute( (T) read( store, key ) );
    }

    @Override
    public void write( final Scope store,
                       final String key,
                       final Object value ) {
        try {
            ioService.startBatch( fileSystem );
            Path path = buildStoragePath( store, key );
            ioService.write( path, xs.toXML( value ) );
        } catch ( final Exception e ) {
            throw new RuntimeException( e );
        } finally {
            ioService.endBatch();
        }

    }

    @Override
    public void delete( final Scope store,
                        final String key ) {
        ioService.deleteIfExists( buildStoragePath( store, key ) );
    }

    @Override
    public void delete( final String key ) {

    }

    private Path buildStoragePath( final Scope scope,
                                   final String key ) {

        final String path;
        if ( scope.equals( DefaultScopeTypes.USER.toScope() ) ) {
            path = "uf-user-" + sessionInfo.getIdentity().getIdentifier();
        } else {
            path = scope.key();
        }

        return fileSystem.getPath( path, "/config/" + key + ".preferences" );
    }

}
