package org.uberfire.client.screens.property.fields;

import java.util.Collection;

import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.ioc.client.container.IOC;
import org.jboss.errai.ioc.client.container.IOCBeanDef;
import org.uberfire.client.screens.property.PropertyEditorFieldInfo;

public enum PropertyEditorType {

    BOOLEAN {
        @Override
        public Widget widget( PropertyEditorFieldInfo property ) {
            return getWidget( property, BooleanField.class );
        }
    }, HTTP_LINK {
        @Override
        public Widget widget( PropertyEditorFieldInfo property ) {
            return TEXT.widget( property );
        }
    }, OBJECT {
        @Override
        public Widget widget( PropertyEditorFieldInfo property ) {
            return null;
        }
    }, NUMBER {
        @Override
        public Widget widget( PropertyEditorFieldInfo property ) {
            return TEXT.widget( property );
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

}
