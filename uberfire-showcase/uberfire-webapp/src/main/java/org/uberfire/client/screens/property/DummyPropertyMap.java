package org.uberfire.client.screens.property;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.uberfire.client.screens.property.fields.PropertyEditorType;

public class DummyPropertyMap {

    public static Map<String, PropertyEditorFieldInfo> getMap() {

        Map<String, PropertyEditorFieldInfo> propertyMap = new HashMap<String, PropertyEditorFieldInfo>();
        propertyMap.put( "Name", new PropertyEditorFieldInfo("Name", "HTTPS", "Monitor", PropertyEditorType.TEXT ) );
        propertyMap.put( "Show in Favorites", new PropertyEditorFieldInfo("Show in Favorites", Boolean.FALSE.toString(), "Monitor", PropertyEditorType.BOOLEAN ) );
        propertyMap.put( "Link to Summary Report", new PropertyEditorFieldInfo("Link to Summary Report", "http://redhat.com", "Monitor", PropertyEditorType.HTTP_LINK ) );
        propertyMap.put( "Notes (to be show in reports)", new PropertyEditorFieldInfo("Notes (to be show in reports)","Created on XYZ", "Monitor", PropertyEditorType.TEXT ) );
        //ederign
        //propertyMap.put( "Regular Reporting", new PropertyEditorFieldInfo( "Monitor", PropertyEditorType.OBJECT ) );

        propertyMap.put( "This monitor dependes on availability of", new PropertyEditorFieldInfo("This monitor dependes on availability of","Ping on hpmfu.local", "Dependency and Polling Interval", PropertyEditorType.TEXT ) );
        propertyMap.put( "polling interval, seconds", new PropertyEditorFieldInfo("polling interval, seconds","60", "Dependency and Polling Interval", PropertyEditorType.NUMBER ) );

        propertyMap.put( "URL", new PropertyEditorFieldInfo("URL","http://redhat.com", "Monitor Definition", PropertyEditorType.HTTP_LINK ) );
        propertyMap.put( "Port", new PropertyEditorFieldInfo("Port","80", "Monitor Definition", PropertyEditorType.NUMBER ) );
        propertyMap.put( "Request Method", new PropertyEditorFieldInfo("Request Method","POST", "Monitor Definition", PropertyEditorType.COMBO, createComboValues("GET", "POST") ) );
       // propertyMap.put( "POST Data", new PropertyEditorFieldInfo("", "Monitor Definition", PropertyEditorType.TEXT ) );
        propertyMap.put( "Accepted Response Codes", new PropertyEditorFieldInfo("Accepted Response Codes","200", "Monitor Definition", PropertyEditorType.TEXT ) );
        propertyMap.put( "Response Validation", new PropertyEditorFieldInfo( "Response Validation","NONE", "Monitor Definition", PropertyEditorType.COMBO, createComboValues("NONE","YES" ) ));
        propertyMap.put( "String to Match", new PropertyEditorFieldInfo( "String to Match","", "Monitor Definition", PropertyEditorType.TEXT ) );
        propertyMap.put( "Login", new PropertyEditorFieldInfo("Login","ederign","Monitor Definition", PropertyEditorType.TEXT ) );
        propertyMap.put( "Password", new PropertyEditorFieldInfo("Password","123456", "Monitor Definition", PropertyEditorType.SECRET_TEXT ) );
        propertyMap.put( "Proxy Server", new PropertyEditorFieldInfo("Proxy Server","No Proxy", "Monitor Definition", PropertyEditorType.COMBO, createComboValues("No Proxy","server 1", "server 2s") ) );
        propertyMap.put( "Proxy URL", new PropertyEditorFieldInfo("Proxy URL","", "Monitor Definition", PropertyEditorType.HTTP_LINK ) );

        //ederign object
        propertyMap.put( "Action Profile", new PropertyEditorFieldInfo("Action Profile","Action", "Action Profile (Alerting)", PropertyEditorType.TEXT ) );

        propertyMap.put( "Down State Timeout, seconds", new PropertyEditorFieldInfo("Down State Timeout, seconds","10", "Availability Monitoring", PropertyEditorType.NUMBER ) );
        propertyMap.put( "Number of Repetitions of Timeout for Down", new PropertyEditorFieldInfo("Number of Repetitions of Timeout for Down","10", "Availability Monitoring", PropertyEditorType.NUMBER ) );
        propertyMap.put( "Extended Down Latency, seconds", new PropertyEditorFieldInfo("Extended Down Latency, seconds","1200", "Availability Monitoring", PropertyEditorType.NUMBER ) );

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
