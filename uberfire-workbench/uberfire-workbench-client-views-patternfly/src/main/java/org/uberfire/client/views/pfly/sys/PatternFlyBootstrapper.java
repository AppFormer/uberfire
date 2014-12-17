package org.uberfire.client.views.pfly.sys;

import org.gwtbootstrap3.client.GwtBootstrap3ClientBundle;

import com.google.gwt.core.client.ScriptInjector;

/**
 * Utilities for ensuring the PatternFly/BS3 system is working early enough that the app can start correctly.
 */
public class PatternFlyBootstrapper {

    /**
     * Uses GWT's ScriptInjector to put jQuery in the page if it isn't already. All Errai IOC beans that rely on
     * GWTBootstrap 3 widgets should call this before creating their first such widget.
     */
    public static void ensurejQueryIsAvailable() {
        if (!isjQueryLoaded()) {
            ScriptInjector.fromString(GwtBootstrap3ClientBundle.INSTANCE.jQuery().getText())
                    .setWindow(ScriptInjector.TOP_WINDOW)
                    .inject();
        }
    }

    /**
     * Checks to see if jQuery is already present.
     *
     * @return true is jQuery is loaded, false otherwise.
     */
    private static native boolean isjQueryLoaded() /*-{
        return (typeof $wnd['jQuery'] !== 'undefined');
    }-*/;

}

