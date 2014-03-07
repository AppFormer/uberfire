package org.uberfire.client.screens.property.fields;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.client.screens.property.PropertyEditorChangeEvent;
import org.uberfire.client.screens.property.PropertyEditorFieldInfo;

@Dependent
public class ComboField extends AbstractField {

    @Inject
    Event<PropertyEditorChangeEvent> propertyEditorChangeEventEvent;

    public ComboField() {
    }

    @Override
    public Widget widget( final PropertyEditorFieldInfo property ) {
        final ListBox listBox = new ListBox();
        int index = 0;
        int selected = -1;
        for ( String value : property.getComboValues() ) {
            listBox.addItem( value );
            selected = searchSelectItem( property, index, selected, value );
            index++;
        }
        if ( selectAnyItem( index ) ) {
            listBox.setSelectedIndex( selected );
        }
        listBox.addChangeHandler( new ChangeHandler() {
            @Override
            public void onChange( ChangeEvent event ) {
                int selectedIndex = listBox.getSelectedIndex();
                propertyEditorChangeEventEvent.fire( new PropertyEditorChangeEvent( property, listBox.getItemText( selectedIndex ) ) );
            }
        }

        );

        return listBox;
    }

    private int searchSelectItem( PropertyEditorFieldInfo property,
                                  int index,
                                  int selected,
                                  String value ) {
        if ( value.equalsIgnoreCase( property.getActualStringValue() ) ) {
            selected = index;
        }
        return selected;
    }

    private boolean selectAnyItem( int index ) {
        return index >= 0;
    }

}
