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

import java.util.HashMap;
import java.util.Map;

import org.jboss.errai.security.shared.api.identity.User;
import org.uberfire.security.authz.AuthorizationResult;
import org.uberfire.security.authz.RuntimeResource;

import static org.uberfire.security.authz.AuthorizationResult.ACCESS_DENIED;

public class RuntimeAuthzCache {

    private final Map<String, Map<String, AuthorizationResult>> internal = new HashMap<String, Map<String, AuthorizationResult>>();

    public boolean notContains(final User user,
                               final RuntimeResource resource) {

        final Map<String, AuthorizationResult> result = internal.get(resource.getSignatureId());
        if (result == null) {
            return true;
        }

        return !result.containsKey(user.getIdentifier());
    }

    public void put(final User user,
                    final RuntimeResource resource,
                    final AuthorizationResult authzResult) {
        if (!internal.containsKey(resource.getSignatureId())) {
            internal.put(resource.getSignatureId(), new HashMap<String, AuthorizationResult>());
        }
        final Map<String, AuthorizationResult> result = internal.get(resource.getSignatureId());
        AuthorizationResult knowValue = result.get(user.getIdentifier());
        if (result.containsKey(user.getIdentifier()) && knowValue.equals(authzResult)) {
            return;
        }
        result.put(user.getIdentifier(), authzResult);
    }

    public AuthorizationResult get(final User user,
                                   final RuntimeResource resource) {
        final Map<String, AuthorizationResult> result = internal.get(resource.getSignatureId());
        if (result == null) {
            return ACCESS_DENIED;
        }

        final AuthorizationResult decision = result.get(user.getIdentifier());
        if (decision == null) {
            return ACCESS_DENIED;
        }

        return decision;
    }

    public void invalidate(final User user) {
        if (user == null || user.getIdentifier() == null || user.getIdentifier().isEmpty()) {
            return;
        }
        for (Map<String, AuthorizationResult> entry : internal.values()) {
            entry.remove(user.getIdentifier());
        }
    }
}
