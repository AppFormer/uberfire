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
package org.uberfire.backend.server.security;

import org.junit.Before;
import org.junit.Test;
import java.io.File;
import java.net.URL;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;

public class RoleMapperTest {

    @Before
    public void setUp() {
        
        // load roles available to the Web application
        URL fileURL = Thread.currentThread().getContextClassLoader().getResource("WEB-INF/classes/security-policy.properties");
        String homeDir = new File(fileURL.getPath()).getParentFile().getParentFile().getParent();
        WebAppSettings.get().setRootDir(homeDir);
        RoleRegistry.get().clear();
        RoleLoader roleLoader = new RoleLoader();
        roleLoader.registerRolesFromwWebXml();
    }

    
    @Test
    public void testRolesMappedToPrincipals() {
        RolePrincipalsRegistry.get().clear();
        RoleMapper roleMapper = new RoleMapper();
        roleMapper.registerRolePrincipals();
        Map<String, Set<String>> rolePrincipals = RolePrincipalsRegistry.get().getRegisteredRolePrincipals();
        //role2 is mapped in file and ldap providers so expectec count is 2
        assertEquals("Unexpected number of Principals mapped to role.", 2, rolePrincipals.get("role2").size());
    }

    @Test
    public void testRolesMappedToRegexPattern() {
        RolePrincipalsRegistry.get().clear();
        RoleMapper roleMapper = new RoleMapper();
        roleMapper.registerRolePrincipals();
        Set<String> rolePrincipals = RolePrincipalsRegistry.get().getRegisteredRolePrincipals("regex");

        // one regex expression is defined with a variable placeholder for the role.  Therefore, one pattern per role is registered.
        assertEquals("Unexpected number of regex patterns.  Should be equal to the number of available roles.", 2, rolePrincipals.size());
    }
}
