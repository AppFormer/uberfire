/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.security;

import java.security.AccessController;
import java.util.List;

/**
 * A generic interface for modelling resources, like UI assets: perspectives, screens or
 * editors or even backend resources like repositories, projects or data objects.
 */
public interface Resource {

    /**
     * An identifier that is unique among all the resources of the same type (see {@link Resource#getType()}).
     */
    default String getIdentifier() {
        return null;
    }

    /**
     * Get the resource type classifier
     */
    default ResourceType getType() {
        return null;
    }

    /**
     * A list of dependent resources.
     *
     * <p>The dependency list is used for instance to determine if a user can access a given resource. Should
     * the access to all its dependencies is denied, it is denied for this instance as well.</p>
     *
     * @return A list of resources or null if this resource has no dependencies.
     *
     * @see AccessController
     */
    default List<Resource> getDependencies() {
        return null;
    }
}
