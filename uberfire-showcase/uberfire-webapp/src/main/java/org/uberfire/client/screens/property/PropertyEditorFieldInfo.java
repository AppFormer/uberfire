package org.uberfire.client.screens.property;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.uberfire.client.screens.property.fields.PropertyEditorType;
import org.uberfire.client.screens.property.fields.validators.PropertyFieldValidator;

public class PropertyEditorFieldInfo {

    private final String key;
    private String currentStringValue;
    private final String originalValue;
    private PropertyEditorCategory category;
    private final PropertyEditorType type;
    private List<String> comboValues;
    private int priority = Integer.MAX_VALUE;
    private List<PropertyFieldValidator> validators = new ArrayList<PropertyFieldValidator>();

    public PropertyEditorFieldInfo( String key,
                                    String currentStringValue,
                                    PropertyEditorType type ) {
        this.key = key;
        this.currentStringValue = currentStringValue;
        this.originalValue = currentStringValue;
        this.type = type;
        this.validators.addAll( type.getValidators() );
    }

    public PropertyEditorFieldInfo withComboValues( List<String> comboValues ) {
        this.comboValues = comboValues;
        return this;
    }

    public PropertyEditorFieldInfo withPriority( int priority ) {
        this.priority = priority;
        return this;
    }

    public PropertyEditorFieldInfo withValidators( PropertyFieldValidator... validators ) {

        for ( PropertyFieldValidator field : validators ) {
            this.validators.add( field );
        }
        return this;
    }

    public List<String> getComboValues() {
        return comboValues;
    }

    public PropertyEditorType getType() {
        return type;
    }

    public String getOriginalValue() {
        return originalValue;
    }

    public String getCurrentStringValue() {
        return currentStringValue;
    }

    public void setCurrentStringValue( String currentStringValue ) {
        this.currentStringValue = currentStringValue;
    }

    public void setPropertyEditorCategory( PropertyEditorCategory category ) {
        this.category = category;
    }

    public String getKey() {
        return key;
    }

    public int getPriority() {
        return priority;
    }

    public List<PropertyFieldValidator> getValidators() {
        return validators;
    }

}

