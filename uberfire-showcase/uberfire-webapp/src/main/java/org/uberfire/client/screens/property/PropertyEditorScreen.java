package org.uberfire.client.screens.property;

import java.util.List;
import java.util.Map;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.screens.PropertyEditorWidget;
import org.uberfire.shared.screens.property.api.BeanPropertyEditorBuilderService;
import org.uberfire.shared.screens.property.api.PropertyEditorEvent;

@Dependent
@Templated
@WorkbenchScreen(identifier = "PropertyEditorScreen")
public class PropertyEditorScreen extends Composite {

    //    @DataField
//    @Inject
    private TextBox searchBox;

    @DataField
    @Inject
    private FlowPanel flowPanel;

    @Inject
    Event<PropertyEditorEvent> propertyEditorEvent;

    @Inject
    private Caller<BeanPropertyEditorBuilderService> beanPropertyEditorBuilderCaller;

    @Override
    @WorkbenchPartTitle
    public String getTitle() {
        return "Property Editor";
    }

//    @EventHandler("searchBox")
//    private void onKeyDown( KeyDownEvent event ) {
//        if ( event.getNativeKeyCode() == KeyCodes.KEY_ENTER ) {
//            beanPropertyEditorBuilderCaller.call( new RemoteCallback<Map<String, List<String>>>() {
//                @Override
//                public void callback( final Map<String, List<String>> map ) {
//                    propertyEditorEvent.fire( new PropertyEditorEvent( getTitle(), PropertyUtils.mapToCategory( map ) ) );
//                }
//            } ).extract( searchBox.getText() );
//        }
//
//    }

    private void createPropertyWidget( PropertyEditorEvent propertyEditorEvent ) {
        flowPanel.clear();
        flowPanel.add( PropertyEditorWidget.create( propertyEditorEvent ) );
    }

    public void propertyEditorEventObserver( @Observes PropertyEditorEvent event ) {
        createPropertyWidget( event );
    }

}
