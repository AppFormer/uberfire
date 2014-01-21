package org.uberfire.io.impl.cluster;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.commons.cluster.ClusterService;
import org.uberfire.commons.cluster.ClusterServiceFactory;
import org.uberfire.commons.cluster.LockExecuteNotifySyncReleaseTemplate;
import org.uberfire.commons.data.Pair;
import org.uberfire.commons.lock.LockExecuteReleaseTemplate;
import org.uberfire.commons.message.AsyncCallback;
import org.uberfire.commons.message.MessageHandler;
import org.uberfire.commons.message.MessageHandlerResolver;
import org.uberfire.commons.message.MessageType;
import org.uberfire.io.FileSystemType;
import org.uberfire.io.IOClusteredService;
import org.uberfire.io.IOService;
import org.uberfire.io.impl.IOServiceIdentifiable;
import org.uberfire.java.nio.IOException;
import org.uberfire.java.nio.base.FileSystemId;
import org.uberfire.java.nio.base.SeekableByteChannelWrapperImpl;
import org.uberfire.java.nio.channels.SeekableByteChannel;
import org.uberfire.java.nio.file.AtomicMoveNotSupportedException;
import org.uberfire.java.nio.file.CopyOption;
import org.uberfire.java.nio.file.DeleteOption;
import org.uberfire.java.nio.file.DirectoryNotEmptyException;
import org.uberfire.java.nio.file.DirectoryStream;
import org.uberfire.java.nio.file.FileAlreadyExistsException;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.FileSystemAlreadyExistsException;
import org.uberfire.java.nio.file.FileSystemNotFoundException;
import org.uberfire.java.nio.file.NoSuchFileException;
import org.uberfire.java.nio.file.NotDirectoryException;
import org.uberfire.java.nio.file.OpenOption;
import org.uberfire.java.nio.file.Option;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.ProviderNotFoundException;
import org.uberfire.java.nio.file.api.FileSystemProviders;
import org.uberfire.java.nio.file.attribute.FileAttribute;
import org.uberfire.java.nio.file.attribute.FileAttributeView;
import org.uberfire.java.nio.file.attribute.FileTime;
import org.uberfire.java.nio.file.spi.FileSystemProvider;
import org.uberfire.java.nio.security.SecurityAware;
import org.uberfire.security.auth.AuthenticationManager;
import org.uberfire.security.authz.AuthorizationManager;

import static org.uberfire.commons.validation.Preconditions.*;
import static org.uberfire.io.impl.cluster.ClusterMessageType.*;

public class IOServiceClusterImpl implements IOClusteredService {

    private static final Logger logger = LoggerFactory.getLogger(IOServiceClusterImpl.class);

    private final IOServiceIdentifiable service;
    private final ClusterService clusterService;
    private NewFileSystemListener newFileSystemListener = null;
    private AtomicBoolean started = new AtomicBoolean(false);

    public IOServiceClusterImpl( final IOService service,
                                 final ClusterServiceFactory clusterServiceFactory ) {
        this( service, clusterServiceFactory, true );
    }

    public IOServiceClusterImpl( final IOService service,
                                 final ClusterServiceFactory clusterServiceFactory,
                                 final boolean autoStart ) {
        checkNotNull( "clusterServiceFactory", clusterServiceFactory );
        this.service = checkInstanceOf("service", service, IOServiceIdentifiable.class);

        logger.debug("Creating instance of cluster service with auto start {}", autoStart);
        this.clusterService = clusterServiceFactory.build( new MessageHandlerResolver() {

            final MessageHandler newFs = new NewFileSystemMessageHandler();
            final MessageHandler syncFs = new SyncFileSystemMessageHandler();
            final MessageHandler queryFs = new QueryFileSystemMessageHandler();

            @Override
            public String getServiceId() {
                return IOServiceClusterImpl.this.service.getId();
            }

            @Override
            public MessageHandler resolveHandler( final String serviceId,
                                                  final MessageType type ) {

                if ( serviceId.equals( IOServiceClusterImpl.this.service.getId() ) ) {
                    if ( NEW_FS.equals( type ) ) {
                        return newFs;
                    } else if ( SYNC_FS.equals( type ) ) {
                        return syncFs;
                    } else if ( QUERY_FOR_FS.equals( type ) ) {
                        return queryFs;
                    }
                }

                return null;
            }
        } );

        if ( autoStart ) {
            start();
        }

    }


