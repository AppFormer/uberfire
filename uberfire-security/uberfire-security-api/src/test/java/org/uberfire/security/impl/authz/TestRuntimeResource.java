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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.uberfire.commons.data.Cacheable;
import org.uberfire.security.authz.RuntimeFeatureResource;

public class TestRuntimeResource implements RuntimeFeatureResource,
                                            Cacheable {

    private final String signatureId;
    private List<String> roles;
    private boolean requiresRefresh = true;

    protected TestRuntimeResource(String signatureId, String... roles) {
        this.signatureId = signatureId;
        if (roles != null) {
            this.roles = Arrays.asList(roles);
        } else {
            this.roles = Collections.emptyList();
        }
    }

    @Override
    public String getSignatureId() {
        return this.signatureId;
    }

    @Override
    public Collection<String> getRoles() {
        return this.roles;
    }

    @Override
    public Collection<String> getTraits() {
        return Collections.emptySet();
    }

    @Override
    public void markAsCached() {
        this.requiresRefresh = false;
    }

    @Override
    public boolean requiresRefresh() {
        return requiresRefresh;
    }
}
