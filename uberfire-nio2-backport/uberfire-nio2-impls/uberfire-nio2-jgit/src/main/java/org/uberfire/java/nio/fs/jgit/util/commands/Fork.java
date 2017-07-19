/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.java.nio.fs.jgit.util.commands;

import java.io.File;

import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.internal.ketch.KetchLeaderCache;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.java.nio.fs.jgit.util.Git;
import org.uberfire.java.nio.fs.jgit.util.exceptions.GitException;

import static org.uberfire.commons.validation.PortablePreconditions.*;

public class Fork {

    private static final String DOT_GIT_EXT = ".git";
    private final KetchLeaderCache leaders;
    private Logger logger = LoggerFactory.getLogger( Fork.class );

    private File parentFolder;
    private final String source;
    private final String target;
    private CredentialsProvider credentialsProvider;

    public Fork( final File parentFolder,
                 final String source,
                 final String target,
                 final CredentialsProvider credentialsProvider,
                 final KetchLeaderCache leaders ) {
        this.parentFolder = checkNotNull( "parentFolder", parentFolder );
        this.source = checkNotEmpty( "source", source );
        this.target = checkNotEmpty( "target", target );
        this.credentialsProvider = checkNotNull( "credentialsProvider", credentialsProvider );
        this.leaders = leaders;
    }

    public Git execute() throws InvalidRemoteException {

        if ( logger.isDebugEnabled() ) {
            logger.debug( "Forking repository <{}> to <{}>", source, target );
        }

        final File origin = new File( parentFolder, source + DOT_GIT_EXT );
        final File destination = new File( parentFolder, target + DOT_GIT_EXT );

        if ( destination.exists() ) {
            String message = String.format( "Cannot fork because destination repository <%s> already exists", target );
            logger.error( message );
            throw new GitException( message );
        }

        return Git.clone( destination, origin.toPath().toUri().toString(), false, credentialsProvider, leaders );
    }
}
