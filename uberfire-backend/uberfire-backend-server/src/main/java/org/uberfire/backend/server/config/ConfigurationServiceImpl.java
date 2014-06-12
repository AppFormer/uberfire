package org.uberfire.backend.server.config;

import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.ContextNotActiveException;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;
import javax.naming.InitialContext;

import org.uberfire.backend.repositories.Repository;
import org.uberfire.backend.server.config.watch.AsyncConfigWatchService;
import org.uberfire.backend.server.config.watch.AsyncWatchServiceCallback;
import org.uberfire.backend.server.config.watch.ConfigServiceWatchServiceExecutor;
import org.uberfire.backend.server.config.watch.ConfigServiceWatchServiceExecutorImpl;
import org.uberfire.commons.async.DescriptiveRunnable;
import org.uberfire.io.FileSystemType;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.IOException;
import org.uberfire.java.nio.base.WatchContext;
import org.uberfire.java.nio.base.options.CommentedOption;
import org.uberfire.java.nio.file.DirectoryStream;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.FileSystemAlreadyExistsException;
import org.uberfire.java.nio.file.Files;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.StandardWatchEventKind;
import org.uberfire.java.nio.file.WatchEvent;
import org.uberfire.java.nio.file.WatchKey;
import org.uberfire.java.nio.file.WatchService;
import org.uberfire.security.Identity;

import static org.uberfire.backend.server.util.Paths.*;

