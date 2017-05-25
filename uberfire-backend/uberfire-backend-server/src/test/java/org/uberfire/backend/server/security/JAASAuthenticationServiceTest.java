package org.uberfire.backend.server.security;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.inOrder;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import java.security.Principal;
import java.security.acl.Group;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.security.auth.Subject;
import javax.security.auth.login.LoginContext;

import org.jboss.errai.security.shared.api.GroupImpl;
import org.jboss.errai.security.shared.api.RoleImpl;
import org.jboss.errai.security.shared.api.identity.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import org.uberfire.backend.server.security.adapter.WebSphereGroupsAdapter;
import org.uberfire.security.authz.adapter.GroupsAdapter;

@RunWith(PowerMockRunner.class)
@PrepareForTest({JAASAuthenticationService.class})
@PowerMockIgnore("javax.security.*")
public class JAASAuthenticationServiceTest {

    private JAASAuthenticationService tested;

    @Before
    public void setup() {
        RoleRegistry.get().clear();
        RoleRegistry.get().registerRole( "admin" );
        RoleRegistry.get().registerRole( "role1" );

        tested = spy( new JAASAuthenticationService( JAASAuthenticationService.DEFAULT_DOMAIN ) );
    }

    @Test
    public void testNoLogin() throws Exception {
        assertEquals( User.ANONYMOUS,
                      tested.getUser() );
    }

    @Test
    public void testGetAnnonymous() throws Exception {
        assertFalse( tested.isLoggedIn() );
    }

    @Test
    public void testLogin() throws Exception {
        String username = "user1";
        String password = "password1";

        Set<Principal> principals = mockPrincipals( "admin",
                                                    "role1",
                                                    "group1" );
        Subject subject = new Subject();
        subject.getPrincipals().addAll( principals );
        LoginContext loginContext = mock( LoginContext.class );
        when( loginContext.getSubject() ).thenReturn( subject );
        doReturn( loginContext ).when( tested ).createLoginContext( anyString(),
                                                                    anyString() );

        User user = tested.login( username,
                                  password );

        assertNotNull( user );
        assertEquals( username,
                      user.getIdentifier() );
        assertEquals( 2,
                      user.getRoles().size() );
        assertTrue( user.getRoles().contains( new RoleImpl( "admin" ) ) );
        assertTrue( user.getRoles().contains( new RoleImpl( "role1" ) ) );
        assertEquals( 1,
                      user.getGroups().size() );
        assertTrue( user.getGroups().contains( new GroupImpl( "group1" ) ) );
    }

    @Test
    public void testLoginSwitchesClassloaderForJsm() throws Exception {
        mockStatic( JAASAuthenticationService.class );
        mockStatic( Thread.class );
        mockStatic( System.class );

        final ClassLoader tccl = mock( ClassLoader.class );
        final Thread thread = mock( Thread.class );

        when( Thread.currentThread() ).thenReturn( thread );
        when( System.getSecurityManager() ).thenReturn( mock( SecurityManager.class ) );
        when( thread.getContextClassLoader() ).thenReturn( tccl );

        String username = "user1";
        String password = "password1";
        Set<Principal> principals = mockPrincipals( "admin",
                                                    "role1",
                                                    "group1" );
        Subject subject = new Subject();
        subject.getPrincipals().addAll( principals );
        LoginContext loginContext = mock( LoginContext.class );
        when( loginContext.getSubject() ).thenReturn( subject );
        doReturn( loginContext ).when( tested ).createLoginContext( anyString(),
                                                                    anyString() );

        tested.login( username,
                      password );

        InOrder inOrder = inOrder( thread );
        inOrder.verify( thread ).setContextClassLoader( tested.getClass().getClassLoader() );
        inOrder.verify( thread ).setContextClassLoader( same( tccl ) );
    }

    @Test
    public void testLoginSubjectGroups() throws Exception {
        String username = "user1";
        String password = "password1";

        Set<Principal> principals = mockPrincipals( "admin",
                                                    "role1",
                                                    "group1" );
        Group aclGroup = mock( Group.class );
        doReturn( JAASAuthenticationService.DEFAULT_ROLE_PRINCIPLE_NAME ).when( aclGroup ).getName();
        Set<Principal> aclGroups = mockPrincipals( "g1",
                                                   "g2" );
        Enumeration<? extends Principal> aclGroupsEnum = Collections.enumeration( aclGroups );
        doReturn( aclGroupsEnum ).when( aclGroup ).members();
        Subject subject = new Subject();
        subject.getPrincipals().addAll( principals );
        subject.getPrincipals().add( aclGroup );
        LoginContext loginContext = mock( LoginContext.class );
        when( loginContext.getSubject() ).thenReturn( subject );
        doReturn( loginContext ).when( tested ).createLoginContext( anyString(),
                                                                    anyString() );

        User user = tested.login( username,
                                  password );

        assertNotNull( user );
        assertEquals( username,
                      user.getIdentifier() );
        assertEquals( 2,
                      user.getRoles().size() );
        assertTrue( user.getRoles().contains( new RoleImpl( "admin" ) ) );
        assertTrue( user.getRoles().contains( new RoleImpl( "role1" ) ) );
        assertEquals( 3,
                      user.getGroups().size() );
        assertTrue( user.getGroups().contains( new GroupImpl( "group1" ) ) );
        assertTrue( user.getGroups().contains( new GroupImpl( "g1" ) ) );
        assertTrue( user.getGroups().contains( new GroupImpl( "g2" ) ) );
    }

