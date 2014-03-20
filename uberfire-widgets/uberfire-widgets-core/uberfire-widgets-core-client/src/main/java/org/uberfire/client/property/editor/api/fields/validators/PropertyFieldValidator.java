package org.uberfire.client.property.editor.api.fields.validators;

public interface PropertyFieldValidator {

    public boolean validate( Object value );

    public String getValidatorErrorMessage();

}
