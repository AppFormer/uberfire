package org.uberfire.security.server.auth.source;

import javax.security.auth.Subject;
import javax.servlet.http.HttpSession;

class HttpSessionStorage implements SubjectsStorage {

    private static final String SUBJECTS_STORAGE = "SUBJECTS_STORAGE";
    private HttpSession session;

    HttpSessionStorage( HttpSession session ) {
        this.session = session;
    }

    @Override
    public void set( Subject subject ) {
        session.setAttribute( SUBJECTS_STORAGE, subject );
    }

    @Override
    public Subject get() {
        return (Subject) session.getAttribute( SUBJECTS_STORAGE );
    }
}
