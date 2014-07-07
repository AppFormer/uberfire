package org.uberfire.security.server.auth.source;

import javax.security.auth.Subject;

public interface SubjectsStorage {

    public void set( Subject subject );

    public Subject get();

}
