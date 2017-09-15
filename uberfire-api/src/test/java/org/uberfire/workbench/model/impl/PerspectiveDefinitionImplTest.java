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
 */

package org.uberfire.workbench.model.impl;

import org.junit.Before;
import org.junit.Test;
import org.uberfire.workbench.model.PerspectiveDefinitionOption;

import static org.junit.Assert.*;

public class PerspectiveDefinitionImplTest {

    private PerspectiveDefinitionImpl perspectiveDefinition;

    @Before
    public void setup() {
        perspectiveDefinition = new PerspectiveDefinitionImpl("panelType");
    }

    @Test
    public void hasOptionTest() {
        perspectiveDefinition.setOptions(PerspectiveDefinitionOption.MAXIMIZATION_DISABLED);

        final boolean hasOption = perspectiveDefinition.hasOption(PerspectiveDefinitionOption.MAXIMIZATION_DISABLED);

        assertTrue(hasOption);
    }

    @Test
    public void doesNotHaveOptionTest() {
        final boolean hasOption = perspectiveDefinition.hasOption(PerspectiveDefinitionOption.MAXIMIZATION_DISABLED);

        assertFalse(hasOption);
    }
}
