package org.uberfire.client.screens.property.fields.validators;

public class LoginValidator implements PropertyFieldValidator {

    @Override
    public boolean validate( Object value ) {
        if ( value.toString().length() >= 8 ) {
            return true;
        }
        return false;
    }
}
