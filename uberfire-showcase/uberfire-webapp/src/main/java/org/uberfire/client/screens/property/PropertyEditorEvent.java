package org.uberfire.client.screens.property;

import java.util.Map;

public class PropertyEditorEvent {

    private final String idEvent;
    private Map<String, PropertyEditorFieldInfo> propertyMap;

    public PropertyEditorEvent( String idEvent,
                                Map<String, PropertyEditorFieldInfo> propertyMap ) {
        this.idEvent = idEvent;
        this.propertyMap = propertyMap;
    }

    public Map<String, PropertyEditorFieldInfo> getPropertyMap() {
        return propertyMap;
    }

    public String getIdEvent() {
        return idEvent;
    }

}
