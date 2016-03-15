/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
package org.uberfire.security.impl.authz;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jboss.errai.security.shared.api.Role;
import org.jboss.errai.security.shared.api.RoleImpl;
import org.jboss.errai.security.shared.api.identity.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.security.authz.AuthorizationPolicy;
import org.uberfire.security.authz.Permission;
import org.uberfire.security.authz.PermissionCollection;
import org.uberfire.security.authz.PermissionManager;

import static org.uberfire.security.authz.AuthorizationResult.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PermissionManagerTest {

    PermissionManager permissionManager;
    AuthorizationPolicy authorizationPolicy;
    Permission viewAll = new DotNamedPermission("resource.view", true);
    Permission denyAll = new DotNamedPermission("resource.view", false);
    Permission view1 = new DotNamedPermission("resource.view.1", true);
    Permission noView1 = new DotNamedPermission("resource.view.1", false);
    Permission view2 = new DotNamedPermission("resource.view.2", true);
    Permission view12 = new DotNamedPermission("resource.view.1.2", true);

    protected User createUserMock(String... roles) {
        User user = mock(User.class);
        Set<Role> roleSet = Stream.of(roles).map(RoleImpl::new).collect(Collectors.toSet());
        when(user.getRoles()).thenReturn(roleSet);
        when(user.getGroups()).thenReturn(null);
        return user;
    }

    @Before
    public void setUp() {
        permissionManager = new DefaultPermissionManager(new DefaultPermissionTypeRegistry());
        permissionManager.setAuthorizationPolicy(
                authorizationPolicy = spy(permissionManager.newAuthorizationPolicy()
                .role("viewAll").permission("resource.view", true)
                .role("noViewAll").permission("resource.view", false)
                .role("onlyView1", 5).permission("resource.view", false).permission("resource.view.1", true)
                .role("noView1").permission("resource.view.1", false)
                .role("onlyView12").permission("resource.view.1.2", true)
                .build()));
    }

    @Test
    public void testScenario1() {
        User user = createUserMock("viewAll");
        assertEquals(permissionManager.checkPermission(viewAll, user), ACCESS_GRANTED);
        assertEquals(permissionManager.checkPermission(view1, user), ACCESS_GRANTED);
        assertEquals(permissionManager.checkPermission(view2, user), ACCESS_GRANTED);
        assertEquals(permissionManager.checkPermission(noView1, user), ACCESS_DENIED);
    }

    @Test
    public void testScenario2() {
        User user = createUserMock("viewAll", "onlyView1");
        assertEquals(permissionManager.checkPermission(viewAll, user), ACCESS_DENIED);
        assertEquals(permissionManager.checkPermission(view1, user), ACCESS_GRANTED);
        assertEquals(permissionManager.checkPermission(view2, user), ACCESS_DENIED);
        assertEquals(permissionManager.checkPermission(noView1, user), ACCESS_DENIED);
    }

    @Test
    public void testScenario3() {
        User user = createUserMock("viewAll", "onlyView1", "noView1");
        assertEquals(permissionManager.checkPermission(viewAll, user), ACCESS_DENIED);
        assertEquals(permissionManager.checkPermission(view1, user), ACCESS_GRANTED);
        assertEquals(permissionManager.checkPermission(view2, user), ACCESS_DENIED);
        assertEquals(permissionManager.checkPermission(noView1, user), ACCESS_DENIED);
    }

    @Test
    public void testScenario4() {
        User user = createUserMock("viewAll", "noView1");
        assertEquals(permissionManager.checkPermission(viewAll, user), ACCESS_GRANTED);
        assertEquals(permissionManager.checkPermission(view1, user), ACCESS_DENIED);
        assertEquals(permissionManager.checkPermission(view2, user), ACCESS_GRANTED);
        assertEquals(permissionManager.checkPermission(noView1, user), ACCESS_GRANTED);
    }

    @Test
    public void testScenario5() {
        User user = createUserMock("onlyView1");
        assertEquals(permissionManager.checkPermission(viewAll, user), ACCESS_DENIED);
        assertEquals(permissionManager.checkPermission(view1, user), ACCESS_GRANTED);
        assertEquals(permissionManager.checkPermission(view2, user), ACCESS_DENIED);
        assertEquals(permissionManager.checkPermission(noView1, user), ACCESS_DENIED);
    }

    @Test
    public void testScenario6() {
        User user = createUserMock("noView1");
        assertEquals(permissionManager.checkPermission(viewAll, user), ACCESS_ABSTAIN);
        assertEquals(permissionManager.checkPermission(view1, user), ACCESS_DENIED);
        assertEquals(permissionManager.checkPermission(view2, user), ACCESS_ABSTAIN);
        assertEquals(permissionManager.checkPermission(noView1, user), ACCESS_GRANTED);
    }

    @Test
    public void testScenario7() {
        User user = createUserMock("onlyView1", "noView1");
        assertEquals(permissionManager.checkPermission(viewAll, user), ACCESS_DENIED);
        assertEquals(permissionManager.checkPermission(view1, user), ACCESS_GRANTED);
        assertEquals(permissionManager.checkPermission(view2, user), ACCESS_DENIED);
        assertEquals(permissionManager.checkPermission(noView1, user), ACCESS_DENIED);
    }

    @Test
    public void testScenario8() {
        User user = createUserMock("noView1", "onlyView12");
        assertEquals(permissionManager.checkPermission(viewAll, user), ACCESS_ABSTAIN);
        assertEquals(permissionManager.checkPermission(view1, user), ACCESS_DENIED);
        assertEquals(permissionManager.checkPermission(view2, user), ACCESS_ABSTAIN);
        assertEquals(permissionManager.checkPermission(view12, user), ACCESS_GRANTED);
        assertEquals(permissionManager.checkPermission(noView1, user), ACCESS_GRANTED);
    }

    @Test
    public void testPriority1() {
        User user = createUserMock("role1", "role2", "role3");
        AuthorizationPolicy policy = permissionManager.newAuthorizationPolicy()
                .role("role1", 1).permission("resource.view", true)
                .role("role2", 2).permission("resource.view", false)
                .role("role3", 3).permission("resource.view.1", true)
                .build();

        permissionManager.setAuthorizationPolicy(policy);
        assertEquals(permissionManager.checkPermission(view1, user), ACCESS_GRANTED);

        PermissionCollection pc = policy.getPermissions(user);
        Collection<Permission> permissions = pc.collection();
        assertEquals(permissions.size(), 2);
        assertTrue(permissions.contains(denyAll));
        assertTrue(permissions.contains(view1));
    }

    @Test
    public void testPriority2() {
        User user = createUserMock("role1", "role2", "role3");
        AuthorizationPolicy policy = permissionManager.newAuthorizationPolicy()
                .role("role1", 3).permission("resource.view", true)
                .role("role2", 2).permission("resource.view", false)
                .role("role3", 1).permission("resource.view.1", true)
                .build();

        permissionManager.setAuthorizationPolicy(policy);
        assertEquals(permissionManager.checkPermission(view1, user), ACCESS_GRANTED);

        PermissionCollection pc = policy.getPermissions(user);
        Collection<Permission> permissions = pc.collection();
        assertEquals(permissions.size(), 1);
        assertTrue(permissions.contains(viewAll));
    }

    @Test
    public void testPriority3() {
        User user = createUserMock("role1", "role2", "role3");
        AuthorizationPolicy policy = permissionManager.newAuthorizationPolicy()
                .role("role1", 1).permission("resource.view", true)
                .role("role2", 2).permission("resource.view", false)
                .role("role3", 1).permission("resource.view.1", true)
                .build();

        permissionManager.setAuthorizationPolicy(policy);
        assertEquals(permissionManager.checkPermission(view1, user), ACCESS_GRANTED);

        PermissionCollection pc = policy.getPermissions(user);
        Collection<Permission> permissions = pc.collection();
        assertEquals(permissions.size(), 2);
        assertTrue(permissions.contains(denyAll));
        assertTrue(permissions.contains(view1));
    }

    @Test
    public void testCacheHits() {
        User user = createUserMock("viewAll");
        permissionManager.checkPermission(viewAll, user);
        permissionManager.checkPermission(viewAll, user);
        permissionManager.checkPermission(viewAll, user);
        permissionManager.checkPermission(viewAll, user);
        verify(authorizationPolicy, times(1)).getPermissions(user);
    }
}
