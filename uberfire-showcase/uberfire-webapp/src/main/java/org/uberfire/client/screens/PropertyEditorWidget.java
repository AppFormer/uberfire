package org.uberfire.client.screens;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.shared.screen.property.fields.PropertyEditorType;
import org.uberfire.shared.screens.property.api.PropertyEditorCategory;
import org.uberfire.shared.screens.property.api.PropertyEditorEvent;
import org.uberfire.shared.screens.property.api.PropertyEditorFieldInfo;

public class PropertyEditorWidget {

    public static FlowPanel create( PropertyEditorEvent event ) {

        FlowPanel accordion = createAccordion();

        for ( PropertyEditorCategory category : event.getProperties() ) {
            FlowPanel accordionGroup = createAccordionGroup( category );

            accordionGroup.add( createItemsTable( category ) );

            accordion.add( accordionGroup );

        }

        return accordion;
    }

    private static FlowPanel createItemsTable(
            PropertyEditorCategory category ) {
        FlowPanel collapse = createCollapse( category );

        for ( PropertyEditorFieldInfo field : category.getFields() ) {

            collapse.add( createCollapseInner( field ) );

        }
        return collapse;
    }

    private static FlowPanel createCollapse( PropertyEditorCategory category ) {
        FlowPanel collapse = new FlowPanel();
        collapse.getElement().addClassName( "accordion-body collapse" );
        collapse.getElement().setId( "collapse" + category.hashCode() );
        return collapse;
    }

    private static FlowPanel createCollapseInner( PropertyEditorFieldInfo field ) {
        FlowPanel collapseInner = new FlowPanel();
        collapseInner.getElement().addClassName( "row-fluid accordion-inner " );

        collapseInner.add( createFieldDescription( field ) );
        collapseInner.add( createFieldInput( field ) );
        return collapseInner;
    }

    private static FlowPanel createFieldInput( PropertyEditorFieldInfo field ) {
        FlowPanel col2 = new FlowPanel();
        col2.getElement().addClassName( "span6" );
        Widget fieldWidget = createFieldWidget( field );
        fieldWidget.getElement().setAttribute( "style", "margin-bottom:0px" );
        if ( field.getType().equals( PropertyEditorType.BOOLEAN ) ){
            fieldWidget.getElement().setAttribute("style","margin-top:5px"  );
        }
        col2.add( fieldWidget );
        return col2;
    }

    private static FlowPanel createFieldDescription( PropertyEditorFieldInfo field ) {
        FlowPanel col1 = new FlowPanel();
        col1.getElement().addClassName( "span6 " );
        Label label = new Label( field.getKey() );
        label.getElement().setAttribute("style","margin-top:5px"  );

        col1.add( label );
        return col1;
    }

    private static FlowPanel createAccordionHeading( PropertyEditorCategory category ) {
        FlowPanel accordionHeading = new FlowPanel();
        accordionHeading.getElement().addClassName( "accordion-heading" );
        HTML link = new HTML( "<a class=\"accordion-toggle\" data-toggle=\"collapse\" data-parent=\"#accordion2\" href=\"#collapse" + category.hashCode() + "\">\n" +
                                      "                " + category.getName() + "\n" +
                                      "            </a>" );

        accordionHeading.add( link );

        return accordionHeading;
    }

    private static FlowPanel createAccordionGroup( PropertyEditorCategory category ) {
        FlowPanel accordionGroup = new FlowPanel();
        accordionGroup.getElement().addClassName( "accordion-group" );
        FlowPanel accordionHeading = createAccordionHeading( category );

        accordionGroup.add( accordionHeading );
        return accordionGroup;
    }

    private static FlowPanel createAccordion() {
        FlowPanel accordion = new FlowPanel();
        accordion.getElement().addClassName( "accordion" );
        accordion.getElement().setId( "accordion2" );
        return accordion;
    }

    public static Widget createFieldWidget( PropertyEditorFieldInfo property ) {
        Widget fieldWidget = property.getType().widget( property );
        return fieldWidget;
    }

}
