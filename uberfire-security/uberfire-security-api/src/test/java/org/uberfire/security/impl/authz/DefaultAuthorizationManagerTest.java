/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
 *
 */

package org.uberfire.security.impl.authz;

import com.google.common.collect.ImmutableSet;
import org.jboss.errai.security.shared.api.RoleImpl;
import org.jboss.errai.security.shared.api.identity.User;
import org.jboss.errai.security.shared.api.identity.UserImpl;
import org.junit.Test;
import org.uberfire.commons.data.Cacheable;
import org.uberfire.security.ResourceManager;
import org.uberfire.security.authz.AuthorizationResult;
import org.uberfire.security.authz.ProfileDecisionManager;
import org.uberfire.security.authz.ResourceDecisionManager;
import org.uberfire.security.authz.RuntimeResource;
import org.uberfire.security.authz.VotingStrategy;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.uberfire.security.authz.AuthorizationResult.ACCESS_DENIED;
import static org.uberfire.security.authz.AuthorizationResult.ACCESS_GRANTED;

public class DefaultAuthorizationManagerTest {

    @Test
    public void testInvalidateCache() {
        final ResourceDecisionManager resourceManager1 = mock(ResourceDecisionManager.class);
        final ResourceDecisionManager resourceManager2 = mock(ResourceDecisionManager.class);
        final ResourceManager resourceManager = mock(ResourceManager.class);
        final VotingStrategy votingStrategy = mock(VotingStrategy.class);
        final ProfileDecisionManager profileDecisionManager = mock(ProfileDecisionManager.class);

        final DefaultAuthorizationManager authorizationManager = new DefaultAuthorizationManager(asList(resourceManager1, resourceManager2),
                                                                                                 resourceManager,
                                                                                                 votingStrategy,
                                                                                                 profileDecisionManager);

        RuntimeResource resource = new TestRuntimeResource("test1234", "author");
        User john = new UserImpl("john", ImmutableSet.of(new RoleImpl("admin")));
        User mary = new UserImpl("mary", ImmutableSet.of(new RoleImpl("author")));

        when(resourceManager.requiresAuthentication(resource)).thenReturn(true);
        when(resourceManager1.decide(resource, john, profileDecisionManager)).thenReturn(AuthorizationResult.ACCESS_DENIED);
        when(votingStrategy.vote(any(Iterable.class))).thenReturn(ACCESS_DENIED);

        assertTrue(resource instanceof Cacheable);
        assertTrue(((Cacheable) resource).requiresRefresh());

        boolean authorized = authorizationManager.authorize(resource, john);
        assertFalse(authorized);

        authorizationManager.invalidate(john);

        verify(resourceManager1, times(1)).invalidate(john);
        verify(resourceManager2, times(1)).invalidate(john);

        when(resourceManager.requiresAuthentication(resource)).thenReturn(true);
        when(resourceManager1.decide(resource, mary, profileDecisionManager)).thenReturn(AuthorizationResult.ACCESS_GRANTED);
        when(votingStrategy.vote(any(Iterable.class))).thenReturn(ACCESS_GRANTED);

        assertTrue(authorizationManager.authorize(resource, mary));

        authorizationManager.invalidate(mary);

        verify(resourceManager1, times(1)).invalidate(mary);
        verify(resourceManager2, times(1)).invalidate(mary);
    }
}