    @Override
    public void start() {
        this.clusterService.start();
        started.set(true);
        logger.debug("Starting cluster service {}", this);
        //New cluster members are executed within locked
        new LockExecuteReleaseTemplate<Void>().execute( clusterService, new FutureTask<Void>( new Callable<Void>() {
            @Override
            public Void call() throws Exception {

                // 10 seconds
                int timeout = 10000;
                final AtomicBoolean msgAnsweredOrTimedout = new AtomicBoolean( false );
                final AtomicBoolean onSync = new AtomicBoolean( false );

                final Map<Integer, FileSystemInfo> fileSystems = new HashMap<Integer, FileSystemInfo>();

                clusterService.broadcastAndWait( service.getId(), QUERY_FOR_FS, Collections.<String, String>emptyMap(), timeout, new AsyncCallback() {
                    @Override
                    public void onTimeOut() {
                        msgAnsweredOrTimedout.set( true );
                    }

                    @Override
                    public void onReply( final MessageType type,
                                         final Map<String, String> content ) {
                        if ( msgAnsweredOrTimedout.get() || onSync.get() ) {
                            return;
                        }

                        onSync.set( true );

                        new Thread() {
                            @Override
                            public void run() {
                                for ( final Map.Entry<String, String> entry : content.entrySet() ) {
                                    if ( entry.getKey().startsWith( "fs_" ) ) {
                                        int index = Integer.valueOf( entry.getKey().substring( entry.getKey().lastIndexOf( "_" ) + 1 ) );
                                        if ( !fileSystems.containsKey( index ) ) {
                                            fileSystems.put( index, new FileSystemInfo() );
                                        }
                                        final FileSystemInfo fsInfo = fileSystems.get( index );
                                        if ( entry.getKey().startsWith( "fs_id_" ) ) {
                                            fsInfo.setId( entry.getValue() );
                                        } else if ( entry.getKey().startsWith( "fs_scheme_" ) ) {
                                            fsInfo.setScheme( entry.getValue() );
                                        } else if ( entry.getKey().startsWith( "fs_uri_" ) ) {
                                            fsInfo.setUri( entry.getValue() );
                                        }
                                    }
                                }

                                for ( final FileSystemInfo fileSystemInfo : fileSystems.values() ) {
                                    try {
                                        final URI newFS = URI.create( fileSystemInfo.getScheme() + "://" + fileSystemInfo.getId() );
                                        service.newFileSystem( newFS, Collections.<String, Object>emptyMap() );
                                    } catch ( FileSystemAlreadyExistsException ex ) {
                                    }

                                    final URI fs = URI.create( fileSystemInfo.getScheme() + "://" + fileSystemInfo.getId() + "?sync=" + fileSystemInfo.getUri() + "&force" );
                                    service.getFileSystem( fs );
                                }

                                msgAnsweredOrTimedout.set( true );
                            }
                        }.start();
                    }
                } );

                while ( !msgAnsweredOrTimedout.get() ) {
                    try {
                        Thread.sleep( 100 );
                    } catch ( InterruptedException ignored ) {
                    }
                }

                return null;
            }
        } ) );
    }

    @Override
    public void dispose() {
        clusterService.dispose();
        service.dispose();
    }

    @Override
    public void startBatch( final Option... options ) {
        clusterService.lock();
        service.startBatch( options );
    }

    @Override
    public void endBatch( final Option... options ) {
        service.endBatch( options );
        clusterService.unlock();
    }

    @Override
    public FileAttribute<?>[] convert( final Map<String, ?> attrs ) {
        return service.convert( attrs );
    }

    @Override
    public Path get( final String first,
                     final String... more ) throws IllegalArgumentException {
        return service.get( first, more );
    }

    @Override
    public Path get( final URI uri ) throws IllegalArgumentException, FileSystemNotFoundException, SecurityException {
        return service.get( uri );
    }

    @Override
    public Iterable<FileSystem> getFileSystems() {
        return service.getFileSystems();
    }

    @Override
    public Iterable<FileSystem> getFileSystems( final FileSystemType type ) {
        return service.getFileSystems( type );
    }

    @Override
    public FileSystem getFileSystem( final URI uri ) throws IllegalArgumentException, FileSystemNotFoundException, ProviderNotFoundException, SecurityException {
        return service.getFileSystem( uri );
    }

