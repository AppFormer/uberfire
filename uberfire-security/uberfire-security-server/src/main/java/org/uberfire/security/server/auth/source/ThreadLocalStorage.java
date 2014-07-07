package org.uberfire.security.server.auth.source;

import javax.security.auth.Subject;

class ThreadLocalStorage implements SubjectsStorage {

    private final java.lang.ThreadLocal<Subject> subjects = new java.lang.ThreadLocal<Subject>();

    @Override
    public void set( Subject subject ) {
        subjects.set( subject );
    }

    @Override
    public Subject get() {
        return subjects.get();
    }
}
