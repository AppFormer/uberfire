package org.uberfire.properties.editor.temp.fields;

import java.util.List;

import com.google.gwt.user.client.ui.Widget;
import org.uberfire.properties.editor.temp.fields.validators.PropertyFieldValidator;
import org.uberfire.properties.editor.temp.model.PropertyEditorFieldInfo;

public abstract class AbstractField {

    public abstract Widget widget( PropertyEditorFieldInfo property );

    protected boolean validate( PropertyEditorFieldInfo property,
                                String value ) {
        List<PropertyFieldValidator> validators = property.getValidators();

        for ( PropertyFieldValidator validator : validators ) {
            if ( !validator.validate( value ) ) {
                return false;
            }
        }

        return true;
    }

    protected String getValidatorErrorMessage( PropertyEditorFieldInfo property,
                                               String value) {
        List<PropertyFieldValidator> validators = property.getValidators();

        for ( PropertyFieldValidator validator : validators ) {
            if ( !validator.validate( value ) ) {
                return validator.getValidatorErrorMessage();
            }
        }

        return "";
    }
}
