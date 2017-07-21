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
package org.uberfire.ext.wires.client.demo.trees.palette;

import javax.enterprise.context.Dependent;

import org.uberfire.ext.wires.client.demo.trees.factory.TreeNodesCategory;
import org.uberfire.ext.wires.core.api.factories.categories.Category;
import org.uberfire.ext.wires.core.client.palette.BaseGroup;

@Dependent
public class TreeNodesGroup extends BaseGroup {

    @Override
    public Category getCategory() {
        return TreeNodesCategory.CATEGORY;
    }
}
