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

package org.uberfire.backend.server.util;

import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.util.URIUtil;
import org.kie.commons.io.IOService;
import org.kie.commons.java.nio.file.attribute.FileTime;
import org.uberfire.backend.vfs.FileSystem;
import org.uberfire.backend.vfs.FileSystemFactory;
import org.uberfire.backend.vfs.Path;

import static org.uberfire.backend.vfs.PathFactory.*;

@ApplicationScoped
public class Paths {

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    private Map<org.kie.commons.java.nio.file.FileSystem, FileSystem> cache = new HashMap<org.kie.commons.java.nio.file.FileSystem, FileSystem>();

    public Path convert( final org.kie.commons.java.nio.file.Path path,
                         final boolean readAttrrs ) {
        if ( path == null ) {
            return null;
        }

        final Map<String, Object> attributes;
        if ( readAttrrs ) {
            attributes = ioService.readAttributes( path, "basic:isRegularFile,isDirectory,size,lastModifiedTime,creationTime" );
            //TODO {porcelli} HACK! visit here when dealing with nio2 optimizations
            attributes.put( "lastModifiedTime", new Date( ( (FileTime) attributes.get( "lastModifiedTime" ) ).toMillis() ) );
            attributes.put( "creationTime", new Date( ( (FileTime) attributes.get( "creationTime" ) ).toMillis() ) );
        } else {
            attributes = null;
        }

        if ( path.getFileName() == null ) {
            if ( attributes == null ) {
                return newPath( convert( path.getFileSystem() ), "/", path.toUri().toString() );
            }
            return newPath( convert( path.getFileSystem() ), "/", path.toUri().toString(), attributes );
        }

        if ( attributes == null ) {
            return newPath( convert( path.getFileSystem() ), path.getFileName().toString(), path.toUri().toString() );
        }

        return newPath( convert( path.getFileSystem() ), path.getFileName().toString(), path.toUri().toString(), attributes );
    }

    public Path convert( final org.kie.commons.java.nio.file.Path path ) {
        return convert( path, true );
    }

    public org.kie.commons.java.nio.file.Path convert( final Path path ) {
        if ( path == null ) {
            return null;
        }

        try {
            return ioService.get( URI.create( path.toURI() ) );
        } catch ( IllegalArgumentException e ) {
            try {
                return ioService.get( URI.create( URIUtil.encodePath( path.toURI() ) ) );
            } catch ( URIException ex ) {
                return null;
            }
        }
    }

    public FileSystem convert( final org.kie.commons.java.nio.file.FileSystem fs ) {
        if ( !cache.containsKey( fs ) ) {
            final Map<String, String> roots = new HashMap<String, String>();
            for ( final org.kie.commons.java.nio.file.Path root : fs.getRootDirectories() ) {
                roots.put( root.toUri().toString(), root.getFileName() == null ? "/" : root.getFileName().toString() );
            }
            cache.put( fs, FileSystemFactory.newFS( roots, fs.supportedFileAttributeViews() ) );
        }

        return cache.get( fs );
    }

}
