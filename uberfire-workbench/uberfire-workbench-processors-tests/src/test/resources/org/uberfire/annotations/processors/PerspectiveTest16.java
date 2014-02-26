package org.uberfire.annotations.processors;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.uberfire.client.annotations.Perspective;
import org.uberfire.client.annotations.UFParts;
import org.uberfire.client.annotations.WorkbenchPerspective;
import org.uberfire.workbench.model.PerspectiveDefinition;
import org.uberfire.client.annotations.UFPanel;
import org.uberfire.client.annotations.UFPart;

@ApplicationScoped
@WorkbenchPerspective(
        identifier = "HomePerspective",
        isDefault = true, isTemplate = true)
public class PerspectiveTest16 {

    @UFPanel(panelType = "PanelType.MULTI_TAB")
    @UFParts({@UFPart("HelloWorldScreen1"),@UFPart("HelloWorldScreen2")})
    Object teste = new Object();

    @UFPanel(panelType = "PanelType.MULTI_TAB")
    @UFParts({@UFPart("HelloWorldScreen1"),@UFPart("HelloWorldScreen2")})
    Object teste2 = new Object();

    @PostConstruct
    public void setup() {

    }


}