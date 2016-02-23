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

import java.util.HashMap;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.security.shared.api.identity.User;
import org.uberfire.security.authz.AuthorizationPolicy;
import org.uberfire.security.authz.AuthorizationResult;
import org.uberfire.security.authz.Permission;
import org.uberfire.security.authz.PermissionCollection;
import org.uberfire.security.authz.PermissionManager;
import org.uberfire.security.authz.PermissionType;
import org.uberfire.security.authz.PermissionTypeRegistry;

import static org.uberfire.security.authz.AuthorizationResult.*;

@ApplicationScoped
public class DefaultPermissionManager implements PermissionManager {

    private PermissionTypeRegistry permissionTypeRegistry;
    private AuthorizationPolicy authorizationPolicy;
    private Map<Permission,AuthorizationResult> resultCache = new HashMap<>();

    public DefaultPermissionManager() {
    }

    @Inject
    public DefaultPermissionManager(PermissionTypeRegistry permissionTypeRegistry) {
        this.permissionTypeRegistry = permissionTypeRegistry;
    }

    public AuthorizationPolicy getAuthorizationPolicy() {
        return authorizationPolicy;
    }

    public void setAuthorizationPolicy(AuthorizationPolicy authorizationPolicy) {
        this.authorizationPolicy = authorizationPolicy;
        this.resultCache.clear();
    }

    @Override
    public AuthorizationPolicyBuilder newAuthorizationPolicy() {
        return new AuthorizationPolicyBuilder(permissionTypeRegistry);
    }

    @Override
    public Permission createPermission(String name, Boolean granted) {
        PermissionType permissionType = permissionTypeRegistry.resolve(name);
        return permissionType.createPermission(name, granted);
    }

    @Override
    public AuthorizationResult checkPermission(Permission permission, User user) {

        if (authorizationPolicy == null || permission == null) {
            return ACCESS_ABSTAIN;
        }
        AuthorizationResult cachedResult = resultCache.get(permission);
        if (cachedResult != null) {
            return cachedResult;
        }
        PermissionCollection userPermissions = authorizationPolicy.getPermissions(user);
        if (userPermissions == null) {
            return cacheResult(permission, ACCESS_ABSTAIN);
        }
        Permission existing = userPermissions.get(permission.getName());
        if (existing != null) {
            return cacheResult(permission, existing.getResult().equals(permission.getResult()) ? ACCESS_GRANTED : ACCESS_DENIED);
        }
        if (userPermissions.implies(permission)) {
            return cacheResult(permission, ACCESS_GRANTED);
        }
        Permission inverted = permission.clone();
        inverted.setResult(inverted.getResult().invert());
        if (userPermissions.implies(inverted)) {
            return cacheResult(permission, ACCESS_DENIED);
        }
        return cacheResult(permission, ACCESS_ABSTAIN);
    }

    protected AuthorizationResult cacheResult(Permission p, AuthorizationResult result) {
        resultCache.put(p, result);
        return result;
    }
}
