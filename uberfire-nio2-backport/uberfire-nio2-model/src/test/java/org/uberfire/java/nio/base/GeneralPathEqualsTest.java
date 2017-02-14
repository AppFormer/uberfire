/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.java.nio.base;

import org.junit.Before;
import org.junit.Test;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.Path;

import static org.fest.assertions.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.uberfire.java.nio.base.AbstractPath.OSType;

public class GeneralPathEqualsTest {

    final FileSystem fs = mock(FileSystem.class);
    final FileSystem nfs = mock(FileSystem.class);

    @Before
    public void setup() {
        when(fs.getSeparator()).thenReturn("/");
        when(nfs.getSeparator()).thenReturn("/");
    }

    @Test
    public void testEquals() {
        final Path path = GeneralPathImpl.create(fs, "/path/to/file.txt", false);
        assertThat(path).isEqualTo(path);

        assertThat(path.equals(new Object())).isFalse();

        assertThat(path).isNotEqualTo(GeneralPathImpl.create(fs, "/path/to/file.txt", true));
        assertThat(path).isNotEqualTo(GeneralPathImpl.create(fs, "path/to/file.txt", false));
        assertThat(path).isNotEqualTo(GeneralPathImpl.create(fs, "/path/to/file.txts", false));
        assertThat(path).isNotEqualTo(GeneralPathImpl.create(nfs, "/path/to/file.txts", false));
        assertThat(path.getRoot()).isNotEqualTo(path);
    }

    @Test
    public void testEqualsWindows() {
        final Path path = GeneralPathImpl.create(fs, "path/to/file.txt", false);
        final Path wpath = GeneralPathImpl.create(fs, "path\\to\\file.txt", false);

        assertThat(path).isNotEqualTo(wpath);
    }

    @Test
    public void testHashCode() {
        final Path path = GeneralPathImpl.create(fs, "/path/to/file.txt", false);
        assertThat(path.hashCode()).isEqualTo(path.hashCode());

        assertThat(path.hashCode()).isNotEqualTo(new Object().hashCode());

        assertThat(path.hashCode()).isNotEqualTo(GeneralPathImpl.create(fs, "/path/to/file.txt", true).hashCode());
        assertThat(path.hashCode()).isNotEqualTo(GeneralPathImpl.create(fs, "path/to/file.txt", false).hashCode());
        assertThat(path.hashCode()).isNotEqualTo(GeneralPathImpl.create(fs, "/path/to/file.txts", false).hashCode());
        assertThat(path.hashCode()).isNotEqualTo(GeneralPathImpl.create(nfs, "/path/to/file.txts", false).hashCode());
        assertThat(path.getRoot().hashCode()).isNotEqualTo(path.hashCode());
    }

    @Test
    public void testHashCodeWindows() {
        final Path path = GeneralPathImpl.create(fs, "path/to/file.txt", false);
        final Path wpath = GeneralPathImpl.create(fs, "path\\to\\file.txt", false);

        assertThat(path.hashCode()).isNotEqualTo(wpath.hashCode());
    }

    @Test
    public void testSeparators() {
        final Path path1 = GeneralPathImpl.create(fs, "/path/to/file.txt", false);
        printPath(path1);
        final Path path2 = GeneralPathImpl.create(fs, "/path\\to/file.txt", false);
        printPath(path2);
        assertThat( path1.equals( path2 ) );

        final Path path3 = GeneralPathImpl.create(fs, "\\path/to/file.txt", false);
        printPath(path3);
        assertThat( !path2.equals( path3 ) );

        final Path path4 = GeneralPathImpl.create(fs, "file:///C:/path/to/file.txt", false);
        printPath(path4);
        final Path path5 = GeneralPathImpl.create(fs, "file:///C:\\path\\to\\file.txt", false);
        printPath(path5);
        assertThat( !path4.equals( path5 ) );
        
        final Path path6 = GeneralPathImpl.create(fs, "C:/path/to/file.txt", false);
        printPath(path6);
        final Path path7 = GeneralPathImpl.create(fs, "/C:/path/", false);
        printPath(path7);
        assertThat( path6.startsWith( path7 ) );
     
        final Path path8 = GeneralPathImpl.create(fs, "to/file.txt", false);
        assertThat( path7.resolve( path8 ).equals( path6 ) ); 
        printPath( path7.resolve( path8 ) );
        
        if ( OSType.currentOS() == OSType.WINDOWS ) {
            assertThat( AbstractPath.getSeparator( "path" ).equals( "\\" ) );
            assertThat( AbstractPath.getSeparator().equals( "\\" ) );
        }
        else {
            assertThat( AbstractPath.getSeparator( "path" ).equals( "/" ) );
            assertThat( AbstractPath.getSeparator().equals( "/" ) );
        }
        assertThat( AbstractPath.getSeparator( "/path\\to\\file.txt" ).equals( "/" ) );
        assertThat( AbstractPath.getSeparator( "\\path/to/file.txt" ).equals( "\\" ) );
        
        assertThat( AbstractPath.removeTrailingSeparator( "/path/to/folder/" ).equals( "/path/to/folder" ) );
        assertThat( AbstractPath.appendTrailingSeparator( "/path/to/folder" ).equals( "/path/to/folder/" ) );
    }
    
    private void printPath( Path path )
    {
        System.out.println( path.toString() );
//        System.out.println( path.toUri().toString() );
    }
}
