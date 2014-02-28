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
import org.uberfire.client.workbench.part.WorkbenchPartPresenter;
import org.uberfire.client.workbench.widgets.panel.SimplePanel;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.PartDefinition;

@Dependent
@Named("TemplateWorkbenchPanelView")
public class TemplateWorkbenchPanelView extends AbstractTemplateWorkbenchPanelView<TemplateWorkbenchPanelPresenter> {


    @SuppressWarnings( "unused" )
    public TemplateWorkbenchPanelView() {
        initWidget( panel );
    }

    @Override
    public Widget asWidget() {
        return this;
    }

}
