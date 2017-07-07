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
package org.uberfire.ext.plugin.client.perspective.editor.api;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jboss.errai.ioc.client.container.IOC;
import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.uberfire.ext.layout.editor.client.api.LayoutDragComponent;
import org.uberfire.ext.layout.editor.client.api.LayoutDragComponentGroup;

public class PerspectiveDragComponentUtils {

    public static LayoutDragComponentGroup lookupPerspectiveDragComponents() {
        List<LayoutDragComponent> perspectiveDragComponents = scanPerspectiveDragComponents();

        LayoutDragComponentGroup group = convertToDragComponentGroup(perspectiveDragComponents);

        return group;
    }

    private static LayoutDragComponentGroup convertToDragComponentGroup(
            List<LayoutDragComponent> perspectiveDragComponents) {
        LayoutDragComponentGroup group = new LayoutDragComponentGroup("Uberfire Widgets");
        int id = 0;
        for (LayoutDragComponent layoutDragComponent : perspectiveDragComponents) {
            group.addLayoutDragComponent(String.valueOf(id),
                                         layoutDragComponent);
            id++;
        }
        return group;
    }

    private static List<LayoutDragComponent> scanPerspectiveDragComponents() {
        List<LayoutDragComponent> result = new ArrayList<>();
        Collection<SyncBeanDef<PerspectiveEditorDragComponent>> beanDefs = IOC
                .getBeanManager().lookupBeans(PerspectiveEditorDragComponent.class);
        for (SyncBeanDef<PerspectiveEditorDragComponent> beanDef : beanDefs) {
            PerspectiveEditorDragComponent dragComponent = beanDef.getInstance();
            result.add(dragComponent);
        }
        return result;
    }

}
