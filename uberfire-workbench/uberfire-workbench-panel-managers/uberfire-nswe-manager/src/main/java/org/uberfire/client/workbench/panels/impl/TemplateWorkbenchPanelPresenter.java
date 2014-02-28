

package org.uberfire.client.workbench.panels.impl;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;

import org.uberfire.client.workbench.PanelManager;
import org.uberfire.client.workbench.events.MaximizePlaceEvent;
import org.uberfire.client.workbench.events.MinimizePlaceEvent;

@Dependent
public class TemplateWorkbenchPanelPresenter extends AbstractTemplateWorkbenchPanelPresenter<TemplateWorkbenchPanelView> {


//    //old Constructor
//    public TemplateWorkbenchPanelPresenter( PanelDefinition definition) {
//        this.definition = definition;
//        this.view = new TemplateWorkbenchPanelView( this.definition);
//    }

    @Inject
    public TemplateWorkbenchPanelPresenter( @Named("TemplateWorkbenchPanelView") final TemplateWorkbenchPanelView view,
                                            final PanelManager panelManager,
                                            final Event<MaximizePlaceEvent> maximizePanelEvent,
                                            final Event<MinimizePlaceEvent> minimizePanelEvent ) {
        this.view = view;
        this.maximizePanelEvent = maximizePanelEvent;
        this.minimizePanelEvent = minimizePanelEvent;
    }

}
