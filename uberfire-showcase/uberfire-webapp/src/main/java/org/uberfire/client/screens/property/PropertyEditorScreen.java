package org.uberfire.client.screens.property;

import java.util.Map;
import java.util.Set;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.Label;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
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

    @DataField
    @Inject
    private FlowPanel propertyName;

    @Inject
    private Caller<PropertyEditorService> propertyEditor;

    @Override
    @WorkbenchPartTitle
    public String getTitle() {
        return "Property Editor";
    }

    @EventHandler("searchItem")
    private void onKeyDown( KeyDownEvent event ) {
//        final String text = searchItem.getText();
        final String text = "org.uberfire.propertyEditor.SamplePlanBean";
        if ( event.getNativeKeyCode() == KeyCodes.KEY_ENTER ) {
            propertyEditor.call( new RemoteCallback<Map<String, String>>() {
                @Override
                public void callback( final Map<String, String> response ) {

                    Label fqcn = new Label( text );
                    propertyName.add( fqcn );
                    for ( String key : response.keySet() ) {
                        Label label = new Label( key + " " + response.get( key ) );
                        propertyName.add( label );
                    }

                }
            } ).getInformation( text );
        }
        searchItem.setText( "" );
    }

}
