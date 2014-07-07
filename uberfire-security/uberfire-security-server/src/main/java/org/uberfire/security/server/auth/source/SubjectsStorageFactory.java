package org.uberfire.security.server.auth.source;

import javax.servlet.http.HttpSession;

import org.uberfire.security.server.UberFireThreadLocalSecurityContext;

public class SubjectsStorageFactory {

    public static SubjectsStorage build() {
        Object session = UberFireThreadLocalSecurityContext.get( UberFireThreadLocalSecurityContext.Key.HTTP_SESSION );
        if ( session != null && session instanceof HttpSession ) {
            return new HttpSessionStorage( (HttpSession) session );
        } else {
            return new ThreadLocalStorage();
        }
    }
}
