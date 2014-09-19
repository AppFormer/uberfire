package org.uberfire.client.editor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.enterprise.context.Dependent;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;
import org.uberfire.client.plugin.JSNativePlugin;

import static java.util.Collections.*;

@Dependent
public class JSNativeEditor extends JSNativePlugin {

    private List<String> resourcesType = new ArrayList<String>(  );
    private String priority;

    public void build( final JavaScriptObject obj ) {
        super.build( obj );
        extractResourcesType( obj );
        this.priority = extractPriority( obj );
    }

    public int getPriority() {
        return Integer.valueOf( this.priority );
    }

    public static native String extractPriority( JavaScriptObject obj ) /*-{
        return obj.priority;
    }-*/;

    private void extractResourcesType( JavaScriptObject obj ) {
        JsArrayString jsResources = getResourceType( obj );
        resourcesType.addAll( toCollection( jsResources ) );
    }

    public List<String> getResourceType() {
        return resourcesType;
    }

    private static native JsArrayString getResourceType( final JavaScriptObject o ) /*-{
        return o.resources_type;
    }-*/;

    public void onConcurrentUpdate() {
        if ( hasMethod( obj, "on_concurrent_update" ) ) {
            executeOnConcurrentUpdate( obj );
        }
    }

    private static native void executeOnConcurrentUpdate( final JavaScriptObject o ) /*-{
        o.on_concurrent_update();
    }-*/;

    public void onConcurrentDelete() {
        if ( hasMethod( obj, "on_concurrent_delete" ) ) {
            executeOnConcurrentDelete( obj );
        }
    }

    private static native void executeOnConcurrentDelete( final JavaScriptObject o ) /*-{
        o.on_concurrent_delete();
    }-*/;

    public void onConcurrentRename() {
        if ( hasMethod( obj, "on_concurrent_rename" ) ) {
            executeOnConcurrentRename( obj );
        }
    }

    private static native void executeOnConcurrentRename( final JavaScriptObject o ) /*-{
        o.on_concurrent_rename();
    }-*/;

    public void onConcurrentCopy() {
        if ( hasMethod( obj, "on_concurrent_copy" ) ) {
            executeOnConcurrentCopy( obj );
        }
    }

    private static native void executeOnConcurrentCopy( final JavaScriptObject o ) /*-{
        o.on_concurrent_copy();
    }-*/;

    public void onRename() {
        if ( hasMethod( obj, "on_rename" ) ) {
            executeOnRename( obj );
        }
    }

    private static native void executeOnRename( final JavaScriptObject o ) /*-{
        o.on_copy();
    }-*/;

    public void onDelete() {
        if ( hasMethod( obj, "on_delete" ) ) {
            executeOnDelete( obj );
        }
    }

    private static native void executeOnDelete( final JavaScriptObject o ) /*-{
        o.on_copy();
    }-*/;

    public void onCopy() {
        if ( hasMethod( obj, "on_copy" ) ) {
            executeOnCopy( obj );
        }
    }

    private static native void executeOnCopy( final JavaScriptObject o ) /*-{
        o.on_copy();
    }-*/;

    public void onUpdate() {
        if ( hasMethod( obj, "on_update" ) ) {
            executeOnUpdate( obj );
        }
    }

    private static native void executeOnUpdate( final JavaScriptObject o ) /*-{
        o.on_update();
    }-*/;


    private Collection<String> toCollection( final JsArrayString list ) {
        if ( list == null || list.length() == 0 ) {
            return emptyList();
        }

        final Collection<String> result = new ArrayList<String>();
        for ( int i = 0; i < list.length(); i++ ) {
            result.add( list.get( i ) );
        }

        return result;
    }

}
