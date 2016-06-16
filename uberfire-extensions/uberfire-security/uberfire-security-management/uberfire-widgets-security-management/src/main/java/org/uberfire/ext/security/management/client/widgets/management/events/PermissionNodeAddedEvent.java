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

import org.uberfire.ext.security.management.client.widgets.management.editor.acl.ACLEditor;
import org.uberfire.security.client.authz.tree.PermissionNode;
import org.uberfire.workbench.events.UberFireEvent;

/**
 * <p>Event notifying a new permission node is added.</p>
 */
public class PermissionNodeAddedEvent extends ContextualEvent implements UberFireEvent {

    private PermissionNode childNode;
    private PermissionNode parentNode;

    public PermissionNodeAddedEvent(ACLEditor aclEditor, PermissionNode parentNode, PermissionNode childNode) {
        super(aclEditor);
        this.parentNode = parentNode;
        this.childNode = childNode;
    }

    public PermissionNode getParentNode() {
        return parentNode;
    }

    public PermissionNode getChildNode() {
        return childNode;
    }
}
