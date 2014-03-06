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
            return new BooleanField().widget( property );
        }
    }, HTTP_LINK {
        @Override
        public Widget widget( PropertyEditorFieldInfo property ) {
            return new TextField().widget( property );
        }
    }, OBJECT {
        @Override
        public Widget widget( PropertyEditorFieldInfo property ) {
            return null;
        }
    }, NUMBER {
        @Override
        public Widget widget( PropertyEditorFieldInfo property ) {
            return new TextField().widget( property );
        }
    }, COMBO {
        @Override
        public Widget widget( PropertyEditorFieldInfo property ) {
            return new ComboField().widget( property );
        }
    }, SECRET_TEXT {
        @Override
        public Widget widget( PropertyEditorFieldInfo property ) {
            return new SecretTextField().widget( property );
        }
    }, TEXT {
        @Override
        public Widget widget( PropertyEditorFieldInfo property ) {
            //ederign
            Collection<IOCBeanDef> iocBeanDefs = IOC.getBeanManager().lookupBeans( "org.uberfire.client.screens.property.fields.TextField" );
            IOCBeanDef iocBeanDef= iocBeanDefs.iterator().next();
            TextField textField = (TextField) iocBeanDef.getInstance();
            return textField.widget( property );
        }
    };

    public abstract Widget widget( PropertyEditorFieldInfo property );

}
