package org.uberfire.annotations.processors;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import com.google.gwt.user.client.ui.FlowPanel;
import org.uberfire.client.annotations.WorkbenchPanel;
import org.uberfire.client.annotations.WorkbenchPart;
import org.uberfire.client.annotations.WorkbenchParts;
import org.uberfire.client.annotations.WorkbenchPerspective;
import org.uberfire.workbench.model.PanelType;
@ApplicationScoped
@WorkbenchPerspective(
        identifier = "HomePerspective",
        isDefault = true, isTemplate = true)
public class PerspectiveTest17 {

    @WorkbenchPanel(panelType = PanelType.TEMPLATE, isDefault =  true)
    @WorkbenchPart("TesteScreen")
    Object teste1 = new FlowPanel();

    @WorkbenchPart("TesteScreen1")
    Object teste2 = new Object();

    @PostConstruct
    public void setup() {

    }


}