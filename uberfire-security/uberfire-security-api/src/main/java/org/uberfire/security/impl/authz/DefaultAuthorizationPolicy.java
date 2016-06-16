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

import java.util.HashSet;
import java.util.Set;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.security.shared.api.Group;
import org.jboss.errai.security.shared.api.Role;
import org.jboss.errai.security.shared.api.identity.User;
import org.uberfire.security.authz.AuthorizationPolicy;
import org.uberfire.security.authz.Permission;
import org.uberfire.security.authz.PermissionCollection;

@Portable
public class DefaultAuthorizationPolicy implements AuthorizationPolicy {

    private Set<DefaultAuthorizationEntry> entrySet = new HashSet<>();

    public DefaultAuthorizationPolicy() {
    }

    protected DefaultAuthorizationEntry registerAuthzEntry(DefaultAuthorizationEntry entry) {
        entrySet.add(entry);
        return entry;
    }

    protected DefaultAuthorizationEntry getAuthzEntry(Role role) {
        for (DefaultAuthorizationEntry entry : entrySet) {
            if (entry.getRole() != null && entry.getRole().equals(role)) {
                return entry;
            }
        }
        return registerAuthzEntry(new DefaultAuthorizationEntry(role));
    }

    protected DefaultAuthorizationEntry getAuthzEntry(Group group) {
        for (DefaultAuthorizationEntry entry : entrySet) {
            if (entry.getGroup() != null && entry.getGroup().equals(group)) {
                return entry;
            }
        }
        return registerAuthzEntry(new DefaultAuthorizationEntry(group));
    }

    @Override
    public Set<Role> getRoles() {
        Set<Role> result = new HashSet<>();
        for (DefaultAuthorizationEntry entry : entrySet) {
            if (entry.getRole() != null) {
                result.add(entry.getRole());
            }
        }
        return result;
    }

    @Override
    public Set<Group> getGroups() {
        Set<Group> result = new HashSet<>();
        for (DefaultAuthorizationEntry entry : entrySet) {
            if (entry.getGroup() != null) {
                result.add(entry.getGroup());
            }
        }
        return result;
    }

    @Override
    public String getRoleDescription(Role role) {
        DefaultAuthorizationEntry entry = getAuthzEntry(role);
        return entry.getDescription();
    }

    @Override
    public void setRoleDescription(Role role, String description) {
        DefaultAuthorizationEntry entry = getAuthzEntry(role);
        entry.setDescription(description);
    }

    @Override
    public String getGroupDescription(Group group) {
        DefaultAuthorizationEntry entry = getAuthzEntry(group);
        return entry.getDescription();
    }

    @Override
    public void setGroupDescription(Group group, String description) {
        DefaultAuthorizationEntry entry = getAuthzEntry(group);
        entry.setDescription(description);
    }

    @Override
    public int getPriority(Role role) {
        DefaultAuthorizationEntry entry = getAuthzEntry(role);
        return entry != null ? entry.getPriority() : 0;
    }

    @Override
    public int getPriority(Group group) {
        DefaultAuthorizationEntry entry = getAuthzEntry(group);
        return entry != null ? entry.getPriority() : 0;
    }

    @Override
    public void setPriority(Role role, int priority) {
        DefaultAuthorizationEntry entry = getAuthzEntry(role);
        entry.setPriority(priority);
    }

    @Override
    public void setPriority(Group group, int priority) {
        DefaultAuthorizationEntry entry = getAuthzEntry(group);
        entry.setPriority(priority);
    }

    @Override
    public PermissionCollection getPermissions(Role role) {
        DefaultAuthorizationEntry entry = getAuthzEntry(role);
        return entry.getPermissions();
    }

    @Override
    public PermissionCollection getPermissions(Group group) {
        DefaultAuthorizationEntry entry = getAuthzEntry(group);
        return entry.getPermissions();
    }

    public void addPermission(Role role, Permission permission) {
        DefaultAuthorizationEntry entry = getAuthzEntry(role);
        entry.getPermissions().add(permission);
    }

    public void addPermission(Group group, Permission permission) {
        DefaultAuthorizationEntry entry = getAuthzEntry(group);
        entry.getPermissions().add(permission);
    }

    public void setPermissions(Role role, PermissionCollection collection) {
        DefaultAuthorizationEntry entry = getAuthzEntry(role);
        entry.setPermissions(collection);
    }

    public void setPermissions(Group group, PermissionCollection collection) {
        DefaultAuthorizationEntry entry = getAuthzEntry(group);
        entry.setPermissions(collection);
    }

    @Override
    public void setHomePerspective(Role role, String perspective) {
        DefaultAuthorizationEntry entry = getAuthzEntry(role);
        entry.setHomePerspective(perspective);
    }

    @Override
    public void setHomePerspective(Group group, String perspective) {
        DefaultAuthorizationEntry entry = getAuthzEntry(group);
        entry.setHomePerspective(perspective);
    }

    @Override
    public String getHomePerspective(Role role) {
        DefaultAuthorizationEntry entry = getAuthzEntry(role);
        return entry.getHomePerspective();
    }

    @Override
    public String getHomePerspective(Group group) {
        DefaultAuthorizationEntry entry = getAuthzEntry(group);
        return entry.getHomePerspective();
    }

    @Override
    public String getHomePerspective(User user) {
        String lastHome = null;
        int lastPriority = 0;

        if (user.getRoles() != null) {
            for (Role role : user.getRoles()) {
                String home = getHomePerspective(role);
                if (home != null) {
                    int priority = getPriority(role);
                    if (lastHome == null || priority > lastPriority) {
                        lastHome = home;
                        lastPriority = priority;
                    }
                }
            }
        }
        if (user.getGroups() != null) {
            for (Group group : user.getGroups()) {
                String home = getHomePerspective(group);
                if (home != null) {
                    int priority = getPriority(group);
                    if (lastHome == null || priority > lastPriority) {
                        lastHome = home;
                        lastPriority = priority;
                    }
                }
            }
        }
        return lastHome;
    }
}
