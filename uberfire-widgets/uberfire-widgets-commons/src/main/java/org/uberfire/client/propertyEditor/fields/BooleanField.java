package org.uberfire.client.propertyEditor.fields;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.CheckBox;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.client.propertyEditor.api.PropertyEditorChangeEvent;
import org.uberfire.client.propertyEditor.api.PropertyEditorFieldInfo;

@Dependent
public class BooleanField extends AbstractField {

    @Inject
    Event<PropertyEditorChangeEvent> propertyEditorChangeEventEvent;

    @Override
    public Widget widget( final PropertyEditorFieldInfo property ) {
        final CheckBox checkBox = new CheckBox();
        checkBox.setValue( Boolean.valueOf( property.getCurrentStringValue() ) );
        checkBox.addValueChangeHandler( new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange( ValueChangeEvent<Boolean> event ) {
                propertyEditorChangeEventEvent.fire( new PropertyEditorChangeEvent( property, checkBox.getValue().toString() ) );

            }
        } );

        return checkBox;
    }
}
