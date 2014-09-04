package org.uberfire.workbench.events;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.backend.vfs.Path;
import org.uberfire.rpc.SessionInfo;

import static org.uberfire.commons.validation.PortablePreconditions.*;

/**
 * An Event indicating a Resource has been deleted
 */
@Portable
public class ResourceDeletedEvent extends ResourceDeleted implements ResourceEvent {

    private Path path;
    private SessionInfo sessionInfo;

    public ResourceDeletedEvent() {
    }

    public ResourceDeletedEvent( final Path path,
                                 final String message,
                                 final SessionInfo sessionInfo ) {
        super( message );
        this.path = checkNotNull( "path", path );
        this.sessionInfo = checkNotNull( "executedBy", sessionInfo );
    }

    @Override
    public Path getPath() {
        return this.path;
    }

    public SessionInfo getSessionInfo() {
        return sessionInfo;
    }

    @Override
    public String toString() {
        return "ResourceDeletedEvent{" +
                "path=" + path +
                ", message=" + getMessage() +
                ", sessionInfo=" + sessionInfo +
                '}';
    }
}
