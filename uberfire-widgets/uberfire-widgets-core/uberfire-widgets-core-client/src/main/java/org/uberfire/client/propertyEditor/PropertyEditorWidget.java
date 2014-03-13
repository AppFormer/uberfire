package org.uberfire.client.propertyEditor;

import java.util.List;

import com.github.gwtbootstrap.client.ui.Accordion;
import com.github.gwtbootstrap.client.ui.AccordionGroup;
import com.github.gwtbootstrap.client.ui.Label;
import com.github.gwtbootstrap.client.ui.Paragraph;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.client.propertyEditor.api.PropertyEditorCategory;
import org.uberfire.client.propertyEditor.api.PropertyEditorEvent;

public class PropertyEditorWidget extends Composite {

    @UiField
    Accordion propertyMenu;

    public void handle( PropertyEditorEvent event ) throws PropertyEditorException {

        if ( PropertyEditorHelper.validade( event ) ) {
            PropertyEditorHelper.extractEditorFrom(propertyMenu,event);

        } else {
            throw new PropertyEditorException( "Invalid event data." );
        }

    }



    public PropertyEditorWidget() {
        initWidget( uiBinder.createAndBindUi( this ) );

    }

    interface MyUiBinder extends UiBinder<Widget, PropertyEditorWidget> {

    }

    private static MyUiBinder uiBinder = GWT.create( MyUiBinder.class );

}