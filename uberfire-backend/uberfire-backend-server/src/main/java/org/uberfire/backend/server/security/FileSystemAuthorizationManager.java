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

package org.uberfire.backend.server.security;

import javax.enterprise.inject.Alternative;

import org.jboss.errai.security.shared.api.identity.User;
import org.jboss.errai.security.shared.exception.UnauthorizedException;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.security.Resource;
import org.uberfire.security.authz.AuthorizationManager;
import org.uberfire.security.authz.AuthorizationResult;
import org.uberfire.security.authz.ProfileDecisionManager;
import org.uberfire.security.impl.authz.DefaultProfileDecisionManager;
import org.uberfire.security.impl.authz.RuntimeResourceDecisionManager;
import org.uberfire.security.impl.authz.RuntimeResourceManager;

import static org.uberfire.commons.validation.PortablePreconditions.checkNotNull;
import static org.uberfire.security.authz.AuthorizationResult.ACCESS_ABSTAIN;
import static org.uberfire.security.authz.AuthorizationResult.ACCESS_GRANTED;

@Alternative
public class FileSystemAuthorizationManager implements AuthorizationManager {

    private final RuntimeResourceDecisionManager decisionManager;
    private final ProfileDecisionManager profileDecisionManager;

    public FileSystemAuthorizationManager() {
        this.decisionManager = new RuntimeResourceDecisionManager(new RuntimeResourceManager());
        this.profileDecisionManager = new DefaultProfileDecisionManager();
    }

    public FileSystemAuthorizationManager(final RuntimeResourceDecisionManager decisionManager,
                                          final ProfileDecisionManager profileDecisionManager) {
        this.decisionManager = decisionManager;
        this.profileDecisionManager = profileDecisionManager;
    }

    @Override
    public boolean supports(final Resource resource) {
        return resource != null && (resource instanceof FileSystem || resource instanceof FileSystemResourceAdaptor);
    }

    @Override
    public boolean authorize(final Resource resource,
                             final User subject) throws UnauthorizedException {
        checkNotNull("subject", subject);

        final FileSystemResourceAdaptor fileSystemResource;
        if (resource instanceof FileSystem) {
            fileSystemResource = new FileSystemResourceAdaptor((FileSystem) resource);
        } else {
            fileSystemResource = (FileSystemResourceAdaptor) resource;
        }

        final AuthorizationResult finalResult = decisionManager.decide(fileSystemResource, subject, profileDecisionManager);

        return finalResult.equals(ACCESS_ABSTAIN) || finalResult.equals(ACCESS_GRANTED);
    }

    @Override
    public void invalidate(final User user) {
        decisionManager.invalidate(user);
    }
}
