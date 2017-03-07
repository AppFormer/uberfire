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
package org.uberfire.ext.wires.core.scratchpad.client.factories.containers;

import javax.enterprise.context.ApplicationScoped;

import com.ait.lienzo.client.core.shape.Rectangle;
import org.uberfire.ext.wires.core.api.factories.FactoryHelper;
import org.uberfire.ext.wires.core.api.factories.categories.Category;
import org.uberfire.ext.wires.core.api.shapes.WiresBaseShape;
import org.uberfire.ext.wires.core.client.factories.AbstractBaseFactory;
import org.uberfire.ext.wires.core.client.factories.categories.ContainerCategory;
import org.uberfire.ext.wires.core.client.util.ShapesUtils;
import org.uberfire.ext.wires.core.scratchpad.client.shapes.containers.WiresRectangularContainer;

@ApplicationScoped
public class RectangularContainerFactory extends AbstractBaseFactory<Rectangle> {

    private static final String DESCRIPTION = "Container";

    private static final int SHAPE_SIZE_X = 200;
    private static final int SHAPE_SIZE_Y = 200;

    @Override
    public String getShapeDescription() {
        return DESCRIPTION;
    }

    @Override
    public Category getCategory() {
        return ContainerCategory.CATEGORY;
    }

    @Override
    public WiresBaseShape getShape(final FactoryHelper helper) {
        return new WiresRectangularContainer(makeShape());
    }

    @Override
    public boolean builds(final WiresBaseShape shapeType) {
        return shapeType instanceof WiresRectangularContainer;
    }

    @Override
    protected Rectangle makeShape() {
        final Rectangle rectangle = new Rectangle(SHAPE_SIZE_X,
                                                  SHAPE_SIZE_Y,
                                                  10);
        rectangle.setX(0 - (SHAPE_SIZE_X / 2))
                .setY(0 - (SHAPE_SIZE_Y / 2))
                .setStrokeColor(ShapesUtils.RGB_STROKE_CONTAINER)
                .setStrokeWidth(ShapesUtils.RGB_STROKE_WIDTH_CONTAINER)
                .setFillColor(ShapesUtils.RGB_FILL_CONTAINER)
                .setAlpha(ShapesUtils.RGB_ALPHA_CONTAINER)
                .setDraggable(false);
        return rectangle;
    }

    @Override
    protected double getWidth() {
        return SHAPE_SIZE_X + (ShapesUtils.RGB_STROKE_WIDTH_SHAPE * 2);
    }

    @Override
    protected double getHeight() {
        return SHAPE_SIZE_Y + (ShapesUtils.RGB_STROKE_WIDTH_SHAPE * 2);
    }
}