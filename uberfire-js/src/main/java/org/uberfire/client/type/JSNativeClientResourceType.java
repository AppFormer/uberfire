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
        if(hasShortName(obj)) {
            this.shortName = extractShortName( obj );
        }
        if(hasDescription( obj )) {
            this.description = extractDescription( obj );
        }
        if(hasPriority( obj )) {
            this.priority = extractPriority( obj );
        }
        if(hasIconClass( obj )) {
            this.iconClass = extractIconClass( obj );
        }
        if(hasAcceptRegex( obj )) {
            this.acceptRegex = extractAcceptRegex( obj );
        }
    }

    private boolean hasAcceptRegex( JavaScriptObject obj ) {
        return hasStringProperty( obj, "accept_regex" );
    }

    public String getAcceptRegex() {
        return acceptRegex;
    }

    public static native String extractAcceptRegex( JavaScriptObject obj ) /*-{
        return obj.accept_regex;
    }-*/;


    private boolean hasShortName( JavaScriptObject obj ) {
        return hasStringProperty( obj, "short_name" );
    }

    public String getShortName() {
        return this.shortName;
    }

    public static native String extractShortName( JavaScriptObject obj ) /*-{
        return obj.short_name;
    }-*/;

    private boolean hasDescription( JavaScriptObject obj ) {
        return hasStringProperty( obj, "description" );
    }

    public String getDescription() {
        return this.description;
    }

    public static native String extractDescription( JavaScriptObject obj ) /*-{
        return obj.description;
    }-*/;

    private boolean hasPriority( JavaScriptObject obj ) {
        return hasStringProperty( obj, "priority" );
    }

    public int getPriority() {
        return Integer.valueOf( this.priority );
    }

    public static native String extractPriority( JavaScriptObject obj ) /*-{
        return obj.priority;
    }-*/;

    private boolean hasIconClass( JavaScriptObject obj ) {
        return hasStringProperty( obj, "icon_class" );
    }

    public String getIconClass() {
        return iconClass;
    }

    public static native String extractIconClass( JavaScriptObject obj ) /*-{
        return obj.icon_class;
    }-*/;

    public static native boolean hasStringProperty( final JavaScriptObject obj,
                                                    final String propertyName )  /*-{
        return ((typeof obj[propertyName]) === "string");
    }-*/;

}
