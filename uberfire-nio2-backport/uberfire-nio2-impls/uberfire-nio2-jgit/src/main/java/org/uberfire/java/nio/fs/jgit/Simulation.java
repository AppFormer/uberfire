package org.uberfire.java.nio.fs.jgit;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.uberfire.java.nio.fs.jgit.util.commands.Commit;
import org.uberfire.java.nio.fs.jgit.util.commands.WriteConfiguration;

public class Simulation {

    private static final BufferedReader br = new BufferedReader( new InputStreamReader( System.in ) );
    private static Map<String, JGitFileSystem> repos = new HashMap<>();
    private static char[] NAMES = "ABCDEFGHI".toCharArray();

    public static class KetchInstanceConfig {

        private final int port;
        private final String name;
        private final Map<String, String> others = new HashMap<>( 2 );

        public KetchInstanceConfig( final int port,
                                    final String name,
                                    final Map<String, String> others ) {
            this.port = port;
            this.name = name;
            this.others.putAll( others );
        }

        public int getPort() {
            return port;
        }

        public String getName() {
            return name;
        }

        public Map<String, String> getOthers() {
            return others;
        }

    }

    public static void main( final String... args ) throws IOException, InterruptedException {
        final File baseDir = new File( "/Users/porcelli/ketch_test" );
        FileUtils.deleteDirectory( baseDir );
        Files.createDirectory( baseDir.toPath() );

        int instanceCount;
        while ( true ) {
            System.out.print( "Number of Instances... :" );
            final String instance = br.readLine();
            try {
                instanceCount = Integer.valueOf( instance );
                if ( instanceCount <= NAMES.length ) {
                    break;
                }
            } catch ( final NumberFormatException ignored ) {
            }
        }

        int port = 9422;
        final Set<ServerInstance> servers = new HashSet<>( instanceCount );
        for ( int i = 0; i < instanceCount; i++ ) {
            final ServerInstance serverInstance = new ServerInstance( NAMES[ i ], port++ );
            servers.add( serverInstance );
        }

        final Map<String, JGitFileSystemProvider> threadMap = new HashMap<>();
        for ( final ServerInstance activeServer : servers ) {
            final Thread thread = new Thread( () -> {
                try {
                    final JGitFileSystemProvider provider = new KetchInstanceSetup().execute( baseDir, new KetchInstanceConfig( activeServer.getPort(), activeServer.getName(), new HashMap<String, String>() {{
                        for ( ServerInstance guestServer : servers ) {
                            if ( !guestServer.getName().equals( activeServer.getName() ) ) {
                                put( guestServer.getName(), String.valueOf( guestServer.getPort() ) );
                            }
                        }
                    }} ) );
                    threadMap.put( activeServer.getName(), provider );
                } catch ( Exception e ) {
                    e.printStackTrace();
                }
            } );
            thread.run();
        }

        System.out.print( "Ready for Write?" );
        final String write = br.readLine();

        if ( !write.trim().isEmpty() ) {
            {
                final JGitFileSystem origin = repos.get( "A" );
                new Commit( origin.getGit(), "master", "anonymous", "anonymous@127.0.0.1", "CommitX", null, null, false, new HashMap<String, File>() {{
                    put( "file.txt", tempFile( "tempBB" ) );
                }} ).execute();
            }

            Thread.sleep( 5000 );
            System.out.println( "COOOL0!" );

            {
                final JGitFileSystem origin = repos.get( "B" );
                new Commit( origin.getGit(), "master", "anonymous", "anonymous@127.0.0.1", "undoCommitX", null, null, false, new HashMap<String, File>() {{
                    put( "file.txt", null );
                }} ).execute();
            }

            threadMap.get( "B" ).shutdown();

            Thread.sleep( 5000 );
            System.out.println( "COOOL1!" );

            {
                final JGitFileSystem origin = repos.get( "C" );
                new Commit( origin.getGit(), "master", "anonymous", "anonymous@127.0.0.1", "commitY", null, null, false, new HashMap<String, File>() {{
                    put( "fileXXXXX.txt", tempFile( "tempAAA" ) );
                }} ).execute();
            }

            Thread.sleep( 5000 );
            System.out.println( "COOOL2!" );

            {
                final JGitFileSystem origin = repos.get( "A" );
                new Commit( origin.getGit(), "master", "anonymous", "anonymous@127.0.0.1", "commitLL", null, null, false, new HashMap<String, File>() {{
                    put( "new-file.txt", tempFile( "new content now" ) );
                }} ).execute();
            }

            System.out.println( "END?" );

        }

    }

