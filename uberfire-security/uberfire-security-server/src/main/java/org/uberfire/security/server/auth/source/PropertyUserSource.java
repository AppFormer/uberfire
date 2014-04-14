/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.security.server.auth.source;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.security.Role;
import org.uberfire.security.SecurityContext;
import org.uberfire.security.auth.AuthenticationSource;
import org.uberfire.security.auth.Credential;
import org.uberfire.security.auth.Principal;
import org.uberfire.security.auth.RoleProvider;
import org.uberfire.security.impl.RoleImpl;
import org.uberfire.security.impl.auth.PrincipalImpl;
import org.uberfire.security.impl.auth.UserNameCredential;
import org.uberfire.security.impl.auth.UsernamePasswordCredential;
import org.uberfire.security.server.SecurityConstants;

import static org.uberfire.commons.validation.Preconditions.*;

public class PropertyUserSource implements AuthenticationSource,
                                           RoleProvider {

    private static final Logger logger = LoggerFactory.getLogger( PropertyUserSource.class );

    private Map<String, ?> options = new HashMap<String, Object>();
    private Map<String, Object> credentials = new HashMap<String, Object>();
    private Map<String, List<Role>> roles = new HashMap<String, List<Role>>();
    private String propertyUserSourcePath = null;

    @Override
    public synchronized void initialize( final Map<String, ?> options ) {
        this.options = options;

        final Properties properties = loadProperties();

        for ( Map.Entry<Object, Object> contentEntry : properties.entrySet() ) {
            final String content = contentEntry.getValue().toString();
            final String[] result = content.split( "," );
            credentials.put( contentEntry.getKey().toString(), result[ 0 ] );
            final List<Role> roles = new ArrayList<Role>();
            if ( result.length > 1 ) {
                for ( int i = 1; i < result.length; i++ ) {
                    final String currentRole = result[ i ];
                    roles.add( new RoleImpl( currentRole ) );
                }
                this.roles.put( contentEntry.getKey().toString(), roles );
            }
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
    public synchronized boolean authenticate( final Credential credential,
                                              final SecurityContext securityContext ) {
        final UsernamePasswordCredential usernamePassword = checkInstanceOf( "credential",
                                                                             credential,
                                                                             UsernamePasswordCredential.class );

        final Object pass = credentials.get( usernamePassword.getUserName() );
        if ( pass != null && pass.equals( usernamePassword.getPassword() ) ) {
            return true;
        }
        return false;
    }

    @Override
    public List<Principal> loadPrincipals() {
        final List<Principal> principals = new ArrayList<Principal>();
        for ( String name : credentials.keySet() ) {
            principals.add( new PrincipalImpl( name ) );
        }
        return principals;
    }

    @Override
    public boolean supportsAddUser() {
        return propertyUserSourcePath != null;
    }

    @Override
    public synchronized void addUser( final Credential credential ) {
        final UsernamePasswordCredential usernamePassword = checkInstanceOf( "credential",
                                                                             credential,
                                                                             UsernamePasswordCredential.class );

        final String userName = usernamePassword.getUserName();
        final Object userPassword = usernamePassword.getPassword();
        final List<Role> userRoles = Collections.<Role>emptyList();

        logger.info( "Adding User: '" + userName + "' with User Roles [" + dumpUserRoles( userRoles ) + "]." );

        final Properties properties = loadProperties();
        properties.setProperty( userName,
                                makePropertiesFileValue( userPassword,
                                                         userRoles ) );
        saveProperties( properties );

        credentials.put( userName,
                         userPassword );
        roles.put( userName,
                   userRoles );
    }

    @Override
    public boolean supportsUpdatePassword() {
        return propertyUserSourcePath != null;
    }

    @Override
    public synchronized void updatePassword( final Credential credential ) {
        final UsernamePasswordCredential usernamePassword = checkInstanceOf( "credential",
                                                                             credential,
                                                                             UsernamePasswordCredential.class );

        final String userName = usernamePassword.getUserName();
        final Object userPassword = usernamePassword.getPassword();
        final List<Role> userRoles = roles.get( userName );

        logger.info( "Updating Password for User: '" + userName + "'." );

        final Properties properties = loadProperties();
        properties.setProperty( userName,
                                makePropertiesFileValue( userPassword,
                                                         userRoles ) );
        saveProperties( properties );

        credentials.put( userName,
                         userPassword );
    }

    @Override
    public boolean supportsDeleteUser() {
        return propertyUserSourcePath != null;
    }

    @Override
    public synchronized void deleteUser( final Credential credential ) {
        final UserNameCredential username = checkInstanceOf( "credential",
                                                             credential,
                                                             UserNameCredential.class );

        final String userName = username.getUserName();

        logger.info( "Deleting User: '" + userName + "'." );

        final Properties properties = loadProperties();
        properties.remove( userName );
        saveProperties( properties );

        credentials.remove( userName );
        roles.remove( userName );
    }

    @Override
    public synchronized List<Role> loadRoles( final Principal principal ) {
        checkNotNull( "principle",
                      principal );
        loadProperties();
        final String userName = principal.getName();
        final List<Role> userRoles = new ArrayList<Role>();
        if ( roles.containsKey( userName ) ) {
            userRoles.addAll( roles.get( userName ) );
        }
        return userRoles;
    }

    @Override
    public boolean supportsRoleUpdates() {
        return propertyUserSourcePath != null;
    }

    @Override
    public synchronized void updateRoles( final Principal principal,
                                          final List<Role> newRoles ) {
        checkNotNull( "principal",
                      principal );
        checkNotNull( "newRoles",
                      newRoles );

        final String userName = principal.getName();
        final Properties properties = loadProperties();
        final String userPassword = parseUserPassword( properties.getProperty( userName ) );

        logger.info( "Updating Roles for User: '" + userName + "'. User Roles [" + dumpUserRoles( newRoles ) + "]." );

        properties.setProperty( userName,
                                makePropertiesFileValue( userPassword,
                                                         newRoles ) );
        saveProperties( properties );

        roles.put( userName,
                   newRoles );
    }

    private Properties loadProperties() {
        InputStream is = null;
        try {
            if ( options.containsKey( "usersPropertyFile" ) ) {
                propertyUserSourcePath = (String) options.get( "usersPropertyFile" );
                is = new FileInputStream( new File( propertyUserSourcePath ) );
            } else {
                is = Thread.currentThread().getContextClassLoader().getResourceAsStream( SecurityConstants.CONFIG_USERS_PROPERTIES );
            }

            if ( is == null ) {
                throw new RuntimeException( "Unable to find properties file." );
            }

            final Properties properties = new Properties();
            properties.load( is );
            return properties;

        } catch ( FileNotFoundException e ) {
            logger.error( e.getMessage(),
                          e );
            throw new RuntimeException( e );
        } catch ( IOException e ) {
            logger.error( e.getMessage(),
                          e );
            throw new RuntimeException( e );
        } finally {
            if ( is != null ) {
                try {
                    is.close();
                } catch ( Exception e ) {
                }
            }
        }
    }

    private void saveProperties( final Properties properties ) {
        OutputStream os = null;
        try {
            if ( propertyUserSourcePath == null ) {
                throw new RuntimeException( "org.uberfire.security.server.UberFireSecurityFilter's usersPropertyFile init parameter has not been set." );
            }

            os = new FileOutputStream( propertyUserSourcePath );

            if ( os == null ) {
                throw new RuntimeException( "Unable to find properties file." );
            }

            logger.info( "Saving User information to '" + propertyUserSourcePath + "'." );

            properties.store( os,
                              "" );

        } catch ( FileNotFoundException e ) {
            logger.error( e.getMessage(),
                          e );
            throw new RuntimeException( e );
        } catch ( IOException e ) {
            logger.error( e.getMessage(),
                          e );
            throw new RuntimeException( e );
        } finally {
            if ( os != null ) {
                try {
                    os.close();
                } catch ( Exception e ) {
                }
            }
        }
    }

    private String makePropertiesFileValue( final Object userPassword,
                                            final List<Role> userRoles ) {
        final StringBuffer sb = new StringBuffer( userPassword.toString() );
        sb.append( "," ).append( dumpUserRoles( userRoles ) );
        return sb.toString();
    }

    private Set<String> parseUserRoles( final String value ) {
        final Set<String> userRoles = new HashSet<String>();
        if ( value == null || value.isEmpty() ) {
            return userRoles;
        }

        final String[] result = value.split( "," );
        if ( result.length > 1 ) {
            for ( int i = 1; i < result.length; i++ ) {
                final String role = result[ i ];
                userRoles.add( role );
            }
        }
        return userRoles;
    }

    private String dumpUserRoles( final List<Role> userRoles ) {
        final StringBuffer sb = new StringBuffer();
        if ( !( userRoles == null || userRoles.isEmpty() ) ) {
            for ( Role role : userRoles ) {
                sb.append( role.getName() ).append( "," );
            }
            sb.delete( sb.length() - 1,
                       sb.length() );
        }
        return sb.toString();
    }

    private String parseUserPassword( final String value ) {
        if ( !( value == null || value.isEmpty() ) ) {
            final String[] result = value.split( "," );
            if ( result.length > 0 ) {
                return result[ 0 ];
            }
        }
        return "";
    }

}
