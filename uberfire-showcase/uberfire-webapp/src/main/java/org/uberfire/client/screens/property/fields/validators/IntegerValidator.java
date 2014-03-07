package org.uberfire.client.screens.property.fields.validators;

public class IntegerValidator implements PropertyFieldValidator {

    @Override
    public boolean validate( Object value ) {
        try {
            Integer.parseInt( value.toString() );
            return true;
        } catch ( Exception e ) {
            return false;
        }
    }
}
