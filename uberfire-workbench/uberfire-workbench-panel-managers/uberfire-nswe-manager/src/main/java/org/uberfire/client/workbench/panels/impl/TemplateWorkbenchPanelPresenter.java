

package org.uberfire.client.workbench.panels.impl;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.client.workbench.PanelManager;
import org.uberfire.client.workbench.TemplatePanelDefinitionImpl;
import org.uberfire.client.workbench.events.MaximizePlaceEvent;
import org.uberfire.client.workbench.events.MinimizePlaceEvent;
import org.uberfire.client.workbench.panels.WorkbenchPanelPresenter;
import org.uberfire.client.workbench.panels.WorkbenchPanelView;
import org.uberfire.client.workbench.part.WorkbenchPartPresenter;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.PartDefinition;
import org.uberfire.workbench.model.Position;

@Dependent
public class TemplateWorkbenchPanelPresenter implements WorkbenchPanelPresenter {

    protected TemplateWorkbenchPanelView view;

   /* @Inject
    private PanelManager panelManager;*/

    private PanelDefinition definition;

    private Event<MaximizePlaceEvent> maximizePanelEvent;

    private Event<MinimizePlaceEvent> minimizePanelEvent;

    public TemplateWorkbenchPanelPresenter() {
    }

    //old Constructor
    public TemplateWorkbenchPanelPresenter( PanelDefinition definition,
                                            boolean root ) {
        this.definition = definition;
        this.view = new TemplateWorkbenchPanelView( this.definition, root );
    }

    //ederign ?
  /*  @Inject
    public TemplateWorkbenchPanelPresenter( @Named("TemplateWorkbenchPanelView") final TemplateWorkbenchPanelView view,
                                          final PanelManager panelManager,
                                          final Event<MaximizePlaceEvent> maximizePanelEvent,
                                          final Event<MinimizePlaceEvent> minimizePanelEvent ) {
        this.view = view;
        this.panelManager = panelManager;
        this.maximizePanelEvent = maximizePanelEvent;
        this.minimizePanelEvent = minimizePanelEvent;
    }*/

    @PostConstruct
    private void init() {
        view.init( this );
    }

    @Override
    public PanelDefinition getDefinition() {
        return definition;
    }

    @Override
    public void setDefinition( PanelDefinition definition ) {
        this.definition = definition;
    }

    @Override
    public void addPart( WorkbenchPartPresenter.View view ) {
        getPanelView().addPart( view );
    }

    @Override
    public void addPart( WorkbenchPartPresenter.View view,
                         String contextId ) {
        getPanelView().addPart( view );
    }

    @Override
    public void removePart( PartDefinition part ) {
        view.removePart( part );
    }

    @Override
    public void addPanel( PanelDefinition panel,
                          WorkbenchPanelView view,
                          Position position ) {
        //ederign
        if ( panel instanceof TemplatePanelDefinitionImpl ) {
            TemplatePanelDefinitionImpl templateDefinition = (TemplatePanelDefinitionImpl) panel;
            Widget widget = view.asWidget();
            templateDefinition.perspective.setWidget( templateDefinition.getFieldName(), widget );
        }

    }

    @Override
    public void removePanel() {
        view.removePanel();
    }

    @Override
    public void changeTitle( PartDefinition part,
                             String title,
                             IsWidget titleDescorator ) {
        getPanelView().changeTitle( part, title, titleDescorator );
    }

    @Override
    public void setFocus( boolean hasFocus ) {
        view.setFocus( hasFocus );
    }

    @Override
    public void selectPart( PartDefinition part ) {
        if ( !contains( part ) ) {
            return;
        }
        view.selectPart( part );
    }

    private boolean contains( final PartDefinition part ) {
        return definition.getParts().contains( part );
    }

    @Override
    public void onPartFocus( PartDefinition part ) {
        //  panelManager.onPartFocus( part );
    }

    @Override
    public void onPartLostFocus() {
        //panelManager.onPartLostFocus();
    }

    @Override
    public void onPanelFocus() {
        //  panelManager.onPanelFocus( definition );
    }

    @Override
    public void onBeforePartClose( PartDefinition part ) {
        // panelManager.onBeforePartClose( part );
    }

    @Override
    public void maximize() {
        if ( !getDefinition().isRoot() ) {
            for ( final PartDefinition part : getDefinition().getParts() ) {
                maximizePanelEvent.fire( new MaximizePlaceEvent( part.getPlace() ) );
            }
        }
    }

    @Override
    public void minimize() {
        if ( !getDefinition().isRoot() ) {
            for ( final PartDefinition part : getDefinition().getParts() ) {
                minimizePanelEvent.fire( new MinimizePlaceEvent( part.getPlace() ) );
            }
        }
    }

    @Override
    public WorkbenchPanelView getPanelView() {
        return view;
    }

    @Override
    public void onResize( int width,
                          int height ) {
        //ederign ??
        getDefinition().setWidth( width == 0 ? null : width );
        getDefinition().setHeight( height == 0 ? null : height );
    }
}
