package org.uberfire.client.propertyEditor;

import com.github.gwtbootstrap.client.ui.Accordion;
import com.github.gwtbootstrap.client.ui.AccordionGroup;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.client.propertyEditor.api.PropertyEditorCategory;
import org.uberfire.client.propertyEditor.api.PropertyEditorEvent;
import org.uberfire.client.propertyEditor.api.PropertyEditorFieldInfo;
import org.uberfire.client.propertyEditor.fields.PropertyEditorType;
import org.uberfire.client.propertyEditor.widgets.PropertyEditorErrorWidget;
import org.uberfire.client.propertyEditor.widgets.PropertyEditorItemLabel;
import org.uberfire.client.propertyEditor.widgets.PropertyEditorItemWidget;
import org.uberfire.client.propertyEditor.widgets.PropertyEditorItemsWidget;
import org.uberfire.client.propertyEditor.widgets.PropertyEditorTextBox;

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
        items.add( createField( field, items ) );
        return items;
    }

    private static PropertyEditorItemWidget createField( PropertyEditorFieldInfo field,
                                                         PropertyEditorItemsWidget parent ) {
        PropertyEditorItemWidget itemWidget = GWT.create( PropertyEditorItemWidget.class );
        PropertyEditorErrorWidget errorWidget = GWT.create( PropertyEditorErrorWidget.class );
        Widget widget = field.getType().widget( field );

        if(field.getType().equals( PropertyEditorType.TEXT )){
            createErrorHandlingInfraStructure( parent, itemWidget, errorWidget, widget );
            itemWidget.add( errorWidget );
        }
        else {
            itemWidget.add( widget );
        }
        return itemWidget;
    }

    private static void createErrorHandlingInfraStructure( PropertyEditorItemsWidget parent,
                                                           PropertyEditorItemWidget itemWidget,
                                                           PropertyEditorErrorWidget errorWidget,
                                                           Widget widget ) {
        PropertyEditorTextBox textbox = (PropertyEditorTextBox) widget;
        textbox.setErrorWidget( errorWidget );
        textbox.setParent( parent );
        itemWidget.add( widget );
    }

    private static PropertyEditorItemLabel createLabel( PropertyEditorFieldInfo field ) {
        PropertyEditorItemLabel item = GWT.create( PropertyEditorItemLabel.class );
        item.setText( field.getLabel() );
        return item;
    }

    public static boolean validade( PropertyEditorEvent event ) {
        if ( event == null ) {
            throw new NullEventException();
        }
        if ( event.getSortedProperties().isEmpty() ) {
            throw new NoPropertiesException();
        }

        return event != null && !event.getSortedProperties().isEmpty();
    }

    private static class NullEventException extends RuntimeException {

    }

    private static class NoPropertiesException extends RuntimeException {

    }
}
