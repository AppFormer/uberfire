package org.uberfire.client.screens.welcome;

import java.util.ArrayList;
import java.util.List;

import org.uberfire.client.propertyEditor.api.PropertyEditorCategory;
import org.uberfire.client.propertyEditor.api.PropertyEditorFieldInfo;
import org.uberfire.client.propertyEditor.fields.PropertyEditorType;

public class WelcomeScreenHelper {

    public static List<PropertyEditorCategory> createProperties() {

        int monitorPriority = 0;

        PropertyEditorCategory monitor = new PropertyEditorCategory( "Monitor", monitorPriority )
                .withField( new PropertyEditorFieldInfo( "Name", "HTTPS", PropertyEditorType.TEXT ).withKey( "name" ) )
                .withField( new PropertyEditorFieldInfo( "Show in Favorites", Boolean.FALSE.toString(), PropertyEditorType.BOOLEAN ) )
                .withField( new PropertyEditorFieldInfo( "Link to Summary Report", "http://redhat.com", PropertyEditorType.TEXT ) )
                .withField( new PropertyEditorFieldInfo( "Notes (to be show in reports)", "Created on XYZ", PropertyEditorType.TEXT ) );

        PropertyEditorCategory dependency = new PropertyEditorCategory( "Dependency and Polling Interval" )
                .withField( new PropertyEditorFieldInfo( "This monitor depends on availability of", "Ping on hpmfu.local", PropertyEditorType.TEXT ) )
                .withField( new PropertyEditorFieldInfo( "polling interval, seconds", "60", PropertyEditorType.NATURAL_NUMBER ) );

        int monitorDefinitionPriority = 1;
        PropertyEditorCategory monitorDefinition = new PropertyEditorCategory( "Monitor Definition", monitorDefinitionPriority )
                .withField( new PropertyEditorFieldInfo( "URL", "http://redhat.com", PropertyEditorType.TEXT ) )
                .withField( new PropertyEditorFieldInfo( "Port", "80", PropertyEditorType.NATURAL_NUMBER ) )
                .withField( new PropertyEditorFieldInfo( "Request Method", "POST", PropertyEditorType.COMBO )
                                    .withComboValues( createComboValues( "GET", "POST" ) ) )
                .withField( new PropertyEditorFieldInfo( "Accepted Response Codes", "200", PropertyEditorType.TEXT ) )
                .withField( new PropertyEditorFieldInfo( "Response Validation", "NONE", PropertyEditorType.COMBO )
                                    .withComboValues( createComboValues( "NONE", "YES" ) ) )

//                .withField( new PropertyEditorFieldInfo( "Login", "ederign", PropertyEditorType.TEXT )
//                                    .withPriority( 0 ).withValidators( new LoginValidator(), new AnotherSampleValidator() ) )

                .withField( new PropertyEditorFieldInfo( "Password", "123456", PropertyEditorType.SECRET_TEXT )
                                    .withPriority( 1 ) )
                .withField( new PropertyEditorFieldInfo( "Proxy Server", "No Proxy", PropertyEditorType.COMBO )
                                    .withComboValues( createComboValues( "No Proxy", "server 1", "server 2s" ) ) );





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
