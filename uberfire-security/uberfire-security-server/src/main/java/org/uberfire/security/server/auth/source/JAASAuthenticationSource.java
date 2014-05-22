package org.uberfire.security.server.auth.source;

import java.io.IOException;
import java.lang.reflect.Method;
import java.security.acl.Group;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginContext;

import org.uberfire.security.Role;
import org.uberfire.security.SecurityContext;
import org.uberfire.security.auth.AuthenticationSource;
import org.uberfire.security.auth.Credential;
import org.uberfire.security.auth.Principal;
import org.uberfire.security.auth.RoleProvider;
import org.uberfire.security.impl.RoleImpl;
import org.uberfire.security.impl.auth.PrincipalImpl;
import org.uberfire.security.impl.auth.UsernamePasswordCredential;

import static org.uberfire.commons.validation.Preconditions.*;
import static org.uberfire.security.server.SecurityConstants.*;

public class JAASAuthenticationSource implements AuthenticationSource,
                                                 RoleProvider {

    public static final String DEFAULT_ROLE_PRINCIPLE_NAME = "Roles";
    private String rolePrincipleName = DEFAULT_ROLE_PRINCIPLE_NAME;
    private final ThreadLocal<Subject> subjects = new ThreadLocal<Subject>();

    private Map<String, ?> options = new HashMap<String, Object>();

    private String domain = "ApplicationRealm";

    @Override
    public void initialize( Map<String, ?> options ) {
        this.options = options;
        if ( options.containsKey( AUTH_DOMAIN_KEY ) ) {
            domain = (String) options.get( AUTH_DOMAIN_KEY );
        }
        if ( options.containsKey( ROLES_IN_CONTEXT_KEY ) ) {
            rolePrincipleName = (String) options.get( ROLES_IN_CONTEXT_KEY );
        }
    }

    @Override
    public boolean supportsCredential( final Credential credential ) {
        if ( credential == null ) {
            return false;
        }
        return credential instanceof UsernamePasswordCredential;
    }

    @Override
    public boolean authenticate( final Credential credential,
                                 final SecurityContext securityContext ) {
        final UsernamePasswordCredential usernamePassword = checkInstanceOf( "credential",
                                                                             credential,
                                                                             UsernamePasswordCredential.class );

        try {
            final LoginContext loginContext = new LoginContext( domain, new UsernamePasswordCallbackHandler( usernamePassword ) );
            loginContext.login();
            subjects.set( loginContext.getSubject() );

            return true;
        } catch ( final Exception ex ) {
        }

        return false;
    }

    @Override
    public List<Principal> loadPrincipals() {
        final List<Principal> principals = new ArrayList<Principal>();
        try {
            final Subject subject = subjects.get();

            if ( subject != null ) {
                final Set<java.security.Principal> jaasPrincipals = subject.getPrincipals();
                if ( jaasPrincipals != null ) {
                    for ( java.security.Principal jaccPrincipal : jaasPrincipals ) {
                        principals.add( new PrincipalImpl( jaccPrincipal.getName() ) );
                    }
                }
            }
        } catch ( Exception e ) {
            throw new RuntimeException( e );
        }

        return principals;
    }

    @Override
    public boolean supportsAddUser() {
        return false;
    }

    @Override
    public void addUser( final Credential credential ) {
        throw new UnsupportedOperationException( "addUser() is not supported." );
    }

    @Override
    public boolean supportsUpdatePassword() {
        return false;
    }

    @Override
    public void updatePassword( final Credential credential ) {
        throw new UnsupportedOperationException( "updatePassword() is not supported." );
    }

    @Override
    public boolean supportsDeleteUser() {
        return false;
    }

    @Override
    public void deleteUser( final Credential credential ) {
        throw new UnsupportedOperationException( "deleteUser() is not supported." );
    }

    @Override
    public List<Role> loadRoles( final Principal principal ) {
        List<Role> roles = null;
        try {
            Subject subject = subjects.get();

            if ( subject != null ) {
                Set<java.security.Principal> principals = subject.getPrincipals();

                if ( principals != null ) {
                    roles = new ArrayList<Role>();
                    for ( java.security.Principal p : principals ) {
                        if ( p instanceof Group && rolePrincipleName.equalsIgnoreCase( p.getName() ) ) {
                            Enumeration<? extends java.security.Principal> groups = ( (Group) p ).members();

                            while ( groups.hasMoreElements() ) {
                                final java.security.Principal groupPrincipal = groups.nextElement();
                                roles.add( new RoleImpl( groupPrincipal.getName() ) );
                            }
                            break;

                        }

                    }
                }
            }
        } catch ( Exception e ) {
            throw new RuntimeException( e );
        }
        return roles;
    }

    @Override
    public boolean supportsRoleUpdates() {
        return false;
    }

    @Override
    public void updateRoles( final Principal principal,
                             final List<Role> roles ) {
        throw new UnsupportedOperationException( "updateRoles() is not supported." );
    }

    class UsernamePasswordCallbackHandler implements CallbackHandler {

        private final UsernamePasswordCredential credential;

        public UsernamePasswordCallbackHandler( final UsernamePasswordCredential credential ) {
            this.credential = credential;
        }

        @Override
        public void handle( final Callback[] callbacks ) throws IOException, UnsupportedCallbackException {
            for ( final Callback callback : callbacks ) {
                if ( callback instanceof NameCallback ) {
                    NameCallback nameCB = (NameCallback) callback;
                    nameCB.setName( credential.getUserName() );
                } else if ( callback instanceof PasswordCallback ) {
                    PasswordCallback passwordCB = (PasswordCallback) callback;
                    passwordCB.setPassword( credential.getPassword().toString().toCharArray() );
                } else {
                    try {
                        final Method method = callback.getClass().getMethod( "setObject", Object.class );
                        method.invoke( callback, credential.getPassword().toString() );
                    } catch ( Exception e ) {
                    }
                }
            }
        }
    }

}
