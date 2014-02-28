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
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.PanelType;

@ApplicationScoped
@WorkbenchPerspective(
        identifier = "HomePerspective",
        isDefault = true, isTemplate = true)
@Templated
public class HomePerspective extends Composite {

    @DataField
    @UFPanel(panelType = "PanelType.TEMPLATE", isDefault =  true)
    @UFPart("MoodScreen")
    FlowPanel moodScreen = new FlowPanel();

    @DataField
    @UFPanel(panelType = "PanelType.TEMPLATE")
    @UFPart("HomeScreen")
    FlowPanel homeScreen = new FlowPanel();

    @PostConstruct
    public void setup() {
    }

}