package org.uberfire.client.workbench.panels.impl;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;

import javax.enterprise.event.Event;

import org.jboss.errai.ioc.client.container.IOCBeanDef;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.uberfire.client.mvp.ActivityMetaInfo;
import org.uberfire.client.workbench.PanelManager;
import org.uberfire.client.workbench.type.ClientResourceType;
import org.uberfire.commons.data.Pair;
import org.uberfire.workbench.events.MaximizePlaceEvent;
import org.uberfire.workbench.events.MinimizePlaceEvent;
import org.uberfire.workbench.model.PanelDefinition;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;


public class SimpleWorkbenchPanelPresenterTest {

    private SimpleWorkbenchPanelView view;

    private PanelManager panelManager;

    private Event<MaximizePlaceEvent> maximizePanelEvent;

    private Event<MinimizePlaceEvent> minimizePanelEvent;

    private SimpleWorkbenchPanelPresenter presenter;

    @Before
    public void setup() {
    /*    view = mock( SimpleWorkbenchPanelView.class );
        panelManager = mock( PanelManager.class );
        maximizePanelEvent = (Event<MaximizePlaceEvent>) mock( MaximizePlaceEvent.class );
        minimizePanelEvent = (Event<MinimizePlaceEvent>) mock( MaximizePlaceEvent.class );
        presenter = new SimpleWorkbenchPanelPresenter(view, panelManager, maximizePanelEvent, minimizePanelEvent);*/
    }

    @Test
    public void oQueTestarei() {

    }

}
