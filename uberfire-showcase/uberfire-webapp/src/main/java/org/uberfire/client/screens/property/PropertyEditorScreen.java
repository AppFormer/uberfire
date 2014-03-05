package org.uberfire.client.screens.property;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TextBox;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.shared.PropertyEditorService;
import org.uberfire.shared.property_editor.PropertyEditorEvent;

@Dependent
@Templated
@WorkbenchScreen(identifier = "PropertyEditorScreen")
public class PropertyEditorScreen extends Composite {

    @DataField
    @Inject
    private TextBox searchItem;

    @Inject
    private Event<PropertyEditorEvent> event;

    @Inject
    private Caller<PropertyEditorService> propertyEditor;

    @Override
    @WorkbenchPartTitle
    public String getTitle() {
        return "Property Editor";
    }

    @EventHandler("searchItem")
    private void onKeyDown( KeyDownEvent event ) {
        if ( event.getNativeKeyCode() == KeyCodes.KEY_ENTER ) {
            propertyEditor.call( new RemoteCallback<String>() {
                @Override
                public void callback( final String response ) {
                    if ( response == null ) {
                        System.out.println(response);
                    } else {
                        System.out.println(response);
                    }
                }
            } ).getInformation( searchItem.getText() );
        }
    }

}
