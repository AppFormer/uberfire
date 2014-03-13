package org.uberfire.client.propertyEditor;

import com.github.gwtbootstrap.client.ui.Accordion;
import com.github.gwtbootstrap.client.ui.AccordionGroup;
import com.google.gwt.core.client.GWT;
import org.uberfire.client.propertyEditor.api.PropertyEditorCategory;
import org.uberfire.client.propertyEditor.api.PropertyEditorEvent;
import org.uberfire.client.propertyEditor.api.PropertyEditorFieldInfo;
import org.uberfire.client.propertyEditor.widgets.PropertyEditorItemLabel;
import org.uberfire.client.propertyEditor.widgets.PropertyEditorItemWidget;
import org.uberfire.client.propertyEditor.widgets.PropertyEditorItemsWidget;

public class PropertyEditorHelper {

    public static void extractEditorFrom( Accordion propertyMenu,
                                          PropertyEditorEvent event ) {
        propertyMenu.clear();

        for ( PropertyEditorCategory category : event.getSortedProperties() ) {
            createCategory( propertyMenu, category );
        }
    }

    private static void createCategory( Accordion propertyMenu,
                                        PropertyEditorCategory category ) {

        AccordionGroup categoryAccordion = GWT.create( AccordionGroup.class );
        categoryAccordion.setHeading( category.getName() );

        for ( PropertyEditorFieldInfo field : category.getFields() ) {
            categoryAccordion.add( createItemsWidget( field ) );

        }
        propertyMenu.add( categoryAccordion );
    }

    private static PropertyEditorItemsWidget createItemsWidget( PropertyEditorFieldInfo field ) {
        PropertyEditorItemsWidget items = GWT.create( PropertyEditorItemsWidget.class );

        items.add( createLabel( field ) );
        items.add( createField( field ) );
        
        return items;
    }

    private static PropertyEditorItemWidget createField( PropertyEditorFieldInfo field ) {
        PropertyEditorItemWidget item2 = GWT.create( PropertyEditorItemWidget.class );
        item2.add(  field.getType().widget( field ) ) ;
        return item2;
    }

    private static PropertyEditorItemLabel createLabel( PropertyEditorFieldInfo field ) {
        PropertyEditorItemLabel item = GWT.create( PropertyEditorItemLabel.class );
        item.setText( field.getKey() );
        return item;
    }

    public static boolean validade( PropertyEditorEvent event ) {
        return event != null && !event.getSortedProperties().isEmpty();
    }
}
