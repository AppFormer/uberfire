package org.uberfire.client.screens.property;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.gwt.user.client.ui.Composite;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.propertyEditor.PropertyEditorWidget;
import org.uberfire.client.propertyEditor.api.PropertyEditorEvent;

@Dependent
@Templated
@WorkbenchScreen(identifier = "PropertyEditorScreen")
public class PropertyEditorScreen extends Composite {

    @DataField
    @Inject
    private PropertyEditorWidget propertyEditor;


    @Inject
    Event<PropertyEditorEvent> propertyEditorEvent;

    @Override
    @WorkbenchPartTitle

    public String getTitle() {
        return "Property Editor";
    }

    public void propertyEditorEventObserver( @Observes PropertyEditorEvent event ) {
        propertyEditor.handle( event );
    }

}
