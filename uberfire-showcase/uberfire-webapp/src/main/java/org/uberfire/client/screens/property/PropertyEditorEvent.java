package org.uberfire.client.screens.property;

import java.util.List;

public class PropertyEditorEvent {

    private final String idEvent;
    private  List<PropertyEditorCategory> properties;

    public PropertyEditorEvent( String idEvent,
                               List<PropertyEditorCategory> properties ) {
        this.idEvent = idEvent;
        this.properties = properties;
    }

    public  List<PropertyEditorCategory> getProperties() {
        return properties;
    }

    public String getIdEvent() {
        return idEvent;
    }

}
