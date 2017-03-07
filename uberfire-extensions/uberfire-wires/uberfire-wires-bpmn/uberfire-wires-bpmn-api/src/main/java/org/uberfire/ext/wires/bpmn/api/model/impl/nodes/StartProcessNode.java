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
package org.uberfire.ext.wires.bpmn.api.model.impl.nodes;

import java.util.HashSet;
import java.util.Set;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.ext.wires.bpmn.api.model.BpmnEdge;
import org.uberfire.ext.wires.bpmn.api.model.BpmnGraphNode;
import org.uberfire.ext.wires.bpmn.api.model.Content;
import org.uberfire.ext.wires.bpmn.api.model.Property;
import org.uberfire.ext.wires.bpmn.api.model.Role;
import org.uberfire.ext.wires.bpmn.api.model.impl.content.DefaultContentImpl;
import org.uberfire.ext.wires.bpmn.api.model.impl.roles.DefaultRoleImpl;
import org.uberfire.ext.wires.bpmn.beliefs.graph.impl.GraphNodeImpl;

/**
 * A BPMN "Start Node"
 */
@Portable
public class StartProcessNode extends GraphNodeImpl<Content, BpmnEdge> implements BpmnGraphNode {

    private Set<Role> roles = new HashSet<Role>() {{
        add(new DefaultRoleImpl("all"));
        add(new DefaultRoleImpl("Startevents_all"));
        add(new DefaultRoleImpl("sequence_start"));
        add(new DefaultRoleImpl("to_task_event"));
        add(new DefaultRoleImpl("from_task_event"));
        add(new DefaultRoleImpl("fromtoall"));
        add(new DefaultRoleImpl("StartEventsMorph"));
    }};

    private Set<Property> properties = new HashSet<Property>();

    public StartProcessNode() {
        setContent(new DefaultContentImpl("StartNoneEvent",
                                          "StartNoneEvent",
                                          "Untyped start event.",
                                          roles,
                                          properties));
    }

    @Override
    public StartProcessNode copy() {
        final StartProcessNode copy = new StartProcessNode();
        copy.setContent(this.getContent().copy());
        return copy;
    }

    @Override
    public String toString() {
        return "StartProcessNode{" +
                "roles=" + roles +
                ", properties=" + properties +
                '}';
    }
}
