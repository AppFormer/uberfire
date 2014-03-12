package org.uberfire.client.propertyEditor.fields.validators;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class AnotherSampleValidator implements PropertyFieldValidator {

    @Override
    public boolean validate( Object value ) {
            return true;
    }
}
