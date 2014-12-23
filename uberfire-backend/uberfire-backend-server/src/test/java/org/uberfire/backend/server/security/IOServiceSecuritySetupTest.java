package org.uberfire.backend.server.security;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import javax.enterprise.inject.Instance;

import org.jboss.errai.security.shared.api.identity.User;
import org.jboss.errai.security.shared.service.AuthenticationService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.security.FileSystemAuthenticator;
import org.uberfire.java.nio.security.FileSystemAuthorizer;
import org.uberfire.java.nio.security.FileSystemUser;
import org.uberfire.security.authz.AuthorizationManager;

@RunWith(MockitoJUnitRunner.class)
public class IOServiceSecuritySetupTest {

    @Mock
    Instance<AuthenticationService> authenticationManagers;

    @Mock
    Instance<AuthorizationManager> authorizationManagers;

    @InjectMocks
    IOServiceSecuritySetup setupBean;

    @Before
    public void setup() {
        // this is the fallback configuration when no @IOSecurityAuth bean is found
        System.setProperty( "org.uberfire.io.auth", MockAuthenticationService.class.getName() );
    }
    
    @After
    public void teardown() {
        System.clearProperty( "org.uberfire.io.auth" );
    }
    
    @Test
    public void testSystemPropertyAuthConfig() throws Exception {
        when( authenticationManagers.isUnsatisfied() ).thenReturn( true );
        when( authorizationManagers.isUnsatisfied() ).thenReturn( true );
        
        setupBean.setup();

        // setup should have initialized the authenticator and authorizer to their defaults
        MockSecuredFilesystemProvider mockFsp = MockSecuredFilesystemProvider.LATEST_INSTANCE;
        assertNotNull( mockFsp.authenticator );
        assertNotNull( mockFsp.authorizer );
        
        // and they should work :)
        FileSystemUser user = mockFsp.authenticator.authenticate( "fake", "fake" );
        assertEquals( MockAuthenticationService.FAKE_USER.getIdentifier(),
                      user.getName() );
        assertTrue( mockFsp.authorizer.authorize( mock( FileSystem.class ),
                                                  user ) );
    }

    @Test
    public void testCustomAuthenticatorBean() throws Exception {
        when( authorizationManagers.isUnsatisfied() ).thenReturn( true );

        // this simulates the existence of a @IOServiceAuth AuthenticationService bean
        when( authenticationManagers.isUnsatisfied() ).thenReturn( false );
        AuthenticationService mockAuthenticationService = mock( AuthenticationService.class );
        when( authenticationManagers.get() ).thenReturn( mockAuthenticationService );
        
        setupBean.setup();

        FileSystemAuthenticator authenticator = MockSecuredFilesystemProvider.LATEST_INSTANCE.authenticator;
        authenticator.authenticate( "fake", "fake" );
        
        // make sure the call went to the one we provided
        verify( mockAuthenticationService ).login( "fake", "fake" );
    }

    @Test
    public void testCustomAuthorizerBean() throws Exception {
        when( authenticationManagers.isUnsatisfied() ).thenReturn( true );

        // this simulates the existence of a @IOServiceAuthz AuthorizationManager bean
        when( authorizationManagers.isUnsatisfied() ).thenReturn( false );
        AuthorizationManager mockAuthorizationManager = mock( AuthorizationManager.class );
        when( authorizationManagers.get() ).thenReturn( mockAuthorizationManager );
        
        setupBean.setup();

        FileSystemAuthorizer installedAuthorizer = MockSecuredFilesystemProvider.LATEST_INSTANCE.authorizer;
        FileSystemAuthenticator installedAuthenticator = MockSecuredFilesystemProvider.LATEST_INSTANCE.authenticator;
        FileSystem mockfs = mock( FileSystem.class );
        FileSystemUser fileSystemUser = installedAuthenticator.authenticate( "fake", "fake" );

        installedAuthorizer.authorize( mockfs, fileSystemUser );
        // make sure the call went to the one we provided
        verify( mockAuthorizationManager ).authorize( any( FileSystemResourceAdaptor.class),
                                                      any( User.class ) );
    }

}
