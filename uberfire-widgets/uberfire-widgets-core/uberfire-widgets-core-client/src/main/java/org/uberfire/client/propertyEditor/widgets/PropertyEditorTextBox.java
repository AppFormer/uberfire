package org.uberfire.client.propertyEditor.widgets;

import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class PropertyEditorTextBox extends Composite {

    @UiField
    TextBox textBox;


    @UiField
    Style style;


    interface Style extends CssResource {
        String regular();

        String error();
    }

    public PropertyEditorTextBox() {
        initWidget( uiBinder.createAndBindUi( this ) );
        textBox.setStyleName( style.regular() );
        textBox.addFocusHandler( new FocusHandler() {
            @Override
            public void onFocus( FocusEvent event ) {
                textBox.selectAll();
            }
        } );
    }

    public void setText(String text){
        textBox.setText( text );
    }

    public String getText() {
        return textBox.getText();
    }

    public void addKeyDownHandler( KeyDownHandler keyDownHandler ) {
        textBox.addKeyDownHandler( keyDownHandler );
    }

    public void setCSSError() {
        textBox.getElement().addClassName(style.error());
        textBox.getElement().removeClassName(style.regular());
    }
    public void setCSSRegular() {
        textBox.getElement().addClassName(style.regular());
        textBox.getElement().removeClassName(style.error());
    }
    interface MyUiBinder extends UiBinder<Widget, PropertyEditorTextBox> {

    }

    private static MyUiBinder uiBinder = GWT.create( MyUiBinder.class );

}