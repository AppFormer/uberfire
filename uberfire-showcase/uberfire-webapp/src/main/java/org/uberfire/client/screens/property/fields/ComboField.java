package org.uberfire.client.screens.property.fields;

import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.client.screens.property.PropertyEditorFieldInfo;

public class ComboField extends AbstractField{

    ComboField(){};

    @Override
    public Widget widget( PropertyEditorFieldInfo property ) {
        ListBox listBox = new ListBox();
        int index = 0;
        int selected = -1;
        for ( String value : property.getComboValues() ) {
            listBox.addItem( value );
            if ( value.equalsIgnoreCase( property.getActualStringValue() ) ) {
                selected = index;
            }
            index++;
        }
        if ( selectAnyItem( index ) ) {
            listBox.setSelectedIndex( selected );
        }
        return listBox;
    }

    private boolean selectAnyItem( int index ) {
        return index >= 0;
    }

}
