package org.uberfire.client.screens.property.editor;

import java.util.ArrayList;
import java.util.List;

import org.uberfire.properties.editor.temp.fields.PropertyEditorFieldType;
import org.uberfire.properties.editor.temp.fields.validators.PropertyFieldValidator;
import org.uberfire.properties.editor.temp.model.PropertyEditorCategory;
import org.uberfire.properties.editor.temp.model.PropertyEditorFieldInfo;

public class PropertyEditorClientScreenHelper {

    public static List<PropertyEditorCategory> createProperties() {

        int monitorPriority = 0;

        PropertyEditorCategory monitor = new PropertyEditorCategory( "Monitor", monitorPriority )
                .withField( new PropertyEditorFieldInfo( "Name", "HTTPS", PropertyEditorFieldType.TEXT ).withKey( "name" ) )
                .withField( new PropertyEditorFieldInfo( "Show in Favorites", Boolean.FALSE.toString(), PropertyEditorFieldType.BOOLEAN ).withValidators( new PropertyFieldValidator() {
                    @Override
                    public boolean validate( Object value ) {
                        return false;
                    }

                    @Override
                    public String getValidatorErrorMessage() {
                        return "error error error!!!!";
                    }
                } ) )
                .withField( new PropertyEditorFieldInfo( "Show in Favorites True", Boolean.FALSE.toString(), PropertyEditorFieldType.BOOLEAN ).withValidators( new PropertyFieldValidator() {
                    @Override
                    public boolean validate( Object value ) {
                        return true;
                    }

                    @Override
                    public String getValidatorErrorMessage() {
                        return "Property Change Forbidden (Validation)";
                    }
                } ) )
                .withField( new PropertyEditorFieldInfo( "Link to Summary Report", "http://redhat.com", PropertyEditorFieldType.TEXT ) )
                .withField( new PropertyEditorFieldInfo( "Notes (to be show in reports)", "Created on XYZ", PropertyEditorFieldType.TEXT ) );

        PropertyEditorCategory dependency = new PropertyEditorCategory( "Dependency and Polling Interval" )
                .withField( new PropertyEditorFieldInfo( "This monitor depends on availability of", "Ping on hpmfu.local", PropertyEditorFieldType.TEXT ) )
                .withField( new PropertyEditorFieldInfo( "polling interval, seconds", "60", PropertyEditorFieldType.NATURAL_NUMBER ) );

        int monitorDefinitionPriority = 1;
        PropertyEditorCategory monitorDefinition = new PropertyEditorCategory( "Monitor Definition", monitorDefinitionPriority )
                .withField( new PropertyEditorFieldInfo( "URL", "http://redhat.com", PropertyEditorFieldType.TEXT ) )
                .withField( new PropertyEditorFieldInfo( "Port", "80", PropertyEditorFieldType.NATURAL_NUMBER ) )
                .withField( new PropertyEditorFieldInfo( "Request Method", "POST", PropertyEditorFieldType.COMBO )
                                    .withComboValues( createComboValues( "GET", "POST" ) ) )
                .withField( new PropertyEditorFieldInfo( "Accepted Response Codes", "200", PropertyEditorFieldType.TEXT ) )
                .withField( new PropertyEditorFieldInfo( "Response Validation", "YES", PropertyEditorFieldType.COMBO )
                                    .withComboValues( createComboValues( "NONE", "YES" ) ).withValidators( new PropertyFieldValidator() {
                            @Override
                            public boolean validate( Object value ) {
                                return false;
                            }

                            @Override
                            public String getValidatorErrorMessage() {
                                return "Combo Validation Example";
                            }
                        } ) )

                .withField( new PropertyEditorFieldInfo( "Login", "ederign", PropertyEditorFieldType.TEXT )
                                    .withPriority( 0 ) )

                .withField( new PropertyEditorFieldInfo( "Password", "123456", PropertyEditorFieldType.SECRET_TEXT )
                                    .withPriority( 1 ).withValidators( new PropertyFieldValidator() {
                            @Override
                            public boolean validate( Object value ) {
                                return false;
                            }

                            @Override
                            public String getValidatorErrorMessage() {
                                return "You cant change your password";
                            }
                        } ) )
                .withField( new PropertyEditorFieldInfo( "Proxy Server", "No Proxy", PropertyEditorFieldType.COMBO )
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
