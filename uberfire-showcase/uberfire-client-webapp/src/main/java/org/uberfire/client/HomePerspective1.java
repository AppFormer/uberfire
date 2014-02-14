package org.uberfire.client;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.client.annotations.Perspective;
import org.uberfire.client.annotations.WorkbenchPerspective;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.model.PanelType;
import org.uberfire.workbench.model.PerspectiveDefinition;
import org.uberfire.workbench.model.impl.PartDefinitionImpl;
import org.uberfire.client.workbench.TemplatePerspectiveDefinitionImpl;

@ApplicationScoped
@WorkbenchPerspective(
        identifier = "HomePerspective1")
@Templated("template1.html")
public class HomePerspective1 extends Composite {


    @DataField
    FlowPanel hello1 =  new FlowPanel();
    @DataField
    FlowPanel hello2 =  new FlowPanel();
    @DataField
    FlowPanel hello3 =  new FlowPanel();


    @PostConstruct
    public void setup() {

    }

    @Perspective
    public PerspectiveDefinition getPerspective() {
        final PerspectiveDefinition p = new TemplatePerspectiveDefinitionImpl( );
        //  final PerspectiveDefinition p = new TemplatePerspectiveDefinitionImpl( PanelType.ROOT_LIST );
        p.setTransient( true );
        p.setName( getClass().getName() );
        p.getRoot().addPart(
                 new PartDefinitionImpl(
                        new DefaultPlaceRequest( "HelloWorldScreen3" ) ) );

        return p;
    }

}