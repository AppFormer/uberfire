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
package org.uberfire.ext.wires.client.demo.trees.shapes;

import com.ait.lienzo.client.core.shape.Circle;
import org.uberfire.ext.wires.core.trees.client.shapes.WiresBaseTreeNode;

/**
 * Example Node that can only have WiresExampleTreeNode2 types of node added as children
 */
public class WiresExampleTreeNode2 extends WiresExampleTreeNode1 {

    public WiresExampleTreeNode2(final Circle shape) {
        super(shape);
    }

    @Override
    public boolean acceptChildNode(final WiresBaseTreeNode child) {
        return child instanceof WiresExampleTreeNode2;
    }
}