@ApplicationScoped
public class ConfigurationServiceImpl implements ConfigurationService,
                                                 AsyncWatchServiceCallback {

    private static final String MONITOR_DISABLED = "org.uberfire.sys.repo.monitor.disabled";
    //    private static final String MONITOR_CHECK_INTERVAL = "org.uberfire.sys.repo.monitor.interval";
    // mainly for windows as *NIX is based on POSIX but escape always to keep it consistent
    private static final String INVALID_FILENAME_CHARS = "[\\,/,:,*,?,\",<,>,|]";

    @Inject
    @Named("system")
    private Repository systemRepository;

    @Inject
    private ConfigGroupMarshaller marshaller;

    @Inject
    private Identity identity;

    //Cache of ConfigGroups to avoid reloading them from file
    private final Map<ConfigType, List<ConfigGroup>> configuration = new ConcurrentHashMap<ConfigType, List<ConfigGroup>>();
    private AtomicLong localLastModifiedValue = new AtomicLong( -1 );

    @Inject
    @Named("configIO")
    private IOService ioService;

    // monitor capabilities
    @Inject
    @org.uberfire.backend.server.config.Repository
    private Event<SystemRepositoryChangedEvent> repoChangedEvent;
    @Inject
    @OrgUnit
    private Event<SystemRepositoryChangedEvent> orgUnitChangedEvent;
    @Inject
    private Event<SystemRepositoryChangedEvent> changedEvent;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private ConfigServiceWatchServiceExecutor executor = null;

    private CheckConfigurationUpdates configUpdates = null;

    private FileSystem fs;

    @PostConstruct
    public void setup() {
        try {
            fs = ioService.newFileSystem( URI.create( systemRepository.getUri() ),
                                          systemRepository.getEnvironment(),
                                          FileSystemType.Bootstrap.BOOTSTRAP_INSTANCE );
            updateLastModified();
        } catch ( FileSystemAlreadyExistsException e ) {
            fs = ioService.getFileSystem( URI.create( systemRepository.getUri() ) );
        }

        Path defaultRoot = fs.getRootDirectories().iterator().next();
        for ( final Path path : fs.getRootDirectories() ) {
            if ( path.toUri().toString().contains( "/master@" ) ) {
                defaultRoot = path;
                break;
            }
        }

        systemRepository.setRoot( convert( defaultRoot ) );

        // enable monitor by default
        if ( System.getProperty( MONITOR_DISABLED ) == null ) {
            configUpdates = new CheckConfigurationUpdates( fs.newWatchService() );
            executorService.execute( new DescriptiveRunnable() {
                @Override
                public String getDescription() {
                    return configUpdates.getDescription();
                }

                @Override
                public void run() {
                    configUpdates.execute( getWatchServiceExecutor() );
                }
            } );
        }
    }

    @PreDestroy
    public void shutdown() {
        if ( configUpdates != null ) {
            configUpdates.deactivate();
        }
        executorService.shutdownNow();
    }

    @Override
    public List<ConfigGroup> getConfiguration( final ConfigType type ) {
        if ( configuration.containsKey( type ) ) {
            return configuration.get( type );
        }
        final List<ConfigGroup> configGroups = new ArrayList<ConfigGroup>();
        final DirectoryStream<Path> foundConfigs = ioService.newDirectoryStream( ioService.get( systemRepository.getUri() ),
                                                                                 new DirectoryStream.Filter<Path>() {
                                                                                     @Override
                                                                                     public boolean accept( final Path entry ) throws IOException {
                                                                                         if ( !Files.isDirectory( entry ) &&
                                                                                                 !entry.getFileName().toString().startsWith( "." ) &&
                                                                                                 entry.getFileName().toString().endsWith( type.getExt() ) ) {
                                                                                             return true;
                                                                                         }
                                                                                         return false;
                                                                                     }
                                                                                 }
                                                                               );
        //Only load and cache if a file was found!
        final Iterator<Path> it = foundConfigs.iterator();
        if ( it.hasNext() ) {
            while ( it.hasNext() ) {
                final String content = ioService.readAllString( it.next() );
                final ConfigGroup configGroup = marshaller.unmarshall( content );
                configGroups.add( configGroup );
            }
            configuration.put( type, configGroups );
        }
        return configGroups;
    }

    @Override
    public boolean addConfiguration( final ConfigGroup configGroup ) {
        String filename = configGroup.getName().replaceAll( INVALID_FILENAME_CHARS, "_" );

        final Path filePath = ioService.get( systemRepository.getUri() ).resolve( filename + configGroup.getType().getExt() );
        // avoid duplicated writes to not cause cyclic cluster sync
        if ( ioService.exists( filePath ) ) {
            return true;
        }

        final CommentedOption commentedOption = new CommentedOption( getIdentityName(),
                                                                     "Created config " + filePath.getFileName() );
        try {
            ioService.startBatch();
            ioService.write( filePath, marshaller.marshall( configGroup ), commentedOption );

            updateLastModified();
        } finally {
            ioService.endBatch();
        }
        //Invalidate cache if a new item has been created; otherwise cached value is stale
        configuration.remove( configGroup.getType() );

        return true;
    }

    @Override
    public boolean updateConfiguration( ConfigGroup configGroup ) {
        String filename = configGroup.getName().replaceAll( INVALID_FILENAME_CHARS, "_" );

        final Path filePath = ioService.get( systemRepository.getUri() ).resolve( filename + configGroup.getType().getExt() );

        final CommentedOption commentedOption = new CommentedOption( getIdentityName(),
                                                                     "Updated config " + filePath.getFileName() );
        try {
            ioService.startBatch();
            ioService.write( filePath, marshaller.marshall( configGroup ), commentedOption );

            updateLastModified();
        } finally {
            ioService.endBatch();
        }
        //Invalidate cache if a new item has been created; otherwise cached value is stale
        configuration.remove( configGroup.getType() );

        return true;
    }

    @Override
    public boolean removeConfiguration( final ConfigGroup configGroup ) {

        //Invalidate cache if an item has been removed; otherwise cached value is stale
        configuration.remove( configGroup.getType() );
        String filename = configGroup.getName().replaceAll( INVALID_FILENAME_CHARS, "_" );
        final Path filePath = ioService.get( systemRepository.getUri() ).resolve( filename + configGroup.getType().getExt() );

        // avoid duplicated writes to not cause cyclic cluster sync
        if ( !ioService.exists( filePath ) ) {
            return true;
        }
        boolean result;
        try {
            ioService.startBatch();
            result = ioService.deleteIfExists( filePath );
            if ( result ) {
                updateLastModified();
            }
        } finally {
            ioService.endBatch();
        }

        return result;
    }

    protected String getIdentityName() {
        try {
            return identity.getName();
        } catch ( ContextNotActiveException e ) {
            return "unknown";
        }
    }

    protected long getLastModified() {
        final Path lastModifiedPath = ioService.get( systemRepository.getUri() ).resolve( LAST_MODIFIED_MARKER_FILE );

        return ioService.getLastModifiedTime( lastModifiedPath ).toMillis();
    }

    protected void updateLastModified() {
        final Path lastModifiedPath = ioService.get( systemRepository.getUri() ).resolve( LAST_MODIFIED_MARKER_FILE );
        final CommentedOption commentedOption = new CommentedOption( "system", "system repo updated" );

        ioService.write( lastModifiedPath, new Date().toString().getBytes(), commentedOption );

        // update the last value to avoid to be retriggered byt the monitor
        localLastModifiedValue.set( getLastModified() );
    }

    @Override
    public void callback( long value ) {
        localLastModifiedValue.set( value );
        // invalidate cached values as system repo has changed
        configuration.clear();
    }

    private class CheckConfigurationUpdates implements AsyncConfigWatchService {

        private final WatchService ws;
        private boolean active = true;

        public CheckConfigurationUpdates( final WatchService watchService ) {
            this.ws = watchService;
        }

        public void deactivate() {
            this.active = false;
        }

        @Override
        public void execute( final ConfigServiceWatchServiceExecutor wsExecutor ) {
            while ( active ) {
                try {

                    final WatchKey wk;
                    try {
                        wk = ws.take();
                    } catch ( final Exception ex ) {
                        break;
                    }

                    final List<WatchEvent<?>> events = wk.pollEvents();

                    boolean markerFileModified = false;
                    for ( final WatchEvent<?> event : events ) {
                        final WatchContext context = (WatchContext) event.context();
                        if ( event.kind().equals( StandardWatchEventKind.ENTRY_MODIFY ) ) {
                            if ( context.getOldPath().getFileName().toString().equals( LAST_MODIFIED_MARKER_FILE ) ) {
                                markerFileModified = true;
                                break;
                            }
                        } else if ( event.kind().equals( StandardWatchEventKind.ENTRY_CREATE ) ) {
                            if ( context.getPath().getFileName().toString().equals( LAST_MODIFIED_MARKER_FILE ) ) {
                                markerFileModified = true;
                                break;
                            }
                        } else if ( event.kind().equals( StandardWatchEventKind.ENTRY_RENAME ) ) {
                            if ( context.getOldPath().getFileName().toString().equals( LAST_MODIFIED_MARKER_FILE ) ) {
                                markerFileModified = true;
                                break;
                            }
                        } else if ( event.kind().equals( StandardWatchEventKind.ENTRY_DELETE ) ) {
                            if ( context.getOldPath().getFileName().toString().equals( LAST_MODIFIED_MARKER_FILE ) ) {
                                markerFileModified = true;
                                break;
                            }
                        }
                    }

                    if ( markerFileModified ) {
                        wsExecutor.execute( wk, localLastModifiedValue.get(), ConfigurationServiceImpl.this );
                    }

                    boolean valid = wk.reset();
                    if ( !valid ) {
                        break;
                    }
                } catch ( final Exception ignored ) {
                }
            }
        }

        @Override
        public String getDescription() {
            return "Config File Watch Service";
        }
    }

    protected ConfigServiceWatchServiceExecutor getWatchServiceExecutor() {
        if ( executor == null ) {
            ConfigServiceWatchServiceExecutorImpl _executor = null;
            try {
                _executor = InitialContext.doLookup( "java:module/ConfigServiceWatchServiceExecutorImpl" );
            } catch ( final Exception ignored ) {
            }

            if ( _executor == null ) {
                _executor = new ConfigServiceWatchServiceExecutorImpl();
                _executor.setConfig( systemRepository, ioService, repoChangedEvent, orgUnitChangedEvent, changedEvent );
            }
            executor = _executor;
        }

        return executor;
    }

}
