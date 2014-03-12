package org.uberfire.client.propertyEditor.api;

import java.util.ArrayList;
import java.util.List;

public class PropertyEditorCategory {

    private String name;
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

    public PropertyEditorCategory withField( PropertyEditorFieldInfo field ) {
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

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + priority;
        result = 31 * result + ( fields != null ? fields.hashCode() : 0 );
        return result;
    }

}
