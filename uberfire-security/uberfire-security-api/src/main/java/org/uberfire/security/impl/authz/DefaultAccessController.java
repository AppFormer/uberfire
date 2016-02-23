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

import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.security.shared.api.identity.User;
import org.uberfire.security.Resource;
import org.uberfire.security.authz.AccessController;
import org.uberfire.security.authz.AuthorizationResult;
import org.uberfire.security.authz.Permission;
import org.uberfire.security.authz.PermissionManager;
import org.uberfire.security.ResourceType;

import static org.uberfire.security.authz.AuthorizationResult.*;

/**
 * Default implementation for controlling access to {@code Resource} instances.
 */
@ApplicationScoped
public class DefaultAccessController implements AccessController {

    private PermissionManager permissionManager;

    public DefaultAccessController() {
    }

    @Inject
    public DefaultAccessController(PermissionManager permissionManager) {
        this.permissionManager = permissionManager;
    }

    @Override
    public AuthorizationResult checkAccess(Resource resource, User user) {

        // A resource may depend on others
        List<Resource> deps = resource.getDependencies();
        if (deps != null) {

            // One dep is accessible
            for (Resource dep : deps) {
                AuthorizationResult itemAccess = checkAccess(dep, user);
                if (!ACCESS_DENIED.equals(itemAccess)) {
                    return itemAccess;
                }
            }
            // No deps found or all deps denied
            return ACCESS_DENIED;
        }
        // No dependent resource
        String id = resource.getIdentifier();
        if (id == null || id.length() == 0) {
            return ACCESS_ABSTAIN;
        }
        // Does the resource have a type?
        // yes => check the "view" permission for the given resource identifier, f.i: "project.view.myprojectid"
        // no  => just check the resource identifier against the security policy
        ResourceType type = resource.getType();
        String name = type != null ? buildPermissionName(type, "view", id) : id;
        Permission permission = permissionManager.createPermission(name, true);
        return permissionManager.checkPermission(permission, user);
    }

    protected String buildPermissionName(ResourceType type, String permission, String resourceId) {
        String name = "";
        if (type != null) {
            name += type.toString().toLowerCase();
        }
        if (permission != null && permission.trim().length() > 0) {
            name += (name.length() > 0 ? "." : "") + permission;
        }
        if (resourceId != null && resourceId.trim().length() > 0) {
            name += (name.length() > 0 ? "." : "") + resourceId;
        }
        return name;
    }
}
