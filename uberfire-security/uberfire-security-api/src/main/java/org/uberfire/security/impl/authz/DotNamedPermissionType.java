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

import org.uberfire.security.authz.Permission;
import org.uberfire.security.authz.PermissionType;

public class DotNamedPermissionType implements PermissionType {

    private String type = null;

    public DotNamedPermissionType(String type) {
        this.type = type;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public boolean supportsPermission(String name) {
        return name == null || name.startsWith(type);
    }

    @Override
    public Permission createPermission(String name, Boolean granted) {
        if (!supportsPermission(name)) {
            throw new IllegalArgumentException("The permission is not supported: " + name);
        }
        return new DotNamedPermission(name, granted);
    }
}
