package org.uberfire.properties.editor.temp.fields;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.ioc.client.container.IOC;
import org.jboss.errai.ioc.client.container.IOCBeanDef;
import org.uberfire.properties.editor.temp.fields.validators.LongValidator;
import org.uberfire.properties.editor.temp.fields.validators.PropertyFieldValidator;
import org.uberfire.properties.editor.temp.fields.validators.TextValidator;
import org.uberfire.properties.editor.temp.model.PropertyEditorFieldInfo;

public enum PropertyEditorFieldType {

    TEXT {
        @Override
        public Widget widget( PropertyEditorFieldInfo property ) {
            return getWidget( property, TextField.class );
        }

        @Override
        public boolean isType( Class<?> type ) {
            return isString( type )||isFloat( type )||isDouble( type );
        }

        private boolean isFloat( Class<?> type ) {
            return ( type.equals( Float.class ) || ( type.toString().equalsIgnoreCase( "float" ) ) );
        }
        private boolean isDouble( Class<?> type ) {
            return ( type.equals( Double.class ) || ( type.toString().equalsIgnoreCase( "double" ) ) );
        }
        private boolean isString( Class<?> type ) {
            return type.equals( String.class );
        }

        @Override
        public List<PropertyFieldValidator> getValidators() {
            ArrayList validators = new ArrayList();
            validators.add( new TextValidator() );
            return validators;
        }

    }, BOOLEAN {
        @Override
        public Widget widget( PropertyEditorFieldInfo property ) {
            return getWidget( property, BooleanField.class );
        }

        @Override
        public boolean isType( Class<?> type ) {
            return ( type.equals( Boolean.class ) || ( type.toString().equalsIgnoreCase( "boolean" ) ) );
        }
    }, NATURAL_NUMBER {
        @Override
        public Widget widget( PropertyEditorFieldInfo property ) {
            return TEXT.widget( property );
        }

        @Override
        public List<PropertyFieldValidator> getValidators() {
            ArrayList validators = new ArrayList();
            validators.add( new LongValidator() );
            return validators;
        }

        @Override
        public boolean isType( Class<?> type ) {
            return isInteger( type ) || isLong( type ) || isShort( type );
        }

        private boolean isShort( Class<?> type ) {
            return ( type.equals( Short.class ) || ( type.toString().equalsIgnoreCase( "short" ) ) );
        }

        private boolean isLong( Class<?> type ) {
            return ( type.equals( Long.class ) || ( type.toString().equalsIgnoreCase( "long" ) ) );
        }

        private boolean isInteger( Class<?> type ) {
            return ( type.equals( Integer.class ) || ( type.toString().equalsIgnoreCase( "int" ) ) );
        }
    }, COMBO {
        @Override
        public Widget widget( PropertyEditorFieldInfo property ) {
            return getWidget( property, ComboField.class );
        }

        @Override
        public boolean isType( Class<?> type ) {
            return type.isEnum();
        }
    }, SECRET_TEXT {
        @Override
        public Widget widget( PropertyEditorFieldInfo property ) {
            return getWidget( property, SecretTextField.class );
        }
    };

    private static Widget getWidget( PropertyEditorFieldInfo property,
                                     Class fieldtype ) {
        IOCBeanDef iocBeanDef = IOC.getBeanManager().lookupBean( fieldtype );
        AbstractField field = (AbstractField) iocBeanDef.getInstance();
        return field.widget( property );
    }

    public abstract Widget widget( PropertyEditorFieldInfo property );

    public boolean isType( Class<?> type ) {
        return false;
    }

    public List<PropertyFieldValidator> getValidators() {
        return new ArrayList();
    }

    public static PropertyEditorFieldType getFromType( Class<?> type ) {
        for ( PropertyEditorFieldType candidate : PropertyEditorFieldType.values() ) {
            if ( candidate.isType( type ) ) {
                return candidate;
            }
        }
        return null;
    }


}
