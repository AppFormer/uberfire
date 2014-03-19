package org.uberfire.client.propertyEditor;

import com.github.gwtbootstrap.client.ui.Accordion;
import com.github.gwtbootstrap.client.ui.AccordionGroup;
import com.github.gwtbootstrap.client.ui.event.ShowEvent;
import com.github.gwtbootstrap.client.ui.event.ShowHandler;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.client.propertyEditor.api.PropertyEditorCategory;
import org.uberfire.client.propertyEditor.api.PropertyEditorEvent;
import org.uberfire.client.propertyEditor.api.PropertyEditorFieldInfo;
import org.uberfire.client.propertyEditor.fields.PropertyEditorType;
import org.uberfire.client.propertyEditor.widgets.AbstractPropertyEditorWidget;
import org.uberfire.client.propertyEditor.widgets.PropertyEditorErrorWidget;
import org.uberfire.client.propertyEditor.widgets.PropertyEditorItemLabel;
import org.uberfire.client.propertyEditor.widgets.PropertyEditorItemWidget;
import org.uberfire.client.propertyEditor.widgets.PropertyEditorItemsWidget;
import org.uberfire.client.propertyEditor.widgets.PropertyEditorTextBox;

public class PropertyEditorHelper {

    public static void extractEditorFrom( PropertyEditorWidget propertyEditorWidget,
                                          Accordion propertyMenu,
                                          PropertyEditorEvent event,
                                          String propertyNameFilter ) {
        propertyMenu.clear();
        for ( PropertyEditorCategory category : event.getSortedProperties() ) {
            createCategory( propertyEditorWidget, propertyMenu, category, propertyNameFilter );
        }
    }

    public static void extractEditorFrom( PropertyEditorWidget propertyEditorWidget,
                                          Accordion propertyMenu,
                                          PropertyEditorEvent event ) {
        extractEditorFrom( propertyEditorWidget, propertyMenu, event, "" );
    }

    private static void createCategory( final PropertyEditorWidget propertyEditorWidget,
                                        Accordion propertyMenu,
                                        final PropertyEditorCategory category,
                                        String propertyNameFilter ) {

        AccordionGroup categoryAccordion = createAccordionGroup( propertyEditorWidget, category );
        boolean categoryHasActiveChilds = false;
        for ( PropertyEditorFieldInfo field : category.getFields() ) {
            if ( isAMatchOfFilter( propertyNameFilter, field ) ) {
                categoryHasActiveChilds = true;
                categoryAccordion.add( createItemsWidget( field ) );
            }

        }
        if ( categoryHasActiveChilds ) {
            propertyMenu.add( categoryAccordion );
        }
    }

    private static AccordionGroup createAccordionGroup( final PropertyEditorWidget propertyEditorWidget,
                                                        final PropertyEditorCategory category ) {
        AccordionGroup categoryAccordion = GWT.create( AccordionGroup.class );
        categoryAccordion.setHeading( category.getName() );
        categoryAccordion.addShowHandler( new ShowHandler() {
            @Override
            public void onShow( ShowEvent showEvent ) {
                propertyEditorWidget.setLastOpenAccordionGroupTitle( category.getName() );
            }
        } );
        if ( propertyEditorWidget.getLastOpenAccordionGroupTitle().equals( category.getName() ) ) {
            categoryAccordion.setDefaultOpen( true );
        }
        return categoryAccordion;
    }

    private static boolean isAMatchOfFilter( String propertyNameFilter,
                                             PropertyEditorFieldInfo field ) {
        if ( propertyNameFilter.isEmpty() ) {
            return true;
        }
        if ( field.getLabel().toUpperCase().contains( propertyNameFilter.toUpperCase() ) ) {
            return true;
        }
        return false;
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

        createErrorHandlingInfraStructure( parent, itemWidget, errorWidget, widget );
        itemWidget.add( widget );
        itemWidget.add( errorWidget );

        return itemWidget;
    }

    private static void createErrorHandlingInfraStructure( PropertyEditorItemsWidget parent,
                                                           PropertyEditorItemWidget itemWidget,
                                                           PropertyEditorErrorWidget errorWidget,
                                                           Widget widget ) {
        AbstractPropertyEditorWidget abstractPropertyEditorWidget = (AbstractPropertyEditorWidget) widget;
        abstractPropertyEditorWidget.setErrorWidget( errorWidget );
        abstractPropertyEditorWidget.setParent( parent );
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
