package org.uberfire.client.propertyEditor;

import com.github.gwtbootstrap.client.ui.Accordion;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.client.propertyEditor.api.PropertyEditorEvent;

public class PropertyEditorWidget extends Composite {

    @UiField
    Accordion propertyMenu;

    public void handle( PropertyEditorEvent event )  {

        if ( PropertyEditorHelper.validade( event ) ) {
            PropertyEditorHelper.extractEditorFrom( propertyMenu, event );
        }

    }

    public PropertyEditorWidget() {
        initWidget( uiBinder.createAndBindUi( this ) );

    }

    interface MyUiBinder extends UiBinder<Widget, PropertyEditorWidget> {

    }

    private static MyUiBinder uiBinder = GWT.create( MyUiBinder.class );

}