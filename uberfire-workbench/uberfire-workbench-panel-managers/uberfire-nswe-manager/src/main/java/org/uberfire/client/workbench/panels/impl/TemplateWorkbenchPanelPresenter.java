package org.uberfire.client.workbench.panels.impl;

import javax.annotation.PostConstruct;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.client.workbench.TemplatePanelDefinitionImpl;
import org.uberfire.client.workbench.panels.WorkbenchPanelPresenter;
import org.uberfire.client.workbench.panels.WorkbenchPanelView;
import org.uberfire.client.workbench.part.WorkbenchPartPresenter;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.PartDefinition;
import org.uberfire.workbench.model.Position;

    public class TemplateWorkbenchPanelPresenter implements WorkbenchPanelPresenter {


    protected TemplateWorkbenchPanelView view;

    private PanelDefinition def;

    public TemplateWorkbenchPanelPresenter( PanelDefinition definition ) {
        this.def = definition;
        this.view = new TemplateWorkbenchPanelView(def);
    }

    @PostConstruct
    private void init() {
        view.init( this );
    }

    @Override
    public PanelDefinition getDefinition() {
        return def;
    }

    @Override
    public void setDefinition( PanelDefinition definition ) {
        this.def = definition;
    }

    @Override
    public void addPart( WorkbenchPartPresenter.View view ) {
        //ederign
        System.out.println("oi");
    }

    @Override
    public void addPart( WorkbenchPartPresenter.View view,
                         String contextId ) {
        //ederign
        System.out.println("oi");
    }

    @Override
    public void removePart( PartDefinition part ) {
        //ederign
        System.out.println("oi");
    }

    @Override
    public void addPanel( PanelDefinition panel,
                          WorkbenchPanelView view,
                          Position position ) {
        if ( panel instanceof TemplatePanelDefinitionImpl ) {
            TemplatePanelDefinitionImpl templateDefinition = (TemplatePanelDefinitionImpl) panel;
            templateDefinition.perspective.setWidget( templateDefinition.getFieldName(), view.asWidget() );
        }
    }

    @Override
    public void removePanel() {
        //ederign
        System.out.println("oi");

    }

    @Override
    public void changeTitle( PartDefinition part,
                             String title,
                             IsWidget titleDecoration ) {
        //ederign
    }

    @Override
    public void setFocus( boolean hasFocus ) {
        //ederign
    }

    @Override
    public void selectPart( PartDefinition part ) {
        //ederign
    }

    @Override
    public void onPartFocus( PartDefinition part ) {
        //ederign
    }

    @Override
    public void onPartLostFocus() {
        //ederign
    }

    @Override
    public void onPanelFocus() {
        //ederign
    }

    @Override
    public void onBeforePartClose( PartDefinition part ) {
        //ederign
    }

    @Override
    public void maximize() {
        //ederign
    }

    @Override
    public void minimize() {
        //ederign
    }

    @Override
    public WorkbenchPanelView getPanelView() {
        return view;
    }

    @Override
    public void onResize( int width,
                          int height ) {
        //ederign
    }
}
