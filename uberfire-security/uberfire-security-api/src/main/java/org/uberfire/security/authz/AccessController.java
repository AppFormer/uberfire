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
import org.uberfire.security.Resource;

/**
 * Interface for checking user access to workbench resources.
 *
 * <p>The "access" term here refers to the ability to be able to reach, read or view
 * a resource. For instance, read a file, view an item in the UI, etc.</p>
 */
public interface AccessController {

    /**
     * Check if the specified user can "access" a given resource.
     *
     * <p>Notice the resource may have dependencies ({@link Resource#getDependencies()}) to
     * other resources, in such case the resource is only accessible if and only if one of
     * its dependent references is accessible too.</p>
     *
     * @param resource The resource
     * @param user The user instance
     * @return The authorization result: GRANTED / DENIED / ABSTAIN
     *
     * @see AuthorizationResult
     */
    AuthorizationResult checkAccess(Resource resource, User user);
}