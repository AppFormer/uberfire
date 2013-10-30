package org.uberfire.client;

import java.util.ArrayList;
import java.util.Collection;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;
import org.jboss.errai.common.client.api.Caller;
import org.uberfire.workbench.model.SplashScreenFilter;
import org.uberfire.workbench.model.impl.SplashScreenFilterImpl;
import org.uberfire.workbench.services.WorkbenchServices;

import static java.util.Collections.*;

@Dependent
public class JSNativeSplashScreen extends JSNativePlugin {

    @Inject
    private Caller<WorkbenchServices> wbServices;

    public Caller<WorkbenchServices> getWbServices() {
        return wbServices;
    }

    public SplashScreenFilter buildFilter() {
        boolean displayNextTime = true;
        JsArrayString interceptionPoints = null;

        if ( hasMethod( obj, "display_next_time" ) ) {
            displayNextTime = getDisplayNextTimeFunctionResult( obj );
        } else if ( hasBooleanProperty( obj, "display_next_time" ) ) {
            displayNextTime = getDisplayNextTime( obj );
        }

        if ( hasMethod( obj, "interception_points" ) ) {
            interceptionPoints = getInterceptionPointsFunctionResult( obj );
        } else {
            interceptionPoints = getInterceptionPoints( obj );
        }

        return new SplashScreenFilterImpl( getId(), displayNextTime, toCollection( interceptionPoints ) );
    }

    private Collection<String> toCollection( final JsArrayString interceptionPoints ) {
        if ( interceptionPoints == null || interceptionPoints.length() == 0 ) {
            return emptyList();
        }

        final Collection<String> result = new ArrayList<String>();
        for ( int i = 0; i < interceptionPoints.length(); i++ ) {
            result.add( interceptionPoints.get( i ) );
        }

        return result;
    }

    private static native boolean getDisplayNextTimeFunctionResult( final JavaScriptObject o ) /*-{
        var result = o.display_next_time();
        if (typeof result === "boolean") {
            return result;
        }
        return false;
    }-*/;

    private static native boolean getDisplayNextTime( final JavaScriptObject o ) /*-{
        return o.display_next_time;
    }-*/;

    private static native JsArrayString getInterceptionPointsFunctionResult( final JavaScriptObject o ) /*-{
        var result = o.interception_points();
        if (result instanceof Array) {
            return result;
        }
        return [];
    }-*/;

    private static native JsArrayString getInterceptionPoints( final JavaScriptObject o ) /*-{
        return o.interception_points;
    }-*/;

}
