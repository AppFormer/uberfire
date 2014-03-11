package org.uberfire.client.propertyEditor.fields.validators;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class LoginValidator implements PropertyFieldValidator {

    @Override
    public boolean validate( Object value ) {
        if ( value.toString().length() >= 8 ) {
            return true;
        }
        return false;
    }
}
