package org.uberfire.client.screens.property;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.uberfire.client.screens.property.fields.PropertyEditorType;

public class DummyPropertyMap {

    public static Map<String, PropertyEditorFieldInfo> getMap() {

        Map<String, PropertyEditorFieldInfo> propertyMap = new HashMap<String, PropertyEditorFieldInfo>();
        propertyMap.put( "Name", new PropertyEditorFieldInfo("HTTPS", "Monitor", PropertyEditorType.TEXT ) );
        propertyMap.put( "Show in Favorites", new PropertyEditorFieldInfo(Boolean.FALSE.toString(), "Monitor", PropertyEditorType.BOOLEAN ) );
        propertyMap.put( "Link to Summary Report", new PropertyEditorFieldInfo("http://redhat.com", "Monitor", PropertyEditorType.HTTP_LINK ) );
        propertyMap.put( "Notes (to be show in reports)", new PropertyEditorFieldInfo("Created on XYZ", "Monitor", PropertyEditorType.TEXT ) );
        //ederign
        //propertyMap.put( "Regular Reporting", new PropertyEditorFieldInfo( "Monitor", PropertyEditorType.OBJECT ) );

        propertyMap.put( "This monitor dependes on availability of", new PropertyEditorFieldInfo("Ping on hpmfu.local", "Dependency and Polling Interval", PropertyEditorType.TEXT ) );
        propertyMap.put( "polling interval, seconds", new PropertyEditorFieldInfo("60", "Dependency and Polling Interval", PropertyEditorType.NUMBER ) );

        propertyMap.put( "URL", new PropertyEditorFieldInfo("http://redhat.com", "Monitor Definition", PropertyEditorType.HTTP_LINK ) );
        propertyMap.put( "Port", new PropertyEditorFieldInfo("80", "Monitor Definition", PropertyEditorType.NUMBER ) );
        propertyMap.put( "Request Method", new PropertyEditorFieldInfo("POST", "Monitor Definition", PropertyEditorType.COMBO, createComboValues("GET", "POST") ) );
       // propertyMap.put( "POST Data", new PropertyEditorFieldInfo("", "Monitor Definition", PropertyEditorType.TEXT ) );
        propertyMap.put( "Accepted Response Codes", new PropertyEditorFieldInfo("200", "Monitor Definition", PropertyEditorType.TEXT ) );
        propertyMap.put( "Response Validation", new PropertyEditorFieldInfo("NONE", "Monitor Definition", PropertyEditorType.COMBO, createComboValues("NONE","YES" ) ));
        propertyMap.put( "String to Match", new PropertyEditorFieldInfo("", "Monitor Definition", PropertyEditorType.TEXT ) );
        propertyMap.put( "Login", new PropertyEditorFieldInfo("ederign","Monitor Definition", PropertyEditorType.TEXT ) );
        propertyMap.put( "Password", new PropertyEditorFieldInfo("123456", "Monitor Definition", PropertyEditorType.SECRET_TEXT ) );
        propertyMap.put( "Proxy Server", new PropertyEditorFieldInfo("No Proxy", "Monitor Definition", PropertyEditorType.COMBO, createComboValues("No Proxy","server 1", "server 2s") ) );
        propertyMap.put( "Proxy URL", new PropertyEditorFieldInfo("", "Monitor Definition", PropertyEditorType.HTTP_LINK ) );

        //ederign object
        propertyMap.put( "Action Profile", new PropertyEditorFieldInfo("Action", "Action Profile (Alerting)", PropertyEditorType.TEXT ) );

        propertyMap.put( "Down State Timeout, seconds", new PropertyEditorFieldInfo("10", "Availability Monitoring", PropertyEditorType.NUMBER ) );
        propertyMap.put( "Number of Repetitions of Timeout for Down", new PropertyEditorFieldInfo("10", "Availability Monitoring", PropertyEditorType.NUMBER ) );
        propertyMap.put( "Extended Down Latency, seconds", new PropertyEditorFieldInfo("1200", "Availability Monitoring", PropertyEditorType.NUMBER ) );

        return propertyMap;
    }

    private static List<String> createComboValues( String... values ) {
        List<String> comboValues = new ArrayList();
        for ( String value : values ) {
            comboValues.add( value );
        }
        return comboValues;
    }

}
