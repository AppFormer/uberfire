package org.uberfire.security.server.auth.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.enterprise.inject.Alternative;

import org.uberfire.security.Role;
import org.uberfire.security.SecurityContext;
import org.uberfire.security.Subject;
import org.uberfire.security.auth.AuthenticationException;
import org.uberfire.security.auth.AuthenticationManager;
import org.uberfire.security.auth.AuthenticationResult;
import org.uberfire.security.auth.AuthenticationScheme;
import org.uberfire.security.auth.AuthenticationSource;
import org.uberfire.security.auth.Credential;
import org.uberfire.security.auth.Principal;
import org.uberfire.security.auth.RoleProvider;
import org.uberfire.security.auth.SubjectPropertiesProvider;
import org.uberfire.security.impl.IdentityImpl;
import org.uberfire.security.server.UserPassSecurityContext;
import org.uberfire.security.server.auth.DefaultAuthenticationProvider;

import static org.uberfire.commons.validation.Preconditions.*;
import static org.uberfire.security.auth.AuthenticationStatus.*;

@Alternative
public class SimpleUserPassAuthenticationManager implements AuthenticationManager {

    private AuthenticationScheme scheme;
    private RoleProvider roleProvider;
    private SubjectPropertiesProvider propertiesProvider;
    private DefaultAuthenticationProvider authProvider;

    public SimpleUserPassAuthenticationManager() {
    }

    public SimpleUserPassAuthenticationManager( final AuthenticationSource source,
                                                final AuthenticationScheme scheme,
                                                final RoleProvider roleProvider,
                                                final SubjectPropertiesProvider propertiesProvider,
                                                final Map<String, String> options ) {
        setup( source, scheme, roleProvider, propertiesProvider, options );
    }

    public void setup( final AuthenticationSource source,
                       final AuthenticationScheme scheme,
                       final RoleProvider roleProvider,
                       final SubjectPropertiesProvider propertiesProvider,
                       final Map<String, String> options ) {
        this.scheme = scheme;
        if ( roleProvider != null ) {
            this.roleProvider = roleProvider;
        } else if ( source instanceof RoleProvider ) {
            this.roleProvider = (RoleProvider) source;
        } else {
            this.roleProvider = null;
        }
        this.propertiesProvider = propertiesProvider;
        this.authProvider = new DefaultAuthenticationProvider( source );

        authProvider.initialize( options );
    }

    @Override
    public Subject authenticate( final SecurityContext context ) throws AuthenticationException {
        final UserPassSecurityContext userPassContext = checkInstanceOf( "context", context, UserPassSecurityContext.class );

        final Principal principal;

        final Credential credential = scheme.buildCredential( userPassContext );

        if ( credential == null ) {
            throw new AuthenticationException( "Invalid credentials." );
        }

        final AuthenticationResult authResult = authProvider.authenticate( credential, context );

        if ( authResult.getStatus().equals( SUCCESS ) ) {
            principal = authResult.getPrincipal();
        } else {
            principal = null;
        }

        if ( principal == null ) {
            throw new AuthenticationException( "Invalid credentials." );
        }

        final List<Role> roles = new ArrayList<Role>();

        if ( roleProvider != null ) {
            roles.addAll( roleProvider.loadRoles( principal, context ) );
        }

        final Map<String, String> properties = new HashMap<String, String>() {{
            if ( propertiesProvider != null ) {
                putAll( propertiesProvider.loadProperties( principal ) );
            }
        }};

        return new IdentityImpl( principal.getName(), roles, properties );
    }

    @Override
    public void logout( final SecurityContext context ) throws AuthenticationException {
    }
}
