package org.uberfire.client.propertyEditor;

import javax.enterprise.context.Dependent;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.client.propertyEditor.api.PropertyEditorCategory;
import org.uberfire.client.propertyEditor.api.PropertyEditorEvent;
import org.uberfire.client.propertyEditor.api.PropertyEditorFieldInfo;
import org.uberfire.client.propertyEditor.fields.PropertyEditorType;

@Dependent
public class PropertyEditorWidget extends FlowPanel {

    public PropertyEditorWidget() {
    }

    public void handle( PropertyEditorEvent event ) {
        this.clear();
        this.add( create( event ) );
    }

    private FlowPanel create( PropertyEditorEvent event ) {

        FlowPanel accordion = createAccordion();

        for ( PropertyEditorCategory category : event.getProperties() ) {
            FlowPanel accordionGroup = createAccordionGroup( category );

            accordionGroup.add( createItemsTable( category ) );

            accordion.add( accordionGroup );

        }

        return accordion;
    }

    private FlowPanel createItemsTable(
            PropertyEditorCategory category ) {
        FlowPanel collapse = createCollapse( category );

        for ( PropertyEditorFieldInfo field : category.getFields() ) {

            collapse.add( createCollapseInner( field ) );

        }
        return collapse;
    }

    private FlowPanel createCollapse( PropertyEditorCategory category ) {
        FlowPanel collapse = new FlowPanel();
        collapse.getElement().addClassName( "accordion-body collapse" );
        collapse.getElement().setId( "collapse" + category.hashCode() );
        return collapse;
    }

    private FlowPanel createCollapseInner( PropertyEditorFieldInfo field ) {
        FlowPanel collapseInner = new FlowPanel();
        collapseInner.getElement().addClassName( "row-fluid accordion-inner " );

        collapseInner.add( createFieldDescription( field ) );
        collapseInner.add( createFieldInput( field ) );
        return collapseInner;
    }

    private FlowPanel createFieldInput( PropertyEditorFieldInfo field ) {
        FlowPanel col2 = new FlowPanel();
        col2.getElement().addClassName( "span6" );
        Widget fieldWidget = createFieldWidget( field );
        String style = fieldWidget.getElement().getAttribute( "style" );
        style = style + "margin-bottom:0px ";
        fieldWidget.getElement().setAttribute( "style", style );
        if ( field.getType().equals( PropertyEditorType.BOOLEAN ) ) {
            fieldWidget.getElement().setAttribute( "style", "margin-top:5px" );
        }
        col2.add( fieldWidget );
        return col2;
    }

    private FlowPanel createFieldDescription( PropertyEditorFieldInfo field ) {
        FlowPanel col1 = new FlowPanel();
        col1.getElement().addClassName( "span6 " );
        Label label = new Label( field.getKey() );
        label.getElement().setAttribute( "style", "margin-top:5px" );

        col1.add( label );
        return col1;
    }

    private FlowPanel createAccordionHeading( PropertyEditorCategory category ) {
        FlowPanel accordionHeading = new FlowPanel();
        accordionHeading.getElement().addClassName( "accordion-heading" );
        HTML link = new HTML( "<a class=\"accordion-toggle\" data-toggle=\"collapse\" data-parent=\"#accordion2\" href=\"#collapse" + category.hashCode() + "\">\n" +
                                      "                " + category.getName() + "\n" +
                                      "            </a>" );

        accordionHeading.add( link );

        return accordionHeading;
    }

    private FlowPanel createAccordionGroup( PropertyEditorCategory category ) {
        FlowPanel accordionGroup = new FlowPanel();
        accordionGroup.getElement().addClassName( "accordion-group" );
        FlowPanel accordionHeading = createAccordionHeading( category );

        accordionGroup.add( accordionHeading );
        return accordionGroup;
    }

    private FlowPanel createAccordion() {
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
