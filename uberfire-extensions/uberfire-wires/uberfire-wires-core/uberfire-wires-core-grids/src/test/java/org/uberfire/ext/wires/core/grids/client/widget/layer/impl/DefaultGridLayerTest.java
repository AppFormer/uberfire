/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
package org.uberfire.ext.wires.core.grids.client.widget.layer.impl;

import java.util.Set;

import com.ait.lienzo.client.core.mediator.Mediators;
import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.Viewport;
import com.ait.lienzo.client.core.types.Transform;
import com.ait.lienzo.client.widget.LienzoPanel;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.google.gwt.user.client.Command;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridData;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.grid.impl.BaseGridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.GridRenderer;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridLayer;
import org.uberfire.ext.wires.core.grids.client.widget.layer.pinning.impl.DefaultPinnedModeManager;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(LienzoMockitoTestRunner.class)
public class DefaultGridLayerTest {

    @Mock
    private Viewport viewport;

    @Mock
    private GridRenderer renderer;

    @Mock
    private Mediators mediators;

    private GridLayer gridLayer;

    private Transform transform;

    @Before
    public void setup() {
        this.transform = new Transform();

        final LienzoPanel panel = new LienzoPanel(500,
                                                  500);
        final DefaultGridLayer wrapped = new DefaultGridLayer() {

            @Override
            public Layer batch() {
                //Don't render Layer for tests
                return this;
            }

            @Override
            public Layer batch(final GridLayerRedrawManager.PrioritizedCommand command) {
                //Don't render Layer for tests
                return this;
            }
        };
        panel.add(wrapped);
        this.gridLayer = spy(wrapped);

        when(gridLayer.getViewport()).thenReturn(viewport);
        when(viewport.getTransform()).thenReturn(transform);
        when(viewport.getMediators()).thenReturn(mediators);
    }

    private GridWidget makeGridWidget() {
        final GridData uiModel = new BaseGridData();
        return new BaseGridWidget(uiModel,
                                  gridLayer,
                                  gridLayer,
                                  renderer) {
            @Override
            public void select() {
                //Don't render Selector for tests
            }
        };
    }

    @Test
    public void checkFlipToGridWidgetWhenPinned() {
        final GridWidget gridWidget = makeGridWidget();
        this.gridLayer.add(gridWidget);

        gridLayer.enterPinnedMode(gridWidget,
                                  new GridLayerRedrawManager.PrioritizedCommand(0) {
                                      @Override
                                      public void execute() {

                                      }
                                  });

        gridLayer.flipToGridWidget(gridWidget);

        verify(gridLayer,
               times(1)).updatePinnedContext(eq(gridWidget));
        verify(gridLayer,
               times(1)).batch(any(GridLayerRedrawManager.PrioritizedCommand.class));
    }

    @Test
    public void checkFlipToGridWidgetWhenNotPinned() {
        final GridWidget gridWidget = makeGridWidget();
        this.gridLayer.add(gridWidget);

        gridLayer.flipToGridWidget(gridWidget);

        verify(gridLayer,
               never()).updatePinnedContext(eq(gridWidget));
        verify(gridLayer,
               never()).batch(any(GridLayerRedrawManager.PrioritizedCommand.class));
    }

    @Test
    public void checkScrollToGridWidgetWhenPinned() {
        final GridWidget gridWidget = makeGridWidget();
        this.gridLayer.add(gridWidget);

        gridLayer.enterPinnedMode(gridWidget,
                                  new GridLayerRedrawManager.PrioritizedCommand(0) {
                                      @Override
                                      public void execute() {
                                          //Do nothing
                                      }
                                  });

        gridLayer.scrollToGridWidget(gridWidget);

        verify(gridLayer,
               never()).select(eq(gridWidget));
    }

