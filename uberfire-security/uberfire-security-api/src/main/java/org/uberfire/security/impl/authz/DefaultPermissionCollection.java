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
import java.util.Iterator;
import java.util.TreeSet;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.security.authz.Permission;
import org.uberfire.security.authz.PermissionCollection;

import static org.uberfire.security.authz.AuthorizationResult.*;

/**
 * A collection where the permissions are ordered by name.
 */
@Portable
public class DefaultPermissionCollection implements PermissionCollection {

    private TreeSet<Permission> permissionSet = new TreeSet<>();

    public DefaultPermissionCollection() {
    }

    @Override
    public Collection<Permission> collection() {
        return permissionSet;
    }

    @Override
    public PermissionCollection add(Permission... permissions) {
        for (Permission p : permissions) {

            // Avoid redundancy
            if (!implies(p)) {
                permissionSet.add(p);
            }
        }
        return this;
    }

    @Override
    public Permission get(String name) {
        for (Permission p : permissionSet) {
            if (equalsName(name, p.getName())) {
                return p;
            }
        }
        return null;
    }

    protected boolean equalsName(String s1, String s2) {
        return (s1 == null && s2 == null) || (s1 != null && s1.equals(s2));
    }

    @Override
    public boolean implies(Permission permission) {
        for (Permission p : permissionSet) {
            if (p.implies(permission)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public PermissionCollection merge(PermissionCollection other, int priority) {
        if (other == null || other.collection().isEmpty()) {
            return this;
        }
        DefaultPermissionCollection result = this.clone();

        for (Permission p : other.collection()) {
            Permission existing = result.get(p.getName());

            // New permission => Add (avoid redundancy)
            if (existing == null) {
                if (!result.implies(p)) {
                    result.add(p);
                }
                continue;
            }

            // Collision detected. Same permission, different results => Check the priority
            boolean differentResult = !existing.getResult().equals(p.getResult());
            boolean otherWins = (priority == 0 && ACCESS_GRANTED.equals(p.getResult())) || priority > 0;
            if (differentResult && otherWins) {
                result.invert(existing);
            }
        }
        return result;
    }

    @Override
    public DefaultPermissionCollection clone() {
        DefaultPermissionCollection clone = new DefaultPermissionCollection();
        for (Permission p : permissionSet) {
            clone.add(p.clone());
        }
        return clone;
    }

    public PermissionCollection invert(Permission target) {
        target.setResult(target.getResult().invert());

        // After inverting the permission ensure no implied permissions are left
        Iterator<Permission> it = permissionSet.iterator();
        while (it.hasNext()) {
            Permission p = it.next();
            if (!target.equals(p) && target.implies(p)) {
                it.remove();
            }
        }
        return this;
    }

    @Override
    public String toString() {
        StringBuilder out = new StringBuilder();
        Iterator<Permission> it = permissionSet.iterator();
        while (it.hasNext()) {
            Permission p = it.next();
            out.append(p).append("\n");
        }
        return out.toString();
    }
}
