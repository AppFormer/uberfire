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
import org.uberfire.preferences.Scope;
import org.uberfire.preferences.ScopeType;
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
                       final ScopeType[] resolutionOrder ) {
        //        for ( Scope scope : resolutionOrder ) {
//            Object result = forScope( scope ).get( key );
//            if ( result != null ) {
//                return result;
//            }
//        }
//
//        value.execute( null );
        return null;
    }

    @Override
    public Object read( final Scope store,
                        final String key ) {
        Path path = fileSystem.getPath( buildStoragePath( store, key ) );
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
                          final ScopeType[] resolutionOrder,
                          final ParameterizedCommand<T> value ) {
        value.execute( (T) read( store, key, resolutionOrder ) );
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
            Path path = fileSystem.getPath( buildStoragePath( store, key ) );
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
        ioService.deleteIfExists( fileSystem.getPath( buildStoragePath( store, key ) ) );
    }

    @Override
    public void delete( final String key ) {

    }

    private String buildStoragePath( final Scope store,
                                     final String key ) {
        final Scope scope;
        if ( store instanceof PreferenceStore ) {
            scope = ( (PreferenceStore) store ).defaultScope();
        } else if ( store instanceof PreferenceStore.ScopedPreferenceStore ) {
            scope = ( (PreferenceStore.ScopedPreferenceStore) store ).scope();
        } else {
            throw new RuntimeException( "Invalid Store" );
        }
        final String path;
        if ( scope.getType().equals( DefaultScopeTypes.USER ) ) {
            path = sessionInfo.getIdentity().getIdentifier();
        } else if ( scope.getType().equals( DefaultScopeTypes.GLOBAL ) ) {
            path = DefaultScopeTypes.GLOBAL.toString().toLowerCase();
        } else {
            path = scope.key();
        }
        return "/config/" + path + "/" + key + ".preferences";
    }

}
