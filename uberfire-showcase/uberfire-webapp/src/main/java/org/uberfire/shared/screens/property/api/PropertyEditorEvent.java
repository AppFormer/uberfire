package org.uberfire.shared.screens.property.api;

import java.util.ArrayList;
import java.util.List;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class PropertyEditorEvent {

    private String idEvent;
    private List<PropertyEditorCategory> properties = new ArrayList<PropertyEditorCategory>();

    public PropertyEditorEvent() {
    }

    ;

    public PropertyEditorEvent( String idEvent,
                                List<PropertyEditorCategory> properties ) {
        this.idEvent = idEvent;
        this.properties = properties;
    }

    public PropertyEditorEvent( String idEvent,
                                PropertyEditorCategory properties ) {
        this.idEvent = idEvent;
        this.properties.add( properties );
    }

    public List<PropertyEditorCategory> getProperties() {
        return properties;
    }

    public String getIdEvent() {
        return idEvent;
    }

}
