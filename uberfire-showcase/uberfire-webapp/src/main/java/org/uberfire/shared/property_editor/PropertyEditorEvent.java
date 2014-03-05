package org.uberfire.shared.property_editor;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class PropertyEditorEvent {

    public Object target;

    public PropertyEditorEvent(){};

    public PropertyEditorEvent( Object target ) {
        this.target = target;
    }

    public String helloWorld() {
        return "";
    }

}
