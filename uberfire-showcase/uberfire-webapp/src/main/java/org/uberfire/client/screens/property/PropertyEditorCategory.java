package org.uberfire.client.screens.property;

import java.util.ArrayList;
import java.util.List;

import org.uberfire.client.screens.property.fields.PropertyEditorType;

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

    public void addField( String key,
                          String actualStringValue,
                          PropertyEditorType type ) {
        fields.add( new PropertyEditorFieldInfo( key, actualStringValue, this, type ) );
    }

    public void addField( String key,
                          String actualStringValue,
                          PropertyEditorType type,
                          List<String> comboValues ) {
        fields.add( new PropertyEditorFieldInfo( key, actualStringValue, this, type, comboValues ) );

    }

    public void addField( String key,
                          String actualStringValue,
                          PropertyEditorType type, int priority) {
        fields.add( new PropertyEditorFieldInfo( key, actualStringValue, this, type, priority ) );
    }

    public void addField( String key,
                          String actualStringValue,
                          PropertyEditorType type,
                          List<String> comboValues , int priority) {
        fields.add( new PropertyEditorFieldInfo( key, actualStringValue, this, type, comboValues, priority ) );

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
