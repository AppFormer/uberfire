package org.uberfire.client.screens.property;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.uberfire.client.screens.property.fields.PropertyEditorType;

public class DummyPropertyMap {

    public static List<PropertyEditorCategory> getMap() {

        PropertyEditorCategory monitor = new PropertyEditorCategory( "Monitor" , 0);
        monitor.addField( "Name", "HTTPS", PropertyEditorType.TEXT );
        monitor.addField( "Show in Favorites", Boolean.FALSE.toString(), PropertyEditorType.BOOLEAN );
        monitor.addField( "Link to Summary Report", "http://redhat.com", PropertyEditorType.HTTP_LINK );
        monitor.addField( "Notes (to be show in reports)", "Created on XYZ", PropertyEditorType.TEXT );

        PropertyEditorCategory dependency = new PropertyEditorCategory( "Dependency and Polling Interval" );
        dependency.addField( "This monitor dependes on availability of", "Ping on hpmfu.local", PropertyEditorType.TEXT );
        dependency.addField( "polling interval, seconds", "60", PropertyEditorType.NUMBER );

        PropertyEditorCategory monitorDefinition = new PropertyEditorCategory( "Monitor Definition",1 );
        monitorDefinition.addField( "URL", "http://redhat.com", PropertyEditorType.HTTP_LINK );
        monitorDefinition.addField( "Port", "80", PropertyEditorType.NUMBER );
        monitorDefinition.addField( "Request Method", "POST", PropertyEditorType.COMBO, createComboValues( "GET", "POST" ) );
        monitorDefinition.addField( "Accepted Response Codes", "200", PropertyEditorType.TEXT );
        monitorDefinition.addField( "Response Validation", "NONE", PropertyEditorType.COMBO, createComboValues( "NONE", "YES" ) );
        monitorDefinition.addField( "String to Match", "", PropertyEditorType.TEXT );
        monitorDefinition.addField( "Login", "ederign", PropertyEditorType.TEXT, 0 );
        monitorDefinition.addField( "Password", "123456", PropertyEditorType.SECRET_TEXT, 1 );
        monitorDefinition.addField( "Proxy Server", "No Proxy", PropertyEditorType.COMBO, createComboValues( "No Proxy", "server 1", "server 2s" ) );
        monitorDefinition.addField( "Proxy URL", "", PropertyEditorType.HTTP_LINK );

        List<PropertyEditorCategory> properties = new ArrayList<PropertyEditorCategory>();
        properties.add( monitor );
        properties.add( dependency );
        properties.add( monitorDefinition );
        
        return properties;
    }

    private static List<String> createComboValues( String... values ) {
        List<String> comboValues = new ArrayList();
        for ( String value : values ) {
            comboValues.add( value );
        }
        return comboValues;
    }

}
