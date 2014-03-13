package org.uberfire.client.propertyEditor.fields;

import java.util.List;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.gwt.aria.client.Property;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.client.propertyEditor.api.PropertyEditorCategory;
import org.uberfire.client.propertyEditor.api.PropertyEditorChangeEvent;
import org.uberfire.client.propertyEditor.api.PropertyEditorFieldInfo;
import org.uberfire.client.propertyEditor.fields.validators.PropertyFieldValidator;
import org.uberfire.client.propertyEditor.widgets.PropertyEditorTextBox;

@Dependent
public class TextField extends AbstractField {

    @Inject
    Event<PropertyEditorChangeEvent> propertyEditorChangeEventEvent;

    @Override
    public Widget widget( final PropertyEditorFieldInfo property ) {
        final PropertyEditorTextBox textBox = GWT.create( PropertyEditorTextBox.class );
        textBox.setText( property.getCurrentStringValue() );
        addEnterKeyHandler( property, textBox );
        return textBox;
    }

    private void addEnterKeyHandler( final PropertyEditorFieldInfo property,
                                     final PropertyEditorTextBox textBox ) {
        textBox.addKeyDownHandler( new KeyDownHandler() {
            @Override
            public void onKeyDown( KeyDownEvent event ) {
                if ( event.getNativeKeyCode() == KeyCodes.KEY_ENTER ) {
                    if ( validate( property, textBox.getText() ) ) {
                        textBox.setCSSRegular();
                        property.setCurrentStringValue( textBox.getText() );
                        propertyEditorChangeEventEvent.fire( new PropertyEditorChangeEvent( property, textBox.getText() ) );
                    } else {
                        textBox.setCSSError();
                        textBox.setText( property.getCurrentStringValue() );
                    }
                }

            }

        } );
    }

    private boolean validate( PropertyEditorFieldInfo property,
                              String value ) {
        List<PropertyFieldValidator> validators = property.getValidators();

        for ( PropertyFieldValidator validator : validators ) {
            if ( !validator.validate( value ) ) {
                return false;
            }
        }

        return true;
    }

}
