package org.uberfire.client;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import org.uberfire.client.annotations.WorkbenchPanel;
import org.uberfire.client.annotations.WorkbenchPart;
import org.uberfire.client.annotations.WorkbenchParts;
import org.uberfire.client.annotations.WorkbenchPerspective;
import org.uberfire.workbench.model.PanelType;

@ApplicationScoped
@WorkbenchPerspective(
        identifier = "HomePerspective",
        isDefault = true, isTemplate = true)
public class PerspectiveTest12 {

    @WorkbenchPanel(panelType = PanelType.MULTI_TAB, isDefault =  true)
    @WorkbenchParts({@WorkbenchPart(part = "HelloWorldScreen1"),@WorkbenchPart(part = "HelloWorldScreen2")})
    Object teste = new Object();

    @WorkbenchPanel
    @WorkbenchPart(part = "HelloWorldScreen3")
    Object teste2 = new Object();

    @WorkbenchPanel
    @WorkbenchPart(part = "HelloWorldScreen4")
    Object teste3 = new Object();

    @WorkbenchPanel
    @WorkbenchPart(part = "HelloWorldScreen5")
    Object teste4 = new Object();

    @PostConstruct
    public void setup() {

    }


}