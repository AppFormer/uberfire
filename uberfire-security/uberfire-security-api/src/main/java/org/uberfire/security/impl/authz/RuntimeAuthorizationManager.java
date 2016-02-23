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

import static org.uberfire.commons.validation.PortablePreconditions.*;
import static org.uberfire.security.authz.AuthorizationResult.*;

import javax.enterprise.context.ApplicationScoped;

import org.jboss.errai.security.shared.api.identity.User;
import org.jboss.errai.security.shared.exception.UnauthorizedException;
import org.uberfire.security.Resource;
import org.uberfire.security.authz.AccessController;
import org.uberfire.security.authz.AuthorizationManager;
import org.uberfire.security.authz.AuthorizationResult;
import org.uberfire.security.authz.ProfileDecisionManager;

@ApplicationScoped
public class RuntimeAuthorizationManager implements AuthorizationManager {

    private final RuntimeResourceManager resourceManager = new RuntimeResourceManager();
    private final RuntimeResourceDecisionManager decisionManager = new RuntimeResourceDecisionManager( resourceManager );
    private final ProfileDecisionManager profileDecisionManager = new DefaultProfileDecisionManager();
    private AccessController accessController;

    public AccessController getAccessController() {
        return accessController;
    }

    public void setAccessController(AccessController accessController) {
        this.accessController = accessController;
    }

    @Override
    public boolean supports( final Resource resource ) {
        return resourceManager.supports( resource );
    }

    @Override
    public boolean authorize( final Resource resource, final User user ) throws UnauthorizedException {
        checkNotNull( "subject", user );

        // Ask first the access controller

        AuthorizationResult finalResult = ACCESS_ABSTAIN;
        if ( accessController != null ) {
            finalResult = accessController.checkAccess( resource, user );
        }

        // If the access controller abstains then ask the decision manager. Reasons to abstain:
        // - no security policy defined
        // - no explicit permissions assigned set over a resource

        if ( finalResult.equals( ACCESS_ABSTAIN ) ) {
            finalResult = decisionManager.decide( resource, user, profileDecisionManager );
        }

        // Access is granted if the result is not denied

        return !finalResult.equals( ACCESS_DENIED );
    }

    @Override
    public String toString() {
      return "RuntimeAuthorizationManager [resourceManager=" + resourceManager + ", decisionManager=" + decisionManager
              + ", profileDecisionManager=" + profileDecisionManager + "]";
    }

}
