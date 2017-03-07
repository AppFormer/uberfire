/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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
package org.uberfire.ext.wires.bpmn.api.model.impl.properties;

import org.junit.Test;
import org.uberfire.ext.wires.bpmn.api.model.Property;
import org.uberfire.ext.wires.bpmn.api.model.impl.types.StringType;

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.*;

public class CopyPropertiesTest {

    @Test
    public void testCopyProperties() {
        final Property property = new DefaultPropertyImpl("id",
                                                          new StringType(),
                                                          "caption",
                                                          "description",
                                                          true,
                                                          true);
        final Property copy = property.copy();

        assertNotNull(copy);
        assertFalse(property == copy);
        assertEquals(property.getId(),
                     copy.getId());
        assertEquals(property.getType(),
                     copy.getType());
        assertEquals(property.getCaption(),
                     copy.getCaption());
        assertEquals(property.getDescription(),
                     copy.getDescription());
        assertEquals(property.isReadOnly(),
                     copy.isReadOnly());
        assertEquals(property.isOptional(),
                     copy.isOptional());
    }
}
