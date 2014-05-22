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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

import static org.uberfire.commons.validation.Preconditions.*;

public abstract class AbstractDatabaseAuthSource implements AuthenticationSource,
                                                            RoleProvider {

    private static final Logger LOG = LoggerFactory.getLogger( AbstractDatabaseAuthSource.class );

    private boolean alreadyInit = false;
    private String usersQuery;
    private String userAuthenticationQuery;
    private String updateUserPasswordQuery;
    private String addUserQuery;
    private String deleteUserQuery;
    private String addUserRolesQuery;
    private String deleteUserRolesQuery;
    private String userRolesQuery;

    public abstract Connection getConnection();

    public synchronized void initialize( final Map<String, ?> options ) {
        if ( !alreadyInit ) {
            usersQuery = "select " + options.get( "userField" ) + " from " + options.get( "userTable" );
            if ( options.containsKey( "usersQuery" ) ) {
                usersQuery = (String) options.get( "usersQuery" );
            }
            LOG.debug( "usersQuery = " + usersQuery );

            userAuthenticationQuery = "select 1 from " + options.get( "userTable" ) + " where " + options.get( "userField" ) + "= ? and " + options.get( "passwordField" ) + "= ?";
            if ( options.containsKey( "userAuthenticationQuery" ) ) {
                userAuthenticationQuery = (String) options.get( "userAuthenticationQuery" );
            }
            LOG.debug( "userAuthenticationQuery = " + userAuthenticationQuery );

            updateUserPasswordQuery = "update " + options.get( "userTable" ) + " set " + options.get( "passwordField" ) + " = ? where " + options.get( "userField" ) + " = ?";
            if ( options.containsKey( "updateUserPasswordQuery" ) ) {
                updateUserPasswordQuery = (String) options.get( "updateUserPasswordQuery" );
            }
            LOG.debug( "updateUserPasswordQuery = " + updateUserPasswordQuery );

            addUserQuery = "insert into " + options.get( "userTable" ) + "(" + options.get( "userField" ) + ", " + options.get( "passwordField" ) + ") values (?, ?)";
            if ( options.containsKey( "addUserQuery" ) ) {
                addUserQuery = (String) options.get( "addUserQuery" );
            }
            LOG.debug( "addUserQuery = " + addUserQuery );

            deleteUserQuery = "delete from " + options.get( "userTable" ) + " where " + options.get( "userField" ) + " = ?";
            if ( options.containsKey( "deleteUserQuery" ) ) {
                deleteUserQuery = (String) options.get( "deleteUserQuery" );
            }
            LOG.debug( "deleteUserQuery = " + deleteUserQuery );

            userRolesQuery = "select " + options.get( "userRoleRoleField" ) + " from " + options.get( "userRoleTable" ) + " where " + options.get( "userRoleUserField" ) + "= ?";
            if ( options.containsKey( "userRolesQuery" ) ) {
                userRolesQuery = (String) options.get( "userRolesQuery" );
            }
            LOG.debug( "userRolesQuery = " + userRolesQuery );

            addUserRolesQuery = "insert into " + options.get( "userRoleTable" ) + "(" + options.get( "userRoleUserField" ) + ", " + options.get( "userRoleRoleField" ) + ") values (?, ?)";
            if ( options.containsKey( "addUserRolesQuery" ) ) {
                addUserRolesQuery = (String) options.get( "addUserRolesQuery" );
            }
            LOG.debug( "addUserRolesQuery = " + addUserRolesQuery );

            deleteUserRolesQuery = "delete from " + options.get( "userRoleTable" ) + " where " + options.get( "userRoleUserField" ) + " = ?";
            if ( options.containsKey( "deleteUserRolesQuery" ) ) {
                deleteUserRolesQuery = (String) options.get( "deleteUserRolesQuery" );
            }
            LOG.debug( "deleteUserRolesQuery = " + deleteUserRolesQuery );

            alreadyInit = true;
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

        Connection connection = null;

        try {
            connection = getConnection();
            final PreparedStatement statement = connection.prepareStatement( userAuthenticationQuery );
            statement.setString( 1,
                                 usernamePassword.getUserName() );
            statement.setObject( 2,
                                 usernamePassword.getPassword() );
            final ResultSet queryResult = statement.executeQuery();
            final boolean result;
            if ( queryResult.next() ) {
                result = true;
            } else {
                result = false;
            }
            queryResult.close();
            statement.close();

            return result;

        } catch ( Exception ex ) {
            throw new IllegalStateException( ex );
        } finally {
            if ( connection != null ) {
                try {
                    connection.close();
                } catch ( SQLException e ) {
                    throw new IllegalStateException( e );
                }
            }
        }
    }

    @Override
    public List<Principal> loadPrincipals() {
        Connection connection = null;

        try {
            connection = getConnection();
            final PreparedStatement statement = connection.prepareStatement( usersQuery );
            final ResultSet queryResult = statement.executeQuery();
            final List<Principal> principals = new ArrayList<Principal>();
            while ( queryResult.next() ) {
                final String userName = queryResult.getString( 1 );
                principals.add( new PrincipalImpl( userName ) );
            }

            queryResult.close();
            statement.close();

            return principals;

        } catch ( Exception ex ) {
            throw new IllegalStateException( ex );
        } finally {
            if ( connection != null ) {
                try {
                    connection.close();
                } catch ( SQLException e ) {
                    throw new IllegalStateException( e );
                }
            }
        }
    }

    @Override
    public boolean supportsAddUser() {
        return true;
    }

    @Override
    public void addUser( final Credential credential ) {
        final UsernamePasswordCredential usernamePassword = checkInstanceOf( "credential",
                                                                             credential,
                                                                             UsernamePasswordCredential.class );

        Connection connection = null;

        try {
            connection = getConnection();
            final PreparedStatement statement = connection.prepareStatement( addUserQuery );
            statement.setString( 1,
                                 usernamePassword.getUserName() );
            statement.setObject( 2,
                                 usernamePassword.getPassword() );
            statement.executeQuery();
            statement.close();

        } catch ( Exception ex ) {
            throw new IllegalStateException( ex );
        } finally {
            if ( connection != null ) {
                try {
                    connection.close();
                } catch ( SQLException e ) {
                    throw new IllegalStateException( e );
                }
            }
        }
    }

    @Override
    public boolean supportsUpdatePassword() {
        return true;
    }

    @Override
    public void updatePassword( final Credential credential ) {
        final UsernamePasswordCredential usernamePassword = checkInstanceOf( "credential",
                                                                             credential,
                                                                             UsernamePasswordCredential.class );

        Connection connection = null;

        try {
            connection = getConnection();
            final PreparedStatement statement = connection.prepareStatement( updateUserPasswordQuery );
            statement.setObject( 1,
                                 usernamePassword.getPassword() );
            statement.setString( 2,
                                 usernamePassword.getUserName() );
            statement.executeQuery();
            statement.close();

        } catch ( Exception ex ) {
            throw new IllegalStateException( ex );
        } finally {
            if ( connection != null ) {
                try {
                    connection.close();
                } catch ( SQLException e ) {
                    throw new IllegalStateException( e );
                }
            }
        }
    }

    @Override
    public boolean supportsDeleteUser() {
        return true;
    }

    @Override
    public void deleteUser( final Credential credential ) {
        final UserNameCredential username = checkInstanceOf( "credential",
                                                             credential,
                                                             UserNameCredential.class );

        Connection connection = null;

        try {
            connection = getConnection();
            final PreparedStatement deleteUserStatement = connection.prepareStatement( deleteUserQuery );
            deleteUserStatement.setString( 1,
                                           username.getUserName() );
            deleteUserStatement.executeQuery();
            deleteUserStatement.close();

            //Don't assume the RDBMS has a cascade delete trigger
            final PreparedStatement deleteUserRolesStatement = connection.prepareStatement( deleteUserRolesQuery );
            deleteUserRolesStatement.setString( 1,
                                                username.getUserName() );
            deleteUserRolesStatement.executeQuery();
            deleteUserRolesStatement.close();

        } catch ( Exception ex ) {
            throw new IllegalStateException( ex );
        } finally {
            if ( connection != null ) {
                try {
                    connection.close();
                } catch ( SQLException e ) {
                    throw new IllegalStateException( e );
                }
            }
        }
    }

    @Override
    public List<Role> loadRoles( final Principal principal ) {
        checkNotNull( "principal",
                      principal );
        Connection connection = null;
        try {
            connection = getConnection();
            final PreparedStatement statement = connection.prepareStatement( userRolesQuery );
            statement.setString( 1,
                                 principal.getName() );
            final ResultSet queryResult = statement.executeQuery();
            final List<Role> roles = new ArrayList<Role>();
            while ( queryResult.next() ) {
                final String roleName = queryResult.getString( 1 );
                roles.add( new RoleImpl( roleName ) );
            }

            queryResult.close();
            statement.close();

            return roles;

        } catch ( Exception ex ) {
            throw new IllegalStateException( ex );
        } finally {
            if ( connection != null ) {
                try {
                    connection.close();
                } catch ( SQLException e ) {
                    throw new IllegalStateException( e );
                }
            }
        }
    }

    @Override
    public boolean supportsRoleUpdates() {
        return true;
    }

    @Override
    public void updateRoles( final Principal principal,
                             final List<Role> roles ) {
        checkNotNull( "principal",
                      principal );
        checkNotNull( "roles",
                      roles );

        Connection connection = null;
        try {
            connection = getConnection();
            final PreparedStatement deleteUserRolesStatement = connection.prepareStatement( deleteUserRolesQuery );
            deleteUserRolesStatement.setString( 1,
                                                principal.getName() );
            deleteUserRolesStatement.executeQuery();
            deleteUserRolesStatement.close();

            final PreparedStatement addUserRolesStatement = connection.prepareStatement( addUserRolesQuery );
            addUserRolesStatement.setString( 1,
                                             principal.getName() );
            for ( Role role : roles ) {
                addUserRolesStatement.setString( 2,
                                                 role.getName() );
                addUserRolesStatement.executeQuery();
            }
            addUserRolesStatement.close();

        } catch ( Exception ex ) {
            throw new IllegalStateException( ex );
        } finally {
            if ( connection != null ) {
                try {
                    connection.close();
                } catch ( SQLException e ) {
                    throw new IllegalStateException( e );
                }
            }
        }
    }

}
