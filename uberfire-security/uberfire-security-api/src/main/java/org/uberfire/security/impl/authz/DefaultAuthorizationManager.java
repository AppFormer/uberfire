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

import java.util.Collection;
import java.util.Iterator;

import org.jboss.errai.security.shared.api.identity.User;
import org.jboss.errai.security.shared.exception.UnauthorizedException;
import org.uberfire.security.Resource;
import org.uberfire.security.ResourceManager;
import org.uberfire.security.authz.AuthorizationManager;
import org.uberfire.security.authz.AuthorizationResult;
import org.uberfire.security.authz.ProfileDecisionManager;
import org.uberfire.security.authz.ResourceDecisionManager;
import org.uberfire.security.authz.VotingStrategy;

import static org.uberfire.commons.validation.PortablePreconditions.checkNotNull;
import static org.uberfire.security.authz.AuthorizationResult.ACCESS_ABSTAIN;
import static org.uberfire.security.authz.AuthorizationResult.ACCESS_GRANTED;

public class DefaultAuthorizationManager implements AuthorizationManager {

    private final Collection<ResourceDecisionManager> decisionManagers;
    private final ResourceManager resourceManager;
    private final VotingStrategy votingStrategy;
    private final ProfileDecisionManager profileDecisionManager;

    public DefaultAuthorizationManager(final Collection<ResourceDecisionManager> decisionManagers,
                                       final ResourceManager resourceManager,
                                       final VotingStrategy votingStrategy,
                                       final ProfileDecisionManager profileDecisionManager) {
        this.votingStrategy = checkNotNull("votingStrategy", votingStrategy);
        this.profileDecisionManager = profileDecisionManager;
        this.decisionManagers = checkNotNull("decisionManagers", decisionManagers);
        this.resourceManager = checkNotNull("resourceManagers", resourceManager);
    }

    @Override
    public boolean supports(final Resource resource) {
        return resourceManager.supports(resource);
    }

    @Override
    public boolean authorize(final Resource resource, final User user) throws UnauthorizedException {
        if (decisionManagers.isEmpty()) {
            return true;
        }

        if (!resourceManager.requiresAuthentication(resource)) {
            return true;
        }

        checkNotNull("subject", user);

        final Iterable<AuthorizationResult> results = new Iterable<AuthorizationResult>() {
            @Override
            public Iterator<AuthorizationResult> iterator() {
                final Iterator<ResourceDecisionManager> decisionManagerIterator = decisionManagers.iterator();
                return new Iterator<AuthorizationResult>() {
                    @Override
                    public boolean hasNext() {
                        return decisionManagerIterator.hasNext();
                    }

                    @Override
                    public AuthorizationResult next() {
                        ResourceDecisionManager resourceDecisionManager = decisionManagerIterator.next();
                        return resourceDecisionManager.decide(resource, user, profileDecisionManager);
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException("Remove not supported.");
                    }
                };
            }
        };

        final AuthorizationResult finalResult = votingStrategy.vote(results);

        if (finalResult.equals(ACCESS_ABSTAIN) || finalResult.equals(ACCESS_GRANTED)) {
            return true;
        }

        return false;
    }

    @Override
    public void invalidate(User user) {
        for (ResourceDecisionManager decisionManager : decisionManagers) {
            decisionManager.invalidate(user);
        }
    }

    @Override
    public String toString() {
        return "DefaultAuthorizationManager [decisionManagers=" + decisionManagers + ", resourceManager="
                + resourceManager + ", votingStrategy=" + votingStrategy + ", profileDecisionManager=" + profileDecisionManager
                + "]";
    }
}
