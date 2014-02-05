/*
 * Copyright 2012 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.uberfire.client.workbench.panels.impl;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;

import org.uberfire.client.mvp.ActivityManager;
import org.uberfire.client.workbench.PanelManager;
import org.uberfire.client.workbench.events.MaximizePlaceEvent;
import org.uberfire.client.workbench.events.MinimizePlaceEvent;

/**
 * A Workbench panel that can contain WorkbenchParts.
 */
@Dependent
public class MultiTabWorkbenchPanelPresenter extends BaseMultiPartWorkbenchPanelPresenter {

    @Inject
    public MultiTabWorkbenchPanelPresenter( @Named("MultiTabWorkbenchPanelView") final MultiTabWorkbenchPanelView view,
                                            final ActivityManager activityManager,
                                            final PanelManager panelManager,
                                            final Event<MaximizePlaceEvent> maximizePanelEvent,
                                            final Event<MinimizePlaceEvent> minimizePanelEvent ) {
        this.view = view;
        this.activityManager = activityManager;
        this.panelManager = panelManager;
        this.maximizePanelEvent = maximizePanelEvent;
        this.minimizePanelEvent = minimizePanelEvent;
    }

}
