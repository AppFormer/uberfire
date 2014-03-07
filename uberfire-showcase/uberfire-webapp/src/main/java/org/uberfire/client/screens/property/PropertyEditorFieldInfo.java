package org.uberfire.client.screens.property;

import java.util.List;

import org.uberfire.client.screens.property.fields.PropertyEditorType;

public class PropertyEditorFieldInfo {

    private final String key;
    private final String actualStringValue;
    private PropertyEditorCategory category;
    private final PropertyEditorType type;
    private List<String> comboValues;
    private int priority = Integer.MAX_VALUE;

    public PropertyEditorFieldInfo( String key,
                                    String actualStringValue,
                                    PropertyEditorCategory category,
                                    PropertyEditorType type ) {
        this.key = key;
        this.actualStringValue = actualStringValue;
        this.category = category;
        this.type = type;
    }

    public PropertyEditorFieldInfo( String key,
                                    String actualStringValue,
                                    PropertyEditorCategory category,
                                    PropertyEditorType type,
                                    List<String> comboValues ) {
        this.key = key;
        this.actualStringValue = actualStringValue;
        this.category = category;
        this.type = type;
        this.comboValues = comboValues;
    }

    public PropertyEditorFieldInfo( String key,
                                    String actualStringValue,
                                    PropertyEditorCategory category,
                                    PropertyEditorType type, int priority) {
        this.key = key;
        this.actualStringValue = actualStringValue;
        this.category = category;
        this.type = type;
        this.priority = priority;
    }

    public PropertyEditorFieldInfo( String key,
                                    String actualStringValue,
                                    PropertyEditorCategory category,
                                    PropertyEditorType type,
                                    List<String> comboValues, int priority ) {
        this.key = key;
        this.actualStringValue = actualStringValue;
        this.category = category;
        this.type = type;
        this.comboValues = comboValues;
        this.priority = priority;
    }


    public List<String> getComboValues() {
        return comboValues;
    }

    public PropertyEditorType getType() {
        return type;
    }

    public PropertyEditorCategory getCategory() {
        return category;
    }

    public String getActualStringValue() {
        return actualStringValue;
    }

    public String getKey() {
        return key;
    }

    public int getPriority() {
        return priority;
    }

}

