package org.uberfire.client.propertyEditor;

import java.util.Iterator;

import com.github.gwtbootstrap.client.ui.Accordion;
import com.github.gwtbootstrap.client.ui.AccordionGroup;
import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.base.DivWidget;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.uberfire.client.propertyEditor.api.PropertyEditorCategory;
import org.uberfire.client.propertyEditor.api.PropertyEditorEvent;

public class PropertyEditorWidget extends Composite {

    @UiField
    Accordion propertyMenu;

    PropertyEditorEvent originalEvent;

    @UiField
    TextBox filterBox;

    @UiField
    Button reload;

    public void handle( PropertyEditorEvent event ) {

        if ( PropertyEditorHelper.validade( event ) ) {
            this.originalEvent = event;
            PropertyEditorHelper.extractEditorFrom( propertyMenu, event );
        }

    }

    @UiHandler("reload")
    void onReload( ClickEvent e ) {
        PropertyEditorHelper.extractEditorFrom( propertyMenu, originalEvent, "" );
    }

    @UiHandler("filterBox")
    public void onKeyDown( KeyDownEvent keyDown ) {
        if ( keyDown.getNativeKeyCode() == KeyCodes.KEY_ENTER && originalEvent !=null) {
            propertyMenu.clear();
            PropertyEditorHelper.extractEditorFrom( propertyMenu, originalEvent, filterBox.getText() );
        }

    }

    public PropertyEditorWidget() {
        initWidget( uiBinder.createAndBindUi( this ) );

    }

    interface MyUiBinder extends UiBinder<Widget, PropertyEditorWidget> {

    }

    private static MyUiBinder uiBinder = GWT.create( MyUiBinder.class );

}