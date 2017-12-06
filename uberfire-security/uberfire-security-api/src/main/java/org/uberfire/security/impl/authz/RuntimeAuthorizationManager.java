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

package org.uberfire.security.impl.authz;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;

import org.jboss.errai.security.shared.api.identity.User;
import org.jboss.errai.security.shared.exception.UnauthorizedException;
import org.uberfire.security.Resource;
import org.uberfire.security.authz.AuthorizationManager;
import org.uberfire.security.authz.AuthorizationResult;
import org.uberfire.security.authz.ProfileDecisionManager;

import static org.uberfire.commons.validation.PortablePreconditions.checkNotNull;
import static org.uberfire.security.authz.AuthorizationResult.ACCESS_ABSTAIN;
import static org.uberfire.security.authz.AuthorizationResult.ACCESS_GRANTED;

@ApplicationScoped
@Alternative
public class RuntimeAuthorizationManager implements AuthorizationManager {

    private final RuntimeResourceManager resourceManager;
    private final RuntimeResourceDecisionManager decisionManager;
    private final ProfileDecisionManager profileDecisionManager;

    public RuntimeAuthorizationManager() {
        this.resourceManager = new RuntimeResourceManager();
        this.decisionManager = new RuntimeResourceDecisionManager(resourceManager);
        this.profileDecisionManager = new DefaultProfileDecisionManager();
    }

    public RuntimeAuthorizationManager(final RuntimeResourceManager resourceManager,
                                       final RuntimeResourceDecisionManager decisionManager,
                                       final ProfileDecisionManager profileDecisionManager) {
        this.resourceManager = resourceManager;
        this.decisionManager = decisionManager;
        this.profileDecisionManager = profileDecisionManager;
    }

    @Override
    public boolean supports(final Resource resource) {
        return resourceManager.supports(resource);
    }

    @Override
    public boolean authorize(final Resource resource,
                             final User user)
            throws UnauthorizedException {
        if (!resourceManager.requiresAuthentication(resource)) {
            return true;
        }

        checkNotNull("subject", user);

        final AuthorizationResult finalResult = decisionManager.decide(resource, user, profileDecisionManager);

        if (finalResult.equals(ACCESS_ABSTAIN) || finalResult.equals(ACCESS_GRANTED)) {
            return true;
        }

        return false;
    }

    @Override
    public void invalidate(final User user) {
        decisionManager.invalidate(user);
    }

    @Override
    public String toString() {
        return "RuntimeAuthorizationManager [resourceManager=" + resourceManager + ", decisionManager=" + decisionManager
                + ", profileDecisionManager=" + profileDecisionManager + "]";
    }
}
