package org.uberfire.client.propertyEditor;

import java.util.List;
import javax.enterprise.context.Dependent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.client.propertyEditor.api.PropertyEditorCategory;
import org.uberfire.client.propertyEditor.api.PropertyEditorEvent;
import org.uberfire.client.propertyEditor.api.PropertyEditorFieldInfo;
import org.uberfire.client.propertyEditor.fields.PropertyEditorType;

@Dependent
public class PropertyEditorWidget extends FlowPanel {

    public static final String ACCORDION_ID = "accordion";
    public static final String COLLAPSE_CSS_CLASS_NAME = "accordion-body collapse";
    public static final String COLLAPSE_INNER_CSS_CLASS_NAME = "row-fluid accordion-inner";
    public static final String BOOTSTRAP_6_COLUMNS_CSS_CLASS = "span6";
    public static final String STYLE = "style";
    public static final String ACCORDION_HEADING_CSS_CLASSNAME = "accordion-heading";
    public static final String ACCORDION_GROUP_CSS_CLASSNAME = "accordion-group";
    public static final String ACCORDION_CSS_CLASSNAME = "accordion";
    public static final String DEFAULT_MARGING = "margin-top:5px";

    public PropertyEditorWidget() {
    }

    public void handle( PropertyEditorEvent event ) throws PropertyEditorException {
        this.clear();
        if ( validade( event ) ) {
            this.add( createWidget( event ) );
        } else {
            throw new PropertyEditorException( "Invalid event data." );
        }

    }

    private boolean validade( PropertyEditorEvent event ) {
        return event != null && !event.getSortedProperties().isEmpty();
    }

    private FlowPanel createWidget( PropertyEditorEvent event ) {

        FlowPanel accordion = createAccordion();

        createChildsOf( accordion, event.getSortedProperties() );

        return accordion;
    }

    private void createChildsOf(
            FlowPanel accordion,
            List<PropertyEditorCategory> childs ) {

        for ( PropertyEditorCategory category : childs ) {
            FlowPanel accordionGroup = createAccordionGroup( category );

            accordionGroup.add( createPropertiesTable( category ) );

            accordion.add( accordionGroup );

        }
    }

    private FlowPanel createPropertiesTable(
            PropertyEditorCategory category ) {
        FlowPanel collapse = createCollapse( category );

        for ( PropertyEditorFieldInfo field : category.getFields() ) {

            collapse.add( createCollapseInner( field ) );

        }
        return collapse;
    }

    private FlowPanel createCollapse( PropertyEditorCategory category ) {
        FlowPanel collapse = GWT.create( FlowPanel.class );
        collapse.getElement().addClassName( COLLAPSE_CSS_CLASS_NAME );
        collapse.getElement().setId( String.valueOf( category.hashCode() ) );
        return collapse;
    }

    private FlowPanel createCollapseInner( PropertyEditorFieldInfo field ) {
        FlowPanel collapseInner = GWT.create( FlowPanel.class );
        collapseInner.getElement().addClassName( COLLAPSE_INNER_CSS_CLASS_NAME );

        createCollapseInnerData( field, collapseInner );

        return collapseInner;
    }

    private void createCollapseInnerData( PropertyEditorFieldInfo field,
                                          FlowPanel collapseInner ) {
        collapseInner.add( createFieldDescription( field ) );
        collapseInner.add( createFieldInput( field ) );
    }

    private FlowPanel createFieldInput( PropertyEditorFieldInfo field ) {
        FlowPanel col2 = GWT.create( FlowPanel.class );
        col2.getElement().addClassName( BOOTSTRAP_6_COLUMNS_CSS_CLASS );
        Widget fieldWidget = createFieldWidget( field );
        col2.add( fieldWidget );
        return col2;
    }

    private FlowPanel createFieldDescription( PropertyEditorFieldInfo field ) {
        FlowPanel col1 = GWT.create( FlowPanel.class );
        col1.getElement().addClassName( BOOTSTRAP_6_COLUMNS_CSS_CLASS );
        col1.add( createLabel( field ) );
        return col1;
    }

    Label createLabel( PropertyEditorFieldInfo field ) {
        Label label = GWT.create( Label.class );
        label.setText( field.getKey() );
        label.getElement().setAttribute( STYLE, DEFAULT_MARGING );
        return label;
    }

    private FlowPanel createAccordionHeading( PropertyEditorCategory category ) {
        FlowPanel accordionHeading = GWT.create( FlowPanel.class );
        accordionHeading.getElement().addClassName( ACCORDION_HEADING_CSS_CLASSNAME );
        HTML link = createHTML( category );

        accordionHeading.add( link );

        return accordionHeading;
    }

    private HTML createHTML( PropertyEditorCategory category ) {
        HTML html = GWT.create( HTML.class );
        html.setHTML( createHTTPLinkHTML( category ) );
        return html;
    }

    private String createHTTPLinkHTML( PropertyEditorCategory category ) {
        return "<a class=\"accordion-toggle\" data-toggle=\"collapse\" data-parent=\"#" + ACCORDION_ID +
                "\" href=\"#" + category.hashCode() + "\">\n" +
                category.getName() + "\n </a>";
    }

    private FlowPanel createAccordionGroup( PropertyEditorCategory category ) {
        FlowPanel accordionGroup = GWT.create( FlowPanel.class );
        accordionGroup.getElement().addClassName( ACCORDION_GROUP_CSS_CLASSNAME );
        FlowPanel accordionHeading = createAccordionHeading( category );

        accordionGroup.add( accordionHeading );
        return accordionGroup;
    }

    private FlowPanel createAccordion() {
        FlowPanel accordion = GWT.create( FlowPanel.class );
        accordion.getElement().addClassName( ACCORDION_CSS_CLASSNAME );
        accordion.getElement().setId( ACCORDION_ID );
        return accordion;
    }

    public static Widget createFieldWidget( PropertyEditorFieldInfo property ) {
        Widget fieldWidget = property.getType().widget( property );
        fieldWidget.getElement().setAttribute( STYLE, createFieldStyle( property, fieldWidget ) );
        return fieldWidget;
    }

    private static String createFieldStyle( PropertyEditorFieldInfo property,
                                            Widget fieldWidget ) {
        String style;
        if ( property.getType().equals( PropertyEditorType.BOOLEAN ) ) {
            style = DEFAULT_MARGING;
        } else {
            style = fieldWidget.getElement().getAttribute( STYLE );
            String fieldsMargin = "margin-bottom:0px ";
            style = style + fieldsMargin;

        }
        return style;
    }

}
