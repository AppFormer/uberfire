package org.uberfire.client.propertyEditor.fields.validators;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
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
