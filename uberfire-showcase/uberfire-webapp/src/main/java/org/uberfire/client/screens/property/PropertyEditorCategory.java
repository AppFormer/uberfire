package org.uberfire.client.screens.property;

import java.util.ArrayList;
import java.util.List;

public class PropertyEditorCategory {


    private final String name;
    private int priority = Integer.MAX_VALUE;
    private List<PropertyEditorFieldInfo> fields = new ArrayList<PropertyEditorFieldInfo>();

    public PropertyEditorCategory( String name ) {
        this.name = name;
    }

    public PropertyEditorCategory( String name,
                                   int priority ) {
        this.name = name;
        this.priority = priority;
    }

    public PropertyEditorCategory withFields( PropertyEditorFieldInfo field ) {
        field.setPropertyEditorCategory( this );
        fields.add( field );
        return this;
    }

    public String getName() {
        return name;
    }

    public int getPriority() {
        return priority;
    }

    public List<PropertyEditorFieldInfo> getFields() {
        return fields;
    }


}
