/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.security.management.api.validation;

import java.util.HashSet;
import java.util.Set;
import javax.validation.ConstraintViolation;

import org.jboss.errai.security.shared.api.Role;

/**
 * <p>The base validator class for a Role entity based on JSR303 Bean Validations.</p>
 * <p>This provides validation logic for both backend and client sides, but you have to provide an instantiable class that provides the error message descriptions for each validation error supported.</p>
 * @since 0.8.0
 */
public abstract class RoleValidator implements EntityValidator<Role> {

    @Override
    public Set<ConstraintViolation<Role>> validate(Role entity) {
        return new HashSet<>();
    }
}
