package org.uberfire.client;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.client.annotations.UFPanel;
import org.uberfire.client.annotations.UFPart;
import org.uberfire.client.annotations.UFParts;
import org.uberfire.client.annotations.WorkbenchPerspective;

@ApplicationScoped
@WorkbenchPerspective(
        identifier = "HomePerspective",
        isDefault = true, isTemplate = true)
@Templated("template.html")
public class HomePerspective extends Composite {

    @DataField
    @UFPanel("PanelType.MULTI_TAB")
    @UFParts({@UFPart("HelloWorldScreen1"),@UFPart("HelloWorldScreen2")})
    FlowPanel hello1 = new FlowPanel();

    //@DataFiel
    //@UFPanel
    //@UFPart("HelloWorldScreen2")
    //FlowPanel hello2 = new FlowPanel();

    @PostConstruct
    public void setup() {

    }

}