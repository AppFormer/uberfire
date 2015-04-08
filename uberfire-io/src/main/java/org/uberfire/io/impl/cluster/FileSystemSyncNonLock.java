package org.uberfire.io.impl.cluster;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.RunnableFuture;

import org.uberfire.commons.cluster.ClusterService;
import org.uberfire.commons.message.MessageType;
import org.uberfire.java.nio.base.FileSystemId;
import org.uberfire.java.nio.file.FileSystem;

import static org.uberfire.io.impl.cluster.ClusterMessageType.*;

public class FileSystemSyncNonLock<V> {

    private final String serviceId;
    private final String scheme;
    private final String id;
    private final String uri;

    public FileSystemSyncNonLock( final String serviceId,
                                  final FileSystem fileSystem ) {
        this.serviceId = serviceId;
        this.scheme = fileSystem.getRootDirectories().iterator().next().toUri().getScheme();
        this.id = ( (FileSystemId) fileSystem ).id();
        this.uri = fileSystem.toString();
    }

    public MessageType getMessageType() {
        return SYNC_FS;
    }

    public String getServiceId() {
        return serviceId;
    }

    public Map<String, String> buildContent() {
        return new HashMap<String, String>() {{
            put( "fs_scheme", scheme );
            put( "fs_id", id );
            put( "fs_uri", uri );
        }};
    }

    public void sendMessage( final ClusterService clusterService ) {
        clusterService.broadcast( getServiceId(), getMessageType(), buildContent() );
    }

    public V execute( final ClusterService clusterService,
                      final RunnableFuture<V> task ) {
        try {
            task.run();

            final V result = task.get();

            sendMessage( clusterService );

            return result;
        } catch ( final ExecutionException e ) {
            throwException( e.getCause() );
        } catch ( final Exception e ) {
            throwException( e );
        }
        return null;
    }

    private void throwException( final Throwable e ) {
        if ( e instanceof RuntimeException ) {
            throw (RuntimeException) e;
        }
        throw new RuntimeException( e );
    }
}
