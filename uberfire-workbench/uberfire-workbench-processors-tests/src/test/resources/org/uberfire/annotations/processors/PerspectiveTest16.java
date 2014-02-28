package org.uberfire.annotations.processors;

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
public class PerspectiveTest16 {

    @WorkbenchPanel(panelType = PanelType.MULTI_TAB)
    @WorkbenchParts({@WorkbenchPart("HelloWorldScreen1"),@WorkbenchPart("HelloWorldScreen2")})
    Object teste = new Object();

    @WorkbenchPanel(panelType = PanelType.MULTI_TAB)
    @WorkbenchParts({@WorkbenchPart("HelloWorldScreen1"),@WorkbenchPart("HelloWorldScreen2")})
    Object teste2 = new Object();

    @PostConstruct
    public void setup() {

    }


}