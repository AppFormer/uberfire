package org.uberfire.client.screens.property;

import java.util.HashMap;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Tree;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.shared.PropertyEditorService;

@Dependent
@Templated
@WorkbenchScreen(identifier = "PropertyEditorScreen")
public class PropertyEditorScreen extends Composite {

    @DataField
    @Inject
    private ScrollPanel scroolPanel;

    @Override
    @WorkbenchPartTitle
    public String getTitle() {
        return "Property Editor";
    }

    public void observer( @Observes PropertyEditorEvent event ) {
        Tree propertyTree = PropertyEditorBuilder.build( event.getPropertyMap() );
        scroolPanel.clear();
        scroolPanel.add( propertyTree );
    }

}
