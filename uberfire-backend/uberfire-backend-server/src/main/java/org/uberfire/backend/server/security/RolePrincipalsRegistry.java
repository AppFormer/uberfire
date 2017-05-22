/*
 * Copyright 2016 JBoss, by Red Hat, Inc
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

import org.jboss.errai.security.shared.api.Role;
import org.jboss.errai.security.shared.api.RoleImpl;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * It holds the collection of Role to Principal mappings that the platform security
 * services use. 
 */
public class RolePrincipalsRegistry {

    private HashMap<String, Set<String>> rolePrincipals = new HashMap<String, Set<String>>();

    private static RolePrincipalsRegistry instance = null;

    private RolePrincipalsRegistry() {
    }

    /**
     * Returns singleton instance of the registry to be able to register role to principals
     */
    public static RolePrincipalsRegistry get() {
        if (instance == null) {
            instance = new RolePrincipalsRegistry();
        }
        return instance;
    }

    /**
     * Registers given <code>role</code> into the registry
     */
    public void registerRolePrincipals(String role, Set<String> principals) {
        if (rolePrincipals.get(role) != null) {
            rolePrincipals.get(role).addAll(principals);
        } else {
            rolePrincipals.put(role, principals);
        }
    }

    /**
     * Gets a a role instance by its name or null if not found.
     */
    public Set<String> getRegisteredRolePrincipals(String name) {
        return rolePrincipals.get(name);
    }

    /**
    /**
     * Returns unmodifiable copy of all reqistered role to principal mappings
     */
    public Map<String, Set<String>> getRegisteredRolePrincipals() {
        return Collections.unmodifiableMap(this.rolePrincipals);
    }

    /**
     * Clears the registry.
     */
    public void clear() {
        this.rolePrincipals.clear();
    }
}
