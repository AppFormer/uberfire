package org.uberfire.shared.screen.property.fields.validators;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class AnotherValidator implements PropertyFieldValidator {

    @Override
    public boolean validate( Object value ) {
            return true;
    }
}