    public static class ServerInstance {

        final String name;
        final int port;

        public ServerInstance( final char name,
                               final int port ) {
            this.name = String.valueOf( name );
            this.port = port;
        }

        public String getName() {
            return name;
        }

        public int getPort() {
            return port;
        }

        @Override
        public boolean equals( final Object o ) {
            if ( this == o ) {
                return true;
            }
            if ( !( o instanceof ServerInstance ) ) {
                return false;
            }

            final ServerInstance that = (ServerInstance) o;

            return name.equals( that.name );
        }

        @Override
        public int hashCode() {
            return name.hashCode();
        }
    }

    public static class KetchInstanceSetup {

        public JGitFileSystemProvider execute( final File baseDir,
                                               final KetchInstanceConfig config ) throws IOException {
            final Path tempDirName = Files.createTempDirectory( "xxx" );
            final JGitFileSystemProvider provider = new JGitFileSystemProvider( new HashMap<String, String>() {{
                put( "org.uberfire.nio.git.daemon.enabled", "true" );
                put( "org.uberfire.nio.git.daemon.port", String.valueOf( config.getPort() ) );
                put( "org.uberfire.nio.git.dir", tempDirName.toFile().getAbsolutePath() );
                put( "org.uberfire.nio.git.ssh.enabled", "false" );
                put( "org.uberfire.nio.git.ketch", "true" );
            }} );
            final URI originRepo = URI.create( "git://uf-playground" );
            final File outDir = baseDir.toPath().resolve( config.getName() ).toFile();
            outDir.mkdirs();
            final JGitFileSystem origin = (JGitFileSystem) provider.newFileSystem( originRepo, new HashMap<String, Object>() {{
                put( "out-dir", outDir.getAbsolutePath() );
//                put( "init", true );
                put( "origin", "https://github.com/droolsjbpm/kie-examples.git" );
            }} );
            tempKetch( origin, config.getName(), config.getOthers() );
            repos.put( config.getName(), origin );
            Runtime.getRuntime().addShutdownHook( new Thread( provider::shutdown ) );
            System.out.println( "Done: " + config.getName() );
            return provider;
        }

        private void tempKetch( final JGitFileSystem origin,
                                final String ketchName,
                                final Map<String, String> otherKetches ) {

            new WriteConfiguration( origin.getGit().getRepository(), cfg -> {
                try {
                    cfg.unsetSection( "remote", "origin" );
                    cfg.setString( "ketch", null, "name", ketchName );
                    cfg.setString( "remote", ketchName, "ketch-type", "FULL" );

                    for ( Map.Entry<String, String> entry : otherKetches.entrySet() ) {
                        cfg.setString( "remote", entry.getKey(), "url", "git://127.0.0.1:" + entry.getValue() + "/" + origin.getName() );
                        cfg.setString( "remote", entry.getKey(), "ketch-type", "FULL" );
                    }

//                    cfg.setString( "remote", "Z", "url", "git://127.0.0.1:" + 9418 + "/" + origin.getName() );
//                    cfg.setString( "remote", "Z", "ketch-type", "FULL" );

                } catch ( Exception ex ) {
                    ex.printStackTrace();
                }

            } ).execute();
        }
    }

    private static File tempFile( final String content ) throws IOException {
        final File file = File.createTempFile( "bar", "foo" );
        final OutputStream out = new FileOutputStream( file );

        if ( content != null && !content.isEmpty() ) {
            out.write( content.getBytes() );
            out.flush();
        }

        out.close();
        return file;
    }

}
