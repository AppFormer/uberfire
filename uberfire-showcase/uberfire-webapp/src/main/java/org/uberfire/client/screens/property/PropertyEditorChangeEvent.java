package org.uberfire.client.screens.property;

public class PropertyEditorChangeEvent {

    private final PropertyEditorFieldInfo property;
    private final String newValue;

    public PropertyEditorChangeEvent( PropertyEditorFieldInfo property,
                                      String newValue ) {
        this.property = property;
        this.newValue = newValue;
    }

    public PropertyEditorFieldInfo getProperty() {
        return property;
    }

    public String getNewValue() {
        return newValue;
    }
}
