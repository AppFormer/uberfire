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
import org.uberfire.client.annotations.UFPanel;
import org.uberfire.client.annotations.UFPart;
import org.uberfire.client.annotations.WorkbenchPerspective;
import org.uberfire.client.workbench.TemplatePanelDefinitionImpl;
import org.uberfire.client.workbench.TemplatePerspectiveDefinitionImpl;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.PanelType;
import org.uberfire.workbench.model.PerspectiveDefinition;
import org.uberfire.workbench.model.Position;
import org.uberfire.workbench.model.impl.PartDefinitionImpl;

@ApplicationScoped
@WorkbenchPerspective(
        identifier = "HomePerspective",
        isDefault = true, isTemplate = true)
@Templated("template.html")
public class HomePerspective extends Composite {

    @DataField
    @UFPanel
    @UFPart(identifier = "HelloWorldScreen1")
    FlowPanel hello1 = new FlowPanel();

    @PostConstruct
    public void setup() {

    }

}