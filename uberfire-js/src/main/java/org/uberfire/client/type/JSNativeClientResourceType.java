package org.uberfire.client.type;

import javax.enterprise.context.Dependent;

import com.google.gwt.core.client.JavaScriptObject;

@Dependent
public class JSNativeClientResourceType {

    private JavaScriptObject obj;
    private String shortName;
    private String description;
    private String priority;
    private String iconClass;
    private String acceptRegex;

    public void build( final JavaScriptObject obj ) {
        if ( this.obj != null ) {
            throw new RuntimeException( "Can't build more than once." );
        }
        this.obj = obj;
        extract( obj );
    }

    private void extract( JavaScriptObject obj ) {
        this.shortName = extractShortName( obj );
        this.description = extractDescription( obj );
        this.priority = extractPriority( obj );
        this.iconClass = extractIconClass( obj );
        this.acceptRegex = extractAcceptRegex( obj );
    }

    public String getAcceptRegex() {
        return acceptRegex;
    }

    public static native String extractAcceptRegex( JavaScriptObject obj ) /*-{
        return obj.accept_regex;
    }-*/;

    public String getShortName() {
        return this.shortName;
    }

    public static native String extractShortName( JavaScriptObject obj ) /*-{
        return obj.short_name;
    }-*/;

    public String getDescription() {
        return this.description;
    }

    public static native String extractDescription( JavaScriptObject obj ) /*-{
        return obj.description;
    }-*/;

    public int getPriority() {
        return Integer.valueOf( this.priority );
    }

    public static native String extractPriority( JavaScriptObject obj ) /*-{
        return obj.priority;
    }-*/;

    public String getIconClass() {
        return iconClass;
    }

    public static native String extractIconClass( JavaScriptObject obj ) /*-{
        return obj.icon_class;
    }-*/;

}
