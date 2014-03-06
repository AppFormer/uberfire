package org.uberfire.client.screens.property;

import java.util.List;

import org.uberfire.client.screens.property.fields.PropertyEditorType;

public class PropertyEditorFieldInfo {

    private final String actualStringValue;
    private String category;
    private final PropertyEditorType type;

    private List<String> comboValues;

    public PropertyEditorFieldInfo( String actualStringValue,
                                    String category,
                                    PropertyEditorType type ) {
        this.actualStringValue = actualStringValue;
        this.category = category;
        this.type = type;
    }

    public PropertyEditorFieldInfo( String actualStringValue,
                                    String category,
                                    PropertyEditorType type,
                                    List<String> comboValues ) {
        this.actualStringValue = actualStringValue;
        this.category = category;
        this.type = type;
        this.comboValues = comboValues;
    }

    public List<String> getComboValues() {
        return comboValues;
    }

    public PropertyEditorType getType() {
        return type;
    }

    public String getCategory() {
        return category;
    }

    public String getActualStringValue() {
        return actualStringValue;
    }
}
