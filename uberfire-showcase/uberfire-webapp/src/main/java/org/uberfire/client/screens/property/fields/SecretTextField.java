package org.uberfire.client.screens.property.fields;

import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.client.screens.property.PropertyEditorFieldInfo;

public class SecretTextField extends AbstractField{

    SecretTextField(){};

    @Override
    public Widget widget( PropertyEditorFieldInfo property ) {
        PasswordTextBox passwordTextBox = new PasswordTextBox();
        passwordTextBox.setText( property.getActualStringValue() );
        return passwordTextBox;
    }
}
