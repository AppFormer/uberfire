package org.uberfire.client;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.uberfire.client.annotations.Perspective;
import org.uberfire.client.annotations.UFParts;
import org.uberfire.client.annotations.WorkbenchPerspective;
import org.uberfire.workbench.model.PerspectiveDefinition;
import org.uberfire.client.annotations.UFPanel;
import org.uberfire.client.annotations.UFPart;
import org.uberfire.workbench.model.PanelType;

@ApplicationScoped
@WorkbenchPerspective(
        identifier = "HomePerspective",
        isDefault = true, isTemplate = true)
public class PerspectiveTest12 {

    @UFPanel(panelType = PanelType.MULTI_TAB, isDefault =  true)
    @UFParts({@UFPart("HelloWorldScreen1"),@UFPart("HelloWorldScreen2")})
    Object teste = new Object();

    @UFPanel
    @UFPart("HelloWorldScreen3")
    Object teste2 = new Object();

    @UFPanel
    @UFPart("HelloWorldScreen4")
    Object teste3 = new Object();

    @UFPanel
    @UFPart("HelloWorldScreen5")
    Object teste4 = new Object();

    @PostConstruct
    public void setup() {

    }


}