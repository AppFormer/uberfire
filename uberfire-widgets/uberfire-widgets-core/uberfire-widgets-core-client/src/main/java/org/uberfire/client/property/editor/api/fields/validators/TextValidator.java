package org.uberfire.client.property.editor.api.fields.validators;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class TextValidator implements PropertyFieldValidator {

    @Override
    public boolean validate( Object value ) {
        if ( value.toString().length() > 8 ) {
            return true;
        }
        return false;
    }

    @Override
    public String getValidatorErrorMessage() {
        return "Invalid Text data.";
    }
}
