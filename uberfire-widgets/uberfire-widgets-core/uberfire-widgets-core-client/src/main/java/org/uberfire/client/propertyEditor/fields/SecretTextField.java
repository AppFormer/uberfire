package org.uberfire.client.propertyEditor.fields;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.client.propertyEditor.api.PropertyEditorChangeEvent;
import org.uberfire.client.propertyEditor.api.PropertyEditorFieldInfo;

@Dependent
public class SecretTextField extends AbstractField{

    @Inject
    Event<PropertyEditorChangeEvent> propertyEditorChangeEventEvent;

    public SecretTextField(){};

    @Override
    public Widget widget( final PropertyEditorFieldInfo property ) {
        final PasswordTextBox passwordTextBox = new PasswordTextBox();
        passwordTextBox.setText( property.getCurrentStringValue() );
        addKeyDownHandler( property, passwordTextBox );
        return passwordTextBox;
    }

    private void addKeyDownHandler( final PropertyEditorFieldInfo property,
                                    final PasswordTextBox passwordTextBox ) {
        passwordTextBox.addKeyDownHandler( new KeyDownHandler() {
            @Override
            public void onKeyDown( KeyDownEvent event ) {
                if ( event.getNativeKeyCode() == KeyCodes.KEY_ENTER ) {
                    propertyEditorChangeEventEvent.fire( new PropertyEditorChangeEvent( property, passwordTextBox.getText() ) );
                }

            }

        } );
    }
}
