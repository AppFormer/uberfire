/*
 * Copyright 2014 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.uberfire.user.management.server;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.bus.server.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.security.Role;
import org.uberfire.security.auth.AuthenticationSource;
import org.uberfire.security.auth.Principal;
import org.uberfire.security.auth.RoleProvider;
import org.uberfire.security.impl.RoleImpl;
import org.uberfire.security.impl.auth.PrincipalImpl;
import org.uberfire.security.impl.auth.UserNameCredential;
import org.uberfire.security.impl.auth.UsernamePasswordCredential;
import org.uberfire.security.server.cdi.AppAuthenticationStores;
import org.uberfire.security.server.cdi.AppRoleProviders;
import org.uberfire.user.management.model.UserInformation;
import org.uberfire.user.management.model.UserManagerCapabilities;
import org.uberfire.user.management.model.UserManagerContent;
import org.uberfire.user.management.service.UserManagementService;

@Service
@ApplicationScoped
public class UserManagementServiceImpl implements UserManagementService {

    private static final Logger logger = LoggerFactory.getLogger( UserManagementServiceImpl.class );

    @Inject
    @AppRoleProviders
    private List<RoleProvider> availableRoleProviders;

    @Inject
    @AppAuthenticationStores
    private List<AuthenticationSource> availableAuthenticationSources;

    private RoleProvider installedRoleProvider;
    private AuthenticationSource installedAuthenticationSource;

    @PostConstruct
    public void initUserManager() {
        if ( availableRoleProviders == null || availableRoleProviders.isEmpty() ) {
            logger.info( "No RoleProviders available. Management of Users' Roles will therefore be disabled." );
        }
        if ( availableAuthenticationSources == null || availableAuthenticationSources.isEmpty() ) {
            logger.info( "No AuthenticationSources available. Management of Users will therefore be disabled." );
        }
        installedRoleProvider = availableRoleProviders.get( 0 );
        installedAuthenticationSource = availableAuthenticationSources.get( 0 );
    }

    @Override
    public boolean isUserManagerInstalled() {
        return installedAuthenticationSource != null;
    }

    @Override
    public UserManagerContent loadContent() {
        return new UserManagerContent( loadUserInformation(),
                                       loadUserManagerCapabilities() );
    }

    private UserManagerCapabilities loadUserManagerCapabilities() {
        if ( installedAuthenticationSource == null ) {
            return new UserManagerCapabilities( false,
                                                false,
                                                false,
                                                false );
        }
        if ( installedRoleProvider == null ) {
            return new UserManagerCapabilities( true,
                                                true,
                                                true,
                                                false );
        }
        return new UserManagerCapabilities( installedAuthenticationSource.supportsAddUser(),
                                            installedAuthenticationSource.supportsUpdatePassword(),
                                            installedAuthenticationSource.supportsDeleteUser(),
                                            installedRoleProvider.supportsRoleUpdates() );
    }

    private List<UserInformation> loadUserInformation() {
        if ( installedAuthenticationSource == null ) {
            throw new IllegalStateException( "No AuthenticationSource is available. Unable to retrieve user information." );
        }

        logger.info( "Retrieving list of users using '" + installedAuthenticationSource.getClass().getName() + "'." );

        final List<Principal> principals = installedAuthenticationSource.loadPrincipals();
        final List<UserInformation> userInformation = new ArrayList<UserInformation>();
        for ( Principal principal : principals ) {
            final String userName = principal.getName();
            userInformation.add( new UserInformation( userName,
                                                      getRoles( principal ) ) );
        }

        return userInformation;
    }

    private Set<String> getRoles( final Principal principal ) {
        final Set<String> userRoles = new HashSet<String>();
        if ( installedRoleProvider == null ) {
            logger.info( "No RoleProvider is available. Unable to retrieve user information." );
            return userRoles;
        }

        final List<Role> roles = installedRoleProvider.loadRoles( principal );
        logger.info( "Retrieving list of Users roles '" + installedRoleProvider.getClass().getName() + "'." );
        for ( Role role : roles ) {
            userRoles.add( role.getName() );
        }

        return userRoles;
    }

    @Override
    public void addUser( final UserInformation userInformation,
                         final String userPassword ) {
        if ( installedAuthenticationSource == null ) {
            throw new IllegalStateException( "No AuthenticationSource is available. Unable to add User." );
        }

        final String userName = userInformation.getUserName();
        logger.info( "Adding User '" + userName + "' using '" + installedAuthenticationSource.getClass().getName() + "'." );
        installedAuthenticationSource.addUser( new UsernamePasswordCredential( userName,
                                                                               userPassword ) );

        if ( installedRoleProvider == null ) {
            logger.info( "No RoleProvider is available. Unable to set User's roles." );
            return;
        }
        final Set<String> userRoles = userInformation.getUserRoles();
        logger.info( "Adding Roles for User '" + userName + "' using '" + installedRoleProvider.getClass().getName() + "'." );
        installedRoleProvider.updateRoles( new PrincipalImpl( userName ),
                                           convertRoles( userRoles ) );
    }

    private List<Role> convertRoles( final Set<String> userRoles ) {
        final List<Role> roles = new ArrayList<Role>();
        for ( String userRole : userRoles ) {
            roles.add( new RoleImpl( userRole ) );
        }
        return roles;
    }

    @Override
    public void updateUser( final UserInformation userInformation ) {
        if ( installedRoleProvider == null ) {
            throw new IllegalStateException( "No RoleProvider is available. Unable to update User's roles." );
        }

        final String userName = userInformation.getUserName();
        final Set<String> userRoles = userInformation.getUserRoles();
        logger.info( "Updating Roles for User '" + userName + "' using '" + installedRoleProvider.getClass().getName() + "'." );
        installedRoleProvider.updateRoles( new PrincipalImpl( userName ),
                                           convertRoles( userRoles ) );
    }

    @Override
    public void updateUser( final UserInformation userInformation,
                            final String userPassword ) {
        if ( installedAuthenticationSource == null ) {
            throw new IllegalStateException( "No AuthenticationSource is available. Unable to update User." );
        }

        final String userName = userInformation.getUserName();
        logger.info( "Updating User '" + userName + "' using '" + installedAuthenticationSource.getClass().getName() + "'." );
        installedAuthenticationSource.updatePassword( new UsernamePasswordCredential( userName,
                                                                                      userPassword ) );

        if ( installedRoleProvider == null ) {
            logger.info( "No RoleProvider is available. Unable to update User's roles." );
            return;
        }
        final Set<String> userRoles = userInformation.getUserRoles();
        logger.info( "Updating Roles for User '" + userName + "' using '" + installedRoleProvider.getClass().getName() + "'." );
        installedRoleProvider.updateRoles( new PrincipalImpl( userName ),
                                           convertRoles( userRoles ) );
    }

    @Override
    public void deleteUser( final UserInformation userInformation ) {
        if ( installedAuthenticationSource == null ) {
            throw new IllegalStateException( "No AuthenticationSource is available. Unable to delete User." );
        }

        final String userName = userInformation.getUserName();
        logger.info( "Deleting user '" + userName + "' using '" + installedAuthenticationSource.getClass().getName() + "'." );
        installedAuthenticationSource.deleteUser( new UserNameCredential( userName ) );
    }

}
