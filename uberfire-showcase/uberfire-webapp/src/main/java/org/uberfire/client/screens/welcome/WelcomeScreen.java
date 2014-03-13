package org.uberfire.client.screens.welcome;

import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.uberfire.client.annotations.WorkbenchContextId;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.propertyEditor.PropertyUtils;
import org.uberfire.client.propertyEditor.api.PropertyEditorChangeEvent;
import org.uberfire.client.propertyEditor.api.PropertyEditorEvent;
import org.uberfire.shared.propertyEditor.BeanPropertyEditorBuilderService;

@Dependent
@WorkbenchScreen(identifier = "welcome")
public class WelcomeScreen
        extends Composite {

    public static final String WELCOME_SCREEN_ID = "welcomeScreen";

    @UiField
    TextBox searchBox;

    @Inject
    Event<PropertyEditorEvent> event;

    @Inject
    private Caller<BeanPropertyEditorBuilderService> beanPropertyEditorBuilderCaller;

    interface ViewBinder
            extends
            UiBinder<Widget, WelcomeScreen> {

    }

    private static ViewBinder uiBinder = GWT.create( ViewBinder.class );

    @PostConstruct
    public void init() {
        initWidget( uiBinder.createAndBindUi( this ) );
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "Welcome";
    }

    @WorkbenchContextId
    public String getMyCorntextRef() {
        return "welcomeContext";
    }

    @UiHandler("launch")
    public void onClickLaunchUnknownPlace( final ClickEvent e ) {
        event.fire( new PropertyEditorEvent( WELCOME_SCREEN_ID, WelcomeScreenHelper.createProperties() ) );
    }

    @UiHandler("searchBox")
    public void onKeyDown( KeyDownEvent keyDown ) {
        if ( keyDown.getNativeKeyCode() == KeyCodes.KEY_ENTER ) {
            beanPropertyEditorBuilderCaller.call( new RemoteCallback<Map<String, List<String>>>() {
                                                      @Override
                                                      public void callback( final Map<String, List<String>> map ) {
                                                          event.fire( new PropertyEditorEvent( getTitle(), PropertyUtils.convertMapToCategory( map ) ) );
                                                      }
                                                  }, new ErrorCallback<Object>() {
                                                      @Override
                                                      public boolean error( Object message,
                                                                            Throwable throwable ) {
                                                          return false;
                                                      }
                                                  }
                                                ).extract( searchBox.getText() );

        }

    }

    public void propertyEditorChangeEvent( @Observes PropertyEditorChangeEvent event ) {
        if ( isMyPropertyEvent( event ) ) {
            Window.alert( "Msg from property editor: Changed: " + event.getProperty().getKey() + " - new value: " + event.getNewValue() );
        }
    }

    private boolean isMyPropertyEvent( PropertyEditorChangeEvent event ) {
        return true;
    }

}
