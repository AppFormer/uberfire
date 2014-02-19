package org.uberfire.client.workbench.panels.impl;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.client.workbench.TemplatePanelDefinitionImpl;
import org.uberfire.client.workbench.panels.WorkbenchPanelView;
import org.uberfire.client.workbench.part.WorkbenchPartPresenter;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.PartDefinition;
import org.uberfire.workbench.model.Position;

public class TemplateWorkbenchPanelView  extends BaseWorkbenchPanelView<TemplateWorkbenchPanelPresenter>  {

    private final PanelDefinition def;

    public TemplateWorkbenchPanelView( PanelDefinition def ) {
        this.def=def;
    }

    @Override
    public TemplateWorkbenchPanelPresenter getPresenter() {
        return this.presenter;
    }

    @Override
    public void clear() {
        //ederign
    }

    @Override
    public void addPart( WorkbenchPartPresenter.View view ) {
        //ederign
        System.out.println();

    }

    @Override
    public void addPanel( PanelDefinition panel,
                          WorkbenchPanelView view,
                          Position position ) {
        //ederign
        System.out.println();

    }

    @Override
    public void changeTitle( PartDefinition part,
                             String title,
                             IsWidget titleDecoration ) {
        //ederign
    }

    @Override
    public void selectPart( PartDefinition part ) {
        //ederign
    }

    @Override
    public void removePart( PartDefinition part ) {
        //ederign
    }

    @Override
    public void removePanel() {
        //ederign
    }

    @Override
    public void setFocus( boolean hasFocus ) {
        //ederign
    }

    @Override
    public void onResize() {
        //ederign
    }

    @Override
    public Widget asWidget() {
        return ( (TemplatePanelDefinitionImpl) def ).perspective.getRealPresenterWidget();
    }

    @Override
    public void init( TemplateWorkbenchPanelPresenter presenter ) {
        this.presenter = presenter;
    }
}
