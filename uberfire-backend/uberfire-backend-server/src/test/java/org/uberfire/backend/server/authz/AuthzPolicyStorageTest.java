/**
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
package org.uberfire.backend.server.authz;

import java.net.URL;
import java.util.List;
import java.util.Set;

import org.jboss.errai.security.shared.api.Role;
import org.jboss.errai.security.shared.api.RoleImpl;
import org.junit.Before;
import org.junit.Test;
import org.uberfire.security.authz.AuthorizationPolicy;
import org.uberfire.security.authz.AuthorizationResult;
import org.uberfire.security.authz.Permission;
import org.uberfire.security.authz.PermissionCollection;
import org.uberfire.security.authz.PermissionManager;
import org.uberfire.security.authz.PermissionTypeRegistry;
import org.uberfire.security.impl.authz.DefaultPermissionManager;
import org.uberfire.security.impl.authz.DefaultPermissionTypeRegistry;

import static org.junit.Assert.*;

public class AuthzPolicyStorageTest {

    AuthorizationPolicyStorage authzPolicyStorage;

    @Before
    public void setUp() {
        PermissionTypeRegistry permissionTypeRegistry = new DefaultPermissionTypeRegistry();
        PermissionManager permissionManager = new DefaultPermissionManager(permissionTypeRegistry);
        authzPolicyStorage = new AuthorizationPolicyStorage(permissionManager);
    }

    @Test
    public void testHomeEntry() {
        List<String> tokens = authzPolicyStorage.parseKey("role.admin.home");
        assertEquals(tokens.size(), 3);
        assertEquals(tokens.get(0), "role");
        assertEquals(tokens.get(1), "admin");
        assertEquals(tokens.get(2), "home");
    }

    @Test
    public void testPriorityEntry() {
        List<String> tokens = authzPolicyStorage.parseKey("role.admin.priority");
        assertEquals(tokens.size(), 3);
        assertEquals(tokens.get(0), "role");
        assertEquals(tokens.get(1), "admin");
        assertEquals(tokens.get(2), "priority");
    }

    @Test
    public void testPermissionEntry() {
        List<String> tokens = authzPolicyStorage.parseKey("role.admin.permission.perspective.view");
        assertEquals(tokens.size(), 4);
        assertEquals(tokens.get(0), "role");
        assertEquals(tokens.get(1), "admin");
        assertEquals(tokens.get(2), "permission");
        assertEquals(tokens.get(3), "perspective.view");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidEntry1() {
        authzPolicyStorage.parseKey("role..priority");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidEntry2() {
        authzPolicyStorage.parseKey(".admin.priority");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidEntry3() {
        authzPolicyStorage.parseKey("role");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidPolicy() {
        URL fileURL = Thread.currentThread().getContextClassLoader().getResource("WEB-INF/classes/invalid-policy.properties");
        authzPolicyStorage.loadPolicy(fileURL);
    }

    @Test
    public void testPolicyLoad() {
        URL fileURL = Thread.currentThread().getContextClassLoader().getResource("WEB-INF/classes/security-policy.properties");
        AuthorizationPolicy policy = authzPolicyStorage.loadPolicy(fileURL);

        Set<Role> roles = policy.getRoles();
        assertEquals(roles.size(), 3);

        Role adminRole = new RoleImpl("admin");
        PermissionCollection permissions = policy.getPermissions(adminRole);
        assertTrue(roles.contains(adminRole));
        assertEquals(policy.getRoleDescription(adminRole), "Administrator");
        assertEquals(policy.getPriority(adminRole), 0);
        assertEquals(permissions.collection().size(), 2);

        Permission permission = permissions.get("perspective.view");
        assertNotNull(permission);
        assertEquals(permission.getResult(), AuthorizationResult.ACCESS_GRANTED);

        permission = permissions.get("perspective.view.SimplePerspective");
        assertNotNull(permission);
        assertEquals(permission.getResult(), AuthorizationResult.ACCESS_DENIED);


        Role userRole = new RoleImpl("user");
        permissions = policy.getPermissions(userRole);
        assertTrue(roles.contains(userRole));
        assertEquals(policy.getRoleDescription(userRole), "End user");
        assertEquals(permissions.collection().size(), 3);

        permission = permissions.get("perspective.view");
        assertNotNull(permission);
        assertEquals(permission.getResult(), AuthorizationResult.ACCESS_DENIED);

        permission = permissions.get("perspective.view.HomePerspective");
        assertNotNull(permission);
        assertEquals(permission.getResult(), AuthorizationResult.ACCESS_GRANTED);

        permission = permissions.get("perspective.view.SimplePerspective");
        assertNotNull(permission);
        assertEquals(permission.getResult(), AuthorizationResult.ACCESS_GRANTED);

        Role managerRole = new RoleImpl("manager");
        permissions = policy.getPermissions(managerRole);
        assertTrue(roles.contains(managerRole));
        assertEquals(policy.getRoleDescription(managerRole), "Manager");
        assertEquals(permissions.collection().size(), 1);

        permission = permissions.get("perspective.view");
        assertNotNull(permission);
        assertEquals(permission.getResult(), AuthorizationResult.ACCESS_GRANTED);
    }
}
