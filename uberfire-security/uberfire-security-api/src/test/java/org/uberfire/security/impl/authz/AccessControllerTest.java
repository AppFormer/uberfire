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

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jboss.errai.security.shared.api.Role;
import org.jboss.errai.security.shared.api.RoleImpl;
import org.jboss.errai.security.shared.api.identity.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.security.Resource;
import org.uberfire.security.ResourceRef;
import org.uberfire.security.authz.AuthorizationResult;
import org.uberfire.security.authz.Permission;
import org.uberfire.security.authz.PermissionManager;
import org.uberfire.security.authz.PermissionType;
import org.uberfire.security.authz.PermissionTypeRegistry;
import org.uberfire.security.ResourceType;
import org.uberfire.security.authz.RuntimeResource;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AccessControllerTest {

    @Mock
    Resource perspective1;

    @Mock
    Resource perspective2;

    @Mock
    ResourceType perspectiveType;

    @Mock
    Resource resource1;

    @Mock
    RuntimeResource resource2;

    @Mock
    Resource menuPerspective1;

    @Mock
    Resource menuPerspective2;

    User user;
    DefaultAccessController accessController;
    PermissionManager permissionManager;
    PermissionTypeRegistry permissionTypeRegistry;

    protected User createUserMock(String... roles) {
        User user = mock(User.class);
        Set<Role> roleSet = Stream.of(roles).map(RoleImpl::new).collect(Collectors.toSet());
        when(user.getRoles()).thenReturn(roleSet);
        when(user.getGroups()).thenReturn(null);
        return user;
    }

    @Before
    public void setUp() {
        user = createUserMock("admin");

        when(perspectiveType.toString()).thenReturn("perspective");

        when(resource1.getDependencies()).thenReturn(null);
        when(resource2.getDependencies()).thenReturn(null);

        when(perspective1.getIdentifier()).thenReturn("p1");
        when(perspective2.getIdentifier()).thenReturn("p2");
        when(perspective1.getDependencies()).thenReturn(null);
        when(perspective2.getDependencies()).thenReturn(null);
        when(perspective1.getType()).thenReturn(perspectiveType);
        when(perspective2.getType()).thenReturn(perspectiveType);

        when(menuPerspective1.getDependencies()).thenReturn(Arrays.asList(perspective1));
        when(menuPerspective2.getDependencies()).thenReturn(Arrays.asList(perspective2));

        permissionTypeRegistry = new DefaultPermissionTypeRegistry();
        permissionManager = spy(new DefaultPermissionManager(permissionTypeRegistry));
        accessController = new DefaultAccessController(permissionManager);

        permissionManager.setAuthorizationPolicy(
                permissionManager.newAuthorizationPolicy()
                        .role("admin")
                        .permission("perspective.view", true)
                        .permission("perspective.view.p2", false)
                        .permission("custom.resource2", true)
                        .build());
    }

    @Test(expected = IllegalStateException.class)
    public void avoidPermissionTypesCollision() {
        PermissionType permissionType = mock(PermissionType.class);
        when(permissionType.getType()).thenReturn("type");
        permissionTypeRegistry.register(permissionType);
        permissionTypeRegistry.register(permissionType);
    }

    public void unknownResourceTest() {
        AuthorizationResult result = accessController.checkAccess(resource1, user);
        assertEquals(result, AuthorizationResult.ACCESS_ABSTAIN);
    }

    @Test
    public void testNonManagedResource() {
        AuthorizationResult result = accessController.checkAccess(resource2, user);
        assertEquals(result, AuthorizationResult.ACCESS_ABSTAIN);
        verify(permissionManager, never()).checkPermission(any(Permission.class), any(User.class));
    }

    @Test
    public void testCustomResourceAccess() {
        when(resource2.getIdentifier()).thenReturn("custom.resource2");
        AuthorizationResult result = accessController.checkAccess(resource2, user);
        assertEquals(result, AuthorizationResult.ACCESS_GRANTED);
        verify(permissionManager).checkPermission(any(Permission.class), any(User.class));
    }

    @Test
    public void testPerspectiveAccessGranted() {
        AuthorizationResult result = accessController.checkAccess(perspective1, user);
        assertEquals(result, AuthorizationResult.ACCESS_GRANTED);
        verify(permissionManager).checkPermission(any(Permission.class), any(User.class));
    }

    @Test
    public void testPerspectiveAccessDenied() {
        AuthorizationResult result = accessController.checkAccess(perspective2, user);
        assertEquals(result, AuthorizationResult.ACCESS_DENIED);
        verify(permissionManager).checkPermission(any(Permission.class), any(User.class));
    }

    @Test
    public void testMenuItemGranted() {
        AuthorizationResult result = accessController.checkAccess(menuPerspective1, user);
        assertEquals(result, AuthorizationResult.ACCESS_GRANTED);
        verify(permissionManager).checkPermission(any(Permission.class), any(User.class));
    }

    @Test
    public void testMenuItemDenied() {
        AuthorizationResult result = accessController.checkAccess(menuPerspective2, user);
        assertEquals(result, AuthorizationResult.ACCESS_DENIED);
    }

    @Test
    public void testMenuItemAbstain() {
        permissionManager.setAuthorizationPolicy(null);
        AuthorizationResult result = accessController.checkAccess(menuPerspective1, user);
        assertEquals(result, AuthorizationResult.ACCESS_ABSTAIN);
    }

    @Test
    public void testMenuGroupGranted() {
        Resource resource = new ResourceRef(null, null, Arrays.asList(menuPerspective1, menuPerspective2));
        AuthorizationResult result = accessController.checkAccess(resource, user);
        assertEquals(result, AuthorizationResult.ACCESS_GRANTED);

        resource = new ResourceRef(null, null, Arrays.asList(menuPerspective1));
        result = accessController.checkAccess(resource, user);
        assertEquals(result, AuthorizationResult.ACCESS_GRANTED);
    }

    @Test
    public void testMenuGroupDenied() {
        Resource resource = new ResourceRef(null, null, Arrays.asList(menuPerspective2));
        AuthorizationResult result = accessController.checkAccess(resource, user);
        assertEquals(result, AuthorizationResult.ACCESS_DENIED);
    }

    @Test
    public void testEmptyMenuDenied() {
        Resource resource = new ResourceRef(null, null, Arrays.asList());
        AuthorizationResult result = accessController.checkAccess(resource, user);
        assertEquals(result, AuthorizationResult.ACCESS_DENIED);
    }
}
