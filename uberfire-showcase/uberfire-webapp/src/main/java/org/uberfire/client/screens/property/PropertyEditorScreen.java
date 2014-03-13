package org.uberfire.client.screens.property;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.propertyEditor.PropertyEditorWidget;
import org.uberfire.client.propertyEditor.PropertyEditorException;
import org.uberfire.client.propertyEditor.PropertyEditorWidgetOld;
import org.uberfire.client.propertyEditor.api.PropertyEditorCategory;
import org.uberfire.client.propertyEditor.api.PropertyEditorEvent;
import org.uberfire.client.propertyEditor.api.PropertyEditorFieldInfo;
import org.uberfire.client.propertyEditor.fields.PropertyEditorType;
import org.uberfire.client.propertyEditor.fields.validators.AnotherSampleValidator;
import org.uberfire.client.propertyEditor.fields.validators.LoginValidator;

@Dependent
@Templated
@WorkbenchScreen(identifier = "PropertyEditorScreen")
public class PropertyEditorScreen extends Composite {

//    @DataField
//    @Inject
//    private PropertyEditorWidget propertyEditorWidget;

    @DataField
    @Inject
    private PropertyEditorWidget propertyEditor;

    @Inject
    Event<PropertyEditorEvent> propertyEditorEvent;

//    @PostConstruct
//    public void buildPropertyWidget(){
//
//        propertyEditor.add( new PropertyEditorWidget(createProperties()) );
//    }
    @Override
    @WorkbenchPartTitle
    public String getTitle() {
        return "Property Editor";
    }

    public void propertyEditorEventObserver( @Observes PropertyEditorEvent event ) {
        try {
            propertyEditor.handle(event);
        } catch ( PropertyEditorException e ) {
            e.printStackTrace();
        }
    }


}
