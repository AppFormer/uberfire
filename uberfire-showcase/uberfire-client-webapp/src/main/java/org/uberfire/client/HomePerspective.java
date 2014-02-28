package org.uberfire.client;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.client.annotations.WorkbenchPanel;
import org.uberfire.client.annotations.WorkbenchPart;
import org.uberfire.client.annotations.WorkbenchPerspective;
import org.uberfire.workbench.model.PanelType;

@ApplicationScoped
@WorkbenchPerspective(
        identifier = "HomePerspective",
        isDefault = true, isTemplate = true)
@Templated
public class HomePerspective extends Composite {

    @DataField
    @WorkbenchPanel(panelType = PanelType.TEMPLATE, isDefault =  true)
    @WorkbenchPart("MoodScreen")
    FlowPanel moodScreen = new FlowPanel();

    @DataField
    @WorkbenchPanel
    @WorkbenchPart("HomeScreen")
    FlowPanel homeScreen = new FlowPanel();

    @PostConstruct
    public void setup() {
    }

}