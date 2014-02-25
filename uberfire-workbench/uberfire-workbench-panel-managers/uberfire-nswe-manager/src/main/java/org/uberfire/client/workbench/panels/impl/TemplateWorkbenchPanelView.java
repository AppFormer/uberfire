package org.uberfire.client.workbench.panels.impl;

import javax.enterprise.context.Dependent;
import javax.inject.Named;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.client.workbench.TemplatePanelDefinitionImpl;
import org.uberfire.client.workbench.part.WorkbenchPartPresenter;
import org.uberfire.client.workbench.widgets.panel.SimplePanel;
import org.uberfire.client.workbench.widgets.panel.StaticFocusedResizePanel;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.PartDefinition;

@Dependent
@Named("TemplateWorkbenchPanelView")
public class TemplateWorkbenchPanelView extends BaseWorkbenchPanelView<TemplateWorkbenchPanelPresenter> {

    private PanelDefinition def;
    private boolean root;

    //@Inject
    //PlaceManager placeManager;

    StaticFocusedResizePanel panel = new StaticFocusedResizePanel();
    //SimplePanel panel = new SimplePanel();

    public TemplateWorkbenchPanelView() {
        initWidget( panel );
    }

    public TemplateWorkbenchPanelView( PanelDefinition def,
                                       boolean root ) {
        this.def = def;
        this.root = root;

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

        initWidget( panel );
    }

    @Override
    public void init( TemplateWorkbenchPanelPresenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public TemplateWorkbenchPanelPresenter getPresenter() {
        return this.presenter;
    }

    @Override
    public void clear() {
        panel.clear();
    }

    @Override
    public void addPart( final WorkbenchPartPresenter.View view ) {
        if ( panel.getPartView() != null ) {
            //ederign
           /* placeManager.tryClosePlace( getPlaceOfPartView(), new Command() {
                @Override
                public void execute() {
                    panel.setPart( view );
                }
            } );*/
        } else {
            panel.setPart( view );
        }

    }

    PlaceRequest getPlaceOfPartView() {
        return panel.getPartView().getPresenter().getDefinition().getPlace();
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
//        final Widget parent = getParent();
//        if ( parent != null ) {
//            final int width = parent.getOffsetWidth();
//            final int height = parent.getOffsetHeight();
//            setPixelSize( width, height );
//            presenter.onResize( width, height );
//            panel.setPixelSize( width, height );
//            resizeSuper();
//        }
    }

    void resizeSuper() {
        super.onResize();
    }

    @Override
    public Widget asWidget() {
        if ( root ) {
            return ( (TemplatePanelDefinitionImpl) def ).perspective.getRealPresenterWidget();
        } else {
            return this;
        }
    }

}
