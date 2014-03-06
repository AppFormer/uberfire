package org.uberfire.client.screens.property;

public class PropertyEditorFieldInfo {

    private String category;
    private final PropertyEditorType type;

    public PropertyEditorFieldInfo( String category,
                                    PropertyEditorType type ) {
        this.category = category;
        this.type = type;
    }

    public PropertyEditorType getType() {
        return type;
    }

    public String getCategory() {
        return category;
    }
}
