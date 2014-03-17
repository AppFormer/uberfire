package org.uberfire.client.propertyEditor.widgets;

import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
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

    PropertyEditorItemsWidget parent;

    PropertyEditorErrorWidget errorWidget;

    public PropertyEditorTextBox() {
        initWidget( uiBinder.createAndBindUi( this ) );
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

    public void setValidationError(String error) {
        parent.setError();
        errorWidget.setText( error );
    }
    public void clearOldValidationErrors() {
        parent.clearError();
        errorWidget.setText( "" );
    }
    interface MyUiBinder extends UiBinder<Widget, PropertyEditorTextBox> {

    }

    private static MyUiBinder uiBinder = GWT.create( MyUiBinder.class );

    public void setParent( PropertyEditorItemsWidget parent ) {
        this.parent = parent;
    }

    public void setErrorWidget( PropertyEditorErrorWidget errorWidget ) {
        this.errorWidget = errorWidget;
    }

}