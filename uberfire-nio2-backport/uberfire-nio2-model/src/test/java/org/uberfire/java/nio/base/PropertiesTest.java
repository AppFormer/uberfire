package org.uberfire.java.nio.base;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 */
public class PropertiesTest {

    @Test
    public void testState() throws IOException {
        final File fcontent = File.createTempFile( "foo", "bar" );
        final Properties properties = new Properties();
        final Date dt = new Date();

        properties.put( "int", 10453 );
        properties.put( "long", 1000000L );
        properties.put( "date", dt );

        final OutputStream out = new FileOutputStream( fcontent );
        properties.store( out );

        final Properties loadProperties = new Properties();

        final InputStream in = new FileInputStream( fcontent );
        loadProperties.load( in );

        assertNotNull( properties.get( "int" ) );
        assertNotNull( properties.get( "long" ) );
        assertNotNull( properties.get( "date" ) );

        assertEquals( 10453, properties.get( "int" ) );
        assertEquals( 1000000L, properties.get( "long" ) );
        assertEquals( dt, properties.get( "date" ) );

    }

    @Test
    public void testEmptyState() throws IOException {
        final File fcontent = File.createTempFile( "foo2", "bar" );
        final Properties loadProperties = new Properties();

        final InputStream in = new FileInputStream( fcontent );
        loadProperties.load( in );

        assertEquals( 0, loadProperties.size() );
    }

}