    @Test
    public void checkScrollToGridWidgetWhenNotPinned() {
        final GridWidget gridWidget = makeGridWidget();
        this.gridLayer.add(gridWidget);

        gridLayer.scrollToGridWidget(gridWidget);

        verify(gridLayer,
               times(1)).select(eq(gridWidget));
    }

    @Test
    public void checkRemoveAllClearsCachedReferences() {
        final GridWidget gridWidget1 = makeGridWidget();
        final GridColumn column1 = mock(GridColumn.class);
        when(column1.isVisible()).thenReturn(true);
        gridWidget1.getModel().appendColumn(column1);

        final GridWidget gridWidget2 = makeGridWidget();
        final GridColumn column2 = mock(GridColumn.class);
        when(column2.isVisible()).thenReturn(true);
        when(column2.isLinked()).thenReturn(true);
        when(column2.getLink()).thenReturn(column1);
        gridWidget2.getModel().appendColumn(column2);

        this.gridLayer.add(gridWidget1);
        this.gridLayer.add(gridWidget2);

        assertEquals(2,
                     gridLayer.getGridWidgets().size());
        assertEquals(1,
                     gridLayer.getGridWidgetConnectors().size());

        gridLayer.removeAll();

        assertEquals(0,
                     gridLayer.getGridWidgets().size());
        assertEquals(0,
                     gridLayer.getGridWidgetConnectors().size());
    }

    @Test
    public void checkConnectorsVisibilityFollowPinnedModeStatus() {
        final GridWidget gridWidget1 = makeGridWidget();
        final GridColumn column1 = mock(GridColumn.class);
        when(column1.isVisible()).thenReturn(true);
        gridWidget1.getModel().appendColumn(column1);

        final GridWidget gridWidget2 = makeGridWidget();
        final GridColumn column2 = mock(GridColumn.class);
        when(column2.isVisible()).thenReturn(true);
        when(column2.isLinked()).thenReturn(true);
        when(column2.getLink()).thenReturn(column1);
        gridWidget2.getModel().appendColumn(column2);

        this.gridLayer.add(gridWidget1);
        this.gridLayer.add(gridWidget2);

        gridLayer.refreshGridWidgetConnectors();

        checkConnectorsVisibility(true);

        gridLayer.enterPinnedMode(gridWidget1,
                                  new GridLayerRedrawManager.PrioritizedCommand(0) {
                                      @Override
                                      public void execute() {
                                          //Do nothing
                                      }
                                  });

        gridLayer.refreshGridWidgetConnectors();

        checkConnectorsVisibility(false);
    }

    @Test
    public void testAddOnEnterPinnedModeCommand() {

        final DefaultGridLayer defaultGridLayer = spy(new DefaultGridLayer());
        final DefaultPinnedModeManager defaultPinnedModeManager = mock(DefaultPinnedModeManager.class);
        final Command command = mock(Command.class);

        doReturn(defaultPinnedModeManager).when(defaultGridLayer).getPinnedModeManager();

        defaultGridLayer.addOnEnterPinnedModeCommand(command);

        verify(defaultPinnedModeManager).addOnEnterPinnedModeCommand(command);
    }

    @Test
    public void testAddOnExitPinnedModeCommand() {

        final DefaultGridLayer defaultGridLayer = spy(new DefaultGridLayer());
        final DefaultPinnedModeManager defaultPinnedModeManager = mock(DefaultPinnedModeManager.class);
        final Command command = mock(Command.class);

        doReturn(defaultPinnedModeManager).when(defaultGridLayer).getPinnedModeManager();

        defaultGridLayer.addOnExitPinnedModeCommand(command);

        verify(defaultPinnedModeManager).addOnExitPinnedModeCommand(command);
    }

    private void checkConnectorsVisibility(final boolean isVisible) {
        final Set<IPrimitive<?>> connectors = gridLayer.getGridWidgetConnectors();
        assertEquals(1,
                     connectors.size());
        assertEquals(isVisible,
                     connectors.iterator().next().isVisible());
    }
}
