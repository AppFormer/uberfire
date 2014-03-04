package org.uberfire.client.screens;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;

import com.github.gwtbootstrap.client.ui.Label;
import com.google.gwt.user.client.ui.IsWidget;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.shared.Mood;

@Dependent
@WorkbenchScreen(identifier = "AnotherScreen")
public class AnotherScreen {

    private static final String ORIGINAL_TEXT = "AnotherScreen?";

    private Label label = new Label( ORIGINAL_TEXT );

    @WorkbenchPartTitle
    public String getTitle() {
        return "homeScreen";
    }

    @WorkbenchPartView
    public IsWidget getView() {
        return label;
    }


}