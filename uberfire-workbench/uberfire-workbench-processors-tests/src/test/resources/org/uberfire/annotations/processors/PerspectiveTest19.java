package org.uberfire.annotations.processors;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import com.google.gwt.user.client.ui.FlowPanel;
import org.uberfire.client.annotations.ParameterMapping;
import org.uberfire.client.annotations.WorkbenchPanel;
import org.uberfire.client.annotations.WorkbenchPart;
import org.uberfire.client.annotations.WorkbenchParts;
import org.uberfire.client.annotations.WorkbenchPerspective;
import org.uberfire.workbench.model.PanelType;
@ApplicationScoped
@WorkbenchPerspective(
        identifier = "HomePerspective",
        isDefault = true, isTemplate = true)
public class PerspectiveTest19 {



    @WorkbenchPart(part = "noParameterScreen")
    Object nopParameter = new FlowPanel();

    @WorkbenchPanel(isDefault = true)
    @WorkbenchPart(part = "oneParameterScreen", parameters = @ParameterMapping(name="uber", val="fire"))
    Object oneParameter = new FlowPanel();

    @WorkbenchPart(part = "twoParametersScreen", parameters = {@ParameterMapping(name="uber", val="fire"),@ParameterMapping(name="uber1", val="fire1")})
    Object twoParameters = new FlowPanel();

    @PostConstruct
    public void setup() {

    }


}