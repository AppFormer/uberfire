package org.uberfire.io.regex;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.uberfire.io.CommonIOServiceDotFileTest;
import org.uberfire.io.IOService;
import org.uberfire.io.impl.IOServiceDotFileImpl;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.Paths;

import static org.uberfire.io.regex.AntPathMatcher.*;

public class AntPathMatcherTest {

    final static IOService ioService = new IOServiceDotFileImpl();
    private static File path = null;

    @BeforeClass
    public static void setup() throws IOException {
        path = CommonIOServiceDotFileTest.createTempDirectory();
        System.setProperty( "org.uberfire.nio.git.dir", path.getAbsolutePath() );
        System.out.println( ".niogit: " + path.getAbsolutePath() );

        final URI newRepo = URI.create( "git://antpathmatcher" );

        ioService.newFileSystem( newRepo, new HashMap<String, Object>() );
    }

    @AfterClass
    @BeforeClass
    public static void cleanup() {
        if ( path != null ) {
            FileUtils.deleteQuietly( path );
        }
    }

    @Test
    public void testIncludes() {
        final Collection<String> patterns = new ArrayList<String>() {{
            add( "git://**" );
            add( "**/repo/**" );
        }};

        {
            final Path path = Paths.get( URI.create( "file:///Users/home" ) );
            Assert.assertFalse( includes( patterns, path ) );
        }

        {
            final Path path = Paths.get( URI.create( "git://antpathmatcher" ) );
            Assert.assertTrue( includes( patterns, path ) );
        }

        {
            final Path path = Paths.get( URI.create( "git://master@antpathmatcher" ) );
            Assert.assertTrue( includes( patterns, path ) );
        }
    }

    @Test
    public void testIncludesMid() {
        final Collection<String> patterns = new ArrayList<String>() {{
            add( "default://**" );
            add( "**/repo/**" );
        }};

        {
            final Path path = Paths.get( URI.create( "file:///Users/home" ) );
            Assert.assertTrue( includes( patterns, path ) );
        }

        {
            final Path path = Paths.get( URI.create( "git://antpathmatcher" ) );
            Assert.assertFalse( includes( patterns, path ) );
        }

        {
            final Path path = Paths.get( URI.create( "git://master@antpathmatcher/repo/sss" ) );
            Assert.assertTrue( includes( patterns, path ) );
        }
    }

    @Test
    public void testExcludes() {
        final Collection<String> patterns = new ArrayList<String>() {{
            add( "git://**" );
            add( "**/repo/**" );
        }};

        {
            final Path path = Paths.get( URI.create( "file:///Users/home" ) );
            Assert.assertFalse( excludes( patterns, path ) );
        }

        {
            final Path path = Paths.get( URI.create( "git://antpathmatcher" ) );
            Assert.assertTrue( excludes( patterns, path ) );
        }

        {
            final Path path = Paths.get( URI.create( "git://master@antpathmatcher" ) );
            Assert.assertTrue( excludes( patterns, path ) );
        }
    }

    @Test
    public void testExcludesMid() {
        final Collection<String> patterns = new ArrayList<String>() {{
            add( "default://**" );
            add( "**/repo/**" );
        }};

        {
            final Path path = Paths.get( URI.create( "file:///Users/home" ) );
            Assert.assertTrue( excludes( patterns, path ) );
        }

        {
            final Path path = Paths.get( URI.create( "git://antpathmatcher" ) );
            Assert.assertFalse( excludes( patterns, path ) );
        }

        {
            final Path path = Paths.get( URI.create( "git://master@antpathmatcher/repo/sss" ) );
            Assert.assertTrue( excludes( patterns, path ) );
        }
    }

    @Test
    public void testFilter() {
        final Collection<String> includes = new ArrayList<String>() {{
            add( "git://**" );
        }};
        final Collection<String> excludes = new ArrayList<String>() {{
            add( "default://**" );
        }};

        {
            final Path path = Paths.get( URI.create( "file:///Users/home" ) );
            Assert.assertFalse( filter( includes, excludes, path ) );
        }

        {
            final Path path = Paths.get( URI.create( "git://antpathmatcher" ) );
            Assert.assertTrue( filter( includes, excludes, path ) );
        }

        {
            final Path path = Paths.get( URI.create( "git://master@antpathmatcher/repo/sss" ) );
            Assert.assertTrue( filter( includes, excludes, path ) );
        }

        Assert.assertTrue( filter( Collections.<String>emptyList(), Collections.<String>emptyList(), Paths.get( URI.create( "git://master@antpathmatcher/repo/sss" ) ) ) );
        Assert.assertTrue( filter( Collections.<String>emptyList(), Collections.<String>emptyList(), Paths.get( URI.create( "git://antpathmatcher" ) ) ) );
    }

    @Test
    public void testIncludesUri() {
        final Collection<String> patterns = new ArrayList<String>() {{
            add( "git://**" );
            add( "**/repo/**" );
        }};

        Assert.assertFalse( includes( patterns, URI.create( "file:///Users/home" ) ) );

        Assert.assertTrue( includes( patterns, URI.create( "git://antpathmatcher" ) ) );

        Assert.assertTrue( includes( patterns, URI.create( "git://master@antpathmatcher" ) ) );
    }

    @Test
    public void testIncludesMidUri() {
        final Collection<String> patterns = new ArrayList<String>() {{
            add( "file://**" );
            add( "**/repo/**" );
        }};

        Assert.assertTrue( includes( patterns, URI.create( "file:///Users/home" ) ) );

        Assert.assertFalse( includes( patterns, URI.create( "git://antpathmatcher" ) ) );

        Assert.assertTrue( includes( patterns, URI.create( "git://master@antpathmatcher/repo/sss" ) ) );
    }

    @Test
    public void testExcludesUri() {
        final Collection<String> patterns = new ArrayList<String>() {{
            add( "git://**" );
            add( "**/repo/**" );
        }};

        Assert.assertFalse( excludes( patterns, URI.create( "file:///Users/home" ) ) );

        Assert.assertTrue( excludes( patterns, URI.create( "git://antpathmatcher" ) ) );

        Assert.assertTrue( excludes( patterns, URI.create( "git://master@antpathmatcher" ) ) );
    }

    @Test
    public void testExcludesMidUri() {
        final Collection<String> patterns = new ArrayList<String>() {{
            add( "file://**" );
            add( "**/repo/**" );
        }};

        Assert.assertTrue( excludes( patterns, URI.create( "file:///Users/home" ) ) );

        Assert.assertFalse( excludes( patterns, URI.create( "git://antpathmatcher" ) ) );

        Assert.assertTrue( excludes( patterns, URI.create( "git://master@antpathmatcher/repo/sss" ) ) );
    }

    @Test
    public void testFilterUri() {
        final Collection<String> includes = new ArrayList<String>() {{
            add( "git://**" );
        }};
        final Collection<String> excludes = new ArrayList<String>() {{
            add( "file://**" );
        }};

        Assert.assertFalse( filter( includes, excludes, URI.create( "file:///Users/home" ) ) );

        Assert.assertTrue( filter( includes, excludes, URI.create( "git://antpathmatcher" ) ) );

        Assert.assertTrue( filter( includes, excludes, URI.create( "git://master@antpathmatcher/repo/sss" ) ) );

        Assert.assertTrue( filter( Collections.<String>emptyList(), Collections.<String>emptyList(), URI.create( "file:///Users/home" ) ) );

        Assert.assertTrue( filter( Collections.<String>emptyList(), Collections.<String>emptyList(), URI.create( "git://master@antpathmatcher/repo/sss" ) ) );

    }
}
