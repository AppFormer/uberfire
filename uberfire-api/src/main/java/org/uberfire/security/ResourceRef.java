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

package org.uberfire.security;

import java.util.List;

/**
 * A resource reference. Useful when a link to the real Resource object is not available.
 */
public class ResourceRef implements Resource {

    private String identifier = null;
    private ResourceType type = null;
    private List<Resource> dependencies = null;

    public ResourceRef(String identifier, ResourceType type, List<Resource> dependencies) {
        this.identifier = identifier;
        this.type = type;
        this.dependencies = dependencies;
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    @Override
    public ResourceType getType() {
        return type;
    }

    public void setType(ResourceType type) {
        this.type = type;
    }

    @Override
    public List<Resource> getDependencies() {
        return dependencies;
    }

    public void setDependencies(List<Resource> dependencies) {
        this.dependencies = dependencies;
    }
}
