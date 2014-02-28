package org.uberfire.client.workbench.panels.impl;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.IsWidget;
import org.uberfire.client.workbench.panels.WorkbenchPanelPresenter;
import org.uberfire.client.workbench.part.WorkbenchPartPresenter;
import org.uberfire.client.workbench.widgets.panel.SimplePanel;
import org.uberfire.workbench.model.PartDefinition;

public abstract class AbstractTemplateWorkbenchPanelView<P extends WorkbenchPanelPresenter> extends BaseWorkbenchTemplatePanelView<P> {

    SimplePanel panel = new SimplePanel();

    @Override
    public void init( P presenter ) {
        this.presenter = presenter;
    }

    @Override
    public P getPresenter() {
        return this.presenter;
    }

    void addFocusAndSelectionHandler() {
        //ederign ?
        panel.addFocusHandler( new FocusHandler() {
            @Override
            public void onFocus( final FocusEvent event ) {
                panelManager.onPanelFocus( presenter.getDefinition() );
            }
        } );

        //When a tab is selected ensure content is resized and set focus
        panel.addSelectionHandler( new SelectionHandler<PartDefinition>() {
            @Override
            public void onSelection( final SelectionEvent<PartDefinition> event ) {
                presenter.onPartLostFocus();
                presenter.onPartFocus( event.getSelectedItem() );
            }
        } );
    }

    @Override
    public void clear() {
        panel.clear();
    }

    @Override
    public void addPart( final WorkbenchPartPresenter.View view ) {
        panel.setPart( view );
    }

    @Override
    public void changeTitle( final PartDefinition part,
                             final String title,
                             final IsWidget titleDecoration ) {
    }

    @Override
    public void selectPart( final PartDefinition part ) {
        scheduleResize();
    }

    @Override
    public void removePart( final PartDefinition part ) {
        panel.clear();
    }

    @Override
    public void setFocus( boolean hasFocus ) {
        panel.setFocus( hasFocus );
    }

    private void scheduleResize() {
        Scheduler.get().scheduleDeferred( new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                onResize();
            }
        } );
    }

    @Override
    public void onResize() {
        //ederign
        Element element = this.getElement();
        DOM.setStyleAttribute( element, "position", "relative" );
        System.out.print( "" );
    }
}
