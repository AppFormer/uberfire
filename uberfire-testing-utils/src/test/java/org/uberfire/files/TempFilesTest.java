package org.uberfire.files;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class TempFilesTest {

    private TempFiles tempFiles;

    @Before
    public void setUp() throws Exception {
        tempFiles = new TempFiles();
    }

    @Test
    public void testDirectory() throws Exception {
        final File tempDirectory = tempFiles.createTempDirectory( "mydir" );

        assertTrue( tempDirectory.exists() );

        tempFiles.deleteFiles();

        assertFalse( tempDirectory.exists() );
    }

    @Test
    public void testFiles() throws Exception {
        final File pomFile = tempFiles.createTempFile( "mydir/pom.xml" );
        final File javaClass = tempFiles.createTempFile( "mydir/org/test/Person.java" );

        assertTrue( pomFile.exists() );
        assertTrue( javaClass.exists() );

        tempFiles.deleteFiles();

        assertFalse( pomFile.exists() );
        assertFalse( javaClass.exists() );

    }
}