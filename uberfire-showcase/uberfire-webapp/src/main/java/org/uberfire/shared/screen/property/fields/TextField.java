package org.uberfire.shared.screen.property.fields;

import java.util.List;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
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
        addSelectAllTOnFocusTo( textBox );
        addEnterKeyHandler( property, textBox );
        return textBox;
    }

    private void addEnterKeyHandler( final PropertyEditorFieldInfo property,
                                     final TextBox textBox ) {
        textBox.addKeyDownHandler( new KeyDownHandler() {
            @Override
            public void onKeyDown( KeyDownEvent event ) {
                if ( event.getNativeKeyCode() == KeyCodes.KEY_ENTER ) {
                    if ( validate( property, textBox.getText() ) ) {
                        //ederign
                        textBox.getElement().setAttribute( "style", "border: 2px solid rgb(204, 204, 204)" );
                        property.setCurrentStringValue( textBox.getText() );
                        propertyEditorChangeEventEvent.fire( new PropertyEditorChangeEvent( property, textBox.getText() ) );
                    } else {
                        //ederign
                        textBox.getElement().setAttribute( "style", "border: 2px solid rgb(255, 0, 0)" );
                        textBox.setText( property.getCurrentStringValue() );
                    }
                }

            }

        } );
    }

    private void addSelectAllTOnFocusTo( final TextBox textBox ) {
        textBox.addFocusHandler( new FocusHandler() {
            @Override
            public void onFocus( FocusEvent event ) {
                textBox.selectAll();
            }
        } );
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
