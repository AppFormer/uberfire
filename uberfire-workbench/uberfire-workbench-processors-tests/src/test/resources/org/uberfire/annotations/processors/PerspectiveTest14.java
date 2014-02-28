package org.uberfire.annotations.processors;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import org.uberfire.client.annotations.WorkbenchPerspective;

@ApplicationScoped
@WorkbenchPerspective(
        identifier = "HomePerspective",
        isDefault = true, isTemplate = true)
public class PerspectiveTest14 {

    //@UFPanel(panelType = "PanelType.MULTI_TAB", isDefault =  true)
    //@UFParts({@UFPart("HelloWorldScreen1"),@UFPart("HelloWorldScreen2")})
    Object teste = new Object();

    @PostConstruct
    public void setup() {

    }


}