    @Override
    public FileSystem newFileSystem( final URI uri,
                                     final Map<String, ?> env ) throws IllegalArgumentException, FileSystemAlreadyExistsException, ProviderNotFoundException, IOException, SecurityException {
        return new LockExecuteNotifySyncReleaseTemplate<FileSystem>() {

            @Override
            public MessageType getMessageType() {
                return NEW_FS;
            }

            @Override
            public String getServiceId() {
                return service.getId();
            }

            @Override
            public Map<String, String> buildContent() {
                return new HashMap<String, String>() {{
                    put( "uri", uri.toString() );
                    for ( final Map.Entry<String, ?> entry : env.entrySet() ) {
                        put( entry.getKey(), entry.getValue().toString() );
                    }
                }};
            }

            @Override
            public int timeOut() {
                return TIMEOUT;
            }
        }.execute( clusterService, new FutureTask<FileSystem>( new Callable<FileSystem>() {
            @Override
            public FileSystem call() throws Exception {
                return service.newFileSystem( uri, new HashMap<String, Object>( env ) {{
                    put( "clusterService", clusterService );
                }} );
            }
        } ) );
    }

    @Override
    public FileSystem newFileSystem( final URI uri,
                                     final Map<String, ?> env,
                                     final FileSystemType type ) throws IllegalArgumentException, FileSystemAlreadyExistsException, ProviderNotFoundException, IOException, SecurityException {
        return new LockExecuteNotifySyncReleaseTemplate<FileSystem>() {

            @Override
            public MessageType getMessageType() {
                return NEW_FS;
            }

            @Override
            public String getServiceId() {
                return service.getId();
            }

            @Override
            public Map<String, String> buildContent() {
                return new HashMap<String, String>() {{
                    put( "uri", uri.toString() );
                    put( "type", type.toString() );
                    for ( final Map.Entry<String, ?> entry : env.entrySet() ) {
                        put( entry.getKey(), entry.getValue().toString() );
                    }
                }};
            }

            @Override
            public int timeOut() {
                return TIMEOUT;
            }
        }.execute( clusterService, new FutureTask<FileSystem>( new Callable<FileSystem>() {
            @Override
            public FileSystem call() throws Exception {
                return service.newFileSystem( uri, new HashMap<String, Object>( env ) {{
                    put( "clusterService", clusterService );
                }}, type );

            }
        } ) );

    }

    @Override
    public void onNewFileSystem( NewFileSystemListener listener ) {
        this.newFileSystemListener = listener;
    }

    @Override
    public InputStream newInputStream( final Path path,
                                       final OpenOption... options ) throws IllegalArgumentException, NoSuchFileException, UnsupportedOperationException, IOException, SecurityException {
        return service.newInputStream( path, options );
    }

    @Override
    public DirectoryStream<Path> newDirectoryStream( final Path dir ) throws IllegalArgumentException, NotDirectoryException, IOException, SecurityException {
        return service.newDirectoryStream( dir );
    }

    @Override
    public DirectoryStream<Path> newDirectoryStream( final Path dir,
                                                     final DirectoryStream.Filter<Path> filter ) throws IllegalArgumentException, NotDirectoryException, IOException, SecurityException {
        return service.newDirectoryStream( dir, filter );
    }

    @Override
    public Path createFile( final Path path,
                            final FileAttribute<?>... attrs ) throws IllegalArgumentException, UnsupportedOperationException, FileAlreadyExistsException, IOException, SecurityException {
        return new FileSystemSyncLock<Path>( service.getId(), path.getFileSystem() ).execute( clusterService, new FutureTask<Path>( new Callable<Path>() {
            @Override
            public Path call() throws Exception {
                return service.createFile( path, attrs );
            }
        } ) );
    }

    @Override
    public Path createDirectory( final Path dir,
                                 final FileAttribute<?>... attrs ) throws IllegalArgumentException, UnsupportedOperationException, FileAlreadyExistsException, IOException, SecurityException {
        return new FileSystemSyncLock<Path>( service.getId(), dir.getFileSystem() ).execute( clusterService, new FutureTask<Path>( new Callable<Path>() {
            @Override
            public Path call() throws Exception {
                return service.createDirectory( dir, attrs );
            }
        } ) );
    }

