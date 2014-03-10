package org.uberfire.client.screens.property.temp;

import java.util.ArrayList;
import java.util.List;

import org.uberfire.shared.screens.property.api.PropertyEditorCategory;
import org.uberfire.shared.screens.property.api.PropertyEditorFieldInfo;
import org.uberfire.shared.screen.property.fields.PropertyEditorType;
import org.uberfire.shared.screen.property.fields.validators.AnotherValidator;
import org.uberfire.shared.screen.property.fields.validators.LoginValidator;

public class DummyProperty {

    public static List<PropertyEditorCategory> getProperty() {

        PropertyEditorCategory monitor = new PropertyEditorCategory( "Monitor", 0 )
                .withField( new PropertyEditorFieldInfo( "Name", "HTTPS", PropertyEditorType.TEXT ) )
                .withField( new PropertyEditorFieldInfo( "Show in Favorites", Boolean.FALSE.toString(), PropertyEditorType.BOOLEAN ) )
                .withField( new PropertyEditorFieldInfo( "Link to Summary Report", "http://redhat.com", PropertyEditorType.TEXT ) )
                .withField( new PropertyEditorFieldInfo( "Notes (to be show in reports)", "Created on XYZ", PropertyEditorType.TEXT ) );

        PropertyEditorCategory dependency = new PropertyEditorCategory( "Dependency and Polling Interval" )
                .withField( new PropertyEditorFieldInfo( "This monitor depends on availability of", "Ping on hpmfu.local", PropertyEditorType.TEXT ) )
                .withField( new PropertyEditorFieldInfo( "polling interval, seconds", "60", PropertyEditorType.INTEGER ) );

        PropertyEditorCategory monitorDefinition = new PropertyEditorCategory( "Monitor Definition", 1 )
                .withField( new PropertyEditorFieldInfo( "URL", "http://redhat.com", PropertyEditorType.TEXT ) )
                .withField( new PropertyEditorFieldInfo( "Port", "80", PropertyEditorType.INTEGER ) )
                .withField( new PropertyEditorFieldInfo( "Request Method", "POST", PropertyEditorType.COMBO ).withComboValues( createComboValues( "GET", "POST" ) ) )
                .withField( new PropertyEditorFieldInfo( "Accepted Response Codes", "200", PropertyEditorType.TEXT ) )
                .withField( new PropertyEditorFieldInfo( "Response Validation", "NONE", PropertyEditorType.COMBO ).withComboValues( createComboValues( "NONE", "YES" ) ) )
                .withField( new PropertyEditorFieldInfo( "String to Match", "", PropertyEditorType.TEXT ) )
                .withField( new PropertyEditorFieldInfo( "Login", "ederign", PropertyEditorType.TEXT ).withPriority( 0 ).withValidators( new LoginValidator(), new AnotherValidator() ) )
                .withField( new PropertyEditorFieldInfo( "Password", "123456", PropertyEditorType.SECRET_TEXT ).withPriority( 1 ) )
                .withField( new PropertyEditorFieldInfo( "Proxy Server", "No Proxy", PropertyEditorType.COMBO ).withComboValues( createComboValues( "No Proxy", "server 1", "server 2s" ) ) )
                .withField( new PropertyEditorFieldInfo( "Proxy URL", "", PropertyEditorType.TEXT ) );

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
