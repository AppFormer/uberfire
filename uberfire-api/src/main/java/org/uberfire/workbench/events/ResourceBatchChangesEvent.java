package org.uberfire.workbench.events;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.backend.vfs.Path;
import org.uberfire.rpc.SessionInfo;

import static org.uberfire.commons.validation.PortablePreconditions.*;

/**
 * An Event indicating a various changes to various Resources
 */
@Portable
public class ResourceBatchChangesEvent implements UberFireEvent {

    private String message;
    private SessionInfo sessionInfo;
    private Map<Path, Collection<ResourceChange>> batch = new HashMap<Path, Collection<ResourceChange>>();

    public ResourceBatchChangesEvent() {
        //Empty constructor for Errai marshalling
    }

    public ResourceBatchChangesEvent( final Map<Path, Collection<ResourceChange>> batch,
                                      final String message,
                                      final SessionInfo sessionInfo ) {
        checkNotNull( "batch", batch );
        this.batch.putAll( batch );
        this.message = message;
        this.sessionInfo = checkNotNull( "sessionInfo", sessionInfo );
    }

    public Map<Path, Collection<ResourceChange>> getBatch() {
        return this.batch;
    }

    public boolean containPath( final Path path ) {
        return batch.containsKey( path );
    }

    public Collection<Path> getAffectedPaths() {
        return batch.keySet();
    }

    public Collection<ResourceChange> getChanges( final Path path ) {
        return batch.get( path );
    }

    public SessionInfo getSessionInfo() {
        return sessionInfo;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "ResourceBatchChangesEvent [sessionInfo=" + sessionInfo + ", batch=" + batch + "]";
    }
}
