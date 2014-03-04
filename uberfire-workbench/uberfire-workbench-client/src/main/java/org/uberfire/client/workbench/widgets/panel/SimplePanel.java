package org.uberfire.client.workbench.widgets.panel;

import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.HasFocusHandlers;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import org.uberfire.client.workbench.part.WorkbenchPartPresenter;
import org.uberfire.workbench.model.PartDefinition;

public class SimplePanel
        extends Composite
        implements HasSelectionHandlers<PartDefinition>,
                   HasFocusHandlers {

    private FlowPanel container = new FlowPanel();

    private PartDefinition partDefinition;

    public SimplePanel() {
        initWidget( container );
    }


    public void setPart( final WorkbenchPartPresenter.View part ) {
        this.partDefinition = part.getPresenter().getDefinition();
        container.add( part );
    }

    public void clear() {
        partDefinition = null;
        container.clear();
    }

    public void setFocus( boolean hasFocus ) {
        if ( hasFocus ) {
            //style
        } else {
            //style
        }
    }

    @Override
    public HandlerRegistration addSelectionHandler( final SelectionHandler<PartDefinition> handler ) {
        return addHandler( handler, SelectionEvent.getType() );
    }

    @Override
    public HandlerRegistration addFocusHandler( FocusHandler handler ) {
        return null;
    }

    public void onResize() {
    }

    public WorkbenchPartPresenter.View getPartView() {
        return (WorkbenchPartPresenter.View) container.asWidget();
    }
}
