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

package org.uberfire.java.nio.fs.jgit;

import java.io.File;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.uberfire.java.nio.file.FileSystemNotFoundException;

import static org.fest.assertions.api.Assertions.*;

/**
 * Created by aparedes on 9/16/16.
 */
public class JGitFileSystemProviderMigrationTest extends AbstractTestInfra {

    @Test
    public void testCreateANewDirectoryWithMigrationEnv() {

        final Map<String, ?> envMigrate = new HashMap<String, Object>() {{
            put( "init", Boolean.TRUE );
            put( "migrate-from", URI.create( "git://old" ) );
        }};

        String newPath = "git://test/old";
        final URI newUri = URI.create( newPath );
        provider.newFileSystem( newUri, envMigrate );

        provider.getFileSystem( newUri );
        assertThat( new File( provider.getGitRepoContainerDir(), "test/old" + ".git" ).exists() ).isTrue();
        assertThat( provider.getFileSystem( newUri ) ).isNotNull();

    }

    @Test
    public void testMigrateOldDirectories() {

        final Map<String, ?> env = new HashMap<String, Object>() {{
            put( "init", Boolean.TRUE );
        }};

        final Map<String, ?> envMigrate = new HashMap<String, Object>() {{
            put( "init", Boolean.TRUE );
            put( "migrate-from", URI.create( "git://old" ) );
        }};

        String oldPath = "git://old";
        final URI oldUri = URI.create( oldPath );
        final JGitFileSystem fs = (JGitFileSystem) provider.newFileSystem( oldUri, env );

        String newPath = "git://test/old";
        final URI newUri = URI.create( newPath );
        provider.newFileSystem( newUri, envMigrate );

        try {
            provider.getFileSystem( oldUri );
            fail( "It should not reach here because old filesystem does not exists" );
        } catch ( FileSystemNotFoundException ex ) {
            assertThat( new File( provider.getGitRepoContainerDir(), "test/old" + ".git" ).exists() ).isTrue();
            assertThat( new File( provider.getGitRepoContainerDir(), "old" + ".git" ).exists() ).isFalse();
        }

    }

}
