package org.uberfire.client.propertyEditor.fields;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.client.propertyEditor.api.PropertyEditorChangeEvent;
import org.uberfire.client.propertyEditor.api.PropertyEditorFieldInfo;
import org.uberfire.client.propertyEditor.widgets.PropertyEditorPasswordTextBox;

@Dependent
public class SecretTextField extends AbstractField{

    @Inject
    Event<PropertyEditorChangeEvent> propertyEditorChangeEventEvent;

    public SecretTextField(){};

    @Override
    public Widget widget( final PropertyEditorFieldInfo property ) {
        final PropertyEditorPasswordTextBox passwordTextBox = GWT.create(PropertyEditorPasswordTextBox.class);
        passwordTextBox.setText( property.getCurrentStringValue() );
        addKeyDownHandler( property, passwordTextBox );
        return passwordTextBox;
    }

    private void addKeyDownHandler( final PropertyEditorFieldInfo property,
                                    final PropertyEditorPasswordTextBox passwordTextBox ) {
        passwordTextBox.addKeyDownHandler( new KeyDownHandler() {
            @Override
            public void onKeyDown( KeyDownEvent event ) {
                if ( event.getNativeKeyCode() == KeyCodes.KEY_ENTER ) {
                    if ( validate( property, passwordTextBox.getText() ) ) {
                        passwordTextBox.clearOldValidationErrors();
                        property.setCurrentStringValue( passwordTextBox.getText() );
                        propertyEditorChangeEventEvent.fire( new PropertyEditorChangeEvent( property, passwordTextBox.getText() ) );
                    } else {
                        passwordTextBox.setValidationError( getValidatorErrorMessage( property, passwordTextBox.getText() ) );
                        passwordTextBox.setText( property.getCurrentStringValue() );
                    }
                }

            }

        } );
    }
}
