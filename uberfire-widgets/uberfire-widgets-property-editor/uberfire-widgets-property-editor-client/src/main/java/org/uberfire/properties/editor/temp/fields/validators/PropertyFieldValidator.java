package org.uberfire.properties.editor.temp.fields.validators;

public interface PropertyFieldValidator {

    public boolean validate( Object value );

    public String getValidatorErrorMessage();

}