    @Override
    public Path createDirectories( final Path dir,
                                   final FileAttribute<?>... attrs ) throws UnsupportedOperationException, FileAlreadyExistsException, IOException, SecurityException {
        return new FileSystemSyncLock<Path>( service.getId(), dir.getFileSystem() ).execute( clusterService, new FutureTask<Path>( new Callable<Path>() {
            @Override
            public Path call() throws Exception {
                return service.createDirectories( dir, attrs );
            }
        } ) );
    }

    @Override
    public Path createDirectory( final Path dir,
                                 final Map<String, ?> attrs ) throws IllegalArgumentException, UnsupportedOperationException, FileAlreadyExistsException, IOException, SecurityException {
        return new FileSystemSyncLock<Path>( service.getId(), dir.getFileSystem() ).execute( clusterService, new FutureTask<Path>( new Callable<Path>() {
            @Override
            public Path call() throws Exception {
                return service.createDirectory( dir, attrs );
            }
        } ) );
    }

    @Override
    public Path createDirectories( final Path dir,
                                   final Map<String, ?> attrs ) throws UnsupportedOperationException, FileAlreadyExistsException, IOException, SecurityException {
        return new FileSystemSyncLock<Path>( service.getId(), dir.getFileSystem() ).execute( clusterService, new FutureTask<Path>( new Callable<Path>() {
            @Override
            public Path call() throws Exception {
                return service.createDirectories( dir, attrs );
            }
        } ) );
    }

