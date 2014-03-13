package org.uberfire.client.propertyEditor.fields;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.ioc.client.container.IOC;
import org.jboss.errai.ioc.client.container.IOCBeanDef;
import org.uberfire.client.propertyEditor.api.PropertyEditorCategory;
import org.uberfire.client.propertyEditor.api.PropertyEditorFieldInfo;
import org.uberfire.client.propertyEditor.fields.validators.IntegerValidator;
import org.uberfire.client.propertyEditor.fields.validators.PropertyFieldValidator;

public enum PropertyEditorType {

    BOOLEAN {
        @Override
        public Widget widget( PropertyEditorFieldInfo property ) {
            return getWidget( property, BooleanField.class );
        }
    }, INTEGER {
        @Override
        public Widget widget( PropertyEditorFieldInfo property ) {
            return TEXT.widget( property );
        }

        @Override
        public List<PropertyFieldValidator> getValidators() {
            ArrayList validators = new ArrayList();
            validators.add( new IntegerValidator() );
            return validators;
        }
    }, COMBO {
        @Override
        public Widget widget( PropertyEditorFieldInfo property ) {
            return getWidget( property, ComboField.class );
        }
    }, SECRET_TEXT {
        @Override
        public Widget widget( PropertyEditorFieldInfo property ) {
            return getWidget( property, SecretTextField.class );
        }
    }, TEXT {
        @Override
        public Widget widget( PropertyEditorFieldInfo property ) {
            return getWidget( property, TextField.class );
        }
    };

    private static Widget getWidget( PropertyEditorFieldInfo property,
                                     Class fieldtype ) {
        IOCBeanDef iocBeanDef = IOC.getBeanManager().lookupBean( fieldtype );
        AbstractField field = (AbstractField) iocBeanDef.getInstance();
        return field.widget( property );
    }

    public abstract Widget widget( PropertyEditorFieldInfo property );

    public List<PropertyFieldValidator> getValidators() {
        return new ArrayList();
    }

}
