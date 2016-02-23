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

/**
 * A PermissionType provides factory services for the creation of permission instances
 * as well as services for controlling the access to Resource instances.
 */
public interface PermissionType {

    /**
     * An string identifier that acts as a unique identifier for the permission type.
     * @return The permission type unique identifier
     */
    String getType();

    /**
     * Checks if the given permission name is supported by this type. That means basically that
     * such name does follow a specific nomenclature for the formatting of its permission names.
     *
     * @param name The permission name to check
     * @return true is such permission is supported or false otherwise.
     */
    boolean supportsPermission(String name);

    /**
     * Creates a permission instance.
     * @param name The name of the permission to create.
     * @param granted true=granted, false=denied, null=abstain
     * @return A permission instance
     */
    Permission createPermission(String name, Boolean granted);
}