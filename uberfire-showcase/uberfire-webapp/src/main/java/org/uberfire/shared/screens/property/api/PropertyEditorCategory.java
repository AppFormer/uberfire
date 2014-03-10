package org.uberfire.shared.screens.property.api;

import java.util.ArrayList;
import java.util.List;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class PropertyEditorCategory {


    private String name;
    private int priority = Integer.MAX_VALUE;
    private List<PropertyEditorFieldInfo> fields = new ArrayList<PropertyEditorFieldInfo>();

    public PropertyEditorCategory(){}

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


}
