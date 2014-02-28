package org.uberfire.client;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.client.annotations.WorkbenchPart;
import org.uberfire.client.annotations.WorkbenchPerspective;

@ApplicationScoped
@WorkbenchPerspective(
        identifier = "AnotherPerspective", isTemplate = true)
@Templated("another_template.html")
public class AnotherPerspective extends Composite {


    @DataField
    @WorkbenchPart(part="MoodScreen")
    FlowPanel moodScreen = new FlowPanel();

    @DataField
    @WorkbenchPart(part="HomeScreen")
    FlowPanel homeScreen = new FlowPanel();

    @PostConstruct
    public void setup() {

    }

}