    /*
     * RHBPMS-4609: When LDAP provides the authorized groups on Websphere the groups returned are not filtered to simple name (i.e, group = admin) when accessed from non-web clients such as ssh.  
     * This test checks that the regex pattern configured as a System property will recognize these unfiltered groups as authorized roles. 
     */
    @Test
    public void testLoginSubjectGroupsWithRegexRoleMapper() throws Exception {

        RoleRegistry.get().clear();
        RoleRegistry.get().registerRole( "admin" );
        RoleRegistry.get().registerRole( "user" );
        RoleRegistry.get().registerRole( "role1" );

        System.setProperty( "org.uberfire.regex.role_mapper",
                            "cn[\\ ]*=[\\ ]*role" );
        JAASAuthenticationService sshClient = spy( new JAASAuthenticationService( JAASAuthenticationService.DEFAULT_DOMAIN ) );

        String username = "user1";
        String password = "password1";

        Set<Principal> principals = mockPrincipals( "ldap",
                                                    "role1" );
        Group aclGroup = mock( Group.class );
        doReturn( JAASAuthenticationService.DEFAULT_ROLE_PRINCIPLE_NAME ).when( aclGroup ).getName();
        Set<Principal> aclGroups = mockPrincipals( "g1",
                                                   "g2" );
        Enumeration<? extends Principal> aclGroupsEnum = Collections.enumeration( aclGroups );
        doReturn( aclGroupsEnum ).when( aclGroup ).members();
        Subject subject = new Subject();
        subject.getPrincipals().addAll( principals );
        subject.getPrincipals().add( aclGroup );
        LoginContext loginContext = mock( LoginContext.class );
        when( loginContext.getSubject() ).thenReturn( subject );
        doReturn( loginContext ).when( sshClient ).createLoginContext( anyString(),
                                                                       anyString() );

        WebSphereGroupsAdapter wsga = mock( WebSphereGroupsAdapter.class );
        List<org.jboss.errai.security.shared.api.Group> groups = mockGroups( "cn=admin,ou=groups,dc=com,dc=example",
                                                                             "cn=user" );
        doReturn( groups ).when( wsga ).getGroups( anyString(),
                                                   anyObject() );

        List<GroupsAdapter> list = new ArrayList<GroupsAdapter>();
        list.add( wsga );
        Whitebox.setInternalState( sshClient,
                                   "groupsAdapters",
                                   list.iterator() );

        User user = sshClient.login( username,
                                     password );

        assertNotNull( user );
        assertEquals( username,
                      user.getIdentifier() );
        assertEquals( 3,
                      user.getRoles().size() );
        assertTrue( user.getRoles().contains( new RoleImpl( "admin" ) ) );
        assertTrue( user.getRoles().contains( new RoleImpl( "user" ) ) );
        assertTrue( user.getRoles().contains( new RoleImpl( "role1" ) ) );
        assertEquals( 3,
                      user.getGroups().size() );
        assertTrue( user.getGroups().contains( new GroupImpl( "g1" ) ) );
        assertTrue( user.getGroups().contains( new GroupImpl( "g2" ) ) );
        assertTrue( user.getGroups().contains( new GroupImpl( "ldap" ) ) );
    }

    @Test
    public void testLoggedIn() throws Exception {
        String username = "user1";
        String password = "password1";

        Set<Principal> principals = mockPrincipals( "admin" );
        Subject subject = new Subject();
        subject.getPrincipals().addAll( principals );
        LoginContext loginContext = mock( LoginContext.class );
        when( loginContext.getSubject() ).thenReturn( subject );
        doReturn( loginContext ).when( tested ).createLoginContext( anyString(),
                                                                    anyString() );

        tested.login( username,
                      password );

        assertTrue( tested.isLoggedIn() );
    }

    @Test
    public void testGetUser() throws Exception {
        String username = "user1";
        String password = "password1";
        RoleRegistry.get().registerRole( "admin" );
        Set<Principal> principals = mockPrincipals( "admin" );
        Subject subject = new Subject();
        subject.getPrincipals().addAll( principals );
        LoginContext loginContext = mock( LoginContext.class );
        when( loginContext.getSubject() ).thenReturn( subject );
        doReturn( loginContext ).when( tested ).createLoginContext( anyString(),
                                                                    anyString() );

        User user = tested.login( username,
                                  password );
        User user1 = tested.getUser();

        assertEquals( user,
                      user1 );
    }

    private Set<Principal> mockPrincipals( String... names ) {
        Set<Principal> principals = new HashSet<Principal>();
        for ( String name : names ) {
            Principal p1 = mock( Principal.class );
            when( p1.getName() ).thenReturn( name );
            principals.add( p1 );
        }
        return principals;
    }

    private List<org.jboss.errai.security.shared.api.Group> mockGroups( String... names ) {
        ArrayList<org.jboss.errai.security.shared.api.Group> groups = new ArrayList<org.jboss.errai.security.shared.api.Group>();
        for ( String name : names ) {
            org.jboss.errai.security.shared.api.Group g = mock( org.jboss.errai.security.shared.api.Group.class );
            when( g.getName() ).thenReturn( name );
            groups.add( g );
        }
        return groups;
    }
}
