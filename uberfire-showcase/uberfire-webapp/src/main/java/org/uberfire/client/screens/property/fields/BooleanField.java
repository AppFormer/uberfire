package org.uberfire.client.screens.property.fields;

import com.github.gwtbootstrap.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.client.screens.property.PropertyEditorFieldInfo;

public class BooleanField extends AbstractField {

    @Override
    public Widget widget( PropertyEditorFieldInfo property ) {
        CheckBox checkBox = new CheckBox();
        checkBox.setValue( Boolean.valueOf( property.getActualStringValue() ) );
        return checkBox;
    }
}
