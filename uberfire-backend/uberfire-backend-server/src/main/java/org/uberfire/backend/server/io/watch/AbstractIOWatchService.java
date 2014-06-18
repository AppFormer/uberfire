package org.uberfire.backend.server.io.watch;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.annotation.PreDestroy;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.naming.InitialContext;

import org.uberfire.backend.server.util.Filter;
import org.uberfire.commons.async.DescriptiveRunnable;
import org.uberfire.commons.async.DescriptiveThreadFactory;
import org.uberfire.commons.services.cdi.ApplicationStarted;
import org.uberfire.io.IOWatchService;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.WatchEvent;
import org.uberfire.java.nio.file.WatchKey;
import org.uberfire.java.nio.file.WatchService;
import org.uberfire.workbench.events.ResourceAddedEvent;
import org.uberfire.workbench.events.ResourceBatchChangesEvent;
import org.uberfire.workbench.events.ResourceDeletedEvent;
import org.uberfire.workbench.events.ResourceRenamedEvent;
import org.uberfire.workbench.events.ResourceUpdatedEvent;

public abstract class AbstractIOWatchService implements IOWatchService,
                                                        Filter<WatchEvent<?>> {

    private final ExecutorService executorService = Executors.newCachedThreadPool( new DescriptiveThreadFactory() );

    private final List<FileSystem> fileSystems = new ArrayList<FileSystem>();
    private final List<WatchService> watchServices = new ArrayList<WatchService>();
    protected boolean isDisposed = false;

    private boolean started;
    private Set<AsyncWatchService> watchThreads = new HashSet<AsyncWatchService>();
    @Inject
    private Event<ResourceBatchChangesEvent> resourceBatchChanges;
    @Inject
    private Event<ResourceUpdatedEvent> resourceUpdatedEvent;
    @Inject
    private Event<ResourceRenamedEvent> resourceRenamedEvent;
    @Inject
    private Event<ResourceDeletedEvent> resourceDeletedEvent;
    @Inject
    private Event<ResourceAddedEvent> resourceAddedEvent;

    private IOWatchServiceExecutor executor = null;

    public AbstractIOWatchService() {
        final boolean autostart = Boolean.parseBoolean( System.getProperty( "org.uberfire.watcher.autostart", "true" ) );
        if ( autostart ) {
            start();
        }
    }

    public synchronized void start() {
        if ( !started ) {
            this.started = true;
            for ( final AsyncWatchService watchThread : watchThreads ) {
                final IOWatchServiceExecutor watchServiceExecutor = getWatchServiceExecutor();
                executorService.execute( new DescriptiveRunnable() {
                    @Override
                    public String getDescription() {
                        return watchThread.getDescription();
                    }

                    @Override
                    public void run() {
                        watchThread.execute( watchServiceExecutor );
                    }
                } );
            }
            watchThreads.clear();
        }
    }

    @PreDestroy
    protected void dispose() {
        isDisposed = true;
        for ( final WatchService watchService : watchServices ) {
            watchService.close();
        }
        executorService.shutdown();
    }

    @Override
    public boolean hasWatchService( final FileSystem fs ) {
        return fileSystems.contains( fs );
    }

    @Override
    public void addWatchService( final FileSystem fs,
                                 final WatchService ws ) {
        fileSystems.add( fs );
        watchServices.add( ws );

        final AsyncWatchService asyncWatchService = new AsyncWatchService() {
            @Override
            public void execute( final IOWatchServiceExecutor wsExecutor ) {
                while ( !isDisposed ) {
                    final WatchKey wk;
                    try {
                        wk = ws.take();
                    } catch ( final Exception ex ) {
                        break;
                    }

                    wsExecutor.execute( wk, AbstractIOWatchService.this );

                    // Reset the key -- this step is critical if you want to
                    // receive further watch events.  If the key is no longer valid,
                    // the directory is inaccessible so exit the loop.
                    boolean valid = wk.reset();
                    if ( !valid ) {
                        break;
                    }
                }
            }

            @Override
            public String getDescription() {
                return AbstractIOWatchService.this.getClass().getName() + "(" + ws.toString() + ")";
            }
        };

        if ( started ) {
            final IOWatchServiceExecutor watchServiceExecutor = getWatchServiceExecutor();
            executorService.execute( new DescriptiveRunnable() {
                @Override
                public String getDescription() {
                    return asyncWatchService.getDescription();
                }

                @Override
                public void run() {
                    asyncWatchService.execute( watchServiceExecutor );
                }
            } );
        } else {
            watchThreads.add( asyncWatchService );
        }
    }

    public void configureOnEvent( @Observes ApplicationStarted applicationStartedEvent ) {
        start();
    }

    protected IOWatchServiceExecutor getWatchServiceExecutor() {
        if ( executor == null ) {
            IOWatchServiceExecutor _executor = null;
            try {
                _executor = InitialContext.doLookup( "java:module/IOWatchServiceExecutorImpl" );
            } catch ( final Exception ignored ) {
            }

            if ( _executor == null ) {
                _executor = new IOWatchServiceExecutorImpl();
                ((IOWatchServiceExecutorImpl)_executor).setEvents( resourceBatchChanges, resourceUpdatedEvent, resourceRenamedEvent, resourceDeletedEvent, resourceAddedEvent );
            }
            executor = _executor;
        }

        return executor;
    }
}
