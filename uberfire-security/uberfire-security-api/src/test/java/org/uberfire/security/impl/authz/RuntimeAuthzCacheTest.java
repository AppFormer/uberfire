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
import org.uberfire.security.authz.AuthorizationResult;
import org.uberfire.security.authz.RuntimeResource;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class RuntimeAuthzCacheTest {

    @Test
    public void testInvalidate() {
        final RuntimeAuthzCache cache = new RuntimeAuthzCache();

        final RuntimeResource resource = new TestRuntimeResource("test1234", "author");
        final RuntimeResource resource2 = new TestRuntimeResource("newteskj", "author2");

        final User john = new UserImpl("john", ImmutableSet.of(new RoleImpl("admin")));
        final User mary = new UserImpl("mary", ImmutableSet.of(new RoleImpl("author")));

        assertTrue(cache.notContains(john, resource));

        cache.put(john, resource, AuthorizationResult.ACCESS_GRANTED);
        cache.put(john, resource2, AuthorizationResult.ACCESS_GRANTED);

        assertFalse(cache.notContains(john, resource));

        cache.invalidate(john);

        assertTrue(cache.notContains(john, resource));
        assertTrue(cache.notContains(john, resource2));

        assertTrue(cache.notContains(mary, resource));
        assertTrue(cache.notContains(mary, resource2));

        cache.put(mary, resource, AuthorizationResult.ACCESS_DENIED);
        cache.put(mary, resource2, AuthorizationResult.ACCESS_GRANTED);

        assertFalse(cache.notContains(mary, resource));
        assertFalse(cache.notContains(mary, resource2));

        cache.invalidate(mary);

        assertTrue(cache.notContains(mary, resource));
        assertTrue(cache.notContains(mary, resource2));
    }
}
