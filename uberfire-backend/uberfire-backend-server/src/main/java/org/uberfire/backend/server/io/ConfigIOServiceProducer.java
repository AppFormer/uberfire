package org.uberfire.backend.server.io;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.errai.security.shared.service.AuthenticationService;
import org.uberfire.backend.server.security.IOSecurityAuth;
import org.uberfire.commons.cluster.ClusterServiceFactory;
import org.uberfire.commons.lifecycle.PriorityDisposableRegistry;
import org.uberfire.commons.services.cdi.Startup;
import org.uberfire.commons.services.cdi.StartupType;
import org.uberfire.io.IOService;
import org.uberfire.io.impl.IOServiceNio2WrapperImpl;
import org.uberfire.io.impl.cluster.IOServiceClusterImpl;
import org.uberfire.java.nio.file.FileSystem;

@ApplicationScoped
@Startup(StartupType.BOOTSTRAP)
public class ConfigIOServiceProducer {

    private static ConfigIOServiceProducer instance;

    @Inject
    @Named("clusterServiceFactory")
    private ClusterServiceFactory clusterServiceFactory;
    
    @Inject
    @IOSecurityAuth
    private Instance<AuthenticationService> applicationProvidedConfigIOAuthService;

    private IOService configIOService;
    private FileSystem configFileSystem;

    @PostConstruct
    public void setup() {
        instance = this;
        if ( clusterServiceFactory == null ) {
            configIOService = new IOServiceNio2WrapperImpl( "config" );
        } else {
            configIOService = new IOServiceClusterImpl( new IOServiceNio2WrapperImpl( "config" ), clusterServiceFactory, clusterServiceFactory.isAutoStart() );
        }
        configFileSystem = (FileSystem) PriorityDisposableRegistry.get( "systemFS" );
    }

    public void destroy() {
        instance = null;
    }

    @Produces
    @Named("configIO")
    public IOService configIOService() {
        return configIOService;
    }

    public FileSystem configFileSystem() {
        if ( configFileSystem == null ) {
            configFileSystem = (FileSystem) PriorityDisposableRegistry.get( "systemFS" );
        }
        return configFileSystem;
    }

    public static ConfigIOServiceProducer getInstance() {
        if ( instance == null ) {
            throw new IllegalStateException( ConfigIOServiceProducer.class.getName() + " not initialized on startup" );
        }
        return instance;
    }

}
