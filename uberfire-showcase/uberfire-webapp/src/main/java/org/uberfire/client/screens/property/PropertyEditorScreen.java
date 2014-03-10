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
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.VerticalPanel;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.shared.screens.property.api.BeanPropertyEditorBuilderService;
import org.uberfire.shared.screens.property.api.PropertyEditorBuilder;
import org.uberfire.shared.screens.property.api.PropertyEditorEvent;

@Dependent
@Templated
@WorkbenchScreen(identifier = "PropertyEditorScreen")
public class PropertyEditorScreen extends Composite {

    @DataField
    @Inject
    private ScrollPanel scroolPanel;

    @DataField
    @Inject
    private TextBox searchBox;


    @Inject
    Event<PropertyEditorEvent> propertyEditorEvent;

    @Inject
    private Caller<BeanPropertyEditorBuilderService> beanPropertyEditorBuilderCaller;

    @Override
    @WorkbenchPartTitle
    public String getTitle() {
        return "Property Editor";
    }

    @EventHandler("searchBox")
    private void onKeyDown(KeyDownEvent event) {
        if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
            beanPropertyEditorBuilderCaller.call( new RemoteCallback<Map<String,List<String>>>() {
                @Override
                public void callback( final Map<String,List<String>> map ) {
                    createPropertyWidget( new PropertyEditorEvent( "12932", PropertyUtils.mapToCategory( map ) ) );
                }
            } ).extract( searchBox.getText() );
        }
    }

    public void observer( @Observes PropertyEditorEvent event ) {
        createPropertyWidget( event );
    }

    private void createPropertyWidget( PropertyEditorEvent event ) {
        VerticalPanel verticalPanel = new VerticalPanel();
        Tree propertyTree = PropertyEditorBuilder.build( event );
        verticalPanel.add( propertyTree );

        scroolPanel.clear();
        scroolPanel.add( verticalPanel );
    }

}
