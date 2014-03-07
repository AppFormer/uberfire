package org.uberfire.client.screens.property;

import java.util.ArrayList;
import java.util.List;

import org.uberfire.client.screens.property.fields.PropertyEditorType;
import org.uberfire.client.screens.property.fields.validators.LoginValidator;

public class DummyPropertyMap {

    public static List<PropertyEditorCategory> getMap() {

        PropertyEditorCategory monitor = new PropertyEditorCategory( "Monitor", 0 )
                .withFields( new PropertyEditorFieldInfo( "Name", "HTTPS", PropertyEditorType.TEXT ) )
                .withFields( new PropertyEditorFieldInfo( "Show in Favorites", Boolean.FALSE.toString(), PropertyEditorType.BOOLEAN ) )
                .withFields( new PropertyEditorFieldInfo( "Link to Summary Report", "http://redhat.com", PropertyEditorType.TEXT ) )
                .withFields( new PropertyEditorFieldInfo( "Notes (to be show in reports)", "Created on XYZ", PropertyEditorType.TEXT ) );

        PropertyEditorCategory dependency = new PropertyEditorCategory( "Dependency and Polling Interval" )
                .withFields( new PropertyEditorFieldInfo( "This monitor dependes on availability of", "Ping on hpmfu.local", PropertyEditorType.TEXT ) )
                .withFields( new PropertyEditorFieldInfo( "polling interval, seconds", "60", PropertyEditorType.INTEGER ) );

        PropertyEditorCategory monitorDefinition = new PropertyEditorCategory( "Monitor Definition", 1 )
                .withFields( new PropertyEditorFieldInfo( "URL", "http://redhat.com", PropertyEditorType.TEXT ) )
                .withFields( new PropertyEditorFieldInfo( "Port", "80", PropertyEditorType.INTEGER ) )
                .withFields( new PropertyEditorFieldInfo( "Request Method", "POST", PropertyEditorType.COMBO ).withComboValues( createComboValues( "GET", "POST" ) ) )
                .withFields( new PropertyEditorFieldInfo( "Accepted Response Codes", "200", PropertyEditorType.TEXT ) )
                .withFields( new PropertyEditorFieldInfo( "Response Validation", "NONE", PropertyEditorType.COMBO ).withComboValues( createComboValues( "NONE", "YES" ) ) )
                .withFields( new PropertyEditorFieldInfo( "String to Match", "", PropertyEditorType.TEXT ) )
                .withFields( new PropertyEditorFieldInfo( "Login", "ederign", PropertyEditorType.TEXT ).withPriority( 0 ).withValidators(new LoginValidator()) )
                .withFields( new PropertyEditorFieldInfo( "Password", "123456", PropertyEditorType.SECRET_TEXT ).withPriority( 1 ) )
                .withFields( new PropertyEditorFieldInfo( "Proxy Server", "No Proxy", PropertyEditorType.COMBO ).withComboValues( createComboValues( "No Proxy", "server 1", "server 2s" ) ) )
                .withFields( new PropertyEditorFieldInfo( "Proxy URL", "", PropertyEditorType.TEXT ) );

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
