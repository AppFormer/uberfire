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
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.PanelType;
import org.uberfire.workbench.model.PerspectiveDefinition;
import org.uberfire.workbench.model.Position;
import org.uberfire.workbench.model.impl.PanelDefinitionImpl;
import org.uberfire.workbench.model.impl.PartDefinitionImpl;
import org.uberfire.workbench.model.impl.TemplatePerspectiveDefinitionImpl;

@ApplicationScoped
@WorkbenchPerspective(
        identifier = "HomePerspective",
        isDefault = true)
@Templated("template.html")
public class HomePerspective extends Composite {

    @DataField
    TextBox messageText = new TextBox();
    @DataField
    TextBox resultText = new TextBox();

    @DataField
    FlowPanel hello1 = new FlowPanel();
    @DataField
    FlowPanel hello2 = new FlowPanel();
    @DataField
    FlowPanel hello3 = new FlowPanel();

    @Inject
    @DataField
    Button sendMessage;

    @PostConstruct
    public void setup() {
        sendMessage.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                resultText.setText( messageText.getText() );
                System.out.println( "blu" );
            }
        } );
    }

    @Perspective
    public PerspectiveDefinition getPerspective() {
        final PerspectiveDefinition p = new TemplatePerspectiveDefinitionImpl( this );
        //final PerspectiveDefinition p = new TemplatePerspectiveDefinitionImpl( PanelType.ROOT_LIST );
        p.setName( getClass().getName() );

/*        p.getRoot().addPart(
                new PartDefinitionImpl(
                        new DefaultPlaceRequest( "HelloWorldScreen1" ) ) );
        p.getRoot().addPart(
                new PartDefinitionImpl(
                        new DefaultPlaceRequest( "HelloWorldScreen2" ) ) );*/

        PanelDefinition panelDefinition = new PanelDefinitionImpl( PanelType.MULTI_TAB );
        panelDefinition.addPart(
                new PartDefinitionImpl(
                        new DefaultPlaceRequest( "HelloWorldScreen1" ) ) {
                } );
        panelDefinition.addPart(
                new PartDefinitionImpl(
                        new DefaultPlaceRequest( "HelloWorldScreen2" ) ) {
                } );
        p.getRoot().appendChild( Position.EAST, panelDefinition );

        return p;
    }

}