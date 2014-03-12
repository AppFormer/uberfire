package org.uberfire.client.screens.property;

import java.util.List;
import java.util.Map;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.propertyEditor.PropertyEditorException;
import org.uberfire.client.propertyEditor.PropertyEditorWidget;
import org.uberfire.client.propertyEditor.api.PropertyEditorEvent;

@Dependent
@Templated
@WorkbenchScreen(identifier = "PropertyEditorScreen")
public class PropertyEditorScreen extends Composite {

    @DataField
    @Inject
    private PropertyEditorWidget propertyEditorWidget;

    @Inject
    Event<PropertyEditorEvent> propertyEditorEvent;

    @Override
    @WorkbenchPartTitle
    public String getTitle() {
        return "Property Editor";
    }

    public void propertyEditorEventObserver( @Observes PropertyEditorEvent event ) {
        try {
            propertyEditorWidget.handle(event);
        } catch ( PropertyEditorException e ) {
            e.printStackTrace();
        }
    }

}