    @Override
    public void delete( final Path path,
                        final DeleteOption... options ) throws IllegalArgumentException, NoSuchFileException, DirectoryNotEmptyException, IOException, SecurityException {
        new FileSystemSyncLock<Void>( service.getId(), path.getFileSystem() ).execute( clusterService, new FutureTask<Void>( new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                service.delete( path, options );
                return null;
            }
        } ) );
    }

    @Override
    public boolean deleteIfExists( final Path path,
                                   final DeleteOption... options ) throws IllegalArgumentException, DirectoryNotEmptyException, IOException, SecurityException {
        return new FileSystemSyncLock<Boolean>( service.getId(), path.getFileSystem() ).execute( clusterService, new FutureTask<Boolean>( new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return service.deleteIfExists( path, options );
            }
        } ) );
    }

    @Override
    public Path createTempFile( final String prefix,
                                final String suffix,
                                final FileAttribute<?>... attrs ) throws IllegalArgumentException, UnsupportedOperationException, IOException, SecurityException {
        return service.createTempFile( prefix, suffix, attrs );
    }

    @Override
    public Path createTempFile( final Path dir,
                                final String prefix,
                                final String suffix,
                                final FileAttribute<?>... attrs ) throws IllegalArgumentException, UnsupportedOperationException, IOException, SecurityException {
        return service.createTempFile( dir, prefix, suffix, attrs );
    }

    @Override
    public Path createTempDirectory( final String prefix,
                                     final FileAttribute<?>... attrs ) throws IllegalArgumentException, UnsupportedOperationException, IOException, SecurityException {
        return service.createTempDirectory( prefix, attrs );
    }

    @Override
    public Path createTempDirectory( final Path dir,
                                     final String prefix,
                                     final FileAttribute<?>... attrs ) throws IllegalArgumentException, UnsupportedOperationException, IOException, SecurityException {
        return service.createTempDirectory( dir, prefix, attrs );
    }

    @Override
    public Path copy( final Path source,
                      final Path target,
                      final CopyOption... options ) throws UnsupportedOperationException, FileAlreadyExistsException, DirectoryNotEmptyException, IOException, SecurityException {
        return new FileSystemSyncLock<Path>( service.getId(), target.getFileSystem() ).execute( clusterService, new FutureTask<Path>( new Callable<Path>() {
            @Override
            public Path call() throws Exception {
                return service.copy( source, target, options );
            }
        } ) );
    }

    @Override
    public long copy( final InputStream in,
                      final Path target,
                      final CopyOption... options ) throws IOException, FileAlreadyExistsException, DirectoryNotEmptyException, UnsupportedOperationException, SecurityException {
        return new FileSystemSyncLock<Long>( service.getId(), target.getFileSystem() ).execute( clusterService, new FutureTask<Long>( new Callable<Long>() {
            @Override
            public Long call() throws Exception {
                return service.copy( in, target, options );
            }
        } ) );
    }

    @Override
    public long copy( final Path source,
                      final OutputStream out ) throws IOException, SecurityException {
        return service.copy( source, out );
    }

    @Override
    public Path move( final Path source,
                      final Path target,
                      final CopyOption... options ) throws UnsupportedOperationException, FileAlreadyExistsException, DirectoryNotEmptyException, AtomicMoveNotSupportedException, IOException, SecurityException {
        return new FileSystemSyncLock<Path>( service.getId(), source.getFileSystem() ).execute( clusterService, new FutureTask<Path>( new Callable<Path>() {
            @Override
            public Path call() throws Exception {
                return new FileSystemSyncLock<Path>( service.getId(), target.getFileSystem() ).execute( clusterService, new FutureTask<Path>( new Callable<Path>() {
                    @Override
                    public Path call() throws Exception {
                        return service.move( source, target, options );
                    }
                } ) );
            }
        } ) );
    }

    @Override
    public <V extends FileAttributeView> V getFileAttributeView( final Path path,
                                                                 final Class<V> type ) throws IllegalArgumentException {
        return service.getFileAttributeView( path, type );
    }

    @Override
    public Map<String, Object> readAttributes( final Path path ) throws UnsupportedOperationException, NoSuchFileException, IllegalArgumentException, IOException, SecurityException {
        return service.readAttributes( path );
    }

    @Override
    public Map<String, Object> readAttributes( final Path path,
                                               final String attributes ) throws UnsupportedOperationException, NoSuchFileException, IllegalArgumentException, IOException, SecurityException {
        return service.readAttributes( path, attributes );
    }

    @Override
    public Path setAttributes( final Path path,
                               final FileAttribute<?>... attrs ) throws UnsupportedOperationException, IllegalArgumentException, ClassCastException, IOException, SecurityException {
        return new FileSystemSyncLock<Path>( service.getId(), path.getFileSystem() ).execute( clusterService, new FutureTask<Path>( new Callable<Path>() {
            @Override
            public Path call() throws Exception {
                return service.setAttributes( path, attrs );
            }
        } ) );
    }

    @Override
    public Path setAttributes( final Path path,
                               final Map<String, Object> attrs ) throws UnsupportedOperationException, IllegalArgumentException, ClassCastException, IOException, SecurityException {
        return new FileSystemSyncLock<Path>( service.getId(), path.getFileSystem() ).execute( clusterService, new FutureTask<Path>( new Callable<Path>() {
            @Override
            public Path call() throws Exception {
                return service.setAttributes( path, attrs );
            }
        } ) );
    }

    @Override
    public Path setAttribute( final Path path,
                              final String attribute,
                              final Object value ) throws UnsupportedOperationException, IllegalArgumentException, ClassCastException, IOException, SecurityException {
        return new FileSystemSyncLock<Path>( service.getId(), path.getFileSystem() ).execute( clusterService, new FutureTask<Path>( new Callable<Path>() {
            @Override
            public Path call() throws Exception {
                return service.setAttribute( path, attribute, value );
            }
        } ) );
    }

    @Override
    public Object getAttribute( final Path path,
                                final String attribute ) throws UnsupportedOperationException, IllegalArgumentException, IOException, SecurityException {
        return service.getAttribute( path, attribute );
    }

    @Override
    public FileTime getLastModifiedTime( final Path path ) throws IllegalArgumentException, IOException, SecurityException {
        return service.getLastModifiedTime( path );
    }

    @Override
    public long size( final Path path ) throws IllegalArgumentException, IOException, SecurityException {
        return service.size( path );
    }

    @Override
    public boolean exists( final Path path ) throws IllegalArgumentException, SecurityException {
        return service.exists( path );
    }

    @Override
    public boolean notExists( final Path path ) throws IllegalArgumentException, SecurityException {
        return service.notExists( path );
    }

    @Override
    public boolean isSameFile( final Path path,
                               final Path path2 ) throws IllegalArgumentException, IOException, SecurityException {
        return service.isSameFile( path, path2 );
    }

    @Override
    public BufferedReader newBufferedReader( final Path path,
                                             final Charset cs ) throws IllegalArgumentException, NoSuchFileException, IOException, SecurityException {
        return service.newBufferedReader( path, cs );
    }

    @Override
    public byte[] readAllBytes( final Path path ) throws IOException, OutOfMemoryError, SecurityException {
        return service.readAllBytes( path );
    }

    @Override
    public List<String> readAllLines( final Path path ) throws IllegalArgumentException, NoSuchFileException, IOException, SecurityException {
        return service.readAllLines( path );
    }

    @Override
    public List<String> readAllLines( final Path path,
                                      final Charset cs ) throws IllegalArgumentException, NoSuchFileException, IOException, SecurityException {
        return service.readAllLines( path, cs );
    }

    @Override
    public String readAllString( final Path path,
                                 final Charset cs ) throws IllegalArgumentException, NoSuchFileException, IOException {
        return service.readAllString( path, cs );
    }

    @Override
    public String readAllString( final Path path ) throws IllegalArgumentException, NoSuchFileException, IOException {
        return service.readAllString( path );
    }

    @Override
    public Path write( final Path path,
                       final byte[] bytes,
                       final OpenOption... options ) throws IOException, UnsupportedOperationException, SecurityException {
        return new FileSystemSyncLock<Path>( service.getId(), path.getFileSystem() ).execute( clusterService, new FutureTask<Path>( new Callable<Path>() {
            @Override
            public Path call() throws Exception {
                return service.write( path, bytes, options );
            }
        } ) );
    }

    @Override
    public Path write( final Path path,
                       final byte[] bytes,
                       final Map<String, ?> attrs,
                       final OpenOption... options ) throws IOException, UnsupportedOperationException, SecurityException {
        return new FileSystemSyncLock<Path>( service.getId(), path.getFileSystem() ).execute( clusterService, new FutureTask<Path>( new Callable<Path>() {
            @Override
            public Path call() throws Exception {
                return service.write( path, bytes, attrs, options );
            }
        } ) );
    }

    @Override
    public Path write( final Path path,
                       final byte[] bytes,
                       final Set<? extends OpenOption> options,
                       final FileAttribute<?>... attrs ) throws IllegalArgumentException, IOException, UnsupportedOperationException {
        return new FileSystemSyncLock<Path>( service.getId(), path.getFileSystem() ).execute( clusterService, new FutureTask<Path>( new Callable<Path>() {
            @Override
            public Path call() throws Exception {
                return service.write( path, bytes, options, attrs );
            }
        } ) );
    }

    @Override
    public Path write( final Path path,
                       final Iterable<? extends CharSequence> lines,
                       final Charset cs,
                       final OpenOption... options ) throws IllegalArgumentException, IOException, UnsupportedOperationException, SecurityException {
        return new FileSystemSyncLock<Path>( service.getId(), path.getFileSystem() ).execute( clusterService, new FutureTask<Path>( new Callable<Path>() {
            @Override
            public Path call() throws Exception {
                return service.write( path, lines, cs, options );
            }
        } ) );
    }

    @Override
    public Path write( final Path path,
                       final String content,
                       final OpenOption... options ) throws IllegalArgumentException, IOException, UnsupportedOperationException {
        return new FileSystemSyncLock<Path>( service.getId(), path.getFileSystem() ).execute( clusterService, new FutureTask<Path>( new Callable<Path>() {
            @Override
            public Path call() throws Exception {
                return service.write( path, content, options );
            }
        } ) );
    }

    @Override
    public Path write( final Path path,
                       final String content,
                       final Charset cs,
                       final OpenOption... options ) throws IllegalArgumentException, IOException, UnsupportedOperationException {
        return new FileSystemSyncLock<Path>( service.getId(), path.getFileSystem() ).execute( clusterService, new FutureTask<Path>( new Callable<Path>() {
            @Override
            public Path call() throws Exception {
                return service.write( path, content, cs, options );
            }
        } ) );
    }

    @Override
    public Path write( final Path path,
                       final String content,
                       final Set<? extends OpenOption> options,
                       final FileAttribute<?>... attrs ) throws IllegalArgumentException, IOException, UnsupportedOperationException {
        return new FileSystemSyncLock<Path>( service.getId(), path.getFileSystem() ).execute( clusterService, new FutureTask<Path>( new Callable<Path>() {
            @Override
            public Path call() throws Exception {
                return service.write( path, content, options, attrs );
            }
        } ) );
    }

    @Override
    public Path write( final Path path,
                       final String content,
                       final Charset cs,
                       final Set<? extends OpenOption> options,
                       final FileAttribute<?>... attrs ) throws IllegalArgumentException, IOException, UnsupportedOperationException {
        return new FileSystemSyncLock<Path>( service.getId(), path.getFileSystem() ).execute( clusterService, new FutureTask<Path>( new Callable<Path>() {
            @Override
            public Path call() throws Exception {
                return service.write( path, content, cs, options, attrs );
            }
        } ) );
    }

    @Override
    public Path write( final Path path,
                       final String content,
                       final Map<String, ?> attrs,
                       final OpenOption... options ) throws IllegalArgumentException, IOException, UnsupportedOperationException {
        return new FileSystemSyncLock<Path>( service.getId(), path.getFileSystem() ).execute( clusterService, new FutureTask<Path>( new Callable<Path>() {
            @Override
            public Path call() throws Exception {
                return service.write( path, content, attrs, options );
            }
        } ) );
    }

    @Override
    public Path write( final Path path,
                       final String content,
                       final Charset cs,
                       final Map<String, ?> attrs,
                       final OpenOption... options ) throws IllegalArgumentException, IOException, UnsupportedOperationException {
        return new FileSystemSyncLock<Path>( service.getId(), path.getFileSystem() ).execute( clusterService, new FutureTask<Path>( new Callable<Path>() {
            @Override
            public Path call() throws Exception {
                return service.write( path, content, cs, attrs, options );
            }
        } ) );
    }

    @Override
    public OutputStream newOutputStream( final Path path,
                                         final OpenOption... options ) throws IllegalArgumentException, UnsupportedOperationException, IOException, SecurityException {
        final OutputStream out = service.newOutputStream( path, options );
        return new OutputStream() {
            @Override
            public void write( final int b ) throws java.io.IOException {
                out.write( b );
            }

            @Override
            public void close() throws java.io.IOException {
                new FileSystemSyncLock<Void>( service.getId(), path.getFileSystem() ).execute( clusterService, new FutureTask<Void>( new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        out.close();
                        return null;
                    }
                } ) );
            }
        };
    }

    @Override
    public SeekableByteChannel newByteChannel( final Path path,
                                               final OpenOption... options ) throws IllegalArgumentException, UnsupportedOperationException, FileAlreadyExistsException, IOException, SecurityException {
        final SeekableByteChannel sbc = service.newByteChannel( path, options );

        return new SeekableByteChannelWrapperImpl( sbc ) {
            @Override
            public void close() throws java.io.IOException {
                new FileSystemSyncLock<Void>( service.getId(), path.getFileSystem() ).execute( clusterService, new FutureTask<Void>( new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        sbc.close();
                        return null;
                    }
                } ) );
            }
        };
    }

    @Override
    public SeekableByteChannel newByteChannel( final Path path,
                                               final Set<? extends OpenOption> options,
                                               final FileAttribute<?>... attrs ) throws IllegalArgumentException, UnsupportedOperationException, FileAlreadyExistsException, IOException, SecurityException {
        final SeekableByteChannel sbc = service.newByteChannel( path, options, attrs );

        return new SeekableByteChannelWrapperImpl( sbc ) {
            @Override
            public void close() throws java.io.IOException {
                new FileSystemSyncLock<Void>( service.getId(), path.getFileSystem() ).execute( clusterService, new FutureTask<Void>( new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        sbc.close();
                        return null;
                    }
                } ) );
            }
        };
    }

    @Override
    public BufferedWriter newBufferedWriter( final Path path,
                                             final Charset cs,
                                             final OpenOption... options ) throws IllegalArgumentException, IOException, UnsupportedOperationException, SecurityException {
        return new BufferedWriter( service.newBufferedWriter( path, cs, options ) ) {
            @Override
            public void close() throws java.io.IOException {
                new FileSystemSyncLock<Void>( service.getId(), path.getFileSystem() ).execute( clusterService, new FutureTask<Void>( new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        superClose();
                        return null;
                    }
                } ) );
            }

            private void superClose() {
                try {
                    super.close();
                } catch ( java.io.IOException e ) {
                    throw new RuntimeException( e );
                }
            }
        };
    }

    @Override
    public void setAuthenticationManager( final AuthenticationManager authenticationManager ) {
        for ( final FileSystemProvider fileSystemProvider : FileSystemProviders.installedProviders() ) {
            if ( fileSystemProvider instanceof SecurityAware ) {
                ( (SecurityAware) fileSystemProvider ).setAuthenticationManager( authenticationManager );
            }
        }
    }

    @Override
    public void setAuthorizationManager( final AuthorizationManager authorizationManager ) {
        for ( final FileSystemProvider fileSystemProvider : FileSystemProviders.installedProviders() ) {
            if ( fileSystemProvider instanceof SecurityAware ) {
                ( (SecurityAware) fileSystemProvider ).setAuthorizationManager( authorizationManager );
            }
        }
    }

    class NewFileSystemMessageHandler implements MessageHandler {

        @Override
        public Pair<MessageType, Map<String, String>> handleMessage( final MessageType type,
                                                                     final Map<String, String> content ) {
            if ( NEW_FS.equals( type ) ) {
                final String _uri = content.get( "uri" );
                final String fsType = content.get( "type" );
                final Map<String, String> env = new HashMap<String, String>();

                for ( final Map.Entry<String, String> entry : content.entrySet() ) {
                    if ( !( entry.getKey().equals( "uri" ) || entry.getKey().equals( "type" ) ) ) {
                        env.put( entry.getKey(), entry.getValue() );
                    }
                }

                final URI uri = URI.create( _uri );
                final FileSystem fs;
                if ( fsType != null ) {
                    fs = service.newFileSystem( uri, env, new FileSystemType() {
                        public String toString() {
                            return fsType;
                        }

                        public int hashCode() {
                            return fsType.hashCode();
                        }
                    } );
                } else {
                    fs = service.newFileSystem( uri, env );
                }

                newFileSystemListener.execute( fs, uri.getScheme(), ( (FileSystemId) fs ).id(), env );
            }
            return null;
        }
    }

    class SyncFileSystemMessageHandler implements MessageHandler {

        @Override
        public Pair<MessageType, Map<String, String>> handleMessage( final MessageType type,
                                                                     final Map<String, String> content ) {
            if ( SYNC_FS.equals( type ) ) {
                final String scheme = content.get( "fs_scheme" );
                final String id = content.get( "fs_id" );
                String uris = content.get( "fs_uri" );
                String[] supportedUris = uris.split("\n");

                for (String supportedUri : supportedUris) {
                    try {
                        String origin;
                        try {
                            origin = URLEncoder.encode( supportedUri, "UTF-8" );
                        } catch ( UnsupportedEncodingException e ) {
                            origin = supportedUri;
                        }

                        if ( origin != null ) {
                            final URI fs = URI.create( scheme + "://" + id + "?sync=" + origin + "&force" );

                            service.getFileSystem( fs );
                        }
                        break;
                    } catch (Exception e) {
                        // try the other supported uri in case of failure
                        logger.warn("File system synchronization for origin {} failed with error {}, trying another if available",
                                supportedUri, e.getMessage());
                    }
                }
            }

            return null;
        }
    }

    class QueryFileSystemMessageHandler implements MessageHandler {

        @Override
        public Pair<MessageType, Map<String, String>> handleMessage( final MessageType type,
                                                                     final Map<String, String> content ) {
            if ( QUERY_FOR_FS.equals( type ) ) {
                Map<String, String> replyContent = new HashMap<String, String>();
                int i = 0;
                for ( FileSystem fs : service.getFileSystems() ) {
                    replyContent.put( "fs_scheme_" + i, fs.getRootDirectories().iterator().next().toUri().getScheme() );
                    replyContent.put( "fs_id_" + i, ( (FileSystemId) fs ).id() );
                    replyContent.put( "fs_uri_" + i, fs.toString() );
                    i++;
                }
                return new Pair<MessageType, Map<String, String>>( QUERY_FOR_FS_RESULT, replyContent );
            }
            return null;
        }
    }

    static class FileSystemInfo {

        private String id;
        private String scheme;
        private String uri;

        FileSystemInfo() {

        }

        FileSystemInfo( String id,
                        String scheme,
                        String uri ) {
            this.id = id;
            this.scheme = scheme;
            this.uri = uri;
        }

        String getId() {
            return id;
        }

        void setId( String id ) {
            this.id = id;
        }

        String getScheme() {
            return scheme;
        }

        void setScheme( String scheme ) {
            this.scheme = scheme;
        }

        String getUri() {
            return uri;
        }

        void setUri( String uri ) {
            this.uri = uri;
        }
    }
}
