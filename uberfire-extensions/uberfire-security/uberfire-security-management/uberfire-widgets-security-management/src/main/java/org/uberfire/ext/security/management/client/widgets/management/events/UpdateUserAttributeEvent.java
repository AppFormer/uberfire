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

package org.uberfire.ext.security.management.client.widgets.management.events;

import java.util.Map;

import org.uberfire.workbench.events.UberFireEvent;

/**
 * <p>Event for a user attribute updated.</p>
 */
public class UpdateUserAttributeEvent extends ContextualEvent implements UberFireEvent {

    private final Map.Entry<String, String> attribute;

    public UpdateUserAttributeEvent(Object context,
                                    Map.Entry<String, String> attribute) {
        super(context);
        this.attribute = attribute;
    }

    public Map.Entry<String, String> getAttribute() {
        return attribute;
    }
}
