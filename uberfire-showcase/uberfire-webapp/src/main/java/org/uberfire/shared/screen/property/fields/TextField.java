package org.uberfire.shared.screen.property.fields;

import java.util.List;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.shared.screens.property.api.PropertyEditorChangeEvent;
import org.uberfire.shared.screens.property.api.PropertyEditorFieldInfo;
import org.uberfire.shared.screen.property.fields.validators.PropertyFieldValidator;

@Dependent
public class TextField extends AbstractField {

    @Inject
    Event<PropertyEditorChangeEvent> propertyEditorChangeEventEvent;

    @Override
    public Widget widget( final PropertyEditorFieldInfo property ) {
        final TextBox textBox = new TextBox();
        textBox.setText( property.getCurrentStringValue() );
        textBox.addKeyDownHandler( new KeyDownHandler() {
            @Override
            public void onKeyDown( KeyDownEvent event ) {
                if ( event.getNativeKeyCode() == KeyCodes.KEY_ENTER ) {
                    if ( validate( property, textBox.getText() ) ) {
                        property.setCurrentStringValue( textBox.getText() );
                        propertyEditorChangeEventEvent.fire( new PropertyEditorChangeEvent( property, textBox.getText() ) );
                    } else {
                        textBox.setText( property.getCurrentStringValue() );
                    }
                }

            }

        } );
        return textBox;
    }

    private boolean validate( PropertyEditorFieldInfo property,
                              String value ) {
        List<PropertyFieldValidator> validators = property.getValidators();

        for ( PropertyFieldValidator validator : validators ) {
            if ( !validator.validate( value ) ) {
                //ederign fire msg?
                return false;
            }
        }

        return true;
    }

}
