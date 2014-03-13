package org.uberfire.client.propertyEditor.fields;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.client.propertyEditor.api.PropertyEditorCategory;
import org.uberfire.client.propertyEditor.api.PropertyEditorChangeEvent;
import org.uberfire.client.propertyEditor.api.PropertyEditorFieldInfo;

@Dependent
public class ComboField extends AbstractField {

    @Inject
    Event<PropertyEditorChangeEvent> propertyEditorChangeEventEvent;

    @Override
    public Widget widget( final PropertyEditorFieldInfo property ) {
        final ListBox listBox = GWT.create( ListBox.class );
        int index = 0;
        int selected = -1;
        for ( String value : property.getComboValues() ) {
            listBox.addItem( value );
            selected = searchSelectItem( property, index, selected, value );
            index++;
        }
        ifSelectedSelectItem( listBox, index, selected );

        addChangeHandler( property, listBox );

        return listBox;
    }

    private void ifSelectedSelectItem( ListBox listBox,
                                       int index,
                                       int selected ) {
        if ( selectAnyItem( index ) ) {
            listBox.setSelectedIndex( selected );
        }
    }

    private void addChangeHandler( final PropertyEditorFieldInfo property,
                                   final ListBox listBox ) {
        listBox.addChangeHandler( new ChangeHandler() {
            @Override
            public void onChange( ChangeEvent event ) {
                int selectedIndex = listBox.getSelectedIndex();
                propertyEditorChangeEventEvent.fire( new PropertyEditorChangeEvent( property, listBox.getItemText( selectedIndex ) ) );
            }
        }

                                );
    }

    private int searchSelectItem( PropertyEditorFieldInfo property,
                                  int index,
                                  int selected,
                                  String value ) {
        if ( value.equalsIgnoreCase( property.getCurrentStringValue() ) ) {
            selected = index;
        }
        return selected;
    }

    private boolean selectAnyItem( int index ) {
        return index >= 0;
    }

}
