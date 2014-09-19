package org.uberfire.client.exporter;

import java.util.Collection;
import javax.enterprise.context.ApplicationScoped;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.Window;
import org.jboss.errai.ioc.client.container.IOC;
import org.jboss.errai.ioc.client.container.IOCBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.jboss.errai.ioc.client.container.SyncBeanManagerImpl;
import org.uberfire.client.type.JSClientResourceType;
import org.uberfire.client.type.JSNativeClientResourceType;
import org.uberfire.client.workbench.type.ClientResourceType;

import static org.jboss.errai.ioc.client.QualifierUtil.DEFAULT_QUALIFIERS;

@ApplicationScoped
public class ClientResourceTypeJSExporter implements UberfireJSExporter {

    @Override
    public void export() {
        publish();
    }

    private native void publish() /*-{
        $wnd.$registerClientResourceType = @org.uberfire.client.exporter.ClientResourceTypeJSExporter::registerClientResourceType(Ljava/lang/Object;);
        $wnd.$getClientResourceType =  @org.uberfire.client.exporter.ClientResourceTypeJSExporter::getClientResourceType(Ljava/lang/String;);
    }-*/;

    public static void getClientResourceType( final String shortName ) {
        final SyncBeanManager beanManager = IOC.getBeanManager();
        Collection<IOCBeanDef<ClientResourceType>> availableResourceTypes = beanManager.lookupBeans( ClientResourceType.class );
        for ( final IOCBeanDef<ClientResourceType> resourceTypeBean : availableResourceTypes ) {
            final ClientResourceType resourceType = resourceTypeBean.getInstance();
            if(resourceType.getShortName().equals( shortName )){
                Window.alert( "It Works" );
            }
        }
    }

    public static void registerClientResourceType( final Object _obj ) {
        final JavaScriptObject obj = (JavaScriptObject) _obj;
        final SyncBeanManager beanManager = IOC.getBeanManager();
        final JSNativeClientResourceType newNativeEditor = beanManager.lookupBean( JSNativeClientResourceType.class ).getInstance();
        newNativeEditor.build( obj );
        JSClientResourceType jsClientResourceType = new JSClientResourceType( newNativeEditor );

        ( (SyncBeanManagerImpl) beanManager ).addBean( (Class) ClientResourceType.class, JSClientResourceType.class, null, jsClientResourceType, DEFAULT_QUALIFIERS, jsClientResourceType.getShortName(), true );
        ( (SyncBeanManagerImpl) beanManager ).addBean( (Class) JSClientResourceType.class, JSClientResourceType.class, null, jsClientResourceType, DEFAULT_QUALIFIERS, jsClientResourceType.getShortName(), true );
    }

}
