package org.uberfire.client.screens.welcome;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.uberfire.client.annotations.WorkbenchContextId;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchScreen;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.shared.property_editor.PropertyEditorEvent;

@Dependent
@WorkbenchScreen(identifier = "welcome")
public class WelcomeScreen
        extends Composite {

    @Inject
    Event<PropertyEditorEvent> event;

    interface ViewBinder
            extends
            UiBinder<Widget, WelcomeScreen> {

    }

    private static ViewBinder uiBinder = GWT.create( ViewBinder.class );

    @PostConstruct
    public void init() {

        initWidget( uiBinder.createAndBindUi( this ) );

    }

    @UiHandler("launch")
    public void onClickLaunchUnknownPlace( final ClickEvent e ) {
        event.fire( new PropertyEditorEvent( this ) );
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "Welcome";
    }

    @WorkbenchContextId
    public String getMyContextRef() {
        return "welcomeContext";
    }

}
