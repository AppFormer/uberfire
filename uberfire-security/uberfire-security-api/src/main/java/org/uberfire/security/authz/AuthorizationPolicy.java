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

import java.util.Set;

import org.jboss.errai.security.shared.api.Group;
import org.jboss.errai.security.shared.api.Role;
import org.jboss.errai.security.shared.api.identity.User;

/**
 * It holds a registry of permission collections assigned to individual Role and Group instances.
 */
public interface AuthorizationPolicy {

    /**
     * Get the collection of roles this policy is related to.
     */
    Set<Role> getRoles();

    /**
     * Get the collection of roles this policy is related to.
     */
    Set<Group> getGroups();

    /**
     * Get the description of an existing role.
     *
     * @param role The role
     * @return The role description
     */
    String getRoleDescription(Role role);

    /**
     * Sets a description for an existing role.
     *
     * @param role The role
     * @param description A non empty description
     */
    void setRoleDescription(Role role, String description);

    /**
     * Get the description of an existing group.
     *
     * @param group The group
     * @return The group description
     */
    String getGroupDescription(Group group);

    /**
     * Sets a description for an existing group.
     *
     * @param group The group identifier
     * @param description A non empty description
     */
    void setGroupDescription(Group group, String description);

    /**
     * Get a role's priority. The priority is important during permission resolution and when a user
     * is assigned to more than one role/group it is used to determine what is the most priority.
     *
     * @param role The role instance
     * @return An integer. The highest, the more priority the collection is. Default priority value is 0.
     */
    int getPriority(Role role);

    /**
     * Get a group's priority. The priority is important during permission resolution and when a user
     * is assigned to more than one role/group it is used to determine what is the most priority.
     *
     * @param group The group instance
     * @return An integer. The highest, the more priority the collection is. Default priority value is 0.
     */
    int getPriority(Group group);

    /**
     * Set the role's priority
     *
     * @param role The role instance
     * @param priority Any valid integer. The highest, the more priority the role is.
     */
    void setPriority(Role role, int priority);

    /**
     * Set the group's priority
     *
     * @param group The group instance
     * @param priority Any valid integer. The highest, the more priority the role is.
     */
    void setPriority(Group group, int priority);

    /**
     * Get the permissions assigned to a given role.
     *
     * @param role The role instance
     * @return The permission collection
     */
    PermissionCollection getPermissions(Role role);

    /**
     * Get the permissions assigned to a given group.
     *
     * @param group The group instance
     * @return The permission collection
     */
    PermissionCollection getPermissions(Group group);

    /**
     * Get the permissions assigned to a given user.
     *
     * <p>Usually, the user's permissions is obtained by mixing all the permissions assigned
     * to each role and group instance the user belongs to.</p>
     *
     * <p>Every interface implementation must take into account the priority of every individual
     * role/group, which means that the most priority role/group have precedence in the permission
     * resolution algorithm.</p>
     *
     * @param user The user instance
     * @return The permission collection
     *
     * @see AuthorizationPolicy#getPriority(Role)
     * @see AuthorizationPolicy#getPriority(Group)
     */
    PermissionCollection getPermissions(User user);

    /**
     * Get the identifier of the home perspective assigned to the given group.
     *
     * @return An existing perspective identifier
     */
    String getHomePerspective(Role role);

    /**
     * Get the identifier of the home perspective assigned to the given group.
     *
     * @return An existing perspective identifier
     */
    String getHomePerspective(Group group);

    /**
     * Get the identifier of the perspective this user is redirected by default.
     *
     * <p>If the user is assigned with more than one role or group then the most priority one
     * is taken.</p>
     *
     * @return An existing perspective identifier
     *
     * @see AuthorizationPolicy#getPriority(Role)
     * @see AuthorizationPolicy#getPriority(Group)
     */
    String getHomePerspective(User user);
}
