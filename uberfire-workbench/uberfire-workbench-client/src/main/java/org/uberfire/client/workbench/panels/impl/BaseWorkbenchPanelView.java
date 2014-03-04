package org.uberfire.client.workbench.panels.impl;

import javax.enterprise.inject.Default;
import javax.inject.Inject;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.client.workbench.BeanFactory;
import org.uberfire.client.workbench.PanelManager;
import org.uberfire.client.workbench.panels.SplitPanel;
import org.uberfire.client.workbench.panels.WorkbenchPanelPresenter;
import org.uberfire.client.workbench.panels.WorkbenchPanelView;
import org.uberfire.client.workbench.widgets.dnd.WorkbenchDragAndDropManager;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.Position;

public abstract class BaseWorkbenchPanelView<P extends WorkbenchPanelPresenter>
        extends ResizeComposite
        implements WorkbenchPanelView<P> {


    @Inject
    protected WorkbenchDragAndDropManager dndManager;

    @Inject
    protected PanelManager panelManager;

    @Inject @Default
    protected BeanFactory factory;

    @Inject
    protected BaseWorkbenchPanelViewHelper baseWorkbenchPanelViewHelper;

    protected P presenter;

    public void addPanel( final PanelDefinition panel,
                          final WorkbenchPanelView view,
                          final Position position ) {

        baseWorkbenchPanelViewHelper.addPanel( panel, view, position, this );

    }

    @Override
    public void removePanel() {
    }

    @Override
    public P getPresenter() {
        return this.presenter;
    }

    protected void resizeParent( final Widget widget ) {
        if ( widget instanceof SplitPanel ) {
            scheduleResize( (RequiresResize) widget );
            return;
        } else if ( widget.getParent() != null && widget.getParent() instanceof RequiresResize ) {
            resizeParent( widget.getParent() );
        } else if ( widget instanceof RequiresResize ) {
            scheduleResize( (RequiresResize) widget );
        }
    }

    protected void scheduleResize( final RequiresResize widget ) {
        Scheduler.get().scheduleDeferred( new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                widget.onResize();
            }
        } );
    }
}
