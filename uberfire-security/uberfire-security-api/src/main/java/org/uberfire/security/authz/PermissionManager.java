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
package org.uberfire.security.authz;

import org.jboss.errai.security.shared.api.identity.User;
import org.uberfire.security.impl.authz.AuthorizationPolicyBuilder;

/**
 * This is the main interface for checking permissions against User instances.
 *
 * <p>This interface is backed by an {@code AuthorizationPolicy} instance which
 * holds all the permissions declarations.</p>

 * <p>Example:
 * <pre>
 *     &#064;Inject
 *     PermissionManager permissionManager;
 *
 *     &#064;Inject
 *     User user;
 *
 *     public boolean checkPermission() {
 *         Permission permission = permissionManager.createPermission("perspective.view.Home", true);
 *         return permissionManager.checkPermission(permission, user);
 *     }
 * </pre>
 * </p>
 */
public interface PermissionManager {

    /**
     * Gets a builder reference in order to initialize a brand new AuthorizationPolicy instance.
     */
    AuthorizationPolicyBuilder newAuthorizationPolicy();

    /**
     * Gets the current authorization policy instance set.
     */
    AuthorizationPolicy getAuthorizationPolicy();

    /**
     * Changes the current authorization policy instance.
     */
    void setAuthorizationPolicy(AuthorizationPolicy authorizationPolicy);

    /**
     * Creates a permission instance.
     *
     * @param name The name of the permission to create
     * @param granted true=granted, false=denied, null=abstain
     *
     * @return A brnad new permission instance
     */
    Permission createPermission(String name, Boolean granted);

    /**
     * Check if the given permission is granted to the specified user.
     *
     * @param permission The permission to check
     * @param user The user instance
     * @return The authorization result: GRANTED / DENIED / ABSTAIN
     *
     * @see AuthorizationResult
     */
    AuthorizationResult checkPermission(Permission permission, User user);
}
